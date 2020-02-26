package com.mps.insight.c5.report.library;

import java.net.URLDecoder;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class PR {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	/*private String tableName1 = "master_report ";*/
	private String tableName2 = "pr_master ";
	private String metricTypeFilter="";
	private String accessMethodFilter="";
	private String dataTypeFilter="";
	private String showAttributeFilter="";
	private String Query="";
	private String monthSumQuery ="";
	private String totalMonth ="";
	private String previewType="";
	
	private Counter5DTO dto;
	
	
	public PR(Counter5DTO dto,String previewType,RequestMetaData rmd){
		try {
			this.dto=dto;
			this.previewType = previewType;
			this.rmd = rmd;
			run();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void run() throws Exception{
		includeMonthParameters();
		getReportFilters();
		generatePRQuery();
		setHeaderParmeter();
	}
	
	private void setHeaderParmeter() {
		try {
			String RangeMatric="Searches_Platform; Total_Item_Investigations; Total_Item_Requests; Unique_Item_Investigations; Unique_Item_Requests; Unique_Title_Investigations; Unique_Title_Requests";
			String defaultMtricType = metricTypeFilter.substring(metricTypeFilter.lastIndexOf("(")+1, metricTypeFilter.lastIndexOf(")")).replaceAll("'","").replaceAll(" ,","; ").trim();
			String chAccessMethodAll = accessMethodFilter.substring(accessMethodFilter.lastIndexOf("(")+1, accessMethodFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(",", "|").trim();
			
			String[] chmatric=defaultMtricType.split(";");
			  String[] chAccessMethod=chAccessMethodAll.split("\\|");
		//	String defaultDataType = "Data_Type=Article|Book|Database|Journal|Multimedia|Newspaper_or_Newsletter|Other|Report";
			  String defaultDataType =  "Data_Type="+dataTypeFilter.substring(dataTypeFilter.lastIndexOf("(")+1, dataTypeFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(" ,", "|").trim();
		      String[] chDataType=defaultDataType.split("\\|");
			  String defaultAccessMethod = "Access_Method=Regular|TDM";
		    
			if(dto.getMetricType()==null||chmatric.length==7)
			{
				defaultMtricType="";
			}
			else
			{
				 defaultMtricType = metricTypeFilter.substring(metricTypeFilter.lastIndexOf("(")+1, metricTypeFilter.lastIndexOf(")")).replaceAll("'","").replaceAll(" ,","; ").trim();
				
			}
			if(dataTypeFilter.contains("'-',' '")||chDataType.length==13){
				defaultDataType="";
			//	defaultDataType = "Data_Type=Article|Book|Database|Journal|Multimedia|Newspaper_or_Newsletter|Other|Report";
			}else{
				defaultDataType = "Data_Type="+dataTypeFilter.substring(dataTypeFilter.lastIndexOf("(")+1, dataTypeFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(" ,", "|").trim();
			}
				
			if(accessMethodFilter.contains("'-',' '")|| chAccessMethod.length==2){
				defaultAccessMethod="";
				//defaultAccessMethod = "Access_Method=Regular|TDM";
			}else{
				defaultAccessMethod = "Access_Method="+accessMethodFilter.substring(accessMethodFilter.lastIndexOf("(")+1, accessMethodFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(",", "|").trim();
			}
			dto.setMetricType(defaultMtricType);
			dto.setReportDescription("Platform Master Report");
			if(defaultDataType=="")
			{
			dto.setReportFilters(defaultDataType+""+defaultAccessMethod);}
			else
			{
				dto.setReportFilters(defaultDataType+"; "+defaultAccessMethod);
			}
			dto.setReportAttributes("Attributes_To_Show=Data_Type|Access_Method");
			dto.setException("");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	private void getReportFilters() throws Exception {
		String metricType = dto.getMetricType();
		
		String accessMethod = dto.getAccessMethod();
		String dataType = dto.getDataType();
		String showAttribute = dto.getReportAttributes();
		
		try{metricType =  URLDecoder.decode(metricType, "UTF-8");}catch(Exception e){}
		try{accessMethod =  URLDecoder.decode(accessMethod, "UTF-8");}catch(Exception e){}
		try{dataType =  URLDecoder.decode(dataType, "UTF-8");}catch(Exception e){}
		
			
		//Map metric type 
		try {
			StringBuilder metricTypeIn=new StringBuilder();
			if(metricType == null||metricType.equalsIgnoreCase("null") 
					|| metricType.equalsIgnoreCase("") || metricType.equalsIgnoreCase(" ")){
				rmd.log("No METRIC TYPE Selected..");
				metricTypeFilter="metric_type IN("+InsightConstant.PR_DEFAULT_METRIC_TYPE+")";
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
		
		
		
		//Map TR Title for Show Attributes 
		try {
			String prTitle = InsightConstant.PR_MASTER_TITLE_1;
			StringBuilder showAttributeIn=new StringBuilder();
			if(showAttribute == null||showAttribute.equalsIgnoreCase("null")){
				rmd.log("No SHOW ATTRIBUTES Selected..");
				showAttributeFilter = prTitle + " "+InsightConstant.PR_OPTIONAL_TITLE+", ";
			} else if(showAttribute.contains(",")){
				String[] showAttributeInInclude = showAttribute.split(",");
				for (int i = 0; i < showAttributeInInclude.length; i++) {
					String showAttributeI = showAttributeInInclude[i];
					
					showAttributeIn.append("`"+showAttributeI+"`, ");
				}
				showAttributeFilter = prTitle + ", "+showAttributeIn.toString();				
			}else{
				showAttributeFilter = prTitle + ", ";
			}
			
			showAttributeFilter = showAttributeFilter + "`Data_Type`,`Access_Method`,`Metric_Type`, ";
			
		} catch (Exception e) {
			throw new  Exception("Unable to create TR master Report Title : "+e.toString());
			
		}
		
	}

	
	private String generatePRQuery(){
		StringBuilder query = new StringBuilder();
		try {
			query.append("SELECT ");
			query .append("CASE WHEN Platform='' THEN 'NA' ELSE platform END AS "+showAttributeFilter+" ");
			query.append(" SUM"+totalMonth+"AS `Reporting_Period_Total`,");
			query.append(" "+monthSumQuery+" ");
			query.append(" from "+TableMapper.TABALE.get("master_report_table")+" where");
			query.append( " "+metricTypeFilter+" AND "+dataTypeFilter+" and ");
			query.append(" Institution_ID='"+dto.getInstitutionID()+"' ");
			query.append(" GROUP BY institution_id, "+showAttributeFilter.substring(0, showAttributeFilter.lastIndexOf(",")));
			query.append(" UNION ALL ");
			query.append(" SELECT ");
			query .append(" CASE WHEN Platform='' THEN 'NA' ELSE platform END AS "+showAttributeFilter+" ");
			query.append(" SUM"+totalMonth+"AS `Reporting_Period_Total`,");
			query.append(" "+monthSumQuery+" ");
			query.append(" from "+tableName2+" where");
			query.append(" Institution_ID='"+dto.getInstitutionID()+"' ");
			query.append(" AND "+metricTypeFilter+"  ");
			query.append(" GROUP BY institution_id, "+showAttributeFilter.substring(0, showAttributeFilter.lastIndexOf(",")));
			if(previewType.equalsIgnoreCase("preview")){
				query.append(" limit 500 ");
			}else{
				
			}
			
		} catch (Exception e) {
			rmd.exception("generatePRQuery : unable to create Query : "+e.toString());
		}
		return this.Query=query.toString();
	}
		private void includeMonthParameters(){
			try {
				String [] fromarr=dto.getFromDate().split("-");
				String [] toarr=dto.getToDate().split("-");
			
				//SUM(M_201801) AS `Jan-2018`,SUM(M_201802) AS `Feb-2018`...........
				monthSumQuery = dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
				monthSumQuery = monthSumQuery.substring(0, monthSumQuery.lastIndexOf(","));
				
				// ((M_201801)+(M_201802)+(M_201803)+(M_201804)+(M_201805)+(M_201806)+(M_201807)+(M_201808)+(M_201809))
				totalMonth= dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");
				
			} catch (Exception e) {
				rmd.exception("includeMonthParameters : unable to generate Month list for query : "+e.toString());
			}
		}
		
		public String getQuery() {
			rmd.log(Query.toString());
			return Query;
		}
	/*
	public static void main(String[] args) throws UnsupportedEncodingException {
		String url ="https://c5live.mpsinsight.com/insightc5api/services/download/counter5reports?"
+"token=Ef5YKxnkUcJUnrX4vQWlks6+GQZ3OIkxAxcQEpTImQQ=&"+
"accountCode=680&"+
"todate=11-2018&"+
"fromdate=01-2018&"+
"report=DR&"+
"metricType=Limit_Exceeded%2CNo_License%2CSearches_Automated%2CSearches_Federated%2CSearches_Regular%2CTotal_Item_Investigations%2CTotal_Item_Requests%2CUnique_Item_Investigations%2CUnique_Item_Requests%2CUnique_Title_Investigations%2CUnique_Title_Requests&"+
"dataType=Article%2CBook%2CDatabase%2CJournal%2CMultimedia%2CNewspaper_or_Newsletter%2COther%2CReport%2CThesis_or_Dissertation&"+
"accessMethod=Regular%2CTDM";

	url = URLDecoder.decode(url, "UTF-8");
	
	String[] ul = url.split(",");

	for (String string : ul) {
		System.out.println(string);
	}
	}*/
}
