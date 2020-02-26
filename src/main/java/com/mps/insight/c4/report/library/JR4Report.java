package com.mps.insight.c4.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.global.MyLogger;

public class JR4Report {

	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String tableName = "JournalReport4";
	private String query = "";
	private String montList = "";
	
	private String accountCode;
	private String from;
	private String to; 
	
	private String queryHeader ="'Total for all Collection' AS Collection,`SearchesRunReport`";
	
	public JR4Report(String accountCode, String report, String from, String to) {
		this.accountCode = accountCode;
		this.from = from;
		this.to = to;
		run();
	}

	public void run() {
		includeMonth();
		generatJR4Report();
	}

	public void generatJR4Report() {
		StringBuilder stb = new StringBuilder();
		try {
			stb.append("SELECT ");
			stb.append(" " + queryHeader + ",");
			stb.append(" (" + getMonthSum(montList) + " ) AS `Reporting_Period_Total`,");
			stb.append(" " + getMonthAliasSum(montList) + "");

			stb.append(" from " + tableName + " where");
			stb.append(" Institution='" + accountCode + "' ");
			stb.append(" GROUP BY Collection,`SearchesRunReport`");
			
		} catch (Exception e) {
			MyLogger.error("JR4Report : generatDB1Report : Unable to create query " + e.toString());
		}
		this.query = stb.toString();
	}

	
	private void includeMonth() {
		try {
			
			montList = dmc.getMonthListNew("JR4",  from, to);
			
		} catch (Exception e) {
			MyLogger.error("JR4Report : unable to add month in query" + e.toString());
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
	/*
	private String getMonthPlus(String montList){
		
		String[] months = montList.split(",");
		StringBuilder sb = new StringBuilder();
		
		for (String string : months) {
			sb.append(string+"+");
			
		}
		//remove last + symbol
		return sb.toString().substring(0, sb.length()-1);
		
	}*/
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	
}
