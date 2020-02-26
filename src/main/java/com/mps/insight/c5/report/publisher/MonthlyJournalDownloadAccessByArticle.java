package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class MonthlyJournalDownloadAccessByArticle {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao=null;
	private String coloumTitle="Parent_Title AS 'Title',DOI AS 'DOI',Item AS 'Item',Issue_volume AS 'Volume',Issue_no AS 'Issue',Page_no AS 'Page',";
		
	public MonthlyJournalDownloadAccessByArticle(Counter5DTO c5Dto, RequestMetaData rmd){
		
		try {
			
			this.dto = c5Dto;
		    c5dao = new Counter5ReportsDao(rmd);
		} catch (Exception e) {
			//handle exception
		}
	}

	public MyDataTable getReport(String download) throws Exception{
		MyDataTable mdt=null;
		String query = "";
		try {
			
			query = this.getQuery(download);
			mdt=c5dao.getDynamicReportJson(dto.getWebmartID(), query);
			return mdt;
			
		} catch (Exception e) {
			throw e;
		}
				
	}
	
	private String getQuery(String download) throws Exception{
		
		String[] toarr= null;
		String[] fromarr= null;
		String dynamicmonth = null;
		int toyear=0;
		int fromyear=0;
		int tomonth=0;
		int frommonth=0;
		
		StringBuilder query=new StringBuilder();
		DynamicMonthCreater dmoncreate = null;
		String tableName = "c5_monthly_journal_download_accesses_by_article_title"; //from config/hardcode/tabvle/xtz/etc
		try {
			
			toarr=dto.getToDate().split("-");
			fromarr=dto.getFromDate().split("-");
			toyear=Integer.parseInt(toarr[1]);
			tomonth=Integer.parseInt(toarr[0]);
			fromyear=Integer.parseInt(fromarr[1]);
			frommonth=Integer.parseInt(fromarr[0]);
			String zero="";
			if(tomonth<10){
				zero="0";
			}
			String temp="M_"+toyear+zero+tomonth;
			dmoncreate=new DynamicMonthCreater();
			dynamicmonth=dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			
			query.append("select "+coloumTitle+temp+" as 'Total_Item_Requests','"+tomonth+"' as 'Access Month','"+toyear+"' AS 'Access Year'")
			.append(" from "+tableName+" WHERE "+temp+" >=0");
			
			if(download.equalsIgnoreCase("preview")){
				query.append(" Limit 500");
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
