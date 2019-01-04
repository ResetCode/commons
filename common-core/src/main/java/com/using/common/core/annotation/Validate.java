package com.using.common.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Constraint(validatedBy = { Validator.class })
public @interface Validate {
	String message() default "";

	Class<? extends ValidateHandler> handler();

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}