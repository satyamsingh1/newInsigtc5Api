package com.mps.insight.rest.service;

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

import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.product.PushLiveSetting;
import com.mps.insight.security.Authorization;

@Path("reportstatus")
public class PushLiveRollBackService {
	@Context
	private HttpServletRequest servletRequest; 
	
	RequestMetaData rmd;
	
	private static final Logger LOG = LoggerFactory.getLogger(PushLiveRollBackService.class);

	String userCode;
	int webmartID;
	Authorization authorization;
	UserDTO user;

	/*
	public PushLiveRollBackService(@HeaderParam("token") String token) throws Exception {

		try {
			if (token == null) {
				throw new Exception("Token : NULL");
			}
			if (token.trim().equalsIgnoreCase("")) {
				throw new Exception("Token : BLANK");
			}
			Users tempUser=new Users();
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
			user=tempUser.getUserDetailByUserID(userID,webmartID);

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
	@Path("pushliveaccount")
	public Response getPublisherReportList(@FormParam("accountCode") String accountCode) throws Exception {
		ResponseBuilder rb = null;
		PushLiveSetting pushLiveSettings = null;
		try {
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			
			webmartID = rmd.getWebmartID();
			userCode = rmd.getUserCode();
			LOG.info(" In getPublisherReportList Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID <= 0) {
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}

			pushLiveSettings = new PushLiveSetting(rmd);
			pushLiveSettings.setAccountReportsLive(webmartID, accountCode, user);
			rb = Response.status(200).entity("Success");
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("pushlive")
	public Response pushLiveAllAccount() throws Exception {
		ResponseBuilder rb = null;
		PushLiveSetting pushLiveSettings = null;
		try {
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			
			webmartID = rmd.getWebmartID();
			userCode = rmd.getUserCode();
			LOG.info(" In getPublisherReportList Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID <= 0) {
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}

			pushLiveSettings = new PushLiveSetting(rmd);
			pushLiveSettings.setAllReportsLive(webmartID, user);
			rb = Response.status(200).entity("Success");
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("rollbackaccount")
	public Response rollBackForAccount(@FormParam("accountCode") String accountCode) throws Exception {
		ResponseBuilder rb = null;
		PushLiveSetting pushLiveSettings = null;
		try {
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			
			webmartID = rmd.getWebmartID();
			userCode = rmd.getUserCode();
			LOG.info(" In rollBackForAccount Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID <= 0) {
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}

			pushLiveSettings = new PushLiveSetting(rmd);
			pushLiveSettings.setAccountReportsRollBack(webmartID, accountCode, user);
			rb = Response.status(200).entity("Success");
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("rollback")
	public Response rollBackAll() throws Exception {
		ResponseBuilder rb = null;
		PushLiveSetting pushLiveSettings = null;
		try {
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			
			webmartID = rmd.getWebmartID();
			userCode = rmd.getUserCode();
			LOG.info(" In rollBackAll Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID <= 0) {
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}

			pushLiveSettings = new PushLiveSetting(rmd);
			pushLiveSettings.setAllReportsRollBack(webmartID, user);
			rb = Response.status(200).entity("Success");
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}
	
}
