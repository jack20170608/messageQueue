package com.fangming.mq.activeMq.samples.nonExclusive.pubSub;

import com.fangming.mq.activeMq.samples.nonExclusive.Constant;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class HelloListener1 {

    @JmsListener(destination = Constant.TOPIC_NAME, containerFactory = Constant.TOPIC_CONTAINER)
    public void receive(String msg){
        System.out.println("订阅者1 - " + msg);
    }
}
