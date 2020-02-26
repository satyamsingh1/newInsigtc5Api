package com.mps.insight.c5.report.ieee;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class TR_ART_REQ_PARTNER {
	
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	/*private String tableName = "master_report";*/
	private String Query="";
	private String monthSumQuery ="";
	private String monthQuery ="";
	//private String monthSumtotalquery ="";
	private String totalMonth ="";
	private String previewType="";
	
	private Counter5DTO dto;
	
	
	public TR_ART_REQ_PARTNER(Counter5DTO dto,String previewType,RequestMetaData rmd){
		this.dto=dto;
		this.previewType = previewType;
		this.rmd = rmd;
		run();
	}
	
	public void run(){
		includeMonthParameters();
		generateTR_ART_REQ_PARTNERQuery();
		
	}
	
	private String  generateTR_ART_REQ_PARTNERQuery(){
		StringBuilder query = new StringBuilder();
		try {
			query .append("SELECT "); 
			query .append(" "+InsightConstant.TR_ART_REQ_PARTNER_MASTER_TITLE); 
			query.append(" "+monthSumQuery+"");
			query.append(" from "+TableMapper.TABALE.get("master_report_table")+" where");
			query.append(" Institution_ID='"+dto.getInstitutionID()+"' ");
			query.append(" and "+InsightConstant.TR_ART_REQ_PARTNER_WHERE_CONDITION); 
			query.append(" "+InsightConstant.TR_ART_REQ_PARTNER_GROUPBY); 
			query.append(" ORDER BY `title_type` ");
			if(previewType.equalsIgnoreCase("preview")){
				query.append(" limit 500 ");
			}else{
				
			}
		} catch (Exception e) {
			//MyLogger.error("generateJR3GoaQuery : unable to create Query : "+e.toString());
			rmd.exception("generateTR_ART_REQ_PARTNERQuery : unable to create Query : "+e.toString());
		}
		return Query=query.toString();
	}
		private void includeMonthParameters(){
			try {
				String [] fromarr=dto.getFromDate().split("-");
				String [] toarr=dto.getToDate().split("-");
			
				//SUM(M_201801) AS `Jan-2018`,SUM(M_201802) AS `Feb-2018`...........
				monthSumQuery = dmc.createSumMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
				
				monthSumQuery = monthSumQuery.substring(0, monthSumQuery.toString().lastIndexOf(","));
				//(SUM(M_201801)+SUM(M_201802)+SUM(M_201803)+SUM(M_201804)+SUM(M_201805).............
				/*monthSumtotalquery = dmc.createTotalMonthSumQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));*/
				monthQuery=dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
				monthQuery = monthQuery.substring(0, monthQuery.lastIndexOf(","));
			
				
				// ((M_201801)+(M_201802)+(M_201803)+(M_201804)+(M_201805)+(M_201806)+(M_201807)+(M_201808)+(M_201809))
				totalMonth= dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");
				
			} catch (Exception e) {
				//MyLogger.error("includeMonthParameters : unable to generate Month list for query : "+e.toString());
				rmd.exception("includeMonthParameters : unable to generate Month list for query : "+e.toString());
			}
		}

		
		public String getQuery() {
			rmd.log(Query.toString());
			return Query;
		}
		
	  
}
