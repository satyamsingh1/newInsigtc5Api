package com.mps.insight.rest.service;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dashboard.DashBoardProductSummery;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.dashboard.LibDashboardGraphImpl;
import com.mps.insight.product.Account;
import com.mps.insight.product.PublisherSettings;

@Path("libLogin")
public class LibDashBoard {

	@Context
	private HttpServletRequest servletRequest; 
	
	RequestMetaData rmd;
	private static final Logger log = LoggerFactory.getLogger(LibDashBoard.class);
	
	String userCode;
	int webmartID;
	/*
	public LibDashBoard(@HeaderParam("token") String token)throws MyException {
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

			if (userID <= 0) {
				throw new MyException("userID : ZERO");
			}
			if (webmartID <= 0) {
				throw new MyException("webmartID : ZERO");
			}

		} catch (MyException e) {
			throw e;
		}

	}*/
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("childAccount")
	public Response getAccountChilds() throws Exception {
		double iTracker=0.0;
		PublisherSettings publisherSettings = null;
		DashBoardProductSummery dashBoardProductSummery= null;
		ResponseBuilder rb = null;
		
		try {
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			dashBoardProductSummery= new DashBoardProductSummery(rmd);//Get object of Class
			publisherSettings = new PublisherSettings(rmd);//Get object of Class
			iTracker=3.0;
			int setNo=publisherSettings.getSetNo(webmartID);//Getting publisher Set No.
			rmd.log("Called : "+webmartID +" and set no is :"+ setNo);
			//log.info("Called : "+webmartID +" and set no is :"+ setNo);
			iTracker=4.0;
			JsonObject jObj = dashBoardProductSummery.getChildAccount(userCode, setNo,webmartID);// Getting all child accounts as JSON 
			//log.info("JSON object created for child account for : "+webmartID +" and user id is :"+ userID);
			iTracker=5.0;
			if(jObj!=null){ // check for Null
				rb = Response.status(200).entity(jObj.toString());// return response as JSON 
			}else{
				iTracker=6.0;
				rb = Response.status(200).entity("\"No Record\":\"Found\"");
			}
			
		} catch (Exception e) {
			rmd.exception("LibDashBoard : getAccountChilds : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
			//log.error("Exception in getAccountChilds : "+e.getMessage());
		}
		
		return rb.build();
	}	
	
/*	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("trendGraph")*/
	public Response getUsrId(@FormParam("institution") String inst) throws Exception {
		double iTracker=0.0;
		DashBoardProductSummery dashBoardProductSummery= null;
		LibDashboardGraphImpl lgraphimpl=null;
		ResponseBuilder rb = null;
		String code=null;
		Integer institutionId=null;
		String institutionCode="";
		PublisherSettings publisherSettings = null;
		
		try {
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			dashBoardProductSummery= new DashBoardProductSummery(rmd);// Get class Object
			lgraphimpl=new LibDashboardGraphImpl(rmd);
			iTracker=3.0;
			if(inst!=null){ // Check for parameter is not null
				try {
					institutionId=Integer.parseInt(inst);//pars institution id (if not parsed then search code by institution name)
					institutionCode = institutionId.toString();
				} catch (Exception e) {
					code=dashBoardProductSummery.getAccountCode(webmartID, inst); // Get institution id from requested parameter
					//institutionId=Integer.parseInt(code);
					institutionCode = code;
					//log.info("Requested Institution  : "+inst +" and institution id is :"+ code);
					rmd.error("Requested Institution  : "+inst +" and institution id is :"+ code + e.toString());
				}
				
			}else{ // if parameter is null then default institution will selected 
				
				publisherSettings = new PublisherSettings(rmd); // get class object
				iTracker=4.0;
				int setNo=publisherSettings.getSetNo(webmartID); // get set no
				iTracker=5.0;
				code = dashBoardProductSummery.getDefaultChildAccount(userCode, setNo,webmartID); // get default institution id from requested publisher 
				rmd.log(" Default Institution getting from DB  for webmart id : "+webmartID+" and institution name is "+inst +" and institution id is :"+ code);
				//log.info(" Default Institution getting from DB  for webmart id : "+webmartID+" and institution name is "+inst +" and institution id is :"+ code);
			}
			rmd.log("Generating graph for institution id : "+institutionId);
			//log.info("Generating graph for institution id : "+institutionId);
			JsonObject jobj = lgraphimpl.getDashboardGraphData(webmartID, institutionCode);
			iTracker=6.0;
			if(jobj!=null){
				rb = Response.status(200).entity(jobj.toString());
			}else{
				iTracker=7.0;
				rb = Response.status(200).entity("\"No Data\":\"Found\"");
			}
			
			
			//change by ksv 
			
			
		} catch (Exception e) {
			rmd.exception("LibDashBoard : getUsrId : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
			//log.error("Exception in getUsrId : "+e.getMessage());
		}
		
		return rb.build();
	}
	
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("trendGraph")
	public Response getLibrayTrend(@FormParam("institution") String inst) throws Exception {
 		double iTracker=0.0;
		LibDashboardGraphImpl lgraphimpl=null;
		ResponseBuilder rb = null;
		JsonObject jobj;
		try {
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			lgraphimpl=new LibDashboardGraphImpl(rmd);
			iTracker=3.0;
				jobj= lgraphimpl.getC5DashboardGraphData(webmartID, inst);	
			iTracker=6.0;
			if(jobj!=null){
				rb = Response.status(200).entity(jobj.toString());
			}else{
				iTracker=7.0;
				rb = Response.status(200).entity("\"No Data\":\"Found\"");
			}
		} catch (Exception e) {
			rmd.exception("libTrendGraph : getLibrayTrend : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("accountlivedetail")
	public Response getAccountLiveMonthAndYear(@FormParam("accountID") String accountID) throws Exception {
		double iTracker=0.0;
		PublisherSettings publisherSettings = null;
		Account acc=null;
		ResponseBuilder rb = null;
		
		try {
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			publisherSettings = new PublisherSettings(rmd);//Get object of Class
			acc=new Account(rmd);
			iTracker=3.0;
			int year=publisherSettings.getLiveYear(webmartID);//Getting publisher Max Year .
			iTracker=4.0;
			String month=acc.getAccountMaxMonthLive(webmartID, accountID, year);
			rmd.log("Called : "+webmartID +" and Month is :"+ month);
		//	log.info("Called : "+webmartID +" and Month is :"+ month);
			JsonObjectBuilder job=Json.createObjectBuilder();
			job.add("liveYear", year);
			job.add("liveMonth", month);
			//rmd.log("JSON object created for child account for : "+webmartID +" and user id is :"+ userID);
			//log.info("JSON object created for child account for : "+webmartID +" and user id is :"+ userID);
			iTracker=5.0;
			if(job!=null){ // check for Null
				rb = Response.status(200).entity(job.build().toString());// return response as JSON 
			}
			//rb = Response.status(200).entity("\"No Data\":\"Found\"");
		} catch (Exception e) {
			rmd.exception("LibDashBoard : getAccountLiveMonthAndYear : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
			//log.error("Exception in getAccountLiveMonthAndYear : "+e.getMessage());
		}
		
		return rb.build();
	}

}
