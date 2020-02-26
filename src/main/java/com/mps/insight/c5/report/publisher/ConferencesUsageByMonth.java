package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class ConferencesUsageByMonth {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;

	public ConferencesUsageByMonth(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		String tableName = "c5_conference_usage_by_month"; //from config/hardcode/tabvle/xtz/etc
		try {
			
			toarr=dto.getToDate().split("-");
			fromarr=dto.getFromDate().split("-");
			toyear=Integer.parseInt(toarr[1]);
			tomonth=Integer.parseInt(toarr[0]);
			fromyear=Integer.parseInt(fromarr[1]);
			frommonth=Integer.parseInt(fromarr[0]);
			
			dmoncreate=new DynamicMonthCreater();
			dynamicmonth=dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			
			query.append("select ")
			.append(" `TITLE_ID` AS Tiltle_Id, `TITLE` AS Title, `PUBLISHER` AS Publisher,`PLATFORM` AS Platform,")
			.append("`ONLINE_ISSN` AS Online_ISSN, `PRINT_ISSN` AS Print,`METRIC_TYPE` AS Metric_Type,")
			.append(dynamicmonth.substring(0,dynamicmonth.lastIndexOf(",")))
			.append(" from "+tableName +" GROUP BY `TITLE_ID`, `TITLE`, `PUBLISHER`,`PLATFORM`, `ONLINE_ISSN`, `PRINT_ISSN`,`METRIC_TYPE`  ");
			
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
