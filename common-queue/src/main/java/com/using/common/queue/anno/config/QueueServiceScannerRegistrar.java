package com.using.common.queue.anno.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import com.using.common.queue.anno.QueueServiceScan;

public class QueueServiceScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
	private ResourceLoader resourceLoader;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes annoAttrs = AnnotationAttributes
				.fromMap(importingClassMetadata.getAnnotationAttributes(QueueServiceScan.class.getName()));
		QueueServiceScanner scanner = new QueueServiceScanner(registry);

		if (resourceLoader != null) {
			scanner.setResourceLoader(resourceLoader);
		}

		String[] basePackage = annoAttrs.getStringArray("basePackages");
		scanner.doScan(basePackage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}
