package com.mps.insight.impl;

import javax.json.JsonObject;

import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.product.AccountUser;
import com.mps.insight.product.ManageUser;

public class PublisherUser {
	private RequestMetaData rmd; 
	ManageUser usermanager=null;
	AccountUser accountUser=null;
	
	public PublisherUser(RequestMetaData rmd) {
		this.rmd = rmd;
		usermanager=new ManageUser(rmd);
		accountUser=new AccountUser(rmd);
	}
	
//	private static final Logger LOG = LoggerFactory.getLogger(PublisherUser.class);
	
	
	public long createPubUser(UserDTO udto) throws Exception{
		
		return usermanager.createUser(udto);
	}
	
	public long createPubUser(UserDTO udto, boolean updateDuplicateStatus) throws Exception{
		
		return usermanager.createUser(udto, updateDuplicateStatus);
	}

	public String deletePubUser(String userCode,int webmartID) {
		
		return usermanager.deleteUser(userCode,webmartID);
	}
	
	public String activateUser(String userCode) {
		
		return usermanager.activateUser(userCode);
	}
	
	public JsonObject getUserList(int webmartID,String userCode,String userType) throws Exception {

		return usermanager.getUserByWebmartID(webmartID,userCode, userType);
	}
	
	/*public JsonObject getPubUserToEdit(int userID,int webmartID) {

		return usermanager.getUserByUserID(userID,webmartID);
	}*/
	
	public JsonObject getPubUserToEdit(String userCode) {

		return usermanager.getUserByUserCode(userCode);
	}
	
	public String editPubUser(UserDTO user, int webmartID) {
		try{
		String result=usermanager.editUser(user, webmartID);

		rmd.log("editPubUser impl : User Edited in user table "+result);
		if(null!=user.getPassword() && user.getPassword().length()>0){
			result=usermanager.updatePassword(user);
		rmd.log("Password Updated ");
		}
		}catch(Exception e){
			rmd.log("Exception in editPubUser : ");
			return "error";
		}
		return "success";
	}

	public ManageUser getUsermanager() {
		return usermanager;
	}

	public void setUsermanager(ManageUser usermanager) {
		this.usermanager = usermanager;
	}

	public String createUserAccount(UserDTO user) {
		
		return accountUser.linkUserAccount(user);
	}
	
	public String deleteUserAccount(UserDTO user) {
		
		return accountUser.deleteUserAccount(user);
	}

	public JsonObject searchAccountsByUser(String userCode, int setNo,int webmartID) {
		
		return accountUser.searchAccountsByUser(userCode, setNo,webmartID);
	}

	public JsonObject getAllRoles(int webmartId) {
		
		return usermanager.getAllRoles(webmartId);
	}

	public long emailAlertByCounterReport(String userCode, String checkedValue) {
		return accountUser.emailAlertByCounterReport(userCode,checkedValue);
		
	}

	public String getCurrentPassword(UserDTO user) {
		return usermanager.getCurrentPassword(user);
	}

	
}
