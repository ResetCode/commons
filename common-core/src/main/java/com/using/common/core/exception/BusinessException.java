package com.using.common.core.exception;

import com.using.common.core.bean.ErrorEnum;

public class BusinessException extends Exception {
	private static final long serialVersionUID = 1L;
	protected ErrorEnum errorEnum;
	protected Object description;

	public BusinessException(ErrorEnum errorEnum, String message, Throwable cause) {
		super(message, cause);
		this.errorEnum = errorEnum;
		this.description = message;
	}

	public BusinessException(ErrorEnum errorEnum, String message) {
		super(message);
		this.errorEnum = errorEnum;
		this.description = message;
	}

	public BusinessException(ErrorEnum errorEnum, Throwable cause) {
		super(errorEnum.getDesc(), cause);
		this.errorEnum = errorEnum;
		this.description = errorEnum.getDesc();
	}

	public BusinessException(ErrorEnum errorEnum) {
		super(errorEnum.getDesc());
		this.errorEnum = errorEnum;
		this.description = errorEnum.getDesc();
	}

	@SuppressWarnings("unused")
	private BusinessException() {
	}

	public ErrorEnum getErrorEnum() {
		return errorEnum;
	}
}