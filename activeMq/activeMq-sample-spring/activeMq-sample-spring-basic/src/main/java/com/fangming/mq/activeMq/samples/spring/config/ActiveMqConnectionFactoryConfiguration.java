package com.fangming.mq.activeMq.samples.spring.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;


/**
 * if not set up the Connection Factory, spring boot will automate start up a embed active mq broker
 */
@Configuration
public class ActiveMqConnectionFactoryConfiguration {

    @Value("${activeMq.url}")
    private String activeMqUrl;

    @Value("${activeMq.userName}")
    private String userName;

    @Value("${activeMq.password}")
    private String password;

    @Bean
    public ConnectionFactory createActiveMqConnectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(activeMqUrl);
        connectionFactory.setUserName(userName);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

}
