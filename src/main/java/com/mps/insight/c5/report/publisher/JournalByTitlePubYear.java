package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class JournalByTitlePubYear {
	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;

	public JournalByTitlePubYear(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		String[] toarr = null;
		String[] fromarr = null;
		int toyear = 0;
		int fromyear = 0;
		int tomonth = 0;
		int frommonth = 0;
		StringBuilder query = new StringBuilder();
		DynamicMonthCreater dmoncreate = new DynamicMonthCreater();
		String tableName = " FROM `c5_journals_by_title_pub_year_new` ";
		String dmonth = null;
		String dmonth2 = null;
		try {
			toarr = dto.getToDate().split("-");
			fromarr = dto.getFromDate().split("-");
			toyear = Integer.parseInt(toarr[1]);
			tomonth = Integer.parseInt(toarr[0]);
			fromyear = Integer.parseInt(fromarr[1]);
			frommonth = Integer.parseInt(fromarr[0]);

			dmonth = dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear, null);
			dmonth2 = dmoncreate.sumMonthQueryC5(tomonth, toyear, frommonth, fromyear);

			query.append("SELECT Title, Publisher, Platform, Journal_DOI AS `DOI`,")
					.append(" PROPRIETARY_ID AS `Proprietary_ID`,")
					.append("PRINT_ISSN AS `Print_ISSN`, ONLINE_ISSN AS `Online_ISSN`, YOP,")
					.append(" " + dmonth + " AS `Reporting_Period_Total`,"+dmonth2+tableName)
					.append("ORDER BY Title, YOP DESC");
			if (download.equalsIgnoreCase("preview")) {
				query.append(" Limit 500");
			} else {
				query.append("");
			}
			return query.toString();
		} catch (Exception e) {
			throw e;
		}

	}
}
