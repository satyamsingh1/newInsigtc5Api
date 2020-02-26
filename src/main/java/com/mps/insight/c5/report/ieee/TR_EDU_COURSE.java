package com.mps.insight.c5.report.ieee;
import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;
public class TR_EDU_COURSE {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	/*private String tableName = "master_report";*/
	private String query="";
	private String totalMonth ="";
	private String monthQuery="";
	private String previewType="";
	
 public Counter5DTO dto;
	public TR_EDU_COURSE(Counter5DTO dto,String previewType,RequestMetaData rmd)
	{
		this.dto=dto;
		this.previewType = previewType;
		this.rmd=rmd;
		run();	
	}
	public void run()
	{
		includeMonth();
		generateEDU_COUR_Report();	
	}
	public void generateEDU_COUR_Report()
	{
		StringBuilder stb=new StringBuilder();
		try
		{
			stb .append("SELECT "); 
			stb .append(" "+InsightConstant.EDU_COUR_1+",");
			stb.append("SUM"+totalMonth+"AS `Reporting_Period_Total`,");
			stb.append(" "+monthQuery+"");
			stb.append(" from "+TableMapper.TABALE.get("master_report_table")+" where");
			stb.append(" Institution_ID='" + dto.getInstitutionID() + "' ");
			stb.append(" and " + InsightConstant.EDU_COUR_1_WHERE_CONDITION ); // `Metric_Type`
			stb.append(" and " + totalMonth + " > 0 ");
			stb.append(" GROUP BY  " + InsightConstant.EDU_COUR_1_MASTER_TITLE_1 );
			stb.append(" ORDER BY `parent_title` ");
			if(previewType.equalsIgnoreCase("preview")){
				stb.append(" limit 500 ");
			}else{
				
			}
		}
		catch(Exception e)
		{
			rmd.exception("EDU_COUR : generate EDU_COUR Report  : Unable to create query "+e.toString());
		}
		this.query=stb.toString();
	}
	public void includeMonth()
	{
		try
		{
			String [] fromarr=dto.getFromDate().split("-");
			String [] toarr=dto.getToDate().split("-");
			monthQuery=dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
			monthQuery = monthQuery.substring(0, monthQuery.lastIndexOf(","));
			totalMonth= dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");
		}
		catch(Exception e)
		{
			rmd.exception("EDU_COUR : unable to add month in query"+e.toString());
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
