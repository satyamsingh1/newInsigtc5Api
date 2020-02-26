package com.mps.insight.dashboard;

import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.DynamicDatabase;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.product.ArticleRequestByType;
import com.mps.insight.product.BookReport2;
import com.mps.insight.product.JournalReport1;
import com.mps.insight.product.PlatFormReport1;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.product.SiteSummary;
import com.mps.insight.product.StandardsReports;

public class DashboardGraphImpl {
	
	private RequestMetaData rmd; 
	public DashboardGraphImpl(RequestMetaData rmd) {
		this.rmd = rmd;
	}

	
	//private static final Logger LOG = LoggerFactory.getLogger(DashboardGraphImpl.class);
	public JsonObject getDashboardGraphData(int webmartID,Set<String> productList) throws Exception{
		ArticleRequestByType arbt;
		BookReport2 br1;
		JournalReport1 jr1;
		PlatFormReport1 pr1;
		StandardsReports standard;
		JsonObjectBuilder finalJson=Json.createObjectBuilder();
		JsonObjectBuilder fulltextBuilder=Json.createObjectBuilder();
		DynamicDatabase dynamicDb=new DynamicDatabase(webmartID);
		PublisherSettings publisherSettings;
		JsonArray fulltextJson;
		JsonArrayBuilder monthArrayBuilder=Json.createArrayBuilder();
		JsonArrayBuilder recordArrayBuilder=Json.createArrayBuilder();
		int year=0;
		int month=0;
		
		try {
			publisherSettings = new PublisherSettings(rmd);
			if (year == 0) {
				year = publisherSettings.getLiveYear(webmartID);
			}
			if (month == 0) {
				month = publisherSettings.getPubLiveMonth(webmartID, year);
			}
			for (int i = 1; i <=month; i++) {
				monthArrayBuilder.add(InsightConstant.MONTH_ARRAY_DASH[i]);
			}
			finalJson.add("header", monthArrayBuilder.build());
			boolean dbValid=dynamicDb.tableCheck("ArticleRequestsByType");
		for (String productName : productList) {
			fulltextJson=null;
			if(InsightConstant.productMaster.get(productName).toUpperCase().startsWith("FULL-TEXT")){
				
					if(dbValid && webmartID==201){
						arbt=new ArticleRequestByType(rmd);
						fulltextJson=arbt.getGraphData(webmartID, InsightConstant.productMaster.get(productName).toUpperCase(),month,year);
					}else{
						if(productName.contains("BOOK")){
							br1=new BookReport2(rmd);
							fulltextJson=br1.getBookGraphData(webmartID, "BOOK",month,year);
						}
						if(productName.contains("JOURNAL")){
							jr1=new JournalReport1(rmd);
							fulltextJson=jr1.getJournalGraphData(webmartID, "JOURNAL",month,year);
						}
						if(productName.contains("STANDARD")){
							standard=new StandardsReports(rmd);
							fulltextJson=standard.getStandardGraphData(webmartID, "STANDARD",month,year);
						}
					}
			}else if(InsightConstant.productMaster.get(productName).toUpperCase().startsWith("PLATFORM") || InsightConstant.productMaster.get(productName).toUpperCase().startsWith("DATABASE")){
				pr1=new PlatFormReport1(rmd);
				fulltextJson=pr1.getPlatFormGraphData(webmartID, InsightConstant.productMaster.get(productName).toUpperCase(),month,year);
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
	
	public JsonObject getUserTechDetail(int webmartID,int liveyear,int livemonth){
		SiteSummary ss=null;
		MyDataTable mdt=null;
		JsonObject finalObject=null;
		try{
			ss=new SiteSummary(rmd);
			mdt=ss.getUserTechDetail(webmartID, liveyear, livemonth);
			finalObject=newUserTechDetail(mdt);	
		}catch(Exception e){
			rmd.log("Exception While getting getUserTechDetail : "+e.getMessage());
		}
		
		
		return finalObject;
	}
	
	public JsonObject newUserTechDetail(MyDataTable dataTable){
		
		int rowCount=0;
  		JsonObjectBuilder jsonUserTechAnalysis=null;
  		int deviceCount = 0;
  		String size="size";
  		String children="children";
		
  		try {
  			
  			jsonUserTechAnalysis =Json.createObjectBuilder();
  			JsonArrayBuilder tmpBrowser=Json.createArrayBuilder();
  			JsonArrayBuilder tmpOs=Json.createArrayBuilder();
  			JsonArrayBuilder tmpDevice=Json.createArrayBuilder();
  			
  			int browserCount = 0;
  			int osCount = 0;
  			JsonObjectBuilder jtmp =null;
  			
  			//
  			rowCount=dataTable.getRowCount();
  			for (int rowIndex = 1 ; rowIndex <= rowCount; rowIndex++) {
  				
  				int totalCount	 = 0;
  				String device = dataTable.getValue(rowIndex, "DEVICE");
  				String os = dataTable.getValue(rowIndex, "OS");
  				String browser = dataTable.getValue(rowIndex, "BROWSER");
  				try{totalCount = Integer.parseInt(dataTable.getValue(rowIndex, "TOTAL"));}catch(Exception e){totalCount = 0;}
  				
  				JsonObjectBuilder jsonRow=Json.createObjectBuilder();
  				
  				//check for last row
  				if(rowIndex < rowCount){
  					
  					//
  					jsonRow.add(InsightConstant.NAME, browser);
  					jsonRow.add(size, totalCount);
  					tmpBrowser.add(jsonRow);
  					browserCount = browserCount + totalCount;
  					
  					//check for OS change  ## OS
  					if(!os.trim().toUpperCase().equalsIgnoreCase(dataTable.getValue(rowIndex + 1, "OS"))){
  						
  						//Putting BROWSER Data in # OS Array
  						jtmp=null;
  						jtmp =Json.createObjectBuilder();
  						jtmp.add(InsightConstant.NAME, os);
  						jtmp.add(size, browserCount);
  						jtmp.add(children, tmpBrowser);
  						osCount = osCount + browserCount;
  						tmpOs.add(jtmp);
  						//flushing /cleaning browser arrray and destroying values and objects
  						tmpBrowser=null;
  						tmpBrowser=Json.createArrayBuilder();
  						browserCount = 0;
  					}//END OS
  					
  					
  					//check for DEVICE change  ## DEVICE
  					if (!device.trim().toUpperCase().equalsIgnoreCase(dataTable.getValue(rowIndex + 1, "DEVICE"))) {
  						// Putting OS Data in # DEVICE Array
  						jtmp=null;
  						jtmp =Json.createObjectBuilder();
  						jtmp.add(InsightConstant.NAME, device);
  						jtmp.add(size, osCount);
  						jtmp.add(children, tmpOs);
  						deviceCount = deviceCount + osCount;
  						// adding object to DEVICE array
  						tmpDevice.add(jtmp);
  						// flushing /cleaning browser arrray and destroying
  						// values and objects
  						tmpOs = null;
  						tmpOs=Json.createArrayBuilder();
  						osCount = 0;

  					}// END DEVICE
  					
  				}else { //check for LAST ROW
  					//addding browser array to OS json Object
  					//check for only one row in Database
  					if(rowCount==1){
  						jsonRow.add(InsightConstant.NAME, browser);
  	  					jsonRow.add(size, totalCount);
  	  					tmpBrowser.add(jsonRow);
  	  					browserCount = browserCount + totalCount;
  					}
  					jtmp=null;
					jtmp =Json.createObjectBuilder();
  					jtmp.add(InsightConstant.NAME, os);
					jtmp.add(size, browserCount);
					jtmp.add(children, tmpBrowser);
  					tmpOs.add(jtmp);
  					osCount = osCount + browserCount;
  					//flushing /cleaning browser arrray and destroying values and objects
  					tmpBrowser = null;
  					tmpBrowser=Json.createArrayBuilder();
  					browserCount = 0;
  					
  					//Putting OS Data in # DEVICE Array
  					jtmp=null;
					jtmp =Json.createObjectBuilder();
					jtmp.add(InsightConstant.NAME, device);
					jtmp.add(size, osCount);
					jtmp.add(children, tmpOs);
					tmpDevice.add(jtmp);
					deviceCount = deviceCount + osCount;
  					//flushing /cleaning browser arrray and destroying values and objects
  					tmpOs = null;
  					tmpOs=Json.createArrayBuilder();
  					osCount = 0;
  				}//END ELSE for Last Row
  				
  				
  			}//end for loop for all rows
  			
  			
  			//creating final Json
  			jsonUserTechAnalysis.add(InsightConstant.NAME, "root");
  			jsonUserTechAnalysis.add(children, tmpDevice);
  		}catch(Exception e){
  				rmd.exception("Exception "+e.getMessage());
  			}
		
		return jsonUserTechAnalysis.build();
	}
	
}
