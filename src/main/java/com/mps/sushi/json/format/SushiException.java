package com.mps.sushi.json.format;

public class SushiException {
	private String code;
	private String severity;
	private String message;
	private String helpUrl;
	private String data;
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getSeverity() {
		return severity;
	}
	
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getHelpUrl() {
		return helpUrl;
	}
	
	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	 
	 
}
