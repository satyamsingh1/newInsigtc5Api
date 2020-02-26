package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class WileyBookChapterRequestByMonth {
	
	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private String coloumTitle = " wileyebook.* FROM (SELECT '' AS `Book ID`, '' AS `Book Title`, '' AS ISBN, 'Total for all Chapter Requests' AS `Chapter Title`, '' AS `Chapter ID`,";
	private String unColoumTitle=" PARENT_ID AS `Book ID`, PARENT_TITLE AS `Book Title`, ISBN AS ISBN, ITEM AS `Chapter Title`, ITEM_ID AS `Chapter ID`,";
	public WileyBookChapterRequestByMonth(Counter5DTO c5Dto, RequestMetaData rmd) {

		try {
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
		String[] toarr= null;
		String[] fromarr= null;
		String dynamicmonth = null;
		String dynamicmonth1 = null;
		String dynamicSumMonth=null;
		int toyear=0;
		int fromyear=0;
		int tomonth=0;
		int frommonth=0;
		String total="";
		
		StringBuilder query=new StringBuilder();
		DynamicMonthCreater dmoncreate = new DynamicMonthCreater();
		String tableName = "`c5_wiley_ebook_chapter_by_month` "; 
		try {
			toarr = dto.getToDate().split("-");
			fromarr = dto.getFromDate().split("-");
			toyear = Integer.parseInt(toarr[1]);
			tomonth = Integer.parseInt(toarr[0]);
			fromyear = Integer.parseInt(fromarr[1]);
			frommonth = Integer.parseInt(fromarr[0]);
			
			dynamicmonth1 = dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			 
			 dynamicmonth=dmoncreate.createTotalMonthSumQueryC55(tomonth, toyear, frommonth, fromyear);
			 
			total=dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear,"YTD Total");
			
			dynamicSumMonth=dmoncreate.sumMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			
			query.append("select " + coloumTitle + dynamicmonth1+""+dynamicmonth+" AS `Reporting_Period_Total`")
			.append(" from " + tableName )
			.append(" UNION ALL select "+ unColoumTitle +dynamicSumMonth+","+total +" AS `YTD Total`")
			.append(" from " + tableName)
			.append(" Where "+total+">0 ) wileyebook ")
			.append(" ORDER BY wileyebook.`Chapter Title` = 'Total for all Chapter Requests' DESC, wileyebook.`Chapter Title` ASC ");
			
			
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
