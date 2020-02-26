package com.mps.insight.product;

import javax.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.global.TableMapper;

public class AccountUser {

	private RequestMetaData rmd =null;
	private static final Logger LOG = LoggerFactory.getLogger(AccountUser.class);
	PublisherSettings pubsetting=null;
	
	public AccountUser(RequestMetaData rmd){
		this.rmd =rmd;
	}
	
	
	
	
	public String linkUserAccount(UserDTO user) {
		InsightDAO insightDao = null;

		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(user.getWebmartID());
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			if(user.getAccounts() != null) {
				String[] accArray = user.getAccounts().split(",");
				for(String account : accArray) {
					stb = new StringBuilder();
					stb.append("INSERT into "+TableMapper.TABALE.get("user_account_table")+" (`"+TableMapper.TABALE.get("user_code")+"`,`"+TableMapper.TABALE.get("account_code")+"`,`role_id`,`description`,`created_by`,`created_at`) ")
					.append("VALUES ('"+user.getUserCode().replaceAll("'", "''")+"','"+account+"','"+user.getRole()+"','','"+user.getCreatedBy().replaceAll("'", "''")+"', NOW());");
					
					rmd.log("query " + stb.toString());
					
					insightDao.executeInsertUpdateQuery(stb.toString());
				}
			}
			
		} catch (Exception e) {
			rmd.exception("Exception while insertion query ");
			return "Fail";
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return "User Linked ";
	}

	public JsonObject searchAccountsByUser(String userCode, int setNo,int webmartID) {
		InsightDAO insightDao = null;
		JsonObject jsonRecords = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			/*stb.append("SELECT a.id AS accountID,a.code AS accountCode,um.user_id AS userID,")
			.append("um.role_id AS accountRole,rm.role AS `roleName`, CONCAT(a.name, ' - ', a.code) AS accountName,")
			.append("a.type AS accountType FROM "+TableMapper.TABALE.get("user_account_table")+" um LEFT JOIN accounts a ON ")
			.append("um.account_id=a.code LEFT JOIN role_master rm ON um.role_id=rm.role_id WHERE user_id='"+userid+"' AND "
					+ "a.set_no=(SELECT MAX(set_no) FROM accounts) ")
			
			.append("UNION ALL ")
			.append("SELECT `id`, `code`, '' AS userID, '' AS accountRole, '' AS roleName, CONCAT(`name`, ' - ' , `code`), `type` ") 
			.append("FROM `accounts` WHERE CODE IN(SELECT child_id FROM account_parent_child WHERE parent_id IN(SELECT ")
			.append("a.code AS accountCode ")
			.append("FROM "+TableMapper.TABALE.get("user_account_table")+" um ") 
			.append("LEFT JOIN accounts a ON um.account_id=a.code ")
			.append("LEFT JOIN role_master rm ON um.role_id=rm.role_id ")
			.append("WHERE user_id='"+userid+"' AND a.set_no=(SELECT MAX(set_no) FROM accounts))) AND set_no = (SELECT MAX(set_no) FROM accounts)");
		*/
			/*stb.append("  SELECT ua."+TableMapper.TABALE.get("account_code")+" as 'accountCode', ua."+TableMapper.TABALE.get("role_id")+", rm.role AS `roleName`, CONCAT(a.Account_name, ' - ', a.account_code) AS accountName, a.account_Type as 'accountType' FROM `"+TableMapper.TABALE.get("user_account_table")+"` ua ")
			.append(" LEFT JOIN c5_accounts a ON ua."+TableMapper.TABALE.get("account_code")+"=a."+TableMapper.TABALE.get("account_code")+" ")
			.append( "LEFT JOIN `role_master` rm ON ua.role_id=rm.role_id ")
			.append(" WHERE user_code='"+userCode+"'");
			
			*stb.append("SELECT ")
			.append("ua."+TableMapper.TABALE.get("account_code")+" AS 'accountCode', ")
			.append("u."+TableMapper.TABALE.get("role_id")+", rm.role AS 'roleName', ")
			.append("CONCAT(a."+TableMapper.TABALE.get("account_name")+", ' - ', a."+TableMapper.TABALE.get("account_code")+") AS accountName, ")
			.append("a."+TableMapper.TABALE.get("account_type")+" AS 'accountType' ")
			.append("FROM `"+TableMapper.TABALE.get("user_account_table")+"` ua ")
			.append("LEFT JOIN "+TableMapper.TABALE.get("user_table")+" u ON ua."+TableMapper.TABALE.get("user_code")+"=u."+TableMapper.TABALE.get("user_code")+" ")
			.append("LEFT JOIN "+TableMapper.TABALE.get("account_table")+" a ON ua."+TableMapper.TABALE.get("account_code")+"=a."+TableMapper.TABALE.get("a_account_code")+" ")
			.append("LEFT JOIN `role_master` rm ON u."+TableMapper.TABALE.get("role_id")+"=rm.role_id ") 
			.append("WHERE ua."+TableMapper.TABALE.get("user_code")+"='"+userCode+"'");
			
			
			*/
			
			
			stb.append("SELECT ")
			.append("a."+TableMapper.TABALE.get("account_code")+" AS accountCode, ")
			.append("rm.role AS `roleName`,  ")
			.append("CONCAT(a."+TableMapper.TABALE.get("account_name")+", ' - ', a."+TableMapper.TABALE.get("account_code")+") AS accountName, ")
			.append("a."+TableMapper.TABALE.get("account_type")+" AS accountType  ")
			.append("FROM "+TableMapper.TABALE.get("user_account_table")+" um  ")
			.append("LEFT JOIN "+TableMapper.TABALE.get("account_table")+" a ON um."+TableMapper.TABALE.get("account_code")+"=a."+TableMapper.TABALE.get("account_code")+" ") 
			.append("LEFT JOIN role_master rm ON um."+TableMapper.TABALE.get("role_id")+"=rm."+TableMapper.TABALE.get("role_id")+"  ")
			.append("WHERE "+TableMapper.TABALE.get("user_code")+"='"+userCode+"'")

			.append("UNION ALL ") 

			.append("SELECT  ")
			.append("a."+TableMapper.TABALE.get("account_code")+" AS accountCode, ")
			.append("'' AS roleName,  ")
			.append("CONCAT(a."+TableMapper.TABALE.get("account_name")+", ' - ' , a."+TableMapper.TABALE.get("account_code")+") AS accountName, ")
			.append("a."+TableMapper.TABALE.get("account_type")+" AS accountType ")
			
			.append("FROM "+TableMapper.TABALE.get("account_table")+" a  ")
			.append("WHERE "+TableMapper.TABALE.get("account_code")+" IN( ")
			.append("SELECT child_"+TableMapper.TABALE.get("account_code")+" ")
			.append("FROM "+TableMapper.TABALE.get("c5_account_parent_child")+" ")
			.append("WHERE "+TableMapper.TABALE.get("account_code")+" IN( ")
			.append("SELECT  ")
			.append("a."+TableMapper.TABALE.get("account_code")+" AS accountCode ") 
			.append("FROM "+TableMapper.TABALE.get("user_account_table")+" um ")  
			.append("LEFT JOIN "+TableMapper.TABALE.get("account_table")+" a ON um."+TableMapper.TABALE.get("account_code")+"=a."+TableMapper.TABALE.get("account_code")+" ") 
			.append("LEFT JOIN role_master rm ON um."+TableMapper.TABALE.get("role_id")+"=rm."+TableMapper.TABALE.get("role_id")+" ") 
			.append("WHERE "+TableMapper.TABALE.get("user_code")+"='"+userCode+"')) ");
			
			
			MyDataTable mdt = insightDao.executeSelectQueryMDT(stb.toString());
			LOG.info("query " + stb.toString());
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			LOG.info("Exception while executing query");
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return jsonRecords;
	}
	
	public long emailAlertByCounterReport(String userCode, String checkedValue) {
		InsightDAO insightDao = null;
		StringBuilder stb = new StringBuilder();
		long status =-2;
		try {
			pubsetting=new PublisherSettings(rmd);
			String publisher_name = pubsetting.getPublisherCode(rmd.getWebmartID());
			insightDao =rmd.getInsightDao();
			
			if(checkedValue.equalsIgnoreCase("yes") || checkedValue.equalsIgnoreCase("1")){
				checkedValue ="Yes";
			}else{
				checkedValue ="No";
			}
			
			stb.append("UPDATE `"+TableMapper.TABALE.get("user_table")+"` SET email_alert="+"'"+checkedValue+"'"+" where code="+"'"+userCode+"'");
			status =insightDao.executeInsertUpdateQuery(stb.toString());
			
		} catch (Exception e) {
			LOG.info("Exception while executing query");
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return status;
	}
	
	
	public String updateUserAccountRole(UserDTO user) {
		InsightDAO insightDao = null;

		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(user.getWebmartID());
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			if(user.getAccounts() != null) {
				String[] accArray = user.getAccounts().split(",");
				for(String account : accArray) {
					stb = new StringBuilder();
					stb.append("UPDATE "+TableMapper.TABALE.get("user_account_table")+" SET `role_id`='"+user.getRoleID())
					.append("' WHERE user_id='"+user.getUserId()+" and ")
					.append("account_id='"+account+"'");
					insightDao.executeInsertUpdateQuery(stb.toString());
				}
			}

			LOG.info("query " + stb.toString());
		} catch (Exception e) {
			LOG.info("Exception while insertion query ");
			return "Fail";
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return "Updated";
	}
	
	public String deleteUserAccount(UserDTO user) {
		InsightDAO insightDao = null;

		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(user.getWebmartID());
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			
					stb = new StringBuilder();
					stb.append("DELETE FROM `"+TableMapper.TABALE.get("user_account_table")+"` WHERE "+TableMapper.TABALE.get("user_code")+"='"+user.getUserCode())
					.append("' and "+TableMapper.TABALE.get("account_code")+"='"+user.getAccounts()+"'");
					insightDao.executeInsertUpdateQuery(stb.toString());
				
			rmd.log("query " + stb.toString());
		} catch (Exception e) {
			rmd.exception("Exception while insertion query ");
			return "Fail";
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return "Deleted";
	}
	
	public JsonObject searchUnlinkedAccountsByUser(int userid, int setNo,int webmartID) {
		InsightDAO insightDao = null;
		JsonObject jsonRecords = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT a.account_code AS accountCode,a.account_name AS accountName FROM ")
			.append("`"+TableMapper.TABALE.get("c5_accounts")+"` a WHERE a.status=1 AND a.account_code NOT IN (SELECT DISTINCT(account_id) ")
			.append("FROM "+TableMapper.TABALE.get("user_account_table")+" WHERE user_id='"+userid+"')");

			MyDataTable mdt = insightDao.executeSelectQueryMDT(stb.toString());
			LOG.info("query " + stb.toString());
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			LOG.info("Exception while executing query");
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return jsonRecords;
	}

}
