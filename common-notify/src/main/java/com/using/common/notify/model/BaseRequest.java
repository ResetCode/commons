package com.using.common.notify.model;

import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

/**
 * 基础数据
 */
public class BaseRequest {

	private String id;
	private String ip;
	private String content;
	private String destin;
	private Integer type;
	private Date createTime;
	private String extra;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDestin() {
		return destin;
	}
	public void setDestin(String destin) {
		this.destin = destin;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(Map<String, Object> map) {
		if(map == null || map.isEmpty()) {
			this.extra = "{}";
		}
		this.extra = JSONArray.toJSONString(map);
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
