package com.mps.insight.rest.service;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
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

import com.mps.insight.c4.report.DynamicReportFormatImpl;
import com.mps.insight.dto.EmailDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.product.DynamicReports;
import com.mps.insight.product.Users;

@Path("dynamic")
public class DynamicReportService {

	@Context
	private HttpServletRequest servletRequest;
	private static final Logger LOG = LoggerFactory.getLogger(DynamicReportService.class);

	String userCode;
	int webmartID;
	RequestMetaData rmd;
	UserDTO user;

	/*public DynamicReportService(@HeaderParam("token") String token) throws Exception {

		try {
			if (token == null) {
				throw new MyException("Token : NULL");
			}
			if (token.trim().equalsIgnoreCase("")) {
				throw new MyException("Token : BLANK");
			}
			Users tempUser=new Users();
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
			user=tempUser.getUserDetailByUserID(userID,webmartID);
			

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
	
	
	/**
	 * Service for get counter 4 applicable report list in JSON format for Set in Couter 4 dynamic tab  Ex:
	 * {abbrev: "BR2", description: "Number of Successful Section Requests by Month and Title", status: "1"}
	 * {abbrev: "BR3", description: "Access Denied to Content Items by Month, Title and Category",…}
	 * {abbrev: "JR1", description: "Number of Successful Full-Text Article Requests by Month and Journal",…}
	 * {abbrev: "JR1GOA",…}
	 * {abbrev: "JR2", description: "Access Denied to Full-text Articles by Month, Journal and Category",…}
	 * @path /insightc5api/services/dynamic/dynamicreportlist
	 * @return Response as JSON
	 * @throws Exception
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dynamicreportlist")
	public Response getDynamicReportList() throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		iTracker=1.0;
		webmartID = rmd.getWebmartID();
		iTracker=2.0;
		userCode = rmd.getUserCode();
		
		ResponseBuilder rb = null;
		DynamicReports dynamicReports = null;
		try {
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			rmd.log("In getDynamicReportList Method");
		//	LOG.info(" In getDynamicReportList Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			dynamicReports = new DynamicReports(rmd);
			iTracker=3.0;
			JsonObject jobj=dynamicReports.getDynamicReportsList(webmartID);
			iTracker=4.0;
			rb = Response.status(200).entity(jobj.toString());
		} catch (Exception e) {
			rmd.exception("DynamicReportService : getDynamicReportList : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}
	
	/**
	 * Service for preview Selected report of selected dates in Interface
	 * @path /insightc5api/services/dynamic/dynamicreportjson
	 * @param accountCode
	 * @param from
	 * @param to
	 * @param report
	 * @return
	 * @throws Exception
	 */
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dynamicreportjson")
	public Response getDynamicReportJson(@FormParam("accountCode") String accountCode,
			@FormParam("from") String from,
			@FormParam("to") String to,
			@FormParam("report") String report) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		iTracker=1.0;
		webmartID = rmd.getWebmartID();
		iTracker=2.0;
		userCode = rmd.getUserCode();
		ResponseBuilder rb = null;
		DynamicReportFormatImpl dynamicReports = null;
		String searchTerm="";
		String searchTerm1="";
		try {
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			LOG.info(" In getDynamicReportJson Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			iTracker=3.0;
			if(accountCode.contains("[{")){
				searchTerm=accountCode.substring(accountCode.indexOf("[{")+2,accountCode.indexOf("}]"));
				if(accountCode.contains(" - ")){
				searchTerm1=accountCode.substring(0,accountCode.indexOf(" - "));
				}else{
					searchTerm1=accountCode.substring(0,accountCode.indexOf("[{"));	
				}
				}else{
					searchTerm=accountCode.trim();
				}

			dynamicReports = new DynamicReportFormatImpl(rmd);
			JsonArray jObjArray=null;
			iTracker=4.0;
			jObjArray=dynamicReports.getDynamicReportsJson(webmartID,searchTerm,report,from,to);
			iTracker=5.0;
			if(null==jObjArray){
				jObjArray = dynamicReports.getDynamicReportsJson(webmartID,searchTerm1,report,from,to);
			}
			iTracker=6.0;
			if(null!=jObjArray && jObjArray.toString().length()<14){
				if(searchTerm1.length()>2){
			jObjArray = dynamicReports.getDynamicReportsJson(webmartID,searchTerm1,report,from,to);
				}
			}
			LOG.info("Dynamic Data : Success");
			iTracker=7.0;
			if(null==jObjArray){
				rb = Response.status(200).entity("{\"error\":\"Report Not Available\"}");
			}else{
				iTracker=8.0;
			rb = Response.status(200).entity(jObjArray.toString());
			}
		} catch (Exception e) {
			rmd.exception("DynamicReportService : getDynamicReportJson : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("maildynamicreport")
	public Response sendMailDynamicReport(@FormParam("accountCode") String accountCode,
			@FormParam("fromdate") String from,
			@FormParam("todate") String to,
			@FormParam("fromuser") String fromuser,
			@FormParam("touser") String touser,
			@FormParam("subject") String subject,
			@FormParam("report") String report,
			@FormParam("attachment") String attachment) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		iTracker=1.0;
		webmartID = rmd.getWebmartID();
		iTracker=2.0;
		userCode = rmd.getUserCode();
		ResponseBuilder rb = null;
		DynamicReportFormatImpl dynamicReports = null;
		EmailDTO edto=new EmailDTO();
		try {
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			rmd.log(" In sendMailDynamicReport Method for Wbmart ID : " + webmartID);
			//LOG.info(" In sendMailDynamicReport Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			dynamicReports = new DynamicReportFormatImpl(rmd);
			iTracker=3.0;
			if(null==touser || touser.length()<2){
				touser=user.getEmailID();	
			}
			edto.setReciever(touser);
			edto.setSubject(subject+" . ");
			edto.setFirstName("User ");
			edto.setUseremail(fromuser);
			iTracker=4.0;
			String jobj=dynamicReports.reportsSendToMail(webmartID,accountCode,report,from,to,attachment,edto);
		//	LOG.info("Dynamic Data : "+jobj);
			rb = Response.status(200).entity(jobj);
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}

}
