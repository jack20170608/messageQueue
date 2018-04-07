package com.fangming.mq.activeMq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;


public class ConsumerApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerApp.class);

    public static void main(String[] args) throws JMSException {
        Session session = ActiveMqConfig.getSession();
        //create target
        Destination dest = session.createQueue(ActiveMqConfig.TEST_QUEUE);
        //create consumer
        MessageConsumer consumer = session.createConsumer(dest);
        //set message Listener
        consumer.setMessageListener(new EchoMessageListerer());
    }


    static class Comsumer implements Runnable {

        private long id;

        private MessageListener topicMessageListener;
        private MessageListener queueMessageListener;

        public Comsumer(long id) {
            this.id = id;
        }

        public Comsumer(long id, MessageListener topicMessageListener, MessageListener queueMessageListener) {
            this.id = id;
            this.topicMessageListener = topicMessageListener;
            this.queueMessageListener = queueMessageListener;
        }

        @Override
        public void run() {

        }


    }

    static class EchoMessageListerer implements MessageListener {
        @Override
        public void onMessage(Message message) {
            TextMessage txtMessage = (TextMessage)message;
            try {
                LOGGER.info ("get message " + txtMessage.getText());
            } catch (JMSException e) {
                LOGGER.error("error {}", e);
            }
        }
    }
}