package com.using.common.queue.config;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.using.common.queue.anno.QueueListener;
import com.using.common.queue.anno.QueueMethod;
import com.using.common.queue.bean.QueueResponse;

//@Component
public class ListenerConfig implements ApplicationContextAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListenerConfig.class);
	@Value("${using.queue.listenerNum}")
	private Integer listenerNum;
	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(QueueListener.class);
		for (Object beanObj : beanMap.values()) {
			if (AopUtils.isAopProxy(beanObj)) {
				beanObj = AopProxyUtils.getSingletonTarget(beanObj);
			}
			Object targetObj = beanObj;

			Method[] methods = beanObj.getClass().getDeclaredMethods();
			for (Method method : methods) {
				QueueMethod handler = method.getAnnotation(QueueMethod.class);
				if (handler == null)
					continue;

				Destination destination = new ActiveMQQueue(handler.name());
				for (int i = 0; i < listenerNum; i++) {
					try {
						Connection connection = jmsTemplate.getConnectionFactory().createConnection();
						Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
						MessageConsumer consumer = session.createConsumer(destination);
						consumer.setMessageListener(new MessageListener() {
							@Override
							public void onMessage(Message message) {
								try {
									ObjectMessage objectMessage = (ObjectMessage) message;
									Object object = objectMessage.getObject();
									Object[] args = object == null ? null : (Object[]) object;
									Object result = null;

									QueueResponse<Serializable> response = new QueueResponse<>();
									try {
										result = method.invoke(targetObj, args);
										if (result != null) {
											Serializable data = (Serializable) result;
											response.setData(data);
										}
									} catch (InvocationTargetException e) {
										Throwable throwable = e.getTargetException();
										response.setThrowable(throwable);
									} catch (Exception e) {
										response.setThrowable(e);
									}

									if (!handler.async()) {
										jmsTemplate.send(objectMessage.getJMSReplyTo(), new MessageCreator() {
											@Override
											public Message createMessage(Session session) throws JMSException {
												ObjectMessage message = session.createObjectMessage(response);
												message.setJMSCorrelationID(objectMessage.getJMSCorrelationID());
												return message;
											}
										});
									}
								} catch (Exception e) {
									LOGGER.error(e.getMessage(), e);
								}
							}
						});
						connection.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
