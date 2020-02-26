/*########################################################################
*Owner: Kapil SIngh Verma
*Initiated Date:  2017-06-22
*last Modified: 2017-06-22
*
*CR Details: CR20170623-0: 2017-06-23 : Kapil SIngh Verma : New Class
*
*
*########################################################################*/

package com.mps.insight.rest.service;

import java.io.File;
import java.util.HashMap;

public class Report {

	private String webmartCode = "";
	private String reportName = "";
	private String reportCode = "";
	private String institutionCode = "";
	private String institutionName = "";
	private String year = "";
	private String month = "";
	private HashMap<String, String> parameterMap = new HashMap<String, String>();
	
	private String filePath = "";
	private String basePath = "";

	public Report(String webmartCode, String year, String month, String institutionCode, String reportName){
		this.webmartCode = webmartCode;
		this.year = year;
		this.month = month;
		this.institutionCode = institutionCode;
		this.reportName = reportName;
		this.basePath = "/data6/reports";
	}
	
	public String getBasePath() {
		return this.basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public String getFilePath() {
		this.filePath = this.basePath + File.separator + this.webmartCode + File.separator + this.year + File.separator + this.month + File.separator + this.institutionCode.trim() + File.separator + this.reportName;
		return filePath;
	}
	
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getReportCode() {
		return reportCode;
	}
	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}
	public String getInstitutionCode() {
		return institutionCode;
	}
	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}
	public String getInsitutionName() {
		return institutionName;
	}
	public void setInsitutionName(String insitutionName) {
		this.institutionName = insitutionName;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public HashMap<String, String> getParameterMap() {
		return parameterMap;
	}
	public void setParameterMap(HashMap<String, String> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
	//custom method to add parameter in parameter Hashmap
	public boolean addParameter(String key, String value) throws Exception{
		
		try {
			//null key check
			if(key == null){throw new Exception("addParameter : NULL key");}
			//blank key check
			if(key.trim().equalsIgnoreCase("")){throw new Exception("addParameter : BLANK key");}
			
			this.parameterMap.put(key.trim().toUpperCase(), value);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	//custom method to get parameter from parameter Hashmap
	public String getParameter(String key) throws Exception{
		try {
			//null key check
			if(key == null){throw new Exception("getParameter : NULL key");}
			//blank key check
			if(key.trim().equalsIgnoreCase("")){throw new Exception("getParameter : BLANK key");}
			
			return this.parameterMap.get(key.trim().toUpperCase());
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	@Override
	public String toString() {
		return "Report [reportName=" + reportName + ", reportCode=" + reportCode + ", institutionCode="
				+ institutionCode + ", insitutionName=" + institutionName + ", year=" + year + ", month=" + month
				+ ", parameterMap=" + parameterMap.toString() + "]";
	}
	
	
	
	
}
