package com.using.common.queue.anno.config;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@ConditionalOnMissingBean(JmsTemplate.class)
public class JmsConfig {
	@Bean
	public JmsTemplate jmsTemplate(@Autowired ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		return jmsTemplate;
	}
}
