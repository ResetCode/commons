package com.using.common.notify.model;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.using.common.notify.enums.MailTemplate;

public class MailTemplateRequest extends BaseRequest{

	private MailTemplate template;
	private Map<String, String> ext = new HashedMap<>();
	

	public MailTemplate getTemplate() {
		return template;
	}

	public void setTemplate(MailTemplate template) {
		this.template = template;
	}

	public Map<String, String> getExt() {
		return ext;
	}

	public void setExt(Map<String, String> ext) {
		this.ext = ext;
	}
	
	
}
