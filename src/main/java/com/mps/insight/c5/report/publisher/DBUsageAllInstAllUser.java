package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class DBUsageAllInstAllUser {
	
	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private String coloumTitle = "`institution_id` AS 'Account_id', `title_id` AS 'Database', `database` AS 'Title',`Publisher`,`isni`,`Metric_Type`,";
	private String groupBy ="`institution_id`, `title_id`, `database`,`Publisher`,`isni`,`Metric_Type`";
	
	public DBUsageAllInstAllUser(Counter5DTO dto, RequestMetaData rmd) {
		try {

			this.dto = dto;
		    c5dao = new Counter5ReportsDao(rmd);
		} catch (Exception e) {

		}
	}
	
	public MyDataTable getReport(String download, String queryType) {
		MyDataTable mdt = null;
		String query = "";
		try {

			query = this.getQuery(download, queryType);
			mdt = c5dao.getDynamicReportJson(dto.getWebmartID(), query);
			return mdt;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	

	private String getQuery(String download, String queryType) {
		String[] toarr= null;
		String[] fromarr= null;
		String dynamicmonth = null;
		int toyear=0;
		int fromyear=0;
		int tomonth=0;
		int frommonth=0;
		
		StringBuilder query=new StringBuilder();
		DynamicMonthCreater dmoncreate = null;
		String tableName = "dr_master"; //from config/hardcode/tabvle/xtz/etc
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
			//totalOfmonth=dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear,"YTD Total");
			
			if("Inst".equalsIgnoreCase(queryType)){
				query.append("select "+coloumTitle)
				.append(dynamicmonth.substring(0,dynamicmonth.lastIndexOf(","))+" ")
				.append("from "+tableName+" ")
				.append("WHERE institution_id NOT IN ('',' ','-') ")
				.append("GROUP BY "+groupBy);
			}if("User".equalsIgnoreCase(queryType)){
				query.append("select "+coloumTitle)
				.append(dynamicmonth.substring(0,dynamicmonth.lastIndexOf(","))+" ")
				.append("from "+tableName+" ")
				.append("GROUP BY "+groupBy);
			}if("All_Inst_All_User".equalsIgnoreCase(queryType)){
				query.append("select "+coloumTitle)
				.append(dynamicmonth.substring(0,dynamicmonth.lastIndexOf(","))+" ")
				.append("from "+tableName+" ")
				.append("WHERE metric_type IN ('Total_Item_Investigations', 'Searches_Regular') ")
				.append("GROUP BY "+groupBy);
			}
			
			
			
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
