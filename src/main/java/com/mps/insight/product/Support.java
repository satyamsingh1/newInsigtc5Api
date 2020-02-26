package com.mps.insight.product;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;

public class Support {
	private RequestMetaData rmd; 
	public Support(RequestMetaData rmd) {
		this.rmd = rmd;
	}

	MyDataTable mdt = null;
	InsightDAO insightDao = null;
	JsonObject jsonRecord = null;
	PublisherSettings pubsetting=null;
	
	private final String LOG_PROCESSING="log_processing";
	//private static final Logger LOG = LoggerFactory.getLogger(Support.class);
	SimpleDateFormat shownFormat = new SimpleDateFormat("dd-MMM-yyyy");

	  private Date getDate(String state, String token) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	        String[] result = state.split(";");
	        Date rptStartDate = null;
	        for (int i = 0; i <= result.length - 1; i++) {
	            if (result[i].startsWith(token)) {
	                int index = result[i].indexOf("::");
	                String strStartDate = result[i].substring(index + 2);
	                try {
	                    rptStartDate = dateFormat.parse(strStartDate);
	                } catch (ParseException e) {
	                	rmd.exception("Date cannot be parsed"+ e.toString());
	                } catch (Exception e) {
	                	rmd.exception("Unexpected exception occured"+e.toString());
	                }
	            }
	        }
	        return rptStartDate;
	    }
	
	public JsonArray generateSystemLog(int webmartId) {

		JsonArray jarray = null;
		rmd.log("generateSystemLog : Start");
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartId);
			insightDao =rmd.getInsightDao();
			StringBuilder query = new StringBuilder();
//			query.append("Select updated_at AS Date, updated_by AS User, action AS Action, state AS Description from audit where webmart_id ="+ webmartId+" AND " +
//					" action in (select action from audit_master where webmart_id="+ webmartId + " and event_type = 'external')" +
//					" order by updated_at DESC");##
			
			query.append("SELECT activity_date AS DATE, user_code AS USER, activity_type AS ACTION, state AS Description"
					+ " FROM c5_user_activity WHERE publisher = '"+ publisher_name +"'   ORDER BY activity_date DESC");
			
			mdt = insightDao.executeSelectQueryMDT(query.toString());

			int rowCount=mdt.getRowCount();
			rmd.log("generateSystemLog : Query executed Row Count :"+rowCount);
			jarray = mdt.getJsonData();
		} catch (Exception e) {
			rmd.log("Exception in generateSystemLog : Query ");
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}

		return jarray;
	}

	public JsonObject liveMonthByWebmartId(String webmartCode,String status) {
		rmd.log("liveMonthByWebmartId : Start "+status);
		
		String query = " select DATE_FORMAT(CONCAT(YEAR,'-',MAX(MONTH),'-','01'),'%b-%Y') as liveMonth "
				+ "from report_inventory "
				+ "where status = '"+status+"' "
				+ "and account_id is not null "
				+ "group by year order by year desc";
		//String publisher_name;
		try {
			//pubsetting=new PublisherSettings(rmd);
			//publisher_name=pubsetting.getPublisherCode(webmart);
			insightDao = InsightDAO.getInstance(webmartCode);
			mdt = insightDao.executeSelectQueryMDT(query.toString());

			jsonRecord = mdt.getJson();
		}catch (Exception e) {
				rmd.log("Exception in liveMonthByWebmartId : Query ");
			} finally {
				
				/*if (insightDao != null) {
					insightDao.disconnect();
				}
				insightDao = null;*/
			}
		rmd.log("liveMonthByWebmartId : End "+status);
		return jsonRecord;
	}

	public JsonObject getProcessingData(int webmartId) {
		rmd.log("getProcessingData : Start ");
		//MonthlyAuditDTO monthlyAuditDto = new MonthlyAuditDTO();
		List<String> processedData = findMonthYearByCategoryAndAction(LOG_PROCESSING, InsightConstant.PROCESSING_MONTH, webmartId);
		JsonObjectBuilder job=Json.createObjectBuilder();
		String processingMonth = "";
		String year = "";
		try {
			if(processedData != null)
			{
				processingMonth = processedData.get(0);
				year = processedData.get(1);
				//Set Processed date into monthlyAuditDto
				String processingDate = new DateFormatSymbols().getShortMonths()[Integer.parseInt(processingMonth)-1] + "-" + year;
				if (processingDate == null || "".equalsIgnoreCase(processingDate)) {
					//monthlyAuditDto.setProcessedDate("");
					job.add("Data being processed:", processingDate);
		        } else {
		        	job.add("Data being processed:", processingDate);
		        }
				
				String logState = findDetails(InsightConstant.CAT_LOG_PROCESSING, InsightConstant.ACTION_PROCESS, webmartId, processingMonth, year);
				job.add("Log processing started date:", shownFormat.format(getDate(logState, InsightConstant.START_DATE)));
				job.add("Estimated processing completion date:", shownFormat.format(getDate(logState, InsightConstant.END_DATE)));
				logState = findDetails(InsightConstant.CAT_REPORT_GENERATION, InsightConstant.ACTION_COUNTER_REPORTS, webmartId, processingMonth, year);
				job.add("Report generation started date:", shownFormat.format(getDate(logState, InsightConstant.START_DATE)));
				job.add("Estimate report completion date", shownFormat.format(getDate(logState, InsightConstant.END_DATE)));
				job.add("Today's date:", shownFormat.format(new Date()));
			}

		}catch (Exception e) {
			rmd.log("Exception in getProcessingData : Query ");
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		rmd.log("getProcessingData : End ");
		
		return job.build();
	}

	public JsonArray getEmailCategoriesByCategory(String category, int webmartID) {
		JsonArray jarray = null;
		rmd.log("getEmailCategoriesByCategory method : Start");
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder query = new StringBuilder();
			query.append("SELECT DISTINCT(email_code), email_category FROM email_categories WHERE webmart_id = "+webmartID+" AND email_category = '"+category+"'");
			
			mdt = insightDao.executeSelectQueryMDT(query.toString());
			//Read property file
			Properties prop = readProperties();
			for(int len = 1; len <= mdt.getRowCount(); len++) {
				mdt.updateData(len, "email_category" , prop.getProperty(mdt.getValue(len, 1)));
			}

			int rowCount=mdt.getRowCount();
			rmd.log("getEmailCategoriesByCategory method : Query executed Row Count :"+rowCount);
			jarray = mdt.getJsonData();
		} catch (Exception e) {
			rmd.log("Exception in getEmailCategoriesByCategory method : Query ");
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return jarray;
		
	}
	
	private List<String> findMonthYearByCategoryAndAction(String category,String action,int webmart) {
		List<String> resList = new ArrayList<>();
		rmd.log("findMonthYearByCategoryAndAction : Start ");
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmart);
			insightDao =rmd.getInsightDao();
			String query = "SELECT month, year FROM `system` WHERE action = '"+action+"' and category = '"+category+"' and webmart_id = "+webmart;
			mdt = insightDao.executeSelectQueryMDT(query.toString());

			int rowCount = mdt.getRowCount();
			if(rowCount > 0 ) {
				resList.add(mdt.getValue(1, 1));
				resList.add(mdt.getValue(1, 2));
			}
			
		}catch (Exception e) {
				rmd.log("Exception in findMonthYearByCategoryAndAction : Query ");
			} finally {
				
				/*if (insightDao != null) {
					insightDao.disconnect();
				}
				insightDao = null;*/
			}
		rmd.log("findMonthYearByCategoryAndAction : End ");
		return resList;
	}
	
	private String findDetails(String category,String action,int webmart, String month, String year) {
		String resState = null;
		rmd.log("findDetails : Start ");
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmart);
			insightDao =rmd.getInsightDao();
			StringBuilder query = new StringBuilder();
			query.append("SELECT state FROM `system` WHERE action = '"+action+"' and category = '"+category+"'");
			query.append(" and month = '"+month+"' and year = "+year+" and webmart_id = "+webmart);
			mdt = insightDao.executeSelectQueryMDT(query.toString());

			int rowCount = mdt.getRowCount();
			if(rowCount > 0) {
				resState = mdt.getValue(1, 1);
			}
			
		}catch (Exception e) {
				rmd.log("Exception in findDetails : Query ");
			} finally {
				
				/*if (insightDao != null) {
					insightDao.disconnect();
				}
				insightDao = null;*/
			}
		rmd.log("findDetails : End ");
		return resState;
	}
	
	private Properties readProperties() {
		
		Properties prop = new Properties();
		String propFileName = "config.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		try {
			if (inputStream != null) {
				prop.load(inputStream);
				return prop;
			} 
		}catch (IOException e) {
			rmd.log("Exception in reading property : "+e.getMessage());
		}
		return prop;
	}
	
}
