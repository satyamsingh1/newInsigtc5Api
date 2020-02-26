package com.mps.insight.impl;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dto.EmailDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;

public class MailSenderManager {
	
	private static final Logger log = LoggerFactory.getLogger(MailSenderManager.class);
	InputStream inputStream;
	String contentType="text/html; charset=\"UTF-8\"";
	
	/* Send mail */
	public void sendMail(EmailDTO emailDTO) throws Exception {

		String host = "";
		String bcc="";
		String to="";
		String cc="";
		
		// Get host, user and passwords from properties file.
		try {
			Properties prop = new Properties();
			String propFileName = "config.properties";
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			// get the property value and print it out
			host = prop.getProperty("mail.host");
			bcc = prop.getProperty("mail.bcc");
			to = prop.getProperty("mail.to");
			cc = prop.getProperty("mail.cc");
		} catch (Exception e) {

		}
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");

		if (emailDTO.getPassword() == null) {
			emailDTO.setPassword("");
		}
		// Get the session object
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailDTO.getUseremail(), emailDTO.getPassword());
			}
		});

		String[] recipients = null;
		String receivers = null;
		if (emailDTO.getSendEmailCopy() == true && emailDTO.getReciever() != null) {
			emailDTO.setReciever(getQueryReceiver(emailDTO.getWebmartId()) + "," + emailDTO.getReciever());
		} else if (emailDTO.getReciever() == null && emailDTO.getSendEmailCopy() == false) {
			emailDTO.setReciever(getQueryReceiver(emailDTO.getWebmartId()));
		}
		receivers = emailDTO.getReciever();
		receivers=receivers+","+to;
		recipients = receivers.replaceAll(";", ",").split(",");
		
		
		try {
			MimeMessage message = new MimeMessage(session);
			message.setContent(emailDTO.getMessage(), contentType);
			message.setFrom(new InternetAddress("support@mpsinsight.com"));
			message.setSubject(emailDTO.getSubject());

			InternetAddress[] toAddresses;
			
			
			if (recipients.length > 0) {
				toAddresses = new InternetAddress[recipients.length];
				for (int i = 0; i < recipients.length; i++) {
					toAddresses[i] = new InternetAddress(recipients[i].trim());
				}
				message.setRecipients(Message.RecipientType.TO, toAddresses);
			
			} else {
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receivers));
			}
			
			if(bcc!=null && !bcc.equalsIgnoreCase("")){
				message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
			}
			if(cc!=null && !cc.equalsIgnoreCase("")){
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
			}
			
			
			
			if (emailDTO != null && emailDTO.getSubject() != null) {
				message.setSubject(emailDTO.getSubject());
			}
			message.setText(emailDTO.getMessage());
			message.setSentDate(new Date());

			// send mail
			Transport.send(message);

			log.info("Mail sent");

		} catch (MessagingException e) {
			log.error("Exception in sendMail send : " + e);
		}
		finally{
			if(null!=inputStream){
				inputStream.close();
			}
		}
	}

	/* Send mail with attachmens */
	public void sendMailWithAttachment(EmailDTO emailDTO) throws Exception {

		String host = "";
		String bcc="";
		String to="";
		String cc="";
		// Get host, user and passwords from properties file.
		try {
			Properties prop = new Properties();
			String propFileName = "config.properties";
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}

			// get the property value and print it out
			host = prop.getProperty("mail.host");
			bcc = prop.getProperty("mail.bcc");
			to = prop.getProperty("mail.to");
			cc = prop.getProperty("mail.cc");
		} catch (Exception e) {
			log.error("Exception in sendMailWithAttachment : "+e.getMessage());
		}
		// Get the session object
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		if (emailDTO.getPassword() == null) {
			emailDTO.setPassword("");
		}
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailDTO.getUseremail(), emailDTO.getPassword());
			}
		});

		String receivers = emailDTO.getReciever();
		receivers=receivers+","+to;
		String[] recipients = receivers.replace(";", ",").split(",");
		//recipients[recipients.length];

		try {
			MimeMessage message = new MimeMessage(session);
			message.setContent(emailDTO.getMessage(), contentType);
			message.setFrom(new InternetAddress(emailDTO.getUseremail()));
			InternetAddress[] toAddresses;
			if (recipients.length > 0) {
				toAddresses = new InternetAddress[recipients.length];
				for (int i = 0; i < recipients.length; i++) {
					toAddresses[i] = new InternetAddress(recipients[i].trim());
				}
				message.setRecipients(Message.RecipientType.TO, toAddresses);
			} else {
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receivers));
			}
			
			if(bcc!=null && !bcc.equalsIgnoreCase("")){
				message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
			}
			if(cc!=null && !cc.equalsIgnoreCase("")){
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
			}
			
			
			// message.set
			message.setSubject(emailDTO.getSubject());
			message.setText(emailDTO.getMessage(), contentType);
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(emailDTO.getMessage());

			// Add attachments
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			if (emailDTO.getAttachment() != null) {
				for (BodyPart attachedFile : emailDTO.getAttachment()) {
					multipart.addBodyPart(attachedFile);
				}
			}
			message.setContent(multipart);
			// send mail
			Transport.send(message);
			log.info("Mail sent");

		} catch (MessagingException e) {
			log.error("Exception in sendMailWithAttachment " + e);
		}
		finally{
			if(null!=inputStream){
				inputStream.close();
			}
		}
	}

	private String getQueryReceiver(int webmartId) throws Exception {
		String webmartCode = new RequestMetaData().getPublisherNameFromRedis(webmartId);
		
		String receiverId = null;

		if (webmartCode.equalsIgnoreCase("ieee")) {
			receiverId = InsightConstant.IEEE_SUPPORT_EMAILID;
		}
		if (webmartCode.equalsIgnoreCase("iopscience") || webmartCode.equalsIgnoreCase("iop")) {
			receiverId = InsightConstant.IOPSCIENCE_SUPPORT_EMAILID;
		}
		if (webmartCode.equalsIgnoreCase("rsc")) {
			receiverId = InsightConstant.RSC_SUPPORT_EMAILID;
		}
		
		if (webmartCode.equalsIgnoreCase("rcni")) {
			receiverId = InsightConstant.RCNI_SUPPORT_EMAILID;
		}

		return receiverId;

	}
}
