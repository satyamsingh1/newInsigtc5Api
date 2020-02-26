package com.mps.insight.c4.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.global.MyLogger;

public class BR2Report {

	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String tableName = "BookReport2";
	private String query = "";
	private String montList = "";
	
	private String accountCode;
	private String from;
	private String to; 
	
	private String queryHeader ="`Title`,`Publisher`,`Platform`,`BookDoi` AS `Book DOI`,`ProprietaryIdentifier` AS `Proprietary Identifier`,`ISBN`,`ISSN`";
	private String totalQueryHeader ="'Total for all Title'AS Title, '' AS Publisher, '' AS Platform ,'' AS BookDOI, '' AS `Proprietary Identifier`, '' AS ISBN,  '' AS ISSN";
	


	public BR2Report(String accountCode, String report, String from, String to) {
		this.accountCode = accountCode;
		this.from = from;
		this.to = to;
		run();
	}

	public void run() {
		includeMonth();
		generatBR2Report();
	}

	public void generatBR2Report() {
		StringBuilder stb = new StringBuilder();
		try {
			stb.append("SELECT ");
			stb.append(" " + totalQueryHeader + ",");
			stb.append(" (" + getMonthSum(montList) + " ) AS `Reporting_Period_Total`,");
			stb.append(" " + getMonthAliasSum(montList) + "");
			stb.append(" from " + tableName + " where");
			stb.append(" Institution='" + accountCode + "' ");
			
			stb.append(" UNION ALL ");
			
			stb.append("SELECT ");
			stb.append(" " + queryHeader + ",");
			stb.append(" (" + getMonthSum(montList) + " ) AS `Reporting_Period_Total`,");
			stb.append(" " + getMonthAliasSum(montList) + "");
			stb.append(" from " + tableName + " where");
			stb.append(" Institution='" + accountCode + "' ");
			stb.append(" GROUP BY `Title`, `Publisher`,`Platform`,`BookDoi`,`ProprietaryIdentifier`,`ISBN`,`ISSN`");
			
		} catch (Exception e) {
			MyLogger.error("BR2Report : generatBR2Report : Unable to create query " + e.toString());
		}
		this.query = stb.toString();
	}

	
	private void includeMonth() {
		try {
			
			montList = dmc.getMonthListNew("BR2",  from, to);
			
		} catch (Exception e) {
			MyLogger.error("BR2Report : unable to add month in query" + e.toString());
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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
