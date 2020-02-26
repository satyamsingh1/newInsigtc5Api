package com.mps.insight.product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
//import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.TableMapper;


public class Reports {
	
	private RequestMetaData rmd; 
	public Reports(RequestMetaData rmd) {
		this.rmd = rmd;
	}

	//private static final Logger LOG = LoggerFactory.getLogger(Reports.class);
	String jsonData = "";
	ResponseBuilder rb = null;
	MyDataTable mdt = null;
	JsonObject jsonRecords = null;
	String yearcondition=" AND gr.year=";
	String monthcondition=" AND gr.month=";
	PublisherSettings pubsetting=null;
	
	public List<String> getReportSection(int webmartID) {
		List<String> reportSection = new ArrayList<>();
		InsightDAO insightDao = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT rs.section AS section FROM report_sections AS rs WHERE rs.webmart_id = " + webmartID
					+ " AND rs.category = 'PUBLISHER_REPORTS' ORDER BY rs.order");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rowCount=mdt.getRowCount();
			for (int i = 1; i <= rowCount; i++) {
				rmd.log(mdt.getValue(i, "section"));
				reportSection.add(mdt.getValue(i, "section"));
			}
			rmd.log(reportSection.toString());
		} catch (Exception e) {
			rmd.log("Exception in getReportSection... ");
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}

		return reportSection;
	}

	public JsonObject getPubReports(int webmartID,int month,int year) {
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		JsonObject tempObject=null;
		String sectionList="";
		String fileName="fileName";
		List<String> section=getReportSection(webmartID);
		for (String reportType : section) {
			sectionList=sectionList+"'"+reportType+"',";
		}
		
		rmd.log(sectionList+" after change : "+sectionList.substring(sectionList.lastIndexOf(",")));
		rmd.log(" after change 0: "+sectionList.substring(0,sectionList.lastIndexOf(",")));
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("getPubReports : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT rm.category AS Category,rm.name AS ReportType,rm.description AS Description,")
			.append("rm.frequency AS frequency,rm.id AS reportID,rm.file_name AS fileName FROM ")
			.append("publisher_reports pr LEFT JOIN reports_master rm ON pr.report_id=rm.id ")
			.append("LEFT JOIN generated_reports gr ON pr.report_id=gr.report_id WHERE ")
			.append("pr.category IN ("+sectionList.substring(0,sectionList.lastIndexOf(","))+") AND ")
			.append("pr.webmart_id="+webmartID+yearcondition+year+monthcondition+month+" AND gr.status='LIVE' ")
			.append("GROUP BY pr.report_id ORDER BY FIELD(rm.category,"+sectionList.substring(0,sectionList.lastIndexOf(","))+") ASC");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rowcount=mdt.getRowCount();
			for(int i=1;i<=rowcount;i++){
				if(mdt.getValue(i, fileName).contains("&")){
					String str=mdt.getValue(i, fileName).replace("&", "111");
					mdt.updateData(i, fileName, str);
				}
			}
			tempObject=mdt.getJson();
		} catch (Exception e) {
			rmd.log("Exception in getPubReports ");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return tempObject;
	}
	
	public JsonObject getPubReportsTemp(int webmartID,int month,int year) {
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		JsonObject tempObject=null;
		String sectionList="";
		List<String> section=getReportSection(webmartID);
		for (String reportType : section) {
			sectionList=sectionList+"'"+reportType+"',";
		}
		
		rmd.log(sectionList+" after change : "+sectionList.substring(sectionList.lastIndexOf(",")));
		rmd.log(" after change 0: "+sectionList.substring(0,sectionList.lastIndexOf(",")));
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("getLiveMonth : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT DISTINCT rm.category AS Category,rm.name AS `Report Type`,rm.description AS Description,")
			.append("rm.frequency AS frequency,gr.report_id AS reportID,rm.file_name AS fileName FROM ")
			.append("`generated_reports` gr LEFT JOIN reports_master rm ON gr.report_id=rm.id WHERE rm.category IN ("+sectionList.substring(0,sectionList.lastIndexOf(","))+")")
			.append(" AND gr.webmart_id="+webmartID+yearcondition+year+monthcondition+month+" AND gr.status='LIVE' ORDER BY FIELD(rm.category,"+sectionList.substring(0,sectionList.lastIndexOf(","))+") ASC");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			tempObject=mdt.getJson();
			
		} catch (Exception e) {
			rmd.log("Exception in getPubReportsTemp");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return tempObject;
	}

	public JsonObject getPubReportYears(int webmartID) {
		JsonArrayBuilder yearArray=Json.createArrayBuilder();
		JsonObject jobject=null;
		InsightDAO insightDao = null;
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			String sectionList="";
			List<String> section=getReportSection(webmartID);
			for (String reportType : section) {
				sectionList=sectionList+"'"+reportType+"',";
			}
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT DISTINCT gr.year AS YEAR FROM `generated_reports` gr LEFT JOIN reports_master rm ON gr.report_id=rm.id ")
			.append("WHERE gr.webmart_id="+webmartID+" AND gr.status='LIVE' AND rm.category IN")
			.append("("+sectionList.substring(0,sectionList.lastIndexOf(","))+") ORDER BY gr.year DESC");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rowCount=mdt.getRowCount();
			for (int i = 1; i <= rowCount; i++) {
				yearArray.add(mdt.getValue(i, "year"));
			}
			jobject=mdt.getJson();
			
		} catch (Exception e) {
			rmd.log("Exception in getPubReportYears");
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return jobject;
	}
	
	public JsonArray getReportLocation(int webmartID,int reportID,String frequency,int year) {
		InsightDAO insightDao = null;
		JsonArray jarr=null;
		String fileName="";
		StringBuilder stb =null;
		String maxMonth="";
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			stb.append("SELECT file_name as fileName FROM `reports_master` WHERE id="+reportID+" AND frequency='"+frequency+"'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			fileName=mdt.getValue(1, 1);
			if("DAILY".equalsIgnoreCase(frequency)){
			stb = new StringBuilder();
			stb.append("SELECT MAX(MONTH) as month FROM generated_reports WHERE YEAR="+year+" AND webmart_id="+webmartID+" AND report_id="+reportID+" AND STATUS='LIVE'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			maxMonth=mdt.getValue(1, 1);
			mdt=null;
			}
			if("DAILY".equalsIgnoreCase(frequency)){
				stb = new StringBuilder();
				stb.append("SELECT DISTINCT(DAY) AS DAY ,'"+maxMonth+"' AS MONTH,'"+year+"' AS YEAR,'"+fileName+"' AS filename ")
				.append("FROM generated_reports WHERE webmart_id='"+webmartID+"' AND YEAR='"+year+"' AND report_id='"+reportID+"' AND ")
				.append("MONTH='"+maxMonth+"' AND STATUS='LIVE' ORDER BY DAY ASC");
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
			}else if("MONTHLY".equalsIgnoreCase(frequency)){
				stb = new StringBuilder();
				stb.append("SELECT DISTINCT(MONTH) AS MONTH ,'00' AS DAY,'"+year+"' AS YEAR,'"+fileName+"' AS file_name ")
				.append("FROM generated_reports WHERE webmart_id='"+webmartID+"' AND YEAR='"+year+"' AND report_id='"+reportID+"' AND STATUS='LIVE' ORDER BY MONTH ASC");
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
			}
			jarr=mdt.getJsonKeyValue();
			
		} catch (Exception e) {
			rmd.log("Exception in getReportLocation ...");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return jarr;
	}
	
	public JsonArray getJournalReportATOZ(int webmartID,int reportID,int year,int month) {
		JsonObjectBuilder jobj=Json.createObjectBuilder();
		JsonArrayBuilder jar=Json.createArrayBuilder();
		InsightDAO insightDao = null;
		int hashcheck=0;
		String [] a2z=new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N",
				"O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("getJournalReport : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT DISTINCT SUBSTRING(fj.TITLE,1,1) FROM  "+TableMapper.TABALE.get("c5_title_feed_master")+" fj LEFT JOIN ")
			.append("generated_reports gr ON gr.journal_id=fj.TITLE_ID WHERE gr.webmart_id="+webmartID+" AND gr.status='LIVE'")
			.append(yearcondition+year+monthcondition+month+" AND gr.report_id='"+reportID+"' GROUP BY fj.TITLE");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rowCount=mdt.getRowCount();
			if(rowCount>0){
			jobj.add("key", "All");
			jobj.add("value", true);
			jar.add(jobj);
			}else{
				jobj.add("key", "All");
				jobj.add("value", false);
				jar.add(jobj);
			}
			for (String alpha : a2z) {
				for (int i = 1; i <=a2z.length ; i++) {
					if(rowCount>=i){
					if(mdt.getValue(i, 1).equals(alpha)){
						jobj.add("key", alpha);
						jobj.add("value", true);
						break;
					}else{
						jobj.add("key", alpha);
						jobj.add("value", false);
					}
					if(mdt.getValue(i, 1).startsWith("1") || mdt.getValue(i, 1).startsWith("2") || mdt.getValue(i, 1).startsWith("3") || mdt.getValue(i, 1).startsWith("4") || 
							mdt.getValue(i, 1).startsWith("5") || mdt.getValue(i, 1).startsWith("6") || mdt.getValue(i, 1).startsWith("7") || mdt.getValue(i, 1).startsWith("8")){
						hashcheck=2;
					}
					}else{
						jobj.add("key", alpha);
						jobj.add("value", false);
					}
					
				}
				jar.add(jobj);
				
			}
			if(hashcheck!=0){
				jobj.add("key", "#");
				jobj.add("value", true);
				jar.add(jobj);	
			}else{
				jobj.add("key", "#");
				jobj.add("value", false);
				jar.add(jobj);
			}
		} catch (Exception e) {
			rmd.log("Exception in getJournalReportATOZ : "+e.getMessage());
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return jar.build();
	}
	
	public JsonObject searchJournalReport(int webmartID,int reportID,int year,int month,int setNO,String jid,String title) {
		JsonObject jobj=null;
		InsightDAO insightDao = null;
		String tempQuery="";
		if(null==jid){
			jid="";
		}
		if(null==title){
			title="";
		}
		if(jid.length()>1 && title.length()>1){
		tempQuery="j.TITLE_ID LIKE '%"+jid+"%' AND j.TITLE LIKE '%"+title+"%'";
		}else if(jid.length()>1){
			tempQuery="j.TITLE_ID LIKE '"+jid+"'";
		}else{
			tempQuery="j.TITLE LIKE '"+title+"'";
		}
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT rm.file_name AS fileName, gs.journal_id AS `JournalID`,")
			.append("j.TITLE AS JournalTitle, js.month AS `month`, js.year AS `year`") 
			.append(" FROM generated_reports AS gs LEFT JOIN "+TableMapper.TABALE.get("c5_title_feed_master")+" AS j ON ")
			.append("j.TITLE_ID=gs.journal_id LEFT JOIN reports_master rm ON ")
			.append("gs.report_id=rm.id WHERE "+tempQuery)
			.append(" AND gs.webmart_id='"+webmartID+"'")
			.append(" AND  gs.report_id='"+reportID+"' AND gs.status='LIVE' AND ")
			.append("gs.year ='"+year+"' AND gs.month="+month+" ORDER BY j.TITLE");
			mdt = insightDao.executeSelectQueryMDT(stb.toString()); 
			jobj=mdt.getJson();
			rmd.log(jobj.toString());
		} catch (Exception e) {
			rmd.log("Exception in searchJournalReport "+e.getMessage());
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return jobj;
	}
	
	public JsonObject searchJournalReportByAlphabet(int webmartID,int reportID,int year,int month,int setNO,String alphabet) {
		JsonObject jobj=null;
		InsightDAO insightDao = null;
		if(null==alphabet || alphabet.equalsIgnoreCase("All")){
			alphabet="";	
		}
		String tempquery="";
		if(null!=alphabet && alphabet.equalsIgnoreCase("#")){
			tempquery="(j.journal_title LIKE '1%' OR j.journal_title "
					+ "LIKE '2%' OR j.journal_title LIKE '3%' OR j.journal_title "
					+ "LIKE '4%' OR j.journal_title LIKE '5%' OR j.journal_title "
					+ "LIKE '6%' OR j.journal_title LIKE '7%' OR j.journal_title "
					+ "LIKE '8%' OR j.journal_title LIKE '9%')";	
		}else{
			tempquery="j.journal_title LIKE '"+alphabet+"%'";	
		}
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("getJournalReport : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT rm.file_name AS fileName,gs.journal_id AS `JournalID`,")
			.append("j.journal_title AS JournalTitle, j.month AS `month`,j.year AS `year` ")
			.append("FROM generated_reports AS gs LEFT JOIN feed_journals AS j ON ")
			.append("j.journal_id=gs.journal_id LEFT JOIN reports_master rm ON ")
			.append("gs.report_id=rm.id WHERE "+tempquery+" AND j.webmart_id ='")
			.append(webmartID+"' AND gs.webmart_id='"+webmartID)
			.append("'  AND  gs.report_id="+reportID+" AND gs.status='LIVE' AND gs.year ='"+year)
			.append("' AND gs.month="+month+" and j.month="+month+" and j.year="+year+" ORDER BY j.journal_title");
			mdt = insightDao.executeSelectQueryMDT(stb.toString()); 
			jobj=mdt.getJson();
			rmd.log(jobj.toString());
		} catch (Exception e) {
			rmd.log("Exception in searchJournalReportByAlphabet : "+e.getMessage());
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return jobj;
	}
	
	public MyDataTable getReportDetail(String report,int webmartID){
		List<String> reportSection = new ArrayList<>();
		InsightDAO insightDao = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("getLiveMonth : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT r.name AS `name`,r.abbrev AS `code`,r.description AS `description`,r.category as category ")
			.append("FROM reports_master r WHERE r.abbrev='"+report+"' AND r.category IN ('COUNTER','ADDITIONAL')");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			rmd.log(reportSection.toString());
		} catch (Exception e) {
			rmd.log("Exception in getReportDetail... "+e.getMessage());
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return mdt;
	}
	
	//change by satyam
	//change by Gyana on 21/02/2019
	public  JsonArray getBookDetail(int webmartID,String datatype) throws Exception{
		JsonObjectBuilder jobj=Json.createObjectBuilder();
		JsonArrayBuilder jar=Json.createArrayBuilder();
		InsightDAO insightDao = null;
		String [] a2z=new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N",
				"O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
		try {
			pubsetting=new PublisherSettings(rmd);
			if("journal".equalsIgnoreCase(datatype)){
				if(webmartID==701)
				{
					datatype = " IN ('journal','magazines', 'transactions','letters','PROCEEDINGS') ";
				}
				else
				{
					datatype = " IN ('journal','magazines', 'transactions','letters') ";
				}
				datatype = " IN ('journal','magazines', 'transactions','letters') ";
			}else if("book".equalsIgnoreCase(datatype)){
				datatype = " IN ('Book','PROCEEDINGS') ";
			}else{
				datatype = "= '"+datatype+"' ";
			}
			
			insightDao =rmd.getInsightDao();
			rmd.log("getLiveMonth : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT  (SUBSTR(`TITLE`,1,1)) AS Alphabet, `TITLE_ID` AS `Key`,`TITLE` AS `Value` FROM `"+TableMapper.TABALE.get("c5_title_feed_master")+"` WHERE `DATA_TYPE` "+datatype+" ORDER BY Alphabet ");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rowCount=mdt.getRowCount();
			int hashcheck = 0;
			
			Set<String> alphabetSet=new HashSet<>();
			for (int rowIndex = 1; rowIndex <=rowCount; rowIndex++) {
				String alphabet = mdt.getValue(rowIndex, "Alphabet");
				alphabetSet.add(alphabet.toUpperCase());
			}
			
			if(!alphabetSet.isEmpty()){
				jobj.add("key", "All");
				jobj.add("value", true);
				jar.add(jobj);
			}
			
			
			for (String alpha : a2z) {
				if(alphabetSet.contains(alpha)){
					jobj.add("key", alpha);
					jobj.add("value", true);
				}else{
					jobj.add("key", alpha);
					jobj.add("value", false);
				}
				
				jar.add(jobj);
			}
			
		/*	
			if(rowCount>0){
				jobj.add("key", "All");
				jobj.add("value", true);
				jar.add(jobj);
				}else{
					jobj.add("key", "All");
					jobj.add("value", false);
					jar.add(jobj);
				}	
			for (String alpha : a2z) {
				for (int i = 1; i <=a2z.length ; i++) {
					if(rowCount>=i){
					if(mdt.getValue(i, 1).equals(alpha)){
						jobj.add("key", alpha);
						jobj.add("value", true);
						break;
					}else{
						jobj.add("key", alpha);
						jobj.add("value", false);
					}
					if(mdt.getValue(i, 1).startsWith("1") || mdt.getValue(i, 1).startsWith("2") || mdt.getValue(i, 1).startsWith("3") || mdt.getValue(i, 1).startsWith("4") || 
							mdt.getValue(i, 1).startsWith("5") || mdt.getValue(i, 1).startsWith("6") || mdt.getValue(i, 1).startsWith("7") || mdt.getValue(i, 1).startsWith("8")){
						hashcheck=2;
					}
					}else{
						jobj.add("key", alpha);
						jobj.add("value", false);
					}
					
				}
				jar.add(jobj);
				
			}*/
			
		/*	if(hashcheck!=0){
				jobj.add("key", "#");
				jobj.add("value", true);
				jar.add(jobj);	
			}else{
				jobj.add("key", "#");
				jobj.add("value", false);
				jar.add(jobj);
			}*/
		} catch (Exception e) {
			rmd.log("Exception in getJournalReportATOZ : "+e.getMessage());
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return jar.build();
	}
	public  JsonObject getpublisherbookreportforalphabet(int webmartID,String alphabet,String datatype) throws Exception{
		List<String> reportSection = new ArrayList<>();
		InsightDAO insightDao = null;
		JsonObject jobj=null;
		
		try {
			pubsetting=new PublisherSettings(rmd);
			
			if("journal".equalsIgnoreCase(datatype)){
				if(webmartID==701)
				{
					datatype = " IN ('journal','magazines', 'transactions','letters','PROCEEDINGS') ";
				}
				else
				{
					datatype = " IN ('journal','magazines', 'transactions','letters') ";
				}
				
			}else if("book".equalsIgnoreCase(datatype)){
				datatype = " IN ('Book','PROCEEDINGS') ";
			}else{
				datatype = "= '"+datatype+"' ";
			}
			
			insightDao =rmd.getInsightDao();
			rmd.log("getLiveMonth : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			
			if(alphabet.equalsIgnoreCase("All")){
				stb.append(" SELECT DISTINCT `TITLE_ID` AS `Key`,`TITLE` AS `Value` FROM `"+TableMapper.TABALE.get("c5_title_feed_master")+"` WHERE `DATA_TYPE` "+datatype+" AND TITLE_ID LIKE '%%' ");
			}
			else if(alphabet.equalsIgnoreCase("#")){
				stb.append(" SELECT DISTINCT `TITLE_ID` AS `Key`,`TITLE` AS `Value` FROM "+TableMapper.TABALE.get("c5_title_feed_master")+"` WHERE `DATA_TYPE` "+datatype+"  AND (UPPER(TITLE_ID) NOT LIKE 'A%')"
						+" AND  (UPPER(TITLE_ID) NOT LIKE 'B%') AND (UPPER(TITLE_ID) NOT LIKE 'C%') AND (UPPER(TITLE_ID) NOT LIKE 'D%') AND (UPPER(TITLE_ID) NOT LIKE 'E%')"
						+" AND (UPPER(TITLE_ID) NOT LIKE 'F%') AND (UPPER(TITLE_ID) NOT LIKE 'G%') AND (UPPER(TITLE_ID) NOT LIKE 'H%') AND (UPPER(TITLE_ID) NOT LIKE 'I%')"
						+" AND (UPPER(TITLE_ID) NOT LIKE 'J%') AND (UPPER(TITLE_ID) NOT LIKE 'K%') AND (UPPER(TITLE_ID) NOT LIKE 'L%') AND (UPPER(TITLE_ID) NOT LIKE 'M%') "
						+" AND (UPPER(TITLE_ID) NOT LIKE 'N%') AND (UPPER(TITLE_ID) NOT LIKE 'O%') AND (UPPER(TITLE_ID) NOT LIKE 'P%') "
						+" AND (UPPER(TITLE_ID) NOT LIKE 'Q%') AND (UPPER(TITLE_ID) NOT LIKE 'R%') AND (UPPER(TITLE_ID) NOT LIKE 'S%')"
						+" AND (UPPER(TITLE_ID) NOT LIKE 'T%') AND (UPPER(TITLE_ID) NOT LIKE 'U%') AND (UPPER(TITLE_ID) NOT LIKE 'V%')"
						+" AND (UPPER(TITLE_ID) NOT LIKE 'W%') AND (UPPER(TITLE_ID) NOT LIKE 'X%') AND (UPPER(TITLE_ID) NOT LIKE 'Y%')"
						+" AND (UPPER(TITLE_ID) NOT LIKE 'Z%')");
  
			}
			else
			{
				stb.append(" SELECT DISTINCT `TITLE_ID` AS `Key`,`TITLE` AS `Value` FROM `"+TableMapper.TABALE.get("c5_title_feed_master")+"` WHERE `DATA_TYPE` "+datatype+" AND TITLE LIKE '"+alphabet+"%' ");
			}
			
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			rmd.log(reportSection.toString());
		} catch (Exception e) {
			rmd.log("Exception in getReportDetail... "+e.getMessage());
		} finally {
					/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		jobj=mdt.getJson();
		return jobj;
	}

}
