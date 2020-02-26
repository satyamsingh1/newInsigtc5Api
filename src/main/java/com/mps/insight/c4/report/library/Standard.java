package com.mps.insight.c4.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.global.MyLogger;

public class Standard {

	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String tableName = "StandardsUsageByMonth";
	private String query = "";
	private String montList = "";
	
	private String accountCode;
	private String from;
	private String to; 
	
	private String queryHeader ="Journal,`Publisher`,`Platform`,`JournalDOI` AS `Journal DOI`,`ProprietaryIdentifier` AS `Proprietary Identifier`,`PrintISSN` AS `Print ISSN`,`OnlineISSN`";
	private String totalQueryHeader ="'Total for all Journal' AS Journal,'' AS `Publisher`,'' AS `Platform`,''  AS `Journal DOI`,'' AS `Proprietary Identifier`,'' AS `Print ISSN`,'' AS `OnlineISSN`";
	
	

	public Standard(String accountCode, String report, String from, String to) {
		this.accountCode = accountCode;
		this.from = from;
		this.to = to;
		run();
	}

	public void run() {
		includeMonth();
		generatStandard();
	}
	 
	public void generatStandard() {
		StringBuilder stb = new StringBuilder();
		try {
			stb.append("SELECT ");
			stb.append(" " + totalQueryHeader + ",");
			stb.append(" SUM(" + getSumHTML(montList)+"+"+ getSumPDF(montList) + ") AS 'Reporting_Period_Total',");
			
			stb.append(" SUM(" + getSumHTML(montList) + " ) AS 'Reporting Period HTML',");
			stb.append(" SUM(" + getSumPDF(montList) + " ) AS 'Reporting Period PDF',");
			stb.append(" " + getMonthAliasSum(montList) + "");
			stb.append(" from " + tableName + " where");
			stb.append(" Institution='" + accountCode + "' ");
			
			stb.append(" UNION ALL ");
			
			stb.append("SELECT ");
			stb.append(" " + queryHeader + ",");
			stb.append(" (" + getMonthHtml(montList)+ "+"+getMonthPdf(montList) + " ) AS 'Reporting_Period_Total',");
			stb.append(" (" + getMonthHtml(montList) + " ) AS 'Reporting Period HTML',");
			stb.append(" (" + getMonthPdf(montList) + " ) AS 'Reporting Period PDF',");
			stb.append(" " + getMonthAlias(montList) + "");
			stb.append(" from " + tableName + " where");
			stb.append(" Institution='" + accountCode + "' ");
			
			
		} catch (Exception e) {
			MyLogger.error("Standard : generatStandard : Unable to create query " + e.toString());
		}
		this.query = stb.toString();
	}

	
	private void includeMonth() {
		try {
			
			montList = dmc.getMonthListNew("Standard",  from, to);
			
		} catch (Exception e) {
			MyLogger.error("Standard : unable to add month in query" + e.toString());
		}
	}
	
	private String getMonthAlias(String montList){
		
		String[] months = montList.split(",");
		StringBuilder sb = new StringBuilder();
		//(Jan_2018_html+Jan_2018_pdf) AS `Jan-2018`
		for (String string : months) {
			sb.append("("+string+"_html")
			.append("+")
			.append(""+string+"_pdf)")
			.append(" AS "+"`"+string+"`"+",");
		}
		//remove last + symbol
		return sb.toString().substring(0, sb.length()-1);
		
	}
	private String getMonthPdf(String montList){
		
		String[] months = montList.split(",");
		StringBuilder sb = new StringBuilder();
		
		for (String string : months) {
			sb.append(string+"_pdf"); 
			sb.append("+");
			//.append("+"); May_2018_html  Mar_2018_html  Feb_2018_html
		}
		//remove last + symbol
		return sb.toString().substring(0, sb.length()-1);
		
	}
	private String getMonthHtml(String montList){
		
		String[] months = montList.split(",");
		StringBuilder sb = new StringBuilder();
		
		for (String string : months) {
			sb.append(string+"_html"); 
			sb.append("+");
			//.append("+"); May_2018_html  Mar_2018_html  Feb_2018_html
		}
		//remove last + symbol
		return sb.toString().substring(0, sb.length()-1);
		
	}
	
	/*private String getMonthSum(String montList){
		
		String[] months = montList.split(",");
		StringBuilder sb = new StringBuilder();
		
		for (String string : months) {
			sb.append("SUM("+string+")")
			.append("+");
		}
		//remove last + symbol
		return sb.toString().substring(0, sb.length()-1);
		
	}*/
	private String getSumHTML(String montList){
		StringBuilder sb = new StringBuilder();
		String html = getMonthPlus(montList);
		String[] splittedHtml = html.trim().split("\\+");
		
		for (String string : splittedHtml) {
			sb.append(""+string+"_html")
			.append("+");
		}
		
		return sb.substring(0,sb.lastIndexOf("+"));
		
	}
	
	private String getSumPDF(String montList){
		StringBuilder sb = new StringBuilder();
		String pdf = getMonthPlus(montList);
		String[] splittedpdf = pdf.trim().split("\\+");
		for (String string : splittedpdf) {
			sb.append(""+string+"_pdf");
			sb.append("+");
		}
		
		return sb.substring(0,sb.lastIndexOf("+"));
		
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
	

	private String getMonthAliasSum(String montList){
		
		String[] months = montList.split(",");
		StringBuilder sb = new StringBuilder();
		  //(SUM(Jan_2018_html)+SUM(Jan_2018_pdf)) AS `Jan-2018`
		for (String string : months) {
			sb.append("(SUM("+string+"_html)")
			.append("+")
			.append("SUM("+string+"_pdf))")
			.append(" AS "+"`"+string+"`"+",");
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
