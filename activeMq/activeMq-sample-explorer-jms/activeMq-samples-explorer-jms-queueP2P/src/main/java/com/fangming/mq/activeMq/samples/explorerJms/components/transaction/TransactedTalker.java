package com.fangming.mq.activeMq.samples.explorerJms.components.transaction;/*
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
    - Send and Receive
    - Transacted Sessions
    - Multiple Sessions

This sample starts up with a username, and the queues you are
sending on, and receiving on.

Continue writing lines and pressing enter to buffer messages until a
specific key word is used to confirm the messages or to completely
forget them.

Messages are buffered and sent when a specific string is seen ("COMMIT").
Messages buffered can be discarded by entering a specified string ("CANCEL").


*/

import com.fangming.mq.activeMq.samples.explorerJms.ExitException;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.List;
import java.util.Objects;

public class TransactedTalker implements Runnable, MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactedTalker.class);
    private static final int    MESSAGE_LIFESPAN = 1800000;  // milliseconds (30 minutes)
    private static final List<String> MESSAGE_TO_SEND
            = Lists.newArrayList("Hi","hxxxx","CANCEL",
                                 "Hello, this is jack.", "COMMIT",
                                 "Hello, this is jack", "What is your name?", "COMMIT");

    private javax.jms.Session sendSession = null;
    private javax.jms.Session receiveSession = null;
    private javax.jms.MessageProducer sender = null;
    private String name;
    private Connection connection;

    public TransactedTalker(String name, Connection connection, String sendQueueName, String receiveQueueName) {
        this.name = name;
        this.connection = connection;
        try {
            // We want to be able up commit/rollback messages sent,
            // but not affect messages received.  Therefore, we need two sessions.
            sendSession = connection.createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
            receiveSession = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

            Queue sendQueue = sendSession.createQueue (sendQueueName);
            sender = sendSession.createProducer(sendQueue);

            Queue receiveQueue = receiveSession.createQueue (receiveQueueName);
            MessageConsumer qReceiver = receiveSession.createConsumer(receiveQueue);
            qReceiver.setMessageListener(this);
            // Now that 'receive' setup is complete, start the Connection
            connection.start();

        }catch (JMSException e){
            LOGGER.error("Session create failure, please check if the message broker is working.");
            throw new ExitException();
        }
        // Create Sender and Receiver 'Talk' queues

    }

    @Override
    public void run() {
        Objects.nonNull(sendSession);
        Objects.nonNull(receiveSession);
        Objects.nonNull(sender);


        LOGGER.info("TransactedTalk application:");
        LOGGER.info ("===========================" );
        LOGGER.info  ("1. Enter text to send and then press Enter to stage the message.");
        LOGGER.info  ("2. Add a few messages to the transaction batch.");
        LOGGER.info  ("3. Then, either:");
        LOGGER.info  ("     o Enter the text 'COMMIT', and press Enter to send all the staged messages.");
        LOGGER.info  ("     o Enter the text 'CANCEL', and press Enter to drop the staged messages waiting to be sent.");
        try {
            for (String s : MESSAGE_TO_SEND) {
                if (s.trim().equals("CANCEL")) {
                    // Rollback the messages. A new transaction is implicitly
                    // started for following messages.
                    LOGGER.info("Cancelling messages...");
                    sendSession.rollback();
                    LOGGER.info("Staged messages have been cleared.");
                } else if (s.length() > 0) {
                    TextMessage msg = sendSession.createTextMessage();
                    msg.setText(name + ": " + s);
                    LOGGER.info("Send message: [{}].", msg.getText());
                    // Queues usually are used for PERSISTENT messages.
                    // Hold messages for 30 minutes (1,800,000 millisecs).
                    sender.send(msg,
                            DeliveryMode.PERSISTENT,
                            Message.DEFAULT_PRIORITY,
                            MESSAGE_LIFESPAN
                    );
                    // See if we should send the messages
                    if (s.trim().equals("COMMIT")) {
                        // Commit (send) the messages. A new transaction is
                        // implicitly  started for following messages.
                        LOGGER.info("Committing messages...");
                        sendSession.commit();
                        LOGGER.info("Staged messages have all been sent.");
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error("Talker talk failure.");
        }
    }

    public void onMessage( javax.jms.Message aMessage) {
        try {
            // Cast the message as a text message.
            javax.jms.TextMessage textMessage = (javax.jms.TextMessage) aMessage;

            // This handler reads a single String from the
            // message and prints it to the standard output.
            try {
                String string = textMessage.getText();
                LOGGER.info("Receive message : [{}].", string );
            } catch (javax.jms.JMSException jmse) {
                jmse.printStackTrace();
            }
        }
        catch (RuntimeException rte) {
            rte.printStackTrace();
        }
    }

    public void exit() {
        try {
            sendSession.rollback(); // Rollback any uncommitted messages.
            connection.close();
        } catch (javax.jms.JMSException jmse) {
            LOGGER.error("Talker exit failure due to [{}].", jmse.getMessage());
        }
    }

}