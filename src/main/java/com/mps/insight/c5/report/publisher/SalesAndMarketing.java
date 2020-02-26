package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class SalesAndMarketing {
	
	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	public SalesAndMarketing(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		
		StringBuilder query=new StringBuilder();
		try {
			query.append(" select 'Sales_and_Marketing report only for downloading ' from dual");
			return query.toString();
		} catch (Exception e) {
			//
			throw e;
		}
	 
		
	}

}
