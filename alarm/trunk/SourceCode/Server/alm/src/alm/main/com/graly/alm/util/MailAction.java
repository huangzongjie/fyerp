package com.graly.alm.util;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

import com.graly.framework.core.config.Config;
import com.graly.framework.core.config.EMailCfMod;

public class MailAction extends AlarmAction {

	private static final Logger logger = Logger.getLogger(MailAction.class);

	private static String USERNAME = "";   
    private static String PASSWORD = "";   

	public MailAction() {
	}

	public MailAction(String param1, String param2, String param3,
			String param4, String param5) {
		this.param1 = param1;	// from
		this.param2 = param2;	// to
		this.param3 = param3;	// cc
		this.param4 = param4;	// title
		this.param5 = param5;	// content
	} 

	@Override
	public void excute() {
		Transport transport = null;
		try {			
			EMailCfMod cfMod = Config.sharedInstance(EMailCfMod.class.getName()).createConfigModule(EMailCfMod.class);
			USERNAME = cfMod.getSmtpUsername();
			PASSWORD = cfMod.getSmtpPassword();
			Properties props = new Properties();
			props.put("mail.smtp.host", cfMod.getSmtpHost());
			props.put("mail.smtp.auth", cfMod.isAuthenticate());
			props.put("mail.smtp.port", cfMod.getSmtpPort());

			Authenticator auth=new Authenticator() {   
                protected PasswordAuthentication getPasswordAuthentication() {   
                    return  new PasswordAuthentication(USERNAME, PASSWORD);   
                }   
            };   
 			Session session = Session.getDefaultInstance(props, auth);
			
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(this.param1));
			msg.setSentDate(new Date());
			
			String[] tos = this.param2.split(";");
			for (String to : tos) {
				if (to.trim().length() != 0) {
					msg.addRecipients(javax.mail.Message.RecipientType.TO, 	to.trim());
				}
			}
			String[] ccs = this.param3.split(";");
			for (String cc : ccs) {
				if (cc.trim().length() != 0) {
					msg.addRecipients(javax.mail.Message.RecipientType.CC, 	cc.trim());
				}
			}
			
			BASE64Encoder enc=new BASE64Encoder();    
			msg.setSubject("=?GBK?B?" + enc.encode(this.param4.getBytes()) + "?=");   
			msg.setDataHandler(new DataHandler(new ByteArrayDataSource(this.param5, "text/html")));   

            transport = session.getTransport("smtp");   
            transport.connect(cfMod.getSmtpHost(), USERNAME, PASSWORD);   
            transport.sendMessage(msg, msg.getAllRecipients());   

		} catch (Exception e) {
			logger.error("Sending alarm eMail failed.", e);
		} finally{
			try {
				if (transport != null) {
					transport.close();
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}			
		}
	}

}
