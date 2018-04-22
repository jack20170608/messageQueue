package com.fangming.mq.activeMq.samples.nonExclusive.ptp;

import com.fangming.mq.activeMq.samples.nonExclusive.Constant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;


@Component
public class PtpListener1 {

    private static final Logger logger = LoggerFactory.getLogger(PtpListener1.class);

    @JmsListener(destination = Constant.QUEUE_NAME, containerFactory = Constant.QUEUE_CONTAINER)
    public void receive(String msg){
        if (StringUtils.equalsIgnoreCase(msg,"Bad")){
            logger.info("P2P message listener 1 receive [bad] message .");
            throw new RuntimeException("Internal process exception.");
        }
        logger.info("P2P message Listener 1 successfully process [{}]. " , msg);
    }
}
