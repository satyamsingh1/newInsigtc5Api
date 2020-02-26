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

import com.mps.insight.c5.report.PublisherReport5Impl;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.product.Reports;

@Path("pubreport5")
public class Counter5PublisherReportService {
	@Context
	private HttpServletRequest servletRequest;

	//private static final Logger LOG = LoggerFactory.getLogger(Counter5PublisherReportService.class);

	RequestMetaData rmd;

	String userCode;
	int webmartID;
	UserDTO user;

	/*
	 * public Counter5PublisherReportService(@HeaderParam("token") String token)
	 * throws Exception {
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("reportlist")
	public Response getDynamicReportList() throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		PublisherReport5Impl c5reportimpl = null;
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			rmd.log(" In rollBackAll Method ");
			// LOG.info(" In rollBackAll Method for Wbmart ID : " + webmartID);
			// check for null and valid values
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			c5reportimpl = new PublisherReport5Impl(rmd);
			iTracker = 3.0;
			JsonArray jobj = c5reportimpl.getCounter5ReportsList(webmartID);
			iTracker = 4.0;
			rb = Response.status(200).entity(jobj.toString());
		} catch (Exception e) {
			rmd.exception("Counter5PublisherReportService : getDynamicReportList : iTracker : " + iTracker + " : "
					+ e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("reportjson")
	public Response getDynamicReportJson(@FormParam("fromdate") String fromdate, 
			@FormParam("todate") String todate,
			@FormParam("report") String report, 
			@FormParam("frequency") String frequency, 
			@FormParam ("bookJournalName") String bookJournalName,
			@FormParam("period") String selectedDate) throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		PublisherReport5Impl reportimpl = null;
		Counter5DTO c5dto = new Counter5DTO();
		String download = "preview";
		try {
			iTracker = 1.0;
			webmartID = rmd.getWebmartID();
			iTracker = 2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			iTracker = 3.0;
			c5dto.setReportCode(report);
			iTracker = 4.0;
			if (null == fromdate || fromdate.trim().equals("")) {
				c5dto.setFromDate(todate);
			} else {
				iTracker = 5.0;
				c5dto.setFromDate(fromdate);
			}
			iTracker = 6.0;
			c5dto.setFrequency(frequency);
			iTracker = 7.0;
			c5dto.setToDate(todate);
			iTracker = 8.0;
			c5dto.setWebmartID(webmartID);
			//Book / Journal Name For Choose Book and Journal
			if(bookJournalName!=null){
				c5dto.setBookJournalName(bookJournalName);
				if(selectedDate!=null){
					c5dto.setFromDate(selectedDate);
					c5dto.setToDate(selectedDate);
				}else{
					c5dto.setFromDate(rmd.getLiveMonth()+"-"+rmd.getLiveYear());
					c5dto.setToDate(rmd.getLiveMonth()+"-"+rmd.getLiveYear());
				}
				
			}
			rmd.log(" In Counter 5 Report Detail :" + c5dto.toString());
			// LOG.info(" In Counter 5 Report Detail : " + c5dto.toString());

			reportimpl = new PublisherReport5Impl(rmd);
			iTracker = 9.0;
			JsonObject jobj = reportimpl.getCounter5ReportsJson(c5dto, download);
			// LOG.info("Dynamic Data : "+jobj.toString());
			iTracker = 10.0;
			rb = Response.status(200).entity(jobj.toString());
		} catch (Exception e) {
			rmd.exception("Counter5PublisherReportService : getDynamicReportJson : iTracker : " + iTracker + " : "
					+ e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return rb.build();
	}
	
		//change by satyam 06/02/2019
       //change by Gyana on 21/02/2019
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("publisherbookreportall")
	public Response getpublisherbookreportallValues(@FormParam("DataType") String datatype) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		Reports reports=new Reports(rmd);
		JsonArray jarray =null;
		//String book=null;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//LOG.info(" In getFullTextValues Method Getting Tiles Fulltext detail for Wbmart ID : " + webmartID);
			rmd.log(" In getpublisherbookreportallValues Method Getting Tiles publisher book report all detail for Wbmart ID : " + webmartID);
			jarray =  reports.getBookDetail(webmartID,datatype);
					iTracker=3.0;
				    rb = Response.status(200).entity(jarray.toString());
		}catch(Exception e)
	      {
			//LOG.error("getFullTextValues Final Json Array : " + jarray.toString());
			rmd.exception("publisherbookreportall : getpublisherbookreportallValues : iTracker : "+iTracker+" : "+e.toString());
		rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}
	
	
//	Gyana changes on 25/02/2019
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("publisherbookreportforalphabet")
	public Response getpublisherbookreportforalphabet(@FormParam("alphabet") String alphabet,@FormParam("DataType") String datatype) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		
		Reports reports=new Reports(rmd);
		JsonObject jobj =null;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			//LOG.info(" In getFullTextValues Method Getting Tiles Fulltext detail for Wbmart ID : " + webmartID);
			rmd.log(" In getFullTextValues Method Getting Tiles Fulltext detail for Wbmart ID : " + webmartID);
			
			jobj =  reports.getpublisherbookreportforalphabet(webmartID,alphabet,datatype);
		
			jobj.toString();
			rb = Response.status(200).entity(jobj.toString());
			
		}catch(Exception e)
		{
			//LOG.error("getFullTextValues Final Json Array : " + jarray.toString());
			rmd.exception("Dashboard : getFullTextValues : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.getMessage());
		}
		return rb.build();
	}
	
}
