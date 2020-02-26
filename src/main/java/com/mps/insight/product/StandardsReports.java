package com.mps.insight.product;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.DynamicDatabase;
import com.mps.insight.global.InsightConstant;

public class StandardsReports {
	@Context
	private HttpServletRequest servletRequest;
	RequestMetaData rmd;
	
	public StandardsReports(RequestMetaData rmd) {
		this.rmd = rmd;
	}
	MyDataTable mdt=null;
	//private static final Logger LOG = LoggerFactory.getLogger(StandardsReports.class);
	public JsonArray getStandardGraphData(int webmartID,String productName,int month,int year) throws Exception{
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		InsightDAO insightDao=null;
		JsonArrayBuilder jb=Json.createArrayBuilder();
		try{
		DynamicDatabase dd=new DynamicDatabase(webmartID);
		iTracker=1.0;
		insightDao=dd.getInsightDao();
		iTracker=2.0;
		String monthSum="";
		for (int i = 1; i <= month; i++) {
			monthSum=monthSum+"SUM("+InsightConstant.MONTH_ARRAY[i]+"_"+year+"_html+"+InsightConstant.MONTH_ARRAY[i]+"_"+year+"_pdf) as '"+InsightConstant.MONTH_ARRAY[i]+"'";
			if(i!=month){
				monthSum=monthSum+",";
			}
		}
		iTracker=3.0;
		String str="select "+monthSum+" from StandardsUsageByMonth";
		iTracker=4.0;
		mdt=insightDao.executeSelectQueryMDT(str);
		for (int i = 1; i <= month; i++) {
			jb.add(Integer.parseInt(mdt.getValue(1, i)));
		}
		
		}catch (Exception e) {
			rmd.exception("StandardsReports : getStandardGraphData : iTracker : "+iTracker+" : "+e.toString());
		}finally{
			if(insightDao!=null){
			insightDao.disconnect();
			}
		}
		return jb.build();
	}
	
	public JsonArray getAccountStandardGraphData(int webmartID,String productName,int month,int year,String institution) throws Exception{
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		InsightDAO insightDao=null;
		JsonArrayBuilder jb=Json.createArrayBuilder();
		try{
		DynamicDatabase dd=new DynamicDatabase(webmartID);
		insightDao=dd.getInsightDao();
		iTracker=1.0;
		String monthSum="";
		for (int i = 1; i <= month; i++) {
			monthSum=monthSum+"SUM("+InsightConstant.MONTH_ARRAY[i]+"_"+year+"_html+"+InsightConstant.MONTH_ARRAY[i]+"_"+year+"_pdf) as '"+InsightConstant.MONTH_ARRAY[i]+"'";
			if(i!=month){
				monthSum=monthSum+",";
			}
		}
		iTracker=2.0;
		String str="select "+monthSum+" from StandardsUsageByMonth where institution='"+institution+"'";
		iTracker=3.0;
		mdt=insightDao.executeSelectQueryMDT(str);
		for (int i = 1; i <= month; i++) {
			jb.add(Integer.parseInt(mdt.getValue(1, i)));
		}
		
		}catch (Exception e) {
			rmd.exception("StandardsReports : getAccountStandardGraphData : iTracker : "+iTracker+" : "+e.toString());
		}finally{
			if(insightDao!=null){
			insightDao.disconnect();
			}
		}
		return jb.build();
	}
}
