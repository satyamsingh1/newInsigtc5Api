package com.mps.insight.product;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.DynamicDatabase;
import com.mps.insight.global.InsightConstant;

public class JournalReport1 {
	private RequestMetaData rmd;
	//private static final Logger LOG = LoggerFactory.getLogger(JournalReport1.class);
	MyDataTable mdt=null;
	public JournalReport1(RequestMetaData rmd) throws MyException {
		this.rmd = rmd;
	}
	public JsonArray getJournalGraphData(int webmartID, String productName,int month,int year) throws Exception {
		JsonArrayBuilder jb = Json.createArrayBuilder();
		InsightDAO insightDao = null;
		try {
			DynamicDatabase dd = new DynamicDatabase(webmartID);
			insightDao = dd.getInsightDao();
			String monthSum = "";
			for (int i = 1; i <= month; i++) {
				monthSum = monthSum + "(SUM(" + InsightConstant.MONTH_ARRAY[i] + "_" + year + "_pdf)+ SUM("
						+ InsightConstant.MONTH_ARRAY[i] + "_" + year + "_html))as '" + InsightConstant.MONTH_ARRAY[i]+"'";
				if (i != month) {
					monthSum = monthSum + ",";
				}
			}
			String str = "select " + monthSum + " from JournalReport1";
			mdt=insightDao.executeSelectQueryMDT(str);
			for (int i = 1; i <= month; i++) {
				jb.add(Integer.parseInt(mdt.getValue(1, i)));
			}
			rmd.log(jb.toString());
		} catch (Exception e) {
			rmd.exception("Exception in JournalReport  "+e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
		}
		return jb.build();
	}
	
	public JsonArray getAccountJournalGraphData(int webmartID, String productName,int month,int year,String institution) throws Exception {
		JsonArrayBuilder jb = Json.createArrayBuilder();
		InsightDAO insightDao = null;
		try {
			DynamicDatabase dd = new DynamicDatabase(webmartID);
			insightDao = dd.getInsightDao();
			String monthSum = "";
			for (int i = 1; i <= month; i++) {
				monthSum = monthSum + "(SUM(" + InsightConstant.MONTH_ARRAY[i] + "_" + year + "_pdf)+ SUM("
						+ InsightConstant.MONTH_ARRAY[i] + "_" + year + "_html))as '" + InsightConstant.MONTH_ARRAY[i]+"'";
				if (i != month) {
					monthSum = monthSum + ",";
				}
			}
			String str = "select " + monthSum + " from JournalReport1 where institution='"+institution+"'";
			mdt=insightDao.executeSelectQueryMDT(str);
			for (int i = 1; i <= month; i++) {
				jb.add(Integer.parseInt(mdt.getValue(1, i)));
			}
			rmd.log(jb.toString());
		} catch (Exception e) {
			rmd.exception("Exception in JournalReport  "+e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
		}
		return jb.build();
	}
	
	public String getQuery(String accountCode, String report, String from, String to){
		
		return "";
	}
}
