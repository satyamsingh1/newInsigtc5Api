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

public class ConferencesReports {
	@Context
	private HttpServletRequest servletRequest;
	RequestMetaData rmd;
	
	MyDataTable mdt=null;
	public ConferencesReports(RequestMetaData rmd) throws Exception {
		this.rmd = rmd;
	}
	
	//private static final Logger LOG = LoggerFactory.getLogger(ConferencesReports.class);
	public JsonArray getConferenceGraphData(int webmartID,String productName,int month,int year) throws Exception{
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		InsightDAO insightDao=null;
		JsonArrayBuilder jb=Json.createArrayBuilder();
		try{
		String [] values=productName.split("~");
		iTracker=1.0;
		if("ALL".equalsIgnoreCase(values[1])){
		}else{
			//value=values[1];
		}
		DynamicDatabase dd=new DynamicDatabase(webmartID);
		iTracker=2.0;
		insightDao=dd.getInsightDao();
		
		String monthSum="";
		for (int i = 1; i <= month; i++) {
			monthSum=monthSum+"SUM("+InsightConstant.MONTH_ARRAY[i]+"_"+year+") as '"+InsightConstant.MONTH_ARRAY[i]+"'";
			if(i!=month){
				monthSum=monthSum+",";
			}
		}
		iTracker=3.0;
		String str="select "+monthSum+" from ConferencesUsageByMonth ";
		iTracker=4.0;
		mdt=insightDao.executeSelectQueryMDT(str);
		for (int i = 1; i <= month; i++) {
			jb.add(Integer.parseInt(mdt.getValue(1, i)));
		}
		
		}catch (Exception e) {
			rmd.exception("ConferencesReports : getConferenceGraphData : iTracker : "+iTracker+" : "+e.toString());
			//LOG.error("Exception in Conference Report "+e.getMessage());
		}finally{
			if(insightDao!=null){
			insightDao.disconnect();
			}
		}
		return jb.build();
	}
}
