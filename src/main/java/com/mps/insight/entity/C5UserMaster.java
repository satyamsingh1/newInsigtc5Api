package com.mps.insight.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c5_user_master")
public class C5UserMaster {
	
	private char api_key;
	@Id
	private String user_code;
	private String email_id;
	private String role_id;
	private String first_name;
	private String last_name;
	private String password;
	private String lp1;
	private String lp2;
	private String lp3;
	private Enum status;
	private String user_type;
	private String question;
	private String answer;
	private String email_alert;
	private String sushi;
	private String created_by;
	private String created_at;
	private String updated_by;
	private String updated_at;
	private String last_login;
	private String publisher;
	public String getUser_code() {
		return user_code;
	}
	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	public String getRole_id() {
		return role_id;
	}
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLp1() {
		return lp1;
	}
	public void setLp1(String lp1) {
		this.lp1 = lp1;
	}
	public String getLp2() {
		return lp2;
	}
	public void setLp2(String lp2) {
		this.lp2 = lp2;
	}
	public String getLp3() {
		return lp3;
	}
	public void setLp3(String lp3) {
		this.lp3 = lp3;
	}
//	public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		this.status = status;
//	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
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
	public String getEmail_alert() {
		return email_alert;
	}
	public void setEmail_alert(String email_alert) {
		this.email_alert = email_alert;
	}
	public String getSushi() {
		return sushi;
	}
	public void setSushi(String sushi) {
		this.sushi = sushi;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getLast_login() {
		return last_login;
	}
	public void setLast_login(String last_login) {
		this.last_login = last_login;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
}
