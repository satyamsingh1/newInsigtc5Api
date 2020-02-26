package com.mps.insight.c4.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.global.MyLogger;

public class JR1GOAReport {

	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String tableName = "JournalReport1GOA";
	private String query = "";
	private String montList = "";
	
	private String accountCode;
	private String from;
	private String to; 
	
	private String queryHeader ="'Total for all Journal' AS Journal,'' AS `Publisher`,'' AS `Platform`,'' AS `Journal DOI`,'' AS `Proprietary Identifier`,'' AS `PrintISSN`,'' AS  `Online ISSN`";
	
	public JR1GOAReport(String accountCode, String report, String from, String to) {
		this.accountCode = accountCode;
		this.from = from;
		this.to = to;
		run();
	}

	public void run() {
		includeMonth();
		generatJR1GOAReport();
	}

	public void generatJR1GOAReport() {
		StringBuilder stb = new StringBuilder();
		try {
			stb.append("SELECT");
			stb.append(" " + queryHeader + ",");
			
			stb.append(" (" + getSumHTML(montList)+"+"+ getSumPDF(montList) + " ) AS `Reporting_Period_Total`,");
			
			stb.append(" " + getReportingPeriodSumHTML(montList) + "  AS `Reporting Period HTML`,");
			stb.append(" " + getReportingPeriodSumPDF(montList) + "  AS `Reporting Period PDF`,");
			stb.append(" " + getMonthAliasSum(montList) + "");

			stb.append(" from " + tableName + " where");
			stb.append(" Institution='" + accountCode + "' ");
			
			
			stb.append("UNION ALL ");
			stb.append("SELECT Journal,`Publisher`,`Platform`,`JournalDOI` AS `Journal DOI`,`ProprietaryIdentifier` AS `Proprietary Identifier`,`PrintISSN` AS `Print ISSN`,`OnlineISSN` AS `Online ISSN`,");
			stb.append(" (" + getMonthHtml(montList)+ "+"+getMonthPdf(montList) + " ) AS 'Reporting_Period_Total',");
			stb.append(" (" + getMonthHtml(montList) + " ) AS 'Reporting Period HTML',");
			stb.append(" (" + getMonthPdf(montList) + " ) AS 'Reporting Period PDF',");
			stb.append(" " + getMonthAlias(montList) + "");
			
			stb.append("FROM "+tableName+" WHERE institution='"+accountCode+"'");
			
		} catch (Exception e) {
			MyLogger.error("JR1GOAReport : generatJR1Report : Unable to create query " + e.toString());
		}
		this.query = stb.toString();
	}
	
	
	private void includeMonth() {
		try {
			
			montList = dmc.getMonthListNew("JR1GOA",  from, to);
			
		} catch (Exception e) {
			MyLogger.error("JR1GOAReport : unable to add month in query" + e.toString());
		}
	}
	
	/*private String getMonthSum(String montList){
		
		String[] months = montList.split(",");
		StringBuilder sb = new StringBuilder();
		//SUM(Feb_2018_html)+SUM(Feb_2018_pdf)
		//(SUM(May_2018)+SUM(Jun_2018)+SUM(Jul_2018)+SUM(Aug_2018)+SUM(Sep_2018) )
		
		for (String string : months) {
			sb.append("SUM("+string+")")
			.append("+");
		}
		//remove last + symbol
		return sb.toString().substring(0, sb.length()-1);
		
	}*/
	
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
	 
	
	  
	private String getSumHTML(String montList){
		StringBuilder sb = new StringBuilder();
		String html = getMonthPlus(montList);
		String[] splittedHtml = html.trim().split("\\+");
	//	sb.append("SUM");
		for (String string : splittedHtml) {
			sb.append("SUM("+string+"_html)")
			.append("+");
		}
		String sumhtml = sb.substring(0,sb.lastIndexOf("+"));
		
		return sumhtml;
		
	}
	
	  
	private String getReportingPeriodSumHTML(String montList){
		StringBuilder sb = new StringBuilder();
		String html = getMonthPlus(montList);
		String[] splittedHtml = html.trim().split("\\+");
		//sb.append("SUM(");
		for (String string : splittedHtml) {
			sb.append("SUM("+string+"_html)")
			.append("+");
		}
		String rpsh = sb.substring(0,sb.lastIndexOf("+"));
		
		
		return rpsh;
		
	}
	
	
	private String getReportingPeriodSumPDF(String montList){
		StringBuilder sb = new StringBuilder();
		String pdf = getMonthPlus(montList);
		String[] splittedPdf = pdf.trim().split("\\+");
		//sb.append("SUM(");
		for (String string : splittedPdf) {
			sb.append("SUM("+string+"_pdf)")
			.append("+");
		}
		String rpsp = sb.substring(0,sb.lastIndexOf("+"));
	
		
		return rpsp;
		
	}
	
	private String getSumPDF(String montList){
		StringBuilder sb = new StringBuilder();
		String pdf = getMonthPlus(montList);
		String[] splittedpdf = pdf.trim().split("\\+");
		//sb.append("SUM(");
		for (String string : splittedpdf) {
			sb.append("SUM("+string+"_pdf)")
			.append("+");
		}
		String sp =sb.substring(0,sb.lastIndexOf("+"));
		return sp;
		
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
