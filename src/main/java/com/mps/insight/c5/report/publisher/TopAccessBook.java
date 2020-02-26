package com.mps.insight.c5.report.publisher;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class TopAccessBook {
	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private RequestMetaData rmd=null;

	public TopAccessBook(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		String[] toarr= null;
		//String[] fromarr= null;
		int toyear=0;
		//int fromyear=0;
		int tomonth=0;
		//int frommonth=0;
		
		StringBuilder query=new StringBuilder();
		String tableName = "c5_top_full_text_chapter_access_by_book"; //from config/hardcode/tabvle/xtz/etc
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
			
			
			String bookId=dto.getBookJournalName();
			query.append("select "
					+ "Title as 'Book', "
					+ "doi as 'DOI', "
					+ "item_title as 'Title', "
					+ "volume as 'Volume', "
					+ "issue as 'Issue',  "
					+ "page as 'Page', "
					+ "M_"+currentYear+""+currentMonth+" as 'Total Successful Full Text Article Requests' "
					+ "from `"+tableName+"` "
					+ "where title_id='"+bookId+"'");
			
			
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
