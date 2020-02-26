package com.mps.insight.c5.report.library;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class TR_B2 {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	/*private String tableName = "master_report";*/
	private String Query="";
	private String monthSumQuery ="";
	private String totalMonth ="";
	private String previewType="";
	
	private Counter5DTO dto;
	
	
	public TR_B2(Counter5DTO dto,String previewType,RequestMetaData rmd){
		this.dto=dto;
		this.previewType = previewType;
		this.rmd=rmd;
		run();
	}
	
	public void run(){
		includeMonthParameters();
		generateTRB2Query();
		
	}
	
	private String generateTRB2Query(){
		StringBuilder query = new StringBuilder();
		try {
			query .append("SELECT"); 
			query.append(" `Parent_Title` as 'Title',`Parent_publisher` AS Publisher,");
			query.append(" `Publisher_ID`,`Platform`,`Parent_DOI` as 'DOI',");
			query.append(" CONCAT('"+dto.getPublisher()+"',':',`Parent_Proprietary_ID`) as 'Proprietary_ID',");
			query.append(" `Parent_ISBN` as 'ISBN',`parent_Print_ISSN` as 'Print_ISSN',`Parent_Online_ISSN` As 'Online_ISSN',`Parent_URI` as 'URI', `YOP` as 'YOP', `Metric_Type`, ");
			query.append(" SUM"+totalMonth+"AS `Reporting_Period_Total`,");
			query.append(" "+monthSumQuery+" ");
			query.append(" from "+TableMapper.TABALE.get("master_report_table")+" where");
			query.append(" Institution_ID='"+dto.getInstitutionID()+"' ");
			query.append(" and "+InsightConstant.TR_B2_WHERE_CONDITION); 
			query.append(" and "+totalMonth+" > 0 ");
			query.append(" GROUP BY "+InsightConstant.TR_B2_GROUPBY);
			query.append(" ORDER BY `parent_title` ");
		
			if(previewType.equalsIgnoreCase("preview")){
				query.append(" limit 500 ");
			}else{
				
			}
		} catch (Exception e) {
			rmd.exception("generateTRB2Query : unable to create Query : "+e.toString());
		}
		return this.Query=query.toString();
	}
		private void includeMonthParameters(){
			try {
				String [] fromarr=dto.getFromDate().split("-");
				String [] toarr=dto.getToDate().split("-");

				monthSumQuery = dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
				monthSumQuery = monthSumQuery.substring(0, monthSumQuery.lastIndexOf(","));
				// ((M_201801)+(M_201802)+(M_201803)+(M_201804)+(M_201805)+(M_201806)+(M_201807)+(M_201808)+(M_201809))
				totalMonth= dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");
				
			} catch (Exception e) {
				rmd.exception("includeMonthParameters : unable to generate Month list for query : "+e.toString());
			}
		}
		
		public String getQuery() {
			rmd.log(Query.toString());
			return Query;
		}
	
	
}

