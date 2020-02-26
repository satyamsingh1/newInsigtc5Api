package com.mps.insight.c5.report.publisher;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class TopInternalSearchRun {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private RequestMetaData rmd=null; 
	//private String coloumTitle = "TYPE AS '',";

	public TopInternalSearchRun(Counter5DTO c5Dto, RequestMetaData rmd) {

		try {
			this.rmd = rmd;
			this.dto = c5Dto;
		    c5dao = new Counter5ReportsDao(rmd);
		} catch (Exception e) {

		}
	}

	public MyDataTable getReport(String download) {
		MyDataTable mdt = null;
		String query = "";
		try {

			query = this.getQuery(download);
			mdt = c5dao.getDynamicReportJson(dto.getWebmartID(), query);
			return mdt;
		} catch (Exception e) {
			throw e;
		}
	}

	private String getQuery(String download) {
		StringBuilder yearIn=new StringBuilder();
		StringBuilder monthIn=new StringBuilder();
		String[] toarr = dto.getToDate().split("-");
		String[] fromarr = dto.getFromDate().split("-");
		
		int toyear = Integer.parseInt(toarr[1]);
		int tomonth = Integer.parseInt(toarr[0]);
		int fromyear = Integer.parseInt(fromarr[1]);
		int frommonth = Integer.parseInt(fromarr[0]);
		
		for (int i = fromyear; i <= toyear; i++) {
			yearIn.append("'"+i+"',");
		}
		for (int i = frommonth; i <= tomonth; i++) {
			monthIn.append("'"+i+"',");
		}
		
		String year = yearIn.substring(0, yearIn.lastIndexOf(","));
		String month = monthIn.substring(0, monthIn.lastIndexOf(","));
		
		
		StringBuilder query=new StringBuilder();
		String tableName = "c5_top_internal_search_term"; //from config/hardcode/tabvle/xtz/etc
		try {
			query.append("SELECT @a:=@a+1 AS 'Sr.no', b.* FROM(SELECT `count` AS 'Number_of_Searches_Run', ")
			.append("search_term AS 'Search_Terms',`year`, `month` FROM `"+tableName+"` WHERE YEAR IN ("+year+") AND MONTH IN ("+month+") AND search_term !='-' ")
			.append("GROUP BY Number_of_Searches_Run, Search_Terms, `year`, `month` ORDER BY `count` DESC LIMIT 50) b, (SELECT @a:= 0) AS a ")
			.append("UNION ALL ")
			.append("SELECT '-' AS 'Sr.no', SUM(c.total) AS 'Number_of_Searches_Run', 'Other' AS 'Search_Terms', c.`year`, c.`month` ")
			.append("FROM (SELECT `count` AS 'total', search_term AS 'Search_Terms', `year`, `month` FROM "+tableName+" ")
			.append("WHERE YEAR IN ("+year+") AND MONTH IN ("+month+") AND search_term !='-' ORDER BY `count` DESC LIMIT 50,100000000) c group by c.year, c.month ")
			.append("UNION ALL ")
			.append("SELECT '-' AS 'Sr.no', SUM(`count`) AS 'Number_of_Searches_Run', 'Total' AS 'Search_Terms', `year`, `month` ")
			.append("FROM "+tableName+" WHERE YEAR IN ("+year+") AND MONTH IN ("+month+") AND search_term !='-'");
			
			if(download.equalsIgnoreCase("preview")){
				query =  new StringBuilder();
				query.append("SELECT `count` AS 'Number_of_Searches_Run', ")
				.append("search_term AS 'Search_Terms', `year`, `month` ")
				.append("FROM "+tableName+" ")
				.append("WHERE `year` IN ("+year+") AND `month` IN ("+month+") ")
				.append("AND search_term !='-' ")
				.append("GROUP BY Number_of_Searches_Run, Search_Terms, `year`, `month` ")
				.append("ORDER BY `count` DESC LIMIT 500");
			}else{
				query.append("");
			}
			return query.toString();
		} catch (Exception e) {
			//
			throw e;
		}
	}

}
