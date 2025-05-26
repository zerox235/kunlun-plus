/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.message.support.rocketmq;

import kunlun.action.message.AbstractMessageBus;
import kunlun.data.Dict;
import kunlun.data.bean.BeanUtil;
import kunlun.exception.ExceptionUtil;
import kunlun.message.model.MessageRt;
import kunlun.message.model.Subscribe;
import kunlun.message.model.SubscribeRt;
import kunlun.util.Assert;
import kunlun.util.CollUtil;
import kunlun.util.IterUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static kunlun.common.constant.Numbers.ONE;
import static kunlun.util.StrUtil.isNotBlank;

public class RocketMqMessageHandler extends AbstractMessageBus {
    private static final Logger log = LoggerFactory.getLogger(RocketMqMessageHandler.class);
    private final Map<Object, DefaultMQPushConsumer> mqConsumers;
    private final DefaultMQProducer mqProducer;

    public RocketMqMessageHandler(DefaultMQProducer mqProducer,
                                  Map<Object, DefaultMQPushConsumer> mqConsumers) {
        this.mqConsumers = mqConsumers;
        this.mqProducer = mqProducer;
    }

    public RocketMqMessageHandler(DefaultMQProducer mqProducer) {

        this(mqProducer, new ConcurrentHashMap<Object, DefaultMQPushConsumer>());
    }

    protected Message convert(kunlun.message.model.Message message) {
        Dict properties = Dict.of(message.getProperties());
        //noinspection unchecked
        Collection<String> keys = (Collection<String>) properties.get("keys");
        Integer flag = (Integer) properties.get("flag");
        String  tags = (String) properties.get("tags");
        String  topic = message.getTopic();
        byte[]  body = (byte[]) message.getBody();
        Message result = new Message(topic, tags, null, body);
        if (CollUtil.isNotEmpty(keys)) {
            result.setKeys(keys);
        }
        if (flag != null) {
            result.setFlag(flag);
        }
        return result;
    }

    @Override
    public <T extends kunlun.message.model.Message> MessageRt send(Collection<T> messages) {
        Assert.notEmpty(messages, "Parameter \"messages\" must not empty. ");
        try {
            SendResult sendResult;
            if (messages.size() == ONE) {
                Message mqMessage = convert(IterUtil.getFirst(messages));
                sendResult = mqProducer.send(mqMessage);
            } else {
                List<Message> messageList = new ArrayList<Message>();
                for (T message : messages) {
                    messageList.add(convert(message));
                }
                sendResult = mqProducer.send(messageList);
            }
            return new MessageRt(BeanUtil.beanToMap(sendResult));
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

    @Override
    public SubscribeRt subscribe(Subscribe sub) {
        if (!(sub.getMessageListener() instanceof RocketMqListener)) {
            throw new IllegalArgumentException("Unsupported the message listener. ");
        }
        RocketMqListener ltr = (RocketMqListener) sub.getMessageListener();
        String subExpr = isNotBlank(ltr.getSubExpression()) ? ltr.getSubExpression() : sub.getSubExpression();
        String topic = isNotBlank(ltr.getTopic()) ? ltr.getTopic() : sub.getTopic();
        try {
            DefaultMQPushConsumer mqConsumer = new DefaultMQPushConsumer(ltr.getConsumerGroup());
            mqConsumer.setNamesrvAddr(ltr.getNameServerAddress());
            mqConsumer.subscribe(topic, subExpr);
            if (ltr instanceof MessageListenerConcurrently) {
                mqConsumer.registerMessageListener((MessageListenerConcurrently) ltr);
            } else if (ltr instanceof MessageListenerOrderly) {
                mqConsumer.registerMessageListener((MessageListenerOrderly) ltr);
            } else { throw new IllegalArgumentException("Unsupported the message listener. "); }
            //
            ltr.preProcess(mqConsumer);
            mqConsumer.start();
            mqConsumers.put(ltr, mqConsumer);
            log.info("RocketMQ subscribe {} {} by {} success. ", topic, subExpr, ltr.getClass().getName());
            return new SubscribeRt();
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

}
