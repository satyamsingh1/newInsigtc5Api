package com.mps.insight.product;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.TableMapper;

public class SearchLibUser {
	@Context
	private HttpServletRequest servletRequest;
	RequestMetaData rmd;
	
	public SearchLibUser(RequestMetaData rmd) {
		this.rmd = rmd;
	}
	
	//private static final Logger LOG = LoggerFactory.getLogger(SearchLibUser.class);
	PublisherSettings pubsetting=null;
	
	public MyDataTable getAllLibUserByPubID(int webmartID) throws Exception{
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			/*stb.append("SELECT u.id AS userID, r.id AS roleID, r.name AS roleName, u.code AS userCode,")
			.append("u.email_id AS emailID, u.first_name AS firstName, u.last_name AS lastName FROM users u ")
			.append("LEFT JOIN user_roles ur ON u.id=ur.user_id LEFT JOIN roles r ON ur.role_id=r.id ")
			.append("WHERE r.name IN ('ROLE_ADMIN','ROLE_USER') AND u.status !='Deleted'")*/
			
			stb.append("SELECT u.id AS userID, ")
			.append("u.role_id AS roleID, ")
			.append("ur.role AS roleName, ")
			.append("u.code AS userCode, ")
			.append("u.email_id AS emailID, ")
			.append("u.first_name AS firstName, ")
			.append("u.last_name AS lastName ")
			.append("FROM `"+TableMapper.TABALE.get("user_table")+"` u  ")
			.append("LEFT JOIN `role_master` ur ")
			.append("ON u.role_id=ur.role_id  ")
			.append("WHERE ur.role IN ('ADMIN','USER') ")
			.append("AND u.status !='Deleted'");
			
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
		} catch (Exception e) {
			rmd.exception("SearchLibUser : getAllLibUserByPubID : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return mdt;
	}
	
	public MyDataTable searchLibUser(String search,int webmartID) throws Exception{
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			/*stb.append("SELECT u.id AS userID,r.role_id AS roleID,r.role AS roleName,u.code AS userCode,")
			.append("u.email_id AS emailID,u.first_name AS firstName,u.last_name AS lastName FROM "+TableMapper.TABALE.get("user_table")+" u ")
			.append("LEFT JOIN user_roles ur ON u.id=ur.user_id LEFT JOIN role_master r ON ur.role_id=r.role_id WHERE ")
			.append("r.role IN ('ROLE_ADMIN','ROLE_USER') AND u.status !='Deleted' ")
			.append("AND (u.email_id LIKE '%"+search+"%' OR u.last_name LIKE '%"+search+"%')");
			
			
			*/
			
			stb.append("SELECT u.id AS userID, ")
			.append("u.role_id AS roleID, ") 
			.append("ur.role AS roleName, ")
			.append(" u.code AS userCode, ")
			.append("u.email_id AS emailID, ")
			.append("u.first_name AS firstName, ")
			.append("u.last_name AS lastName ") 
			.append("FROM "+TableMapper.TABALE.get("user_table")+" u ") 
			.append("LEFT JOIN `role_master` ur ON u.role_id=ur.role_id ")
			.append("WHERE ur.role IN ('ADMIN','USER') ") 
			.append("AND u.status !='Deleted' ")    
			.append("AND (u.email_id LIKE '%"+search+"%' OR u.last_name LIKE '%"+search+"%')");  
			
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
		} catch (Exception e) {
			rmd.exception("SearchLibUser : searchLibUser : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return mdt;
	}
	
	public MyDataTable searchAccountAllLibUser(String userIDs,String accountsCode,int webmartID) throws Exception{
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
		/*	stb.append("SELECT u.id AS userID,r.role_id AS roleID,r.role AS roleName,u.code AS userCode,")
			.append("u.email_id AS emailID,u.first_name AS firstName,u.last_name AS lastName FROM "+TableMapper.TABALE.get("user_table")+" u ")
			.append("LEFT JOIN user_roles ur ON u.id=ur.user_id LEFT JOIN role_master r ON ur.role_id=r.role_id ")
			.append("WHERE u.id IN (SELECT DISTINCT(user_id) FROM "+TableMapper.TABALE.get("user_account_table")+" ")
			.append("WHERE account_id IN ("+accountsCode+") AND user_id IN ("+userIDs+"))");
			*/
			stb.append("SELECT u.id AS userID, ")
			.append("u.role_id AS roleID, ")
			.append("ur.role AS roleName, ")
			.append("u.code AS userCode, ")  
			.append("u.email_id AS emailID, ")
			.append("u.first_name AS firstName, ")
			.append("u.last_name AS lastName ")
			.append("FROM `"+TableMapper.TABALE.get("user_table")+"` u ")
			.append("LEFT JOIN `role_master` ur ON u.role_id=ur.role_id ")
			.append("WHERE u.id IN (SELECT DISTINCT(user_id) FROM "+TableMapper.TABALE.get("user_account_table")+" ")
			.append("WHERE  account_id IN ("+accountsCode+") AND user_id IN ("+userIDs+"))");
			
			rmd.log(stb.toString());
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
		} catch (Exception e) {
			rmd.exception("SearchLibUser : searchAccountAllLibUser : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return mdt;
	}

}
