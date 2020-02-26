package com.mps.insight.dto;

public class CommonDTO {
	private String updatedBy;
	private int webmartID;
	private String dataKey;
	private String dataValue;
	
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public int getWebmartID() {
		return webmartID;
	}
	public void setWebmartID(int webmartID) {
		this.webmartID = webmartID;
	}
	public String getDataKey() {
		return dataKey;
	}
	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}
	public String getDataValue() {
		return dataValue;
	}
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

}
