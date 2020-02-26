package com.mps.insight.impl;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.global.TableMapper;
import com.mps.insight.product.ManageUser;
import com.mps.insight.product.PublisherSettings;

public class SushiPartner {
	
	private RequestMetaData rmd; 
	public SushiPartner(RequestMetaData rmd) {
		this.rmd = rmd;
	}
	
	ManageUser usermanager;
	//private static final Logger LOG = LoggerFactory.getLogger(SushiPartner.class);
	
	PublisherSettings pubsetting=null;
	
	public MyDataTable getSushiPartner(int webmartID) throws Exception {
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		//PublisherSettings pubsetting=null;
		
		try {
			pubsetting=new PublisherSettings(rmd);
			insightDao =rmd.getInsightDao();
			rmd.log("getPublisherIDFromWebmartID : webmart_id=" + webmartID);
			//int pubID=0;
			//pubID=pubsetting.getPublisherIDFromWebmartID(webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT u.user_code AS `Partner Code`,u.first_name AS `Partner Name`,CASE WHEN u.role_id =0 THEN 'Generic' ELSE 'Specific' END AS `Partner Type` ")
			.append("FROM "+TableMapper.TABALE.get("user_table")+" u WHERE user_type ='SushiPartner' AND STATUS != 'Deleted' ORDER BY u.user_code");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
		} catch (Exception e) {
			rmd.log("Exception in GetSushiPartner");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return mdt;
	}
	
	public JsonObject getAccountSushiPartner(int webmartID,String accountID) {
		InsightDAO insightDao = null;
		MyDataTable accountSushi=null;
		MyDataTable pubSushi=null;
		JsonObjectBuilder jobj=Json.createObjectBuilder();
		JsonArrayBuilder jab=Json.createArrayBuilder();
		JsonObjectBuilder finalObject=Json.createObjectBuilder();
		try {
			pubsetting=new PublisherSettings(rmd);
			insightDao =rmd.getInsightDao();
			rmd.log("getAccountSushiPartner : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT sp."+TableMapper.TABALE.get("sp_user_code")+" AS UserID, u.email_id AS EmailID, u.first_name AS firstName ")
			.append("FROM "+TableMapper.TABALE.get("sushi_partner_accounts_table")+" sp LEFT JOIN "+TableMapper.TABALE.get("user_table")+" u ON sp."+TableMapper.TABALE.get("sp_user_code")+"=u.user_code WHERE ")
			.append(" sp."+TableMapper.TABALE.get("sp_account_code")+"='"+accountID+"'");
			accountSushi = insightDao.executeSelectQueryMDT(stb.toString());
			pubSushi=getSushiPartner(webmartID);
			
			int psrowcount=pubSushi.getRowCount();
			int arowCount=accountSushi.getRowCount();
			boolean checked=false;
						for(int i=1;i<=psrowcount;i++){
				checked=false;
				jobj.add(pubSushi.getColumnName(1), pubSushi.getValue(i, 1));
				jobj.add(pubSushi.getColumnName(2), pubSushi.getValue(i, 2));
				jobj.add(pubSushi.getColumnName(3), pubSushi.getValue(i, 3));
				for (int j = 1; j <=arowCount ; j++) {
					
					if(pubSushi.getValue(i, 1).equalsIgnoreCase(accountSushi.getValue(j, 1))){
						checked=true;
						break;
					}
				}
				rmd.log("Detail : "+pubSushi.getValue(i, 1));
				jobj.add("checked",checked);
				jab.add(jobj);
			}
			finalObject.add("data", jab);
			
		} catch (Exception e) {
			rmd.log("Exception in GetSushiPartner");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return finalObject.build();
	}
	
	public boolean validatePartnerCode(String partnerCode,int webmartID){
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		boolean check=false;
		try {
			pubsetting=new PublisherSettings(rmd);
			insightDao =rmd.getInsightDao();
			//rmd.log("validatePartnerCode : PublisherID =" + pubID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT u.id AS `userID`,u.publisher_id AS pubID,u.code AS `Partner Code`,")
			.append("u.first_name AS `Partner Name`,u.publisher_id AS `Partner Type` FROM "+TableMapper.TABALE.get("user_table")+" u ")
			.append("WHERE user_type ='SushiPartner' ")
			.append("AND STATUS != 'Deleted' AND u.code='"+partnerCode+"' ORDER BY u.code");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rowCount=mdt.getRowCount();
			if(rowCount>=1){
				check=false;
			}else{
				check=true;
			}
			//rmd.log(reportSection.toString());
		} catch (Exception e) {
			rmd.log("Exception in validatePartnerCode");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		return check;
	}
	
	public long createSushiUser(UserDTO udto){
		
		ManageUser usermanager=new ManageUser(rmd);
		return usermanager.createUser(udto);
	}

	public String deleteSushiUser(String userCode,int webmartID) {
		ManageUser usermanager=new ManageUser(rmd);
		return usermanager.deleteUser(userCode,webmartID);
	}
	
	public JsonObject getSushiUserToEdit(String userCode,int webmartID) {
		ManageUser usermanager=new ManageUser(rmd);
		return usermanager.getUserByUserCode(userCode);
	}
	
	public String editSushiUser(UserDTO user) {
		String result="";
		ManageUser usermanager=new ManageUser(rmd);
		result=usermanager.editUser(user, user.getWebmartID());
		if(null!=user.getPassword() && user.getPassword().length()>0){
			result=usermanager.updatePassword(user);
		rmd.log("Password Updated ");
		}else if(null!=user.getHighIP() && user.getHighIP().length()>1){
			usermanager.updateUserIP(user.getUserId(), user.getHighIP(), user.getLowIP(),user.getWebmartID());
			rmd.log("UserIP updated ");
		}
		return result;
	}
	
	public String insertAccountSushiPartner(UserDTO user) {
		String result="";
		InsightDAO insightDao = null;
		String publisher_name=null;
		long newId = 0;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(user.getWebmartID());
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			String [] accounts=user.getAccounts().split(",");
			for (String account : accounts) {
				stb.append("INSERT INTO "+TableMapper.TABALE.get("sushi_partner_accounts_table")+" (`account_id`,`user_id`,")
				.append("`updated_by`,`updated_at`) VALUES ('"+account+"','"+user.getUserId())
				.append("','"+user.getUpdatedBy()+"',now())");
				newId = insightDao.executeInsertUpdateQuery(stb.toString());
			}
			
			if (newId == 1) {
				result="success";
			}else{
				result="fail";
			}
			
		} catch (Exception e) {
			rmd.log("Exception in insertAccountSushiPartner");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return result;
	}
	
	public String deleteAccountSushiPartner(UserDTO user) {
		String result="";
		InsightDAO insightDao = null;
		String publisher_name=null;
		long newId = 0;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(user.getWebmartID());
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			String [] accounts=user.getAccounts().split(",");
			for (String account : accounts) {
				stb.append("DELETE FROM "+TableMapper.TABALE.get("sushi_partner_accounts_table")+" WHERE account_id='"+account+"' and user_id='"+user.getUserId()+"'");
				newId = insightDao.executeInsertUpdateQuery(stb.toString());
			}
			
			if (newId == 1) {
				result="success";
			}else{
				result="fail";
			}
		} catch (Exception e) {
			rmd.log("Exception in GetSushiPartner");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return result;
	}

	public ManageUser getUsermanager() {
		ManageUser usermanager=new ManageUser(rmd);
		return usermanager;
	}

	public void setUsermanager(ManageUser usermanager) {
		usermanager=new ManageUser(rmd);
		this.usermanager = usermanager;
	}
	
	public void updateSushiPartner(String partnerCode, String userID, String accountId){
		
	}
	
}
