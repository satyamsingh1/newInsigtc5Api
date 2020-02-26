package com.mps.insight.dashboard;

import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.mps.insight.c4.report.ProductTypeImpl;
import com.mps.insight.c5.report.Counter5ReportImpl;
import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.DynamicDatabase;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;
import com.mps.insight.product.ArticleRequestByType;
import com.mps.insight.product.BookReport2;
import com.mps.insight.product.JournalReport1;
import com.mps.insight.product.PlatFormReport1;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.product.StandardsReports;

public class LibDashboardGraphImpl {
	private RequestMetaData rmd; 
	MyDataTable mdt=null;
	public LibDashboardGraphImpl(RequestMetaData rmd) {
		this.rmd = rmd;
	}
	
	//private static final Logger LOG = LoggerFactory.getLogger(LibDashboardGraphImpl.class);
	public JsonObject getDashboardGraphData(int webmartID,String institution) throws Exception{
		ArticleRequestByType arbt;
		BookReport2 br1;
		JournalReport1 jr1;
		PlatFormReport1 pr1;
		StandardsReports standard;
		JsonObjectBuilder finalJson=Json.createObjectBuilder();
		JsonObjectBuilder fulltextBuilder=Json.createObjectBuilder();
		DynamicDatabase dynamicDb=new DynamicDatabase(webmartID);
		ProductTypeImpl productTypeimpl = new ProductTypeImpl(rmd);
		PublisherSettings publisherSettings;
		JsonArray fulltextJson;
		JsonArrayBuilder monthArrayBuilder=Json.createArrayBuilder();
		JsonArrayBuilder recordArrayBuilder=Json.createArrayBuilder();
		int year=0;
		int month=0;
		
		try {
			publisherSettings = new PublisherSettings(rmd);
			Set<String> productList=productTypeimpl.getProductList();
			if (year == 0) {
				year = publisherSettings.getLiveYear(webmartID);
			}
			if (month == 0) {
				month = publisherSettings.getPubLiveMonth(webmartID, year);
			}
			for (int i = 1; i <=month; i++) {
				monthArrayBuilder.add(InsightConstant.MONTH_ARRAY[i]);
			}
			finalJson.add("header", monthArrayBuilder.build());
			boolean dbValid=dynamicDb.tableCheck("ArticleRequestsByType");
		for (String productName : productList) {
			fulltextJson=null;
			if(InsightConstant.productMaster.get(productName).toUpperCase().startsWith("FULL-TEXT")){
				
					if(dbValid){
						arbt=new ArticleRequestByType(rmd);
						fulltextJson=arbt.getAccountGraphData(webmartID, InsightConstant.productMaster.get(productName).toUpperCase(),month,year,institution);
					}else{
						if(productName.contains("BOOK")){
							br1=new BookReport2(rmd);
							fulltextJson=br1.getAccountBookGraphData(webmartID, "BOOK",month,year,institution);
						}
						if(productName.contains("JOURNAL")){
							jr1=new JournalReport1(rmd);
							fulltextJson=jr1.getAccountJournalGraphData(webmartID, "JOURNAL",month,year,institution);
						}
						if(productName.contains("STANDARD")){
							standard=new StandardsReports(rmd);
							fulltextJson=standard.getAccountStandardGraphData(webmartID, "STANDARD",month,year,institution);
						}
						
					}
					
			}else if(InsightConstant.productMaster.get(productName).toUpperCase().startsWith("PLATFORM") || InsightConstant.productMaster.get(productName).toUpperCase().startsWith("DATABASE")){
				pr1=new PlatFormReport1(rmd);
				fulltextJson=pr1.getAccountPlatFormGraphData(webmartID, InsightConstant.productMaster.get(productName).toUpperCase(),month,year,institution);
				
			}
			
			fulltextBuilder.add(InsightConstant.NAME, productName);
			fulltextBuilder.add(InsightConstant.DATA, fulltextJson);
			recordArrayBuilder.add(fulltextBuilder);
		}
		
		}catch (Exception e) {
			
		}
		finalJson.add(InsightConstant.RECORD, recordArrayBuilder);
		rmd.log(finalJson.toString());
		return finalJson.build();
	}


	
	//get dashboard graph json for library for Counter 5
	public JsonObject getC5DashboardGraphData(int webmartID, String institution) throws Exception{
		//variables for query
		InsightDAO insightDao = null;
		insightDao = rmd.getInsightDao();
		Counter5ReportImpl counter5ReportImpl =new Counter5ReportImpl(rmd);
		String range=counter5ReportImpl.getDateRange(webmartID, institution);
		
		StringBuilder monthYearJson = new StringBuilder();
		StringBuilder monthQuery = new StringBuilder();
		if(range.contains(",")){
			String[] monthYear = range.split(",");
		
			int start=0;
			if(monthYear.length>12){
				start=monthYear.length - 12;
			}
			
			for (int i = start; i < monthYear.length; i++) {
				monthQuery.append("SUM(M_"+monthYear[i]+") as M_"+monthYear[i]+",");
				monthYearJson.append(InsightConstant.MONTH_ARRAY[Integer.parseInt(monthYear[i].substring(4,6))]+"-"+monthYear[i].substring(0,4)+",");
			}
		}
		
		String month_Query = monthQuery.toString().substring(0, monthQuery.toString().length()-1);
		
		
		String Query ="";
		
		if(rmd.getWebmartCode().equalsIgnoreCase("ASM")){
			Query ="SELECT Title_id AS 'data_type', metric_type, "+month_Query+" FROM "+TableMapper.TABALE.get("dr_master_table")+" WHERE "
					+ "institution_id = '"+institution+"' AND Metric_type IN ('searches_regular') GROUP BY Title_id, metric_type ";
		}else{
			//create query
			Query ="SELECT  data_type, metric_type, "+month_Query+"  FROM "+TableMapper.TABALE.get("master_report_table")+" WHERE"
					+ " institution_id = '"+institution+"' AND Metric_type IN ('Total_Item_Requests')"
					+ "GROUP BY data_type, metric_type" + " UNION ALL " +
					" SELECT  data_type, metric_type, "+month_Query+" FROM "+TableMapper.TABALE.get("dr_master_table")+" WHERE "
					+ "institution_id = '"+institution+"' AND Metric_type IN ('searches_regular')" +
					" GROUP BY data_type, metric_type";
		}
		 //execute query
		  mdt=insightDao.executeSelectQueryMDT(Query);
		
		 //create json 
		  //json variables
		  JsonObjectBuilder finalJson=Json.createObjectBuilder();//
		  
		  JsonArrayBuilder dataJson = Json.createArrayBuilder();//get Data array
		  JsonObjectBuilder dataTypeName=Json.createObjectBuilder();//
		 
		  JsonArrayBuilder bindDataName=Json.createArrayBuilder();
		  
		  JsonArrayBuilder monthArrayBuilder=Json.createArrayBuilder();
		  
		  		  
		  mdt.getJson();
		  String[] header = monthYearJson.toString().split(",");
		  for (int i = 0; i <header.length; i++) {
			  monthArrayBuilder.add(header[i]);
			}
		  finalJson.add("header", monthArrayBuilder.build());
		  
		  int rowCount = mdt.getRowCount();
		  int colCount = mdt.getColumnCount();
		  for(int rowIndex=1;rowIndex<=rowCount;rowIndex++) {
			  
			  dataTypeName.add("name", mdt.getValue(rowIndex, "data_type"));

			 for (int columnIndex = 3; columnIndex <= colCount; columnIndex++) {
				 dataJson.add(Integer.parseInt(mdt.getValue(rowIndex, columnIndex)));
			}
			 dataTypeName.add("data", dataJson.build());
			 
			  bindDataName.add(dataTypeName);
		  }
		  
		  finalJson.add(InsightConstant.RECORD, bindDataName);
		  
		try {
			
			//Generate Column list Dynamically, consider Live Year Month as Max Month, start from Jan-2019
			//institution_reports_statistics check for "1" 
			
			
			//Generate The query
			
			
			//convert the MDT data in required JSON format for HighChart
			
			
			//return the Json
		
		}catch (Exception e) {
			
		}
		//finalJson.add(InsightConstant.RECORD, recordArrayBuilder);
		rmd.log(finalJson.toString());
		return finalJson.build();
	}


}
