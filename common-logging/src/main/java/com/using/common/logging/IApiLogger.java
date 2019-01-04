package com.using.common.logging;

import com.using.common.logging.vo.ApiLog;

public interface IApiLogger {

	public ApiLog logReq(String loggerName, Integer type,Integer invoker, String reqIp, String accessToken, Object object);
	
	public void logReq(final ApiLog apiLog, Integer caller,String respId,Object respBody);
}
