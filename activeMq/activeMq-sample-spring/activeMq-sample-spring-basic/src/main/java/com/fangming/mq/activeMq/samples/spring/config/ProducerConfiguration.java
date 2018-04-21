package com.fangming.mq.activeMq.samples.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
public class ProducerConfiguration {

	@Autowired
	private ConnectionFactory connectionFactory;

	// Example use of CachingConnectionFactory for the producer
	@Bean
	public JmsTemplate jmsTemplate() {
		CachingConnectionFactory ccf = new CachingConnectionFactory(connectionFactory);
		return new JmsTemplate(ccf);
	}
}
