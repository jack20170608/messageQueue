package com.fangming.mq.activeMq.samples.nonExclusive.pubSub;

import com.fangming.mq.activeMq.samples.nonExclusive.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "pubSub")
public class PubSubController {

    @Autowired
    private PubSubProducer pubSubProducer;

    @RequestMapping(value = "/send/{messageContent}")
    public String send(@PathVariable("messageContent") String messageContent){
        pubSubProducer.send(messageContent);
        return Constant.RESPONSE_SUCCESS;
    }
}
