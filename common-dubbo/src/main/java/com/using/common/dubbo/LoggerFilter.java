package com.using.common.dubbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.rpc.Result;
import com.reger.dubbo.rpc.filter.ConsumerFilter;
import com.reger.dubbo.rpc.filter.JoinPoint;
import com.reger.dubbo.rpc.filter.ProviderFilter;

@Component
public class LoggerFilter implements ConsumerFilter, ProviderFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerFilter.class);

	@Override
	public Result invoke(JoinPoint<?> point) {
		// TODO 处理调用日志
		LOGGER.info("LoggerFilter!!!!!!!!!!!!!!!!!");
		Result result = point.proceed();
		if(result.hasException()) {
			LOGGER.error(result.getException().getMessage(), result.getException());
		}
		
		return result;
	}
}