package com.mps.insight.dto;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.mail.BodyPart;

public class EmailDTO {

	private String username="";                   
    private String useremail="";
	private String firstName="";
	private String lastName="";
	private String token="";
	private int webmartId=-2;
	private String reciever="";
	private String sender="";
	private Date date;
	private String Subject="";
	private String Message="";
	private List<File> filesUrl;
	private List<BodyPart> attachment;
	private String password="";
	private boolean sendEmailCopy;
	
	public String getSubject() {
		return Subject;
	}
	public void setSubject(String subject) {
		Subject = subject;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUseremail() {
		return useremail;
	}
	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}
	public String getReciever() {
		return reciever;
	}
	public void setReciever(String reciever) {
		this.reciever = reciever;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getWebmartId() {
		return webmartId;
	}
	public void setWebmartId(int webmartId) {
		this.webmartId = webmartId;
	}
	public List<File> getFilesUrl() {
		return filesUrl;
	}
	public void setFilesUrl(List<File> filesUrl) {
		this.filesUrl = filesUrl;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean getSendEmailCopy() {
		return sendEmailCopy;
	}
	public void setSendEmailCopy(boolean sendEmailCopy) {
		this.sendEmailCopy = sendEmailCopy;
	}
	public List<BodyPart> getAttachment() {
		return attachment;
	}
	public void setAttachment(List<BodyPart> attachment) {
		this.attachment = attachment;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmailDTO [username=").append(username).append(", useremail=").append(useremail)
				.append(", firstName=").append(firstName).append(", lastName=").append(lastName).append(", token=")
				.append(token).append(", webmartId=").append(webmartId).append(", reciever=").append(reciever)
				.append(", sender=").append(sender).append(", date=").append(date).append(", Subject=").append(Subject)
				.append(", Message=").append(Message).append(", filesUrl=").append(filesUrl).append(", attachment=")
				.append(attachment).append(", password=").append(password).append(", sendEmailCopy=")
				.append(sendEmailCopy).append("]");
		return builder.toString();
	}
	

}
