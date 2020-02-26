package com.mps.insight.rest.service;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.rest.service.Report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("report")
public class ReportDownload {
	//private static final Logger LOG = LoggerFactory.getLogger(ReportDownload.class);
	
	private HttpServletRequest servletRequest; 
	RequestMetaData rmd;
	@Context
    private UriInfo uriInfo;
    @Context
    private Request request;
    //default constructor
	public ReportDownload(){
       //LOG.info("Default Constructor ReportDownload");
		rmd.log("Default Constructor ReportDownload");
    }
	//Parameterized constructor
	public ReportDownload(UriInfo uriInfo, Request request){
	       //LOG.info("Contect/Paramereised Constructor ReportDownload");
		   rmd.log("Contect/Paramereised Constructor ReportDownload");
	       this.uriInfo = uriInfo;
	       this.request = request;
    }
	@GET
	public Response GetReport() throws Exception{
        rmd.log("Retrieving the Complete Report");
        rmd.log("GET request");
        Response response = null;  
        double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
        try{
        	iTracker = 1.0;
        	response = Response.status(200)
        			.header("Access-Control-Allow-Origin", "*")
        			.entity("KSV : Web Service Test : Success").build();
        }catch (Exception e) {
        	response = Response.status(Response.Status.EXPECTATION_FAILED)
        			.header("Access-Control-Allow-Origin", "*")
        			.entity("Exception : " + e.toString()).build();
        	rmd.error(" ReportDownload: GetReport : iTracker : "+iTracker+" : "+e.toString());
		}
        return response;
    }
	//method to download report from disk path
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("download/{webmartcode}/{year}/{month}/{instcode}/{reportname}")
	public Response GetReport(@PathParam("webmartcode") String webmartCode,
								@PathParam("year") String year, 
								@PathParam("month") String month, 
								@PathParam("instcode") String instCode, 
								@PathParam("reportname") String reportName, 
								@HeaderParam("authorization") String authString
								) throws Exception{
		String filePath = "";
        Report report = null;
        File reportFile = null;
        int returnCode = 200;
        ResponseBuilder rb = null;
        int authCode = 0;
        double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
        try{
        	iTracker = 1.0;
        	/*UserAuthorization.getAuthorizationCode(authString);
        	if(UserAuthorization.getAuthorizationCode(authString)!=null){
        		//Authorization Success
        	}else {
        		throw new Exception("Basic Authoriozation : Fail : " + authCode);
        	}*/
        	//initiating Report Model
        	report = new Report(webmartCode, year, month, instCode, reportName);
        	//getting file path
        	filePath = report.getFilePath();
        	iTracker = 2.0;
        	reportFile = new File(filePath);
        	//check for file on path
        	if(reportFile.exists()){
        		iTracker = 3.0;
        		rb = Response.ok((Object) reportFile).header("Content-Disposition","attachment; filename=\"MPS_Insight_" + reportName.trim() + "\"");
        	}else{
        		iTracker = 4.0;
        		returnCode = -9;
				rb = Response.status(returnCode).entity(returnCode + " : Report File Not Found : " + report.toString());
        	}	
        }catch (Exception e) {
        	rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.toString());
        	rmd.error(" ReportDownload: GetReport : iTracker : "+iTracker+" : "+e.toString());
		}
        //returning the response
        return rb.header("Access-Control-Allow-Origin", "*").build();
    }
	
}//END class
