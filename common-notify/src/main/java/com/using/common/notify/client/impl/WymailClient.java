package com.using.common.notify.client.impl;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.using.common.notify.client.IMailClient;
import com.using.common.notify.enums.MailTemplate;

@Service
public class WymailClient implements IMailClient {

	private final static Logger LOG = LoggerFactory.getLogger(WymailClient.class);

	private Properties props = new Properties();
	private Session session;

	private final static String HOST = "smtp.qiye.163.com";
	private final static String USER = "liumohan@youzhengkeji.com";
	private final static String PASSWORD = "504984316Ll";

	@PostConstruct
	private void init() {

		// 优化到配置文件中
		props.put("mail.smtp.auth", "true"); // 使用验证
		props.put("mail.host", HOST);
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.debug", "true");
		session = Session.getDefaultInstance(props);
		session.setDebug(true);
	}

	@Override
	public void sendMail(String to, String title, String content) throws MessagingException {

		LOG.info("WY client will send mail ...");
		long th = System.currentTimeMillis();

		try {
			Transport transport = session.getTransport();
			transport.connect(HOST, USER, PASSWORD);

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(USER));
			message.setSubject(title);
			message.setText(content);
			transport.sendMessage(message, new Address[] { new InternetAddress(to) });
			long eth = System.currentTimeMillis();
			LOG.info("{} send mail to {} success used {} ms", USER, to, eth - th);

		} catch (MessagingException e) {
			throw e;
		}
	}

	@Override
	public void sendByTemplate(String to,MailTemplate template, String... args) throws MessagingException {
		String text = getTemplateId(template);
		String title = template.getTitle();
		
		for(int i = 0; i < args.length; i++) {
			text = text.replaceFirst("%P%", args[i]);
		}
		sendMail(to, title, text);
	}

	private String getTemplateId(MailTemplate template) {
		switch(template) {
		case VERIFY_CODE : return "您的验证码是：%P%,3分钟内有效。如非您本人操作，可忽略本消息【有征科技】";
		}
		return null;
	}
}
