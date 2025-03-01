/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.message.support.redisson;

import kunlun.action.message.AbstractMessageHandler;
import kunlun.data.Dict;
import kunlun.data.tuple.Pair;
import kunlun.exception.ExceptionUtil;
import kunlun.message.MessageListener;
import kunlun.message.model.DelayMessage;
import kunlun.message.model.Message;
import kunlun.message.model.Result;
import kunlun.message.model.Subscribe;
import kunlun.time.DateUtil;
import kunlun.util.Assert;
import kunlun.util.ThreadUtil;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * The redisson delay message handler.
 * @author Kahle
 */
public class RedissonDelayMessageHandler extends AbstractMessageHandler {
    protected static Logger log = LoggerFactory.getLogger(RedissonDelayMessageHandler.class);
    protected static final Long DEFAULT_SLEEP_TIME = 500L;
    protected final Map<String, QueuePair> delayedQueues;
    protected final Boolean ignoreException;
    protected final Long    sleepTimeWhenRejected;
    protected final ExecutorService executorService;
    protected final RedissonClient  redissonClient;

    public RedissonDelayMessageHandler(RedissonClient  redissonClient,
                                       ExecutorService executorService) {

        this(redissonClient, executorService, null, null);
    }

    public RedissonDelayMessageHandler(RedissonClient  redissonClient,
                                       ExecutorService executorService,
                                       Long    sleepTimeWhenRejected,
                                       Boolean ignoreException) {
        this(redissonClient, executorService, sleepTimeWhenRejected
                , ignoreException, new ConcurrentHashMap<String, QueuePair>());
    }

    protected RedissonDelayMessageHandler(RedissonClient  redissonClient,
                                          ExecutorService executorService,
                                          Long    sleepTimeWhenRejected,
                                          Boolean ignoreException,
                                          Map<String, QueuePair> delayedQueues) {
        Assert.notNull(executorService, "Parameter \"executorService\" must not null. ");
        Assert.notNull(redissonClient, "Parameter \"redissonClient\" must not null. ");
        Assert.notNull(delayedQueues, "Parameter \"delayedQueues\" must not null. ");
        if (ignoreException == null) { ignoreException = false; }
        if (sleepTimeWhenRejected == null) { sleepTimeWhenRejected = DEFAULT_SLEEP_TIME; }
        sleepTimeWhenRejected = sleepTimeWhenRejected > 4000 ? 4000 : sleepTimeWhenRejected;
        sleepTimeWhenRejected = sleepTimeWhenRejected < 100 ? 100 : sleepTimeWhenRejected;
        this.sleepTimeWhenRejected = sleepTimeWhenRejected;
        this.ignoreException = ignoreException;
        this.executorService = executorService;
        this.redissonClient = redissonClient;
        this.delayedQueues = delayedQueues;
    }

    protected QueuePair getQueuePair(String topic) {
        Assert.notBlank(topic, "Parameter \"topic\" must not blank. ");
        QueuePair pair = delayedQueues.get(topic);
        if (pair != null) { return pair; }
        synchronized (topic.intern()) {
            if ((pair = delayedQueues.get(topic)) != null) { return pair; }
            RBlockingDeque<DelayMessage> blockingDeque = redissonClient.getBlockingDeque(topic);
            RDelayedQueue<DelayMessage> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
            delayedQueues.put(topic, pair = new QueuePair(delayedQueue, blockingDeque));
            return pair;
        }
    }

    @Override
    public <T extends Message> Result send(Collection<T> messages) {
        Assert.notEmpty(messages, "Parameter \"message\" must not empty. ");
        for (Message message : messages) {
            Assert.isInstanceOf(DelayMessage.class, message);
            DelayMessage delayMsg = (DelayMessage) message;
            if (delayMsg.getCreateTime() == null) {
                delayMsg.setCreateTime(DateUtil.getTimeInMillis());
            }
            TimeUnit timeUnit = delayMsg.getDelayTimeUnit();
            Long delayTime = delayMsg.getDelayTime();
            QueuePair pair = getQueuePair(delayMsg.getTopic());
            pair.getLeft().offer(delayMsg, delayTime, timeUnit);
        }
        return new Result();
    }

    @Override
    public DelayMessage receive(Base condition) {
        Assert.notNull(condition, "Parameter \"condition\" must not null. ");
        Assert.notBlank(condition.getTopic(), "Parameter \"condition.topic\" must not blank. ");
        QueuePair pair = getQueuePair(condition.getTopic());
        return pair.getRight().pollFirst();
    }

    @Override
    public Result subscribe(Subscribe subscribe) {
        Assert.notNull(subscribe, "Parameter \"subscribe\" must not null. ");
        Assert.notNull(subscribe.getTopic()
                , "Parameter \"subscribe.topic\" must not null. ");
        Assert.notNull(subscribe.getMessageListener()
                , "Parameter \"subscribe.messageListener\" must not null. ");
        Assert.isInstanceOf(MessageListener.class, subscribe.getMessageListener());
        QueuePair pair = getQueuePair(subscribe.getTopic());
        MessageListener listener = (MessageListener) subscribe.getMessageListener();
        MessageConsumer consumer = new MessageConsumer(
                executorService, listener, sleepTimeWhenRejected, ignoreException, pair);
        int listenerId = pair.getRight().subscribeOnFirstElements(consumer);
        return new Result(Dict.of("listenerId", listenerId));
    }

    /**
     * The inner redisson queue pair.
     * @author Kahle
     */
    protected static class QueuePair implements Pair<RDelayedQueue<DelayMessage>, RBlockingDeque<DelayMessage>> {
        private final RBlockingDeque<DelayMessage> right;
        private final RDelayedQueue<DelayMessage> left;
        protected QueuePair(RDelayedQueue<DelayMessage> left, RBlockingDeque<DelayMessage> right) {
            this.right = right;
            this.left = left;
        }
        @Override
        public RDelayedQueue<DelayMessage> getLeft() { return left; }
        @Override
        public RBlockingDeque<DelayMessage> getRight() { return right; }
    }

    /**
     * The inner message consumer.
     * @author Kahle
     */
    protected static class MessageConsumer implements Consumer<DelayMessage> {
        private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);
        private final Boolean ignoreException;
        private final Long    sleepTimeWhenRejected;
        private final ExecutorService executorService;
        private final MessageListener messageListener;
        private final QueuePair       queuePair;
        protected MessageConsumer(ExecutorService executorService,
                                  MessageListener messageListener,
                                  Long      sleepTimeWhenRejected,
                                  Boolean   ignoreException,
                                  QueuePair queuePair) {
            if (sleepTimeWhenRejected == null) { sleepTimeWhenRejected = DEFAULT_SLEEP_TIME; }
            if (ignoreException == null) { ignoreException = false; }
            this.sleepTimeWhenRejected = sleepTimeWhenRejected;
            this.executorService = executorService;
            this.messageListener = messageListener;
            this.ignoreException = ignoreException;
            this.queuePair = queuePair;
        }
        @Override
        public void accept(final DelayMessage message) {
            try {
                // The redisson's subscribe use "takeFirstAsync"'s RFuture (like callback).
                // So another thread pool needs to handle the listener logic.
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        messageListener.onMessage(message);
                    }
                });
            }
            catch (RejectedExecutionException e) {
                log.warn("Submitting a task to the thread pool was rejected. " +
                        "The rejected message is \"{}\". " +
                        "The message object is \"{}\". ", e.getMessage(), message);
                queuePair.getLeft().offerAsync(message, sleepTimeWhenRejected, TimeUnit.MILLISECONDS);
                ThreadUtil.sleepQuietly(sleepTimeWhenRejected);
            }
            catch (Exception e) {
                // If the exception is thrown, the subscription will stop.
                log.error("Message consumer accept error! ", e);
                if (!ignoreException) { throw ExceptionUtil.wrap(e); }
            }
        }
        // End.
    }

}
