package com.fangming.mq.activeMq.samples.explorerJms.components.requestReplyStyle;/*
 * � 2001-2009, Progress Software Corporation and/or its subsidiaries or affiliates.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 
Sample Application

Writing a Basic JMS Application with Point-to-Point Queues,
using:
    - Synchronous Request/Reply
    - javax.jms.QueueRequestor class
    - JMSReplyTo Header

When this program runs, it waits for messages on the queue,
"SampleQ1" (by default).
When that message arrives, a response based on the request
is sent back to the "Requestor" specified in the JMSReplyTo header.

This sample replies with a simple text manipulation of the request;
the text is either folded to all UPPERCASE or all lowercase.

Suggested demonstration:
  - In a console window with the environment set,
    start a copy of the Replier. For example:
       java Replier -u SampleQReplier
  - In another console window, start a Requestor.
    For example:
       java Requestor -u SampleQRequestor
  - Enter text in the Requestor window then press Enter.
    The Replier responds with the message in all uppercase characters.
  - Start other Requestors with different user names to see that
    replies are not broadcast to all users. For example:
       java Requestor -u SampleRequestorFoo

  - Start other Repliers.
  - See that only one replier is receiving messages,(as it should).
  - See the Requestor only receives one response.
       java Replier -u toLower -m lowercase

 */
import com.fangming.mq.activeMq.samples.explorerJms.ExitException;
import org.apache.activemq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.*;
import javax.jms.Message;


@Component
public class Replier implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Replier.class);

    public enum TransferMode {
        UPPERCASE,LOWERCASE
    }

    @Value("${queue.brokerUrl}")
    private String broker;

    @Value("${queue.connectID}")
    private String connectID;

    @Value("${queue.username}")
    private String username;

    @Value("${queue.password}")
    private String password;

    @Value("${queue.requestReplier.defaultQueue}")
    private String defaultQueueName;

    @Value("${queue.requestReplier.defaultMode}")
    private TransferMode defaultMode;

    private Connection connect = null;
    private Session session = null;
    private MessageProducer replier = null;
    private Queue queue;
    private MessageConsumer messageConsumer;

    @PostConstruct
    private void initQueue() {
        try {
            javax.jms.ConnectionFactory factory;
            factory = new ActiveMQConnectionFactory(username, password, broker);
            connect = factory.createConnection(username, password);
            session = connect.createSession(true, Session.AUTO_ACKNOWLEDGE);

            queue = session.createQueue (defaultQueueName);
            MessageConsumer receiver = session.createConsumer(queue);

            receiver.setMessageListener(this);
            replier = session.createProducer(null);  // Queue will be set for each reply

            // Now that all setup is complete, start the Connection.
            connect.start();

        } catch (javax.jms.JMSException jmse) {
            LOGGER.error("Cannot connect to Broker");
            throw new ExitException();
        }
    }

    /**
     * Handle the message.
     * (as specified in the javax.jms.MessageListener interface).
     *
     * IMPORTANT NOTES:
     * (1)We must follow the design paradigm for JMS
     *    synchronous requests.  That is, we must:
     *     - get the message
     *     - look for the header specifying JMSReplyTo
     *     - send a reply to the queue specified there.
     *    Failing to follow these steps might leave the originator
     *    of the request waiting forever.
     * (2)Unlike the 'Talk' sample and others using an asynchronous
     *    message listener, it is possible here to use ONLY
     *    ONE SESSION because the messages being sent are sent from
     *    the same thread of control handling message delivery. For
     *    more information see the JMS spec v1.0.2 section 4.4.6.
     *
     * OPTIONAL BEHAVIOR: The following actions taken by the
     * message handler represent good programming style, but are
     * not required by the design paradigm for JMS requests.
     *   - set the JMSCorrelationID (tying the response back to
     *     the original request.
     *   - use transacted session "commit" so receipt of request
     *     won't happen without the reply being sent.
     *
     */
    public void onMessage( Message aMessage)
    {
        try
        {
            // Cast the message as a text message.
            javax.jms.TextMessage textMessage = (TextMessage)aMessage;

            // This handler reads a single String from the
            // message and prints it to the standard output.
            try
            {
                String string = textMessage.getText();
                LOGGER.info( "[Request] " + string );

                // Check for a ReplyTo Queue
                javax.jms.Queue replyQueue = (javax.jms.Queue) aMessage.getJMSReplyTo();
                if (replyQueue != null)
                {
                    // Send the modified message back.
                    javax.jms.TextMessage reply =  session.createTextMessage();
                    if (TransferMode.UPPERCASE == defaultMode)
                        reply.setText("Uppercasing-" + string.toUpperCase());
                    else
                        reply.setText("Lowercasing-" + string.toLowerCase());

                    reply.setJMSCorrelationID(aMessage.getJMSMessageID());
                    replier.send (replyQueue, reply);
                    session.commit();
                }
            }
            catch (javax.jms.JMSException jmse)
            {
                jmse.printStackTrace();
            }
        }
        catch (RuntimeException rte)
        {
            rte.printStackTrace();
        }
    }

}
