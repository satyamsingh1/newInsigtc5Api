package com.mps.insight.product;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.DynamicDatabase;
import com.mps.insight.global.InsightConstant;

public class PlatFormReport1 {
	//private static final Logger LOG = LoggerFactory.getLogger(PlatFormReport1.class);
	MyDataTable mdt=null;
	private RequestMetaData rmd; 
	public PlatFormReport1(RequestMetaData rmd) {
		this.rmd = rmd;
	}
	public JsonArray getPlatFormGraphData(int webmartID,String productName,int month,int year) throws Exception{
		InsightDAO insightDao=null;
		JsonArrayBuilder jb=Json.createArrayBuilder();
		String table="";
		try{
		String [] values=productName.split("~");
		String value="";
		if("ALL".equalsIgnoreCase(values[1])){
		}else{
			value=values[1];
		}
		DynamicDatabase dd=new DynamicDatabase(webmartID);
		insightDao=dd.getInsightDao();
		String monthSum="";
		for (int i = 1; i <= month; i++) {
			monthSum=monthSum+"SUM("+InsightConstant.MONTH_ARRAY[i]+"_"+year+") as '"+InsightConstant.MONTH_ARRAY[i]+"'";
			if(i!=month){
				monthSum=monthSum+",";
			}
		}
		if(productName.toUpperCase().startsWith("PLATFORM")){
			table=InsightConstant.PR1TABLE;
		}else if(productName.toUpperCase().startsWith("DATABASE")){
			table=InsightConstant.DB1TABLE;
		}
		
		String str="select "+monthSum+" from "+table+" WHERE userActivity LIKE '%"+value+"%'";
		mdt=insightDao.executeSelectQueryMDT(str);
		for (int i = 1; i <= month; i++) {
			jb.add(Integer.parseInt(mdt.getValue(1, i)));
		}
		
		}catch (Exception e) {
			rmd.exception("Exception while Graph  "+productName+" : "+e.getMessage());
		}finally{
			if(insightDao!=null){
			insightDao.disconnect();
			}
		}
		return jb.build();
	}
	
	public JsonArray getAccountPlatFormGraphData(int webmartID,String productName,int month,int year,String institution) throws Exception{
		InsightDAO insightDao=null;
		JsonArrayBuilder jb=Json.createArrayBuilder();
		try{
		String [] values=productName.split("~");
		String value="";
		String table="";
		if("ALL".equalsIgnoreCase(values[1])){
		}else{
			value=values[1];
		}
		DynamicDatabase dd=new DynamicDatabase(webmartID);
		insightDao=dd.getInsightDao();
		String monthSum="";
		for (int i = 1; i <= month; i++) {
			monthSum=monthSum+"SUM("+InsightConstant.MONTH_ARRAY[i]+"_"+year+") as '"+InsightConstant.MONTH_ARRAY[i]+"'";
			if(i!=month){
				monthSum=monthSum+",";
			}
		}
		if(productName.toUpperCase().startsWith("PLATFORM")){
			table=InsightConstant.PR1TABLE;
		}else if(productName.toUpperCase().startsWith("DATABASE")){
			table=InsightConstant.DB1TABLE;
		}
		
		String str="select "+monthSum+" from "+table+" WHERE userActivity LIKE '%"+value+"%' and institution='"+institution+"'";
		mdt=insightDao.executeSelectQueryMDT(str);
		for (int i = 1; i <= month; i++) {
			jb.add(Integer.parseInt(mdt.getValue(1, i)));
		}
		
		}catch (Exception e) {
			rmd.exception("Exception in  Graph "+productName+" : "+e.getMessage());
		}finally{
			if(insightDao!=null){
			insightDao.disconnect();
			}
		}
		return jb.build();
	}
}
