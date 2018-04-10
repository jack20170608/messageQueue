package com.fangming.mq.activeMq.samples.explorerJms;

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
import com.google.common.collect.Lists;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.language.bean.RuntimeBeanExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.Queue;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

@Component
public class QueueMonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueMonitorService.class);

    @Value("${queueMonitor.brokerUrl}")
    private String broker = "tcp://localhost:61616";

    @Value("${queueMonitor.connectID}")
    private String connectID = "QueueMonitor";

    @Value("${queueMonitor.username}")
    private String username = "QueueMonitor";

    @Value("${queueMonitor.password}")
    private String password = "QueueMonitor";

    @Value("${queueMonitor.browseQueues}")
    private String browseQueues  = "Q1,Q2,Q3";

    private List<Queue> theMonitorQueues = Lists.newArrayList();
    private javax.jms.Connection connect = null;
    private javax.jms.Session session = null;


    @PostConstruct
    private void initQueueMonitor(){
        try {
            javax.jms.ConnectionFactory factory;
            factory = new ActiveMQConnectionFactory(username, password, broker);

            connect = factory.createConnection (username, password);
            session = connect.createSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
        }
        catch (javax.jms.JMSException jmse) {
            LOGGER.error("Cannot connect to Broker");
            throw new ExitException();
        }

        // Set up Queues:
        StringTokenizer queues = new StringTokenizer(browseQueues, ",");
        String queueName = null;
        while (queues.hasMoreTokens()) {
            try{
                queueName = queues.nextToken();
                LOGGER.info("Monitoring  " + queueName);
                Queue queue = session.createQueue(queueName);
                theMonitorQueues.add(queue);
            }
            catch (javax.jms.JMSException jmse) {
                LOGGER.error("Queue :[{}] create failure ", queueName);
            }
        }
        // After init it is time to start the connection
        try {
            connect.start();
        }
        catch (javax.jms.JMSException jmse) {
            LOGGER.error("Cannot start connection");
            throw new ExitException();
        }
    }

    /*

    public class OnBrowse implements ActionListener
    {

        public void actionPerformed(ActionEvent evt)
        {
            // Clear the textArea.
            textArea.setText("");
            textArea.paintImmediately(textArea.getBounds());

            if(theQueues.size() == 0){
                textArea.setText("No Queues to be monitored");
            }
            else{
                for(int i = 0; i<theQueues.size();i++)
                {
                    try
                    {
                        // Create a browser on the queue and show the messages waiting in it.
                        javax.jms.Queue q = (javax.jms.Queue) theQueues.elementAt(i);
                       textArea.append("--------------------------------------------------\n");
                       textArea.append("Messages on queue " + q.getQueueName() + ":\n");
  
                        // Create a queue browser
                        System.out.print ("Browsing messages in queue " + q.getQueueName() + "\"...");
                        javax.jms.QueueBrowser browser = session.createBrowser(q);
                        System.out.println ("[done]");
                        int cnt = 0;
                        Enumeration e = browser.getEnumeration();
                        if(!e.hasMoreElements())
                        {
                            textArea.append ("(This queue is empty.)");
                        }
                        else
                        {
                            while(e.hasMoreElements())
                            {
                                System.out.print(" --> getting message " + String.valueOf(++cnt) + "...");
                                javax.jms.Message message = (javax.jms.Message) e.nextElement();
                                System.out.println("[" + message + "]");
                                if (message != null)
                                {
                                    String msgText = getContents (message);
                                    textArea.append(msgText + "\n");
                                    try
                                    {
                                        // Scroll the text area to show the message
                                        Rectangle area = textArea.modelToView(textArea.getText().length());
                                        textArea.scrollRectToVisible(area);
                                        textArea.paintImmediately(textArea.getBounds());
                                    }
                                    catch(Exception jle) { jle.printStackTrace();}
                                }
                            }
                        }
                        // Free any resources in the browser.
                        browser.close();
                        textArea.append ("\n");
                    }
                    catch (javax.jms.JMSException jmse){
                        jmse.printStackTrace();
                    }
                }
                try
                {
                    // Scroll the text area to show the message
                    Rectangle area = textArea.modelToView(textArea.getText().length());
                    textArea.scrollRectToVisible(area);
                    textArea.paintImmediately(textArea.getBounds());
                }
                catch(Exception jle) { jle.printStackTrace();}
            }
        }
    }
    */

    public String getContents (javax.jms.Message message){


            String msgBody = null;
            String msgClass = message.getClass().getName();

            if (message instanceof javax.jms.TextMessage)
            {
                msgClass = "javax.jms.TextMessage";
                try
                {
                    msgBody = ((javax.jms.TextMessage)message).getText();
                }
                catch (javax.jms.JMSException jmse)
                {
                    msgBody = "";
                }
            }
          
            else if (message instanceof org.apache.activemq.command.ActiveMQMapMessage)
            {
		  			    System.out.println ("(Name value pairs in the MapMessage are not displayed.)");
            }
            else if (message instanceof javax.jms.BytesMessage)
          			{
		  			    System.out.println ("Warning: A bytes message was discarded because it could not be processed as a javax.jms.TextMessage.");
		  			 }
            else if (message instanceof javax.jms.ObjectMessage)
          			{
		  			    System.out.println ("Warning: An object message was discarded because it could not be processed as a javax.jms.TextMessage.");
		  			 }

            else if (message instanceof javax.jms.StreamMessage)
					{
			   			System.out.println ("Warning: A stream message was discarded because it could not be processed as a javax.jms.TextMessage.");
					 }
        return "- " + msgClass + " from " + msgBody ;

    }
}