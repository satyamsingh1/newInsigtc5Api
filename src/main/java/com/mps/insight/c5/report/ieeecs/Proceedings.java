package com.mps.insight.c5.report.ieeecs;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.TableMapper;

public class Proceedings {
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private Counter5DTO dto;
	private String previewType;
	private RequestMetaData rmd;
	private String query = "";
	private String monthSumQuery = "";
	private String totalMonth = "";

	public Proceedings(Counter5DTO dto, String previewType, RequestMetaData rmd) {
		try {
			this.dto = dto;
			this.previewType = previewType;
			this.rmd = rmd;
			run();
		} catch (Exception e) {
			rmd.exception(e.toString());
		}
	}

	
	private void run(){
		includeMonthParameters();
		String sqlQuery = generateQeury();
		setQuery(sqlQuery);
	}
	
	
	private String generateQeury() {
		
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("SELECT ")
			.append("`parent_title` AS Title, ")
			.append("`Parent_publisher` AS Publisher, ")
			.append("`Publisher_ID`, ")
			.append("`Platform`, ")
			.append("`parent_Print_ISSN` AS Print_ISSN, ")
			.append("`parent_Online_ISSN` AS Online_ISSN, ")
			.append("Metric_Type, ")
			.append("SUM" + totalMonth + " AS `Reporting_Period_Total`, ")
			.append("" + monthSumQuery + " ")
			.append("from " + TableMapper.TABALE.get("master_report_table") + " where ")
			.append("Institution_ID='" + dto.getInstitutionID() + "' ")
			.append("AND Metric_Type IN ('Total_Item_Requests', 'Unique_Item_Requests') ")
			.append("AND data_type ='Proceedings' ")
			.append("GROUP BY `parent_title`, `Parent_publisher`, `Publisher_ID`, `Platform`, `parent_Print_ISSN`, `parent_Online_ISSN`, Metric_Type ")
			.append("ORDER BY parent_title");
			
			if (previewType.equalsIgnoreCase("preview")) {
				sb.append(" limit 500 ");
			} else {

			}
			
		} catch (Exception e) {
			rmd.error("Unable to create query : "+sb.toString()+" : "+e.toString());
		}
		return sb.toString();
	}

	private void includeMonthParameters() {
		try {
			String[] fromarr = dto.getFromDate().split("-");
			String[] toarr = dto.getToDate().split("-");

			// SUM(M_201801) AS `Jan-2018`,SUM(M_201802) AS
			// `Feb-2018`...........
			monthSumQuery = dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
			monthSumQuery = monthSumQuery.substring(0, monthSumQuery.lastIndexOf(","));

			// ((M_201801)+(M_201802)+(M_201803)+(M_201804)+(M_201805)+(M_201806)+(M_201807)+(M_201808)+(M_201809))
			totalMonth = dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");

		} catch (Exception e) {
			rmd.exception("includeMonthParameters : unable to generate Month list for query : " + e.toString());
		}
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	
}
