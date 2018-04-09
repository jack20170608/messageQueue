package com.fangming.mq.activeMq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Created by Jack on 2018/4/7.
 */
public class ActiveMqConfig {

    //Linux env to start active Mq
    //  >cd [activemq_install_dir]/bin
    //daemon mode
    //  >./activemq start
    //  >./activemq console

    public static final String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;

    public static final String TEST_QUEUE = "test-activemq-queue";

    public static final String TEST_TOPIC = "test-activemq-topic";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMqConfig.class);

    private ThreadLocal<Session> sessionThreadLocal;

    public static Session getSession() throws JMSException{

        //Init the connection Factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMqConfig.BROKER_URL);

        LOGGER.info(ActiveMqConfig.BROKER_URL);

        //get the connection
        Connection conn = connectionFactory.createConnection();

        //start the connection
        conn.start();

        //create a new session，
        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        return session;
    }

    public static void closeSession(Session session) throws JMSException {
        if (null != session){
            session.close();
        }
    }
}
