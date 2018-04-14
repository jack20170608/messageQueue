package com.fangming.mq.activeMq.samples.explorerJms.components.transaction;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.jms.*;

/**
 * Created by Jack on 2018/4/14.
 */
@Component
public class TransactionTalkerFactory implements DisposableBean{

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTalkerFactory.class);

    @Value("${queue.brokerUrl}")
    private String broker;

    @Value("${queue.connectID}")
    private String connectID;

    @Value("${queue.username}")
    private String username;

    @Value("${queue.password}")
    private String password;

    @Value("${queue.transactionTalker.sendQueue}")
    private String sendQueue;

    @Value("${queue.transactionTalker.receiveQueue}")
    private String receiveQueue;

    private TransactedTalker theFirstTalker, theSecondTalker;


    @PostConstruct
    private void initTalkers() throws JMSException{
        TransactedTalker firstTalker = getTransactionTalker("talker1", sendQueue, receiveQueue);
        TransactedTalker secondTalker = getTransactionTalker("talker2", receiveQueue, sendQueue);

        new Thread(firstTalker).start();
        new Thread(secondTalker).start();
    }

    public TransactedTalker getTransactionTalker(String name, String sendQueueName, String receiveQueueName) throws JMSException {
        Assert.notNull(sendQueueName, "message send queue should not be null.");
        Assert.notNull(receiveQueueName, "message receive queue should not be null");
        ConnectionFactory factory = new ActiveMQConnectionFactory(username, password, broker);
        Connection connect = factory.createConnection (username, password);
        TransactedTalker talker = new TransactedTalker(name, connect, sendQueueName, receiveQueueName);
        return talker;
    }


    @Override
    public void destroy() throws Exception {
        theFirstTalker.exit();
        theSecondTalker.exit();
    }
}
