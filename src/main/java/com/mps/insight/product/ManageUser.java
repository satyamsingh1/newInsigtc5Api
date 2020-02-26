package com.mps.insight.product;

import javax.json.JsonObject;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;
import com.mps.insight.security.EncoderDecoder;

public class ManageUser {

	//private static final Logger LOG = LoggerFactory.getLogger(ManageUser.class);
	public AccountUser accountUser;
	PublisherSettings pubsetting=null;
	private RequestMetaData rmd; 
	
	public ManageUser(RequestMetaData rmd) {
		this.rmd = rmd;
	}
	
	
	public long createUser(UserDTO user) {
		InsightDAO insightDao = null;
		long newId = 0;
		String uerCode = "";
		accountUser = new AccountUser(rmd);
		StringBuilder stb = new StringBuilder();
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(user.getWebmartID());
			insightDao =rmd.getInsightDao();
			stb.append("INSERT INTO "+TableMapper.TABALE.get("user_table")+" ("+TableMapper.TABALE.get("user_code")+","+TableMapper.TABALE.get("email_id")+",")
					.append(TableMapper.TABALE.get("role_id")+","+TableMapper.TABALE.get("first_name")+","+TableMapper.TABALE.get("last_name")+",")
					.append(TableMapper.TABALE.get("password")+", lp3, "+TableMapper.TABALE.get("status")+",")
					.append(TableMapper.TABALE.get("user_type")+",`created_by`,`created_at`,`updated_by`,`updated_at`) VALUES (")
					.append("'" + user.getUserCode().replaceAll("'", "''") + "','" + user.getEmailID().replaceAll("'", "''") + "', '" + user.getRoleID())
					.append("','" + user.getFirstName().replaceAll("'", "''") + "','" + user.getLastName().replaceAll("'", "''") + "','" + user.getPassword())
					.append("','" + user.getLastPassword3())
					.append("','" + user.getStatus() + "','" + user.getUserType() + "','" + user.getCreatedBy().replaceAll("'", "''") + "',")
					.append("NOW() ,'" + user.getCreatedBy().replaceAll("'", "''") + "',NOW());");

			newId = insightDao.executeInsertUpdateQuery(stb.toString());
			
			rmd.log("user ID : " + newId + "   query " + stb.toString());
			
			if (newId == 1) {
				String userCode = getUserIDfromUserCode(user.getUserCode(), user.getPublisherID(),user.getWebmartID());
				user.setUserCode(userCode);
				if (user.getUserType().contains("Library") && user.getAccounts()!=null && !user.getAccounts().trim().equals("")) {
					accountUser.linkUserAccount(user);
				} else if (user.getUserType().contains("Sushi")) {
					stb = new StringBuilder();
					stb.append(
							"INSERT INTO user_ips (`user_id`,`display_ip`,`ip_low`,`ip_high`,`updated_by`,`updated_at`) ")
							.append("VALUES ('"+user.getUserId()+"','"+user.getLowIP()+"',INET_ATON('"+user.getLowIP()+"'),")
							.append("INET_ATON('"+user.getHighIP()+"'),'"+user.getEmailID()+"',NOW())");
					newId = insightDao.executeInsertUpdateQuery(stb.toString());
					rmd.log("User IP inserted : "+newId);
				}
			}


		} catch (Exception e) {
			rmd.log("Exception while insertion query " + e.getMessage());

		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return newId;
	}

	public long createUser(UserDTO user, boolean duplicateAllow) {
		InsightDAO insightDao = null;
		long newId = 0;
		String uerCode = "";
		accountUser = new AccountUser(rmd);
		StringBuilder stb = new StringBuilder();
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(user.getWebmartID());
			insightDao =rmd.getInsightDao();
			stb.append("INSERT INTO "+TableMapper.TABALE.get("user_table")+" ("+TableMapper.TABALE.get("user_code")+","+TableMapper.TABALE.get("email_id")+",")
					.append(TableMapper.TABALE.get("role_id")+","+TableMapper.TABALE.get("first_name")+","+TableMapper.TABALE.get("last_name")+","+TableMapper.TABALE.get("password")+",")
					.append(" lp3, "+TableMapper.TABALE.get("status")+",")
					.append(TableMapper.TABALE.get("user_type")+",`created_by`,`created_at`,`updated_by`,`updated_at`) VALUES (")
					.append("'" + user.getUserCode().replaceAll("'", "''") + "','" + user.getEmailID().replaceAll("'", "''") + "', '" + user.getRoleID())
					.append("','" + user.getFirstName().replaceAll("'", "''") + "','" + user.getLastName().replaceAll("'", "''") + "','" + user.getPassword())
					.append("','" + user.getLastPassword3())
					.append("','Live','" + user.getUserType() + "','" + user.getCreatedBy().replaceAll("'", "''") + "',")
					.append("NOW() ,'" + user.getCreatedBy().replaceAll("'", "''") + "',NOW()) ")
					.append("ON DUPLICATE KEY UPDATE ")
					.append("user_code = VALUES (user_code), ")
					.append("email_id = VALUES (email_id), ")
					.append("`role_id` = VALUES (role_id), ")
					.append("first_name = VALUES (first_name), ")
					.append("last_name = VALUES (last_name), ")
					.append("PASSWORD = VALUES (PASSWORD), ")
					.append("lp3 = VALUES (lp3), ")
					.append("`status` = VALUES (STATUS), ")
					.append("user_type = VALUES (user_type), ")
					.append("`created_by` = VALUES (created_by), ")
					.append("`created_at` = VALUES (created_at), ")
					.append("`updated_by` = VALUES (updated_by), ")
					.append("`updated_at` = VALUES (updated_at);");

			newId = insightDao.executeInsertUpdateQuery(stb.toString());
			
			rmd.log("user ID : " + newId + "   query " + stb.toString());
			
			if (newId == 1) {
				String userCode = getUserIDfromUserCode(user.getUserCode(), user.getPublisherID(),user.getWebmartID());
				user.setUserCode(userCode);
				if (user.getUserType().contains("Library") && user.getAccounts()!=null && !user.getAccounts().trim().equals("")) {
					accountUser.linkUserAccount(user);
				} else if (user.getUserType().contains("Sushi")) {
					stb = new StringBuilder();
					stb.append(
							"INSERT INTO user_ips (`user_id`,`display_ip`,`ip_low`,`ip_high`,`updated_by`,`updated_at`) ")
							.append("VALUES ('"+user.getUserId()+"','"+user.getLowIP()+"',INET_ATON('"+user.getLowIP()+"'),")
							.append("INET_ATON('"+user.getHighIP()+"'),'"+user.getEmailID()+"',NOW())");
					newId = insightDao.executeInsertUpdateQuery(stb.toString());
					rmd.log("User IP inserted : "+newId);
				}
			}


		} catch (Exception e) {
			rmd.log("Exception while insertion query " + e.getMessage());

		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return newId;
	}

	public String editUser(UserDTO user, int webmartID) {
		InsightDAO insightDao = null;
		String result = "edited";
		try {
			pubsetting=new PublisherSettings(rmd);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			stb.append("UPDATE "+TableMapper.TABALE.get("user_table")+" SET ");
			if (null != user.getEmailID() && user.getEmailID().length() > 0) {
				stb.append(TableMapper.TABALE.get("email_id")+"='" + user.getEmailID().replaceAll("'", "''") + "',");
			}
			if (null != user.getFirstName() && user.getFirstName().length() > 0) {
				stb.append(TableMapper.TABALE.get("first_name")+"='" + user.getFirstName().replaceAll("'", "''") + "',");
			}
			if (null != user.getLastName() && user.getLastName().length() > 0) {
				stb.append(TableMapper.TABALE.get("last_name")+"='" + user.getLastName().replaceAll("'", "''") + "',");
			}
			if (null != user.getQuestion() && user.getQuestion().length() > 0) {
				stb.append(TableMapper.TABALE.get("question")+"='" + user.getQuestion().replaceAll("'", "''") + "',");
			}
			if (null != user.getPassword() && user.getPassword().length() > 0) {
				stb.append(TableMapper.TABALE.get("password")+"='" + user.getPassword().replaceAll("'", "''") + "',");
			}
			try {

				if (!user.getRoleID().equalsIgnoreCase("-1") && !user.getRoleID().equalsIgnoreCase("")) {
					stb.append(TableMapper.TABALE.get("role_id")+"='" + user.getRoleID() + "',");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (null != user.getAnswer() && user.getAnswer().length() > 0) {
				stb.append(TableMapper.TABALE.get("answer")+"='" + user.getAnswer().replaceAll("'", "''") + "',");
			}
			if (null != user.getStatus() && user.getStatus().length() > 0) {
				stb.append(TableMapper.TABALE.get("status")+"='" + user.getStatus() + "',");
			}
			stb.append("`updated_by`='" + user.getUpdatedBy() + "',");
			stb.append("`updated_at`= now()");
			stb.append(" WHERE "+TableMapper.TABALE.get("user_code")+" = '" + user.getUserCode().replaceAll("'", "''")+"'");
			insightDao.executeInsertUpdateQuery(stb.toString());

		} catch (Exception e) {
			rmd.log("Exception in Edit User : " + e.getMessage());
			result = "fail";
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return result;
	}

	public String deleteUser(String userCode,int webmartID) {
		InsightDAO insightDao = null;
		Long userReturnid = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			stb.append("UPDATE "+TableMapper.TABALE.get("user_table")+" u SET u.status='Deleted' WHERE u."+TableMapper.TABALE.get("user_code")+"='" + userCode+"'");
			userReturnid = insightDao.executeInsertUpdateQuery(stb.toString());
			rmd.log(userReturnid.toString());
		} catch (Exception e) {
			return InsightConstant.ERROR;
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return "Deleted";
	}
	
	public String activateUser(String userCode) {
		InsightDAO insightDao = null;
		Long userReturnid = null;
		try {
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			
			
			stb.append("INSERT INTO `c5_user_master_reactivated` ")
			.append("(`api_key`,`user_code`,`email_id`,`role_id`,`first_name`,")
			.append("`last_name`,`password`,`lp1`,`lp2`,`lp3`,`status`,")
			.append("`user_type`,`question`,`answer`,`email_alert`,`sushi`,")
			.append("`created_by`,`created_at`,`updated_by`,`updated_at`,`last_login`) ")
			.append("SELECT `api_key`,`user_code`,`email_id`,`role_id`,`first_name`,")
			.append("`last_name`,`password`,`lp1`,`lp2`,`lp3`,`status`,`user_type`,")
			.append("`question`,`answer`,`email_alert`,`sushi`,`created_by`,")
			.append("`created_at`,`updated_by`,`updated_at`,`last_login` ")
			.append("FROM c5_user_master WHERE user_code='"+userCode+"'");
			
			userReturnid = insightDao.executeInsertUpdateQuery(stb.toString());

			rmd.log(userReturnid.toString());
			
		} catch (Exception e) {
			return InsightConstant.ERROR;
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return "Activated";
	}

	public JsonObject getUserByUserID(int userID,int webmartID) {
		InsightDAO insightDao = null;
		MyDataTable mdt = null;
		JsonObject tempObject = null;
		String userType = "";
		Users user = null;

		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			user = new Users(rmd);
			rmd.log("getUserByUserID : userID=" + userID);
			userType = user.getUserTypeByUserID(userID,webmartID);
			StringBuilder stb = new StringBuilder();
			if ("SushiPartner".equalsIgnoreCase(userType)) {
				stb.append(
						"SELECT u.id AS userID,u.code AS partnerCode, u.email_id AS email,u.first_name AS partnerName,")
						.append("u.user_type AS userType,INET_NTOA(uip.ip_low) AS lowIP,INET_NTOA(uip.ip_high) AS highIP ")
						.append("FROM "+TableMapper.TABALE.get("user_table")+" u LEFT JOIN user_ips uip ON u.id=uip.user_id WHERE u.id=" + userID
								+ " LIMIT 1");
			} else {
				stb.append("SELECT u.id AS userID, u.code AS userCode, u.email_id AS email,u.first_name AS firstName,")
						.append("u.last_name AS lastName,u.user_type AS userType,r.role_id AS roleID,")
						.append("r.role AS roleName,u.question AS `Question`,u.answer AS `Answer`,u.password as `password` ")
						.append("FROM "+TableMapper.TABALE.get("user_table")+" u LEFT JOIN role_master r ON u.role_id=r.role_id WHERE u.id="
								+ userID);
			}
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			String decrptPassword = mdt.getValue(1, "password");
			EncoderDecoder ed=new EncoderDecoder();
			String orgPass = ed.decrypt(decrptPassword);
			mdt.updateData(1, "password", orgPass);
			tempObject = mdt.getJson();
			
		} catch (Exception e) {
			rmd.log("Exception in getUserByUserID");
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt = null;
		}

		return tempObject;
	}

	public JsonObject getUserByWebmartID(int webmartID, String userCode, String userType) throws Exception {
		InsightDAO insightDao = null;
		MyDataTable mdt = null;
		JsonObject tempObject = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("getPublisherIDFromWebmartID : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
		/*	stb.append(
					"SELECT DISTINCT um.id AS userID,um.code as userCode,um.email_id AS Email,um.first_name as firstName,um.last_name as lastName, ")
					.append("um.role_id AS roleID,rm.role AS role FROM role_master rm LEFT JOIN "+TableMapper.TABALE.get("user_table")+" um ")
					.append("ON rm.role_id=um.role_id WHERE um.user_type='" + userType + "' AND um.status!='Deleted' AND ");
			if (userType.equalsIgnoreCase("Publisher")) {
				stb.append("um.id!=" + userID + " AND rm.role IN (" + InsightConstant.PUB_USER_ARRAY + ")");
			} else if (userType.equalsIgnoreCase("LibraryClients")) {
				stb.append("um.id!=" + userID + " AND rm.role IN (" + InsightConstant.LIB_USER_ARRAY + ")");
			}
			*/
			String roleModule ="";
			if("LibraryClients".equalsIgnoreCase(userType)){
				roleModule = "library";
			}else if("Publisher".equalsIgnoreCase(userType)){
				roleModule = "publisher";
			}else{
				roleModule = userType;
			}
			
			stb.append(
					"SELECT DISTINCT um."+TableMapper.TABALE.get("user_code")+" as userCode, um."+TableMapper.TABALE.get("email_id")+" AS Email, um."+TableMapper.TABALE.get("first_name")+" as firstName, um."+TableMapper.TABALE.get("last_name")+" as lastName, ")
					.append("um."+TableMapper.TABALE.get("role_id")+" AS roleID, rm.role AS role FROM role_master rm LEFT JOIN "+TableMapper.TABALE.get("user_table")+" um ")
					.append("ON rm.role_id=um."+TableMapper.TABALE.get("role_id")+" WHERE um."+TableMapper.TABALE.get("user_type")+"='" + userType + "' AND um."+TableMapper.TABALE.get("status")+"!='Deleted' and rm.module='"+roleModule+"'")
					.append(" AND um."+TableMapper.TABALE.get("email_id")+" NOT IN('', ' ', 'null')");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			tempObject = mdt.getJson();
			
		} catch (Exception e) {
			rmd.log("Exception in getUserByWebmartID");
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt = null;
		}

		return tempObject;
	}

	public String updatePassword(UserDTO user) {
		InsightDAO insightDao = null;
		try {
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			rmd.log("Updating Password for userCode : " + user.getUserCode());
			stb.append("UPDATE "+TableMapper.TABALE.get("user_table")+" SET `password`='" + user.getPassword() + "', lp3='"+user.getLastPassword3()+"' WHERE user_code='" + user.getUserCode()+"'");
			insightDao.executeInsertUpdateQuery(stb.toString());
			rmd.log("Password updated success : Query");
		} catch (Exception e) {
			return InsightConstant.ERROR;
		} 
		return InsightConstant.SUCCESS;
	}

	public String updateUserRole(int roleID, int userID,int webmartID) {
		InsightDAO insightDao = null;
		Long userReturnid = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			rmd.log("Updating updateUserRole for UserID : " + userID);
			stb.append("UPDATE "+TableMapper.TABALE.get("user_table")+" SET `role_id`='" + roleID + "' WHERE id=" + userID);
			userReturnid = insightDao.executeInsertUpdateQuery(stb.toString());
			rmd.log(userReturnid.toString() + " UserID updateUserRole update successfull");
		} catch (Exception e) {
			return InsightConstant.ERROR;
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return InsightConstant.SUCCESS;
	}

	public JsonObject getAllRoles(int webmartId) {
		InsightDAO insightDao = null;
		JsonObject jsonRecords = null;
		MyDataTable mdt = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartId);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();

			stb.append("SELECT rm.role_id AS roleID,rm.webmart_id AS webmartID,rm.module AS module,")
					.append("rm.role AS roleName,rm.abbreviation AS abbreviation,rm.permissions AS permission,rm.description AS description ")
					.append("FROM role_master rm WHERE rm.webmart_id=" + webmartId);
			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			if (mdt.getJsonData().size() == 0) {
				stb = new StringBuilder();
				stb.append("SELECT rm.role_id AS roleID,rm.webmart_id AS webmartID,rm.module AS module,")
						.append("rm.role AS roleName,rm.abbreviation AS abbreviation,rm.permissions AS permission,rm.description AS description ")
						.append("FROM role_master rm WHERE rm.webmart_id=-2");
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
			}

			rmd.log("query " + stb.toString());
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.log("Exception while executing query");
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return jsonRecords;
	}

	public String getUserIDfromUserCode(String userCode, int publisherID,int webmartID) {
		InsightDAO insightDao = null;
		MyDataTable mdt = null;
		String userID = "";
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT um."+TableMapper.TABALE.get("user_code")+" FROM "+TableMapper.TABALE.get("user_table")+" um WHERE um."+TableMapper.TABALE.get("user_code")+"='" + userCode+"'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			userID = mdt.getValue(1, 1);
		} catch (Exception e) {
			rmd.log("Exception in getUserIDfromUserCode");
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt = null;
		}

		return userID;
	}
	
	public String updateUserIP(long userID,String highIP,String lowIP,int webmartID) {
		InsightDAO insightDao = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			rmd.log("Updating Password for UserID : " + userID);
			stb.append("UPDATE user_ips ip SET ip_low=INET_ATON('"+lowIP+"'),ip_high=INET_ATON('"+highIP+"') WHERE user_id="+userID);
			insightDao.executeInsertUpdateQuery(stb.toString());
			rmd.log("updateUserIP updated success : Query");
		} catch (Exception e) {
			rmd.log("Exception in updateUserIP ");
			return InsightConstant.ERROR;
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return "updated";
	}


	public JsonObject getUserByUserCode(String userCode) {
		InsightDAO insightDao = null;
		MyDataTable mdt = null;
		JsonObject tempObject = null;
		String userType = "";
		Users user = null;

		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(rmd.getWebmartID());
			insightDao =rmd.getInsightDao();
			user = new Users(rmd);
			rmd.log("get User By UserCode : userCode=" + userCode);
			userType = user.getUserTypeByUserCode(userCode);
			StringBuilder stb = new StringBuilder();
			if ("SushiPartner".equalsIgnoreCase(userType)) {
				stb.append(
						"SELECT u."+TableMapper.TABALE.get("user_code")+" AS partnerCode, u.email_id AS email,u.first_name AS partnerName,")
						.append("u.user_type AS userType,INET_NTOA(uip.ip_low) AS lowIP,INET_NTOA(uip.ip_high) AS highIP, u.password AS `password` ")
						.append("FROM "+TableMapper.TABALE.get("user_table")+" u LEFT JOIN user_ips uip ON u.user_code=uip.user_code WHERE u."+TableMapper.TABALE.get("user_code")+"='" + userCode
								+ "' LIMIT 1");
			} else {
				stb.append("SELECT u."+TableMapper.TABALE.get("user_code")+" AS userCode, u.email_id AS email,u.first_name AS firstName,")
						.append("u.last_name AS lastName,u.user_type AS userType,r.role_id AS roleID,")
						.append("r.role AS roleName,u.question AS `Question`,u.answer AS `Answer`,u.password as `password`, u.lp3 as lastPassword  ")
						.append("FROM "+TableMapper.TABALE.get("user_table")+" u LEFT JOIN role_master r ON u.role_id=r.role_id WHERE u."+TableMapper.TABALE.get("user_code")+"='"
								+ userCode+"'");
			}
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			if(mdt==null || mdt.getRowCount()<1){
				rmd.exception("MDT found blank : query : "+stb.toString());
			}
			String decrptPassword = mdt.getValue(1, "password");
			EncoderDecoder ed=new EncoderDecoder();
			String orgPass = ed.decrypt(decrptPassword);
			try {
				if(orgPass==null){orgPass = ed.decrypt(mdt.getValue(1, "lastPassword"));}} catch (Exception e) {}
			
			mdt.updateData(1, "password", orgPass);
			tempObject = mdt.getJson();
			
		} catch (Exception e) {
			rmd.log("Exception in getUserByUserID");
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt = null;
		}

		return tempObject;
	}


	public long activateUser(UserDTO udto) {
		// TODO Auto-generated method stub
		return 0;
	}


	public String getCurrentPassword(UserDTO user) {
		StringBuilder query = new StringBuilder();
		String password="";
		try {
			query.append("SELECT u.password as `password` ")
			.append("FROM "+TableMapper.TABALE.get("user_table")+" u WHERE u."+TableMapper.TABALE.get("user_code")+"='"
					+ user.getUserCode()+"'");
			MyDataTable mdt = rmd.getInsightDao().executeSelectQueryMDT(query.toString());
			password = mdt.getValue(1, "password").toString();
		} catch (Exception e) {
			rmd.exception("Unable to get current Password : "+e.toString());
		}
		return password;
	}

}
