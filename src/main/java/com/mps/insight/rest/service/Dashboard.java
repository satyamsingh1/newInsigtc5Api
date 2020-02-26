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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.product.Account;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.product.SiteSummary;
import com.mps.redis.Redis;

@RestController
@RequestMapping("summary")
public class Dashboard {
	@Autowired
	private HttpServletRequest servletRequest;

	// private static final Logger LOG =
	// LoggerFactory.getLogger(Dashboard.class);

	RequestMetaData rmd;
	String userCode;
	int webmartID;

	@Produces(MediaType.APPLICATION_JSON)
	@PostMapping("fulltext")
	public String getFullTextValues() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		String rb = "";
		int liveYear = 0;
		int liveMonth = 0;
		PublisherSettings publisherSettings = null;
		SiteSummary siteSummary = null;
		Redis redis = new Redis();
		JsonArray jarray = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			// LOG.info(" In getFullTextValues Method Getting Tiles Fulltext
			// detail for Wbmart ID : " + webmartID);
			rmd.log(" In getFullTextValues Method Getting Tiles Fulltext detail for Wbmart ID : " + webmartID);
			publisherSettings = new PublisherSettings(rmd);
			iTracker = 3.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker = 4.0;
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
			iTracker = 5.0;
			String fulltext = redis.getValueFromRedisWithKey(webmartID + "_full_text");
			if (null == fulltext || fulltext.isEmpty() || fulltext.length() <= 0) {
				siteSummary = new SiteSummary(rmd);
				iTracker = 6.0;
				if (liveMonth > 0) {
					jarray = siteSummary.getFullTextDetail(publisherSettings.getPublisherCode(webmartID), liveMonth,
							liveYear);
					iTracker = 7.0;
					rb = jarray.toString();
					iTracker = 8.0;
					redis.setValueToRedisWithKey(webmartID + "_full_text", jarray.toString());

				}
			} else {
				iTracker = 8.0;
				rb = fulltext;
				// LOG.info("Exception in getFullTextValues : " + fulltext);
				rmd.log("Exception in getFullTextValues  : " + fulltext);
			}

		} catch (Exception e) {
			// LOG.error("getFullTextValues Final Json Array : " +
			// jarray.toString());
			rmd.exception("Dashboard : getFullTextValues : iTracker : " + iTracker + " : " + e.toString());
			rb = "Exception : " + e.getMessage();
		}
		return rb;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("pageview")
	public Response getPageViewValues() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		PublisherSettings publisherSettings = null;
		SiteSummary siteSummary = null;
		Redis redis = new Redis();
		int liveYear = 0;
		int liveMonth = 0;
		JsonArray jarray = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			// LOG.info(" In getPageViewValues Method Getting Tiles PageView
			// detail for Wbmart ID : " + webmartID);
			rmd.log(" In getPageViewValues Method Getting Tiles PageView detail for Wbmart ID : " + webmartID);
			publisherSettings = new PublisherSettings(rmd);
			iTracker = 3.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker = 4.0;
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
			iTracker = 5.0;
			String pageview = redis.getValueFromRedisWithKey(webmartID + "_page_view");
			if (null == pageview || pageview.isEmpty() || pageview.length() <= 0) {
				siteSummary = new SiteSummary(rmd);
				iTracker = 6.0;
				if (liveMonth > 0) {
					jarray = siteSummary.getPageViewDetail(publisherSettings.getPublisherCode(webmartID), liveMonth,
							liveYear);
					iTracker = 8.0;
					rb = Response.status(200).entity(jarray.toString());
					iTracker = 7.0;
					redis.setValueToRedisWithKey(webmartID + "_page_view", jarray.toString());

				}
			} else {
				iTracker = 8.0;
				rb = Response.status(200).entity(pageview);
				// LOG.info("Exception getFullTextValues : " + pageview);
			}
		} catch (Exception e) {
			// LOG.error(" getPageViewValues Final Json Array : " +
			// jarray.toString());
			rmd.exception("Dashboard : getPageViewValues : iTracker : " + iTracker + " : " + e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.toString());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("processingstatus")
	public Response getProcessingStatus() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		PublisherSettings publisherSettings = null;
		JsonArray jarray = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			rmd.log(" In getProcessingStatus Method Getting Tiles processingstatus detail for Wbmart ID : "
					+ webmartID);
			/*
			 * //LOG.
			 * info(" In getProcessingStatus Method Getting Tiles processingstatus detail for Wbmart ID : "
			 * + webmartID);
			 */

			publisherSettings = new PublisherSettings(rmd);
			iTracker = 3.0;
			jarray = publisherSettings.getLiveMonthAndYear(webmartID);
			iTracker = 4.0;
			rb = Response.status(200).entity(jarray.toString());

		} catch (Exception e) {
			// LOG.info("getProcessingStatus Final Json Array : " +
			// jarray.toString());
			rmd.exception("Dashboard : getProcessingStatus : iTracker : " + iTracker + " : " + e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("accountsummary")
	public Response getInstitutionAndGroup() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		PublisherSettings publisherSettings = null;
		Account account = null;
		Redis redis = new Redis();
		JsonObject jarray = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			/*
			 * LOG.info(
			 * " In getInstitutionAndGroup Method Getting Tiles Institution and Consortia Details for Wbmart ID : "
			 * + webmartID);
			 */
			rmd.log(" In getInstitutionAndGroup Method Getting Tiles Institution and Consortia Details");
			publisherSettings = new PublisherSettings(rmd);
			int setNo = 0;
			iTracker = 3.0;
			setNo = publisherSettings.getSetNo(webmartID);
			iTracker = 4.0;
			String accountsummary = redis.getValueFromRedisWithKey(webmartID + "_account_summary");
			iTracker = 5.0;
			if (null == accountsummary || accountsummary.isEmpty() || accountsummary.length() <= 0) {
				account = new Account(rmd);
				rmd.log("In getInstitutionAndGroup : Webmart ID = " + webmartID + " and SetNo = " + setNo);
				// LOG.info("In getInstitutionAndGroup : Webmart ID = " +
				// webmartID + " and SetNo = " + setNo);
				iTracker = 6.0;
				jarray = account.getInstitutionAndGroup(webmartID, setNo);
				iTracker = 7.0;
				redis.setValueToRedisWithKey(webmartID + "_account_summary", jarray.toString());
				iTracker = 8.0;
				rb = Response.status(200).entity(jarray.toString());

			} else {
				iTracker = 9.0;
				rb = Response.status(200).entity(accountsummary);
				// LOG.info("getFullTextValues Final Json Array : " +
				// accountsummary);
			}
		} catch (Exception e) {
			// LOG.error("getProcessingStatus Final Json Array : " +
			// jarray.toString());
			rmd.exception("Dashboard : getInstitutionAndGroup : iTracker : " + iTracker + " : " + e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("highestipdetail")
	public Response getHighestIPList(@FormParam("recordCount") int recordCount) throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		// LOG.info("Retrieving the Dashbaord highestipdetail Data for Webmart
		// ID : " + webmartID);
		rmd.log("Retrieving the Dashbaord highestipdetail Data ");
		ResponseBuilder rb = null;
		SiteSummary siteSummary = null;
		PublisherSettings publisherSettings;
		int liveYear = 0;
		int liveMonth = 0;
		Redis redis = new Redis();
		String highip = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			publisherSettings = new PublisherSettings(rmd);
			iTracker = 3.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker = 4.0;
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);

			siteSummary = new SiteSummary(rmd);
			iTracker = 5.0;

			highip = redis.getValueFromRedisWithKey(webmartID + "_high_ip");
			if (null == highip || highip.isEmpty() || highip.length() <= 0) {
				JsonObject jarray = siteSummary.getHighestAccessedIP(webmartID, recordCount, liveMonth, liveYear);
				highip = jarray.toString();
				redis.setValueToRedisWithKey(webmartID + "_high_ip", highip);
			}
			rb = Response.status(200).entity(highip);

		} catch (Exception e) {
			rmd.exception("Dashboard : getHighestIPList : iTracker : " + iTracker + " : " + e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("unidentifiedipdetail")
	public Response getUnidentifiedIPList(@FormParam("recordCount") int recordCount) throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		rmd.log("Retrieving the Dashbaord getUnidentifiedIPList Data");
		ResponseBuilder rb = null;
		SiteSummary siteSummary = null;
		PublisherSettings publisherSettings;
		int liveYear = 0;
		int liveMonth = 0;
		Redis redis = new Redis();
		String unidip = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			publisherSettings = new PublisherSettings(rmd);
			iTracker = 3.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker = 4.0;
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);

			iTracker = 5.0;
			if (recordCount >= 0) {
				recordCount = 10;
				unidip = redis.getValueFromRedisWithKey(webmartID + "_unidentified_ip");
				siteSummary = new SiteSummary(rmd);
				iTracker = 6.0;
				if (null == unidip || unidip.isEmpty() || unidip.length() <= 0) {
					JsonObject jarray = siteSummary.getUnidentifiedIP(webmartID, recordCount, liveMonth, liveYear);
					unidip = jarray.toString();
					iTracker = 7.0;
					redis.setValueToRedisWithKey(webmartID + "_unidentified_ip", unidip);
				}
			}
			rb = Response.status(200).entity(unidip);
		} catch (Exception e) {
			rmd.exception("Dashboard : getUnidentifiedIPList : iTracker : " + iTracker + " : " + e.toString());
			// LOG.error("Retrieved the Dashbaord getUnidentifiedIPList Data
			// final Json Array : " + unidip.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("licencedenial")
	public Response getDenielList(@FormParam("recordCount") int recordCount) throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		rmd.log("Retrieving the Dashbaord getDenielList Data ");
		// LOG.info("Retrieving the Dashbaord getDenielList Data for Webmart ID
		// : " + webmartID);
		ResponseBuilder rb = null;
		PublisherSettings publisherSettings;
		SiteSummary siteSummary = null;
		Redis redis = new Redis();
		int liveYear = 0;
		int liveMonth = 0;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			publisherSettings = new PublisherSettings(rmd);
			iTracker = 3.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker = 4.0;
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
			String unidip = null;
			siteSummary = new SiteSummary(rmd);
			iTracker = 5.0;
			if (recordCount >= 0) {
				recordCount = 10;
				unidip = redis.getValueFromRedisWithKey(webmartID + "_licence_deniel");
				iTracker = 6.0;
				if (null == unidip || unidip.isEmpty() || unidip.length() <= 0) {
					JsonObject jarray = siteSummary.getDenielList(webmartID, recordCount, liveMonth, liveYear);
					unidip = jarray.toString();
					iTracker = 7.0;
					redis.setValueToRedisWithKey(webmartID + "_licence_deniel", unidip);
				}

			} else {
				iTracker = 8.0;
				JsonObject jarray = siteSummary.getDenielList(webmartID, recordCount, liveMonth, liveYear);
				unidip = jarray.toString();

			}
			rb = Response.status(200).entity(unidip);

		} catch (Exception e) {
			rmd.exception("Dashboard : getDenielList : iTracker : " + iTracker + " : " + e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("pubviewlist")
	public Response getPubDashboardView() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		rmd.log("Retrieving the Dashbaord getUnidentifiedIPList Data ");
		// LOG.info("Retrieving the Dashbaord getUnidentifiedIPList Data for
		// Webmart ID : " + webmartID);
		ResponseBuilder rb = null;
		SiteSummary siteSummary = null;
		JsonObject jarray = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			siteSummary = new SiteSummary(rmd);
			iTracker = 3.0;
			jarray = siteSummary.getPubDashBoardView(webmartID);
			iTracker = 4.0;
			rb = Response.status(200).entity(jarray.toString());

		} catch (Exception e) {
			rmd.exception("Dashboard : getPubDashboardView : iTracker : " + iTracker + " : " + e.toString());
			// LOG.info("Retrieved the Dashbaord getUnidentifiedIPList Data
			// final Json Array : " + jarray.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}

}// END class
