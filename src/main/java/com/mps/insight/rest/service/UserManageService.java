package com.mps.insight.rest.service;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.impl.PublisherUser;
import com.mps.insight.impl.SearchAccountImpl;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.product.Users;
import com.mps.insight.security.Authorization;
import com.mps.insight.security.EncoderDecoder;

@Path("manageuser") 
public class UserManageService {
	@Context
	private HttpServletRequest servletRequest; 
	
	RequestMetaData rmd;
	private static final Logger log = LoggerFactory.getLogger(UserManageService.class);
	String userCode;
	int webmartID;
	Authorization authorization;
	UserDTO currentUser;
/*
	public UserManageService(@HeaderParam("token") String token) throws MyException {
		log.info("UserManageService constructor called ...");
		try {
			if (token == null) {
				throw new MyException("Token : NULL");
			}
			if (token.trim().equalsIgnoreCase("")) {
				throw new MyException("Token : BLANK");
			}

			authorization = new Authorization();
			String decode = authorization.decryptData(token);
			String[] temp = decode.split("~#~");
			if (temp == null) {
				throw new MyException("decode split : NULL");
			}
			if (temp.length != 3) {
				throw new MyException("decode split : Invalid Length");
			}

			userID = Integer.parseInt(temp[1].trim());
			webmartID = Integer.parseInt(temp[2].trim());
			Users user = new Users();
			currentUser = user.getUserDetailByUserID(userID,webmartID);

			if (userID <= 0) {
				throw new MyException("userID : ZERO");
			}
			if (webmartID <= 0) {
				throw new MyException("webmartID : ZERO");
			}

		} catch (MyException e) {
			throw e;
		}

	}
*/
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUserList")
	public Response getUserList(@FormParam("userType") String userType) throws Exception {
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		JsonObject jobject =null;
		try {
		    iTracker = 1.0;
			//log.info(" In getUserList Method  : " + webmartID);
			rmd.log(" In getUserList Method  : " + webmartID);
			publisherUser = new PublisherUser(rmd);
			jobject = publisherUser.getUserList(webmartID, userCode, userType);
			iTracker = 2.0;
			rb = Response.status(200).entity(jobject.toString());
			
		} catch (Exception e) {
			//log.error("getUserList  Final Json Array : " + jobject.toString());
			rmd.error("UserManageService : getUserList : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			//log.error("Exception in getUserList : " + e.getMessage());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("editUserScreen")
	public Response getPubUserToEdit(@FormParam("userCode") String userCode) throws Exception {
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		try {
			iTracker = 1.0;
			//log.info(" In getPubUserToEdit Method Wbmart ID : " + webmartID);
			rmd.log("In getPubUserToEdit Method : userCode : "+userCode);
			publisherUser = new PublisherUser(rmd);
			JsonObject result = publisherUser.getPubUserToEdit(userCode);
			iTracker = 2.0;
			rb = Response.status(200).entity(result.toString());
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			//log.error("Exception In getPubUserToEdit Method : " + e.getMessage());
			rmd.error("UserManageService : getPubUserToEdit : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("deleteuser")
	public Response deleteUser(@FormParam("userCode") String userCode) throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		//log.info(" In deleteUser Method User ID : " + userID);
		rmd.log("In deleteUser Method start : userID : "+userCode);
		if (userCode==null ||userCode.equalsIgnoreCase("")) {
			throw new Exception("User Code empty");
		}
		try {
			iTracker = 1.0;
			publisherUser = new PublisherUser(rmd);
			String result = publisherUser.deletePubUser(userCode,webmartID);
			iTracker = 2.0;
			rb = Response.status(200).entity(InsightConstant.SUCCESS_RESPONSE + result + "\"}");
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			//log.error("In deleteUser method Exception : " + e.getMessage());
			rmd.error("UserManageService : deleteUser : iTracker : "+iTracker+" : "+e.toString());
		}
		//log.info(" In deleteUser Method End : " + userID);
		rmd.log("In deleteUser Method End:"+userCode);
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("createuser")
	public Response createUser(
			@FormParam("email") String email, @FormParam("firstname") String firstname,
			@FormParam("lastname") String lastname, @FormParam("password") String password, @FormParam("role") String role,
			@FormParam("accounts") String accounts, @FormParam("userType") String userType) throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		PublisherSettings ps = new PublisherSettings(rmd);
		SearchAccountImpl searchimpl=new SearchAccountImpl();
		EncoderDecoder ed=new EncoderDecoder();
		try {
			
			iTracker = 1.0;
			//log.info(" In createUser Method Start : " + userID);
			rmd.log("In createUser Method Start:"+userCode);
			Users user = new Users();
			currentUser = user.getUserDetailByUserCode(userCode,webmartID);
			iTracker = 2.0;
			publisherUser = new PublisherUser(rmd);
			UserDTO udto = new UserDTO();
			udto.setFirstName(firstname);
			udto.setLastName(lastname);
			udto.setEmailID(email);
			udto.setUserCode(email);
			if (userType.equalsIgnoreCase("Publisher")) {
				udto.setUserType("Publisher");
			} else if (userType.equalsIgnoreCase("LibraryClients")) {
				udto.setUserType("LibraryClients");
			}
		
				udto.setRoleID(role);
			
			if (rmd.getWebmartCode().equalsIgnoreCase("iopscience")) {
				udto.setPassword(ed.get_SHA_1_SecurePassword(password));
				udto.setLastPassword3(ed.encrypt(password));
			}else{
				udto.setPassword(ed.encrypt(password));
				
			}
				
			//udto.setPassword(ed.encrypt(password));
			//int pubID = ps.getPublisherIDFromWebmartID(webmartID);
			
			iTracker = 3.0;
			//udto.setPublisherID(pubID);
			udto.setStatus("New");
			udto.setCreatedBy(currentUser.getFirstName());
			iTracker = 4.0;
			udto.setWebmartID(webmartID);
			
			
			if (accounts != null && accounts.contains(",")) {
				if (userType.equalsIgnoreCase("LibraryClients")) {
					accounts=searchimpl.getAccountCodeFromSearch(accounts, webmartID);
					iTracker = 5.0;
				}
				iTracker = 6.0;
				udto.setAccounts(accounts.substring(0, accounts.length() - 1));
			} else if(accounts != null && accounts.contains("[{")){
				int startIndex = accounts.trim().indexOf("[{");
				int endIndex = accounts.trim().lastIndexOf("}]");
				String userAccount = accounts.substring(startIndex+2, endIndex);
				udto.setAccounts(userAccount);
				
				
			}else {
				iTracker = 7.0;
				udto.setAccounts(accounts);
			}
			
			
			String result = "";
			if (role.equalsIgnoreCase("")||role.equalsIgnoreCase("-1")) {
				result = "Please Select Role ";
			} else {
				long nUserId = publisherUser.createPubUser(udto);

				if (nUserId == 0) {
					result = "User Code already exist in our records pls Try diffrent Code ";
					String userStatuts = publisherUser.activateUser(email);
					result = udto.getUserType() + " User "+userStatuts+" successfully ";
					nUserId = publisherUser.createPubUser(udto, true);
					
				} else {
					result = udto.getUserType() + " User Created successfully ";
					iTracker = 8.0;
				}
			}

			rb = Response.status(200).entity(InsightConstant.SUCCESS_RESPONSE + result + "\"}");
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			//log.error("In createUser Method Exception : " + e.toString());
			rmd.error("UserManageService :createUser : iTracker : "+iTracker+" : "+e.toString());
		}
		//log.info(" In createUser Method End : " + userID);
		rmd.log("In createUser Method End:"+userCode);
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("edituser")
	public Response editUser(@FormParam("userCode") String  userCode, @FormParam("firstname") String firstname,
			@FormParam("lastname") String lastname, @FormParam("email") String email,
			@FormParam("password") String password, @FormParam("question") String question,
			@FormParam("answer") String answer, @FormParam("role") String role) throws MyException {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		EncoderDecoder ed=new EncoderDecoder();
		//log.info(" In editUser Method Start : " + userid);
          rmd.log(" In editUser Method Start: user Code: "+userCode);
		if (userCode ==null || userCode.equalsIgnoreCase("")) {
			throw new MyException("User id empty");
		}
		try {
			iTracker = 1.0;
			Users user = new Users();
			currentUser = user.getUserDetailByUserCode(rmd.getUserCode(),webmartID);
			iTracker = 2.0;
			publisherUser = new PublisherUser(rmd);
			UserDTO udto = new UserDTO();
			udto.setUserCode(userCode);
			udto.setFirstName(firstname);
			udto.setLastName(lastname);
			udto.setEmailID(email);
			udto.setRoleID(role);
			udto.setQuestion(question);
			udto.setAnswer(answer);
			if(null!=password && password.length()>2){
				if(rmd.getWebmartCode().equalsIgnoreCase("iopscience")){
					udto.setPassword(ed.get_SHA_1_SecurePassword(password));
					udto.setLastPassword3(ed.encrypt(password));	
				}else{
					udto.setPassword(ed.encrypt(password));	
					udto.setLastPassword3(publisherUser.getCurrentPassword(udto));
				}
					
			}else{
			udto.setPassword(password);
			}
			String temp=currentUser.getStatus();
			iTracker = 3.0;
			if(temp.equalsIgnoreCase("New")){
				udto.setStatus("Live");
			}
			
			udto.setUpdatedBy(currentUser.getUserCode());
			iTracker = 4.0;
			String result=null;
			if(udto.getPassword().equals(currentUser.getPassword())){
				result="Current password and Previous password can not be same";	
			}else{
				result = publisherUser.editPubUser(udto, webmartID);
			}
			rb = Response.status(200).entity(InsightConstant.SUCCESS_RESPONSE + result + "\"}");
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			//log.error("In editUser Method Exception : " + e.toString());
			rmd.error("UserManageService :createUser : iTracker : "+iTracker+" :"+e.toString());
		}
		//log.info(" In editUser Method Start : " + userID);
		rmd.log("In editUser Method Start:"+userCode);
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("searchaccountsbyuser")
	public Response searchAccountsByUser(@FormParam("user_code") String userCode) throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		//userID = rmd.getUserID();
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		JsonObject jarray;
		PublisherSettings ps;
		//log.info(" In searchAccountsByUser Method Start : " + userID);
          rmd.log("In searchAccountsByUser Method Start: userid: "+userCode);
		if (userCode == null) {
			throw new Exception("User id empty");
		}
		try {
			 iTracker = 1.0;
			publisherUser = new PublisherUser(rmd);
			ps = new PublisherSettings(rmd);
			int setNo = ps.getSetNo(webmartID);
			iTracker = 2.0;
			jarray = publisherUser.searchAccountsByUser(userCode, setNo,webmartID);
			rb = Response.status(200).entity(jarray.toString());

		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			//log.error("In searchAccountsByUser Method Exception : " + e.toString());
			rmd.error("UserManageService :searchAccountsByUser : iTracker : "+iTracker+" :"+e.toString());
		}
		//log.info(" In searchAccountsByUser Method End : " + userID);
		rmd.log("In searchAccountsByUser Method End:"+userCode);
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getallroles")
	public Response getAllRoles() throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		JsonObject jarray;
		//log.info(" In getAllRoles Method Start : " + userID);
        rmd.log(" In getAllRoles Method Start : " + userCode);
		try {
			iTracker = 1.0;
			publisherUser = new PublisherUser(rmd);
			jarray = publisherUser.getAllRoles(webmartID);
			iTracker = 2.0;
			rb = Response.status(200).entity(jarray.toString());

		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			//log.error("In getAllRoles Method Exception : " + e.toString());
			rmd.error("UserManageService :getAllRoles : iTracker : "+iTracker+" :"+e.toString());
		}
		//log.info(" In getAllRoles Method End : " + userID);
		rmd.log(" In getAllRoles Method End  ");
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("removeAccountUser")
	public Response deleteAccountsUser(@FormParam("userCode") String userCode, @FormParam("accounts") String accounts)
			throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		String result = "";
		UserDTO user = new UserDTO();
		//log.info(" In deleteAccountsUser Method Start : " + userID);
          rmd.log(" In deleteAccountsUser Method Start :userCode: " + userCode);
		if (userCode==null||  userCode.equalsIgnoreCase("")) {
			throw new Exception("User id empty");
		}
		try {
			iTracker = 1.0;
			Users users = new Users();
			currentUser = users.getUserDetailByUserCode(rmd.getUserCode(),webmartID);
			iTracker = 2.0;
			user.setUserCode(userCode);
			user.setAccounts(accounts);
			user.setUpdatedBy(currentUser.getEmailID());
			iTracker = 3.0;
			user.setWebmartID(webmartID);
			publisherUser = new PublisherUser(rmd);
			result = publisherUser.deleteUserAccount(user);
			iTracker = 4.0;
			rb = Response.status(200).entity(InsightConstant.SUCCESS_RESPONSE + result + "\"}");

		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			//log.error("In deleteAccountsUser Method Exception : " + e.toString());
			rmd.error("UserManageService :deleteAccountsUser : iTracker : "+iTracker+" :"+e.toString());
		}
		//log.info(" In deleteAccountsUser Method End : " + userID);
		rmd.log(" In deleteAccountsUser Method End :userCode: " + userCode);
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("linkAccountUser")
	public Response linkAccountsUser(@FormParam("userCode") String userCode, @FormParam("accounts") String accounts,
			@FormParam("role") String role) throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		String result = "";
		UserDTO user = new UserDTO();
		SearchAccountImpl searchimpl=new SearchAccountImpl();
		//log.info(" In linkAccountsUser Method Start : " + userID);
          rmd.log(" In linkAccountsUser Method Start :userCode: " + userCode);
		if (userCode==null || userCode.equalsIgnoreCase("null")) {
			throw new Exception("User id empty");
		}
		try {
			iTracker = 1.0;
			Users users = new Users();
			currentUser = users.getUserDetailByUserCode(rmd.getUserCode(),webmartID);
			iTracker = 2.0;
			user.setUserCode(userCode);
			accounts=searchimpl.getAccountCodeFromSearch(accounts, webmartID);
			iTracker = 3.0;
			user.setAccounts(accounts);
			user.setUpdatedBy(currentUser.getEmailID());
			iTracker = 4.0;
			user.setWebmartID(webmartID);
			user.setRole(role);
			publisherUser = new PublisherUser(rmd);
			result = publisherUser.createUserAccount(user);
			iTracker = 5.0;
			rb = Response.status(200).entity(InsightConstant.SUCCESS_RESPONSE + result + "\"}");
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			//log.error("In linkAccountsUser Method Exception : " + e.toString());
			rmd.error("UserManageService :linkAccountsUser : iTracker : "+iTracker+" :"+e.toString());
		}
		//log.info(" In linkAccountsUser Method End : " + userID);
		rmd.log(" In linkAccountsUser Method End : userCode: " + userCode);
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("activateUser")
	public Response activateUser(@FormParam("userCode") String userCode) throws Exception {
		//rmd = (RequestMetaData)servletRequest.getAttribute("UserEntity");
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		String result = "";
		//log.info(" In activateUser Method Start : " + userID);
         rmd.log(" In activateUser Method Start : userCode " + userCode);
		if (userCode==null || userCode.equalsIgnoreCase("")) {
			throw new Exception("User Code empty");
		}
		try {
			iTracker = 1.0;
			publisherUser = new PublisherUser(rmd);
			result = publisherUser.activateUser(userCode);
			iTracker = 2.0;
			rb = Response.status(200).entity(InsightConstant.SUCCESS_RESPONSE + result + "\"}");

		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			//log.error("In activateUser Method Exception : " + e.getMessage());
			rmd.error("UserManageService :linkAccountsUser : iTracker : "+iTracker+" :"+e.toString());
		}
		//log.info(" In activateUser Method End : " + userID);
		rmd.log(" In activateUser Method End : userCode" + userCode);
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("emailalert")
	public Response emailalert(@FormParam("userCode") String userCode, @FormParam("checkedValue") String checkedValue ) throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		String result="";
		JSONObject json = new JSONObject();
		long status= -2;
		try {
			iTracker = 1.0;
			publisherUser = new PublisherUser(rmd);
			iTracker = 2.0;
			status = publisherUser.emailAlertByCounterReport(userCode,checkedValue);
			if(status >= 1){
				result = "SUCCESS"; 
			}else{
				result = "FAIL"; 
			}
			
			json.put("Email_Alert ", status);
			json.put("Records_updated", result);
			
			rb = Response.status(200).entity(json.toString());
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			rmd.error("UserManageService :emailalert : iTracker : "+iTracker+" :"+e.toString());
		}
		
		return rb.build();
	}
}
