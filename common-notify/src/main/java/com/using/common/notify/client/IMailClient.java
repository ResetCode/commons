package com.using.common.notify.client;

import javax.mail.MessagingException;

import com.using.common.notify.enums.MailTemplate;

public interface IMailClient {

	
	/**
	 * 发送邮件
	 * @param to
	 * 			收件方
	 * @param title
	 * 			标题
	 * @param content
	 * 			内容
	 * @throws MessagingException 
	 */
	public void sendMail(String to, String title, String content) throws MessagingException;
	
	/**
	 * 以模板形式发送短信
	 * @param to
	 * @param templte
	 * @param args
	 * @throws MessagingException 
	 */
	public void sendByTemplate(String to, MailTemplate template, String... args) throws MessagingException;
	
}
