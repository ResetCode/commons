package com.using.common.core.annotation;

public class ValidateResult {
	protected String message;
	protected boolean valid;

	public static final ValidateResult success() {
		ValidateResult result = new ValidateResult();
		result.valid = true;

		return result;
	}

	public static final ValidateResult fail() {
		return fail(null);
	}

	public static final ValidateResult fail(String message) {
		ValidateResult result = new ValidateResult();
		result.message = message;
		result.valid = false;

		return result;
	}

	private ValidateResult() {

	}

	public String getMessage() {
		return message;
	}

	public boolean isValid() {
		return valid;
	}
}
