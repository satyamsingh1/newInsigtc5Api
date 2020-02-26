package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class EducationalCoursesAllInstitution {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private String coloumTitle = " `PUBLISHER`, `PLATFORM`, `ONLINE_ISSN`, `PRINT_ISSN`, Metric_type, ";

	public EducationalCoursesAllInstitution(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		String dmonth=null;
		String dmonth2=null;
		String dmonth3=null;
		String dmonth4=null;
		String dmonth5=null;
		int toyear=0;
		int fromyear=0;
		int tomonth=0;
		int frommonth=0;
		
		StringBuilder query=new StringBuilder();
		DynamicMonthCreater dmoncreate = null;
		String tableName = "c5_educational_courses_all_institution"; //from config/hardcode/tabvle/xtz/etc
		try {
			
			toarr=dto.getToDate().split("-");
			fromarr=dto.getFromDate().split("-");
			toyear=Integer.parseInt(toarr[1]);
			tomonth=Integer.parseInt(toarr[0]);
			fromyear=Integer.parseInt(fromarr[1]);
			frommonth=Integer.parseInt(fromarr[0]);
			
			dmoncreate=new DynamicMonthCreater();//SUM(M_201901) as `Jan-2019`
			dynamicmonth=dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			dmonth=dmoncreate.createTotalMonthSumQueryC55(tomonth, toyear, frommonth, fromyear);//(SUM(M_201901)+SUM(M_201902)+SUM(M_201903)
			dmonth2=dmoncreate.createSumMonthQueryC5(tomonth, toyear, frommonth, fromyear);//SUM(M_201901) as `Jan-2019`,SUM(M_201902) as `Feb-2019`,SUM(M_201903) as `Mar-2019`,
			dmonth4=dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear, null);//(M_201901+M_201902+M_201903) 
			dmonth5=dmoncreate.sumMonthQueryC5(tomonth, toyear, frommonth, fromyear);
	
			
			
			
			query.append("SELECT edu.* FROM (SELECT 'Total for all Journals' AS Title,")
			.append(" '' AS `Publisher`, '' AS `Platform`, '' AS `Print_ISSN`, '' AS `Online_ISSN`, '' as `Metric_Type`, ")
			.append(" "+dmonth+" AS `Reporting_Period_Total`,")
			.append(dynamicmonth.substring(0,dynamicmonth.lastIndexOf(",")))
			.append(" FROM `c5_educational_courses_all_institution`")
			.append("UNION ALL ")
			
			.append("SELECT TITLE AS Title, PUBLISHER AS `Publisher`, PLATFORM AS `Platform`,")
			.append(" PRINT_ISSN AS `PRINT ISSN`, ONLINE_ISSN AS `Online ISSN`, Metric_type, ")
			.append(""+dmonth4+" AS `YTD Total`,")
			.append(dmonth5)
			.append("FROM `c5_educational_courses_all_institution`")
			.append("WHERE "+dmonth4+" > 0 ) edu ")
			.append("ORDER BY edu.TITLE = 'Total for all Journals' DESC, edu.TITLE ASC");
			
			
			
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
