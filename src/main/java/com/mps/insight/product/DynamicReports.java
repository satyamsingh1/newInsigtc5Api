package com.mps.insight.product;

import javax.json.JsonObject;
import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.DynamicDatabase;

public class DynamicReports {
	RequestMetaData rmd;
	
	public DynamicReports(RequestMetaData rmd){
		this.rmd = rmd;
	}
	
	//private static final Logger LOG = LoggerFactory.getLogger(DynamicReports.class);
	
	public JsonObject getDynamicReportsList(int webmartID) {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		JsonObject tempObject=null;
		
		try {
			rmd.log("DynamicReports : getDynamicReportsList() : webmart_id=" + webmartID);
			iTracker=2.0;
			insightDao = rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT abbrev ,description,pr.status FROM ")
			.append("publisher_reports AS pr INNER JOIN reports_master AS rm ON ")
			.append("pr.report_id=rm.id WHERE pr.status=1 AND webmart_id='"+webmartID)
			.append("' ORDER BY  rm.category  DESC, abbrev ASC");
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			iTracker=5.0;
			tempObject=mdt.getJson();
			//rmd.log(" tempObject : "+tempObject.toString());
		} catch (Exception e) {
			rmd.exception("DynamicReports : getDynamicReportsList : iTracker : "+iTracker+" : "+e.toString());
			//LOG.info("Exception in getDynamicReportsList");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return tempObject;
	}
	
	public MyDataTable getDynamicReportJson(int webmartID,String dynamicQuery) {
		double iTracker=0.0;
		InsightDAO insightDao =null;
		MyDataTable mdt=null;
		//DynamicDatabase dd=new DynamicDatabase(webmartID);
		try {
			iTracker=1.0;
			insightDao = InsightDAO.getDynamicInstance(rmd.getWebmartCode());

			rmd.log("Dynamic DB Detail : " + insightDao.getSqlConnection().getMetaData().getDatabaseProductName());
			rmd.log("getDynamicReportJson : Query =" + dynamicQuery);
			iTracker=2.0;
			if(null!=dynamicQuery && dynamicQuery.length()>4){
			mdt = insightDao.executeSelectQueryMDT(dynamicQuery);
			}
		} catch (Exception e) {
			rmd.exception("DynamicReports : getDynamicReportJson : iTracker : "+iTracker+" : "+e.toString());
			//LOG.info("Exception in getDynamicReportsList : "+e.getMessage());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return mdt;
	}

}
