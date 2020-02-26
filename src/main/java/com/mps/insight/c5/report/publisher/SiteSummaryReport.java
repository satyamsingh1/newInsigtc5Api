package com.mps.insight.c5.report.publisher;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Months;

import com.mps.insight.c4.report.DynamicDayMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class SiteSummaryReport {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private String coloumTitle = "`TYPE`,";
	String[] monthArray = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	
	public SiteSummaryReport(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		DynamicDayMonthCreater ddc = new DynamicDayMonthCreater();
		String queryMonth = "";
		StringBuilder query = new StringBuilder();
		String[] toarr = dto.getToDate().split("-");
		String[] fromarr = dto.getFromDate().split("-");
		int today = 0;
		int toMonth = 0;
		int toYear = 0;
		int fromday = 0;
		int fromMonth =0;
		int fromYear = 0;
		if (null != dto.getFrequency() && (dto.getFrequency().contains("DAILY"))) {
			today = Integer.parseInt(toarr[0]);
			toMonth = Integer.parseInt(toarr[1]);
			toYear = Integer.parseInt(toarr[2]);
			fromday = Integer.parseInt(fromarr[0]);
			fromMonth = Integer.parseInt(fromarr[1]);
			fromYear = Integer.parseInt(fromarr[2]);
			queryMonth = ddc.getDateDaybyDayforSiteSummary(today, toMonth, toYear, fromday, fromMonth, fromYear);
		}else if (null != dto.getFrequency() && (dto.getFrequency().contains("WEEKLY"))) {
			today = Integer.parseInt(toarr[0]);
			toMonth = Integer.parseInt(toarr[1]);
			toYear = Integer.parseInt(toarr[2]);
			fromday = Integer.parseInt(fromarr[0]);
			fromMonth = Integer.parseInt(fromarr[1]);
			fromYear = Integer.parseInt(fromarr[2]);
			queryMonth = call(fromday, fromMonth, fromYear, today, toMonth, toYear);
					//ddc.createDailyQueryC5(today, toMonth, toYear, fromday, fromMonth, fromYear);
		} else if (null != dto.getFrequency() && dto.getFrequency().contains("MONTHLY")) {
			toMonth = Integer.parseInt(toarr[0]);
			toYear = Integer.parseInt(toarr[1]);
			today =ddc.getNumberofDay(toMonth, toYear);
			fromday = 1;
			fromMonth = Integer.parseInt(fromarr[0]);
			fromYear = Integer.parseInt(fromarr[1]);
			queryMonth = ddc.getDateByDay(today, toMonth, toYear, fromday, fromMonth, fromYear, "Monthly");
		}
		String tableName = "c5_site_summary";
		
		StringBuilder orderBy=new StringBuilder();
		orderBy.append("ORDER BY ")
		.append("CASE ")
		.append("WHEN `type` LIKE '%hit%' THEN 0 ")
		.append("WHEN `type` LIKE '%page_view%' THEN 1 ")
		.append("WHEN `type` LIKE '%visit%' THEN 2 ")
		.append("WHEN `type` LIKE '%full_text%' THEN 3 ")
		.append("ELSE 5 END, `type`");
		
		try {
			query.append("SELECT " + coloumTitle).append(queryMonth + " FROM " + tableName +" GROUP BY `TYPE` "+orderBy );
			
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
	
	public String call(int fromDay, int fromMonth, int fromYear, int toDay, int toMonth, int toYear) {
		LocalDate from = LocalDate.of(fromYear, fromMonth, fromDay);
		LocalDate to = LocalDate.of(toYear, toMonth, toDay);
		LocalDate firstDate = from;
		StringBuffer str = new StringBuffer("SUM(`");
		StringBuffer strAverage = new StringBuffer("(SUM(`");
		StringBuffer total=new StringBuffer();
		int counts = 1;
		int averageCount=1;
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("'D_'yyyyMMdd");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d-MMM-yyyy");

		while (from.isBefore(to)||from.isEqual(to)) {
		str.append(from.format(formatter1));
		strAverage.append(from.format(formatter1));
		str.append("`");
        strAverage.append("`");
		if (counts > 6) {
			averageCount++;
		str.append(") ");
		strAverage.append(") ");
		str.append(" AS `");
		str.append(firstDate.format(formatter2));
		str.append("-");//Week seprator
		str.append(from.format(formatter2));
		str.append("`,SUM(`");
		strAverage.append("+ SUM(`");
		firstDate = from.plus(1, ChronoUnit.DAYS);
		counts = 0;

		} else {
		str.append("+`");
		strAverage.append("+`");
		}
		counts++;
		from = from.plus(1, ChronoUnit.DAYS);
		}
         strAverage.append(") ");
		str.append(") AS `");
		from = from.minus(1, ChronoUnit.DAYS);
		str.append(firstDate.format(formatter2));
		str.append("-");//Week seprator
		str.append(from.format(formatter2));
		str.append("`");
		 total.append(strAverage.toString());
		strAverage.append(")/"+averageCount+") AS `Average`");
		total.append(") as `Total`");
		str.append(",Floor("+strAverage);
		str.append(","+total);
		return str.toString().replace("+`)", ")");
	}


}
