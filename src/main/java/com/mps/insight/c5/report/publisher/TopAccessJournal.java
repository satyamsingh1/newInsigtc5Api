package com.mps.insight.c5.report.publisher;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class TopAccessJournal {
	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private RequestMetaData rmd=null;

	public TopAccessJournal(Counter5DTO c5Dto, RequestMetaData rmd) {

		try {

			this.dto = c5Dto;
		    c5dao = new Counter5ReportsDao(rmd);
		    this.rmd=rmd;
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
		StringBuilder query=new StringBuilder();
		String tableName = "c5_top_full_text_article_access_by_journal"; //from config/hardcode/tabvle/xtz/etc
		try {
			
			String currentMonth="";
			String currentYear="";
			
			if(dto.getToDate()!=null){
				String toDate = dto.getToDate();
				String[] dates = toDate.split("-");
				currentMonth = dates[0];
				currentYear = dates[1];
			}else{
				currentMonth=rmd.getLiveMonth();
				currentYear=rmd.getLiveYear();
			}
			
			
			String journalId=dto.getBookJournalName();
			query.append("SELECT @a:=@a+1 AS 'Sr.no', ")
			.append("b.*  FROM ( ")
			.append("SELECT Title AS 'Journal', doi AS 'DOI', ")
			.append("item_title AS 'Title', volume AS 'Volume', ")
			.append("issue AS 'Issue',  page AS 'Page', ")
			.append("M_"+currentYear+""+currentMonth+" AS 'Total Successful Full Text Article Requests' ")
			.append("FROM `"+tableName+"` ")
			.append("WHERE title_id='"+journalId+"' AND M_"+currentYear+""+currentMonth+">0 ")
			.append("ORDER BY M_"+currentYear+""+currentMonth+" DESC LIMIT 50 ")
			.append(")b , (SELECT @a:=0) AS a ")
			.append("UNION ALL ")
			.append("SELECT 'Other' AS 'Sr.no',  ")
			.append("'' AS 'Journal', '' AS 'DOI', ")
			.append("'' AS 'Title', '' AS 'Volume', ")
			.append("'' AS 'Issue',  '' AS 'Page', ")
			.append("SUM(a.total) AS 'Total Successful Full Text Article Requests' ")
			.append("FROM ( ")
			.append("SELECT Title AS 'Journal', doi AS 'DOI', ")
			.append("item_title AS 'Title', volume AS 'Volume', ")
			.append("issue AS 'Issue',  page AS 'Page', ")
			.append("M_"+currentYear+""+currentMonth+" AS 'total' ")
			.append("FROM `"+tableName+"` ")
			.append("WHERE title_id='"+journalId+"' AND M_"+currentYear+""+currentMonth+">0 ")
			.append("ORDER BY M_"+currentYear+""+currentMonth+" DESC LIMIT 50, 1000000) a")
			.append(" UNION ALL ")
			.append("SELECT 'Total' AS 'Sr.no',  ")
			.append("'' AS 'Journal', '' AS 'DOI', '' AS 'Title', ")
			.append("'' AS 'Volume', '' AS 'Issue',  '' AS 'Page', ")
			.append("SUM(M_"+currentYear+""+currentMonth+") AS 'Total Successful Full Text Article Requests' ")
			.append("FROM `"+tableName+"` ")
			.append("WHERE title_id='"+journalId+"' AND M_"+currentYear+""+currentMonth+">0 ");
					
			
			if(download.equalsIgnoreCase("preview")){
				query=new StringBuilder();
				query.append("SELECT Title AS 'Jouranl', doi AS 'DOI', item_title AS 'Title', ")
				.append("volume AS 'Volume', issue AS 'Issue',  page AS 'Page', ")
				.append("M_"+currentYear+""+currentMonth+" AS 'Total Successful Full Text Article Requests' ")
				.append("FROM `"+tableName+"` ")
				.append("WHERE title_id='"+journalId+"' AND M_"+currentYear+""+currentMonth+">0 ORDER BY M_"+currentYear+""+currentMonth+" DESC LIMIT 500");
			
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
