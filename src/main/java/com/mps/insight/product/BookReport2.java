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

public class BookReport2 {
	@Context
	private HttpServletRequest servletRequest;
	RequestMetaData rmd;
	
	//private static final Logger LOG = LoggerFactory.getLogger(BookReport2.class);
	MyDataTable mdt=null;
	
	public BookReport2(RequestMetaData rmd) throws Exception {
		this.rmd = rmd;
	}
	
	public JsonArray getBookGraphData(int webmartID, String productName,int month,int year) throws Exception {
		double iTracker=0.0;
		JsonArrayBuilder jb = Json.createArrayBuilder();
		InsightDAO insightDao = null;
		try {
			DynamicDatabase dd = new DynamicDatabase(webmartID);
			iTracker=1.0;
			insightDao = dd.getInsightDao();
			String monthSum = "";
			for (int i = 1; i <= month; i++) {
				monthSum = monthSum + "SUM(" + InsightConstant.MONTH_ARRAY[i] + "_" + year + ") as '"
						+ InsightConstant.MONTH_ARRAY[i]+"'";
				if (i != month) {
					monthSum = monthSum + ",";
				}
			}
			iTracker=2.0;
			String str = "select " + monthSum + " from BookReport2";
			iTracker=3.0;
			mdt=insightDao.executeSelectQueryMDT(str);
			iTracker=4.0;
			for (int i = 1; i <= month; i++) {
				jb.add(Integer.parseInt(mdt.getValue(1, i)));
			}
			rmd.log(jb.toString());
		} catch (Exception e) {
			//LOG.error("Exception in Book Report "+e.getMessage());
			rmd.exception("BookReport2 : getBookGraphData : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
		}
		return jb.build();
	}
	
	public JsonArray getAccountBookGraphData(int webmartID, String productName,int month,int year,String institution) throws Exception {
		double iTracker=0.0;
		JsonArrayBuilder jb = Json.createArrayBuilder();
		InsightDAO insightDao = null;
		try {
			DynamicDatabase dd = new DynamicDatabase(webmartID);
			iTracker=1.0;
			insightDao = dd.getInsightDao();
			String monthSum = "";
			for (int i = 1; i <= month; i++) {
				monthSum = monthSum + "SUM(" + InsightConstant.MONTH_ARRAY[i] + "_" + year + ") as '"
						+ InsightConstant.MONTH_ARRAY[i]+"'";
				if (i != month) {
					monthSum = monthSum + ",";
				}
			}
			iTracker=2.0;
			String str = "select " + monthSum + " from BookReport2 where institution='"+institution+"'";
			iTracker=3.0;
			mdt=insightDao.executeSelectQueryMDT(str);
			iTracker=4.0;
			for (int i = 1; i <= month; i++) {
				jb.add(Integer.parseInt(mdt.getValue(1, i)));
			}
			rmd.log(jb.toString());
		} catch (Exception e) {
			rmd.exception("BookReport2 : getAccountBookGraphData : iTracker : "+iTracker+" : "+e.toString());
			//LOG.error("Exception in Book Report "+e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
		}
		return jb.build();
	}
}
