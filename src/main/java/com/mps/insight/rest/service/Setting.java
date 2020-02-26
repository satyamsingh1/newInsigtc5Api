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
import com.mps.insight.dto.CommonDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.product.EmailAlertSetting;
import com.mps.insight.product.FavoriteSetting;
import com.mps.insight.product.PushLiveSetting;
import com.mps.insight.product.Users;
import com.mps.insight.security.Authorization;

@Path("setting")
public class Setting {
	@Context
	private HttpServletRequest servletRequest; 
	RequestMetaData rmd;
	private static final Logger LOG = LoggerFactory.getLogger(Setting.class);
	String userCode;
	int webmartID;
	Authorization authorization;
	UserDTO currentUser;
/*
	public Setting(@HeaderParam("token") String token) throws Exception {

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
			Users user=new Users();
			currentUser=user.getUserDetailByUserID(userID,webmartID);
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
	@Path("searchaccounts")
	public Response searchAccounts(@FormParam("search") String search) throws Exception {
		ResponseBuilder rb = null;
		FavoriteSetting favoriteSetting = null;
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		try {
			iTracker = 1.0;
			//LOG.info(" In searchAccounts Method Getting detail for Wbmart ID : "+ webmartID);
			rmd.log(" In searchAccounts Method Getting detail for Wbmart ID : "+ webmartID);
			if (webmartID < 100) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			favoriteSetting = new FavoriteSetting(rmd);
			JsonObject jobject = favoriteSetting.getAccountsForFavorite(search, webmartID);
			iTracker = 3.0;
			rb = Response.status(200).entity(jobject.toString());
			//LOG.info("searchAccounts  Final Json Array : " + jobject.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\""+e.getMessage()+"\"}");
			rmd.error(" Setting: searchAccounts : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("emailalertscreen")
	public Response emailAlertScreen() throws Exception {
		ResponseBuilder rb = null;
		EmailAlertSetting emailAlertSetting = null;
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		try {
			iTracker = 1.0;
			//LOG.info(" In emailAlertScreen Method Getting detail for Wbmart ID : "+ webmartID);
			rmd.log(" In emailAlertScreen Method Getting detail for Wbmart ID : "+ webmartID);
			if (webmartID < 100) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			emailAlertSetting = new EmailAlertSetting(rmd);
			JsonObject jobject = emailAlertSetting.getEmailAlerts(webmartID);
			iTracker = 3.0;
			rb = Response.status(200).entity(jobject.toString());
			//LOG.info("emailAlertScreen  Final Json Array : " + jobject.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\""+e.getMessage()+"\"}");
			rmd.error(" Setting: emailAlertScreen : iTracker : "+iTracker+" : "+e.toString());
		}	return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("pushlivesetting")
	public Response pushliveSetting() throws Exception {
		ResponseBuilder rb = null;
		PushLiveSetting pushLiveSetting = null;
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		try {
			iTracker = 1.0;
			//LOG.info(" In pushliveSetting Method Getting detail for Wbmart ID : "+ webmartID);
			rmd.log(" In pushliveSetting Method Getting detail for Wbmart ID : "+ webmartID);
			if (webmartID < 100) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			pushLiveSetting = new PushLiveSetting(rmd);
			
			iTracker = 3.0;
			JsonObject jobject = pushLiveSetting.getPushLiveSetting(webmartID);
			iTracker =4.0;
			rb = Response.status(200).entity(jobject.toString());
			//LOG.info("pushliveSetting  Final Json Array : " + jobject.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\""+e.getMessage()+"\"}");
			rmd.error(" Setting: pushliveSetting : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("updatepushlivesetting")
	public Response updatePushliveSetting(@FormParam("datakey") String datakey,
			@FormParam("datavalue") String datavalue) throws Exception {
		ResponseBuilder rb = null;
		PushLiveSetting pushLiveSetting = null;
		CommonDTO dto=new CommonDTO();
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		try {
			//LOG.info(" In pushliveSetting Method Getting detail for Wbmart ID : "+ webmartID);
			rmd.log(" In pushliveSetting Method Getting detail for Wbmart ID : "+ webmartID);
			if (webmartID < 100) {
				iTracker = 1.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			dto.setDataKey(datakey);
			dto.setDataValue(datavalue);
			dto.setUpdatedBy(rmd.getEmailID());
			dto.setWebmartID(webmartID);
			iTracker = 2.0;
			pushLiveSetting = new PushLiveSetting(rmd);
			iTracker = 3.0;
			String result = pushLiveSetting.updatePushLiveSetting(dto);
			iTracker = 4.0;
			rb = Response.status(200).entity("{\""+datakey+"\":\""+result+"\"}");
			//LOG.info("updatePushliveSetting   : " + result);
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\""+e.getMessage()+"\"}");
			rmd.error(" Setting: updatePushliveSetting : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("libraryconfigsetting")
	public Response getLibraryConfig() throws Exception {
		ResponseBuilder rb = null;
		EmailAlertSetting emailAlertSetting = null;
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		try {
			 iTracker = 1.0;
			//LOG.info(" In getLibraryConfig Method Getting detail for Wbmart ID : "+ webmartID);
			 rmd.log(" In getLibraryConfig Method Getting detail for Wbmart ID : "+ webmartID);
			if (webmartID < 100) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			emailAlertSetting = new EmailAlertSetting(rmd);
			iTracker = 3.0;
			JsonObject jobject = emailAlertSetting.getLibraryConfigSetting(webmartID);
			iTracker = 4.0;
			rb = Response.status(200).entity(jobject.toString());
			//LOG.info("getLibraryConfig  Final Json Array : " + jobject.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\""+e.getMessage()+"\"}");
			rmd.error(" Setting: getLibraryConfig : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("accountfavoriteaddlist")
	public Response accountfavoriteaddlist(@FormParam("accountIds") String accountIds,
			@FormParam("favoriteLevel") String favoriteLevel) throws Exception {
		ResponseBuilder rb = null;
		FavoriteSetting favoriteSetting = null;
		UserDTO userDto = null;
		String[] arrAccountIds = accountIds.split(",");
		String result="";
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		Users users = new Users();
		userDto = users.getUserDetailByUserCode(userCode,webmartID);
	    iTracker = 2.0;
		userDto.setUserCode(userCode);
		userDto.setWebmartID(webmartID);
		try {
			 iTracker = 3.0;
			favoriteSetting = new FavoriteSetting(rmd);
			// Save favorite Account
			try {
				 iTracker = 4.0;
				result=favoriteSetting.addFavoritesAccounts(favoriteLevel, arrAccountIds, userDto);
				 iTracker = 5.0;
				rb = Response.status(200).entity("{\"account favorites\":\""+result+"\"}");
			} catch (Exception e) {
				//LOG.info("Exception in accountfavoriteaddlist :: " + e.getMessage());
				rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\"Not added to Favorites\"}");
				rmd.error(" Setting: accountfavoriteaddlist : iTracker : "+iTracker+" : "+e.toString());
			}
				//rb = Response.status(200).entity(Response.Status.OK);
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\""+e.getMessage()+"\"}");
			rmd.error(" Setting: accountfavoriteaddlist : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("removefavorite")
	public Response accountfavoriteremove(@FormParam("accountIds") String accountIds) throws Exception {
		ResponseBuilder rb = null;
		FavoriteSetting favoriteSetting = null;
		String[] arrAccountIds = accountIds.split(",");
		String result="";
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		try {
			iTracker = 1.0;
			favoriteSetting = new FavoriteSetting(rmd);
			// Save favorite Account
			try {
				iTracker = 2.0;
				for (String string : arrAccountIds) {
					result=favoriteSetting.removeFavorite(string,webmartID);	
				}
				rb = Response.status(200).entity("{\"account favorites\":\""+result+"\"}");
			} catch (Exception e) {
				//LOG.info("Exception in accountfavoriteremove :: " + e.getMessage());
				rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\"Not removed from Favorites\"}");
				rmd.error(" Setting: accountfavoriteremove : iTracker : "+iTracker+" : "+e.toString());
			}
				//rb = Response.status(200).entity(Response.Status.OK);
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\""+e.getMessage()+"\"}");
			rmd.error(" Setting: accountfavoriteremove : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("addemailalert")
	public Response addEmailAlert(@FormParam("email") String email,
			@FormParam("firstname") String firstname,
			@FormParam("lastname") String lastname,
			@FormParam("alertname") String alertname) throws Exception {
		ResponseBuilder rb = null;
		EmailAlertSetting emailAlertSetting = null;
		UserDTO udto=new UserDTO();
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		try {
			iTracker = 1.0;
			//LOG.info(" In getLibraryConfig Method Getting detail for Wbmart ID : "+ webmartID);
			rmd.log(" In getLibraryConfig Method Getting detail for Wbmart ID : "+ webmartID);
			if (webmartID < 100) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			udto.setFirstName(firstname);
			udto.setLastName(lastname);
			udto.setEmailID(email);
			udto.setAlertName(alertname);
			udto.setWebmartID(webmartID);
			udto.setUpdatedBy(rmd.getEmailID());
			emailAlertSetting = new EmailAlertSetting(rmd);
			iTracker = 3.0;
			String result = emailAlertSetting.addEmailAlert(udto);
			iTracker = 4.0;
			rb = Response.status(200).entity("{\"emailalert\":\"success\",\"alertID\":\""+result+"\"}");
			//LOG.info("addEmailAlert   : " + result);
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\""+e.getMessage()+"\"}");
			rmd.error(" Setting: addEmailAlert : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("removeemailalert")
	public Response removeEmailAlert(@FormParam("alertID") String alertID) throws Exception
	   {
		ResponseBuilder rb = null;
		EmailAlertSetting emailAlertSetting = null;
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		try {
			iTracker = 1.0;
			//LOG.info(" In getLibraryConfig Method Getting detail for Wbmart ID : "+ webmartID);
			rmd.log(" In getLibraryConfig Method Getting detail for Wbmart ID : "+ webmartID);
			if (webmartID < 100) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			emailAlertSetting = new EmailAlertSetting(rmd);
			String result="";
			String[] list=alertID.split(",");
			for (String string : list) {
				result = emailAlertSetting.removeEmailAlert(string,webmartID);
			}
			rb = Response.status(200).entity("{\"emailalert\":\""+result+"\"}");
			//LOG.info("addEmailAlert   : " + result);
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\""+e.getMessage()+"\"}");
			rmd.error(" Setting: removeEmailAlert : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("updatelibrarysetting")
	public Response updateLibraryConfig(@FormParam("detail") String detail) throws Exception {
		ResponseBuilder rb = null;
		EmailAlertSetting emailAlertSetting = null;
		String [] arr=detail.split(",");
		CommonDTO dto=null;
		String result="";
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		try {
			iTracker = 1.0;
			//LOG.info(" In updatelibrarysetting Method Getting detail for Wbmart ID : "+ webmartID +" And Setting Detail : "+detail);
			rmd.log(" In updatelibrarysetting Method Getting detail for Wbmart ID : "+ webmartID +" And Setting Detail : "+detail);
			//LOG.info(" In getLibraryConfig Method Getting detail for Wbmart ID : "+ webmartID);
			if (webmartID < 100) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			for (String data : arr) {
				String [] datadetail=data.split(":");
				dto=new CommonDTO();
				dto.setWebmartID(webmartID);
				dto.setDataKey(datadetail[0]);
				dto.setDataValue(datadetail[1]);
				dto.setUpdatedBy(rmd.getEmailID());
				emailAlertSetting = new EmailAlertSetting(rmd);
				result=emailAlertSetting.updateLibraryConfigSetting(dto);
				iTracker = 3.0;
				//LOG.info("updateLibraryConfig  Success : " +result);
			}
			rb = Response.status(200).entity("{\"Library Configration report \":\"updated\"}");
			//LOG.info("updateLibraryConfig  Success");
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"error\":\""+e.getMessage()+"\"}");
			rmd.error(" Setting: updateLibraryConfig : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
}
