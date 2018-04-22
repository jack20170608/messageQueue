package com.fangming.mq.activeMq.samples.nonExclusive.ptp;

import com.fangming.mq.activeMq.samples.nonExclusive.Constant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class PtpProducer {

    @Autowired
    private JmsTemplate jmsQueueTemplate;

    public void send(String messageContent){
        if (StringUtils.equalsIgnoreCase(messageContent,"Exception")){
            throw new RuntimeException("Message send failure.");
        }
        jmsQueueTemplate.send(Constant.QUEUE_NAME, session -> {
            return session.createTextMessage(messageContent);
        });
    }
}
