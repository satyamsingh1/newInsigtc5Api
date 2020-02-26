package com.mps.insight.rest.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.mps.insight.dashboard.Counter5DashboardImpl;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.redis.Redis;

@Path("counter5dash")
public class Counter5Dashboard {
	@Context
	private HttpServletRequest servletRequest; 
	
	//private static final Logger log = LoggerFactory.getLogger(Counter5Dashboard.class);
	RequestMetaData rmd;
	int userID;
	int webmartID;

	
	// added by satyam  singh Dec/04/2019
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("geocountry")
	public Response getGeoCountry() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Counter5DashboardImpl impl = null;
		
		String result = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			rmd.log("In C5 Dashboard Country ip ");
			impl = new Counter5DashboardImpl(rmd);
			iTracker = 3.0;
			result = impl.getCounter5DashboardCountryData(webmartID);
			//String replData=result.replace("\"", "");
			
			iTracker = 4.0;
			rb = Response.status(200).entity(result);

		} catch (Exception e) {
			rmd.exception("Counter5Dashboard : getCountry ip  : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("nolicense")
	public Response getNoLicense() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Counter5DashboardImpl impl = null;
		String result = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			rmd.log("In C5 Dashboard nolicense ");
			//log.info("In C5 Dashboard nolicense ");
			impl = new Counter5DashboardImpl(rmd);
			iTracker = 3.0;
			result = impl.getCounter5DashboardGraphData(webmartID);
			iTracker = 4.0;
			rb = Response.status(200).entity(result);

		} catch (Exception e) {
			rmd.exception("Counter5Dashboard : getNoLicense : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("requestanalysis")
	public Response getRequestAnalysisDetail() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Counter5DashboardImpl impl = null;
		String result = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//log.info("In C5 Dashboard requestanalysis ");
			rmd.log("In C5 Dashboard requestanalysis ");
			impl = new Counter5DashboardImpl(rmd);
			iTracker = 3.0;
			result = impl.getCounter5RequestSummaryGraphData(webmartID);
			iTracker = 4.0;
			rb = Response.status(200).entity(result);

		} catch (Exception e) {
			rmd.exception("Counter5Dashboard : getRequestAnalysisDetail : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("limitexceeded")
	public Response getLimitExceededDetail() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Counter5DashboardImpl impl = null;
		String result = null;
		try {
			 iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			 iTracker = 2.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//log.info("In C5 Dashboard requestanalysis ");
			rmd.log("In C5 Dashboard limitexceeded ");
			impl = new Counter5DashboardImpl(rmd);
			iTracker = 3.0;
			result = impl.getCounter5LimitExceededGraphData(webmartID);
			iTracker = 4.0;
			rb = Response.status(200).entity(result);

		} catch (Exception e) {
			rmd.exception("Counter5Dashboard : getLimitExceededDetail : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("sitesummary")
	public Response getSiteSummary() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Counter5DashboardImpl impl = null;
		String result = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			rmd.log("In C5 Dashboard sitesummary ");
			impl = new Counter5DashboardImpl(rmd);
			iTracker = 3.0;
			result = impl.getCounter5SiteSummaryDashboardGraphData(webmartID);
			iTracker = 4.0;
			rb = Response.status(200).entity(result);

		} catch (Exception e) {
			rmd.exception("Counter5Dashboard : getSiteSummary : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
			
		}
		return rb.build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("sitesummaryc5")
	public Response getSiteSummaryC5() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Counter5DashboardImpl impl = null;
		String result = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			rmd.log("In C5 Dashboard sitesummaryc5 ");
			//log.info("In C5 Dashboard sitesummary ");
			impl = new Counter5DashboardImpl(rmd);
			iTracker = 3.0;
			result = impl.getSiteSummaryC5GraphData(webmartID);
			iTracker = 4.0;
			rb = Response.status(200).entity(result);

		} catch (Exception e) {
			rmd.exception("Counter5Dashboard : getSiteSummaryC5 : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}
	
	// saurabh
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("consolidateDatabaseSummary")
	public Response consolidateDatabaseSummary() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Counter5DashboardImpl impl = null;
		String result = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			rmd.log("Consolidate Dashboard Graph");
			impl = new Counter5DashboardImpl(rmd);
			iTracker = 3.0;
			Redis redis = new Redis();
			result = redis.getValueFromRedisWithKey(webmartID + "_consolidateDatabase");
			if (result==null) {
				result = impl.consolidateDatabaseSummary(webmartID);
				redis.setValueToRedisWithKey(webmartID + "_consolidateDatabase", result);
			}
			
			iTracker = 4.0;
			rb = Response.status(200).entity(result);

		} catch (Exception e) {
			rmd.exception("Counter5Dashboard : getRequestAnalysisDetail : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}
}
