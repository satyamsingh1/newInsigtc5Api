package com.mps.insight.product;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.JsonArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.global.TableMapper;



public class Users {
	
	
	private static final Logger log = LoggerFactory.getLogger(Users.class);
	private RequestMetaData rmd =null;
	
	public Users(RequestMetaData rmd){
		this.rmd = rmd;
	}
	
	public Users(){
		super();
	}
	public String getUserTypeByUserID(int userID,int webmartID){
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		String userType="";
		String publisher_name;
		try {
			if(rmd!=null){
				insightDao =rmd.getInsightDao();
			}else{
				rmd=new RequestMetaData();
				publisher_name=rmd.getPublisherNameFromRedis(webmartID);
				insightDao = rmd.getInsightDao();
			}
			
			
			log.info("getUserTypeByUserID : userID=" + userID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT u."+TableMapper.TABALE.get("user_type")+" AS userType FROM "+TableMapper.TABALE.get("user_table")+" u WHERE u."+TableMapper.TABALE.get("id")+"="+userID);
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			userType=mdt.getValue(1, "userType");
			log.info(" tempObject : "+userType);
		} catch (Exception e) {
			log.info("Exception in getUserTypeByUserID "+e.getMessage());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}	*/
			}
		
		return userType;
	}
	
	
	public UserDTO getUserDetailByUserCode(String userCode, int webmartID){
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		UserDTO userDto=new UserDTO();
		String publisher_name = "";
		try {
			if(rmd!=null){
				insightDao =rmd.getInsightDao();
			}else{
				rmd=new RequestMetaData();
				publisher_name=rmd.getPublisherNameFromRedis(webmartID);
				insightDao = rmd.getInsightDao();
			}
			
			
			//log.info("getUserDetailByUserID : userID=" + userID);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT u."+TableMapper.TABALE.get("email_id")+" AS `email`,u."+TableMapper.TABALE.get("first_name")+" AS `firstName`,")
			.append("u."+TableMapper.TABALE.get("last_name")+" AS `lastName`,u."+TableMapper.TABALE.get("password")+" as `password`,u."+TableMapper.TABALE.get("status")+" AS `status`, u."+TableMapper.TABALE.get("user_type")+" AS `userType`,u."+TableMapper.TABALE.get("user_code")+" AS code,")
			.append("u."+TableMapper.TABALE.get("question")+" as question,u."+TableMapper.TABALE.get("answer")+" as answer, created_by AS createdBy ")
			.append("FROM "+TableMapper.TABALE.get("user_table")+" u WHERE u."+TableMapper.TABALE.get("status")+" != 'Deleted' AND u."+TableMapper.TABALE.get("user_code")+"='"+userCode+"'");
			
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			int rowCount = mdt.getRowCount();
			
			if(rowCount>0){
				userDto.setEmailID(mdt.getValue(1, "email"));
				userDto.setFirstName(mdt.getValue(1, "firstName"));
				userDto.setLastName(mdt.getValue(1, "lastName"));
				userDto.setPassword(mdt.getValue(1, "password"));
				userDto.setStatus(mdt.getValue(1, "status"));
				userDto.setUserType(mdt.getValue(1, "userType"));
				userDto.setUserCode(mdt.getValue(1, "code"));
				userDto.setQuestion(mdt.getValue(1, "question"));
				userDto.setAnswer(mdt.getValue(1, "answer"));
				userDto.setWebmartCode(publisher_name);
			}
			
			
			
			
		} catch (Exception e) {
			log.error("Users : Exception in getUserDetailByUserID "+e.getMessage());
		} finally {
			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
		}
		
		return userDto;
	}

	public JsonArray getUserByEmail(String email, String publisher_name){
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		JsonArray jarray = null;

		try {
			insightDao = InsightDAO.getInstance(publisher_name);
			log.info("Start method getUserByEmail : Email=" + email);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT u."+TableMapper.TABALE.get("question")+" AS `question`, u."+TableMapper.TABALE.get("user_code")+" AS `user_code` FROM "+TableMapper.TABALE.get("user_table")+" u WHERE u."+TableMapper.TABALE.get("email_id")+"= '"+email+"' AND u."+TableMapper.TABALE.get("status")+" != 'Deleted'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jarray = mdt.getJsonData();
			log.info("getUserByEmail : Json Data = " + jarray.toString());
		} catch (Exception e) {
			log.info("Exception in getUserByEmail"+e.getMessage());
		} finally {
			if (insightDao != null) {
				insightDao.disconnect();
			}
		}
		return jarray;
	}
	
	public long updateUserPassword(UserDTO userDto) {
		InsightDAO insightDao = null;
	    long recordseffected = 0;

	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    String updatedAt = dateFormat.format(new Date(System.currentTimeMillis()));
	    String custpwd=userDto.getPassword().trim();
		if (userDto.getId() == 0)
		{
			// if new user, lowercase userId
			userDto.setUserCode(userDto.getUserCode().toLowerCase());
		}
		
		try {
			
			
		
			log.info("start updateUserPassword method");
			StringBuilder stb = new StringBuilder();
			stb.append("UPDATE "+TableMapper.TABALE.get("user_table")+" u SET u."+TableMapper.TABALE.get("password")+"=")
			.append("'"+custpwd+"',")
			.append("u.lp3='"+userDto.getLastPassword3()+"', u.updated_by='"+userDto.getUserCode()+"',")
			.append("u.updated_at='"+updatedAt+"' WHERE u."+TableMapper.TABALE.get("user_code")+"='"+userDto.getUserCode()+"'");
			
			
			
			/*
			
			stb.append("UPDATE "+TableMapper.TABALE.get("user_table")+" u SET u.password=")
			.append("'"+custpwd+"',u.updated_by='"+userDto.getUserCode()+"',")
			.append("u.updated_at='"+updatedAt+"' WHERE u.id="+userDto.getId());*/
		// recordseffected = insightDao.executeInsertUpdateQuery(stb.toString());
			log.info("updateUserPassword : Password updated");
			return recordseffected;

		} catch (Exception e) {
			log.info("Exception in updateUserPassword");
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
		}
		return recordseffected;
	}
	
	public UserDTO getUserDetailByUserIDAndPublisher(String userCode,String publisher_name){
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		UserDTO userDto=new UserDTO();
		try {
			insightDao = InsightDAO.getInstance(publisher_name);
			log.info("getUserDetailByUserID : userID=" + userCode);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT u."+TableMapper.TABALE.get("email_id")+" AS `email`,u."+TableMapper.TABALE.get("first_name")+" AS `firstName`,")
			.append("u."+TableMapper.TABALE.get("last_name")+" AS `lastName`,u."+TableMapper.TABALE.get("password")+" as `password`,u."+TableMapper.TABALE.get("status")+" AS `status`, u."+TableMapper.TABALE.get("user_type")+" AS `userType`,u."+TableMapper.TABALE.get("user_code")+" AS code,")
			.append("u."+TableMapper.TABALE.get("question")+" as question,u."+TableMapper.TABALE.get("answer")+" as answer, created_by AS createdBy ")
			.append("FROM "+TableMapper.TABALE.get("user_table")+" u WHERE u."+TableMapper.TABALE.get("status")+" != 'Deleted' AND u."+TableMapper.TABALE.get("user_code")+"='"+userCode+"'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			int rowCount = mdt.getRowCount();
			
			if(rowCount>0){
				userDto.setEmailID(mdt.getValue(1, "email"));
				userDto.setFirstName(mdt.getValue(1, "firstName"));
				userDto.setLastName(mdt.getValue(1, "lastName"));
				userDto.setPassword(mdt.getValue(1, "password"));
				userDto.setStatus(mdt.getValue(1, "status"));
				userDto.setUserType(mdt.getValue(1, "userType"));
				userDto.setUserCode(mdt.getValue(1, "code"));
				userDto.setQuestion(mdt.getValue(1, "question"));
				userDto.setAnswer(mdt.getValue(1, "answer"));
				userDto.setWebmartCode(publisher_name);
			}
			
		} catch (Exception e) {
			log.info("Exception in getUserDetailByUserID"+e.getMessage());
		} finally {
			 
			if (insightDao != null) {
				insightDao.disconnect();
			}
		}
		
		return userDto;
	}

	public String getUserTypeByUserCode(String userCode) {
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		String userType="";
		String publisher_name;
		try {
			if(rmd!=null){
				insightDao =rmd.getInsightDao();
			}else{
				rmd=new RequestMetaData();
				publisher_name=rmd.getPublisherNameFromRedis(rmd.getWebmartID());
				insightDao = rmd.getInsightDao();
			}
			
			
			log.info("getUserTypeByUserCode : userCode=" + userCode);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT u."+TableMapper.TABALE.get("user_type")+" AS userType FROM "+TableMapper.TABALE.get("user_table")+" u WHERE u."+TableMapper.TABALE.get("user_code")+"='"+userCode+"'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			userType=mdt.getValue(1, "userType");
			log.info(" tempObject : "+userType);
		} catch (Exception e) {
			log.info("Exception in getUserTypeByUserID "+e.getMessage());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}	*/
			}
		
		return userType;
	}

	public String getCurrentPassword(UserDTO user) {
		StringBuilder query = new StringBuilder();
		String password="";
		
		InsightDAO insightDao = null;
		
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
