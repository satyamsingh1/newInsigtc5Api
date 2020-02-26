package com.mps.insight.rest.service;

import javax.json.JsonArray;
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

import com.mps.insight.c5.report.Counter5ReportImpl;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.EmailDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.UserInfo;
import com.mps.insight.impl.SearchAccountImpl;
import com.mps.redis.Redis;

/**
 * Service Class for Counter Reports
 * @author kuldeep.singh
 *
 */
@Path("counter5")
public class Counter5ReportService {
	@Context
	private HttpServletRequest servletRequest;

	// private static final Logger LOG =
	// LoggerFactory.getLogger(Counter5ReportService.class);

	RequestMetaData rmd;

	String userCode;
	int webmartID;
	UserDTO user;
	/*
	 * public Counter5ReportService(@HeaderParam("token") String token) throws
	 * Exception {
	 * 
	 * try { if (token == null) { throw new Exception("Token : NULL"); } if
	 * (token.trim().equalsIgnoreCase("")) { throw new Exception("Token : BLANK"); }
	 * Users tempUser=new Users(); authorization = new Authorization(); String
	 * decode = authorization.decryptData(token); String[] temp =
	 * decode.split("~#~"); if (temp == null) { throw new
	 * Exception("decode split : NULL"); } if (temp.length != 3) { throw new
	 * Exception("decode split : Invalid Length"); }
	 * 
	 * userID = Integer.parseInt(temp[1].trim()); webmartID =
	 * Integer.parseInt(temp[2].trim());
	 * user=tempUser.getUserDetailByUserID(userID,webmartID);
	 * 
	 * if (userID <= 0) { throw new Exception("userID : ZERO"); } if (webmartID <=
	 * 0) { throw new Exception("webmartID : ZERO"); }
	 * 
	 * } catch (Exception e) { throw e; }
	 * 
	 * }
	 */

	
	/**
	 * Collect data of Counter5 Library Reports as JSON
	 * EX:{"institution_id":"10002",
	 * "key":"TR_ART_TYPE",
	 * "value":"Article Requests by Type",
	 * "desc":"Article Requests by Type",
	 * "type":"ADDITIONAL",
	 * "frequency":"MONTHLY_DYNAMIC",
	 * "fromdate":"2019-01-01",
	 * "todate":"2019-08-31"}
	 * 
	 * @Path /insightc5api/services/counter5/reportlist
	 * @param institutionID (EX: 10002)
	 * @param reportSection (EX: processing)
	 * @return Response as JSON 
	 * @throws Exception
	 * <br>
	 *  
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("reportlist")
	public Response getDynamicReportList(@FormParam("Institution_ID") String institutionID, @FormParam("reportSection") String reportSection) throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Counter5ReportImpl c5reportimpl = null;
		JsonArray jobj = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			userCode = rmd.getUserCode();
			iTracker = 2.0;
			rmd.log("In rollBackAll Method for Institution ID : " + institutionID);
			// LOG.info(" In rollBackAll Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}

			c5reportimpl = new Counter5ReportImpl(rmd);
			iTracker = 3.0;
			if (institutionID == null || institutionID.equalsIgnoreCase("")) {
				jobj = c5reportimpl.getCounter5ReportsList(webmartID);
			} else {
				iTracker = 4.0;
				jobj = c5reportimpl.getCounter5ReportsList(webmartID, institutionID, reportSection);
			}
			iTracker = 5.0;
			rb = Response.status(200).entity(jobj.toString());

			/*
			 *  Save user Activity in database
			 */
			rmd.setUserActivity("Preview Processing Reports (QA) list for Institution Id : "+institutionID);
			rmd.setResponceStatus(InsightConstant.SUCCESS);
			new UserInfo().saveInfo(rmd);
		} catch (Exception e) {
			rmd.exception(
					"Counter5ReportService : getDynamicReportList : iTracker : " + iTracker + " : " + e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			/*
			 *  Save user Activity in database
			 */
			rmd.setUserActivity("Preview Processing Reports (QA) list for Institution Id : "+institutionID);
			rmd.setResponceStatus(InsightConstant.FAIL);
			new UserInfo().saveInfo(rmd);
		}
		return rb.build();
	}

	/**
	 * Service for Collect Report attributes details from Redis Server or Database as JSON.
	 * Report Attributes are basic collections of report filter applicable in master reports
	 * EX:
	 * accessMethod: [{Key: "Regular", value: "Regular"}, {Key: "TDM", value: "TDM"}]
	 * accessType: [{Key: "Controlled", value: "Controlled"}, {Key: "OA_Gold", value: "OA_Gold"},…]
	 * dataType: [{Key: "Book", value: "Book"}, {Key: "Journal", value: "Journal"},…]
	 * metricType: [{Key: "Limit_Exceeded", value: "Limit_Exceeded"}, {Key: "No_License", value: "No_License"},…]
	 * yop: [{Key: "2019", value: "2019"}, {Key: "2018", value: "2018"}, {Key: "2017", value: "2017"},…]
	 * @Path /insightc5api/services/counter5/reportattributedetail
	 * @FormParm reportID (EX: TR)
	 * @param reportID
	 * @return Response as JSON
	 * @throws Exception
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("reportattributedetail")
	public Response getCounter5ReportAttributeDetail(@FormParam("reportID") String reportID) throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Counter5ReportImpl dynamicReports = null;
		Redis redis = new Redis();
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			rmd.log("In getCounter5ReportAttributeDetail Method for reportID : " + reportID);
			// LOG.info(" In getCounter5ReportAttributeDetail Method for Wbmart ID : " +
			// webmartID);
			iTracker = 3.0;
			// change by KSV on 2018-12-15 for global Master report filters
			// String metricType = redis.getValueFromRedisWithKey(webmartID + "_" +
			// reportID.toUpperCase() + "_metricType");
			String metricType = null;
			if (webmartID == 601) {
				metricType = redis.getValueFromRedisWithKey(webmartID + "_" + reportID.toUpperCase() + "_Filters");
			} else {
				metricType = redis.getValueFromRedisWithKey(reportID.toUpperCase() + "_Filters");
			}
			iTracker = 4.0;
			if (metricType == null || metricType.equalsIgnoreCase("error") || metricType.equalsIgnoreCase("")
					|| metricType.equalsIgnoreCase(" ")) {
				// check for null and valid values
				if (webmartID == 0) {
					throw new MyException(InsightConstant.ERROR_SESSION);
				}
				dynamicReports = new Counter5ReportImpl(rmd);
				iTracker = 5.0;
				JsonObject jobj = dynamicReports.getCounter5ReportsAttributeDetail(webmartID, reportID);
				iTracker = 5.0;
				// change by KSV on 2018-12-15 for global Master report filters
				// redis.setValueToRedisWithKey(webmartID + "_" + reportID.toUpperCase() +
				// "_metricType", jobj.toString());
				if (webmartID == 601) {
					redis.setValueToRedisWithKey(webmartID + "_" + reportID.toUpperCase() + "_Filters",
							jobj.toString());
				} else {
					redis.setValueToRedisWithKey(reportID.toUpperCase() + "_Filters", jobj.toString());
				}

				iTracker = 6.0;
				rb = Response.status(200).entity(jobj.toString());
			} else {
				iTracker = 7.0;
				rb = Response.status(200).entity(metricType);
			}

		} catch (Exception e) {
			rmd.exception("Counter5ReportService : getCounter5ReportAttributeDetail : iTracker : " + iTracker + " : "
					+ e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}

	
	/**
	 * Service for Preview selected report in Interface as Grid upto 500 rows 
	 * @path /insightc5api/services/counter5/counter5reportjson
	 * @param accountCode Ex:10002
	 * @param fromdate Ex:07-2019
	 * @param todate Ex: 07-2019
	 * @param report Ex: TR
	 * @param metrictype (if filter selected then List of Filters with comma separated 
	 * Ex: Limit_Exceeded,Total_Item_Requests, otherwise null)
	 * @param datatype (if filter selected then List of Filters with comma separated 
	 * Ex: Book,Journal, otherwise null)
	 * @param accesstype (if filter selected then List of Filters, otherwise null)
	 * @param accessmethod (if filter selected then List of Filters with comma separated 
	 * Ex:Controlled,OA_Gold, otherwise null)
	 * @param yop (if filter selected then Selected year Ex: 2018, otherwise null)
	 * @param showattribute (Not applicable for now ....)
	 * @return Counter 5 report as JSON 
	 * @throws Exception
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("counter5reportjson")
	public Response getDynamicReportJson(@FormParam("accountCode") String accountCode,
			@FormParam("fromdate") String fromdate, @FormParam("todate") String todate,
			@FormParam("report") String report, @FormParam("metricType") String metrictype,
			@FormParam("dataType") String datatype, @FormParam("accessType") String accesstype,
			@FormParam("accessMethod") String accessmethod, @FormParam("yop") String yop,
			@FormParam("showattribute") String showattribute) throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Counter5ReportImpl dynamicReports = null;
		SearchAccountImpl searchimpl = new SearchAccountImpl();
		Counter5DTO c5dto = new Counter5DTO();
		try {
			String previewType = "preview";
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			iTracker = 3.0;
			c5dto.setReportCode(report);
			try {
				if (webmartID == 0) {
					throw new MyException(InsightConstant.ERROR_SESSION);
				}
				accountCode = searchimpl.getAccountCodeFromSearch(accountCode, webmartID);

			} catch (Exception e) {
				rmd.error(
						"Exception While fetching Account Code / metrictype / datatype / accesstype / accessmethod / yop from search result / query parameter ");
				// LOG.info("Exception While fetching Account Code / metrictype / datatype /
				// accesstype / accessmethod / yop from search result / query parameter ");
			}
			iTracker = 4.0;
			c5dto.setAccountID(accountCode);
			iTracker = 5.0;
			c5dto.setFromDate(fromdate);
			iTracker = 6.0;
			c5dto.setToDate(todate);
			iTracker = 7.0;
			c5dto.setMetricType(metrictype);
			iTracker = 8.0;
			c5dto.setDataType(datatype);
			iTracker = 9.0;
			c5dto.setAccessType(accesstype);
			iTracker = 10.0;
			c5dto.setAccessMethod(accessmethod);
			iTracker = 11.0;
			c5dto.setYop(yop);
			iTracker = 12.0;
			c5dto.setReportAttributes(showattribute);
			iTracker = 13.0;
			c5dto.setWebmartID(webmartID);
			iTracker = 14.0;
			c5dto.setInstitutionID(accountCode.trim());
			// LOG.info(" In Counter 5 Report Detail : " + c5dto.toString());
			// rmd.log("In Counter 5 Report Detail : " + c5dto.toString());
			dynamicReports = new Counter5ReportImpl(rmd);
			iTracker = 15.0;
			JsonObject jobj = dynamicReports.getCounter5ReportsJson(c5dto, previewType);
			iTracker = 16.0;
			// LOG.info("Dynamic Data : "+jobj.toString());
			rb = Response.status(200).entity(jobj.toString());
			rmd.setUserActivity("Preview Counter Report : "+report);
			rmd.setResponceStatus(InsightConstant.SUCCESS);
			new UserInfo().saveInfo(rmd);
			
		} catch (Exception e) {
			rmd.setUserActivity("Preview Counter Report : "+report);
			rmd.setResponceStatus(InsightConstant.FAIL);
			new UserInfo().saveInfo(rmd);
			
			rmd.exception(
					"Counter5ReportService : getDynamicReportJson : iTracker : " + iTracker + " : " + e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}

	/**
	 * Service for Sending previewed report by mail in CSV, TSV or XLS format, 
	 * An email will be made throw Mail Pop-up 
	 * @path /insightc5api/services/counter5/mailreport
	 * @param accountCode Ex: 10002
	 * @param fromdate Ex: 01-2019
	 * @param todate Ex: 01-2019
	 * @param report Ex: TR
	 * @param fromuser Filled automaticall with Login User Ex:publisher@mpsinsight.com 
	 * @param touser Mail to be send Ex: kuldeep@mpslimited.com
	 * @param subject will be filled automatically with report name, can be changed as per user need
	 * @param attachment report format Ex: csv
	 * @param message will be filled automatically with report name, can be changed as per user need
	 * @return Message with Success or Fail
	 * @throws Exception
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("mailreport")
	public Response sendMailDynamicReport(@FormParam("accountCode") String accountCode,
			@FormParam("from") String fromdate, @FormParam("to") String todate, @FormParam("report") String report,
			@FormParam("fromuser") String fromuser, @FormParam("touser") String touser,
			@FormParam("subject") String subject, @FormParam("attachment") String attachment,
			@FormParam("message") String message) throws Exception {

		double iTracker = 0.0;
		Counter5ReportImpl dynamicReports = null;
		EmailDTO edto = new EmailDTO();
		ResponseBuilder rb = null;
		String previewType = "download";

		try {
			iTracker = 1.0;
			rmd = (RequestMetaData) servletRequest.getAttribute("RMD");

			iTracker = 1.1;
			webmartID = rmd.getWebmartID();

			iTracker = 2.0;
			userCode = rmd.getUserCode();

			iTracker = 2.1;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			iTracker = 2.2;
			rmd.log("sendMailDynamicReport Method for Account Code : " + accountCode);

			iTracker = 2.3;
			dynamicReports = new Counter5ReportImpl(rmd);
			iTracker = 3.0;
			if (null == touser || touser.length() < 2) {
				touser = user.getEmailID();
			}

			iTracker = 4.0;
			edto.setReciever(touser); // setting to user
			edto.setUseremail(fromuser); // setting from user
			edto.setFirstName("User "); // user first Name
			edto.setSubject(subject); // setting subject
			edto.setMessage(message); // setting message

			iTracker = 5.0;
			String jobj = dynamicReports.reportsSendToMail(webmartID, accountCode, report, fromdate, todate, attachment,
					edto, previewType);

			iTracker = 6.0;
			rb = Response.status(200).entity(jobj);

		} catch (Exception e) {
			rmd.exception("Counter5ReportService : sendMailDynamicReport : iTracker : " + iTracker + " : "
					+ e.toString() + " : " + edto.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}

}
