package com.fangming.mq.activeMq.samples.nonExclusive.ptp;

import com.fangming.mq.activeMq.samples.nonExclusive.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "ptp")
public class PtpController {

    @Autowired
    private PtpProducer ptpProducer;

    @RequestMapping(value = "/send/{messageContent}")
    public Object send(@PathVariable("messageContent") String messageContent){
        ptpProducer.send(messageContent);
        return Constant.RESPONSE_SUCCESS;
    }
}
