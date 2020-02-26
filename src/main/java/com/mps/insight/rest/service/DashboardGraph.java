package com.mps.insight.rest.service;

import java.util.Set;

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

import com.mps.insight.c4.report.ProductTypeImpl;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.dashboard.DashboardGraphImpl;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.product.SiteSummary;
import com.mps.redis.Redis;


@Path("product")
public class DashboardGraph {
	
	@Context
	private HttpServletRequest servletRequest; 
	
	private static final Logger LOG = LoggerFactory.getLogger(DashboardGraph.class);

	private RequestMetaData rmd;
	String userCode;
	int webmartID;

	
	/*
	public DashboardGraph(@HeaderParam("token") String token) throws MyException {

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

	}
*/
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("analysisgraph")
	public Response getProcessGraphDetails(@FormParam("monthCount") int duration) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		PublisherSettings publisherSettings = null;
		Redis redis = new Redis();
		int liveYear = 0;
		int liveMonth = 0;
		DashboardGraphImpl graphImpl = new DashboardGraphImpl(rmd);
		ProductTypeImpl dynamicReports = new ProductTypeImpl(rmd);
		publisherSettings = new PublisherSettings(rmd);
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			iTracker=3.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker=4.0;
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
			iTracker=5.0;
			String analysisgraph = redis
					.getValueFromRedisWithKey(webmartID + "_analysis_graph");
			iTracker=6.0;
			if (null == analysisgraph || analysisgraph.isEmpty() || analysisgraph.length() <= 0) {
				if(liveMonth>0){
			iTracker=7.0;
				Set<String> productList = dynamicReports.getProductList();
			iTracker=8.0;
				JsonObject Object = graphImpl.getDashboardGraphData(webmartID, productList);
				//LOG.info(Object.toString());
			iTracker=9.0;
				rb = Response.status(200).entity(Object.toString());
			iTracker=10.0;
				redis.setValueToRedisWithKey(webmartID + "_analysis_graph", Object.toString());
				}
			} else {
			iTracker=11.0;
				rb = Response.status(200).entity(analysisgraph);
			}
		} catch (Exception e) {
			rmd.exception("DashboardGraph : getProcessGraphDetails : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("applicableproducts")
	public Response getProductTrendType() throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		webmartID = rmd.getWebmartID();
		userCode = rmd.getUserCode();
		rmd.log("Retrieving the getProductTrendType  Webmart ID : " + webmartID);
		//LOG.info("Retrieving the getProductTrendType  Webmart ID : " + webmartID);
		ResponseBuilder rb = null;
		PublisherSettings publisherSettings;
		JsonObject jarray =null;
		try {
			iTracker=1.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			publisherSettings = new PublisherSettings(rmd);
			iTracker=2.0;
			jarray = publisherSettings.getProductTrendType(webmartID);
			iTracker=3.0;
			rb = Response.status(200).entity(jarray.toString());

		} catch (Exception e) {
		//	LOG.error("Retrieved getProductTrendType Json Array : " + jarray.toString());
			rmd.exception("DashboardGraph : getProductTrendType : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("progressdetail")
	public Response getFullTextDetailTable(@FormParam("productType") String productType,
			@FormParam("product") String product, @FormParam("monthCount") int duration) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		SiteSummary siteSummary = null;
		int liveYear = 0;
		int liveMonth = 0;
		PublisherSettings publisherSettings = null;
		JsonObject jarray = null;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
		/*	LOG.info(" In getFullTextDetailTable Method Wbmart ID : " + webmartID + " productType : " + productType
					+ " monthCount : " + duration);*/
			rmd.log(" In getFullTextDetailTable Method Wbmart ID : " + webmartID + " productType : " + productType
					+ " monthCount : " + duration);
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			publisherSettings = new PublisherSettings(rmd);
			iTracker=3.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker=4.0;
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
			siteSummary = new SiteSummary(rmd);
			iTracker=5.0;
			jarray = siteSummary.getFullTextProgressTable(webmartID, productType, product, duration,
					liveYear, liveMonth);
			iTracker=6.0;
			rb = Response.status(200).entity(jarray.toString());
			
			
		} catch (Exception e) {
			//LOG.error("In getFullTextDetailTable Final Json Array : " + jarray.toString());
			rmd.exception("DashboardGraph : getFullTextDetailTable : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("progressdetailmore")
	public Response getFullTextDetailTableMore(@FormParam("productType") String productType,
			@FormParam("product") String product, @FormParam("monthCount") int duration,
			@FormParam("order") String order) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		
		ResponseBuilder rb = null;
		SiteSummary siteSummary = null;
		int liveYear = 0;
		int liveMonth = 0;
		PublisherSettings publisherSettings = null;
		JsonObject jarray = null;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			/*LOG.info(" In getFullTextDetailTableMore Method Wbmart ID : " + webmartID + " productType : " + productType
					+ " monthCount : " + duration);*/
			rmd.log(" In getFullTextDetailTableMore Method Wbmart ID : " + webmartID + " productType : " + productType
					+ " monthCount : " + duration);
			
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			publisherSettings = new PublisherSettings(rmd);
			iTracker=3.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker=4.0;
			liveMonth = publisherSettings.getPubLiveMonth(webmartID, liveYear);
			siteSummary = new SiteSummary(rmd);
			iTracker=5.0;
			jarray = siteSummary.getFullTextProgressTableMore(webmartID, productType, product, duration,
					liveYear, liveMonth, order);
			iTracker=6.0;

			rb = Response.status(200).entity(jarray.toString());
			
		} catch (Exception e) {
		//	LOG.error("In getFullTextDetailTableMore Final Json Array : " + jarray.toString());
			rmd.exception("DashboardGraph : getFullTextDetailTableMore : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("usertechanalysis")
	public Response getUserTechAnalisys() throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		
		ResponseBuilder rb = null;
		int liveYear = 0;
		int liveMonth = 0;
		PublisherSettings publisherSettings = null;
		JsonObject jarray = null;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			//LOG.info(" In getUserTechAnalisys Method Wbmart ID : " + webmartID );
			rmd.log(" In getUserTechAnalisys Method Wbmart ID : " + webmartID );
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			publisherSettings = new PublisherSettings(rmd);
			iTracker=3.0;
			liveYear = publisherSettings.getLiveYear(webmartID);
			iTracker=4.0;
			liveMonth = publisherSettings.getLibLiveMonth(webmartID, liveYear);
			DashboardGraphImpl graphImpl = new DashboardGraphImpl(rmd);
			iTracker=5.0;
			jarray = graphImpl.getUserTechDetail(webmartID,liveYear,liveMonth);
			iTracker=6.0;
			rb = Response.status(200).entity(jarray.toString());
			
		} catch (Exception e) {
			//LOG.error("In getUserTechAnalisys Final Json Array : " + jarray.toString());
			rmd.exception("DashboardGraph : getUserTechAnalisys : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}
	
	
}
