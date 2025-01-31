/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.message.support.redisson;

import kunlun.message.MessageListener;
import kunlun.message.model.DelayMessage;
import kunlun.message.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract classic delay message listener.
 * @author Kahle
 */
public abstract class AbstractDelayMessageListener implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(AbstractDelayMessageListener.class);

    /**
     * Get the delay message listener's topic.
     * @return The delay message listener's topic
     */
    public abstract String getTopic();

    /**
     * Processing the received delayed message.
     * @param message The received delayed message
     */
    public abstract void process(DelayMessage message);

    @Override
    public Object onMessage(Message message) {
        try {
            process((DelayMessage) message);
        }
        catch (Exception e) {
            log.error("Processing the received message error! ", e);
        }
        return null;
    }

}
