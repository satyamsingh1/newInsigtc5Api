package com.mps.insight.c5.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class TR_J1 {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private Counter5DTO dto;
	private String query = "";
	private String monthSumQuery="";
	private String totalMonth="";
	/*private String tableName="master_report";*/
	private String previewType="";
	
	public TR_J1(Counter5DTO dto,String previewType,RequestMetaData rmd) {
		this.dto = dto;
		this.previewType = previewType;
		this.rmd = rmd;
		run();
	}

	public void run() {
		includeMonthParameters();
		generateJR1Query();

	}

	private String generateJR1Query() {
		StringBuilder query = new StringBuilder();
		// `Parent_Title`, AS title,`Publisher`,.....`Metric_Type`
		try {
			query.append("SELECT ");
			query.append("`parent_title` AS Title,`Parent_publisher` AS Publisher,");
			query.append("`Publisher_ID`,`Platform`, parent_doi AS `DOI`, ");
			query.append("CONCAT('"+dto.getPublisher()+"',':',`Title_ID`) AS `Proprietary_ID`, ");
			query.append(" `parent_Print_ISSN` AS Print_ISSN,");
			query.append("`parent_Online_ISSN` AS Online_ISSN,`parent_URI` AS URI,`Metric_Type`, ");
			query.append(" SUM" + totalMonth + " AS Reporting_Period_Total , ");
			query.append(" " + monthSumQuery + " ");
			query.append(" from " + TableMapper.TABALE.get("master_report_table") + " where");
			query.append(" Institution_ID='" + dto.getInstitutionID() + "' ");
			query.append(" and " + InsightConstant.TR_J1_WHERE_CONDITION);
			if(dto.getPublisher().equalsIgnoreCase("IEEECS")){
				query.append(" AND title_id IN(SELECT journal_id FROM `c5_subsription_feed` WHERE institution_id='"+ dto.getInstitutionID() + "') AND data_type !='Proceedings'");
			}
			query.append(" and " + totalMonth + " > 0 ");
			query.append(" Group BY "+InsightConstant.TR_J1_GROUPBY);
			query.append(" ORDER BY `parent_title` ");
			
			if(previewType.equalsIgnoreCase("preview")){
				query.append(" limit 500 ");
			}else{
				
			}
			
		} catch (Exception e) {
			//MyLogger.error("generateJR1Query : unable to create Query : "+e.toString());
			rmd.exception("generateJR1Query : unable to create Query : "+e.toString());
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

	/*public String getHeader(){
		
		
	}
	*/
	public String getQuery() {
		rmd.log(query.toString());
		return query;
	}

	/*public void getData(){
		
		String query =  getQuery();
		mdt = null;
		
	}*/
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public void getReport(){
		//json
		//scv
		//tsv
		
		
	}
	
}
