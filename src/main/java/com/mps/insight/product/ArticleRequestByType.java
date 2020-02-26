package com.mps.insight.product;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.DynamicDatabase;
import com.mps.insight.global.InsightConstant;

public class ArticleRequestByType {
	
	RequestMetaData rmd=null;
	
	//private static final Logger LOG = LoggerFactory.getLogger(ArticleRequestByType.class);
	public ArticleRequestByType(RequestMetaData rmd) {
		this.rmd = rmd;
	}
	
	MyDataTable mdt=null;

	public JsonArray getGraphData(int webmartID,String productName,int month,int year) throws Exception{
		double iTracker=0.0;
		InsightDAO insightDao=null;
		JsonArrayBuilder jb=Json.createArrayBuilder();
		try{
		String [] values=productName.split("~");
		String value="";
		if("ALL".equalsIgnoreCase(values[1])){
		}else{
			value=values[1];
		}
		DynamicDatabase dd=new DynamicDatabase(webmartID);
		iTracker=1.0;
		insightDao=dd.getInsightDao();
		String monthSum="";
		for (int i = 1; i <= month; i++) {
			monthSum=monthSum+"SUM("+InsightConstant.MONTH_ARRAY[i]+"_"+year+") as '"+InsightConstant.MONTH_ARRAY[i]+"'";
			if(i!=month){
				monthSum=monthSum+",";
			}
		}
		iTracker=2.0;
		String str="select "+monthSum+" from ArticleRequestsByType WHERE title LIKE '%"+value+"%'";
		iTracker=3.0;
		mdt=insightDao.executeSelectQueryMDT(str);
		iTracker=4.0;
		for (int i = 1; i <= month; i++) {
			jb.add(Integer.parseInt(mdt.getValue(1, i)));
		}
		
		}catch (Exception e) {
			rmd.exception("ArticleRequestByType : getGraphData : iTracker : "+iTracker+" : "+e.toString());
			//LOG.error("Exception in getPubLiveMonth "+e.getMessage());
		}finally{
			if(insightDao!=null){
			insightDao.disconnect();
			}
		}
		return jb.build();
	}
	
	public JsonArray getAccountGraphData(int webmartID,String productName,int month,int year,String institution) throws Exception{
		double iTracker=0.0;
		InsightDAO insightDao=null;
		JsonArrayBuilder jb=Json.createArrayBuilder();
		try{
		String [] values=productName.split("~");
		String value="";
		if("ALL".equalsIgnoreCase(values[1])){
		}else{
			value=values[1];
		}
		DynamicDatabase dd=new DynamicDatabase(webmartID);
		iTracker=1.0;
		insightDao=dd.getInsightDao();
		String monthSum="";
		for (int i = 1; i <= month; i++) {
			monthSum=monthSum+"SUM("+InsightConstant.MONTH_ARRAY[i]+"_"+year+") as '"+InsightConstant.MONTH_ARRAY[i]+"'";
			if(i!=month){
				monthSum=monthSum+",";
			}
		}
		iTracker=2.0;
		String str="select "+monthSum+" from ArticleRequestsByType WHERE title LIKE '%"+value+"%' and institution='"+institution+"'";
		iTracker=3.0;
		mdt=insightDao.executeSelectQueryMDT(str);
		
		for (int i = 1; i <= month; i++) {
			jb.add(Integer.parseInt(mdt.getValue(1, i)));
		}
		
		}catch (Exception e) {
			rmd.exception("ArticleRequestByType : getAccountGraphData : iTracker : "+iTracker+" : "+e.toString());
			//LOG.error("Exception in getPubLiveMonth "+e.getMessage());
		}finally{
			if(insightDao!=null){
			insightDao.disconnect();
			}
		}
		return jb.build();
	}
}
