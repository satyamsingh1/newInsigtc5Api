package com.mps.insight.c5.report.publisher;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dynamicdate.DynamicMonthCreaterCounter5;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.product.Counter5ReportsDao;

public class MemberDownloadReport {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private String coloumTitle = "ip_address,";
	String[] monthArray = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	
	public MemberDownloadReport(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		
		String queryMonth = "";
		StringBuilder query = new StringBuilder();
		String[] toarr = dto.getToDate().split("-");
		String[] fromarr = dto.getFromDate().split("-");
		DynamicMonthCreaterCounter5 dynamic=new DynamicMonthCreaterCounter5();
		int today = 0;
		int toMonth = 0;
		int toYear = 0;
		int fromday = 0;
		int fromMonth =0;
		int fromYear = 0;
		 if (null != dto.getFrequency() && dto.getFrequency().contains("MONTHLY")) {
			toMonth = Integer.parseInt(toarr[0]);
			toYear = Integer.parseInt(toarr[1]);
			today =getNumberofDay(toMonth, toYear);
			fromday = 1;
			fromMonth = Integer.parseInt(fromarr[0]);
			fromYear = Integer.parseInt(fromarr[1]);
			queryMonth = dynamic.getDateByDay(today, toMonth, toYear, fromday, fromMonth, fromYear);
		}
		String tableName = "c5_ip_daily_visits";
		
		try {
			query.append("SELECT " + coloumTitle).append(queryMonth + " FROM " + tableName +" group by ip_address");
			
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
	
	public int getNumberofDay(int month,int year){
		int day=31;
		if(month==4 || month==6 || month==9 || month==11){
			day=30;
		}
		if(month==2){
			if(year==2020 || year==2024){
				day=29;
			}else{
				day=28;
			}
		}
		return day;
	}
	
	public String createDailyQueryForMonth(int today,int toMonth, int toYear,int fromday, int fromMonth,
			int fromYear) {
		//DynamicDayMonthCreater ddc = new DynamicDayMonthCreater();
	
		String zeroday="";
		String zeromonth="";
		String temp="";
		StringBuilder sb = new StringBuilder();
		StringBuilder sbtemp = null;
		int tempMonth=0;
		int tempMonth1=0;
		int counter=0;
		for(int i=fromMonth;i<=toMonth;i++){
			counter ++; 
			if (i < 10) {
				zeromonth = "0";
			} else {
				zeromonth = "";
			}
			if(i==toMonth){
				tempMonth=today;
			}else{
				tempMonth=getNumberofDay(i, toYear);
			}
			
			if(i==fromMonth){
				tempMonth1=fromday;
			}else{
				tempMonth1=1;
			}
			sb.append("(");
			sbtemp=new StringBuilder();
			for(int j=tempMonth1;j<=tempMonth;j++){
				
					if (j < 10) {
						zeroday = "0";
					} else {
						zeroday = "";
					}
					sbtemp.append("D_")
							.append(fromYear)
							.append(zeromonth)
							.append(i).append(zeroday+j+"+");
				
			}
			temp += sbtemp.toString();
			
			sb.append(sbtemp.substring(0, sbtemp.lastIndexOf("+")).toString()+") as `"+InsightConstant.MONTH_ARRAY[i]+"-"+toYear+"`,");
		}
		
		temp=sb.toString().substring(0,sb.toString().lastIndexOf(","));
		String query = temp.toString();
		
		
		return query;
	}
	
}
