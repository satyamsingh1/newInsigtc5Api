package com.mps.insight.c5.report.publisher;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class ReferralReports {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private String coloumTitle = "REFEREL_URL, REFEREL_COUNT";

	public ReferralReports(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		//String[] fromarr= null;
		StringBuilder query = new StringBuilder();
		String tableName = "c5_referel_month_wise"; // from
															// config/hardcode/tabvle/xtz/etc
		try {
			toarr=dto.getToDate().split("-");
			//fromarr=dto.getFromDate().split("-");
			query.append("SELECT " + coloumTitle).append(" FROM " + tableName+" where YEAR ='"+toarr[1]+"' AND MONTH ='"+toarr[0]+"'");
			
			if(download.equalsIgnoreCase("preview")){
				query.append(" Limit 500");
			}else{
				StringBuilder queryTotal=new StringBuilder();
				
				queryTotal.append("SELECT * FROM( ")
				.append(query)
				.append(" ORDER BY CASE "
						+ "WHEN REFEREL_URL ='No Referrals' THEN 1 "
						+ "WHEN REFEREL_URL ='others' 	THEN 2 "
						+ "ELSE 0 "
						+ "END, REFEREL_COUNT  DESC ")
				.append(") b UNION ALL "
						+ "SELECT 'Total'AS 'REFEREL_URL', SUM(REFEREL_COUNT) AS REFEREL_COUNT FROM ( ")
				.append(query +" ) a");
				
				query = new StringBuilder(queryTotal);
			}
			
			return query.toString();

		} catch (Exception e) {
			throw e;
		}
	}

	// get M_YYYYMM
	// get D_yyyymmdd

}
