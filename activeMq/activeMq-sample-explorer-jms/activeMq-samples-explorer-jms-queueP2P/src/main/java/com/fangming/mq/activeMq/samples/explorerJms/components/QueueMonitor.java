package com.fangming.mq.activeMq.samples.explorerJms.components;

/*

Sample Application

Writing a Basic JMS Application using:
    - QueueBrowser
    - JMS with a Graphical Interface
    - behavior based on message type

When you run this program, it will read all the parameters out
of the QueueMonitor.properties file. In this file you can specify
which queues you want to monitor. Then a Java window will open and
every time you click the Browse button, The current contents of the queues
will be displayed in the text window.

Usage:
  java QueueMonitor

Suggested demonstration:
  - Start one instance of this application:
        java QueueMonitor
  - Run on or more Talk applications (without the receiving queue).
  - Enter messages on various Talk windows.
  - Watch the QueueMonitor display the messages.

*/

import com.fangming.mq.activeMq.samples.explorerJms.ExitException;
import com.google.common.collect.Lists;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

@Component
public class QueueMonitor implements DisposableBean, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueMonitor.class);

    @Value("${queue.brokerUrl}")
    private String broker;

    @Value("${queue.connectID}")
    private String connectID;

    @Value("${queue.username}")
    private String username;

    @Value("${queue.password}")
    private String password;

    @Value("${queue.monitor.browseQueues}")
    private String browseQueues;

    @Value("${queue.monitor.timeInterval}")
    private int timeInterval;

    private Thread damonMonitoryThread;
    private volatile boolean runningFlag;

    private List<Queue> theMonitorQueues = Lists.newArrayList();
    private javax.jms.Connection connect = null;
    private javax.jms.Session session = null;


    @PostConstruct
    private void initQueueMonitor() {
        try {
            javax.jms.ConnectionFactory factory;
            factory = new ActiveMQConnectionFactory(username, password, broker);

            connect = factory.createConnection(username, password);
            session = connect.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
        } catch (javax.jms.JMSException jmse) {
            LOGGER.error("Cannot connect to Broker");
            throw new ExitException();
        }

        // Set up Queues:
        StringTokenizer queues = new StringTokenizer(browseQueues, ",");
        String queueName = null;
        while (queues.hasMoreTokens()) {
            try {
                queueName = queues.nextToken();
                LOGGER.info("Monitoring  " + queueName);
                Queue queue = session.createQueue(queueName);
                theMonitorQueues.add(queue);
            } catch (javax.jms.JMSException jmse) {
                LOGGER.error("Queue :[{}] create failure ", queueName);
            }
        }
        // After init it is time to start the connection
        try {
            connect.start();

            damonMonitoryThread = new Thread(this);
            damonMonitoryThread.setDaemon(true);
            damonMonitoryThread.setName("QMonitorThread");
            //Set the running flag to true
            runningFlag = true;
            damonMonitoryThread.start();
        } catch (javax.jms.JMSException jmse) {
            LOGGER.error("Cannot start connection");
            throw new ExitException();
        }

    }


    public String getContents(javax.jms.Message message) {
        String msgBody = null;
        String msgClass = message.getClass().getName();
        if (message instanceof javax.jms.TextMessage) {
            msgClass = "javax.jms.TextMessage";
            try {
                msgBody = ((javax.jms.TextMessage) message).getText();
            } catch (javax.jms.JMSException jmse) {
                msgBody = "";
            }
        } else if (message instanceof org.apache.activemq.command.ActiveMQMapMessage) {
            System.out.println("(Name value pairs in the MapMessage are not displayed.)");
        } else if (message instanceof javax.jms.BytesMessage) {
            System.out.println("Warning: A bytes message was discarded because it could not be processed as a javax.jms.TextMessage.");
        } else if (message instanceof javax.jms.ObjectMessage) {
            System.out.println("Warning: An object message was discarded because it could not be processed as a javax.jms.TextMessage.");
        } else if (message instanceof javax.jms.StreamMessage) {
            System.out.println("Warning: A stream message was discarded because it could not be processed as a javax.jms.TextMessage.");
        }
        return "- " + msgClass + " from " + msgBody;
    }

    @Override
    public void run() {
        //Print the queue info
        while (runningFlag) {
            if (theMonitorQueues.size() == 0) {
                LOGGER.info("No Queues to be monitored");
            } else {
                for (Queue queue : theMonitorQueues) {
                    try {
                        // Create a browser on the queue and show the messages waiting in it.
                        LOGGER.info("--------------------------------------------------");
                        LOGGER.info("Messages on queue " + queue.getQueueName() + ":");

                        javax.jms.QueueBrowser browser = session.createBrowser(queue);
                        int cnt = 0;
                        Enumeration e = browser.getEnumeration();
                        if (!e.hasMoreElements()) {
                            LOGGER.info("(This queue is empty.)");
                        } else {
                            while (e.hasMoreElements()) {
                                LOGGER.info(" --> getting message " + String.valueOf(++cnt));
                                javax.jms.Message message = (javax.jms.Message) e.nextElement();
                                if (message != null) {
                                    String msgText = getContents(message);
                                    LOGGER.info("[" + msgText + "]");
                                }
                            }
                        }
                        // Free any resources in the browser.
                        browser.close();
                    } catch (javax.jms.JMSException jmse) {
                        jmse.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(timeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        //clean the session once bean destroy
        try {
            runningFlag = false;
            if (null != connect) {
                connect.close();
            }
        } catch (JMSException e) {
            LOGGER.error("JMS connection close failure.");
        }

    }
}