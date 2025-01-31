/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.message.support.rocketmq;

import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;

/**
 * @see org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently
 * @see org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly
 * @see org.apache.rocketmq.client.consumer.listener.MessageListener
 * @author Kahle
 */
public interface RocketMqListener extends MessageListener {

    String getNameServerAddress();

    String getConsumerGroup();

    String getTopic();

    String getSubExpression();

    void preProcess(MQConsumer mqConsumer);

}
