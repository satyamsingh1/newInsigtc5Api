package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class MonthlyDownloadAccessCustomer {

	private Counter5DTO dto = null;
	String total="";
	private Counter5ReportsDao c5dao=null;
	private String coloumTitle=" a.* FROM (SELECT Institution_ID, Institution_Name, Institution_Type AS `Type`, 'Total for all Journals' AS Title_ID, '' AS Title, Access_Type, Metric_Type, ";
	private String colTitle=" Institution_ID, Institution_Name, Institution_Type AS `Type`, Title_ID, Title, Access_Type, Metric_Type, ";	
	public MonthlyDownloadAccessCustomer(Counter5DTO c5Dto, RequestMetaData rmd){
		
		try {
			
			this.dto = c5Dto;
		    c5dao = new Counter5ReportsDao(rmd);
		} catch (Exception e) {
			//handle exception
		}
	}

	public MyDataTable getReport(String download) throws Exception{
		MyDataTable mdt=null;
		String query = "";
		try {
			
			query = this.getQuery(download);
			mdt=c5dao.getDynamicReportJson(dto.getWebmartID(), query);
			return mdt;
			
		} catch (Exception e) {
			throw e;
		}
				
	}
	
	private String getQuery(String download) throws Exception{
		
		String[] toarr= null;
		String[] fromarr= null;
		String dynamicmonth = null;
		int toyear=0;
		int fromyear=0;
		int tomonth=0;
		int frommonth=0;
		
		StringBuilder query=new StringBuilder();
		DynamicMonthCreater dmoncreate = null;
		String tableName = "c5_monthly_download_journal_by_customer"; //from config/hardcode/tabvle/xtz/etc
		try {
			
			toarr=dto.getToDate().split("-");
			fromarr=dto.getFromDate().split("-");
			toyear=Integer.parseInt(toarr[1]);
			tomonth=Integer.parseInt(toarr[0]);
			fromyear=Integer.parseInt(fromarr[1]);
			frommonth=Integer.parseInt(fromarr[0]);
			
			dmoncreate=new DynamicMonthCreater();
			dynamicmonth=dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			total=dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear,"YTD Total");
			
			query.append("select "+coloumTitle+dynamicmonth.substring(0,dynamicmonth.lastIndexOf(",")))
			.append(" from "+tableName+"  WHERE Institution_ID NOT IN('','-') AND Institution_Name NOT IN('',' ') AND Data_Type ='Journal' GROUP BY Institution_ID, institution_Name, Institution_Type, Access_Type, Metric_type ")
			.append(" UNION ALL ")
			.append( "select "+colTitle+dynamicmonth.substring(0,dynamicmonth.lastIndexOf(",")) )
			.append(" from "+tableName+"  WHERE Institution_ID NOT IN('','-') AND Institution_Name NOT IN('',' ') AND Data_Type ='Journal' AND "+ total +" >0 GROUP BY Institution_ID, Institution_Name, Institution_Type, Title_ID, title, Access_Type, Metric_type ) a  ")
			.append(" ORDER BY a.Institution_ID, a.Title_ID = 'Total for all Journals' DESC, a.Title_ID ASC, a.Access_Type, a.Metric_Type ");
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
