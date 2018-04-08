package com.fangming.mq.activeMq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fangming.mq.activeMq.ProducerApp.DesitinationType;

import javax.jms.*;


public class ConsumerApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerApp.class);

    public static void main(String[] args) throws Exception {
        Comsumer c1 = new Comsumer(1, DesitinationType.QUEUE, ActiveMqConfig.TEST_QUEUE);
        c1.setMessageListener(EchoMessageListerer.of(c1));
        Comsumer c2 = new Comsumer(2, DesitinationType.QUEUE, ActiveMqConfig.TEST_QUEUE);
        c2.setMessageListener(EchoMessageListerer.of(c2));

        Comsumer c3 = new Comsumer(3, DesitinationType.TOPIC, ActiveMqConfig.TEST_TOPIC);
        c3.setMessageListener(EchoMessageListerer.of(c3));
        Comsumer c4 = new Comsumer(4, DesitinationType.TOPIC, ActiveMqConfig.TEST_TOPIC);
        c4.setMessageListener(EchoMessageListerer.of(c4));

        Thread t1 = Comsumer.wrapperThread(c1);
        Thread t2 = Comsumer.wrapperThread(c2);
        Thread t3 = Comsumer.wrapperThread(c3);
        Thread t4 = Comsumer.wrapperThread(c4);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
    }


    static class Comsumer implements Runnable{

        private long id;
        private String destination;
        private MessageListener messageListener;
        private ProducerApp.DesitinationType desitinationType;

        public static Thread wrapperThread(Comsumer comsumer){
            return new Thread(comsumer);
        }

        public Comsumer(long id, DesitinationType desitinationType, String destination) {
            this(id, desitinationType, destination, null);
        }

        public Comsumer(long id, DesitinationType desitinationType, String destination, MessageListener messageListener) {
            this.id = id;
            this.desitinationType = desitinationType;
            this.destination = destination;

            this.messageListener = messageListener;

        }

        @Override
        public void run() {
            Destination dest;
            try {
                Session session = ActiveMqConfig.getSession();
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

        @Override
        public String toString() {
            return "Comsumer{" +
                    "id=" + id +
                    ", destination='" + destination + '\'' +
                    '}';
        }

        public void setMessageListener(MessageListener messageListener) {
            this.messageListener = messageListener;
        }
    }

    static class EchoMessageListerer implements MessageListener {

        public static EchoMessageListerer of(Comsumer comsumer){
            return new EchoMessageListerer(comsumer);
        }

        private Comsumer comsumer;

        public EchoMessageListerer(Comsumer comsumer) {
            this.comsumer = comsumer;
        }

        @Override
        public void onMessage(Message message) {
            TextMessage txtMessage = (TextMessage)message;
            try {
                LOGGER.info (comsumer.toString() + " get message " + txtMessage.getText());
            } catch (JMSException e) {
                LOGGER.error("error {}", e);
            }
        }
    }
}