package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class CommonPublisherReportImpl {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private String coloumTitle = "Book,Publisher,Platform,ISBN,";
	private String GroupByTitle = "";

	public CommonPublisherReportImpl(Counter5DTO c5Dto, RequestMetaData rmd) {

		try {

			this.dto = c5Dto;
			c5dao = new Counter5ReportsDao(rmd);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public MyDataTable getReport(String download) {
		MyDataTable mdt = null;
		String query = null;
		try {
			query = this.getQuery(download);
			mdt = c5dao.getDynamicReportJson(dto.getWebmartID(), query);
			return mdt;
		} catch (Exception e) {
			throw e;
		}

	}

	private String getQuery(String download) {
		String[] toarr = null;
		String[] fromarr = null;
		String dynamicmonth = null;
		int toyear = 0;
		int fromyear = 0;
		int tomonth = 0;
		int frommonth = 0;
		StringBuilder query = new StringBuilder();
		DynamicMonthCreater dmoncreate = new DynamicMonthCreater();
		String tableName = getTableName(); // from
											// config/hardcode/tabvle/xtz/etc
		String whereClause = null;
		String groupAndOrderCondition = null;
		String total="";
		try {
			groupAndOrderCondition = groupAndOrderCondition();
			whereClause = whereCondition();
			toarr = dto.getToDate().split("-");
			fromarr = dto.getFromDate().split("-");
			toyear = Integer.parseInt(toarr[1]);
			tomonth = Integer.parseInt(toarr[0]);
			fromyear = Integer.parseInt(fromarr[1]);
			frommonth = Integer.parseInt(fromarr[0]);
			dynamicmonth = dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			total=dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear,"YTD Total");
			String month=null;
			month=total+"As Reporting_Period_Total,"+dynamicmonth.substring(0,dynamicmonth.lastIndexOf(","));
			
			query.append("select " + getColoumnTitle() + month)
					.append(" from " + tableName+ getGroupByTitle()+total);
			if (null != whereClause && whereClause.length() > 4) {
				query.append(" where " + whereClause);
			}
			query.append(groupAndOrderCondition);
			
			if(download.equalsIgnoreCase("preview")){
				query.append(" Limit 500");
			}else{
				query.append("");
			}
			

			return query.toString();
		} catch (Exception e) {
			throw e;
		}

	}

	public String getTableName() {
		String tablename = "";
		if (dto.getReportCode().equalsIgnoreCase("EBOOK_OUTPUT") ) {
			tablename = "c5_ebook_output_and_details";
		}

		return tablename;
	}

	public String getColoumnTitle() {
		 if (dto.getReportCode().equalsIgnoreCase("EBOOK_OUTPUT")) {
			coloumTitle = "`Institution_ID` AS 'Account ID',`Institution_Name` AS 'Institution or Customer ID',"
					+ "`Institution_Type` AS 'Type',`Title` AS 'eBook ID',`Metric_Type`,";
		}

		return coloumTitle;
	}
	
	public String getGroupByTitle() {
		if (dto.getReportCode().equalsIgnoreCase("EBOOK_ACCESS_CHAPTER")) {
			GroupByTitle = " GROUP BY `DOI`,`Item`,`Publication_Date`,`Item_ID`,`Issue_no`,`Title_ID`,`Parent_Title`,`Article_Version`,`Publisher`,`ISBN`,`Print_ISSN`,`Online_ISSN`,`Platform`,`Parent_DOI`,";
		}else if (dto.getReportCode().equalsIgnoreCase("EBOOK_OUTPUT")) {
			GroupByTitle = " GROUP BY `Institution_ID`,`Institution_Name`,`Institution_Type`,`Title`,`Metric_Type`,";
		}

		return GroupByTitle;
	}
	
	public String whereCondition() {
		String querypart="";
		if (dto.getReportCode().equalsIgnoreCase("EBOOK_OUTPUT")) {
			querypart = " Title='Total for all Titles'";
		}
		return querypart;
	}

	public String groupAndOrderCondition() {
		String query="";
		 if (dto.getReportCode().equalsIgnoreCase("EBOOK_OUTPUT") ) {
			query = " ORDER BY Institution_ID";
		}
		return query;
	}

}
