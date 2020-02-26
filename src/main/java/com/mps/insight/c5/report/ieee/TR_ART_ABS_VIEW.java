package com.mps.insight.c5.report.ieee;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;

import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class TR_ART_ABS_VIEW {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String query = "";
	private String totalMonth = "";
	private String monthQuery = "";
	private String previewType = "";

	public Counter5DTO dto;

	public TR_ART_ABS_VIEW(Counter5DTO dto, String previewType, RequestMetaData rmd) {
		this.dto = dto;
		this.previewType = previewType;
		this.rmd = rmd;
		run();
	}

	public void run() {
		includeMonth();
		generatARTICLEABSTACTVIEWReport();
	}

	public void generatARTICLEABSTACTVIEWReport() {
		StringBuilder stb = new StringBuilder();
		try {
		
			    stb.append("SELECT (CASE DOI WHEN '' THEN Item_ID ELSE DOI END) AS 'DOI' , `Parent_Title`,`Item`,`issue_volume`,`issue_no`,`page_no`,Metric_Type, ");
				stb.append(" SUM"+totalMonth+" AS `Reporting_Period_Total`, ");
				stb.append(" "+ monthQuery +" ");
				stb.append("FROM "+TableMapper.TABALE.get("master_report_table"));
				stb.append(" WHERE Institution_ID='"+dto.getInstitutionID() + "' ");
				stb.append("AND Metric_Type IN ('Total_Item_Investigations','Unique_Item_Investigations') ");
				stb.append("AND Data_Type !='Book' AND Access_Method='Regular' ");
				stb.append("AND Access_Type  IN ('Controlled','OA_GOLD') ");
				stb.append("AND Institution_ID != '' AND Title_ID != '' ");
				stb.append("AND Parent_Title != '' ");
				stb.append("AND "+totalMonth+" >0 ");
				stb.append("GROUP BY Item_ID,`DOI`,`Parent_Title`,`Item`,`issue_volume`,`issue_no`,`page_no`,Metric_Type ");
  
			  if (previewType.equalsIgnoreCase("preview"))
			  { 
				  stb.append(" limit 500 "); } else {
			  }
		} catch (Exception e) {
			rmd.exception("ARTICLE_ABSTRACT_VIEW : generat_ARTICLE_ABSTRACT_VIEW_Report : Unable to create query " + e.toString());
		}
		this.query = stb.toString();
	}

	public void includeMonth() {
		try {
			String[] fromarr = dto.getFromDate().split("-");
			String[] toarr = dto.getToDate().split("-");
			monthQuery = dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
			monthQuery = monthQuery.substring(0, monthQuery.lastIndexOf(","));
			totalMonth = dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");
		} catch (Exception e) {
			rmd.exception("ARTICLE_ABSTRACT_VIEW : unable to add month in query" + e.toString());
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
