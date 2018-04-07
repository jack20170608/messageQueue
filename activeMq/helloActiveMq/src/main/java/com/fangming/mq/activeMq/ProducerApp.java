package com.fangming.mq.activeMq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;


public class ProducerApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerApp.class);

    public static void main(String[] args) throws JMSException, InterruptedException {

        Session session = ActiveMqConfig.getSession();
        //create target
        Destination dest = session.createQueue(ActiveMqConfig.TEST_QUEUE);

        //createTopic方法用来创建Topic
        //session.createTopic("TOPIC");

        //though session to create message producer
        MessageProducer producer = session.createProducer(dest);
        for (int i=0; i<100; i++) {
            Thread.sleep(1000L);
            TextMessage message = session.createTextMessage("hello active mq " + i);
            //send message to queue
            producer.send(message);
            LOGGER.debug("send message {}", i);
        }

        ActiveMqConfig.closeSession(session);
    }
}