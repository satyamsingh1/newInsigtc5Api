package com.mps.insight.c5.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class TR_J3 {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private Counter5DTO dto;
	private String query = "";
	private String monthSumQuery="";
	private String monthSumtotalquery="";
	private String totalMonth="";
	/*private String tableName="master_report";*/
	private String previewType="";
	
	public TR_J3(Counter5DTO dto,String previewType,RequestMetaData rmd) {
		this.dto = dto;
		this.previewType = previewType;
		this.rmd = rmd;
		run();
	}

	public void run() {
		includeMonthParameters();
		generateJR3Query();

	}
	
	private String generateJR3Query() {
		StringBuilder query = new StringBuilder();
		try {
			query.append("SELECT ");
			query.append("`Parent_Title` AS Title ,`Parent_publisher` AS Publisher,`Publisher_ID`, ");
			query.append("`Platform`,parent_doi AS `DOI`,  CONCAT('"+dto.getPublisher()+"',':',`Title_ID`) as `Proprietary_ID`, ");
			query.append("`parent_Print_ISSN` AS Print_ISSN,`parent_Online_ISSN` AS Online_ISSN,`parent_URI` AS `URI`,`Access_Type`,`Metric_Type`, ");
			
			 // `Parent_Title`, AS title,`Publisher`,.....`Metric_Type`

			
			query.append(" " + monthSumtotalquery + " ");
			query.append(" " + monthSumQuery + " ");
			

			query.append(" from " + TableMapper.TABALE.get("master_report_table") + " where");
			query.append(" Institution_ID='" + dto.getInstitutionID() + "' ");
			// `Metric_Type` IN ('Total_Item_Requests','Unique_Item_Requests') AND
			query.append(" and " + InsightConstant.TR_J3_WHERE_CONDITION); 
			query.append(" and " + totalMonth + " > 0 ");
			// GROUP BY `Parent_Title`,`Publisher`,`Publisher_ID`............
			query.append(" " + InsightConstant.TR_J3_GROUPBY); 
			query.append(" ORDER BY `parent_title` ");
			if(previewType.equalsIgnoreCase("preview")){
				query.append(" limit 500 ");
			}else{
				
			}
		} catch (Exception e) {
			//MyLogger.error("generateJR3Query : unable to create Query : "+e.toString());
			rmd.exception("generateJR3Query : unable to create Query : "+e.toString());
		}
		return this.query = query.toString();
	}

	private void includeMonthParameters() {
		try {
			String[] fromarr = dto.getFromDate().split("-");
			String[] toarr = dto.getToDate().split("-");

			// SUM(M_201801) AS `Jan-2018`,SUM(M_201802) AS `Feb-2018`...........
			monthSumQuery = dmc.createSumMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
			
			monthSumQuery = monthSumQuery.substring(0, monthSumQuery.toString().lastIndexOf(","));
			
			// (SUM(M_201801)+SUM(M_201802)+SUM(M_201803)+SUM(M_201804)+SUM(M_201805).............
			monthSumtotalquery = dmc.createTotalMonthSumQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));

			

			// ((M_201801)+(M_201802)+(M_201803)+(M_201804)+(M_201805)+(M_201806)+(M_201807)+(M_201808)+(M_201809))
			totalMonth = dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");

		} catch (Exception e) {
			//MyLogger.error("includeMonthParameters : unable to generate Month list for query : "+e.toString());
			rmd.exception("includeMonthParameters : unable to generate Month list for query : "+e.toString());
		}
	}

	public String getQuery() {
		rmd.log(query.toString());
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
