package com.using.common.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import com.using.common.logging.vo.ApiLog;

public abstract class ApiLogger implements IApiLogger{

	/* 
	 * 进方法前调用
	 * @see com.using.arathi.logging.IApiLogger#logReq(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public ApiLog logReq(String loggerName, Integer type,Integer invoker, String reqIp,
			String accessToken, Object object) {
		
//		StackTraceElement elm = getInvoker(loggerName);
		ApiLog log = new ApiLog();
		Date curDate = new Date();
//		if(elm != null) {
//			log.setInterfacez(elm.getClassName() + "." + elm.getMethodName());
//		}
		log.setInterfacez(loggerName);
		log.setInvokerIp(reqIp);
		log.setType(type);
		log.setCreateTime(curDate);
		log.setAccessToken(accessToken);
		if(object !=null) {
			log.setReqBody(object.toString());
		}
		if(invoker != null) {
			log.setInvoker(invoker);
		}
		
		
		return log;
	}
	
	/* 方法结束后调用
	 * @see com.using.arathi.logging.IApiLogger#logReq(com.using.arathi.logging.vo.ApiInvokeLog, java.lang.Integer, java.lang.String, java.lang.Object)
	 */
	@Override
	public void logReq(ApiLog apiLog, Integer caller, String respId,
			Object object) {
//		if(apiLog == null || apiLog) {
			
//		}
		
		apiLog.setCaller(caller);
		apiLog.setCallerIp(respId);
		
		Date reqTime = apiLog.getCreateTime();
		long curTime = System.currentTimeMillis();
		apiLog.setRespTime(curTime - reqTime.getTime());
		
		if(object != null) {
			apiLog.setRespBody(object.toString());
		}
		
		//由子类实现
		saveLog(apiLog);
	}
	
	protected abstract void saveLog(ApiLog log);
	
	/**
	 * 获取运行时stackTrance
	 * @param className
	 * @return
	 */
	public static StackTraceElement getInvoker(String className) {
		StackTraceElement[] elms = new Exception().getStackTrace();
		for(StackTraceElement elm : elms) {
			if (elm.getClassName().equals(className)) {
				return elm;
			}
		}
		
		return null;
	}
	
	/**
	 * 获取异常信息
	 * @param e
	 * @return
	 */
	public static String getExceptionCause(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		try {
			e.printStackTrace(pw);
			String result = sw.toString();
			while( result.getBytes().length > 4096) {
				
				result = result.substring(0,result.length() /2);
			}
			return result;
		} finally {
			try {
				pw.close();
				sw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
}
