package com.mps.insight.c4.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.global.MyLogger;

public class ConsortiaMember {

	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String tableName = "ArticleRequestsByConsortiaMember";
	private String query = "";
	private String montList = "";
	
	private String accountCode;
	private String from;
	private String to; 
	
	
	public ConsortiaMember(String accountCode, String report, String from, String to) {
		this.accountCode = accountCode;
		this.from = from;
		this.to = to;
		run();
	}

	public void run() {
		includeMonth();
		generatConsortiaMember();
	}

	public void generatConsortiaMember() {
		StringBuilder stb = new StringBuilder();
		try {
			stb.append("SELECT `institutionName`,");
			stb.append("("+getMonthPlus(montList)+") AS `Reporting Period Total`, ");
			stb.append(montList+" ");
			
			stb.append(" from " + tableName + " where");
			stb.append(" Institution='" + accountCode + "' ");
			
			
		} catch (Exception e) {
			MyLogger.error("ConsortiaMember : generatConsortiaMember : Unable to create query " + e.toString());
		}
		this.query = stb.toString();
	}

	
	private void includeMonth() {
		try {
			
			montList = dmc.getMonthListNew("ConsortiaMember",  from, to);
			
		} catch (Exception e) {
			MyLogger.error("ConsortiaMember : unable to add month in query" + e.toString());
		}
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
