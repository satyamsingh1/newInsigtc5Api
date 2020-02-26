package com.mps.insight.c5.report.publisher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mps.insight.c4.report.DynamicDayMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.product.Counter5ReportsDao;

public class HighestAccessInstitution {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao =null;
	private RequestMetaData rmd = null;
	private String coloumTitle = "INSTITUTION AS 'INSTITUTION ID',INSTITUTION_NAME AS 'INSTITUTION NAME', Metric_Type ";
	private String groupBy = "INSTITUTION,INSTITUTION_NAME, Metric_Type ";
	private String coloumTitleUnion = " '' AS INSTITUTION, '' AS INSTITUTION_NAME, '' AS Metric_Type ";
	private String tableName = "c5_pub_high_access_institution";
	
	public HighestAccessInstitution(Counter5DTO c5Dto, RequestMetaData rmd) {

		try {

			this.dto = c5Dto;
		    c5dao = new Counter5ReportsDao(rmd);
		    this.rmd = rmd;
		} catch (Exception e) {

		}
	}

	public MyDataTable getReport(String download) {
		MyDataTable mdt = null;
		String query = "";
		try {

			query = this.getQuery(download);
			System.out.println(query);
			mdt = c5dao.getDynamicReportJson(dto.getWebmartID(), query);
			return mdt;
		} catch (Exception e) {
			throw e;
		}
	}

	private String getQuery(String download) {
		DynamicDayMonthCreater ddc = new DynamicDayMonthCreater();
		String queryMonth = "";
		Map<String, String> dayMap = null;
		StringBuilder query = new StringBuilder();
		String[] toarr = dto.getToDate().split("-");
		String[] fromarr = dto.getFromDate().split("-");
		int today = 0;
		int toMonth = 0;
		int toYear = 0;
		int fromday = 0;
		int fromMonth =0;
		int fromYear = 0;
		if (null != dto.getFrequency() && dto.getFrequency().contains("DAILY")) {
			today = Integer.parseInt(toarr[0]);
			toMonth = Integer.parseInt(toarr[1]);
			toYear = Integer.parseInt(toarr[2]);
			fromday = Integer.parseInt(fromarr[0]);
			fromMonth = Integer.parseInt(fromarr[1]);
			fromYear = Integer.parseInt(fromarr[2]);
			queryMonth = ddc.createDailyQueryC5(today, toMonth, toYear, fromday, fromMonth, fromYear);
			List<String> dailyList = ddc.createDailyQueryC5List(today, toMonth, toYear, fromday, fromMonth, fromYear);
			dayMap = getMonthQureyDaily(dailyList);
			dayMap.put("queryMonth", queryMonth);
		} else if (null != dto.getFrequency() && dto.getFrequency().contains("MONTHLY")) {
			toMonth = Integer.parseInt(toarr[0]);
			toYear = Integer.parseInt(toarr[1]);
			today =ddc.getNumberofDay(toMonth, toYear);
			fromday = 1;
			fromMonth = Integer.parseInt(fromarr[0]);
			fromYear = Integer.parseInt(fromarr[1]);
		    queryMonth = ddc.createDailyQueryForMonth(today, toMonth, toYear, fromday, fromMonth, fromYear);
			Map<String, String> queryMont = ddc.createDailyQueryForMonthList(today, toMonth, toYear, fromday, fromMonth, fromYear);
			dayMap = getMonthQureyMonthly(queryMont);
			dayMap.put("queryMonth", queryMonth);
		}
		
		if(dto.getReportCode().contains("HIGH_ACCESS_INST_BOOK")){
			tableName="c5_pub_high_access_institution_books";	
		}
		
		try {
			if(download.equalsIgnoreCase("preview")){
				query = new StringBuilder();
				query.append("SELECT " + coloumTitle+",");
				
				if(dto.getFrequency().contains("DAILY")){
					query.append(queryMonth+" ");
					//.append("SUM("+dayMap.get("dayAdd")+") As Total ");
				}else{
					query.append(queryMonth);
				}
				
				query.append("FROM " + tableName+" ")
				.append("GROUP BY "+groupBy+" ")
				.append("ORDER BY Total DESC ")
				.append(" Limit 500 ");
			}else{
				
				query.append("SELECT * FROM ( ")
				//sub query start
				
				.append("SELECT @a:=@a+1 AS 'Sr.no', main.*  FROM ( ")
				.append("SELECT " + coloumTitle+",")
				.append(dayMap.get("queryMonth")+" ")
				//.append("SUM("+dayMap.get("dayAdd")+") As Total ")
				.append("FROM " + tableName+" ")
				.append("GROUP BY "+groupBy+" ")
				.append("ORDER BY Total DESC ")
				.append(")main, (SELECT @a:= 0) AS a LIMIT 50 ")
				.append(") org ")
				
				//Second Other Total 
				
				.append("UNION ALL ")
				
				.append("SELECT 'Other' AS 'Sr.no',  '' AS INSTITUTION, '' AS INSTITUTION_NAME, '' AS Metric_Type , ")
				.append(dayMap.get("dayAliasSum")+", ")
				.append("'' AS  Average,'' AS total  FROM ( ")
				.append("SELECT * FROM ( ")
				.append("SELECT " + coloumTitle+",")
				.append(dayMap.get("queryMonth")+" ")
				.append("FROM " + tableName+" ")
				.append("GROUP BY "+groupBy+" ")
				.append("ORDER BY Total DESC ")
				.append(")main LIMIT 50, 100000 ")
				.append(") a ")
				.append("UNION ALL ")
				//Third All total
				.append("SELECT 'Total' AS 'Sr.no',")
				.append(coloumTitleUnion+", "+dayMap.get("dailySum")+",'' AS  Average,'' AS total ")
				.append("FROM " + tableName);
				
				
				
				
				
				
				
			/*	
				
				
				.append(") b, (SELECT @a:= 0) AS a ")
				
				.append(" UNION ALL ")
				
				.append(" SELECT 'Other' AS 'Sr.no', ")
				.append(coloumTitleUnion+", '' AS  Average,'' AS total , ")
				.append(dayMap.get("dayAliasSum"))
				
				.append("FROM (")
				
				.append("SELECT "+coloumTitle+","+dayMap.get("queryMonth")+" ")
				
				.append("FROM " + tableName+" ")
				.append("GROUP BY "+groupBy+" ")
				.append("")
				.append("LIMIT 50 , 100000")
				
				.append(") a ")
				
				.append(" UNION ALL ")
				.append("SELECT 'Total' AS 'Sr.no',")
				.append(coloumTitleUnion+", '' AS  Average,'' AS total ,")
				.append(dayMap.get("dailySum"))
				.append("FROM " + tableName); */
			}
			
			return query.toString();

		} catch (Exception e) {
			throw e;
		}
	}
	
	private Map<String, String> getMonthQureyDaily(List<String> dailyList){
		Map<String, String> dayMap=new HashMap<>();
		
		try {
			if(dailyList.isEmpty()){
				rmd.exception("Day list is Empty : ");
			}else{
				
				StringBuilder dayAdd=new StringBuilder();
				StringBuilder dayAliasSum=new StringBuilder();
				StringBuilder dayAlias=new StringBuilder();
				StringBuilder dailySum=new StringBuilder();
				
				for (String day : dailyList) {
					dayAdd.append(day+"+");
					String year = day.substring(2, 6);
					String month = day.substring(6, 8);
					String cDay = day.substring(8, 10);
					String cMonth = InsightConstant.MONTH_ARRAY[Integer.parseInt(month)];
					dayAliasSum.append("sum(a.`"+Integer.parseInt(cDay)+"-"+cMonth+"-"+year+"`) As '"+Integer.parseInt(cDay)+"-"+cMonth+"-"+year+"',");
					dailySum.append("sum("+day+") As '"+Integer.parseInt(cDay)+"-"+cMonth+"-"+year+"',");
					dayAlias.append("a.`"+Integer.parseInt(cDay)+"-"+cMonth+"-"+year+"`+");
				}
				
				dayMap.put("dayAdd", dayAdd.substring(0, dayAdd.length()-1));
				dayMap.put("dailySum", dailySum.substring(0, dailySum.length()-1));
				dayMap.put("dayAliasSum", dayAliasSum.substring(0, dayAliasSum.length()-1));
				dayMap.put("dayAlias", dayAlias.substring(0, dayAlias.length()-1));
			}
		} catch (Exception e) {
			rmd.exception("Error while creating day list : "+e.toString());
		}
		
		
		return dayMap;
		
	}
	
	private Map<String, String> getMonthQureyMonthly(Map<String, String> dailyList){
		Map<String, String> dayMap=new HashMap<>();
		
		try {
			if(dailyList.isEmpty()){
				rmd.exception("Day list is Empty : ");
			}else{
				
				
				StringBuilder dayAliasSum=new StringBuilder();
				StringBuilder dayAlias=new StringBuilder();
				StringBuilder dayAsyearSum=new StringBuilder();
				
				// month list for total from subquery
				String monthlist = dailyList.get("monthName");
				String[] splitMonthList = monthlist.split(",");
				for (String month : splitMonthList) {
					dayAliasSum.append("sum(a.`"+month+"`) As '"+month+"',");
				}
				
				
				if(dailyList.get("monthsAsYear").contains(",")){
					String[] splitdaylylist = dailyList.get("monthsAsYear").split(",");
					for (String string : splitdaylylist) {
						dayAsyearSum.append("sum"+string+",");
					}
				}
				
				dayAlias.append("a.`total`+");
				
				dayMap.put("dayAdd", dailyList.get("months").substring(0, dailyList.get("months").length()-1));
				dayMap.put("dailySum", dayAsyearSum.substring(0, dayAsyearSum.length()-1));
				dayMap.put("dayAliasSum", dayAliasSum.substring(0, dayAliasSum.length()-1));
				dayMap.put("dayAlias", dayAlias.substring(0, dayAlias.length()-1));
				dayMap.put("queryMonth", dailyList.get("monthsAsYear").substring(0, dailyList.get("monthsAsYear").length()-1));
				
			}
		} catch (Exception e) {
			rmd.exception("Error while creating day list : "+e.toString());
		}
		
		
		return dayMap;
		
	}
	
	
}
