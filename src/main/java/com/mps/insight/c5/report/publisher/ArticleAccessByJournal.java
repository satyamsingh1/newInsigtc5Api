package com.mps.insight.c5.report.publisher;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class ArticleAccessByJournal {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null; 
	//private String coloumTitle = "TITLE_TYPE AS ` Total for all Types`,";
	private RequestMetaData rmd = null;

	public ArticleAccessByJournal(Counter5DTO c5Dto, RequestMetaData rmd) {

		try {
			this.rmd = rmd;
			c5dao = new Counter5ReportsDao(this.rmd);
			this.dto = c5Dto;

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
		String[] toarr= null;
		//String[] fromarr= null;
		String title=dto.getTitleID();
		//String dynamicmonth = null;
		//String dynamicmonthsum = null;
		String title1="Journal,DOI,Title,Volume,Issue,Page,";
		String title2="title AS 'Journal', DOI AS 'DOI', item_title AS 'Title', volume AS 'Volume', issue AS 'Issue', page AS 'Page',";
		String title3="'"+title+"' AS Journal, '' AS DOI, '' AS Title, '' AS Volume, '' AS Issue, '' AS Page,";
		int toyear=0;
		//int fromyear=0;
		int tomonth=0;
		//int frommonth=0;
		
		StringBuilder query=new StringBuilder();
		//DynamicMonthCreater dmoncreate = null;
		String tableName = "c5_top_full_text_article_access_by_journal"; //from config/hardcode/tabvle/xtz/etc
		try {
			
			toarr=dto.getToDate().split("-");
			//fromarr=dto.getFromDate().split("-");
			toyear=Integer.parseInt(toarr[1]);
			tomonth=Integer.parseInt(toarr[0]);
			//fromyear=Integer.parseInt(fromarr[1]);
			//frommonth=Integer.parseInt(fromarr[0]);
			String zero="";
			if(tomonth<10){
				zero="0";
			}
			String temp="M_"+toyear+zero+tomonth;
			
			//dmoncreate=new DynamicMonthCreater();
			//dynamicmonth=dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			//dynamicmonthsum=dmoncreate.createSumMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			
			query.append("SELECT "+title1+temp+" FROM ")
			.append("(SELECT "+title2+temp+"  FROM "+tableName)
			.append(" WHERE title_id='"+title+"' ORDER BY "+temp+" DESC LIMIT 50 )a")
			.append(" UNION ALL SELECT "+title1+"SUM("+temp+") as "+temp+" FROM")
			.append(" (SELECT "+title3+temp+" FROM "+tableName)
			.append(" WHERE title_id='"+title+"' ORDER BY "+temp)
			.append(" DESC LIMIT 50,100000000) b GROUP BY Journal,DOI,Title,Volume,Issue,Page " )
			.append("UNION ALL SELECT "+title3+"SUM("+temp+") as "+temp)
			.append(" FROM "+tableName+" WHERE title_id='"+title+"'")
			.append(" GROUP BY Journal,DOI,Title,Volume,Issue,Page ");
			
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
