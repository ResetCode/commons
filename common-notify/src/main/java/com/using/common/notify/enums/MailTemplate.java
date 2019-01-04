package com.using.common.notify.enums;

/**
 * 邮件模板
 * @author liumohan
 *
 */
public enum MailTemplate {

	VERIFY_CODE("验证码","您的验证码是：%s,3分钟内有效。如非您本人操作，可忽略本消息【有征科技】");
	
	private String title;
	private String text;
	
	private MailTemplate(String title,String text) {
		this.text = text;
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
