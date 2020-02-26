package com.mps.insight.c5.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class TR_J4 {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private Counter5DTO dto;
	private String query = "";
	private String monthSumQuery="";
	//private String monthSumtotalquery="";
	private String totalMonth="";
	/*private String tableName="master_report";*/
	private String previewType="";

	public TR_J4(Counter5DTO dto,String previewType,RequestMetaData rmd) {
		this.dto = dto;
		this.previewType = previewType;
		this.rmd = rmd;
		run();
	}

	public void run() {
		includeMonthParameters();
		generateJR4Query();

	}

	private String generateJR4Query() {
		StringBuilder query = new StringBuilder();
		try {
			query.append("SELECT ");
			query.append("`parent_title` AS Title,`Parent_publisher` AS Publisher,`Publisher_ID`,");
			query.append("`Platform`, parent_doi AS `DOI`, ");
			query.append("CONCAT('"+dto.getPublisher()+"',':',`Title_ID`) AS `Proprietary_ID`,`parent_Print_ISSN` AS Print_ISSN,`parent_Online_ISSN` AS Online_ISSN,");
			query.append("`parent_URI` AS URI,`YOP`,`Metric_Type`,");
			
			
			 // `Parent_Title`, AS title,`Publisher`,.....`Metric_Type`

			query.append(" SUM" + totalMonth + " AS Reporting_Period_Total , ");
			query.append(" " + monthSumQuery + " ");
			
			query.append(" from " + TableMapper.TABALE.get("master_report_table") + " where");
			query.append(" Institution_ID='" + dto.getInstitutionID() + "' ");
			// `Metric_Type` IN ('Total_Item_Requests','Unique_Item_Requests') AND
			query.append(" and " + InsightConstant.TR_J4_WHERE_CONDITION); 
			query.append(" and " + totalMonth + " > 0 ");
			// GROUP BY `Parent_Title`,`Publisher`,`Publisher_ID`............
			query.append(" " + InsightConstant.TR_J4_GROUPBY);
			query.append(" ORDER BY `parent_title` ");
			if(previewType.equalsIgnoreCase("preview")){
				query.append(" limit 500 ");
			}else{
				
			}
		} catch (Exception e) {
			//MyLogger.error("generateJR4Query : unable to create Query : "+e.toString());
			rmd.exception("generateJR4Query : unable to create Query : "+e.toString());
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
			/*monthSumtotalquery = dmc.createTotalMonthSumQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
*/
			

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
