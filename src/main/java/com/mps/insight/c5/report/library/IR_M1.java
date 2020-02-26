package com.mps.insight.c5.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class IR_M1 {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private Counter5DTO dto;
	private String query = "";
	private String monthSumQuery="";
	private String totalMonth="";
	/*private String tableName="master_report";*/
	private String previewType="";
	
	public IR_M1(Counter5DTO dto,String previewType,RequestMetaData rmd) {
		this.dto = dto;
		this.previewType = previewType;
		this.rmd = rmd;
		run();
	}

	public void run() {
		includeMonthParameters();
		generateIRM1Query();

	}

	private String generateIRM1Query() {
		StringBuilder query = new StringBuilder();
		try {
			query.append("SELECT ");
			 // `Parent_Title`, AS title,`Publisher`,.....`Metric_Type`

			query.append(" " + InsightConstant.IR_M1_MASTER_TITLE + ", ");
			query.append(" SUM" + totalMonth + " AS Reporting_Period_Total , ");
			query.append(" " + monthSumQuery + " ");
			query.append(" from " + TableMapper.TABALE.get("master_report_table") + " where");
			query.append(" Institution_ID='" + dto.getInstitutionID() + "' ");
			// `Metric_Type` IN ('Total_Item_Requests','Unique_Item_Requests') AND
			query.append(" and " + InsightConstant.IR_M1_WHERE_CONDITION); 
			query.append(" and " + totalMonth + " > 0 ");
			
			query.append("GROUP BY "+ InsightConstant.IR_M1_MASTER_TITLE );
			query.append(" ORDER BY `Item` ");
			
			if(previewType.equalsIgnoreCase("preview")){
				query.append(" limit 500 ");
			}else{
				
			}
			
		} catch (Exception e) {
		rmd.exception("generateIRM1Query : unable to create Query : "+e.toString());
		}
		return this.query = query.toString();
	}

	private void includeMonthParameters() {
		try {
			String[] fromarr = dto.getFromDate().split("-");
			String[] toarr = dto.getToDate().split("-");

			// (M_201801) AS `Jan-2018`,(M_201802) AS `Feb-2018`...........
			monthSumQuery = dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));

			monthSumQuery = monthSumQuery.substring(0, monthSumQuery.lastIndexOf(","));
			
			// ((M_201801)+(M_201802)+(M_201803)+(M_201804)+(M_201805)+(M_201806)+(M_201807)+(M_201808)+(M_201809))
			totalMonth = dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");

		} catch (Exception e) {
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
