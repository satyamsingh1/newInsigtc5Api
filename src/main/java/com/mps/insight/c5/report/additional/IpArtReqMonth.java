
package com.mps.insight.c5.report.additional;
import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;
public class IpArtReqMonth {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String tableName = TableMapper.TABALE.get("c5_ip_address");
	private String query="";
	private String totalMonth ="";
	private String summonth="";
	private String previewType="";

 public Counter5DTO dto;
	public IpArtReqMonth(Counter5DTO dto,String previewType,RequestMetaData rmd)
	{
		this.dto=dto;
		this.previewType = previewType;
		this.rmd=rmd;
		run();	
	}
	public void run()
	{
		includeMonth();
		generatIP_ART_REQ_MONTH_Report();	
	}
	public void generatIP_ART_REQ_MONTH_Report()
	{
		StringBuilder stb=new StringBuilder();
		try
		{
			stb .append("SELECT "); 
			stb .append(" "+InsightConstant.IP_ART_REQ_MONTH+",");
			stb.append("SUM"+totalMonth+"AS `Reporting_Period_Total`,");
			stb.append(" "+summonth+"");
			stb.append(" from "+tableName+" where");
			stb.append(" Institution_ID='"+dto.getInstitutionID()+"' ");
			stb.append(" and "+InsightConstant.IP_ART_REQ_WHERE_CONDITION);
			stb.append(" AND"+ totalMonth+">0 ");
			stb.append(" GROUP BY ip, Metric_Type");
			if(previewType.equalsIgnoreCase("preview")){
				stb.append(" limit 500 ");
			}else{
				
			}
		}
		catch(Exception e)
		{
			rmd.exception("IP_ART_REQ_MONTH : generat IP_ART_REQ_MONTH Report : Unable to create query "+e.toString());
		}
		this.query=stb.toString();
	}
	public void includeMonth()
	{
		try
		{
			String [] fromarr=dto.getFromDate().split("-");
			String [] toarr=dto.getToDate().split("-");
			totalMonth= dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");
			summonth= dmc.createSumMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
			summonth = summonth.substring(0, summonth.lastIndexOf(","));
		}
		catch(Exception e)
		{
			rmd.exception("IP_ART_REQ_MONTH : unable to add month in query"+e.toString());
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


