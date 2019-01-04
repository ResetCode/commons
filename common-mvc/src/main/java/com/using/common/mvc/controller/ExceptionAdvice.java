package com.using.common.mvc.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.using.common.core.bean.ErrorEnum;
import com.using.common.core.bean.JsonResult;
import com.using.common.core.exception.BusinessException;
import com.using.common.core.exception.InvalidTokenExcpetion;
import com.using.common.core.exception.NoPermissionException;

@ControllerAdvice
@ResponseBody
public class ExceptionAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public JsonResult<String> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		jsonResult.setErrorMessage(ErrorEnum.ERROR_MISSING_PARAMETERS, "请求参数缺少[" + exception.getParameterName() + "]");

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestPartException.class)
	public JsonResult<String> handleMissingServletRequestPartException(MissingServletRequestPartException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		jsonResult.setErrorMessage(ErrorEnum.ERROR_MISSING_PARAMETERS,
				"请求参数缺少[" + exception.getRequestPartName() + "]");

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public JsonResult<String> handleHttpRequestMethodNotSupportedException(
			HttpRequestMethodNotSupportedException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		jsonResult.setErrorMessage(ErrorEnum.ERROR_MISSING_PARAMETERS, "不允许[" + exception.getMethod() + "]请求");

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public JsonResult<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		jsonResult.setErrorMessage(ErrorEnum.ERROR_PARAM, "请求参数[" + exception.getParameter().getParameterName() + "]需为["
				+ exception.getRequiredType().getSimpleName() + "]类型");

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public JsonResult<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		jsonResult.setErrorMessage(ErrorEnum.ERROR_PARAM, "不支持的ContentType[" + exception.getContentType() + "]");

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(org.springframework.validation.BindException.class)
	public JsonResult<String> BindException(org.springframework.validation.BindException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		List<ObjectError> errors = exception.getAllErrors();
		List<String> messages = new ArrayList<>();
		for (ObjectError error : errors) {
			messages.add(error.getDefaultMessage());
		}

		jsonResult.setErrorMessage(ErrorEnum.ERROR_PARAM, "参数有误[" + StringUtils.join(messages, ",") + "]");

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public JsonResult<String> MethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		BindingResult result = exception.getBindingResult();
		List<ObjectError> errors = result.getAllErrors();
		List<String> messages = new ArrayList<>();
		for (ObjectError error : errors) {
			messages.add(error.getDefaultMessage());
		}

		jsonResult.setErrorMessage(ErrorEnum.ERROR_PARAM, "参数有误[" + StringUtils.join(messages, ",") + "]");

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public JsonResult<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		String message = exception.getMessage();
		if (StringUtils.isNotBlank(message)) {
			int index = message.indexOf(":");
			if (index > 0) {
				message = message.substring(0, index);
			}
		}

		jsonResult.setErrorMessage(ErrorEnum.ERROR_PARAM, "请求内容不全[" + message + "]");

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public JsonResult<String> handleConstraintViolationException(ConstraintViolationException exception) {
		JsonResult<String> jsonResult = new JsonResult<>();
		Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
		List<String> messages = new ArrayList<>();
		for (ConstraintViolation<?> violation : violations) {
			messages.add(violation.getMessage());
		}
		String message = StringUtils.join(messages, ",");
		jsonResult.setErrorMessage(ErrorEnum.ERROR_PARAM, message);
		LOGGER.error(message, exception);

		return jsonResult;
	}

	@ExceptionHandler(ParseException.class)
	public JsonResult<String> handleParseException(ParseException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		jsonResult.setErrorMessage(ErrorEnum.ERROR_PARAM, exception.getMessage());

		return jsonResult;
	}

	@ExceptionHandler(BusinessException.class)
	public JsonResult<String> handleBusinessException(BusinessException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		jsonResult.setErrorMessage(exception.getErrorEnum(), exception.getMessage());

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(InvalidTokenExcpetion.class)
	public JsonResult<String> handleInvalidTokenExcpetion(InvalidTokenExcpetion exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		jsonResult.setErrorMessage(ErrorEnum.AUTH_NOT_LOGIN);

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(NoPermissionException.class)
	public JsonResult<String> handleNoPermissionException(NoPermissionException exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		jsonResult.setErrorMessage(ErrorEnum.AUTH_NO_AUTHORITY);

		return jsonResult;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public JsonResult<String> handleException(Exception exception) {
		LOGGER.error(exception.getMessage(), exception);

		JsonResult<String> jsonResult = new JsonResult<>();
		jsonResult.setErrorMessage(ErrorEnum.ERROR_SYSTEM);

		return jsonResult;
	}
}
