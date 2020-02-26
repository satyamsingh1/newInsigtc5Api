package com.mps.sushi.json.format;

import java.util.Map;

public class ReportHeader {
	private String created;
	private String createdBy;
	private String customerId;
	private String reportId;
	private String release;
	private String reportName;
	private String institutionName;
	private String startMonth;
	private String endMonth;
	private int startYear;
	private int endYear;	
	private Map<String, String> institutionId;
	private Map<String, String> reportFilters;
	private Map<String, String> reportAttributes;
	private SushiException sushiException;
	public String getCreated() {
		return created;
	}
	
	public void setCreated(String created) {
		this.created = created;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public String getReportId() {
		return reportId;
	}
	
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	
	public String getRelease() {
		return release;
	}
	
	public void setRelease(String release) {
		this.release = release;
	}
	
	public String getReportName() {
		return reportName;
	}
	
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public String getInstitutionName() {
		return institutionName;
	}
	
	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}
	
	public Map<String, String> getInstitutionId() {
		return institutionId;
	}
	
	public void setInstitutionId(Map<String, String> institutionId) {
		this.institutionId = institutionId;
	}
	
	public Map<String, String> getReportFilters() {
		return reportFilters;
	}
	
	public void setReportFilters(Map<String, String> reportFilters) {
		this.reportFilters = reportFilters;
	}
	
	public Map<String, String> getReportAttributes() {
		return reportAttributes;
	}
	
	public void setReportAttributes(Map<String, String> reportAttributes) {
		this.reportAttributes = reportAttributes;
	}
	
	public SushiException getSushiException() {
		return sushiException;
	}
	
	public void setSushiException(SushiException sushiException) {
		this.sushiException = sushiException;
	}
	
	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
	

	
}
