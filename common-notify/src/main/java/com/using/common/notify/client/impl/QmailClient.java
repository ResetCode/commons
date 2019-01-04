package com.using.common.notify.client.impl;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.using.common.notify.client.IMailClient;
import com.using.common.notify.enums.MailTemplate;

@Service
public class QmailClient implements IMailClient{

	@Override
	public void sendMail(String to, String title, String content) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendByTemplate(String to, MailTemplate template, String... args)
			throws MessagingException {
		// TODO Auto-generated method stub
		
	}

}
