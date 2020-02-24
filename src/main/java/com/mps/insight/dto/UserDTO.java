package com.mps.insight.dto;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class UserDTO {
	private int id;
	private int publisherID;
	private int webmartID;
	private String userCode;
	private String webmartCode;
	private String emailID="";
	private String firstName="";
	private String lastName="";
	private String password;
	private String lastPassword1;
	private String lastPassword2;
	// use for SHA-1 description with decrypt method
	private String lastPassword3;
	
	private String status="";
	private String userType="";
	private String question="";
	private String answer="";
	private String token="";
	private String role="";
	private String roleID="";
	private String highIP="";
	private String lowIP="";
	private String createdBy="";
	private String updatedBy="";
	private long userId=0;
	private long accountId=0;
	private String description="";
	private String accounts="";
	private String alertName="";
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPublisherID() {
		return publisherID;
	}
	public void setPublisherID(int publisherID) {
		this.publisherID = publisherID;
	}
	public int getWebmartID() {
		return webmartID;
	}
	public void setWebmartID(int webmartID) {
		this.webmartID = webmartID;
	}
	
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getWebmartCode() {
		return webmartCode;
	}
	public void setWebmartCode(String webmartCode) {
		this.webmartCode = webmartCode;
	}
	public String getEmailID() {
		return emailID;
	}
	public void setEmailID(String emailID) {
		this.emailID = emailID;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	/*public String getToken() {
		return token;
	}*/
	public void setToken(String token) {
		this.token = token;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String toJsonString(){
		
		return toJsonObject().toString();
	}
	
	public String getHighIP() {
		return highIP;
	}
	public void setHighIP(String highIP) {
		this.highIP = highIP;
	}
	public String getLowIP() {
		return lowIP;
	}
	public void setLowIP(String lowIP) {
		this.lowIP = lowIP;
	}
	
	public String getRoleID() {
		return roleID;
	}
	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAccounts() {
		return accounts;
	}
	public void setAccounts(String accounts) {
		this.accounts = accounts;
	}
	
	public String getAlertName() {
		return alertName;
	}
	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}
	
	public String getLastPassword1() {
		return lastPassword1;
	}
	public void setLastPassword1(String lastPassword1) {
		this.lastPassword1 = lastPassword1;
	}
	public String getLastPassword2() {
		return lastPassword2;
	}
	public void setLastPassword2(String lastPassword2) {
		this.lastPassword2 = lastPassword2;
	}
	public String getLastPassword3() {
		return lastPassword3;
	}
	public void setLastPassword3(String lastPassword3) {
		this.lastPassword3 = lastPassword3;
	}
	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", publisherID=" + publisherID + ", webmartID=" + webmartID + ", userCode="
				+ userCode + ", webmartCode=" + webmartCode + ", emailID=" + emailID + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", password=" + password + ", status=" + status + ", userType=" + userType
				+ ", question=" + question + ", answer=" + answer + ", token=" + token + ", role=" + role + ", roleID="
				+ roleID + ", highIP=" + highIP + ", lowIP=" + lowIP + ", createdBy=" + createdBy + ", updatedBy="
				+ updatedBy + ", userId=" + userId + ", accountId=" + accountId + ", description=" + description
				+ ", accounts=" + accounts + ", alertName=" + alertName + "]";
	}
	
	
	public JsonObjectBuilder toJsonObject(){
		JsonObjectBuilder userJson=Json.createObjectBuilder().add("iD", id).add("publisherID", publisherID)
				.add("userCode", userCode).add("emailID", emailID).add("firstName", firstName).add("lastName", lastName)
				.add("password", password).add("status", status).add("userType", userType).add("question", question)
				.add("answer", answer).add("publisherCode", webmartCode).add("webmartID", webmartID).add("role", role);
		
		return userJson;
	}
	
	
	/*private String getToken(){
		String token = "";
		Authorization authorization = new Authorization();
		try {
			token = authorization.getBasicAuth(getUserCode(), token);
			token = token + "~#~" + getId() + "~#~" + getWebmartID();
			token = authorization.encryptData(token);
			//
			
			
			return token;
		} catch (Exception e) {
			return "";
		}
	}*/
}
