package com.mps.insight.rest.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.product.Account;
import com.mps.insight.product.Feed;
import com.mps.insight.product.PublisherSettings;
import com.mps.redis.Redis;

@RestController("accounts")
public class AccountService {
	@Context
	private HttpServletRequest servletRequest; 
	RequestMetaData rmd;
	
	String userCode ="";
	int webmartID = -2;
	String token = "";
	private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);
	
	@Produces(MediaType.APPLICATION_JSON)
	@PostMapping("grouptype")
	public Response getAccountsGroupType() throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		String jsonData = "";
		JsonObject jObjArray = null;
		Account account = null;
		try {
		    iTracker=1.0;
			webmartID = rmd.getWebmartID();
			 iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException("Invalid Session ");
			}
			account = new Account(rmd);
			 iTracker=3.0;
			jObjArray = account.getAccountsGroupType(webmartID);
			 iTracker=4.0;
			jsonData = jObjArray.toString();
			 iTracker=5.0;
			if (jsonData.isEmpty())
				rb = Response.status(200).entity("{\"error\":\"No accountGroup Found\"}");
			else
				iTracker=6.0;
				rb = Response.status(200).entity(jsonData);
		} catch (MyException e) {
			rmd.exception("Accounts : getAccountsGroupType : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("accountyearlist")
	public Response getAccountsYearList(@FormParam("accountID") String accountID) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		String status = "";
		JsonArray jObjArray = null;
		PublisherSettings ps = null;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(" Invalid Session ");
			}
			ps = new PublisherSettings(rmd);
			iTracker=3.0;
			jObjArray = ps.getAccountLiveYearList(webmartID, accountID, status);
			iTracker=4.0;
			rb = Response.status(200).entity(jObjArray.toString());
		} catch (MyException e) {
			rmd.exception("Accounts : getAccountsYearList : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("generatedreports")
	public Response getAccountsGeneratedReports(@FormParam("accountID") String account_code,
			@FormParam("year") String year, @FormParam("month") String month, @FormParam("screen") String screen)
			throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		String jsonData = "";
		JsonObject jObjArray = null;
		Account account = null;
		PublisherSettings pubSettings = null;
		int liveYear = 0;
		String liveMonth = "";
		// String accountMonth="";
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException("Invalid Session ");
			}
			pubSettings = new PublisherSettings(rmd);
			iTracker=3.0;
			if (year != null && !"".equalsIgnoreCase(year)) {
				liveYear = Integer.parseInt(year);
			} else {
				iTracker=4.0;
				liveYear = pubSettings.getLiveYear(webmartID);
			}
			iTracker=5.0;
			if (month != null && !"".equalsIgnoreCase(month)) {
				liveMonth = month;
			} else {
				iTracker=6.0;
				liveMonth = pubSettings.getAccountMonthStatus(webmartID, liveYear, account_code, screen);
			}
			account = new Account(rmd);
			iTracker=7.0;
			if (screen.contains("QA")) {
				if (liveMonth.equalsIgnoreCase("")) {
					liveMonth = pubSettings.getAccountMonthStatus(webmartID, liveYear, account_code, "LIVE");
					screen = "LIVE";
				}
			} else {
				iTracker=8.0;
				screen = "LIVE";
			}
			iTracker=9.0;
			jObjArray = account.getAccountsGeneratedReports(webmartID, liveYear, liveMonth, account_code, screen);
			jsonData = jObjArray.toString();
			iTracker=10.0;
			if (jsonData.isEmpty())
				rb = Response.status(200)
						.entity(InsightConstant.ERROR_RESPONSE + ":\"no Generated reports available\"}");
			else
				iTracker=11.0;
				rb = Response.status(200).entity(jsonData);
		} catch (MyException e) {
			rmd.exception("Accounts : getAccountsGeneratedReports : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("user/pubfavorites")
	public Response getPubFavorites(@Context UriInfo info) throws MyException {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		String jsonData = "";
		JsonObject jObjArray = null;
		Feed feed = null;
		PublisherSettings pubSettings = null;
		int setNo = 0;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(" Invalid Session ");
			}
			pubSettings = new PublisherSettings(rmd);
			feed = new Feed(rmd);
			try {
				iTracker=3.0;
				setNo = pubSettings.getSetNo(webmartID);
			} catch (Exception e) {
				/*LOG.info("Exception in retrieving set no for given period" + e);*/
				rmd.error("Exception in retrieving set no for given period" + e.toString());
				rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			}
			// get all the feeds for the publisher
			try {
				iTracker=4.0;
				jObjArray = feed.getFavoritesList(webmartID);
			} catch (Exception e) {
				//LOG.info("Exception in retrieving favourites list for set no  " + setNo + " :: " + e);
				rmd.error("Exception in retrieving favourites list for set no  " + setNo + " :: " + e.toString());
				rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			}
			jsonData = jObjArray.toString();
			iTracker=5.0;
			if (jsonData.isEmpty())
				rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + ":\"No favorite found\"}");
			else
				iTracker=6.0;
				rb = Response.status(200).entity(jsonData);
		} catch (MyException e) {
			rmd.exception("Accounts : getPubFavorites : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("user/myfavorites")
	public Response getUserFavorites(@Context UriInfo info) throws MyException {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		PublisherSettings pubSettings = null;
		String jsonData = "";
		JsonObject jObjArray = null;
		Account accounts = null;
		int setNo = 0;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException("Invalid Session ");
			}
			//LOG.info("In getUserFavorites method start");
			rmd.log("In getUserFavorites method start");
			pubSettings = new PublisherSettings(rmd);
			accounts = new Account(rmd);
			// get my favorites list for the publisher
			try {
				iTracker=3.0;
				setNo = pubSettings.getSetNo(webmartID);
				iTracker=4.0;
				jObjArray = accounts.getMyFavoritesList(webmartID, String.valueOf(userCode));
			} catch (Exception e) {
				//LOG.error("Exception in retrieving my favourites list for set no  " + setNo + " :: " + e);
				rmd.error("Exception in retrieving my favourites list for set no  " + setNo + " :: " + e.toString());
				rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			}
			jsonData = jObjArray.toString();
			iTracker=5.0;
			if (jsonData.isEmpty())
				rb = Response.status(200).entity("{\"error\":\"No User Favorite found\"}");
			else
				iTracker=6.0;
				rb = Response.status(200).entity(jsonData);
		} catch (Exception e) {
			rmd.exception("Accounts : getUserFavorites : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
		}
	
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("searchresult")
	public Response getSearchResult(@FormParam("accountName") String accountName,
			@FormParam("accountType") String accountType) throws MyException {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		PublisherSettings pubSettings = null;
		String jsonData = "";
		JsonObject jObjArray = null;
		Account accounts = null;
		int setNo = 0;
		String searchTerm = "";
		String searchTerm1 = "";
		
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			
			iTracker=2.0;
			userCode = rmd.getUserCode();
			
			// String searchTearm2="";
			accountName = accountName.replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\\'");
			iTracker=3.0;
			if (accountName.contains("[{")) {
				searchTerm = accountName.substring(accountName.indexOf("[{") + 2, accountName.indexOf("}]"));
				if (accountName.contains(" - ")) {
					iTracker=4.0;
					searchTerm1 = accountName.substring(0, accountName.indexOf(" - "));
					searchTerm = accountName.substring(0, accountName.indexOf(" - "));
				} else {
					iTracker=5.0;
					searchTerm1 = accountName.substring(0, accountName.indexOf("[{"));
					searchTerm = accountName.substring(0, accountName.indexOf("[{"));
				}
			} else {
				iTracker=6.0;
				searchTerm = accountName.trim();
			}
			
			iTracker=7.0;
			if (webmartID == 0) {
				throw new MyException(" Invalid Session , webmartID = 0");
			}
			
			iTracker=8.0;
			if(searchTerm.length() > 2){
				pubSettings = new PublisherSettings(rmd);
				accounts = new Account(rmd);
				setNo = pubSettings.getSetNo(webmartID);

				//getting results
				jObjArray = accounts.getSearchResults(webmartID, searchTerm, accountType);
			}else{
				
				
			}
				
			/*
			iTracker=9.0;
			jObjArray = accounts.getSearchResultsByCode(webmartID, searchTerm, accountType, setNo);
			
			iTracker=10.0;
			if (jObjArray.toString().length() < 14 && searchTerm1.length() > 2) {
				jObjArray = accounts.getSearchResults(webmartID, searchTerm1, accountType, setNo);
			}
			
			iTracker=11.0;
			if (jObjArray.toString().length() < 14) {
				jObjArray = accounts.getSearchResults(webmartID, searchTerm, accountType, setNo);
			}
			*/
			
			jsonData = jObjArray.toString();
			iTracker=12.0;
			if (jsonData.isEmpty() || jsonData.length() < 14)
				rb = Response.status(200).entity("{\"error\":\"No search result found \"}");
			else
				iTracker=13.0;
				rb = Response.status(200).entity(jsonData);
		} catch (Exception e) {
			rmd.exception("Exception in retrieving search accounts list for set no  " + setNo + " : " + iTracker + ": " + e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("reportstatus")
	public Response getReportsStatus(@FormParam("accountID") String accountID) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		String jsonData = "";
		JsonArray jObjArray = null;
		Account accounts = null;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			
			if (webmartID == 0) {
				throw new MyException(" Invalid Session ");
			}
			accounts = new Account(rmd);
			try {
				iTracker=3.0;
				jObjArray = accounts.getAllAvaiableLive(webmartID, accountID);
			} catch (Exception e) {
				rmd.exception("Accounts : getReportsStatus : iTracker : "+iTracker+" : "+e.toString());
				rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			}
			jsonData = jObjArray.toString();
			iTracker=5.0;
			if (jsonData.isEmpty())
				rb = Response.status(200).entity("{\"error\":\"No past report status found \"}");
			else
				iTracker=6.0;
				rb = Response.status(200).entity(jsonData);
		} catch (Exception e) {
			rmd.exception("Accounts : getReportsStatus : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("settingaccountlistintocache")
	public Response setAccountListForCache() throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Account account = null;
		account = new Account(rmd);
		PublisherSettings pubSettings = null;
		List<String> accountsList = new ArrayList<String>();

		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			
			pubSettings=new PublisherSettings(rmd);
			iTracker=3.0;
			int setNo=pubSettings.getSetNo(webmartID);
			if (webmartID == 0) {
				throw new MyException(" Invalid Session ");
			}
			iTracker=4.0;
			accountsList = account.getAllAccountsByPublisher(webmartID);
			Redis redis = new Redis();
			iTracker=5.0;
			String status = redis.setAccountListIntoRedis(accountsList, webmartID,setNo);
			iTracker=6.0;
			rb = Response.status(200).entity(status + " for webmartID = " + webmartID);
		} catch (Exception e) {
			rmd.exception("Accounts : setAccountListForCache : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("publisheraccountlistfromcache")
	public Response getAccountListFromCache(@FormParam("search") String search) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		List<String> jedisAccountsList = new ArrayList<String>();
		PublisherSettings ps=null;
		String jedisCacheKey = null;
		JsonArrayBuilder jsonAccountsList = Json.createArrayBuilder();
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			ps=new PublisherSettings(rmd);
			
			if (webmartID == 0) {
				throw new MyException("Invalid Session ");
			}
			iTracker=3.0;
			//int setNo=ps.getSetNo(webmartID);
			jedisCacheKey=webmartID+"_account_list";
			Redis redis = null;
			redis = new Redis();
			iTracker=4.0;
			jedisAccountsList = redis.getAccountListFromRedis(jedisCacheKey);
			iTracker=5.0;
			if (jedisAccountsList.isEmpty()) {
				setAccountListForCache();
				jedisAccountsList = redis.getAccountListFromRedis(jedisCacheKey);
			}
			for (int i = 0; i < jedisAccountsList.size(); i++) {
				iTracker=6.0;
				if (jedisAccountsList.get(i).toLowerCase().contains(search.toLowerCase())) {
					jsonAccountsList.add(jedisAccountsList.get(i));
				}

			}
			iTracker=7.0;
			rb = Response.status(200).entity(jsonAccountsList.build().toString());
		} catch (Exception e) {
			rmd.exception("Accounts : getAccountListFromCache : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
		}
		return rb.build();
	}


	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("searchbyalphabet")
	public Response searchByAlphabet(@FormParam("alphabet") String alphabet) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		PublisherSettings pubSettings = null;
		String jsonData = "";
		JsonObject jObjArray = null;
		Account accounts = null;
		int setNo = 0;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException("Invalid Session ");
			}
			pubSettings = new PublisherSettings(rmd);
			accounts = new Account(rmd);
			try {
				iTracker=3.0;
				setNo = pubSettings.getSetNo(webmartID);
				iTracker=4.0;
				jObjArray = accounts.searchAccountsByAlphabet(webmartID, alphabet, setNo);
			} catch (Exception e) {
				//LOG.info("Exception in retrieving search by alphabet list for set no  " + setNo + " :: " + e);
				rmd.error("Exception in retrieving search by alphabet list for set no  " + setNo + " :: " + e.toString());
				rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			}
			jsonData = jObjArray.toString();
			iTracker=5.0;
			if (jsonData.isEmpty())
				rb = Response.status(200).entity("{\"error\":\"No record found with this alphabet\"}");
			else
				iTracker=6.0;
				rb = Response.status(200).entity(jsonData);
		} catch (Exception e) {
			rmd.exception("Accounts : searchByAlphabet : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("searchusersbyaccount")
	public Response searchUsersByAccount(@FormParam("accountid") String accId) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		
		ResponseBuilder rb = null;
		JsonObject jarray;
		Account account = null;
		//LOG.info(" In searchUsersByAccount Method Start : " + userID);
		rmd.log(" In searchUsersByAccount Method Start : " + accId);
		if (null ==accId) {
			throw new Exception("Account id empty");
		}
		try {
			iTracker=1.0;
			if (webmartID == 0) {
				throw new MyException("{\"error\":\" Invalid Session \"}");
			}
			account = new Account(rmd);
			iTracker=2.0;
			jarray = account.searchUsersByAccount(accId, userCode, webmartID);
			iTracker=3.0;
			rb = Response.status(200).entity(jarray.toString());

		} catch (Exception e) {
			rmd.exception("Accounts : searchUsersByAccount : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			//LOG.error("In searchUsersByAccount Method Exception : " + e.toString());
		}
		
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("searchunlinkedusers")
	public Response searchUnlinkedUsers(@FormParam("accountid") String accId, @FormParam("email") String email)
			throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		
		ResponseBuilder rb = null;
		JsonObject jarray;
		Account account = null;
		PublisherSettings ps = null;
		
	//	LOG.info(" In searchUnlinkedUsers Method Start : " + userID);
		rmd.log( " In searchUnlinkedUsers Method Start : " + accId);
		try {
			iTracker=1.0;
			if (webmartID == 0) {
				throw new MyException("{\"error\":\" Invalid Session \"}");
			}
			ps = new PublisherSettings(rmd);
			iTracker=2.0;
			account = new Account(rmd);
			iTracker=3.0;
			jarray = account.searchUnlinkedUserAccount(accId, webmartID, email);
			iTracker=4.0;
			rb = Response.status(200).entity(jarray.toString());

		} catch (Exception e) {
			rmd.exception("Accounts : searchUnlinkedUsers : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			//LOG.error("In searchUnlinkedUsers Method Exception : " + e.toString());
		}
		
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("unlinkedusers")
	public Response unlinkedUser(@FormParam("account_id") String accountCode, @FormParam("userCode") String userCode)
			throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
	
		ResponseBuilder rb = null;
	//	LOG.info(" In searchUnlinkedUsers Method Start : " + userID);
		rmd.log( " In UnlinkedUsers Method Start for : " + accountCode);
		try {
			Account account = new Account(rmd);
			iTracker=2.0;
			String result = account.deleteUserAccount(accountCode, userCode);
			rb = Response.status(Response.Status.OK).entity(result);
			
		} catch (Exception e) {
			rmd.exception("Accounts : searchUnlinkedUsers : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			//LOG.error("In searchUnlinkedUsers Method Exception : " + e.toString());
		}
		
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("deleteuseraccount")
	public Response deleteUserAccount(@FormParam("account_id") int accountId, @FormParam("userCode") String userCode)
			throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
				
		ResponseBuilder rb = null;
		Account account = null;
		rmd.log(" In deleteUserAccount Method Start : " + accountId);
		//LOG.info(" In deleteUserAccount Method Start : " + userID);
		try {
			iTracker=1.0;
			if (webmartID == 0) {
				throw new MyException("{\"error\":\" Invalid Session \"}");
			}
			account = new Account(rmd);
			iTracker=2.0;
			String response = account.deleteUserAccount(webmartID, accountId, userCode);
			iTracker=3.0;
			rb = Response.status(Response.Status.OK).entity(response);

		} catch (Exception e) {
			rmd.exception("Accounts : deleteUserAccount : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		//	LOG.error("In deleteUserAccount Method Exception : " + e.toString());
		}
		
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("accountlistatoz")
	public Response accountListAtoZ() throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		
		ResponseBuilder rb = null;
		Account account = null;
		PublisherSettings ps = null;
		JsonObject jo = null;
		//LOG.info(" In accountListAtoZ Method Start : " + userID);
		try {
			iTracker=1.0;
			if (webmartID == 0) {
				throw new MyException("{\"error\":\" Invalid Session \"}");
			}
			ps = new PublisherSettings(rmd);
			account = new Account(rmd);
			iTracker=2.0;
			int setNo = ps.getSetNo(webmartID);
			iTracker=3.0;
			jo = account.checkAccountAtoZ(webmartID);
			iTracker=4.0;
			rb = Response.status(Response.Status.OK).entity(jo.toString());

		} catch (Exception e) {
			rmd.exception("Accounts : accountListAtoZ : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		//	LOG.error("In accountListAtoZ Method Exception : " + e.toString());
		}
		
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("checkaccountfav")
	public Response accountFavoriteCheck(@FormParam("accountid") String accId) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		
		ResponseBuilder rb = null;
		JsonArray jarray;
		Account account = null;
		//LOG.info(" In accountFavoriteCheck Method Start : " + userID);
		rmd.log(" In accountFavoriteCheck Method Start : " + accId);
		try {
			iTracker=1.0;
			if (webmartID == 0) {
				throw new MyException("{\"error\":\" Invalid Session \"}");
			}
			account = new Account(rmd);
			iTracker=2.0;
			jarray = account.checkAccountFavorite(webmartID, accId);
			iTracker=3.0;
			if (jarray.isEmpty()) {
				rb = Response.status(200).entity("{\"favorite\":\"add\"}");
			} else {
				iTracker=4.0;
				rb = Response.status(200).entity("{\"favorite\":\"remove\"}");
			}
		} catch (Exception e) {
			rmd.exception("Accounts : accountFavoriteCheck : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		//	LOG.error("In accountFavoriteCheck Method Exception : " + e.toString());
		}
		
		return rb.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("exportaccountlist")
	public Response exportAccountList(@QueryParam("type") String type) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		String contentType="Content-Disposition";
		String attachment="attachment; filename=\"AccountList.csv\"";
		Account account=new Account(rmd);
		InputStream exportCsv = null;
		
		//PublisherSettings ps = null;
		 rmd.log(" In exportAccountList Method Start : ");
		//LOG.info(" In exportAccountList Method Start : ");
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			
			if (webmartID == 0) {
				throw new MyException("{\"error\":\" Invalid Session \"}");
			}
			iTracker=3.0;
			exportCsv = account.exportData(webmartID);
			iTracker=4.0;
			rb = Response.ok(exportCsv);
        	rb.header(contentType,attachment);
		} catch (Exception e) {
			rmd.exception("Accounts : exportAccountList : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			//LOG.error("In exportAccountList Method Exception : " + e.toString());
		}
		
		return rb.build();
	}
	//*********************************************************************

}
