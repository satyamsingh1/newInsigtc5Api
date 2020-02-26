package com.mps.insight.rest.service;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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

import com.mps.insight.dto.EmailDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.PasswordGenerator;
import com.mps.insight.impl.MailSenderManager;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.product.Support;
import com.mps.insight.product.Users;
import com.mps.insight.security.Authorization;

@Path("supportservices")
public class SupportServices {
	@Context
	private HttpServletRequest servletRequest; 
	RequestMetaData rmd;
	private static final Logger log = LoggerFactory.getLogger(SupportServices.class);
	String userCode;
	int webmartID;
	Authorization authorization;
	UserDTO currentUser;
/*
	public SupportServices(@HeaderParam("token") String token) throws MyException {

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
			Users user=new Users();
			currentUser=user.getUserDetailByUserID(userID,webmartID);

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
	@Path("getsecquestion")
	public Response getSecurityQuestion(@FormParam("email") String email,
			@FormParam("publisherId") int publisherId
			) throws MyException {
		double iTracker = 0.0;
		iTracker = 1.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Users users = new Users();
		PublisherSettings ps=null;
		//log.info("SupportServices class getSecurityQuestion method : Start");
		rmd.log("SupportServices class getSecurityQuestion method : Start");
		try {
			webmartID = rmd.getWebmartID();
			userCode = rmd.getUserCode();
			ps=new PublisherSettings(rmd);
			JsonArray jarray = users.getUserByEmail(email.trim(),ps.getPublisherCode(webmartID));
			iTracker = 2.0;
			rb = Response.status(200).entity(jarray.toString());
		}
		catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE+e.getMessage()+"\"}");
			//log.error("In getSecurityQuestion method Exception : "+e.getMessage());
			rmd.error("SupportServices : getSecurityQuestion : iTracker : "+iTracker+" : "+e.toString());
		}
		//log.info("SupportServices class getSecurityQuestion method : End");
		rmd.log("SupportServices class getSecurityQuestion method : End");
		return rb.build();
	}

	@POST
	@Path("updatepassword")
	public Response updatePassword(@FormParam("user_code") String user_code) throws MyException {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		//userCode = rmd.getUserID();
		ResponseBuilder rb = null;
		Users users = new Users();
		EmailDTO emailDto = new EmailDTO();
		MailSenderManager mailSender = new MailSenderManager();
		
		//log.info("SupportServices class updatePassword method : Start");
        rmd.log("SupportServices class updatePassword method : Start:user_id: "+user_code);
		UserDTO userDto = users.getUserDetailByUserCode(user_code,webmartID);
		iTracker = 1.0;
		try {
			iTracker = 2.0;
			if(userDto != null) {
				iTracker = 3.0;
				PasswordGenerator passwordGenerator=new PasswordGenerator(8,false);
				String password=passwordGenerator.getNewPassword();
				iTracker = 4.0;
				emailDto.setFirstName(userDto.getFirstName());
				emailDto.setLastName(userDto.getLastName());
				emailDto.setToken("resetPassword");
				emailDto.setMessage("New Password is : "+ password);
				emailDto.setUseremail(currentUser.getEmailID().trim());
				emailDto.setReciever(userDto.getEmailID().trim());
				iTracker = 5.0;
				userDto.setUserCode(user_code);
				userDto.setPassword(password);
				emailDto.setWebmartId(webmartID);
				if(userDto.getUserCode() !=null)
				{
					iTracker = 6.0;
					userDto.setUpdatedBy(userDto.getUserCode());
				}
				else
				{
					iTracker = 7.0;
					userDto.setUpdatedBy("System_reset_user");
				}
				long rowsAffacted = users.updateUserPassword(userDto);
				if(rowsAffacted != 0) {
					iTracker = 8.0;
					mailSender.sendMail(emailDto);
					rb = Response.status(200).entity("{\"success\":\"updated\"}");
				}
			}
		}
		catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE+e.getMessage()+"\"}");
			//log.error("In updatePassword method Exception : "+e.getMessage());
			 rmd.error("SupportServices : updatePassword : iTracker : "+iTracker+" : "+e.toString());
		}
		//log.info("SupportServices class updatePassword method : End");
		rmd.log("SupportServices class updatePassword method : End");
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("systemlog/getsystemlogs")
	public Response getSystemLogs() throws Exception {
		ResponseBuilder rb = null;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		//log.info("SupportService class getSystemLogs method : Start");
		rmd.log("SupportService class getSystemLogs method : Start");
		Support support = new Support(rmd);
		try {
			iTracker = 1.0;
			JsonArray jarray = support.generateSystemLog(webmartID);
			iTracker = 2.0;
			rb = Response.status(200).entity(jarray.toString());
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE+e.getMessage()+"\"}");
			//log.error("In getSystemLogs method Exception : "+e.getMessage());
			rmd.error("SupportServices : getSystemLogs : iTracker : "+iTracker+" : "+e.toString());
		}
		//log.info("SupportService class getSystemLogs method : End");
          rmd.log("SupportService class getSystemLogs method : End");
		return rb.build();
		
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("systemlog/reportstatus")
	public Response reportStatus() throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		//log.info("SupportService class reportStatus method : Start");
		rmd.log("SupportService class reportStatus method : Start");
		ResponseBuilder rb = null;
		String webmartCode = null;
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		JsonObject qaMonth = null;
		JsonObject liveMonth = null;
		JsonObject rollbackMonth = null;
		Support support = new Support(rmd);
		try {
			iTracker = 1.0;
			PublisherSettings publisherSettings = new PublisherSettings(rmd);
			webmartCode = publisherSettings.getPublisherCode(webmartID);
			iTracker = 2.0;
			if(webmartCode != null) {
				iTracker = 3.0;
				qaMonth = support.liveMonthByWebmartId(webmartCode,"qa");
				liveMonth = support.liveMonthByWebmartId(webmartCode,"Live");
				rollbackMonth = support.liveMonthByWebmartId(webmartCode,"rollback");
			}
			jsonBuilder.add("qa", qaMonth);
			jsonBuilder.add("Live", liveMonth);
			jsonBuilder.add("rollback", rollbackMonth);
			rb = Response.status(200).entity(jsonBuilder.build().toString());
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE+e.getMessage()+"\"}");
			//log.error("In reportStatus method Exception : "+e.getMessage());
			rmd.log("SupportServices : reportStatus : iTracker : "+iTracker+" : "+e.toString());
		}
		//log.info("SupportService class reportStatus method : End");
		rmd.log("SupportService class reportStatus method : End");
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("systemlog/processingsummary")
	public Response processingSummary() throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		ResponseBuilder rb = null;
		JsonObject monthlyAuditDto = null;
		//log.info("SupportService class processingSummary method : Start");
		rmd.log("SupportService class processingSummary method : Start");
		Support support = new Support(rmd);
		try {
			 iTracker = 1.0;
			monthlyAuditDto = support.getProcessingData(webmartID);
			 iTracker = 2.0;
			rb = Response.status(200).entity(monthlyAuditDto.toString());
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE+e.getMessage()+"\"}");
			//log.error("In reportStatus method Exception : "+e.getMessage());
			rmd.error("SupportServices : processingSummary : iTracker : "+iTracker+" : "+e.toString());
		}
		//log.info("SupportService class processingSummary method : End");
		rmd.log("SupportService class processingSummary method : End");
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("contactus/typesofqueries")
	public Response contactTypesOfQueries() throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		ResponseBuilder rb = null;
		JsonArray jarray = null;

		//log.info("SupportService class contactTypesOfQueries method : Start");
		rmd.log("SupportService class contactTypesOfQueries method : Start");
		Support support = new Support(rmd);
		try {
			iTracker = 1.0;
			jarray = support.getEmailCategoriesByCategory(InsightConstant.PUBLISHER_CONTACT ,webmartID);
			iTracker = 2.0;
			rb = Response.status(200).entity(jarray.toString());
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE+e.getMessage()+"\"}");
			//log.error("In contactTypesOfQueries method Exception : "+e.getMessage());
			rmd.error("SupportServices : contactTypesOfQueries : iTracker : "+iTracker+" : "+e.toString());
		}
		//log.info("SupportService class contactTypesOfQueries method : End");
		rmd.log("SupportService class contactTypesOfQueries method : End");

		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("contactus/sendquerymail")
	public Response sendQueryMail(
			@FormParam("subject") String subject,
			@FormParam("query_type") String queryType,
			@FormParam("message_body") String messageBody,
			@FormParam("send_copy") boolean sendCopy) throws Exception {
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		double iTracker = 0.0;
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		ResponseBuilder rb = null;

		//log.info("SupportService class sendQueryMail method : Start");
           rmd.log("SupportService class sendQueryMail method : Start");
		EmailDTO emailDto = new EmailDTO();
		MailSenderManager mailSender = new MailSenderManager();

		emailDto.setFirstName(currentUser.getFirstName());
		emailDto.setLastName(currentUser.getLastName());
		emailDto.setSubject(subject);
		emailDto.setWebmartId(webmartID);
		emailDto.setMessage(queryType+"  "+messageBody);
		emailDto.setUseremail(currentUser.getEmailID().trim());
		emailDto.setSendEmailCopy(sendCopy);

		try {
			iTracker = 1.0;
			if(sendCopy == true) {
				//Add Receiver
				iTracker = 2.0;
				emailDto.setReciever(currentUser.getEmailID().trim());
			}
			//Send Mail
			mailSender.sendMail(emailDto);
			iTracker = 3.0;
			rb = Response.status(200).entity("{\"success\":\"mail sent\"}");
		} catch (Exception e) {
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE+e.getMessage()+"\"}");
			//log.error("In sendQueryMail method Exception : "+e.getMessage());
			rmd.error("SupportServices : sendQueryMail : iTracker : "+iTracker+" : "+e.toString());
		}
		//log.info("SupportService class sendQueryMail method : End");
         rmd.log("SupportService class sendQueryMail method : End");
		return rb.build();
	}
}
