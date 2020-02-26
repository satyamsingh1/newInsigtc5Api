package com.mps.insight.product;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.exception.MyException;

public class LibraryReportsPubView {

	//private static final Logger LOG = LoggerFactory.getLogger(LibraryReportsPubView.class);
	InsightDAO insightDao = null;
	PublisherSettings pubsetting=null;
	private RequestMetaData rmd; 
	
	public LibraryReportsPubView(RequestMetaData rmd) throws MyException {
		this.rmd = rmd;
	}
	
	public JsonObject getLibReports(int webmartID) {
		MyDataTable mdt = null;
		MyDataTable mdt1 = null;
		// JsonObject tempObject = null;
		JsonObjectBuilder job = Json.createObjectBuilder();
		JsonObjectBuilder jobfinal = Json.createObjectBuilder();
		JsonArrayBuilder jab = Json.createArrayBuilder();
		int rowCount = 0;
		// int colCount = 0;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("getLiveMonth : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append(
					"SELECT pr.report_id AS ReportID,rm.category AS Category,rm.name AS `Report Type`,rm.description AS Description FROM publisher_reports pr ")
					.append("LEFT JOIN reports_master rm ON pr.report_id=rm.id LEFT JOIN `report_sections` rs ON rm.category=rs.section ")
					.append("WHERE pr.webmart_id=" + webmartID + " AND rs.category='USAGE_REPORTS' AND rs.webmart_id="
							+ webmartID + " GROUP BY pr.report_id, rm.category ,rm.name,rm.description  ORDER BY rm.category,pr.sort_order");
			StringBuilder stb2 = new StringBuilder();
			stb2.append(
					"SELECT pr.report_id AS ReportID,rm.category AS Category,rm.name AS `Report Type`,rm.description AS Description FROM publisher_reports pr ")
					.append("LEFT JOIN reports_master rm ON pr.report_id=rm.id LEFT JOIN `report_sections` rs ON pr.category=rs.section ")
					.append("WHERE pr.webmart_id=" + webmartID + " AND rs.category='USAGE_REPORTS' AND rs.webmart_id="
							+ webmartID + " GROUP BY pr.report_id, rm.category ,rm.name,rm.description  ORDER BY rm.category,pr.sort_order");

			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			rowCount = mdt.getRowCount();
			// colCount = mdt.getColumnCount();
			boolean check = false;

			mdt1 = insightDao.executeSelectQueryMDT(stb2.toString());
			for (int i = 1; i <= rowCount; i++) {
				for (int j = 1; j <= mdt1.getRowCount(); j++) {
					if (mdt.getValue(i, 1).equalsIgnoreCase(mdt1.getValue(j, 1))) {
						check = true;
					}
				}
				job.add(mdt.getColumnName(1), mdt.getValue(i, 1));
				job.add(mdt.getColumnName(2), mdt.getValue(i, 2));
				job.add(mdt.getColumnName(3), mdt.getValue(i, 3));
				job.add(mdt.getColumnName(4), mdt.getValue(i, 4));
				job.add("checked", check);
				check = false;
				jab.add(job);

			}
			jobfinal.add("data", jab);
			rmd.log(job.toString());
			// tempObject = mdt.getJsonRecords();
		} catch (Exception e) {
			rmd.log("Exception in getLibReports...");
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt = null;
		}

		return jobfinal.build();
	}

	public String updateLibreportsConfig(int webmartID, String reportids) {
		MyDataTable mdt = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			String[] reports = reportids.replace("\"", "").split(",");
			StringBuilder sb = new StringBuilder();

			sb.append("SELECT pr.report_id AS `reportID`,pr.category AS `category` FROM ").append(
					"publisher_reports pr WHERE pr.webmart_id="+webmartID+" AND ")
			.append("(pr.category LIKE '%COUNTER%' OR pr.category LIKE '%ADDITIONAL%')");

			mdt = insightDao.executeSelectQueryMDT(sb.toString());
			int rowCount = mdt.getRowCount();
			int checkstatus=0;
			//int colCount = mdt.getColumnCount();
			for (int i = 1; i <= rowCount; i++) {
				for (String report : reports) {
					if (Integer.parseInt(report) == Integer.parseInt(mdt.getValue(i, 1))) {
						checkstatus=1;
						if (mdt.getValue(i, 2).startsWith("V_")) {
							updatereportsConfig(webmartID, report, mdt.getValue(i, 2).replace("V_", ""));
						}
					}
				}
				if(checkstatus==0){
					if (mdt.getValue(i, 2).startsWith("V_")) {
						
						}else{
							String temp="V_"+mdt.getValue(i, 2);
							updatereportsConfig(webmartID,mdt.getValue(i, 1), temp);
						
						}
				}
				checkstatus=0;

			}

		} catch (Exception e) {
			rmd.exception("Exception in updatereportsConfig... ");
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/

			mdt = null;
		}

		return "success";
	}

	public void updatereportsConfig(int webmartID, String reportID, String category) {
		Long resultid = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder sb = new StringBuilder();

			sb.append("UPDATE publisher_reports pr SET pr.category='" + category + "' WHERE ")
					.append("pr.webmart_id=" + webmartID + " and pr.report_id='" + reportID + "'");

			resultid = insightDao.executeInsertUpdateQuery(sb.toString());

			rmd.log("updatereportsConfig Query =" + sb.toString() + " : Query : Success : " + resultid);

		} catch (Exception e) {
			rmd.exception("Exception in getFullTextDetail... ");
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
	}
}
