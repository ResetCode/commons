package com.using.common.queue.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QueueMethod {
	/** 队列名称 */
	String name();
	
	/** 是否异步, 默认false */
	boolean async() default false;
}
