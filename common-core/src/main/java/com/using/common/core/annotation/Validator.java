package com.using.common.core.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class Validator implements ConstraintValidator<Validate, Object> {
	protected ValidateHandler handler;
	protected String message;

	@Override
	public void initialize(Validate validate) {
		this.message = validate.message();

		try {
			handler = validate.handler().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		ValidateResult result = handler.validate(value, context);
		if (StringUtils.isNotBlank(result.getMessage())) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(result.getMessage()).addConstraintViolation();
		}
		
		return result.isValid();
	}
}
