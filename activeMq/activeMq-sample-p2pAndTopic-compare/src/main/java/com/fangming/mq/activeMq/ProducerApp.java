package com.fangming.mq.activeMq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;


public class ProducerApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerApp.class);

    public static void main(String[] args) throws JMSException, InterruptedException {

        Session session = ActiveMqConfig.getSession();

        //2 message procedures send message to the same queue
        Thread procedure1 = new Thread(new Producer(1, DesitinationType.QUEUE, ActiveMqConfig.TEST_QUEUE, session));
        procedure1.start();
        Thread procedure2 = new Thread(new Producer(2, DesitinationType.QUEUE, ActiveMqConfig.TEST_QUEUE, session));
        procedure2.start();
        //1 message procedures send message to the same topic
        Thread procedure3 = new Thread(new Producer(3, DesitinationType.TOPIC, ActiveMqConfig.TEST_TOPIC, session));
        procedure3.start();

        procedure1.join();
        procedure2.join();
        procedure3.join();

        //close session
        ActiveMqConfig.closeSession(session);
    }

    static class Producer implements Runnable {

        private MessageProducer producer;
        private final Session session;
        private final String destName;
        private final int id;

        public final String messageFormat = "Hello active mq, procedure %d's message %d will send to %s";

        public Producer(int id,DesitinationType desitinationType, String destName, Session session) {
            this.id = id;
            this.destName = destName;
            this.session = session;
            Destination dest ;
            try {
                if (desitinationType == DesitinationType.QUEUE) {
                    dest = session.createQueue(destName);
                } else {
                    dest = session.createTopic(destName);
                }
                this.producer = session.createProducer(dest);
            }catch (Exception e){
                LOGGER.error("Destination create failure, due to ", e.getMessage());
            }
        }

        @Override
        public void run() {
            String messageContent = null;
            for (int i=0; i< 3; i++) {
                try {
                    Thread.sleep(1000L);
                    messageContent = String.format(messageFormat, id, i, destName);
                    TextMessage message = session.createTextMessage(messageContent);
                    //send message to queue
                    producer.send(message);
                } catch (Exception e) {
                    LOGGER.error("error {}", e.getMessage());
                }
                LOGGER.debug("send message {}", messageContent);
            }
        }
    }

    enum DesitinationType {
        QUEUE,TOPIC
    }
}