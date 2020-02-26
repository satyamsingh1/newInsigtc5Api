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

import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.impl.LibraryUserManager;
import com.mps.insight.impl.PublisherUser;

@Path("managelibrary")
public class LibraryUserService {

	@Context
	private HttpServletRequest servletRequest; 
	private static final Logger log = LoggerFactory.getLogger(LibraryUserService.class);
	RequestMetaData rmd;
    String userCode;
	int webmartID;
	UserDTO currentUser;
/*
	public LibraryUserService(@HeaderParam("token") String token) throws MyException {
			log.info("LibraryUserService constructor called ...");
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
			log.error("Exception in Authorization : Constructor token :"+token);
		}
	}
	*/
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getaccountalllibuser")
	public Response getAccountAllLibUser(@FormParam("child_Account_code") String child_Account_code) throws Exception {
		ResponseBuilder rb = null;
		LibraryUserManager libraryUserManager = null;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		try {
			iTracker = 1.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//log.info(" In getAccountAllLibUser Method Wbmart ID : "+ webmartID);
			rmd.log(" In getAccountAllLibUser Method ");
			libraryUserManager = new LibraryUserManager(rmd);
			iTracker = 2.0;
			JsonObject jobject = libraryUserManager.searchLibUser(webmartID, child_Account_code);
			rb = Response.status(200).entity(jobject.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
			//log.error("Exception in getAccountAllLibUser : "+e.getMessage());
			rmd.error("LibraryUserService : getAccountAllLibUser : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("searchaccountlibuser")
	public Response searchAccountLibUser(@FormParam("child_Account_code") String child_Account_code,
			@FormParam("user") String user,
			@FormParam("linked") String linked) throws Exception {
		ResponseBuilder rb = null;
		LibraryUserManager libraryUserManager = null;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		try {
			iTracker = 1.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//log.info(" In searchAccountLibUser Method account : "+ accountCode);
			rmd.log(" In searchAccountLibUser Method ");
			libraryUserManager = new LibraryUserManager(rmd);
			iTracker = 2.0;
			JsonObject jobject = libraryUserManager.searchAccountLibUser(webmartID, child_Account_code,user);
			iTracker = 3.0;
			rb = Response.status(200).entity(jobject.toString());
		} catch (Exception e) {
			//log.error("Exception in searchAccountLibUser "+e.getMessage());
			rmd.error("LibraryUserService : searchAccountLibUser : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("editLibUserScreen")
	public Response editLibUser(@FormParam("userCode") String userCode) throws Exception {
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		try {
			iTracker = 1.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//log.info(" In editLibUser Method User ID : "+ userID);
			rmd.log(" In editLibUser Method User Code : "+ userCode);
			publisherUser = new PublisherUser(rmd);
			iTracker = 2.0;
			JsonObject result=publisherUser.getPubUserToEdit(userCode);
			iTracker = 3.0;
			rb = Response.status(200).entity(result.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			rmd.error("LibraryUserService : editLibUser : iTracker : "+iTracker+" : "+e.toString());
			//log.error("Exception in editLibUser "+e.getMessage());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getlibuserlist")
	public Response getLiblisherList() throws Exception {
		ResponseBuilder rb = null;
		PublisherUser publisherUser = null;
		String userType = "LibraryClients";
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		try {
			iTracker = 1.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//log.info(" In getLiblisherList Method for Wbmart ID : "+ webmartID);
			rmd.log(" In getLiblisherList Method ");
			publisherUser = new PublisherUser(rmd);
			iTracker = 2.0;
			JsonObject jobject = publisherUser.getUserList(webmartID,userCode,userType);
			iTracker = 3.0;
			rb = Response.status(200).entity(jobject.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
			//log.error("Exception In getLiblisherList Exception : "+e.toString());
			rmd.error("LibraryUserService : getLiblisherList : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}

}
