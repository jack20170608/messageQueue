package com.fangming.mq.activeMq.samples.nonExclusive.ptp;

import com.fangming.mq.activeMq.samples.nonExclusive.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * 点对点模式 - 生产者
 * 1.每个消息只有一个消费者（Consumer）(即一旦被消费，消息就不再在消息队列中)
 * 2.发送者和接收者之间在时间上没有依赖性，也就是说当发送者发送了消息之后，不管接收者有没有正在运行，它不会影响到消息被发送到队列
 * 3.接收者在成功接收消息之后需向队列应答成功
 * Created by jason-geng on 5/14/17.
 */
@Component
public class PtpProducer {

    @Autowired
    private JmsTemplate jmsQueueTemplate;

    /**
     * 发送原始消息 Message
     */
    public void send(){
        jmsQueueTemplate.send(Constant.QUEUE_NAME, session -> {
            return session.createTextMessage("我是原始消息");
        });
//        throw new RuntimeException();
    }

    /**
     * 发送消息自动转换成原始消息
     */
    public void convertAndSend(){
        jmsQueueTemplate.convertAndSend(Constant.QUEUE_NAME, "我是自动转换的消息");
    }
}
