/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.message.support.mqtt;

import kunlun.action.message.AbstractMessageBus;
import kunlun.common.constant.Nil;
import kunlun.exception.ExceptionUtil;
import kunlun.generator.id.IdUtil;
import kunlun.message.MessageListener;
import kunlun.message.model.Message;
import kunlun.message.model.MessageRt;
import kunlun.message.model.Subscribe;
import kunlun.message.model.SubscribeRt;
import kunlun.util.ShutdownHookUtil;
import kunlun.util.StrUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static kunlun.common.constant.Algorithms.UUID;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.util.Assert.notBlank;
import static kunlun.util.Assert.notNull;

/**
 * 基于 Paho 的 MQTT 消息总线.
 * @author Kahle
 */
public class MqttPahoMessageBus extends AbstractMessageBus {
    private static final Logger log = LoggerFactory.getLogger(MqttPahoMessageBus.class);
    public static final String QOS = "qos";
    private final MqttClient mqttClient;

    public MqttPahoMessageBus(MqttClient mqttClient) {

        this.mqttClient = notNull(mqttClient);
    }

    public MqttPahoMessageBus(String serverUri, String clientId, MqttClientPersistence persistence, MqttConnectOptions options) {
        notBlank(serverUri);
        if (StrUtil.isBlank(clientId)) {
            clientId = "JavaMqttClient_" + IdUtil.next(UUID);
        }
        if (persistence == null) {
            persistence = new MemoryPersistence();
        }
        if (options == null) {
            options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
        }
        try {
            mqttClient = new MqttClient(serverUri, clientId, persistence);
            mqttClient.connect(options);
            log.info("Mqtt client connected. ");
            ShutdownHookUtil.addRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mqttClient.isConnected()) {
                            mqttClient.disconnect();
                            log.info("Mqtt client disconnected. ");
                        }
                    } catch (MqttException e) {
                        log.error("Mqtt client disconnect error. ", e);
                    }
                }
            });
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

    public MqttPahoMessageBus() {

        this("tcp://broker.emqx.io:1883", Nil.STR, Nil.<MqttClientPersistence>g(), Nil.<MqttConnectOptions>g());
    }

    public MqttClient getMqttClient() {

        return mqttClient;
    }

    @Override
    public <T extends Message> MessageRt send(Collection<T> messages) {
        try {
            Integer qos;
            for (T message : messages) {
                MqttMessage mqttMessage = new MqttMessage((byte[]) message.getBody());
                if ((qos = (Integer) message.getProperties().get(QOS)) != null) {
                    mqttMessage.setQos(qos);
                } else { mqttMessage.setQos(ZERO); }
                getMqttClient().publish(message.getTopic(), mqttMessage);
                log.info("publish mqtt message: {}", message.getTopic());
            }
            return new MessageRt();
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

    @Override
    public SubscribeRt subscribe(Subscribe subscribe) {
        try {
            MqttMessageListener listener = new MqttMessageListener((MessageListener) subscribe.getMessageListener());
            getMqttClient().subscribe(subscribe.getTopic(), listener);
            return new SubscribeRt();
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

    public static class MqttMessageListener implements MessageListener, IMqttMessageListener {
        private final MessageListener messageListener;

        public MqttMessageListener(MessageListener messageListener) {

            this.messageListener = notNull(messageListener);
        }

        @Override
        public Object onMessage(Message message) {

            return messageListener.onMessage(message);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Message msg = new Message(topic, message.getPayload());
            msg.setId(String.valueOf(message.getId()));
            msg.getProperties().put(QOS, message.getQos());
            onMessage(msg);
        }
    }

}
