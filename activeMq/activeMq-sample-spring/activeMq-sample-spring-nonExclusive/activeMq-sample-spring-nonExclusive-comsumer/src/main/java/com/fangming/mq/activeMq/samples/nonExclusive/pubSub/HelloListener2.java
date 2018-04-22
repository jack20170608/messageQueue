package com.fangming.mq.activeMq.samples.nonExclusive.pubSub;

import com.fangming.mq.activeMq.samples.nonExclusive.Constant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class HelloListener2 {

    private static final Logger logger = LoggerFactory.getLogger(HelloListener2.class);

    @JmsListener(destination = Constant.TOPIC_NAME, containerFactory = Constant.TOPIC_CONTAINER)
    public void receive(String msg){
        if (StringUtils.equalsIgnoreCase(msg,"Bad")){
            logger.info("PubAndSub message listener 2 receive [bad] message .");
            throw new RuntimeException("Internal process exception.");
        }
        logger.info("PubAndSub message Listener 2 successfully process [{}]. " , msg);
    }
}
