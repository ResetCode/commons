package com.using.common.queue.anno.config;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.using.common.core.exception.BusinessException;
import com.using.common.util.UUIDUtils;
import com.using.common.queue.anno.QueueMethod;
import com.using.common.queue.bean.QueueResponse;

public class QueueServiceProxy<T> implements InvocationHandler {
	private T target;
	private Class<T> targetClass;
	private JmsTemplate jmsTemplate;

	@SuppressWarnings("unchecked")
	public QueueServiceProxy(Class<T> clazz, JmsTemplate jmsTemplate) {
		target = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);
		this.targetClass = clazz;
		this.jmsTemplate = jmsTemplate;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == targetClass) {
			QueueMethod queueMethod = method.getAnnotation(QueueMethod.class);
			if (queueMethod == null)
				throw new IllegalAccessException("该方法不是一个队列方法");

			String queueName = queueMethod.name();
			boolean async = queueMethod.async();
			String correlationID = UUIDUtils.randReplacedLower();
			if (async) {
				jmsTemplate.send(queueName, new ObjectMessageCreator(correlationID, args));
				return correlationID;
			} else {
				Message message = jmsTemplate.sendAndReceive(queueName, new ObjectMessageCreator(correlationID, args));

				if (message == null) {
					return null;
				}
				ObjectMessage objectMessage = (ObjectMessage) message;
				Object object = objectMessage.getObject();
				if (object == null) {
					return null;
				}

				QueueResponse<?> response = (QueueResponse<?>) object;
				if (response.getHasException()) {
					if (response.getThrowable() instanceof BusinessException)
						throw (BusinessException) response.getThrowable();
					else
						throw response.getThrowable();
				}

				return response.getData();
			}
		} else {
			return method.invoke(this, args);
		}
	}

	protected class ObjectMessageCreator implements MessageCreator {
		protected String correlationID;
		protected Serializable data;

		public ObjectMessageCreator(String correlationID, Serializable data) {
			this.correlationID = correlationID;
			this.data = data;
		}

		@Override
		public Message createMessage(Session session) throws JMSException {
			ObjectMessage message = session.createObjectMessage(data);
			message.setJMSCorrelationID(correlationID);
			return message;
		}
	}

	public T getTarget() {
		return target;
	}

	public Class<T> getTargetClass() {
		return targetClass;
	}
}
