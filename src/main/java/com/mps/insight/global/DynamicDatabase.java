package com.mps.insight.global;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.PublisherSettings;

public class DynamicDatabase {
	private int webmartID;
	private static final Logger LOG = LoggerFactory.getLogger(DynamicDatabase.class);
	InputStream inputStream;
	MyDataTable mdt=null;
	
	
	public DynamicDatabase(int webmartID) {
		super();
		this.webmartID = webmartID;
		LOG.info(" Webmart ID : "+webmartID);
	}

	public int getWebmartID() {
		return webmartID;
	}


	public void setWebmartID(int webmartID) {
		this.webmartID = webmartID;
	}


	
	
	
	
	public HashMap<String,String> getDBDetails(){
		HashMap<String,String> details=new HashMap<>();
		try {
			Properties prop = new Properties();
			String propFileName = "config.properties";
			String driver="";
			String url="";
			String username="";
			String password;
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 
			// get the property value and print it out
			driver = prop.getProperty(webmartID+"_jdbc.driver");
			url = prop.getProperty(webmartID+"_jdbc.url");
			username = prop.getProperty(webmartID+"_jdbc.username");
			password = prop.getProperty(webmartID+"_jdbc.password");
			LOG.info("driver : "+driver +" URL : "+url+ " Username : "+username );
			details.put("driver", driver);
			details.put("url", url);
			details.put("username", username);
			details.put("password", password);
			
		} catch (Exception e) {
			LOG.error("Eror in Get DB detail : "+e.getMessage());
		} finally {
			try {
				if(inputStream!=null){
				inputStream.close();
				}
			} catch (IOException e) {
				LOG.error("Exception in getDBDetails "+e.getMessage());
			}
		}
		return details;
	}
	
	public InsightDAO getInsightDao(){
		//HashMap<String,String> details=new HashMap<>();
		InsightDAO insightDao=null;
		
		try {
			PublisherSettings ps=new PublisherSettings(new RequestMetaData());
			String publisher=ps.getPublisherCode(webmartID);
			insightDao=InsightDAO.getInstance(publisher);
			/*Properties prop = new Properties();
			String propFileName = "config.properties";
			String driver="";
			String url="";
			String username="";
			String password;
			
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 
			// get the property value and print it out
			driver = prop.getProperty(webmartID+"_jdbc.driver");
			url = prop.getProperty(webmartID+"_jdbc.url");
			username = prop.getProperty(webmartID+"_jdbc.username");
			password = prop.getProperty(webmartID+"_jdbc.password");
			LOG.info("driver : "+driver +" URL : "+url+ " Username : "+username );
			details.put("driver", driver);
			details.put("url", url);
			details.put("username", username);
			details.put("password", password);
			insightDao=new InsightDAO(driver, url, username, password);*/
		} catch (Exception e) {
			LOG.error("Error while getting Dynamic Connection : "+e.getMessage());
		} finally {
			/*try {
				if(inputStream!=null){
				inputStream.close();
				}
			} catch (IOException e) {
				LOG.error("Exception in getDBConnection InsightDao "+e.getMessage());
			}*/
		}
		return insightDao;
	}
	
	public boolean tableCheck(String tableName){
		boolean check=false;
		InsightDAO insdao=getInsightDao();
		String query="select 1 from "+tableName+" limit 2";
		int temp=7;
		try {
			mdt=insdao.executeSelectQueryMDT(query);
			
				temp=Integer.parseInt(mdt.getValue(1, 1));
			
			if(temp==7){
				check=false;
			}else {
				check=true;
			}
		} catch (Exception e) {
			check=false;
			LOG.error("Error while check table existence : "+e.getMessage());
		}finally{
			if(insdao!=null){
			insdao.disconnect();
			}
		}
		return check;
	}
	
}
