package com.mps.insight.c5.report.library;

import java.net.URLDecoder;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class DR {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String tableName = TableMapper.TABALE.get("dr_master_table");
	
	private String metricTypeFilter="";
	private String accessTypeFilter="";
	private String accessMethodFilter="";
	private String dataTypeFilter="";
	private String showAttributeFilter="";
	
	private String query = "";
	private String totalMonth = "";
	private String monthQuery = "";
	private String previewType="";
	public Counter5DTO dto;

	public DR(Counter5DTO dto,String previewType,RequestMetaData rmd) {
		try {
			this.dto = dto;
			this.previewType = previewType;
			this.rmd=rmd;
			run();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void run() throws Exception {
		includeMonth();
		getReportFilters();
		generatDRReport();
		setHeaderParmeter();
	}

	private void setHeaderParmeter() {
		try {
			String chAccessMethodAll = accessMethodFilter.substring(accessMethodFilter.lastIndexOf("(")+1, accessMethodFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(",", "|").trim();
			String defaultMtricType = metricTypeFilter.substring(metricTypeFilter.lastIndexOf("(")+1, metricTypeFilter.lastIndexOf(")")).replaceAll("'","").replaceAll(" ,","; ");
			//String defaultDataType = "Book|Database|Journal|Multimedia|Newspaper_or_Newsletter|Other|Report|Thesis_or_Dissertation";
			String defaultDataType=dataTypeFilter.substring(dataTypeFilter.lastIndexOf("(")+1, dataTypeFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(" ,", "|").trim();
			String defaultAccessType = "Access_Type=Controlled|OA_Gold|Other_Free_To_Read";
	       //String defaultAccessType = accessTypeFilter.substring(accessTypeFilter.lastIndexOf("(")+1, accessTypeFilter.lastIndexOf(" )")).replaceAll("'", "").replaceAll(",", "|").trim();
			
			String defaultAccessMethod = "Access_Method=Regular|TDM";
			String[] chmatric=defaultMtricType.split(";");
			 String[] chDataType=defaultDataType.split("\\|");
			 String[] chAccessMethod=chAccessMethodAll.split("\\|");
			if(dto.getMetricType()==null||chmatric.length==11)
			{
				defaultMtricType="";
			}
			else
			{
				 defaultMtricType = metricTypeFilter.substring(metricTypeFilter.lastIndexOf("(")+1, metricTypeFilter.lastIndexOf(")")).replaceAll("'","").replaceAll(" ,","; ").trim();
				
			}
			if(dataTypeFilter.contains("'-',' '")||chDataType.length==8){
				defaultDataType="";
				//defaultDataType = "Data_Type=Book|Database|Journal|Multimedia|Newspaper_or_Newsletter|Other|Report|Standard|Thesis_or_Dissertation";
			}else{
				defaultDataType = "Data_Type="+dataTypeFilter.substring(dataTypeFilter.lastIndexOf("(")+1, dataTypeFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(" ,", "|").trim();
			}
			
			if(accessTypeFilter.contains("'-',' '")){
				defaultAccessType = "";				
			}else{
				defaultAccessType = "Access_Type="+accessTypeFilter.substring(accessTypeFilter.lastIndexOf("(")+1, accessTypeFilter.lastIndexOf(" )")).replaceAll("'", "").replaceAll(",", "|").trim();
			}
				
			if(accessMethodFilter.contains("'-',' '")|| chAccessMethod.length==2){
				
				defaultAccessMethod="";
				//defaultAccessMethod = "Access_Method=Regular|TDM";
			}else{
				defaultAccessMethod = "Access_Method="+accessMethodFilter.substring(accessMethodFilter.lastIndexOf("(")+1, accessMethodFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(",", "|").trim();
			}
			
			dto.setDataType(defaultDataType);
			dto.setReportDescription("Database Master Report");
			dto.setMetricType(defaultMtricType);
			dto.setAccessMethod(defaultAccessMethod);
			if(defaultDataType=="")
			{
			dto.setReportFilters(defaultDataType+""+defaultAccessType+""+defaultAccessMethod);
			}else
			{
				dto.setReportFilters(defaultDataType+"; "+defaultAccessType+""+defaultAccessMethod);
			}
			dto.setReportAttributes("Attributes_To_Show=Data_Type|Access_Method");
			dto.setException("");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	private void getReportFilters() throws Exception {
		String metricType = dto.getMetricType();
		String accessType = dto.getAccessType();
		String accessMethod = dto.getAccessMethod();
		String dataType = dto.getDataType();
		String showAttribute = dto.getReportAttributes();
		
		try{metricType =  URLDecoder.decode(metricType, "UTF-8");}catch(Exception e){}
		try{accessType =  URLDecoder.decode(accessType, "UTF-8");}catch(Exception e){}
		try{accessMethod =  URLDecoder.decode(accessMethod, "UTF-8");}catch(Exception e){}
		try{dataType =  URLDecoder.decode(dataType, "UTF-8");}catch(Exception e){}
		
		//Map metric type 
		try {
			StringBuilder metricTypeIn=new StringBuilder();
			if(metricType == null||metricType.equalsIgnoreCase("null") 
					|| metricType.equalsIgnoreCase("") || metricType.equalsIgnoreCase(" ")){
				rmd.log("No METRIC TYPE Selected..");
				metricTypeFilter="metric_type IN("+InsightConstant.DR_DEFAULT_METRIC_TYPE+")";
			}else if(metricType.contains(",")){
				String[] metricTypeInclude = metricType.split(",");
				for (int i = 0; i < metricTypeInclude.length; i++) {
					metricTypeIn.append("'"+metricTypeInclude[i]+"' ,");
				}
				metricTypeFilter = "metric_type IN("+metricTypeIn.substring(0, metricTypeIn.lastIndexOf(","))+")";
			}else{
				metricTypeFilter = "metric_type IN('"+metricType+"')";
			}
		} catch (Exception e) {
			rmd.exception("Unable to Map Metric type for Report : "+e.toString());
		}
		
		
		//Map access type 
		try {
			StringBuilder accessTypeIn=new StringBuilder();
			if(accessType == null||accessType.equalsIgnoreCase("null") 
					|| accessType.equalsIgnoreCase("") || accessType.equalsIgnoreCase(" ")){
				rmd.log("No ACCESS TYPE Selected..");
				accessTypeFilter="access_type Not IN('-',' ')";
			} else if(accessType.contains(",")){
				String[] accessTypeInclude = accessType.split(",");
				for (int i = 0; i < accessTypeInclude.length; i++) {
					accessTypeIn.append("'"+accessTypeInclude[i]+"' ,");
				}
				accessTypeFilter = "access_type IN("+accessTypeIn.substring(0, accessTypeIn.lastIndexOf(","))+")";
			}else{
				accessTypeFilter = "access_type IN('"+accessType+"')";
			}
		} catch (Exception e) {
			rmd.exception("Unable to Map Access type for Report : "+e.toString());
		}
		
		//Map access Method 
		try {
			StringBuilder accessMethodIn=new StringBuilder();
			if(accessMethod == null||accessMethod.equalsIgnoreCase("null") 
					|| accessMethod.equalsIgnoreCase("") || accessMethod.equalsIgnoreCase(" ")){
				rmd.log("No ACCESS METHOD Selected..");
				accessMethodFilter="Access_Method Not IN('-',' ')";
			} else if(accessMethod.contains(",")){
				String[] accessMethodInclude = accessMethod.split(",");
				for (int i = 0; i < accessMethodInclude.length; i++) {
					accessMethodIn.append("'"+accessMethodInclude[i]+"' ,");
				}
				accessMethodFilter = "Access_Method IN("+accessMethodIn.substring(0, accessMethodIn.lastIndexOf(","))+")";
			}else{
				accessMethodFilter = "Access_Method IN('"+accessMethod+"')";
			}
			
		} catch (Exception e) {
			rmd.exception("Unable to Map Access Method for Report : "+e.toString());
		}
		
		//Map data Type 
		try {
			StringBuilder dataTypeIn=new StringBuilder();
			if(dataType == null||dataType.equalsIgnoreCase("null") 
					|| dataType.equalsIgnoreCase("") || dataType.equalsIgnoreCase(" ")){
				rmd.log("No DATA TYPE Selected..");
				dataTypeFilter="data_type Not IN('-',' ')";
			} else if(dataType.contains(",")){
				String[] dataTypeInclude = dataType.split(",");
				for (int i = 0; i < dataTypeInclude.length; i++) {
					dataTypeIn.append("'"+dataTypeInclude[i]+"' ,");
				}
				dataTypeFilter = "data_type IN("+dataTypeIn.substring(0, dataTypeIn.lastIndexOf(" ,"))+")";
			}else{
				dataTypeFilter = "data_type IN('"+dataType+"')";
			}
			
		} catch (Exception e) {
			rmd.exception("Unable to Map Data Type for Report : "+e.toString());
		}
		
		
		
		//Map DR Title for Show Attributes 
		try {
			String drTitle = InsightConstant.DR_MASTER_TITLE;
			StringBuilder showAttributeIn=new StringBuilder();
			if(showAttribute == null||showAttribute.equalsIgnoreCase("null")){
				rmd.log("No SHOW ATTRIBUTES Selected..");
				showAttributeFilter = drTitle + ", CONCAT('"+dto.getPublisher()+"',':',`Title_ID`) AS `Proprietary_ID` ,"+InsightConstant.DR_OPTIONAL_TITLE;
				
				//CONCAT('"+dto.getPublisher()+"',':',`Title_ID`) AS `Proprietary_ID`,
			} else if(showAttribute.contains(",")){
				String[] showAttributeInInclude = showAttribute.split(",");
				for (int i = 0; i < showAttributeInInclude.length; i++) {
					String showAttributeI = showAttributeInInclude[i];
					
					showAttributeIn.append("`"+showAttributeI+"`, ");
				}
				showAttributeFilter = drTitle + ", "+showAttributeIn.toString();				
			}else{
				showAttributeFilter = drTitle + ", ";
			}
			
			showAttributeFilter = showAttributeFilter + "`Metric_Type`, ";
			
		} catch (Exception e) {
			throw new  Exception("Unable to create TR master Report Title : "+e.toString());
			
		}
		
	}

	
	public String generatDRReport() {
		StringBuilder query = new StringBuilder();
		try {
			query.append("SELECT ");
			
			query .append(" "+showAttributeFilter+" ");
			query.append(" SUM"+totalMonth+"AS `Reporting_Period_Total`,");
			query.append(" "+monthQuery+" ");
			query.append(" from "+tableName+" where");
			query.append(" Institution_ID='"+dto.getInstitutionID()+"' ");
			query.append(" AND "+InsightConstant.DR_WHERE_CONDITION);
			query.append(" AND "+metricTypeFilter+" AND "+accessTypeFilter
					+" AND "+accessMethodFilter+" AND "+dataTypeFilter);
			query.append(" and "+totalMonth+" > 0 ");
			query.append("GROUP BY `Database`,`Publisher`,`Publisher_ID`,`Platform`, Title_ID ,`Data_Type`,`Access_Method`,`Metric_Type`" );
			query.append(" ORDER BY `Database` ");
			
			if(previewType.equalsIgnoreCase("preview")){
				query.append(" limit 500 ");
			}else{
				
			}
		
		} catch (Exception e) {
			rmd.exception("DR : generatDRReport : Unable to create query "+e.toString());
		}
		return this.query = query.toString();
	}

	public void includeMonth() {
		try {
			String[] fromarr = dto.getFromDate().split("-");
			String[] toarr = dto.getToDate().split("-");

			// for create M_201805 AS may
			monthQuery = dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
			monthQuery = monthQuery.substring(0, monthQuery.lastIndexOf(","));
			
			totalMonth = dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");
		} catch (Exception e) {
			rmd.exception("DR : includeMonth : Unable to add months in query "+e.toString());
		}
	}

	
	public String getQuery() {
		rmd.log(query.toString());
		return query;
	}
}
