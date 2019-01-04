package com.using.common.logging.vo;

import java.util.Date;

public class ApiLog {

//	private Long id;
	
	private Integer invoker;
	
	private Integer caller;
	
	private String invokerIp;
	
	private String callerIp;
	
	private String interfacez;
	
	private String accessToken;
	
	private Long respTime;
	
	private Integer type;
	
	private String reqBody;
	
	private String respBody;
	
	private Date createTime;

//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}

	public Integer getInvoker() {
		return invoker;
	}

	public void setInvoker(Integer invoker) {
		this.invoker = invoker;
	}

	public Integer getCaller() {
		return caller;
	}

	public void setCaller(Integer caller) {
		this.caller = caller;
	}

	public String getInvokerIp() {
		return invokerIp;
	}

	public void setInvokerIp(String invokerIp) {
		this.invokerIp = invokerIp;
	}

	public String getCallerIp() {
		return callerIp;
	}

	public void setCallerIp(String callerIp) {
		this.callerIp = callerIp;
	}

	public String getInterfacez() {
		return interfacez;
	}

	public void setInterfacez(String interfacez) {
		this.interfacez = interfacez;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Long getRespTime() {
		return respTime;
	}

	public void setRespTime(Long respTime) {
		this.respTime = respTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getReqBody() {
		return reqBody;
	}

	public void setReqBody(String reqBody) {
		this.reqBody = reqBody;
	}

	public String getRespBody() {
		return respBody;
	}

	public void setRespBody(String respBody) {
		this.respBody = respBody;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
