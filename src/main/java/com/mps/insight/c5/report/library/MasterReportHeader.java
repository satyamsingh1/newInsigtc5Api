package com.mps.insight.c5.report.library;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.mps.insight.dto.AccountDTO;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.MyLogger;
import com.mps.insight.product.Account;

public class MasterReportHeader {
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private StringBuilder reportAttribute = new StringBuilder();
	private Counter5DTO dto=null;
	private String previewType="";
	private RequestMetaData rmd=null;
	
	public MasterReportHeader(Counter5DTO dto,String previewType,RequestMetaData rmd){
		this.dto = dto;
		this.previewType =previewType;
		this.rmd = rmd;
	}
	
	
	public HashMap<String, String> getHeaderTemplate(){
		
		HashMap<String, String> headerMap = new LinkedHashMap<>(); 
		 headerMap.put("Report_Name", dto.getReportDescription());
		 headerMap.put("Report_ID", dto.getReportCode());
		 headerMap.put("Release", "5");
		 headerMap.put("Institution_Name", getInstitutionName());
		 headerMap.put("Institution_ID", dto.getPublisher()+":"+dto.getAccountID());
		 headerMap.put("Metric_Types", dto.getMetricType());
		
		 headerMap.put("Report_Filters", dto.getReportFilters());
		 headerMap.put("Report_Attributes", dto.getReportAttributes());
		 headerMap.put("Exceptions", dto.getException());
		 headerMap.put("Reporting_Period", getReportingPeriod());
		 headerMap.put("Created", formatter.format(new Date()));
		 headerMap.put("Created_By", "MPS");
		
		 
		 return headerMap;      
	}
	
	
	
	public void generateHeader(){
		reportAttribute = new StringBuilder();
		try {
			reportAttribute.append("Attributes_To_Show=Data_Type|Access_Method");
			//Data_Type=Article|Book|Database|Journal|Multimedia|Newspaper_or_Newsletter|Other|Report|Standard|Thesis_or_Dissertation;Access_Type=Controlled|OA_Gold|Other_Free_To_Read;Access_Method=Regular|TDM
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	public String getHeader(){
		return null;
		
	}
	
	public String getInstitutionName(){
		String institutionName = "";
		try {
			Account account = new Account(rmd);
			AccountDTO adto = account.getAccountByCode(dto.getAccountID(), dto.getWebmartID());
			institutionName = String.valueOf(adto.getAccountName());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return institutionName;
		
	}
	
	public String getReportingPeriod(){
		String reportingPeriod = "";
		try {
			String[] fromdatet = dto.getFromDate().split("-");
			String[] todatet = dto.getToDate().split("-");
			
			String day = "";
			
			switch (todatet[0]) {
			case "02":
				if(Integer.parseInt(todatet[1])%4==0){
					day = "29";
				}else{
					day = "28";
				}
				
				break;
			case "04":
				day = "30";
				break;
			case "06":
				day = "30";
				break;
			case "09":
				day = "30";
				break;
			case "11":
				day = "30";
				break;
				
			default:
				day = "31";
			}
			
			
			reportingPeriod = "Begin_Date="+fromdatet[1] + "-" + fromdatet[0] + "-01; End_Date=" + todatet[1] + "-" + todatet[0] + "-"
					+ day;
			
			
		} catch (Exception e) {
			MyLogger.error("Unable to create Reporting period "+e.toString());
		}
		return reportingPeriod;
	}
	
}

