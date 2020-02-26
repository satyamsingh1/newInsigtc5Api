package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class BookUsageAllInstitution {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null ;
	private String coloumTitle = "`title` AS `Book`,`publisher` AS `Publisher`,`platform` AS `Platform`,`isbn` AS `Print ISBN`,";

	public BookUsageAllInstitution(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		
		StringBuilder query=new StringBuilder();
		DynamicMonthCreater dmoncreate = null;
		String tableName = "c5_book_usage_all_institution"; //from config/hardcode/tabvle/xtz/etc
		String totalOfmonth="";
		try {
			
			toarr=dto.getToDate().split("-");
			fromarr=dto.getFromDate().split("-");
			toyear=Integer.parseInt(toarr[1]);
			tomonth=Integer.parseInt(toarr[0]);
			fromyear=Integer.parseInt(fromarr[1]);
			frommonth=Integer.parseInt(fromarr[0]);
			
			dmoncreate=new DynamicMonthCreater();
			dynamicmonth=dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			totalOfmonth=dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear,"YTD Total");
			
			query.append("select "+coloumTitle)
			.append(totalOfmonth.substring(0, totalOfmonth.lastIndexOf(","))+"As Reporting_Period_Total,"+dynamicmonth.substring(0,dynamicmonth.lastIndexOf(","))+" from "+tableName+" ORDER BY title");
			
			//month=total.substring(0, total.lastIndexOf(","))+"As Reporting_Period_Total,"+dynamicmonth.substring(0,dynamicmonth.lastIndexOf(","));
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
