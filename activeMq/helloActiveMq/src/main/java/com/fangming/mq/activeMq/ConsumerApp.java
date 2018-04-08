package com.fangming.mq.activeMq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fangming.mq.activeMq.ProducerApp.DesitinationType;

import javax.jms.*;


public class ConsumerApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerApp.class);

    public static void main(String[] args) throws JMSException {
        Session session = ActiveMqConfig.getSession();
        EchoMessageListerer listerer = new EchoMessageListerer();
        Comsumer c1 = new Comsumer(1, DesitinationType.QUEUE, ActiveMqConfig.TEST_QUEUE, listerer, session);
        Comsumer c2 = new Comsumer(2, DesitinationType.QUEUE, ActiveMqConfig.TEST_QUEUE, listerer, session);


        Comsumer c3 = new Comsumer(3, DesitinationType.TOPIC, ActiveMqConfig.TEST_TOPIC, listerer, session);
        Comsumer c4 = new Comsumer(4, DesitinationType.TOPIC, ActiveMqConfig.TEST_TOPIC, listerer, session);
    }


    static class Comsumer {

        private long id;
        private String destination;
        private MessageListener messageListener;

        private Session session;
        private ProducerApp.DesitinationType desitinationType;
        private MessageConsumer messageConsumer;

        public Comsumer(long id, DesitinationType desitinationType, String destination, MessageListener messageListener, Session session) {
            this.id = id;
            this.desitinationType = desitinationType;
            this.destination = destination;

            this.messageListener = messageListener;
            this.session = session;

            Destination dest;
            try {
                //create target
                if (desitinationType == ProducerApp.DesitinationType.QUEUE) {
                    dest = session.createQueue(destination);
                }else {
                    dest = session.createTopic(destination);
                }
                //create consumer
                MessageConsumer consumer = session.createConsumer(dest);
                //set message Listener
                consumer.setMessageListener(messageListener);
            }catch (JMSException e){
                LOGGER.error("JMS error.");
            }
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