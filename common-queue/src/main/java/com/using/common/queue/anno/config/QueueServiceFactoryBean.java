package com.using.common.queue.anno.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.jms.core.JmsTemplate;

public class QueueServiceFactoryBean<T> implements FactoryBean<T> {
	private JmsTemplate jmsTemplate;
	private T target;
	private Class<T> clazz;

	@Override
	public T getObject() throws Exception {
		if (target == null) {
			QueueServiceProxy<T> proxy = new QueueServiceProxy<>(clazz, jmsTemplate);
			target = proxy.getTarget();
		}

		return this.target;
	}

	@Override
	public Class<?> getObjectType() {
		return this.clazz;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public QueueServiceFactoryBean(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
}
