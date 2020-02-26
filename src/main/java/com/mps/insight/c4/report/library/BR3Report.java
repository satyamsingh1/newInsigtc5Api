package com.mps.insight.c4.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.global.MyLogger;

public class BR3Report {

	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String tableName = "BookReport3";
	private String query = "";
	private String montList = "";
	private String previewType="";
	
	private String accountCode;
	private String from;
	private String to; 
	
	private String queryHeaderTotalLimitExceeded ="CONCAT('Total for all licence limit exceeded') AS Title,'' AS `Publisher`,'' AS `Platform`,'' AS `Book DOI`,'' AS   `Proprietary Identifier`,'' AS `ISBN`,'' AS `ISSN`,'Access denied: concurrent/simultaneous user licence limit exceeded' AS `Access denied category`";
	private String queryHeaderTotalitemNotLicenced ="'Total for all item not licenced' AS Title,'' AS `Publisher`,'' AS `Platform`,'' AS  `Book DOI`,'' AS  `Proprietary Identifier`,'' AS `ISBN`,'' AS `ISSN`,'Access denied: content item not licenced' AS `Access denied category`";
	private String queryHeader ="Title,`Publisher`,`Platform`,`BookDoi` AS `Book DOI`,`ProprietaryIdentifier` AS `Proprietary Identifier`,`ISBN`,`ISSN`,`AccessDeniedCategory` AS `Access denied category`";
	
	public BR3Report(String accountCode, String report, String from, String to) {
		this.previewType = previewType;
		this.accountCode = accountCode;
		this.from = from;
		this.to = to;
		run();
	}

	public void run() {
		includeMonth();
		generatBR3Report();
	}
	
	public void generatBR3Report() {
		StringBuilder stb = new StringBuilder();
		try {
			stb.append("SELECT ");
			stb.append(" " + queryHeaderTotalLimitExceeded + ",");
			stb.append(" (" + getMonthSum(montList) + " ) AS `Reporting_Period_Total`,");
			stb.append(" " + getMonthAliasSum(montList) + "");
			stb.append(" from " + tableName + " where");
			stb.append(" Institution='" + accountCode + "' ");
			stb.append(" AND accessDeniedCategory='Access denied: concurrent/simultaneous user licence limit exceeded' ");
			
			stb.append("UNION ALL ");
			stb.append("SELECT ");
			stb.append(" " + queryHeaderTotalitemNotLicenced + ",");
			stb.append(" (" + getMonthSum(montList) + " ) AS `Reporting_Period_Total`,");
			stb.append(" " + getMonthAliasSum(montList) + "");
			stb.append(" from " + tableName + " where");
			stb.append(" Institution='" + accountCode + "' ");
			stb.append(" AND accessDeniedCategory='Access denied: content item not licenced' ");
			
			stb.append("UNION ALL ");
			
			
			stb.append("SELECT ");
			stb.append(" " + queryHeader + ",");
			stb.append(" (" + getMonthPlus(montList) + " ) AS `Reporting_Period_Total`,");
			stb.append(" " + montList + "");
			stb.append(" from " + tableName + " where");
			stb.append(" Institution='" + accountCode + "' ");
			
			if(previewType.equalsIgnoreCase("preview")){
				stb.append(" limit 500 ");
			}else{
				
			}
			
		} catch (Exception e) {
			MyLogger.error("BR3Report : generatBR3Report : Unable to create query " + e.toString());
		}
		this.query = stb.toString();
	}

	
	private void includeMonth() {
		try {
			
			montList = dmc.getMonthListNew("BR3",  from, to);
			
		} catch (Exception e) {
			MyLogger.error("BR3Report : unable to add month in query" + e.toString());
		}
	}
	
	private String getMonthSum(String montList){
		
		String[] months = montList.split(",");
		StringBuilder sb = new StringBuilder();
		
		for (String string : months) {
			sb.append("SUM("+string+")")
			.append("+");
		}
		//remove last + symbol
		return sb.toString().substring(0, sb.length()-1);
		
	}
	
	private String getMonthAliasSum(String montList){
		
		String[] months = montList.split(",");
		StringBuilder sb = new StringBuilder();
		
		for (String string : months) {
			sb.append("SUM("+string+")")
			.append(" AS "+string+",");
		}
		//remove last + symbol
		return sb.toString().substring(0, sb.length()-1);
		
	}

	private String getMonthPlus(String montList){
		
		String[] months = montList.split(",");
		StringBuilder sb = new StringBuilder();
		
		for (String string : months) {
			sb.append(string+"+");
			
		}
		//remove last + symbol
		return sb.toString().substring(0, sb.length()-1);
		
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
