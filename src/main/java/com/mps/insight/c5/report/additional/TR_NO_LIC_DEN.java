package com.mps.insight.c5.report.additional;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class TR_NO_LIC_DEN {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private Counter5DTO dto;
	private String query = "";
	private String monthSumQuery="";
	private String totalMonth="";
	private String tableName=TableMapper.TABALE.get("tr_master_table");
	private String previewType="";
	
	public TR_NO_LIC_DEN(Counter5DTO dto,String previewType,RequestMetaData rmd){
		this.dto=dto;
		this.previewType = previewType;
		this.rmd = rmd;
		run();
	}
	
	public void run() {
		includeMonthParameters();
		generateTRNOLicQuery();

	}

	private String generateTRNOLicQuery() {
		StringBuilder query = new StringBuilder();
		try {
			query.append("SELECT ");
			 // `Parent_Title`, AS title,`Publisher`,.....`Metric_Type`

			query.append(" " + InsightConstant.TR_NO_LIC_TITLE + ", ");
			query.append(" SUM" + totalMonth + " AS Reporting_Period_Total , ");
			query.append(" " + monthSumQuery + " ");
			query.append(" from " + tableName + " where");
			query.append(" Institution_ID='" + dto.getInstitutionID() + "' ");
			// `Metric_Type` IN ('Total_Item_Requests','Unique_Item_Requests') AND
			query.append(" and " + InsightConstant.TR_NO_LIC_WHERE_CONDITION); 
			query.append(" and " + totalMonth + " > 0 ");
			query.append(" ORDER BY title  ");
			if(previewType.equalsIgnoreCase("preview")){
				query.append(" limit 500 ");
			}else{
				
			}
			
		} catch (Exception e) {
			//MyLogger.error("generateTRNOLicQuery : unable to create Query : "+e.toString());
			rmd.exception("generateTRNOLicQuery : unable to create Query : "+e.toString());
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
