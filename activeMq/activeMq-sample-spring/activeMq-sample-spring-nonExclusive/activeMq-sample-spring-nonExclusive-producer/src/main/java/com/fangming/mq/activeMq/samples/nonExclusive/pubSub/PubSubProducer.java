package com.fangming.mq.activeMq.samples.nonExclusive.pubSub;

import com.fangming.mq.activeMq.samples.nonExclusive.Constant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class PubSubProducer {

    @Autowired
    private JmsTemplate jmsTopicTemplate;


    public void send(String messageContent) {
        if (StringUtils.equalsIgnoreCase(messageContent,"Exception")){
            throw new RuntimeException("Message send failure.");
        }
        jmsTopicTemplate.send(Constant.TOPIC_NAME, session -> {
            return session.createTextMessage(messageContent);
        });
    }
}

