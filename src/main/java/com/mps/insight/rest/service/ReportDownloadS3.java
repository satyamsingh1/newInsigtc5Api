package com.mps.insight.rest.service;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.aws.S3;
import com.mps.insight.dto.RequestMetaData;

@Path("s3report")
public class ReportDownloadS3 {
	private HttpServletRequest servletRequest; 
	RequestMetaData rmd;
	//private static final Logger LOG = LoggerFactory.getLogger(Setting.class);
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
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
        InputStream inputStream = null;
        ResponseBuilder rb = null;
        int authCode = 0;
        S3 s3Client = null;
        double iTracker = 0.0;
		rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
        try{
        	iTracker = 1.0;
        	rmd.log("In ReportDownload method stated");
        	//authCode = RestAuthorization.getAuthorizationCode(authString);
        	/*if(UserAuthorization.getAuthorizationCode(authString) != null){
        		//Authorization Success
        	}else {
        		throw new Exception("Basic Authoriozation : Fail : " + authCode);
        	}*/
        	//initiating Report Model
        	report = new Report(webmartCode, year, month, instCode, reportName);
        	report.setBasePath("data2/reports");
        	iTracker = 2.0;
        	//getting file path
        	filePath = report.getFilePath();
        	filePath = filePath.replaceAll("\\\\", "/");
        	//connecting to S3 Service
        	//s3Client= new S3();
        	//getting S3 file in Input Stream
        	//inputStream = s3Client.download(filePath);
        	s3Client = new S3("AKIAI3U4NGRDVAJSWRGQ", "s53ZCnX3b+g/9Hc/uIM/xf+5HErl5u35MVMyM+E2", S3.REGION.EU_WEST_2);
        	inputStream = s3Client.download("inisght_reports/",filePath);
        	//setting input File in response
        	iTracker = 3.0;
        	rb = Response.ok(inputStream, MediaType.APPLICATION_OCTET_STREAM);
        	iTracker = 4.0;
        }catch (Exception e) {
        	rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Exception : " + e.toString());
        	rmd.error(" ReportDownloadS3: GetReport : iTracker : "+iTracker+" : "+e.toString());
		}
        rmd.log("In ReportDownload method End");
        //try {s3Client.destroy();}catch(Exception e){}
        //returning the response
        return rb.header("Access-Control-Allow-Origin", "*").build();
    }	
}
