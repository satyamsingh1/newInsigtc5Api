package com.mps.insight.rest.service;

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
import com.mps.insight.exception.MyException;
import com.mps.insight.product.LibraryReportsPubView;

@Path("configurereports")
public class ConfigureLibReport {
	
	//private static final Logger LOG = LoggerFactory.getLogger(ConfigureLibReport.class);

	@Context
	private HttpServletRequest servletRequest; 
	RequestMetaData rmd;
	
	String userCode;
	int webmartID;
/*
	public ConfigureLibReport(@HeaderParam("token") String token) throws Exception {

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
	@Path("libreports")
	public Response getLibReports() throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		LibraryReportsPubView libraryReportsPubView = null;
		// SiteSummary siteSummary = null;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			rmd.log("In getLibReports Method Getting Tiles getLibReports detail ");
			/*LOG.info(" In getLibReports Method Getting Tiles getLibReports detail for Wbmart ID : "
					+ webmartID);*/
			if (webmartID == 0) {
				throw new MyException("{\"error\":\" Invalid Session \"}");
			}
			libraryReportsPubView = new LibraryReportsPubView(rmd);
			iTracker=3.0;
			JsonObject jarray = libraryReportsPubView.getLibReports(webmartID);
			iTracker=4.0;
			rb = Response.status(200).entity(jarray.toString());
			//LOG.info("getLibReports  Final Json Array : " + jarray.toString());
		} catch (Exception e) {
			rmd.exception("ConfigureLibReport : getLibReports : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("updatelibreports")
	public Response updateLibReports(@FormParam("reportids") String reportids) throws Exception {
		double iTracker=0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		ResponseBuilder rb = null;
		LibraryReportsPubView libraryReportsPubView = null;
		try {
			iTracker=1.0;
			webmartID = rmd.getWebmartID();
			iTracker=2.0;
			userCode = rmd.getUserCode();
			rmd.log("In getLibReports Method Getting Tiles getLibReports detail ");
			/*LOG.info(" In getLibReports Method Getting Tiles getLibReports detail for Wbmart ID : "
					+ webmartID);*/
			if (webmartID == 0) {
				throw new MyException("{\"error\":\" Invalid Session \"}");
			}
			libraryReportsPubView = new LibraryReportsPubView(rmd);
			iTracker=3.0;
			String jarray = libraryReportsPubView.updateLibreportsConfig(webmartID,reportids);
			iTracker=4.0;
			rb = Response.status(200).entity("{\"success\":\""+jarray+"\"}");
		} catch (Exception e) {
			rmd.exception("ConfigureLibReport : updateLibReports : iTracker : "+iTracker+" : "+e.toString());
			rb = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.toString());
		}
		return rb.build();
	}
	
}
