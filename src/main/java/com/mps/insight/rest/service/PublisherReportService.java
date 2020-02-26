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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.product.Reports;
import com.mps.insight.security.Authorization;

@Path("publisherreport")
public class PublisherReportService {
	@Context
	private HttpServletRequest servletRequest; 
	RequestMetaData rmd;
	private static final Logger LOG = LoggerFactory.getLogger(PublisherReportService.class);
	String useCode;
	int webmartID;
	Authorization authorization;
/*
	public PublisherReportService(@HeaderParam("token") String token) throws Exception {

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
	@Path("reportlist")
	public Response getPublisherReportList(@FormParam("year") int year) throws Exception {
		ResponseBuilder rb = null;
		int liveYear = 0;
		int liveMonth = 0;
		PublisherSettings publisherSettings = null;
		Reports reports = null;
		JsonObject jarray = null;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		useCode = rmd.getUserCode();
		double iTracker = 0.0;
		try {
			 iTracker = 1.0;
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			webmartID = rmd.getWebmartID();
			useCode = rmd.getUserCode();
			//LOG.info(" In getPublisherReportList Method for Wbmart ID : " + webmartID);
			rmd.log(" In getPublisherReportList Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID <= 0) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			publisherSettings = new PublisherSettings(rmd);
			if (year < 2000) {
				iTracker = 3.0;
				liveYear = publisherSettings.getLiveYear(webmartID);
			} else {
				liveYear = year;
			}
			liveMonth = publisherSettings.getLibLiveMonthC4(webmartID, liveYear);//publisher_setting
			if (liveYear==year) {
				liveMonth=publisherSettings.getMinLibLiveMonthC4(webmartID, liveYear);
			}
			iTracker = 4.0;
			//LOG.info("Getting Live Month and Year For Webmart Id : " + webmartID + " month : " + liveMonth+ " Live Year : " + liveYear);
			rmd.log("Getting Live Month and Year For Webmart Id : " + webmartID + " month : " + liveMonth+ " Live Year : " + liveYear);
			reports = new Reports(rmd);
			iTracker = 5.0;
			jarray = reports.getPubReports(webmartID, liveMonth, liveYear);
			if(null==jarray || jarray.isEmpty() || jarray.get("data").toString().length()<5){
				iTracker = 6.0;
				liveMonth = publisherSettings.getLibLiveMonthC4(webmartID, liveYear);//publisher_setting
				jarray = reports.getPubReports(webmartID, liveMonth, liveYear);
			}
			rb = Response.status(200).entity(jarray.toString());
		} catch (Exception e) {
			//LOG.error("getFullTextValues Final Json Array : " + jarray.toString());
			rmd.error(" PublisherReportService: getPublisherReportList : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.toString());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("reportlisttemp")
	public Response getPublisherReportListTest(@FormParam("year") int year) throws Exception {
		ResponseBuilder rb = null;
		int liveYear = 0;
		int liveMonth = 0;
		PublisherSettings publisherSettings = null;
		Reports reports = null;
		JsonObject jarray =null;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		useCode = rmd.getUserCode();
		double iTracker = 0.0;
		try {
			 iTracker = 1.0;
			//LOG.info(" In getPublisherReportList Method for Wbmart ID : " + webmartID);
			rmd.log(" In getPublisherReportList Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID <= 0) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}

			publisherSettings = new PublisherSettings(rmd);
			if (year < 2000) {
				iTracker = 3.0;
				liveYear = publisherSettings.getLiveYear(webmartID);
			} else {
				liveYear = year;
			}
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker = 4.0;
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
			//LOG.info("Getting Live Month and Year For Webmart Id : " + webmartID + " month : " + liveMonth+ " Live Year : " + liveYear);
			rmd.log("Getting Live Month and Year For Webmart Id : " + webmartID + " month : " + liveMonth+ " Live Year : " + liveYear);
			reports = new Reports(rmd);
			iTracker = 5.0;
			jarray = reports.getPubReportsTemp(webmartID, liveMonth, liveYear);
			iTracker = 6.0;
			rb = Response.status(200).entity(jarray.toString());

			
		} catch (Exception e) {
			//LOG.error("getFullTextValues Final Json Array : " + jarray.toString());
			rmd.error(" PublisherReportService: getPublisherReportListTest : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.toString());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("pubyearlist")
	public Response getPublisherReportYearList() throws Exception {
		ResponseBuilder rb = null;
		Reports reports = null;
		JsonObject jarray = null;
		double iTracker = 0.0;
		try {
			 iTracker = 1.0;
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			webmartID = rmd.getWebmartID();
			useCode = rmd.getUserCode();
			rmd.log(" In getPublisherReportYearList Method for Wbmart ID : " + webmartID);
			//LOG.info(" In getPublisherReportYearList Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID <= 0) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			reports = new Reports(rmd);
			iTracker = 3.0;
			jarray = reports.getPubReportYears(webmartID);
			iTracker = 4.0;
			rb = Response.status(200).entity(jarray.toString());

			
		} catch (Exception e) {
			//LOG.error("getPublisherReportYearList Final Json Array : " + jarray.toString());
			rmd.error(" PublisherReportService: getPublisherReportYearList : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.toString());
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getjournals")
	public Response getJournals(@FormParam("reportID") int reportID,@FormParam("year") int year) throws Exception {
		ResponseBuilder rb = null;
		Reports reports = null;
		int liveYear = 0;
		int liveMonth = 0;
		int setNO=0;
		PublisherSettings publisherSettings = null;
		JsonArray jarray =null;
		double iTracker = 0.0;
		try {
			 iTracker = 1.0;
			rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
			webmartID = rmd.getWebmartID();
			useCode = rmd.getUserCode();
			publisherSettings = new PublisherSettings(rmd);
			if(year==0){
			 iTracker = 2.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
			//setNO=publisherSettings.getSetNo(webmartID);
			iTracker = 3.0;
			}else{
			liveYear = year;
			if(liveYear==2019)
			liveMonth = publisherSettings.getLibLiveMonthC4(webmartID, liveYear);
			else{
				liveMonth=12;
			}
			//setNO=publisherSettings.getSetNoByMonth_Year(webmartID,liveYear,liveMonth);
			iTracker = 4.0;
			}
			//LOG.info(" In getPublisherReportYearList Method for Wbmart ID : " + webmartID);
			rmd.log(" In getPublisherReportYearList Method for reportID  : " + reportID);
			// check for null and valid values
			if (webmartID <= 0) {
				iTracker = 5.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			reports = new Reports(rmd);
			iTracker = 6.0;
			jarray = reports.getJournalReportATOZ(webmartID, reportID, liveYear, liveMonth);
			iTracker = 7.0;
			rb = Response.status(200).entity(jarray.toString());
		} catch (Exception e) {
			//LOG.error("getPublisherReportYearList Final Json Array : " + jarray.toString());
			rmd.error(" PublisherReportService: getJournals : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.toString());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("searchjournals")
	public Response searchJournals(@FormParam("reportID") int reportID,
			@FormParam("journalID") String journalID,@FormParam("title") String title,
			@FormParam("year") int year) throws Exception {
		ResponseBuilder rb = null;
		Reports reports = null;
		int liveYear = 0;
		int liveMonth = 0;
		int setNO=0;
		PublisherSettings publisherSettings = null;
		JsonObject jarray =null;
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		useCode = rmd.getUserCode();
		try {
		     iTracker = 1.0;
			publisherSettings = new PublisherSettings(rmd);
			if(year==0){
				iTracker = 2.0;
				liveYear = publisherSettings.getLiveYear(webmartID);
				liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
				setNO=publisherSettings.getSetNo(webmartID);
				 iTracker = 3.0;
				}else{
				liveYear = year;
				 iTracker = 4.0;
				liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
				setNO=publisherSettings.getSetNoByMonth_Year(webmartID,liveYear,liveMonth);
				}
			//LOG.info(" In getPublisherReportYearList Method for Wbmart ID : " + webmartID);
			rmd.log(" In getPublisherReportYearList Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if(journalID.length()<=1 && title.length()<=1){
				 iTracker = 5.0;
				throw new Exception(" Enter some key word to search  ");
			}
			reports = new Reports(rmd);
			 iTracker = 6.0;
			jarray = reports.searchJournalReport(webmartID, reportID, liveYear, liveMonth, setNO,journalID,title);
			 iTracker = 7.0;
			rb = Response.status(200).entity(jarray.toString());

			
		} catch (Exception e) {
			//LOG.error("getPublisherReportYearList Final Json Array : " + jarray.toString());
			rmd.error(" PublisherReportService: searchJournals : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.toString());
		}
		return rb.build();
	}

	@POST
	@Path("location")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReportLocation(@FormParam("reportID") int reportID, @FormParam("frequency") String frequency,
			@FormParam("year") int year) {
		ResponseBuilder rb = null;
		Reports reports = null;
		PublisherSettings publisherSetting = null;
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		useCode = rmd.getUserCode();
		try {
			iTracker = 1.0;
			if (webmartID < 100) {
				iTracker = 2.0;
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}
			rmd.log(" In getPublisherReportYearList Method for : reportID" + reportID);
			publisherSetting = new PublisherSettings(rmd);
			if (year < 1) {
				iTracker = 3.0;
				year = publisherSetting.getLiveYear(webmartID);
			}
			reports = new Reports(rmd);
			iTracker = 4.0;
			JsonArray jarray=reports.getReportLocation(webmartID, reportID, frequency, year);
			iTracker = 5.0;
			rb = Response.status(200).entity(jarray.toString());

		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.toString());
			rmd.error(" PublisherReportService: getReportLocation : iTracker : "+iTracker+" : "+e.toString());
		}
		return rb.build();
	}
	
	@POST
	@Path("searchbyalphabet")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSearchJournalByAlphabet(@FormParam("reportID") int reportID,
			@FormParam("alphabet") String alphabet,@FormParam("year") int year) throws Exception {
		ResponseBuilder rb = null;
		Reports reports = null;
		int liveYear = 0;
		int liveMonth = 0;
		int setNO=0;
		PublisherSettings publisherSettings = null;
		JsonObject jarray = null;
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		useCode = rmd.getUserCode();
		try {
			iTracker = 1.0;
			publisherSettings = new PublisherSettings(rmd);
			liveYear = publisherSettings.getLiveYear(webmartID);
			if(year==0 || liveYear==year){
				iTracker = 2.0;
				liveMonth = publisherSettings.getLibLiveMonthC4(webmartID, liveYear);
				setNO=publisherSettings.getSetNo(webmartID);
				iTracker = 3.0;
				}else{
				liveYear = year;
				iTracker = 4.0;
				if(liveYear==2019){
				liveMonth = publisherSettings.getLibLiveMonthC4(webmartID, liveYear);
				}
				else
				{
					liveMonth=12;	
				}
				setNO=publisherSettings.getSetNoByMonth_Year(webmartID,liveYear,liveMonth);
				iTracker = 5.0;
				}
			//LOG.info(" In getPublisherReportYearList Method for Wbmart ID : " + webmartID);
			rmd.log(" In getPublisherReportYearList Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			reports = new Reports(rmd);
			iTracker = 6.0;
			jarray = reports.searchJournalReportByAlphabet(webmartID, reportID, liveYear, liveMonth, setNO, alphabet);
			iTracker = 7.0;
			rb = Response.status(200).entity(jarray.toString());

			
		} catch (Exception e) {
			//LOG.error("getPublisherReportYearList Final Json Array : " + jarray.toString());
			rmd.error(" PublisherReportService: getSearchJournalByAlphabet : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.toString());
		}
		return rb.build();
	}
}
