package com.using.common.queue.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.using.common.queue.anno.config.JmsConfig;
import com.using.common.queue.anno.config.QueueServiceScannerRegistrar;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ QueueServiceScannerRegistrar.class, JmsConfig.class })
public @interface QueueServiceScan {
	String[] basePackages();
}
