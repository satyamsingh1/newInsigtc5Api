
package com.mps.insight.c5.report.additional;
import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
public class EBOOK_ACCESS_CHAPTER {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String tableName = "ir_master";
	private String query="";
	private String totalMonth ="";
	private String monthQuery="";
	private String previewType="";
	
 public Counter5DTO dto;
	public EBOOK_ACCESS_CHAPTER(Counter5DTO dto,String previewType,RequestMetaData rmd)
	{
		this.dto=dto;
		this.previewType = previewType;
		this.rmd=rmd;
		run();	
	}
	public void run()
	{
		includeMonth();
		generatEBOOK_ACCESS_CHAPTER_Report();	
	}
	public void generatEBOOK_ACCESS_CHAPTER_Report()
	{
		StringBuilder stb=new StringBuilder();
		try
		{
			stb .append("SELECT "); 
			stb .append(" "+InsightConstant.EBOOK_ACCESS_CHAPTER+",");
			stb.append("SUM"+totalMonth+"AS `Reporting_Period_Total`,");
			stb.append(" "+monthQuery+"");
			stb.append(" from "+tableName+" where");
			
			stb.append(" Institution_ID='"+dto.getInstitutionID()+"' ");
			stb.append(" and "+InsightConstant.EBOOK_ACCESS_CHAPTER_WHERE_CONDITION);
			stb.append(" AND"+ totalMonth+">0");
			if(previewType.equalsIgnoreCase("preview")){
				stb.append(" limit 500 ");
			}else{
				
			}
		}
		
		
		catch(Exception e)
		{
			rmd.exception("EBOOK_ACCESS_CHAPTER : generat EBOOK_ACCESS_CHAPTER Report : Unable to create query "+e.toString());
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
			rmd.exception("EBOOK_ACCESS_CHAPTER : unable to add month in query"+e.toString());
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


