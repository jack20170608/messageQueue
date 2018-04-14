package com.fangming.mq.activeMq.samples.explorerJms.components.requestReplyStyle; /**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
Copyright 2001-2008, Progress Software Corporation -  All Rights Reserved

Sample Application

Writing a Basic JMS Application with Point-to-Point Queues,
using:
    - Synchronous Request/Reply
    - javax.jms.QueueRequestor class
    - JMSReplyTo Header

When this program runs, it reads input from System.in
and then sends the text as a message to the queue, "Q1"
(by default).

A "Replier" class should be waiting for the request.
It will reply with a message.

NOTE: Unlike the Publish-Subscribe example, you need
not run the Replier first.  However, this Requestor
will block until the Replier is started to service the queue.

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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.*;


@Component
public class Requestor implements Runnable, DisposableBean{

    private static final Logger LOGGER = LoggerFactory.getLogger(Requestor.class);

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

    @Value("${queue.requestReplier.requestContent}")
    private String requestContent;

    @Value("${queue.requestReplier.requestInterval}")
    private long requestInterval;

    private QueueConnection connect = null;
    private QueueSession session = null;
    private QueueRequestor requestor = null;
    private Queue queue = null;

    private Thread wrapperThread;


    @PostConstruct
    private void initQueue() {
        try {
            javax.jms.ConnectionFactory factory;
            factory = new ActiveMQConnectionFactory(username, password, broker);
            connect = (QueueConnection) factory.createConnection(username, password);
            session = (QueueSession) connect.createSession(false, Session.AUTO_ACKNOWLEDGE);

            queue = session.createQueue (defaultQueueName);
            requestor = new javax.jms.QueueRequestor(session, queue);

            // Now that all setup is complete, start the Connection.
            connect.start();
            wrapperThread = new Thread(this);
            wrapperThread.start();

        } catch (javax.jms.JMSException jmse) {
            LOGGER.error("Cannot connect to Broker");
            throw new ExitException();
        }
    }

    @Override
    public void run() {
        int messageNo = 10000;
        try {
            while (true) {
                TextMessage msg = session.createTextMessage();
                msg.setText("No:" + messageNo + " " + username + ": [" + requestContent + "]");
                // Instead of sending, we will use the QueueRequestor.
                javax.jms.Message response = requestor.request(msg);
                // The message should be a TextMessage.  Just report it.
                javax.jms.TextMessage textMessage = (javax.jms.TextMessage) response;
                LOGGER.info("[Reply] " + textMessage.getText());
                Thread.sleep(requestInterval);
                messageNo++;
            }
        } catch (Exception e) {
            LOGGER.error("Requester start up failure.");
        }

    }

    @Override
    public void destroy() throws Exception {
        try {
            if (null != requestor) {
                requestor.close();
            }
            if (null != connect) {
                connect.close();
            }
        } catch (javax.jms.JMSException jmse)
        {
            jmse.printStackTrace();
        }
    }
}
