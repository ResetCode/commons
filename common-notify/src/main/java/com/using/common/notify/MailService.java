package com.using.common.notify;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.using.common.util.UUIDUtils;
import com.using.common.notify.client.IMailClient;
import com.using.common.notify.enums.MailTemplate;
import com.using.common.notify.enums.NotifyType;
import com.using.common.notify.model.MailMessage;
import com.using.common.notify.model.MailTemplateRequest;

@Service
public class MailService extends BaseService {

	 private static Logger logger = LoggerFactory.getLogger(MailService.class);

	@Autowired
	@Qualifier("wymailClient")
	private IMailClient client;

	@Autowired(required = false)
	@Value(value = "${mail.send.debug}")
	private String debug = "0";

	/**
	 * @param req
	 *            ip、title、content、destin[to]
	 * @throws MessagingException
	 */
	public void sendMail(MailMessage req) throws MessagingException {

		if (super.notifyPersistence != null) {
			MailMessage message = buildMessage(req.getIp(), req.getTitle(), req.getContent(), req.getDestin());
			super.save(message);
		}

		if (debug != null && debug.equals("0")) {
			client.sendMail(req.getDestin(), req.getTitle(), req.getContent());
		}
	}

	/**
	 * @param request
	 *            ip,template,ext,destin[to]
	 * @throws MessagingException 
	 */
	public void sendByTemplate(MailTemplateRequest request) throws MessagingException {

		String[] strs = null;
		String title = null;
		MailTemplate template = request.getTemplate();
		Map<String, String> ext = request.getExt();
		switch (template) {
		case VERIFY_CODE:
			//验证码","您的验证码是：%P%,3分钟内有效。如非您本人操作，可忽略本消息【有征科技】
			strs = new String[] { ext.get("code") };
			break;
		}

		if (super.notifyPersistence != null) {
			title = template.getTitle();
			String content = String.format(request.getTemplate().getText(), strs);
			MailMessage message = buildMessage(request.getIp(), title, content, request.getDestin());
			super.save(message);
		}
		logger.info("send mail : debug= " + debug + (debug != null && debug.equals("0")) + "to=" + request.getDestin() + "");
		if (debug != null && debug.equals("0")) {
			client.sendByTemplate(request.getDestin(), template, strs);
		}
	}

	private MailMessage buildMessage(String ip, String title, String content, String destin) {
		MailMessage message = new MailMessage();
		message.setId(UUIDUtils.randReplacedLower());
		message.setIp(ip);//
		message.setContent(content);//
		message.setDestin(destin);//
		message.setType(NotifyType.MAIL);
		message.setTitle(title);//
		Map<String, Object> extra = new HashMap<>();
		extra.put("title", title);
		message.setExtra(extra);
		message.setCreateTime(new Date());
		return message;
	}

}
