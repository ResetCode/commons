package com.using.common.queue.anno.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.using.common.queue.anno.QueueService;

public class QueueServiceScanner extends ClassPathBeanDefinitionScanner {
	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		// 仅扫描QueueService注解过的bean
		addIncludeFilter(new AnnotationTypeFilter(QueueService.class));

		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
		if (!beanDefinitions.isEmpty()) {
			for (BeanDefinitionHolder holder : beanDefinitions) {
				GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();

				definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName());
				definition.getPropertyValues().add("jmsTemplate", new RuntimeBeanReference("jmsTemplate"));
				definition.setBeanClass(QueueServiceFactoryBean.class);
				definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
			}
		}

		return beanDefinitions;
	}

	/**
	 * 仅识别interface
	 */
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
		return annotationMetadata.isInterface() && annotationMetadata.isIndependent();
	}

	public QueueServiceScanner(BeanDefinitionRegistry registry) {
		super(registry, false);
	}
}
