package com.mps.insight.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.global.DynamicDatabase;
import com.mps.insight.global.TableMapper;
import com.mps.sushi.json.format.DateRange;
import com.mps.sushi.json.format.Instance;
import com.mps.sushi.json.format.Performance;
import com.mps.sushi.json.format.ReportHeader;
import com.mps.sushi.json.format.ReportItems;
import com.mps.sushi.json.format.SushiConstants;
import com.mps.sushi.json.format.SushiException;
import com.mps.sushi.json.format.SushiReport;

public class SushiReportsImpl {
	private static final Logger LOG = LoggerFactory.getLogger(SushiReportsImpl.class);	
	public MyDataTable getReportsData(int webmartID, String institutionId, String reportName) throws Exception{
		DynamicDatabase dd = null;
		InsightDAO insightDao = null;
        MyDataTable mdt = null;
        StringBuilder stb = null;
        String tablename=TableMapper.TABALE.get("tr_master_table");
		try {	
			dd = new DynamicDatabase(webmartID);
			insightDao = dd.getInsightDao();
			LOG.info("getReportsData : webmart_id=" + webmartID);
			stb = new StringBuilder();
			if(reportName.equalsIgnoreCase("TR")){
				stb.append("SELECT * FROM `"+tablename+"` WHERE institution_id='" + institutionId + "' AND Data_Type='Journal' AND Title_ID != '-' AND Title_ID != '' AND Title != '' ")
				.append(" ORDER BY Title, Publisher, Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("TR_J1")){
				stb.append("SELECT * FROM `"+tablename+"` WHERE institution_id='" + institutionId + "' AND Data_Type='Journal' AND Title_ID != '-' AND Title_ID != '' AND Title != '' ")
				.append(" AND Access_Type = 'Controlled' AND Metric_Type IN ('Total_Item_Requests', 'Unique_Item_Requests')")
				.append(" AND Access_Method = 'Regular' ORDER BY Title, Publisher, Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("TR_J2")){
				stb.append("SELECT * FROM `"+tablename+"` WHERE institution_id='" + institutionId + "' AND Data_Type='Journal' AND Title_ID != '-'  AND Title_ID != '' AND Title != '' ")
				.append(" AND Metric_Type IN ('Limit_Exceeded', 'No_License') ")
				.append(" AND Access_Method = 'Regular' ORDER BY Title, Publisher, Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("TR_J3")){
				stb.append("SELECT * FROM `"+tablename+"` WHERE institution_id='" + institutionId + "' AND Data_Type='Journal' AND Title_ID != '-'  AND Title_ID != '' AND Title != '' ")
				.append(" AND Metric_Type IN ('Total_Item_Investigations', 'Total_Item_Requests', 'Unique_Item_Investigations', 'Unique_Item_Requests')")
				.append(" AND Access_Method = 'Regular' ORDER BY Title, Publisher, Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("TR_J4")){
				stb.append("SELECT * FROM `"+tablename+"` WHERE institution_id='" + institutionId + "' AND Data_Type='Journal' AND Title_ID != '-' ")
				.append(" AND Access_Type = 'Controlled' AND Metric_Type IN ('Total_Item_Requests', 'Unique_Item_Requests')")
				.append(" AND Access_Method = 'Regular' ORDER BY Title, Publisher, Platform, Metric_Type");			
			}
			/*else if(reportName.equalsIgnoreCase("TR_J4")){
			stb.append("SELECT * FROM `ir_master` WHERE institution_id='" + institutionId + "' AND Data_Type='Journal' AND Title_ID != '-' AND Title_ID != '' ")
			.append(" AND Access_Type = 'Controlled' AND Metric_Type IN ('Total_Item_Requests', 'Unique_Item_Requests')")
			.append(" AND Access_Method = 'Regular' ORDER BY Parent_Title, Item, YOP, Publisher, Platform, Metric_Type");			
			}*/
			else if(reportName.equalsIgnoreCase("TR_B1")){
				stb.append("SELECT * FROM `"+tablename+"` WHERE institution_id='" + institutionId + "' AND Data_Type='Book' AND Title_ID != '-'  AND Title_ID != '' AND Title != '' ")
				.append(" AND Access_Type = 'Controlled' AND Metric_Type IN ('Total_Item_Requests', 'Unique_Item_Requests')")
				.append(" AND Access_Method = 'Regular' ORDER BY Title, Publisher, Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("TR_B2")){
				stb.append("SELECT * FROM `"+tablename+"` WHERE institution_id='" + institutionId + "' AND Data_Type='Book' AND Title_ID != '-'  AND Title_ID != '' AND Title != '' ")
				.append(" AND Metric_Type IN ('Limit_Exceeded', 'No_License')")
				.append(" AND Access_Method = 'Regular' ORDER BY Title, Publisher, Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("TR_B3")){
				stb.append("SELECT * FROM `"+tablename+"` WHERE institution_id='" + institutionId + "' AND Data_Type='Book' AND Title_ID != '-'  AND Title_ID != '' AND Title != '' ")
				.append(" AND Metric_Type IN ('Total_Item_Investigations', 'Total_Item_Requests', 'Unique_Item_Investigations', 'Unique_Item_Requests', 'Unique_Title_Investigations', 'Unique_Title_Requests')")
				.append(" AND Access_Method = 'Regular' ORDER BY Title, Publisher, Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("IR")){
				stb.append("SELECT * FROM `ir_master` WHERE institution_id='" + institutionId + "' AND (Data_Type='Journal' OR Data_Type='Article' OR Data_Type='Multimedia')  AND Title_ID != '-' AND Title_ID != '' AND Item_ID != '' AND Item_ID != '-'  AND Item != '' ")
				.append(" ORDER BY Item, Publisher, Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("IR_A1")){
				stb.append("SELECT * FROM `ir_master` WHERE institution_id='" + institutionId + "' AND (Data_Type='Article' OR (Data_Type='Journal' AND Section_Type = 'Article')) AND Title_ID != '-' AND Title_ID != '' AND  Item_ID != '' AND Item_ID != '-'  AND Item != '' ")
				.append(" AND Metric_Type IN ('Total_Item_Requests')")
				.append(" AND Access_Method = 'Regular' ORDER BY Item, Publisher, Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("IR_M1")){
				stb.append("SELECT * FROM `ir_master` WHERE institution_id='" + institutionId + "' AND Data_Type='Multimedia' AND Title_ID != '-' AND Title_ID != '' AND  Item_ID != '' AND Item_ID != '-'  AND Item != '' ")
				.append(" AND Metric_Type IN ('Total_Item_Requests')")
				.append(" AND Access_Method = 'Regular' ORDER BY Item, Publisher, Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("PR")){
				stb.append("SELECT * FROM `pr_master` WHERE institution_id='" + institutionId + "' AND Platform != '' ")
				.append(" ORDER BY Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("PR_P1")){
				stb.append("SELECT * FROM `pr_master` WHERE institution_id='" + institutionId + "' AND Platform != '' ")
				.append(" AND Metric_Type IN ('Searches_Platform', 'Total_Item_Requests', 'Unique_Item_Requests', 'Unique_Title_Requests')")
				.append(" AND Access_Method = 'Regular' ORDER BY Platform, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("DR")){
				stb.append("SELECT * FROM `"+TableMapper.TABALE.get("dr_master_table")+"` dr WHERE institution_id='" + institutionId + "' AND Title_ID != ''  AND `Database` != '' ")
				.append(" ORDER BY dr.Database, Platform, Publisher, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("DR_D1")){
				stb.append("SELECT * FROM `"+TableMapper.TABALE.get("dr_master_table")+"` dr WHERE institution_id='" + institutionId + "' AND Title_ID != ''  AND `Database` != '' ")
				.append(" AND Access_Type = 'Controlled' AND Metric_Type IN ('Searches_Automated', 'Searches_Federated', 'Searches_Regular', 'Total_Item_Investigations', 'Total_Item_Requests')")
				.append(" AND Access_Method = 'Regular' ORDER BY dr.Database, Platform, Publisher, Metric_Type");			
			}
			else if(reportName.equalsIgnoreCase("DR_D2")){
				stb.append("SELECT * FROM `"+TableMapper.TABALE.get("dr_master_table")+"` dr WHERE institution_id='" + institutionId + "' AND Title_ID != ''  AND `Database` != '' ")
				.append(" AND Metric_Type IN ('Limit_Exceeded', 'No_License')")
				.append(" AND Access_Method = 'Regular' ORDER BY dr.Database, Platform, Publisher, Metric_Type");			
			}
			LOG.info("Query for "+ reportName + " :: " +stb.toString());
	        mdt = insightDao.executeSelectQueryMDT(stb.toString());	        
		} catch (Exception e) {
			LOG.info("Exception in getReportsData");
		} finally {			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;
		}		
		return mdt;
	}
	
	public MyDataTable authorise(String hashCode,String publisher) throws Exception{
		InsightDAO insightDao = null;
		MyDataTable mdt=null;		
		try {
			insightDao = InsightDAO.getInstance(publisher);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT api_key, client_id, requester_id, customer_id FROM `sushi_accounts` where api_key = '"+ hashCode +"'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
		} catch (Exception e) {
			LOG.info("Exception in authorize");
		} finally {			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;
		}
		
		return mdt;
	}
	
	public MyDataTable authenticate(String hashCode,String publisher) throws Exception{
		InsightDAO insightDao = null;
		MyDataTable mdt=null;		
		
		try {
			insightDao = InsightDAO.getInstance(publisher);
			StringBuilder stb = new StringBuilder();
			//stb.append("SELECT id as user_id FROM `"+TableMapper.TABALE.get("user_table")+"` where api_key = '"+ hashCode +"'");
			/*stb.append("SELECT um.id, um.api_key, uam.webmart_id, uam.user_id , uam.account_id FROM "+TableMapper.TABALE.get("user_table")+" um, "
					+ ""+TableMapper.TABALE.get("user_account_table")+" uam WHERE um.id = uam.user_id AND um.api_key != '' "
					+ "AND um.api_key = '"+hashCode+"'");*/			
			stb.append("SELECT * FROM "+TableMapper.TABALE.get("user_table")+" WHERE api_key='"+hashCode+"'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
		} catch (Exception e) {
			LOG.info("Exception in authenticate");
		} finally {			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;
		}		
		return mdt;
	}
	
	public MyDataTable validate(String customerId, String publisher, int userId) throws Exception{
		InsightDAO insightDao = null;
		MyDataTable mdt=null;		
		
		try {
			insightDao = InsightDAO.getInstance(publisher);
			StringBuilder stb = new StringBuilder();
			//stb.append("SELECT id as user_id FROM `"+TableMapper.TABALE.get("user_table")+"` where api_key = '"+ hashCode +"'");
			stb.append("SELECT user_id, account_id FROM `"+TableMapper.TABALE.get("user_account_table")+"` WHERE account_id='"+customerId+"' AND user_id="+userId +" AND status = 1");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
		} catch (Exception e) {
			LOG.info("Exception in validate() :: "+e);
		} finally {			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;
		}
		
		return mdt;
	}
	
	public MyDataTable getAccountName(String customerId, String publisher) throws Exception{
		InsightDAO insightDao = null;
		MyDataTable mdt=null;		
		
		try {
			insightDao = InsightDAO.getInstance(publisher);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT name FROM `accounts` WHERE code='"+customerId+"'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
		} catch (Exception e) {
			LOG.info("Exception in validate() :: "+e);
		} finally {			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;
		}
		
		return mdt;
	}
	
	
	public MyDataTable getReportsInfo(int clientID, String institutionId) throws Exception{
		DynamicDatabase dd = null;
		InsightDAO insightDao = null;
        MyDataTable mdt = null;
        StringBuilder stb = null;
		try {	
			dd = new DynamicDatabase(clientID);
			insightDao = dd.getInsightDao();
			LOG.info("getReportsData : webmart_id=" + clientID);
			stb = new StringBuilder();
			stb.append("SELECT report_code, report_name, report_description  FROM `institution_reports_statistics` WHERE Institution_ID='" + institutionId + "' AND report_type='COUNTER' ");				
	        mdt = insightDao.executeSelectQueryMDT(stb.toString());	        
		} catch (Exception e) {
			LOG.info("Exception in getReportsData");
		} finally {			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;
		}		
		return mdt;
	}
	
	public MyDataTable getStatusInfo(int clientID, String institutionId) throws Exception{
		DynamicDatabase dd = null;
		InsightDAO insightDao = null;
        MyDataTable mdt = null;
        StringBuilder stb = null;
		try {	
			dd = new DynamicDatabase(clientID);
			insightDao = dd.getInsightDao();
			LOG.info("getReportsData : webmart_id=" + clientID);
			stb = new StringBuilder();
			stb.append("SELECT *  FROM `sushi_service_status` WHERE institution_id='" + institutionId + "' AND client_id = " + clientID);				
	        mdt = insightDao.executeSelectQueryMDT(stb.toString());	        
		} catch (Exception e) {
			LOG.info("Exception in getStatusInfo");
		} finally {			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;
		}		
		return mdt;
	}
	
	public SushiReport getDataFromMDT(MyDataTable mdt, String customerId, String instID, String beginDate, String endDate, String reportId, String reportName, String pub, SushiException sushiException){			
		SushiReport report = new SushiReport();
		ReportHeader reportHeader = new ReportHeader();
		Map<String, ReportItems> reportItemsList = new LinkedHashMap<>();
		String instName = "";
		int release = 5;		
		ReportItems reportItems = null;	
        Map<String, String> itemId = null;
        Map<String, String> itemContributor = null;
        Map<String, String> itemAttributes = null;
        Map<String, String> itemDates = null;
        Map<String, String> parentItemId = null;
        Map<String, String> parentItemContributor = null;
        Map<String, String> parentItemAttributes = null;
        Map<String, String> parentItemDates = null;
        Map<String, String> componentItemId = null;
        Map<String, String> componentItemContributor = null;
        Map<String, String> componentItemAttributes = null;
        Map<String, String> componentItemDates = null;
        Map<String, String> publisherId = null;
        Map<String, String> instId = null;
        String publisher = "";
        Calendar calendar;
        int daysInMonth;
        Instance instance = null;
        DateRange dateRange = null;
        List<Instance> instanceList = null;
        Performance performance = null;
        List<Performance> performanceList = null;
        //SushiException sushiException = new SushiException();
        String prevTitle = "-$-";
        String prevPlatform = "-$-";
        String prevPublisher = "-$-";
        String currTitle = "-$-";
        String currPlatform = "-$-";
        String currDatabase = "-$-";
        String prevDatabase = "-$-";
        String currPublisher = "-$-";
        String currParentTitle = "-$-";
        String currArticleId = "-$-";
        String prevArticleId = "-$-";
        String prevParentTitle = "-$-";
        //String currYear = "";
        //String prevYear = "";
        String addZero = "";
        int startMonth = 0;
        int endMonth = 0;
        int startYear = 0;
        int endYear = 0;
        int startDay = 0;
        int endDay = 0;
        if(beginDate != null && endDate != null){
        	if((beginDate.split("-").length == 3 && endDate.split("-").length == 3) || (beginDate.split("-").length == 2 && endDate.split("-").length == 2)){
        		String[] startDate = beginDate.split("-");
        		String[] finishDate = endDate.split("-");
        		startMonth = Integer.parseInt(startDate[1]);
        		startYear = Integer.parseInt(startDate[0]);
        		endMonth = Integer.parseInt(finishDate[1]);	        
		        endYear = Integer.parseInt(finishDate[0]);
		        if(finishDate.length == 3)
		        	endDay =  Integer.parseInt(finishDate[2]);
		        intializeHashTable(endYear);
		        if((startYear == endYear && startMonth > endMonth) || startYear > endYear || (finishDate.length == 3 && numberOfDays.get(endMonth-1) != endDay)){
		        	if(sushiException == null){
	        			sushiException = new SushiException();
						sushiException.setCode("3020");
						sushiException.setSeverity("Error");
						sushiException.setMessage(SushiConstants.E3020);
						sushiException.setHelpUrl("string");
						
						//sushiException.setData(SushiConstants.E3020_DATA);
						if(finishDate.length == 3 && numberOfDays.get(endMonth-1) != endDay)
							sushiException.setData("End Date '" + endDate + "' is not a valid date format. Date should be " + endYear + "-" + (endMonth < 10 ? "0" + endMonth : endMonth) + "-" + numberOfDays.get(endMonth-1));
						else
							sushiException.setData("End Date should be greater than Begin Date");
	        		}
		        }
        	}
        	else{
        		if(sushiException == null){
        			sushiException = new SushiException();
					sushiException.setCode("3020");
					sushiException.setSeverity("Error");
					sushiException.setMessage(SushiConstants.E3020);
					sushiException.setHelpUrl("string");
					sushiException.setData("Date should be in YYYY-MM-DD or YYYY-MM format");
        		}
        	}
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try{
        	if(sushiException == null){
				if(mdt.getRowCount() != 0){	        	
					for(int rowNo = 1; rowNo <= mdt.getRowCount(); rowNo++)	{        	
			        	performanceList = new ArrayList<>();
			        	if(reportId.toUpperCase().startsWith("IR")){
			        		currTitle = mdt.getValue(rowNo, SushiConstants.ITEM).trim();
			        		currParentTitle = mdt.getValue(rowNo, "Parent_Title").trim();
			        		currArticleId = mdt.getValue(rowNo, "Item_ID").trim();
			        	}
			        	else if(reportId.toUpperCase().startsWith("DR"))
			        		currDatabase = mdt.getValue(rowNo, "Database").trim();
			        	/*else if(reportId.toUpperCase().equalsIgnoreCase("TR_J4")){
			        		if(currTitle.equalsIgnoreCase("The Analyst"))
			        			System.out.println("Here in error");
			        		currTitle = mdt.getValue(rowNo, "Parent_Title").trim();
			        		currYear = mdt.getValue(rowNo, "YOP").trim();
			        	}*/
			        	else if(!reportId.toUpperCase().startsWith("PR") && !reportId.toUpperCase().startsWith("DR"))
			        		currTitle = mdt.getValue(rowNo, SushiConstants.TITLE).trim();
			        	currPlatform = mdt.getValue(rowNo, SushiConstants.PLATFORM).trim();
			        	if(!reportId.toUpperCase().startsWith("PR"))
			        		 currPublisher = mdt.getValue(rowNo, SushiConstants.PUBLISHER).trim();		        	
			        	//if(currDatabase.equalsIgnoreCase(prevDatabase) && currParentTitle.equalsIgnoreCase(prevParentTitle) && currTitle.equalsIgnoreCase(prevTitle) && currPlatform.equalsIgnoreCase(prevPlatform) && currPublisher.equalsIgnoreCase(prevPublisher) && currArticleId.equalsIgnoreCase(prevArticleId) && (currYear.equalsIgnoreCase(prevYear) )){
			        	if(currDatabase.equalsIgnoreCase(prevDatabase) && currParentTitle.equalsIgnoreCase(prevParentTitle) && currTitle.equalsIgnoreCase(prevTitle) && currPlatform.equalsIgnoreCase(prevPlatform) && currPublisher.equalsIgnoreCase(prevPublisher) && currArticleId.equalsIgnoreCase(prevArticleId)){	
			        		List<Performance> performanceStats = null;
			        		if(reportId.toUpperCase().startsWith("PR"))
			        			performanceStats = reportItemsList.get(currPlatform).getPerformance();
			        		else if(reportId.toUpperCase().startsWith("DR"))
			        			performanceStats = reportItemsList.get(currDatabase).getPerformance();
			        		else if(reportId.toUpperCase().startsWith("IR"))
			        			performanceStats = reportItemsList.get(currTitle + currParentTitle + currArticleId).getPerformance();
			        		/*else if(reportId.toUpperCase().equalsIgnoreCase("TR_J4"))
			        			performanceStats = reportItemsList.get(currTitle+currYear).getPerformance();
			        		*/else	
			        			performanceStats = reportItemsList.get(currTitle).getPerformance();
			        		int found = 0;
			        		for(int i=startMonth;i<=endMonth;i++){			            		
			            		calendar = new GregorianCalendar(endYear, i-1, 1);
			    				daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			            		instance = new Instance();
			            		dateRange = new DateRange();
			            		performance = new Performance();
			            		if(i < 10)
			            			addZero = "0";
			            		else
			            			addZero="";	
			            		for(int k=0;k<performanceStats.size();k++){
			            			if(performanceStats.get(k).getPeriod().getBeginDate().equals(endYear + "-" + addZero + i + "-01")){
			            				instance.setMetricType(mdt.getValue(rowNo, SushiConstants.METRIC_TYPE));
			    	            		instance.setCount(Integer.parseInt(mdt.getValue(rowNo, "M_" + endYear + addZero + i)));
			    	            		performanceStats.get(k).getInstance().add(instance);
			    	            		if(reportId.toUpperCase().startsWith("PR"))
			    	            			reportItemsList.get(currPlatform).setPerformance(performanceStats);
			    	            		else if(reportId.toUpperCase().startsWith("DR"))
			    	            			reportItemsList.get(currDatabase).setPerformance(performanceStats);
			    	            		else if(reportId.toUpperCase().startsWith("IR"))
						        			reportItemsList.get(currTitle + currParentTitle + currArticleId).setPerformance(performanceStats);
			    	            		/*else if(reportId.toUpperCase().equalsIgnoreCase("TR_J4"))
			    	            			reportItemsList.get(currTitle+currYear).setPerformance(performanceStats);*/
			    	            		else
			    	            			reportItemsList.get(currTitle).setPerformance(performanceStats);
			    	            		found = 1;
			            			}
			            		}	            			
			            		if(found == 0){
				            		dateRange.setBeginDate(endYear + "-"+ addZero + i + "-01");
				            		dateRange.setEndDate(endYear + "-" + addZero + i + "-" + daysInMonth);			            			
				            		instance.setMetricType(mdt.getValue(rowNo, SushiConstants.METRIC_TYPE));
				            		instance.setCount(Integer.parseInt(mdt.getValue(rowNo, "M_" + endYear + addZero + i)));		            			
				            		instanceList = new ArrayList<>();
				            		instanceList.add(instance);
					            	performance.setInstance(instanceList);
					            	performance.setPeriod(dateRange);
					            	if(reportId.toUpperCase().startsWith("PR"))
					            		reportItemsList.get(currPlatform).getPerformance().add(performance);	
					            	else if(reportId.toUpperCase().startsWith("DR"))
		    	            			reportItemsList.get(currDatabase).setPerformance(performanceStats);
					            	else if(reportId.toUpperCase().startsWith("IR"))
					        			reportItemsList.get(currTitle + currParentTitle + currArticleId).setPerformance(performanceStats);
/*					            	else if(reportId.toUpperCase().startsWith("TR_J4"))
					        			reportItemsList.get(currTitle + currYear).setPerformance(performanceStats);
*/					            	else
					            		reportItemsList.get(currTitle).setPerformance(performanceStats);
			            		}          		
			            	}	
			        	}
			        	else{
			        		prevDatabase = currDatabase;
			        		prevTitle = currTitle;
				        	prevPlatform = currPlatform;
				        	prevPublisher = currPublisher;
				        	prevParentTitle = currParentTitle;
				        	prevArticleId = currArticleId;
				        	//prevYear = currYear;
			        		reportItems = new ReportItems();		        		
				        	itemId = new LinkedHashMap<>();
				        	publisherId = new LinkedHashMap<>();
				        	if(!reportId.toUpperCase().startsWith("PR") && !reportId.toUpperCase().startsWith("DR")){
				        		if(!("").equals(mdt.getValue(rowNo, "DOI")))
				        			itemId.put("DOI", mdt.getValue(rowNo, "DOI"));
				        		if(!("").equals(mdt.getValue(rowNo, "Proprietary_ID")))
				        			itemId.put("Proprietary_ID", mdt.getValue(rowNo, "Proprietary_ID"));
				        		if(!("").equals(mdt.getValue(rowNo, "ISBN")))
				        			itemId.put("ISBN", mdt.getValue(rowNo, "ISBN"));
				        		if(!("").equals(mdt.getValue(rowNo, "Print_ISSN")))
				        			itemId.put("Print_ISSN", mdt.getValue(rowNo, "Print_ISSN"));
				        		if(!("").equals(mdt.getValue(rowNo, "Online_ISSN")))
				        			itemId.put("Online_ISSN", mdt.getValue(rowNo, "Online_ISSN"));
				        		if(!("").equals(mdt.getValue(rowNo, "URI")))
				        			itemId.put("URI", mdt.getValue(rowNo, "URI"));				        	
					        	
				        	}
				        	if(!reportId.toUpperCase().startsWith("PR")){
				        		publisherId.put("Type", pub);
					        	publisherId.put("Value", mdt.getValue(rowNo, SushiConstants.INSTITUTION_ID));
				        	}
				        	reportItems.setItemId(itemId);
				        	
				        	instId = new LinkedHashMap<>();
				        	instId.put(pub, mdt.getValue(rowNo, SushiConstants.INSTITUTION_ID));
				        	if(!reportId.toUpperCase().startsWith("PR") && !("").equalsIgnoreCase(mdt.getValue(rowNo, "ISNI")))
				        		instId.put("ISNI",mdt.getValue(rowNo, "ISNI"));
				        	/*instId.put("Type", pub);
				        	instId.put("Value", mdt.getValue(rowNo, SushiConstants.INSTITUTION_ID));
				        	instId.put("Type", "ISNI");
				        	instId.put("Value", mdt.getValue(rowNo, "ISNI"));*/
				        	
				        	for(int i=startMonth;i<=endMonth;i++){
			            		instanceList = new ArrayList<>();
			            		performance = new Performance();
			            		calendar = new GregorianCalendar(endYear, i-1, 1);
			    				daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			            		instance = new Instance();
			            		dateRange = new DateRange();
			            		if(i<10)
			            			addZero = "0";
			            		else
			            			addZero="";			            		
			            		dateRange.setBeginDate(endYear + "-" + addZero + i + "-01");
			            		dateRange.setEndDate(endYear + "-" + addZero + i + "-"+daysInMonth);		            			
			            		instance.setMetricType(mdt.getValue(rowNo, SushiConstants.METRIC_TYPE));
			            		instance.setCount(Integer.parseInt(mdt.getValue(rowNo, "M_"+ endYear + addZero + i)));
			            		instanceList.add(instance);
			            		performance.setInstance(instanceList);
			            		performance.setPeriod(dateRange);
			            		performanceList.add(performance);
			            	}	
				        	
				        	if(reportId.toUpperCase().startsWith("IR")){
				        		itemContributor = new LinkedHashMap<>();	
				                String[] itemContributors = mdt.getValue(rowNo, "Authors").split(",");
				                for(String contributor : itemContributors)
				                	itemContributor.put("Name", contributor);
				                /*itemContributors = mdt.getValue(rowNo, "ORCID").split(",");
				                for(String contributor : itemContributors)
				                	itemContributor.put("ORCID", contributor);
				                
				                itemContributors = mdt.getValue(rowNo, "ISNI").split(",");
				                for(String contributor : itemContributors)
				                	itemContributor.put("ISNI", contributor);*/
				                reportItems.setItemContributor(itemContributor);
				                
				                itemDates = new LinkedHashMap<>();
				                itemDates.put("Pub_Date", mdt.getValue(rowNo, "Publication_Date"));
				                /*itemDates.put("First_Accessed_Online", mdt.getValue(rowNo, "First_Accessed_Online"));
				                itemDates.put("Proprietary", mdt.getValue(rowNo, "Proprietary"));*/
				                reportItems.setItemDates(itemDates);
				                
				                itemAttributes = new LinkedHashMap<>();
				                itemAttributes.put("Article_Version", mdt.getValue(rowNo, "Article_Version"));
				                /*itemAttributes.put("Article_Type", mdt.getValue(rowNo, "Article_Type"));
				                itemAttributes.put("Qualification_Name", mdt.getValue(rowNo, "Qualification_Name"));
				                itemAttributes.put("Qualification_Level", mdt.getValue(rowNo, "Qualification_Level"));
				                itemAttributes.put("Proprietary", mdt.getValue(rowNo, "Proprietary"));*/
				                reportItems.setItemAttributes(itemAttributes);
				                
				                // parent block
				                parentItemId = new LinkedHashMap<>();	
				                if(!("").equals(mdt.getValue(rowNo, "Parent_DOI")))
				                	parentItemId.put("DOI", mdt.getValue(rowNo, "Parent_DOI"));
				                if(!("").equals(mdt.getValue(rowNo, "Parent_Proprietary_ID")))
				                	parentItemId.put("Proprietary_ID", mdt.getValue(rowNo, "Parent_Proprietary_ID"));
				                if(!("").equals(mdt.getValue(rowNo, "Parent_ISBN")))
				                	parentItemId.put("ISBN", mdt.getValue(rowNo, "Parent_ISBN"));
				                if(!("").equals(mdt.getValue(rowNo, "Parent_Print_ISSN")))
				                	parentItemId.put("Print_ISSN", mdt.getValue(rowNo, "Parent_Print_ISSN"));
				                if(!("").equals(mdt.getValue(rowNo, "Parent_Online_ISSN")))
				                	parentItemId.put("Online_ISSN", mdt.getValue(rowNo, "Parent_Online_ISSN"));
				                if(!("").equals(mdt.getValue(rowNo, "Parent_URI")))
				                	parentItemId.put("URI", mdt.getValue(rowNo, "Parent_URI"));
				                
				                reportItems.setItemParent(new ReportItems());
				                reportItems.getItemParent().setTitle(currParentTitle);
				                reportItems.getItemParent().setItemId(parentItemId);
				                parentItemContributor = new LinkedHashMap<>();
				                itemContributors = mdt.getValue(rowNo, "Parent_Authors").split(",");
				                for(String contributor : itemContributors)
				                	parentItemContributor.put("Name", contributor);
				                reportItems.getItemParent().setItemContributor(parentItemContributor);
				                
				                parentItemDates = new LinkedHashMap<>();
				                parentItemDates.put("Pub_Date", mdt.getValue(rowNo, "Parent_Publication_Date"));
				                reportItems.getItemParent().setItemDates(parentItemDates);
				                
				                parentItemAttributes = new LinkedHashMap<>();
				                parentItemAttributes.put("Article_Version", mdt.getValue(rowNo, "Parent_Article_Version"));
				                reportItems.getItemParent().setItemAttributes(parentItemAttributes);
				                
				                reportItems.getItemParent().setPublisher(currPublisher);
				                reportItems.getItemParent().setPublisherId(publisherId);
				                reportItems.getItemParent().setDataType(mdt.getValue(rowNo, "Parent_Data_Type"));
				                
				                //component block
				                reportItems.setItemComponent(new ReportItems());
				                componentItemId = new LinkedHashMap<>();
				                if(!("").equals(mdt.getValue(rowNo, "Component_DOI")))
				                	componentItemId.put("DOI", mdt.getValue(rowNo, "Component_DOI"));
				                if(!("").equals(mdt.getValue(rowNo, "Component_Proprietary_ID")))
				                	componentItemId.put("Proprietary_ID", mdt.getValue(rowNo, "Component_Proprietary_ID"));
				                if(!("").equals(mdt.getValue(rowNo, "Component_ISBN")))
				                	componentItemId.put("ISBN", mdt.getValue(rowNo, "Component_ISBN"));
				                if(!("").equals(mdt.getValue(rowNo, "Component_Print_ISSN")))
				                	componentItemId.put("Print_ISSN", mdt.getValue(rowNo, "Component_Print_ISSN"));
				                if(!("").equals(mdt.getValue(rowNo, "Component_Online_ISSN")))
				                	componentItemId.put("Online_ISSN", mdt.getValue(rowNo, "Component_Online_ISSN"));
				                if(!("").equals(mdt.getValue(rowNo, "Component_URI")))
				                	componentItemId.put("URI", mdt.getValue(rowNo, "Component_URI"));
				                
				                reportItems.getItemComponent().setTitle(mdt.getValue(rowNo, "Component_Title"));
				                reportItems.getItemComponent().setItemId(componentItemId);
				                
				                componentItemContributor = new LinkedHashMap<>();
				                itemContributors = mdt.getValue(rowNo, "Component_Authors").split(",");
				                for(String contributor : itemContributors)
				                	componentItemContributor.put("Name", contributor);
				                reportItems.getItemComponent().setItemContributor(componentItemContributor);
				                
				                componentItemDates = new LinkedHashMap<>();
				                componentItemDates.put("Pub_Date", mdt.getValue(rowNo, "Component_Publication_Date"));
				                reportItems.getItemComponent().setItemDates(parentItemDates);
				                
				                componentItemAttributes = new LinkedHashMap<>();
				                componentItemAttributes.put("Article_Version", mdt.getValue(rowNo, "Component_Article_Version"));
				                reportItems.getItemComponent().setItemAttributes(parentItemAttributes);
				                
				                reportItems.getItemComponent().setPublisher(currPublisher);
				                reportItems.getItemComponent().setPublisherId(publisherId);
				                reportItems.getItemComponent().setDataType(mdt.getValue(rowNo, "Component_Data_Type"));
				                reportItems.getItemComponent().setPerformance(performanceList);			                
				        	}	
			            	
			            	if(reportId.toUpperCase().startsWith("IR"))
			            		reportItems.setTitle(mdt.getValue(rowNo, SushiConstants.ITEM));
			            	/*else if(reportId.toUpperCase().equalsIgnoreCase("TR_J4"))
			            		reportItems.setTitle(mdt.getValue(rowNo, "Parent_Title"));*/
			            	else if(!reportId.toUpperCase().startsWith("PR") && !reportId.toUpperCase().startsWith("DR"))
			            		reportItems.setTitle(mdt.getValue(rowNo, SushiConstants.TITLE));
			            	
			            	if(!reportId.toUpperCase().startsWith("PR"))
			            		reportItems.setPublisher(mdt.getValue(rowNo, SushiConstants.PUBLISHER));
			            	
			            	if(reportId.toUpperCase().startsWith("DR"))
			            		reportItems.setDatabase(currDatabase);
			            	reportItems.setItemId(itemId);
			            	reportItems.setPlatform(mdt.getValue(rowNo, SushiConstants.PLATFORM));		            	
			            	reportItems.setPublisherId(publisherId);
			            	reportItems.setDataType(mdt.getValue(rowNo, "Data_Type"));
			            	if(!reportId.toUpperCase().startsWith("PR") && !reportId.toUpperCase().startsWith("DR"))
			            		reportItems.setSectionType(mdt.getValue(rowNo, "Section_Type"));
			            	reportItems.setYop(mdt.getValue(rowNo, "YOP"));
			            	reportItems.setAccessType(mdt.getValue(rowNo, "Access_Type"));
			            	reportItems.setAccessMethod(mdt.getValue(rowNo, "Access_Method"));
			            	reportItems.setPerformance(performanceList);
			            	instName = mdt.getValue(rowNo, "Institution_Name");
			            	if(!reportId.toUpperCase().startsWith("PR"))
			            		publisher = mdt.getValue(rowNo, "Publisher");
			            	
			            	if(reportId.toUpperCase().startsWith("IR"))
			            		reportItemsList.put(currTitle + currParentTitle + currArticleId, reportItems);
			            	else if(reportId.toUpperCase().startsWith("PR"))
			            		reportItemsList.put(currPlatform, reportItems);
			            	else if(reportId.toUpperCase().startsWith("DR"))
			            		reportItemsList.put(currDatabase, reportItems);
			            	/*else if(reportId.equalsIgnoreCase("TR_J4"))
			            		reportItemsList.put(currTitle+currYear, reportItems);*/
			            	else
			            		reportItemsList.put(currTitle, reportItems);
			        	}
			        }
					sushiException = new SushiException();
					sushiException.setCode("");
					sushiException.setSeverity("");
					sushiException.setMessage("");
					sushiException.setHelpUrl("");
					sushiException.setData("");
		        }
				else{
					sushiException = new SushiException();
					sushiException.setCode("3030");
					sushiException.setSeverity("Error");
					sushiException.setMessage(SushiConstants.E3030);
					sushiException.setHelpUrl("string");
					sushiException.setData(SushiConstants.E3030_DATA);
					mdt = getAccountName(customerId, pub);
					if(mdt != null && mdt.getRowCount() > 0)
						instName = mdt.getValue(1, "name");
					instId = new LinkedHashMap<>();
		        	/*instId.put("Type", pub);
		        	instId.put("Value", customerId);*/
		        	instId.put(pub, customerId);
				}
        	}
        	else{
        		mdt = getAccountName(customerId, pub);
				if(mdt != null && mdt.getRowCount() > 0)
					instName = mdt.getValue(1, "name");
				instId = new LinkedHashMap<>();
	        	/*instId.put("Type", pub);
	        	instId.put("Value", customerId);*/
				instId.put(pub, customerId);
        	}
	        reportHeader.setCreated(sdf.format(new Date()));
	    	reportHeader.setCreatedBy(SushiConstants.CREATED_BY);
	    	reportHeader.setCustomerId(customerId);
	    	reportHeader.setReportId(reportId);
	    	reportHeader.setRelease(String.valueOf(release));
	    	reportHeader.setReportName(reportName);
	    	reportHeader.setInstitutionName(instName);        	
	    	reportHeader.setInstitutionId(instId);
	    	if(startMonth < 10 )
	    		reportHeader.setStartMonth("0" + startMonth);
	    	else
	    		reportHeader.setStartMonth(String.valueOf(startMonth));
	    	if(endMonth < 10 )
	    		reportHeader.setEndMonth("0" + endMonth);
	    	else
	    		reportHeader.setEndMonth(String.valueOf(endMonth));
	    	reportHeader.setStartYear(startYear);
	    	reportHeader.setEndYear(endYear);
	    	reportHeader.setSushiException(sushiException);
			report.setReportHeader(reportHeader);
		    report.setReportItemsList(reportItemsList);
		    
        }
	    catch(Exception e){
	        LOG.info("Exception " + e.getMessage());
	    }
	    finally{
	        	
	    }
        return report;
	}

	public JsonArray getReportInfoData(MyDataTable mdt, String customerId, String instID, String token, SushiException sushiException) throws Exception{
		JsonArrayBuilder jsonArrayBuilder = null;
		String reportId = "";
		try{
			jsonArrayBuilder = Json.createArrayBuilder();
			if(sushiException == null){
				if(mdt.getRowCount() != 0){	  
					for(int rowNo = 1; rowNo <= mdt.getRowCount(); rowNo++)	{
						reportId = mdt.getValue(rowNo, "report_code");
						if(reportId != null){
							reportId = reportId.trim().toUpperCase();
							if(reportId.equalsIgnoreCase("TR")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("TR_J1")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("TR_J2")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("TR_J3")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("TR_J4")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("TR_B1")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("TR_B2")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("TR_B3")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("IR")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("IR_A1")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("IR_M1")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("DR")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("DR_D1")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("DR_D2")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("PR")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}
							else if(reportId.equalsIgnoreCase("PR_P1")){
								jsonArrayBuilder.add(Json.createObjectBuilder().add("Report_Name", mdt.getValue(rowNo, "report_name")).add("Report_ID", reportId).add("Release", "5").add("Report_Description", mdt.getValue(rowNo, "report_description")).add("Path", "/reports/"+ reportId.toLowerCase()));
							}						
						}
						
					}				
				}
				else{					
					jsonArrayBuilder.add(Json.createObjectBuilder().add("Code", "1000")
							.add("Severity", "Fatal")
							.add("Message", SushiConstants.F1000)
							.add("Help_URL", "string")
							.add("Data", SushiConstants.F1000_DATA));
				}
			}
			else{
				jsonArrayBuilder.add(Json.createObjectBuilder().add("Code", sushiException.getCode())
						.add("Severity", sushiException.getSeverity())
						.add("Message", sushiException.getMessage())
						.add("Help_URL", sushiException.getHelpUrl())
						.add("Data", sushiException.getData()));
			}
		}
		catch(Exception e){
			LOG.info("Exception " + e.getMessage());
		}
		
		return jsonArrayBuilder.build();
	}
	
	public JsonArray getReportStatusData(MyDataTable mdt, String customerId, String instID, SushiException sushiException) throws Exception{
		JsonArrayBuilder jsonArrayBuilder = null;
		JsonArrayBuilder jsonArrayBuilderAlerts = null;
		try{
			jsonArrayBuilder = Json.createArrayBuilder();
			jsonArrayBuilderAlerts = Json.createArrayBuilder();
			int columnCount = 0;
			int alerts = 0;
			if(sushiException == null){
				if(mdt.getRowCount() != 0){	  
					columnCount = mdt.getColumnCount();
					alerts = (columnCount - 8)/2;
					for(int rowNo = 1; rowNo <= mdt.getRowCount(); rowNo++)	{
						for(int j = 1;j<=alerts ;j++ ){
							if(!mdt.getValue(rowNo, "alert_date_time"+j).equalsIgnoreCase("") || !mdt.getValue(rowNo, "alert_message"+j).equalsIgnoreCase("")){
								jsonArrayBuilderAlerts.add(Json.createObjectBuilder().add("Date_Time", mdt.getValue(rowNo, "alert_date_time"+j)).add("Alert", mdt.getValue(rowNo, "alert_message"+j)));
							}
						}
							jsonArrayBuilder.add(Json.createObjectBuilder().add("Description", mdt.getValue(rowNo, "description")).add("ServiceActive", mdt.getValue(rowNo, "service_status")).add("RegistryURL", mdt.getValue(rowNo, "registry_url")).add("Note", mdt.getValue(rowNo, "note")).add("Alerts", jsonArrayBuilderAlerts));
					}
				}
				else{
					jsonArrayBuilder.add(Json.createObjectBuilder().add("Code", "1000")
							.add("Severity", "Fatal")
							.add("Message", SushiConstants.F1000)
							.add("Help_URL", "string")
							.add("Data", SushiConstants.F1000_DATA));
				}
			}
			else{
				jsonArrayBuilder.add(Json.createObjectBuilder().add("Code", sushiException.getCode())
						.add("Severity", sushiException.getSeverity())
						.add("Message", sushiException.getMessage())
						.add("Help_URL", sushiException.getHelpUrl())
						.add("Data", sushiException.getData()));
			}
		}
		catch(Exception e){
			LOG.info("Exception " + e.getMessage());
		}
		
		return jsonArrayBuilder.build();
	}
	
	public JsonObject getReportInJson(SushiReport report){
		JsonObjectBuilder jsonBuilder = null;
		JsonObjectBuilder headerBuilder = null;
		JsonObjectBuilder performanceBuilder = null;
		JsonArrayBuilder reportItemsData = null;		
		JsonArrayBuilder itemID = null;
		JsonArrayBuilder institutionID = null;
		JsonArrayBuilder reportFilters = null;
		JsonArrayBuilder reportAttributes = null;			
		JsonArrayBuilder publisherID = null;
		JsonArrayBuilder exceptions = null;
		JsonArrayBuilder performance =  null;
		JsonObjectBuilder period = null;
		JsonArrayBuilder instance = null;
		JsonObject reportObj = null;
		try{
			jsonBuilder = Json.createObjectBuilder();
			headerBuilder = Json.createObjectBuilder();
			performanceBuilder = Json.createObjectBuilder();
			reportItemsData = Json.createArrayBuilder();			
			institutionID = Json.createArrayBuilder();
			reportFilters = Json.createArrayBuilder();
			reportAttributes = Json.createArrayBuilder();
			exceptions = Json.createArrayBuilder();
			ReportHeader reportHeader = report.getReportHeader();	
			headerBuilder.add("Created", reportHeader.getCreated())
						 .add("Created_By", reportHeader.getCreatedBy())
						 .add("Customer_ID", reportHeader.getCustomerId())
						 .add("Report_ID", reportHeader.getReportId())
						 .add("Release", reportHeader.getRelease())
						 .add("Report_Name", reportHeader.getReportName())
						 .add("Institution_Name", reportHeader.getInstitutionName());
			
			//hard coded :: need to replace later
			for(String map : report.getReportHeader().getInstitutionId().keySet()){
				institutionID.add(Json.createObjectBuilder().add("Type", "Proprietary").add("Value", map+":"+report.getReportHeader().getInstitutionId().get(map)));
			}
			//institutionID.add(Json.createObjectBuilder().add("Type", report.getReportHeader().getInstitutionId().get("Type")).add("Value", report.getReportHeader().getInstitutionId().get("Value")));
	        reportFilters.add(Json.createObjectBuilder().add("Name", "Begin_Date").add("Value", report.getReportHeader().getStartYear() + "-" + report.getReportHeader().getStartMonth()));
	        reportFilters.add(Json.createObjectBuilder().add("Name", "End_Date").add("Value", report.getReportHeader().getEndYear() + "-" + report.getReportHeader().getEndMonth()));
	    	reportAttributes.add(Json.createObjectBuilder().add("Name", "Attributes_To_Show").add("Value", "Data_Type|Access_Method"));
	    	exceptions.add(Json.createObjectBuilder().add("Code", reportHeader.getSushiException().getCode()).add("Severity", reportHeader.getSushiException().getSeverity()).add("Message", reportHeader.getSushiException().getMessage()).add("Help_URL", reportHeader.getSushiException().getHelpUrl()).add("Data", reportHeader.getSushiException().getData()));
	    	
	    	headerBuilder.add("Institution_ID", institutionID);
	    	headerBuilder.add("Report_Filters", reportFilters);
	    	headerBuilder.add("Report_Attributes", reportAttributes);
	    	headerBuilder.add("Exceptions", exceptions);
	    	
	    	
	    	for(ReportItems rItems: report.getReportItemsList().values()){
	    		int validPerformance = 0;	    		
	    		itemID = Json.createArrayBuilder();  
	        	publisherID = Json.createArrayBuilder(); 
	        	performance =  Json.createArrayBuilder();
	        	for(String map : rItems.getItemId().keySet()){
	        		if(rItems.getItemId().get(map) == null || rItems.getItemId().get(map).equalsIgnoreCase("-") )
	        			itemID.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			itemID.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemId().get(map)));
	        	}
	        	publisherID.add(Json.createObjectBuilder().add("Type", rItems.getPublisherId().get("Type")).add("Value", rItems.getPublisherId().get("Value")).build());
	        	for(Performance perform : rItems.getPerformance()){
	        		int validInstance = 0;
	        		instance = Json.createArrayBuilder();
	            	period = Json.createObjectBuilder();
	        		period.add("Begin_Date", perform.getPeriod().getBeginDate()).add("End_Date", perform.getPeriod().getEndDate());
	    			for(Instance instan : perform.getInstance()){
	    				if(instan.getCount() > 0){
	    					instance.add(Json.createObjectBuilder().add("Metric_Type", instan.getMetricType()).add("Count", instan.getCount()));
	    					validPerformance ++;
	    					validInstance ++;
	    				}
	    			} 
	    			if(validInstance > 0){
		    			performanceBuilder.add("Period", period);
		        		performanceBuilder.add("Instance", instance);
		        		performance.add(performanceBuilder);
	    			}
	        	}        	
	        	if(validPerformance > 0){
			    	reportItemsData.add(Json.createObjectBuilder().add("Title", rItems.getTitle())
			    			.add("Item_ID", itemID)
			        		.add("Platform", rItems.getPlatform())
			        		.add("Publisher", rItems.getPublisher())
			        		.add("Publisher_ID", publisherID)
			        		.add("Data_Type", rItems.getDataType())
			        		.add("Section_Type", rItems.getSectionType())
			        		.add("YOP", rItems.getYop())
			        		.add("Access_Type", rItems.getAccessType())
			        		.add("Access_Method", rItems.getAccessMethod())
				            .add("Performance", performance));
	        	}
	    	}	    	
	    	jsonBuilder.add("Report_Header", headerBuilder);
	        jsonBuilder.add("Report_Items", reportItemsData);
	        reportObj = jsonBuilder.build();
		}
		catch(Exception e){
			LOG.info("Exception " + e.getMessage());
		}
		finally{
			
		}
		return reportObj;	
	 }
	
	public JsonObject getReportInJsonPR(SushiReport report){
		JsonObjectBuilder jsonBuilder = null;
		JsonArrayBuilder publisherID = null;
		JsonObjectBuilder headerBuilder = null;
		JsonObjectBuilder performanceBuilder = null;
		JsonArrayBuilder reportItemsData = null;
		JsonArrayBuilder institutionID = null;
		JsonArrayBuilder reportFilters = null;
		JsonArrayBuilder reportAttributes = null;
		JsonArrayBuilder exceptions = null;
		JsonArrayBuilder performance =  null;
		JsonObjectBuilder period = null;
		JsonArrayBuilder instance = null;
		JsonObject reportObj = null;
		try{
			jsonBuilder = Json.createObjectBuilder();
			headerBuilder = Json.createObjectBuilder();
			performanceBuilder = Json.createObjectBuilder();
			reportItemsData = Json.createArrayBuilder();			
			institutionID = Json.createArrayBuilder();
			reportFilters = Json.createArrayBuilder();
			reportAttributes = Json.createArrayBuilder();
			exceptions = Json.createArrayBuilder();
			ReportHeader reportHeader = report.getReportHeader();	
			headerBuilder.add("Created", reportHeader.getCreated())
						 .add("Created_By", reportHeader.getCreatedBy())
						 .add("Customer_ID", reportHeader.getCustomerId())
						 .add("Report_ID", reportHeader.getReportId())
						 .add("Release", reportHeader.getRelease())
						 .add("Report_Name", reportHeader.getReportName())
						 .add("Institution_Name", reportHeader.getInstitutionName());
			
			for(String map : report.getReportHeader().getInstitutionId().keySet()){
				institutionID.add(Json.createObjectBuilder().add("Type", "Proprietary").add("Value", map + ":" + report.getReportHeader().getInstitutionId().get(map)));
			}
			//hard coded :: need to replace later
	    	//institutionID.add(Json.createObjectBuilder().add("Type", report.getReportHeader().getInstitutionId().get("Type")).add("Value", report.getReportHeader().getInstitutionId().get("Value")));
	        reportFilters.add(Json.createObjectBuilder().add("Name", "Begin_Date").add("Value", report.getReportHeader().getStartYear() + "-" + report.getReportHeader().getStartMonth()));
	        reportFilters.add(Json.createObjectBuilder().add("Name", "End_Date").add("Value", report.getReportHeader().getEndYear() + "-" + report.getReportHeader().getEndMonth()));
	        reportAttributes.add(Json.createObjectBuilder().add("Name", "Attributes_To_Show").add("Value", "Data_Type|Access_Method"));
	    	exceptions.add(Json.createObjectBuilder().add("Code", reportHeader.getSushiException().getCode()).add("Severity", reportHeader.getSushiException().getSeverity()).add("Message", reportHeader.getSushiException().getMessage()).add("Help_URL", reportHeader.getSushiException().getHelpUrl()).add("Data", reportHeader.getSushiException().getData()));
	    	
	    	headerBuilder.add("Institution_ID", institutionID);
	    	headerBuilder.add("Report_Filters", reportFilters);
	    	headerBuilder.add("Report_Attributes", reportAttributes);
	    	headerBuilder.add("Exceptions", exceptions);
	    	
	    	
	    	for(ReportItems rItems: report.getReportItemsList().values()){
	    		int validPerformance = 0;
	        	performance =  Json.createArrayBuilder();
	        	publisherID = Json.createArrayBuilder(); 
	        	for(Performance perform : rItems.getPerformance()){
	        		int validInstance = 0;
	        		instance = Json.createArrayBuilder();
	            	period = Json.createObjectBuilder();
	        		period.add("Begin_Date", perform.getPeriod().getBeginDate()).add("End_Date", perform.getPeriod().getEndDate());
	    			for(Instance instan : perform.getInstance()){
	    				if(instan.getCount() > 0){
	    					instance.add(Json.createObjectBuilder().add("Metric_Type", instan.getMetricType()).add("Count", instan.getCount()));	    				
	    					validPerformance ++;
	    					validInstance ++;
	    				}
	    			}
	    			if(validInstance > 0){
		    			performanceBuilder.add("Period", period);
		        		performanceBuilder.add("Instance", instance);
		        		performance.add(performanceBuilder);
	    			}
	        	}   
	        	
	        	if(reportHeader.getReportId().toUpperCase().startsWith("PR") && validPerformance > 0){
			    	reportItemsData.add(Json.createObjectBuilder()
			    			.add("Platform", rItems.getPlatform())
			        		.add("Data_Type", rItems.getDataType())
			        		.add("YOP", rItems.getYop())
			        		.add("Access_Type", rItems.getAccessType())
			        		.add("Access_Method", rItems.getAccessMethod())
				            .add("Performance", performance));
	        	}
	        	else if(reportHeader.getReportId().toUpperCase().startsWith("DR") && validPerformance > 0){
	        		publisherID.add(Json.createObjectBuilder().add("Type", rItems.getPublisherId().get("Type")).add("Value", rItems.getPublisherId().get("Value")).build());
			    	reportItemsData.add(Json.createObjectBuilder()
			    			.add("Database", rItems.getDatabase())
			    			.add("Platform", rItems.getPlatform())
			    			.add("Publisher", rItems.getPublisher())
			        		.add("Publisher_ID", publisherID)
			        		.add("Data_Type", rItems.getDataType())
			        		.add("YOP", rItems.getYop())
			        		.add("Access_Type", rItems.getAccessType())
			        		.add("Access_Method", rItems.getAccessMethod())
				            .add("Performance", performance));
		        }
	    	}	    	
	    	jsonBuilder.add("Report_Header", headerBuilder);
	        jsonBuilder.add("Report_Items", reportItemsData);
	        reportObj = jsonBuilder.build();
		}
		catch(Exception e){
			LOG.info("Exception " + e.getMessage());
		}
		finally{
			
		}
		return reportObj;	
	 }
	
	public JsonObject getReportInJsonIR(SushiReport report){
		JsonObjectBuilder jsonBuilder = null;
		JsonObjectBuilder headerBuilder = null;
		JsonObjectBuilder performanceBuilder = null;
		JsonObjectBuilder performanceBuilderComponent = null;
		JsonArrayBuilder reportItemsData = null;
		JsonArrayBuilder reportItemsParentData = null;
		JsonArrayBuilder reportItemsComponentData = null;
		JsonArrayBuilder itemID = null;		
		JsonArrayBuilder itemContributors = null;
		JsonArrayBuilder itemDates = null;
		JsonArrayBuilder itemAttributes = null;
		JsonArrayBuilder parentItemID = null;
		JsonArrayBuilder parentItemContributors = null;
		JsonArrayBuilder parentItemDates = null;
		JsonArrayBuilder parentItemAttributes = null;
		JsonArrayBuilder componentItemID = null;
		JsonArrayBuilder componentItemContributors = null;
		JsonArrayBuilder componentItemDates = null;
		JsonArrayBuilder componentItemAttributes = null;
		JsonArrayBuilder institutionID = null;
		JsonArrayBuilder reportFilters = null;
		JsonArrayBuilder reportAttributes = null;			
		JsonArrayBuilder publisherID = null;
		JsonArrayBuilder exceptions = null;
		JsonArrayBuilder performance =  null;
		JsonArrayBuilder performanceComponent =  null;
		JsonObjectBuilder period = null;
		JsonArrayBuilder instance = null;
		JsonObject reportObj = null;
		try{
			jsonBuilder = Json.createObjectBuilder();
			headerBuilder = Json.createObjectBuilder();
			performanceBuilder = Json.createObjectBuilder();
			performanceBuilderComponent = Json.createObjectBuilder();
			reportItemsData = Json.createArrayBuilder();
			reportItemsParentData = Json.createArrayBuilder();
			reportItemsComponentData = Json.createArrayBuilder();
			institutionID = Json.createArrayBuilder();
			reportFilters = Json.createArrayBuilder();
			reportAttributes = Json.createArrayBuilder();
			exceptions = Json.createArrayBuilder();
			ReportHeader reportHeader = report.getReportHeader();	
			headerBuilder.add("Created", reportHeader.getCreated())
						 .add("Created_By", reportHeader.getCreatedBy())
						 .add("Customer_ID", reportHeader.getCustomerId())
						 .add("Report_ID", reportHeader.getReportId())
						 .add("Release", reportHeader.getRelease())
						 .add("Report_Name", reportHeader.getReportName())
						 .add("Institution_Name", reportHeader.getInstitutionName());
			
			for(String map : report.getReportHeader().getInstitutionId().keySet()){
				institutionID.add(Json.createObjectBuilder().add("Type", "Proprietary").add("Value", map + ":" + report.getReportHeader().getInstitutionId().get(map)));
			}
			//hard coded :: need to replace later
	    	//institutionID.add(Json.createObjectBuilder().add("Type", report.getReportHeader().getInstitutionId().get("Type")).add("Value", report.getReportHeader().getInstitutionId().get("Value")));
	        reportFilters.add(Json.createObjectBuilder().add("Name", "Begin_Date").add("Value", report.getReportHeader().getStartYear() + "-" + report.getReportHeader().getStartMonth()));
	        reportFilters.add(Json.createObjectBuilder().add("Name", "End_Date").add("Value", report.getReportHeader().getEndYear() + "-" + report.getReportHeader().getEndMonth()));
	        reportAttributes.add(Json.createObjectBuilder().add("Name", "Attributes_To_Show").add("Value", "Data_Type|Access_Method"));
	    	exceptions.add(Json.createObjectBuilder().add("Code", reportHeader.getSushiException().getCode()).add("Severity", reportHeader.getSushiException().getSeverity()).add("Message", reportHeader.getSushiException().getMessage()).add("Help_URL", reportHeader.getSushiException().getHelpUrl()).add("Data", reportHeader.getSushiException().getData()));
	    	
	    	headerBuilder.add("Institution_ID", institutionID);
	    	headerBuilder.add("Report_Filters", reportFilters);
	    	headerBuilder.add("Report_Attributes", reportAttributes);
	    	headerBuilder.add("Exceptions", exceptions);
	    	
	    	
	    	for(ReportItems rItems: report.getReportItemsList().values()){	
	    		itemContributors = Json.createArrayBuilder();
	    		itemDates = Json.createArrayBuilder();
	    		itemAttributes = Json.createArrayBuilder();
	    		itemID = Json.createArrayBuilder();  
	    		parentItemID = Json.createArrayBuilder();
	    		parentItemContributors = Json.createArrayBuilder();
	    		parentItemDates = Json.createArrayBuilder();
	    		parentItemAttributes = Json.createArrayBuilder();
	    		componentItemID = Json.createArrayBuilder();
	    		componentItemContributors = Json.createArrayBuilder();
	    		componentItemDates = Json.createArrayBuilder();
	    		componentItemAttributes = Json.createArrayBuilder();
	        	publisherID = Json.createArrayBuilder(); 
	        	performance =  Json.createArrayBuilder();
	        	performanceComponent =  Json.createArrayBuilder();
	        	//for IR start
	        	for(String map : rItems.getItemId().keySet()){
	        		if(rItems.getItemId().get(map) == null || rItems.getItemId().get(map).equalsIgnoreCase("-") )
	        			itemID.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			itemID.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemId().get(map)));
	        	}
	        	
	        	for(String map : rItems.getItemContributor().keySet()){
	        		if(rItems.getItemContributor().get(map) == null || rItems.getItemContributor().get(map).equalsIgnoreCase("-") )
	        			itemContributors.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			itemContributors.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemContributor().get(map)));
	        	}
	        	
	        	for(String map : rItems.getItemDates().keySet()){
	        		if(rItems.getItemDates().get(map) == null || rItems.getItemDates().get(map).equalsIgnoreCase("-") )
	        			itemDates.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			itemDates.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemDates().get(map)));
	        	}
	        	
	        	for(String map : rItems.getItemAttributes().keySet()){
	        		if(rItems.getItemAttributes().get(map) == null || rItems.getItemAttributes().get(map).equalsIgnoreCase("-") )
	        			itemAttributes.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			itemAttributes.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemAttributes().get(map)));
	        	}
	        	//parent IR
	        	for(String map : rItems.getItemParent().getItemId().keySet()){
	        		if(rItems.getItemParent().getItemId().get(map) == null || rItems.getItemParent().getItemId().get(map).equalsIgnoreCase("-") )
	        			parentItemID.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			parentItemID.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemParent().getItemId().get(map)));
	        	}
	        	
	        	for(String map : rItems.getItemParent().getItemContributor().keySet()){
	        		if(rItems.getItemParent().getItemContributor().get(map) == null || rItems.getItemParent().getItemContributor().get(map).equalsIgnoreCase("-") )
	        			parentItemContributors.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			parentItemContributors.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemParent().getItemContributor().get(map)));
	        	}
	        	
	        	for(String map : rItems.getItemParent().getItemDates().keySet()){
	        		if(rItems.getItemParent().getItemDates().get(map) == null || rItems.getItemParent().getItemDates().get(map).equalsIgnoreCase("-") )
	        			parentItemDates.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			parentItemDates.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemParent().getItemDates().get(map)));
	        	}
	        	
	        	for(String map : rItems.getItemParent().getItemAttributes().keySet()){
	        		if(rItems.getItemParent().getItemAttributes().get(map) == null || rItems.getItemParent().getItemAttributes().get(map).equalsIgnoreCase("-") )
	        			parentItemAttributes.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			parentItemAttributes.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemParent().getItemAttributes().get(map)));
	        	}
	        	
	        	//component IR
	        	for(String map : rItems.getItemComponent().getItemId().keySet()){
	        		if(rItems.getItemComponent().getItemId().get(map) == null || rItems.getItemComponent().getItemId().get(map).equalsIgnoreCase("-") )
	        			componentItemID.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			componentItemID.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemComponent().getItemId().get(map)));
	        	}
	        	
	        	for(String map : rItems.getItemComponent().getItemContributor().keySet()){
	        		if(rItems.getItemComponent().getItemContributor().get(map) == null || rItems.getItemComponent().getItemContributor().get(map).equalsIgnoreCase("-") )
	        			componentItemContributors.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			componentItemContributors.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemComponent().getItemContributor().get(map)));
	        	}
	        	
	        	for(String map : rItems.getItemComponent().getItemDates().keySet()){
	        		if(rItems.getItemComponent().getItemDates().get(map) == null || rItems.getItemComponent().getItemDates().get(map).equalsIgnoreCase("-") )
	        			componentItemDates.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			componentItemDates.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemComponent().getItemDates().get(map)));
	        	}
	        	
	        	for(String map : rItems.getItemComponent().getItemAttributes().keySet()){
	        		if(rItems.getItemComponent().getItemAttributes().get(map) == null || rItems.getItemComponent().getItemAttributes().get(map).equalsIgnoreCase("-") )
	        			componentItemAttributes.add(Json.createObjectBuilder().add("Type", map).add("Value", ""));		            	
	        		else
	        			componentItemAttributes.add(Json.createObjectBuilder().add("Type", map).add("Value", rItems.getItemComponent().getItemAttributes().get(map)));
	        	}
	        	
	        	//for IR end
	        	publisherID.add(Json.createObjectBuilder().add("Type", rItems.getPublisherId().get("Type")).add("Value", rItems.getPublisherId().get("Value")).build());
	        	for(Performance perform : rItems.getItemComponent().getPerformance()){
	        		instance = Json.createArrayBuilder();
	            	period = Json.createObjectBuilder();
	        		period.add("Begin_Date", perform.getPeriod().getBeginDate()).add("End_Date", perform.getPeriod().getEndDate());
	    			for(Instance instan : perform.getInstance()){
	    				instance.add(Json.createObjectBuilder().add("Metric_Type", instan.getMetricType()).add("Count", instan.getCount()));	    				
	    			} 
	    			performanceBuilderComponent.add("Period", period);
	    			performanceBuilderComponent.add("Instance", instance);
	        		performanceComponent.add(performanceBuilderComponent);
	        	}
	        	
	        	for(Performance perform : rItems.getPerformance()){
	        		instance = Json.createArrayBuilder();
	            	period = Json.createObjectBuilder();
	        		period.add("Begin_Date", perform.getPeriod().getBeginDate()).add("End_Date", perform.getPeriod().getEndDate());
	    			for(Instance instan : perform.getInstance()){
	    				instance.add(Json.createObjectBuilder().add("Metric_Type", instan.getMetricType()).add("Count", instan.getCount()));	    				
	    			} 
	    			performanceBuilder.add("Period", period);
	        		performanceBuilder.add("Instance", instance);
	        		performance.add(performanceBuilder);
	        	}
	        	// for IR title is changeed to item
	        	
	        	reportItemsParentData.add(Json.createObjectBuilder().add("Item_Name", rItems.getItemParent().getTitle())
	        			.add("Item_ID", parentItemID)
	        			.add("Item_Contributors", parentItemContributors)
	        			.add("Item_Dates", parentItemDates)
	        			.add("Item_Attributes", parentItemAttributes)
	        			.add("Publisher", rItems.getItemParent().getPublisher())
	        			.add("Publisher_ID", publisherID)
	        			.add("Data_Type", rItems.getItemParent().getDataType()));
	        	
	        	publisherID.add(Json.createObjectBuilder().add("Type", rItems.getPublisherId().get("Type")).add("Value", rItems.getPublisherId().get("Value")).build());
	        	reportItemsComponentData.add(Json.createObjectBuilder().add("Item_Name", rItems.getItemComponent().getTitle())
	        			.add("Item_ID", componentItemID)
	        			.add("Item_Contributors", componentItemContributors)
	        			.add("Item_Dates", componentItemDates)
	        			.add("Item_Attributes", componentItemAttributes)
	        			.add("Publisher", rItems.getItemComponent().getPublisher())
	        			.add("Publisher_ID", publisherID)
	        			.add("Data_Type", rItems.getItemComponent().getDataType())
	        			.add("Performance", performanceComponent));
	        	
	        	publisherID.add(Json.createObjectBuilder().add("Type", rItems.getPublisherId().get("Type")).add("Value", rItems.getPublisherId().get("Value")).build());
		    	reportItemsData.add(Json.createObjectBuilder().add("Item", rItems.getTitle())
		    			.add("Item_ID", itemID)
		    			.add("Item_Contributors", itemContributors)
		    			.add("Item_Dates", itemDates)
		    			.add("Item_Attributes", itemAttributes)
		        		.add("Platform", rItems.getPlatform())
		        		.add("Publisher", rItems.getPublisher())		        		
		        		.add("Publisher_ID", publisherID)
		        		.add("Item_Parent", reportItemsParentData)
		        		.add("Item_Component", reportItemsComponentData)
		        		.add("Data_Type", rItems.getDataType())
		        		.add("Section_Type", rItems.getSectionType())
		        		.add("YOP", rItems.getYop())
		        		.add("Access_Type", rItems.getAccessType())
		        		.add("Access_Method", rItems.getAccessMethod())
			            .add("Performance", performance));
		    		
	    	}	    	
	    	jsonBuilder.add("Report_Header", headerBuilder);
	        jsonBuilder.add("Report_Items", reportItemsData);
	        reportObj = jsonBuilder.build();
		}
		catch(Exception e){
			LOG.info("Exception " + e.getMessage());
		}
		finally{
			
		}
		return reportObj;	
	 }
	
	public MyDataTable getMembersInfo(int clientID, String institutionId, int setNo, int userId) throws Exception{
		DynamicDatabase dd = null;
		InsightDAO insightDao = null;
        MyDataTable mdt = null;
        MyDataTable mdt1 = null;
        StringBuilder stb = null;
        StringBuilder stb1 = null;
        String parentName = "";
		try {	
			dd = new DynamicDatabase(clientID);
			insightDao = dd.getInsightDao();
			LOG.info("getMembersInfo : webmart_id=" + clientID);
			stb = new StringBuilder();
			stb.append("SELECT name FROM accounts WHERE type = 'Group' AND set_no=" + setNo + " AND code = '" + institutionId + "'");				
	        mdt = insightDao.executeSelectQueryMDT(stb.toString());
	        if(mdt != null &&  mdt.getRowCount() >= 0){
	        	parentName = mdt.getValue(1, "name");
	        	stb1 = new StringBuilder();
	        	/*stb1.append("select * from accounts where set_no =" + setNo + " and code in ")
	    				.append(" (select distinct child_id from account_parent_child where parent_id ")
	    				.append(" = '" + institutionId + "' and set_no = " + setNo + ")");*/
	        	/*stb1.append("SELECT DISTINCT CODE , sa.customer_id AS customer_id, sa.requester_id AS requestor_id, "
	        			+ "acc.name AS account_name, sa.api_key AS api_key FROM accounts acc LEFT JOIN sushi_accounts sa "
	        			+ "ON acc.code = sa.customer_id WHERE acc.set_no = " + setNo + " AND acc.webmart_id = " + clientID + " AND "
	        			+ "acc.code IN (SELECT DISTINCT child_id FROM account_parent_child "
	        			+ "WHERE parent_id = '" + institutionId + "' AND set_no = "  + setNo + " AND webmart_id = " + clientID + ") "
	        			+ "ORDER BY customer_id");*/
	        	
	        	/*
	        	stb1.append("SELECT DISTINCT acc.code as code, uam.account_id AS customer_id, uam.user_id AS requestor_id, acc.name AS account_name, ")
	        			.append("um.api_key as api_key FROM accounts acc LEFT JOIN "+TableMapper.TABALE.get("user_account_table")+" uam ON acc.code = uam.account_id LEFT JOIN ")
	        			.append(""+TableMapper.TABALE.get("user_table")+" um ON uam.user_id = um.id WHERE acc.set_no = "  + setNo + " AND acc.webmart_id = "  + clientID + " ")
	        			.append("AND acc.code IN (SELECT DISTINCT child_id FROM account_parent_child apc JOIN "+TableMapper.TABALE.get("user_account_table")+" uam ON ")
	        			.append("apc.child_id = uam.account_id WHERE apc.parent_id = '" + institutionId + "' AND apc.set_no = "  + setNo + " AND uam.status = 1 AND uam.user_id=" + userId + ") ")
	        			.append("ORDER BY customer_id");
	        	*/
	        	
	        	stb1.append("SELECT DISTINCT acc.code as code, uam.account_id AS customer_id, uam.user_id AS requestor_id, acc.name AS account_name, ")
    			.append("um.api_key as api_key FROM accounts acc LEFT JOIN "+TableMapper.TABALE.get("user_account_table")+" uam ON acc.code = uam.account_id LEFT JOIN ")
    			.append(""+TableMapper.TABALE.get("user_table")+" um ON uam.user_id = um.id WHERE acc.set_no = (SELECT MAX(set_no) FROM accounts) AND acc.webmart_id = "  + clientID + " ")
    			.append("AND acc.code IN (SELECT DISTINCT child_id FROM account_parent_child apc JOIN "+TableMapper.TABALE.get("user_account_table")+" uam ON ")
    			.append("apc.child_id = uam.account_id WHERE apc.parent_id = '" + institutionId + "' AND apc.set_no =(SELECT MAX(set_no) FROM account_parent_child) AND uam.status = 1) ")
    			.append("ORDER BY customer_id");
	        	
	        	mdt1 = insightDao.executeSelectQueryMDT(stb1.toString());
	        }
		} catch (Exception e) {
			LOG.info("Exception in getMembersInfo");
		} finally {			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;
		}		
		return mdt1;
	}
	
	public JsonArray getMembersInfoData(MyDataTable mdt, String publisher, String requestorId, String customerId, String instID, SushiException sushiException) throws Exception{		
		JsonArrayBuilder jsonArrayBuilder = null;
		JsonArrayBuilder jsonArrayBuilderInstitutionID = null;
		try{
			jsonArrayBuilder = Json.createArrayBuilder();
			jsonArrayBuilderInstitutionID = Json.createArrayBuilder();
			if(sushiException == null){
				if(mdt.getRowCount() != 0){	 
					for(int rowNo = 1; rowNo <= mdt.getRowCount(); rowNo++)	{
						    jsonArrayBuilderInstitutionID.add(Json.createObjectBuilder().add("Type", "Proprietary").add("Value", publisher + ":" +mdt.getValue(rowNo, "code")));		
							jsonArrayBuilder.add(Json.createObjectBuilder().add("Customer_ID", mdt.getValue(rowNo, "customer_id"))
									.add("Requestor_ID", mdt.getValue(rowNo, "requestor_id"))
									//.add("Apikey", publisher + "::" + mdt.getValue(rowNo, "api_key"))
									.add("Name", mdt.getValue(rowNo, "account_name"))
									.add("Notes", "Usage pulled using content licensed by the consortium.")
									.add("Institution_ID", jsonArrayBuilderInstitutionID));
							
					}
					
				}
				else{
					jsonArrayBuilder.add(Json.createObjectBuilder().add("Code", "3040")
							.add("Severity", "Warning")
							.add("Message", SushiConstants.W3040)
							.add("Help_URL", "string")
							.add("Data", SushiConstants.F1000_DATA));
				}
			}
			else{
				jsonArrayBuilder.add(Json.createObjectBuilder().add("Code", sushiException.getCode())
						.add("Severity", sushiException.getSeverity())
						.add("Message", sushiException.getMessage())
						.add("Help_URL", sushiException.getHelpUrl())
						.add("Data", sushiException.getData()));
			}
		}
		catch(Exception e){
			LOG.info("Exception " + e.getMessage());
		}
		
		return jsonArrayBuilder.build();
	}
	
	public int getMaxSetNo(int clientID) throws Exception{
		DynamicDatabase dd = null;
		InsightDAO insightDao = null;
        MyDataTable mdt = null;
        StringBuilder stb = null;
        int setNo = 0;
		try {	
			dd = new DynamicDatabase(clientID);
			insightDao = dd.getInsightDao();
			LOG.info("getMaxSetNo : webmart_id=" + clientID);
			stb = new StringBuilder();	        
	        stb.append("SELECT MAX(set_no) AS set_no FROM feed_sets where webmart_id = "+ clientID);	
	        mdt = insightDao.executeSelectQueryMDT(stb.toString());	
	        setNo = Integer.parseInt(mdt.getValue(1, "set_no"));
		} catch (Exception e) {
			LOG.info("Exception in getMaxSetNo");
		} finally {			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;
		}		
		return setNo;
	}
	
	
	public int getWebmartFromPublisher(String publisher){
		int webmartId = 0;
		try {
			InsightDAO insightDao = InsightDAO.getInstance(publisher);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT * FROM `publisher_settings` WHERE data_category='WEBMART_ID' AND data_key='"+publisher.toUpperCase().trim()+"'");
			MyDataTable mdt = insightDao.executeSelectQueryMDT(stb.toString());
			webmartId = Integer.parseInt(mdt.getValue(1, "data_value"));
		} catch (Exception e) {
			 LOG.error("SushiReportsImpl : getWebmartFromPublisher : Unable to get Webmart_id : "+e.toString());
		}
		
		return webmartId;
	}
	
	private void intializeHashTable(int year) {
        LOG.info("START : intializeHashTable");

        Calendar calender = new GregorianCalendar(year, 0, 1);
        for (int i = 0; i < month.length; i++) {
            calender.set(Calendar.MONTH, i);
            numberOfDays.put(i, calender.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        LOG.info("END : intializeHashTable");
    }
	private static Hashtable<Integer, Integer> numberOfDays = new Hashtable();
    private String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

}
