package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class SectionRequest {
	
	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	//private String coloumTitle = " `Title`,`Publisher`,`Platform`,`ISBN` AS 'ISBN',`ISBN` AS 'e-ISBN',`Print_ISSN` AS 'ISSN',`Metric_Type`,";
	
	private String coloumTitle = " parent_title AS `Title`, parent_publisher AS `Publisher`,`Platform`,`ISBN` AS 'ISBN',`ISBN` AS 'e-ISBN', `parent_Print_ISSN` AS 'ISSN',`Metric_Type`,";

	private String groupTitle = " GROUP BY parent_title , parent_publisher ,`Platform`,`ISBN`, `parent_Print_ISSN`,`Metric_Type` ";
	public SectionRequest(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		int toyear=0;
		int fromyear=0;
		int tomonth=0;
		int frommonth=0;
		String total="";
		
		StringBuilder query=new StringBuilder();
		DynamicMonthCreater dmoncreate = new DynamicMonthCreater();
		//String tableName = "c5_section_request ";
		String tableName = "c5_ebook_access_by_chapter "; 
		try {
			toarr = dto.getToDate().split("-");
			fromarr = dto.getFromDate().split("-");
			toyear = Integer.parseInt(toarr[1]);
			tomonth = Integer.parseInt(toarr[0]);
			fromyear = Integer.parseInt(fromarr[1]);
			frommonth = Integer.parseInt(fromarr[0]);
			dynamicmonth = dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			total=dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear,"YTD Total");
			String month=null;
			month= "SUM("+total+") As Reporting_Period_Total,"+dynamicmonth.substring(0,dynamicmonth.lastIndexOf(","));
			
			query.append("select " + coloumTitle + month)
			.append(" from " + tableName+ " Where metric_type in ('Total_Item_Requests') And "+total+">0" +groupTitle);
			
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
