package com.using.common.core.annotation;

import javax.validation.ConstraintValidatorContext;

public interface ValidateHandler {
	
	ValidateResult validate(Object value, ConstraintValidatorContext context);
}