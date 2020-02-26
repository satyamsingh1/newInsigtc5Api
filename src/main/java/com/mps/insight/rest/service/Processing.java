package com.mps.insight.rest.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dto.EmailDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.dto.UserFavoriteDTO;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.impl.MailSenderManager;
import com.mps.insight.product.Account;
import com.mps.insight.product.Feed;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.product.PushLiveSetting;
import com.mps.insight.product.Users;
import com.mps.insight.security.Authorization;

@Path("processing")
public class Processing {
	@Context
	private HttpServletRequest servletRequest;
	RequestMetaData rmd;
	private static final Logger LOG = LoggerFactory.getLogger(Processing.class);
	String userCode;
	int webmartID;
	Authorization authorization;
	UserDTO user;

	/*
	 * public Processing(@HeaderParam("token") String token) throws MyException
	 * {
	 * 
	 * try { if (token == null) { throw new MyException("Token : NULL"); } if
	 * (token.trim().equalsIgnoreCase("")) { throw new
	 * MyException("Token : BLANK"); }
	 * 
	 * authorization = new Authorization(); String decode =
	 * authorization.decryptData(token); String[] temp = decode.split("~#~"); if
	 * (temp == null) { throw new MyException("decode split : NULL"); } if
	 * (temp.length != 3) { throw new
	 * MyException("decode split : Invalid Length"); }
	 * 
	 * userID = Integer.parseInt(temp[1].trim()); webmartID =
	 * Integer.parseInt(temp[2].trim());
	 * 
	 * if (userID <= 0) { throw new MyException("userID : ZERO"); } if
	 * (webmartID <= 0) { throw new MyException("webmartID : ZERO"); }
	 * 
	 * } catch (MyException e) { throw e; }
	 * 
	 * }
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("feedarchive")
	public Response feeds(@Context UriInfo info) throws Exception {
		ResponseBuilder rb = null;
		String jsonData = "";
		JsonObject jarray = null;
		Feed feed = null;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		try {
			iTracker = 1.0;
			int monthArr[] = new int[3];
			int yearArr[] = new int[3];
			feed = new Feed(rmd);
			String yearMonth = "";
			int year = 0;
			int month = 0;
			// Get year month
			try {
				iTracker = 2.0;
				if (webmartID == 0) {
					iTracker = 3.0;
					throw new MyException(InsightConstant.ERROR_SESSION);
				}
				yearMonth = feed.getMonthYear(webmartID);
				if (yearMonth != null) {
					iTracker = 4.0;
					year = Integer.parseInt(yearMonth.substring(0, 4));
					month = Integer.parseInt(yearMonth.substring(4));
				}
			} catch (Exception e) {
				// LOG.info("Exception in live year or month" + e.getMessage());
				rmd.error("Exception in live year or month: iTracker: " + iTracker + ":" + e.getMessage());
				rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Error in live year or month ");
			}

			if (webmartID == 301) {
				if (month > 3) {
					iTracker = 5.0;
					monthArr[0] = month - 3;
					monthArr[1] = month - 2;
					monthArr[2] = month - 1;
					yearArr[0] = year;
					yearArr[1] = year;
					yearArr[2] = year;

				} else {
					if (month == 3) {
						iTracker = 6.0;
						monthArr[0] = 12;
						yearArr[0] = year - 1;
						monthArr[1] = 1;
						yearArr[1] = year;
						monthArr[2] = 2;
						yearArr[2] = year;
					}

					else if (month == 2) {
						iTracker = 7.0;
						monthArr[0] = 11;
						yearArr[0] = year - 1;
						monthArr[1] = 12;
						yearArr[1] = year - 1;
						monthArr[2] = 1;
						yearArr[2] = year;
					} else if (month == 1) {
						iTracker = 8.0;
						monthArr[0] = 10;
						yearArr[0] = year - 1;
						monthArr[1] = 11;
						yearArr[1] = year - 1;
						monthArr[2] = 12;
						yearArr[2] = year - 1;
					}

				}
			} else {
				if (month > 3) {
					iTracker = 9.0;
					monthArr[0] = month - 2;
					monthArr[1] = month - 1;
					monthArr[2] = month;
					yearArr[0] = year;
					yearArr[1] = year;
					yearArr[2] = year;
				} else {
					if (month == 3) {
						iTracker = 10.0;
						monthArr[0] = 1;
						yearArr[0] = year;
						monthArr[1] = 2;
						yearArr[1] = year;
						monthArr[2] = 3;
						yearArr[2] = year;
					}

					else if (month == 2) {
						iTracker = 10.1;
						monthArr[0] = 12;
						yearArr[0] = year - 1;
						monthArr[1] = 1;
						yearArr[1] = year;
						monthArr[2] = 2;
						yearArr[2] = year;
					} else if (month == 1) {
						iTracker = 10.2;
						monthArr[0] = 11;
						yearArr[0] = year - 1;
						monthArr[1] = 12;
						yearArr[1] = year - 1;
						monthArr[2] = 1;
						yearArr[2] = year;
					}
				}
			}
			String mon = Arrays.toString(monthArr);
			String allSetNo = "";
			// Get set no
			try {
				iTracker = 10.3;
				allSetNo = feed.getAllSetNo(webmartID, mon.substring(1, mon.toString().length() - 1), year);
			} catch (Exception e) {
				// LOG.info("Exception in retrieving set no for given period" +
				// e);
				rmd.error(
						"Exception in retrieving set no for given period: iTracker: " + iTracker + " " + e.toString());
				rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Error in set No ");
			}
			// get all the feeds for the publisher
			try {
				iTracker = 10.4;
				jarray = feed.getFeeds(webmartID, allSetNo, String.valueOf(year),
						mon.substring(1, mon.toString().length() - 1));
			} catch (Exception e) {
				// LOG.info("Exception in retrieving feeds for live year " +
				// year + " or month " + month + " :: " + e);
				rmd.error("Exception in retrieving feeds for live year " + year + " or month " + month + " : iTracker: "
						+ iTracker + " " + e.toString());
				rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("No Feed Archive records found ");
			}
			jsonData = jarray.toString();
			if (jsonData.isEmpty()) {
				iTracker = 10.4;
				rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("No Feed Archive records found");
			} else
				rb = Response.status(200).entity(jsonData);
		} catch (Exception e) {
			rmd.error(" Processing: feeds : iTracker : " + iTracker + " : " + e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		} finally {
			// disconnecting connection
			// if(insightDao != null){insightDao.disconnect();}
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("qa/accountfavoriteadd")
	public Response addToFavorites(@FormParam("accountName") String accountName) throws Exception {
		// LOG.info("Class Processing method addToFavorites : Start");
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");

		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		ResponseBuilder rb = null;
		Feed feed = null;
		UserFavoriteDTO userFavorite = new UserFavoriteDTO();
		Account account = new Account(rmd);
		String accountCode = account.getAccountCodeByName(accountName, webmartID);
		iTracker = 1.0;
		UserDTO userDto = null;
		if (userCode == null || userCode.equalsIgnoreCase("null") || userCode.equalsIgnoreCase("")
				|| userCode.equalsIgnoreCase(" ")) {
			iTracker = 2.0;
			Users users = new Users();
			userDto = users.getUserDetailByUserCode(userCode, webmartID);
			userFavorite.setUpdatedBy(userDto.getUserCode());
			iTracker = 3.0;
		}
		userFavorite.setUserCode(userCode);
		userFavorite.setWebmartId(webmartID);
		userFavorite.setAccountCode(accountCode);
		try {
			iTracker = 4.0;
			rmd.log("Class Processing method addToFavorites : Start");
			if (webmartID == 0) {
				iTracker = 5.0;
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			feed = new Feed(rmd);

			// Save favorite Account
			feed.addToFavorites(userFavorite);
			rb = Response.status(200).entity(Response.Status.OK);
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			// LOG.error("Exception in addToFavorites method Exception :
			// "+e.getMessage());
			rmd.error(" Processing: addToFavorites : iTracker : " + iTracker + " : " + e.toString());
		}
		// LOG.info("Class Processing method addToFavorites : End");
		rmd.log("Class Processing method addToFavorites : End");
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("qa/accountfavoriteremove")
	public Response accountfavoriteremove(@FormParam("accountName") String accountName) throws Exception {
		// LOG.info("Class Processing method accountfavoriteremove : Start");
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		ResponseBuilder rb = null;
		Feed feed = null;
		UserFavoriteDTO userFavorite = new UserFavoriteDTO();
		Account account = new Account(rmd);
		String accountCode = account.getAccountCodeByName(accountName, webmartID);
		userFavorite.setUserCode(userCode);
		userFavorite.setWebmartId(webmartID);
		userFavorite.setAccountCode(accountCode);
		try {
			iTracker = 1.0;
			rmd.log("Class Processing method accountfavoriteremove : Start");
			feed = new Feed(rmd);
			// remove favorite Account
			try {
				iTracker = 2.0;
				feed.removeFavorites(userFavorite);
			} catch (Exception e) {
				// LOG.error("Exception in accountfavoriteremove method :: " +
				// e.getMessage());
				rmd.error(
						"Exception in accountfavoriteremove method : iTracker : " + iTracker + " : " + e.getMessage());
				rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Not added to Favorites");
			}
			rb = Response.status(200).entity(Response.Status.OK);
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Not added to Favorites");
			rmd.error(" Processing: accountfavoriteremove : iTracker : " + iTracker + " : " + e.toString());
		}
		// LOG.info("Class Processing method accountfavoriteremove : End");
		rmd.log("Class Processing method accountfavoriteremove : End");
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("qa/reportproblem")
	public Response reportproblem(@FormParam("accountId") Long accountId, @FormParam("messageBody") String messageBody)
			throws Exception {
		// LOG.info("Class Processing method reportproblem : Start");
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		double iTracker = 0.0;
		MailSenderManager mailSender = new MailSenderManager();
		ResponseBuilder rb = null;
		EmailDTO emailDto = new EmailDTO();
		emailDto.setWebmartId(webmartID);
		emailDto.setMessage(messageBody.trim());
		UserDTO userDto = null;
		String reciever = "";
		if (userCode == null || userCode.equalsIgnoreCase("null") || userCode.equalsIgnoreCase("")
				|| userCode.equalsIgnoreCase(" ")) {
			iTracker = 1.0;
			Users users = new Users();
			userDto = users.getUserDetailByUserCode(userCode, webmartID);
			emailDto.setUseremail(userDto.getEmailID());
			emailDto.setReciever(reciever);
			emailDto.setSubject("MPS Insight Mail");
			iTracker = 2.0;
		}
		try {
			iTracker = 3.0;
			rmd.log("Class Processing method reportproblem : Start");
			mailSender.sendMail(emailDto);
			iTracker = 4.0;
			rb = Response.status(200).entity(Response.Status.OK);
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Mail send failure");
			// LOG.error("Exception in reportproblem method :: " +
			// e.getMessage());
			rmd.error(" Processing: reportproblem : iTracker : " + iTracker + " : " + e.toString());
		} finally {

		}
		// LOG.info("Class Processing method reportproblem : End");
		rmd.log("Class Processing method reportproblem : End");
		return rb.build();

	}

	// change by Satyam
	// push Manual Roll Back list
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("qa/manualrollbacklist")
	public Response manualrollbackList() throws Exception {
		// LOG.info("Class Processing method reportproblem : Start");
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		String webmartCode = rmd.getWebmartCode();
		userCode = rmd.getUserCode();
		Account account = new Account(rmd);
		double iTracker = 0.0;

		JsonObject jobj = null;
		ResponseBuilder rb = null;
		PublisherSettings pubSettings = null;

		try {
			iTracker = 2.0;
			pubSettings = new PublisherSettings(rmd);
			// String year=pubSettings.getPublisherLiveYear(webmartCode);
			// String Month=pubSettings.getPublisherLiveMonth(webmartCode);
			String year = rmd.getLiveYear();
			String Month = rmd.getLiveMonth();

			jobj = account.getManualrolebackList(webmartCode, year, Month);

			rb = Response.status(200).entity(jobj.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Mail send failure");
			// LOG.error("Exception in reportproblem method :: " +
			// e.getMessage());
			rmd.error(" Processing: reportproblem : iTracker : " + iTracker + " : " + e.toString());
		} finally {

		}
		// LOG.info("Class Processing method reportproblem : End");
		rmd.log("Class Processing method reportproblem : End");
		return rb.build();
	}

	// change by satyam 06/02/2019
	// push Manual Roll Back click by request
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("qa/manualrollback")
	public Response qaRollBackManual(@FormParam("institution_id") String institution_id) throws Exception {
		ResponseBuilder rb = null;
		PushLiveSetting pushLiveSettings = null;
		try {
			rmd = (RequestMetaData) servletRequest.getAttribute("RMD");

			webmartID = rmd.getWebmartID();
			userCode = rmd.getUserCode();
			rmd.log(" In Qa Roll Back Manual Roll Back Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID <= 0) {
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}

			pushLiveSettings = new PushLiveSetting(rmd);
			pushLiveSettings.setQaManualReportsRollBack(webmartID, institution_id);
			rb = Response.status(200).entity("Success");
		} catch (Exception e) {
			rmd.exception(" : " + e.getMessage());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}

	// change by satyam 06/02/2019 for push Roll Back All QA
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("qa/rollback")
	public Response QaRollBackAll() throws Exception {
		ResponseBuilder rb = null;
		PushLiveSetting pushLiveSettings = null;
		try {
			rmd = (RequestMetaData) servletRequest.getAttribute("RMD");

			webmartID = rmd.getWebmartID();
			userCode = rmd.getUserCode();
			rmd.log(" In QARollBackAll Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID <= 0) {
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}

			pushLiveSettings = new PushLiveSetting(rmd);
			String result = pushLiveSettings.setAllQaReportsRollBack(webmartID, user);
			rb = Response.status(200).entity(result.toString());
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}

	// change by satyam 06/02/2019 for push live all
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("qa/pushlive")
	public Response qaPushLiveAllAccount() throws Exception {
		ResponseBuilder rb = null;
		PushLiveSetting pushLiveSettings = null;
		try {
			rmd = (RequestMetaData) servletRequest.getAttribute("RMD");

			webmartID = rmd.getWebmartID();
			userCode = rmd.getUserCode();
			LOG.info(" In getPublisherReportList Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID <= 0) {
				throw new Exception(webmartID + " WebmartId is Not valid ");
			}

			pushLiveSettings = new PushLiveSetting(rmd);
			pushLiveSettings.setAllQaReportsLive(webmartID, user);
			rb = Response.status(200).entity("Success");
		} catch (Exception e) {
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}

	// saurabh
	// change by Satyam singh 11/15/2019
	@GET
	@Produces("application/csv")
	@Path("c5feedarchive")
	public Response getfeederData(@QueryParam("reportfeedtype") String dataTypeFeed,
			@QueryParam("BeginDate") String beginDate, @QueryParam("EndDate") String endDate) throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		rmd.log("Retrieving the Counter-5 feed  Data ");
		ResponseBuilder rb = null;
		String contentType = "Content-Disposition";
		String attachment = "attachment; filename=\"";
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			Feed feed = new Feed(rmd);

			iTracker = 3.0;
			// jarray =
			// pushLiveSettings.getfeedData(dataTypeFeed,beginDate,endDate);
			InputStream results = feed.getC5FeedData(webmartID, dataTypeFeed, beginDate, endDate);
			iTracker = 4.0;
			rb = Response.status(200).entity(results.toString());

			String simpleDate = new SimpleDateFormat("yyyyMMdd_hhmm").format(new Date());
			rb = Response.ok(results);
			rb.header(contentType, attachment + dataTypeFeed + "_" + simpleDate + ".csv\"");
		} catch (Exception e) {
			rmd.exception("FileDownloadService : getDynamicReportJsonOverloaded : iTracker : " + iTracker + " : "
					+ e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("c5feedarchivelist")
	public Response getfeedList() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		rmd.log("Retrieving the Counter-5 feed_type list ");
		ResponseBuilder rb = null;

		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			Feed feed = new Feed(rmd);
			JsonArray jab = feed.getC5FeedList();
			iTracker = 3.0;

			rb = Response.status(200).entity(jab.toString());

		} catch (Exception e) {
			rmd.exception(" c5 feed list : getFeedList for C5 : iTracker : " + iTracker + " : " + e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("c5feedate")
	public Response getfeedDate() throws Exception {
		PublisherSettings publisherSettings = null;
		ResponseBuilder rb = null;
		double iTracker = 0.0;
		int liveYear = 0;
		int liveMonth = 0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		rmd.log("Getting Feed Date");
		
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			publisherSettings = new PublisherSettings(rmd);
			iTracker=3.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker=4.0;
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
			if(liveMonth==0 || liveYear==0){
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			rmd.log("---"+liveMonth+"---"+liveYear);
			Feed feed = new Feed(rmd);
			JsonArray jab = feed.getFeedDateLiveDate(liveMonth,liveYear);
			iTracker = 5.0;

			rb = Response.status(200).entity(jab.toString());

		} catch (Exception e) {
			rmd.exception(" Feed Date :: iTracker : " + iTracker + " : " + e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}
}
