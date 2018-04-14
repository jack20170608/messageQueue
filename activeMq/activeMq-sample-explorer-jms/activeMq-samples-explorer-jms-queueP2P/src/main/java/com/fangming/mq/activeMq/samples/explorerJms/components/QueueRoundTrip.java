package com.fangming.mq.activeMq.samples.explorerJms.components;/*
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

Queue/PTP
Send and receive using multiple sessions and a temporary queue

This sample shows the round trip time for a message being sent to the broker
and received. A temporary queue is used to guarantee that each instance of this
sample receives its own messages only.

 */
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;


@Component
public class QueueRoundTrip implements DisposableBean, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueRoundTrip.class);

    @Value("${queue.brokerUrl}")
    private String broker ;

    @Value("${queue.connectID}")
    private String connectID ;

    @Value("${queue.username}")
    private String username ;

    @Value("${queue.password}")
    private String password ;

    @Value("${queue.roundTrip.times}")
    private int numTests = 100;


    private static final int msgSize = 1400;
    private static byte[] msgBody = new byte[msgSize];

    private Thread wrapperThread ;

    private javax.jms.Connection connection     = null;
    private javax.jms.Session sendSession       = null;
    private javax.jms.Session receiveSession    = null;
    private javax.jms.MessageProducer sender    = null;
    private javax.jms.MessageConsumer receiver  = null;


    @PostConstruct
    private void initQueue(){
        try {
            // create the payload
            byte charToWrite = (0x30);
            for (int i = 0; i < msgSize; i++) {
                msgBody[i] = charToWrite;
                charToWrite = (byte) (charToWrite + 0x01);
                if (charToWrite == (0x39)) {
                    charToWrite = (0x30);
                }
            }

            //Set up two sessions, one for sending and the other for receiving
            ConnectionFactory factory = new ActiveMQConnectionFactory(username, password, broker);
            connection = factory.createConnection(username, password);
            sendSession = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            receiveSession = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

            javax.jms.TemporaryQueue tempQueue = sendSession.createTemporaryQueue();
            receiver = receiveSession.createConsumer(tempQueue);
            sender = sendSession.createProducer(tempQueue);

            connection.start();

            wrapperThread = new Thread(this);
            wrapperThread.setName("QueueRoundTripThread");
            wrapperThread.start();

        } catch (javax.jms.JMSException jmse) {
            LOGGER.error("Cannot connect to broker- " + broker + ".\n");
        }
    }


    @Override
    public void run() {
        try {
            LOGGER.info("The Round Trip process starting.");
            //create a message to send
            javax.jms.BytesMessage msg = sendSession.createBytesMessage();
            msg.writeBytes(msgBody);

            //send and receive the message the specified number of times:
            long time = System.currentTimeMillis();
            for (int i = 0; i < numTests; i++) {
                sender.send(msg);
                msg = (javax.jms.BytesMessage) receiver.receive();
            }
            time = System.currentTimeMillis() - time;

            LOGGER.info("Time for " + numTests + " sends and receives:[" + time + "]ms." +
                    "Average Time per message:[" + (float) time / (float) numTests + "]ms");
            LOGGER.info("The Round Trip testing done.");
        }catch (JMSException e){
            LOGGER.error("Round Trip testing failure, due to ", e.getMessage());
        }
    }

    @Override
    public void destroy() throws Exception {
        if (null != connection ) {
            connection.close();
        }
    }
}