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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.impl.SushiPartner;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.security.Authorization;

@Path("sushipartner")
public class ManageSushiPartner {
	@Context
	private HttpServletRequest servletRequest; 
	RequestMetaData rmd;
	private static final Logger LOG = LoggerFactory.getLogger(ManageSushiPartner.class);
	String userCode;
	int webmartID;
	Authorization authorization;
	/*
	public ManageSushiPartner(@HeaderParam("token") String token) throws Exception {

		try {
			if (token == null) {
				throw new Exception("Token : NULL");
			}
			if (token.trim().equalsIgnoreCase("")) {
				throw new Exception("Token : BLANK");
			}

			authorization = new Authorization();
			String decode = authorization.decryptData(token);
			String[] temp = decode.split("~#~");
			if (temp == null) {
				throw new Exception("decode split : NULL");
			}
			if (temp.length != 3) {
				throw new Exception("decode split : Invalid Length");
			}

			userID = Integer.parseInt(temp[1].trim());
			webmartID = Integer.parseInt(temp[2].trim());

			if (userID <= 0) {
				throw new Exception("userID : ZERO");
			}
			if (webmartID <= 0) {
				throw new Exception("webmartID : ZERO");
			}

		} catch (Exception e) {
			throw e;
		}
	}
	*/
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getsushilist")
	public Response getSushiPartnerList() throws Exception {
		ResponseBuilder rb = null;
		SushiPartner sushiPartner = null;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		try {
			iTracker = 1.0;
			if (webmartID == 0) {
				iTracker = 2.0;
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//LOG.info(" In getSushiPartnerList for Wbmart ID : "+ webmartID);
			rmd.log(" In getSushiPartnerList start");
			sushiPartner = new SushiPartner(rmd);
			iTracker = 3.0;
			MyDataTable mdt = sushiPartner.getSushiPartner(webmartID);
			JsonObject jobject=mdt.getJson();
			iTracker = 4.0;
			rb = Response.status(200).entity(jobject.toString());
			//LOG.info("getSushiPartnerList  Final Json Array : " + jobject.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
			rmd.error("ManageSushiPartner : getSushiPartnerList : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("createSushiPartner")
	public Response createSushiPartner(@FormParam("partnerCode") String partnerCode,
			@FormParam("partnerName") String partnerName, @FormParam("email") String email, @FormParam("pwd") String pwd
			, @FormParam("highIP") String highIP
			, @FormParam("lowIP") String lowIP,
			@FormParam("roleID") String roleID) throws Exception {
		ResponseBuilder rb = null;
		SushiPartner sushiPartner = null;
		UserDTO udto=new UserDTO();
		PublisherSettings pubsetting=null;
		long newId=0;
		String result="fail";
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		try {
			iTracker = 1.0;
			rmd.log(" In createSushiPartner Method start ");
			if (webmartID == 0) {
				iTracker = 2.0;
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			pubsetting=new PublisherSettings(rmd);
			//int pubID=0;
			iTracker = 3.0;
			//pubID=pubsetting.getPublisherIDFromWebmartID(webmartID);
			iTracker = 3.0;
			//LOG.info(" In createSushiPartner Method  Wbmart ID : "+ webmartID);
			sushiPartner = new SushiPartner(rmd);
			boolean validateCode = sushiPartner.validatePartnerCode( partnerCode,webmartID);
			if(validateCode){
				iTracker = 4.0;
			rb = Response.status(200).entity("");
			if(null==pwd && null==highIP){
				iTracker = 5.0;
				throw new Exception("Please Enter IP or Password");
			}
			if(null==pwd || pwd.length()<1){
				//pwd="";
			}
			//udto.setPublisherID(pubID);
			udto.setRoleID(roleID);
			udto.setEmailID(email);
			udto.setUserCode(partnerCode);
			udto.setFirstName(partnerName);
			udto.setPassword(pwd);
			udto.setUserType("SushiPartner");
			udto.setHighIP(highIP);
			udto.setLowIP(lowIP);
			udto.setStatus("Live");
			newId=sushiPartner.createSushiUser(udto);
			if(newId==1){
				iTracker = 6.0;
				result="created";
			}
			rb = Response.status(200).entity("{\""+partnerCode+"\":\""+result+"\"}");
			}else{
				iTracker = 7.0;
				throw new Exception("Partner Code already Exit");
			}
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			rmd.error("ManageSushiPartner : createSushiPartner : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("deletesushipartner")
	public Response deleteSushiPartner(@FormParam("userCode") String userCode) throws Exception {
		ResponseBuilder rb = null;
		SushiPartner sushiPartner = null;
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		//userID = rmd.getUserID();
		try {
			iTracker = 1.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//LOG.info(" In deleteSushiPartner Method Wbmart ID : "+ webmartID);
			rmd.log("In deleteSushiPartner Method :userCode" +userCode);
			sushiPartner = new SushiPartner(rmd);
			iTracker = 2.0;
			String result=sushiPartner.deleteSushiUser(userCode,webmartID);
			iTracker = 3.0;
			rb = Response.status(200).entity("{\"success\":\""+result+"\"}");
			iTracker = 4.0;
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			rmd.error("ManageSushiPartner : deleteSushiPartner : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("editPartnerScreen")
	public Response getSushiPartnerToEdit(@FormParam("userCode") String userCode) throws Exception {
		ResponseBuilder rb = null;
		SushiPartner sushiPartner = null;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		//userID = rmd.getUserID();
		double iTracker = 0.0;
		try {
			iTracker = 1.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//LOG.info(" In getSushiPartnerToEdit Method for Wbmart ID : "+ webmartID);
			rmd.log("In getSushiPartnerToEdit Method start: userCode" +userCode);
			sushiPartner = new SushiPartner(rmd);
			iTracker = 2.0;
			JsonObject result=sushiPartner.getSushiUserToEdit(userCode,webmartID);
			iTracker = 3.0;
			rb = Response.status(200).entity(result.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			rmd.error("ManageSushiPartner : getSushiPartnerToEdit : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("editsushipartner")
	public Response editSushiPartner(@FormParam("userID") int userID,
			@FormParam("partnerName") String partnerName, @FormParam("email") String email,
			@FormParam("pwd") String pwd,
			@FormParam("highIP") String highIP,
			@FormParam("lowIP") String lowIP,
			@FormParam("roleID") String roleID) throws Exception {
		ResponseBuilder rb = null;
		SushiPartner sushiPartner = null;
		UserDTO udto=new UserDTO();
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		//userID = rmd.getUserID();
		double iTracker = 0.0;
		try {
			iTracker = 1.0;
			//LOG.info(" In editSushiPartner Method for Wbmart ID : "+ webmartID);
			rmd.log("In editSushiPartner Method start");
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			udto.setId(userID);
			udto.setHighIP(highIP);
			udto.setPassword(pwd);
			udto.setEmailID(email);
			udto.setFirstName(partnerName);
			udto.setLowIP(lowIP);
			udto.setRoleID(roleID);
			sushiPartner = new SushiPartner(rmd);
			iTracker = 2.0;
			String result=sushiPartner.editSushiUser(udto);
			iTracker = 3.0;
			rb = Response.status(200).entity("{\"success\":\""+result+"\"}");
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			rmd.error("ManageSushiPartner : editSushiPartner : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("accountsushipartner")
	public Response getAccountSushiPartner(@FormParam("accountID") String accountID) throws Exception {
		ResponseBuilder rb = null;
		SushiPartner sushiPartner = null;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		try {
			iTracker = 1.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//LOG.info(" In getAccountSushiPartner Method Account ID : "+ accountID);
			rmd.log("In getAccountSushiPartner Method start");
			if (accountID.length()<1) {
				iTracker = 2.0;
				throw new Exception(accountID + " accountID is Not valid ");
			}
			sushiPartner = new SushiPartner(rmd);
			iTracker = 3.0;
			JsonObject result=sushiPartner.getAccountSushiPartner(webmartID, accountID);
			iTracker = 4.0;
			rb = Response.status(200).entity(result.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			rmd.error("ManageSushiPartner : getAccountSushiPartner : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
}