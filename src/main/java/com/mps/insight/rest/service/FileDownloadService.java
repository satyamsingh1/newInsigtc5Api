package com.mps.insight.rest.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.mps.aws.S3;
import com.mps.insight.c4.report.DynamicReportFormatImpl;
import com.mps.insight.c5.report.Counter5ReportImpl;
import com.mps.insight.c5.report.PublisherReport5Impl;
import com.mps.insight.c5.report.publisher.SITE_OVERVIEW_All_INST;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.UserInfo;
import com.mps.insight.impl.DownloadManagerImpl;
import com.mps.insight.product.Account;
import com.mps.insight.product.PublisherSettings;

@Path("download")
public class FileDownloadService {
	@Context
	private HttpServletRequest servletRequest;
    //private static final Logger LOG =LoggerFactory.getLogger(FileDownloadService.class);
	RequestMetaData rmd;

	String userCode;
	int webmartID;
	String contentType="Content-Disposition";
	String attachment="attachment; filename=\"";

	/*
	 * public FileDownloadService(@QueryParam("token") String token) throws
	 * MyException { LOG.info("FileDownloadService constructor called ..."); try
	 * { if (token == null) { throw new MyException("Token : NULL"); } if
	 * (token.trim().equalsIgnoreCase("")) { throw new
	 * MyException("Token : BLANK"); } if (token.contains(" ")) {
	 * token=token.replace(" ", "+"); }
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
	 * } catch (Exception e) {
	 * 
	 * }
	 * 
	 * }
	 */
	@GET
	@Path("report")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@QueryParam("reporturl") String reportUrl, @QueryParam("year") String year,
			@QueryParam("month") String month, @QueryParam("day") String day,
			@QueryParam("reporttype") String reporttype, @QueryParam("reportname") String reportname,
			@QueryParam("format") String format, @QueryParam("accountcode") String accountcode,
			@QueryParam("screen") String screen) {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		iTracker = 1.0;
		webmartID = rmd.getWebmartID();
		iTracker = 2.0;
		userCode = rmd.getUserCode();
		ResponseBuilder response = null;
		InputStream inputStream = null;
		PublisherSettings ps = null;
		DownloadManagerImpl dml = new DownloadManagerImpl();
		Account acc = null;
		iTracker = 3.0;
		if (null != format && format.equalsIgnoreCase("xml")) {
			format = "zip";
		}
		iTracker = 4.0;
		if (null != format && format.equalsIgnoreCase("gcsv")) {
			format = "csv";
			reportname = reportname + "_german";
		}
		int tempmonth = 0;
		try {
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			ps = new PublisherSettings(rmd);
			iTracker = 5.0;
			String publisher = ps.getPublisherCode(webmartID);
			acc = new Account(rmd);
			iTracker = 6.0;
			if (null == month || month.trim().equalsIgnoreCase("") || month.equalsIgnoreCase("00")) {
				if (null == screen || screen.equalsIgnoreCase("LIVE")) {
					if (null == screen && reporttype.equalsIgnoreCase("Publisher")) {
						tempmonth = ps.getLibLiveMonthC4(webmartID, Integer.parseInt(year));//need to change 
					} else if (reporttype.equalsIgnoreCase("feeds")) {
						tempmonth = 0;
					} else {
						iTracker = 0.0;
						tempmonth = ps.getLibLiveMonthC4(webmartID, Integer.parseInt(year));
					}
				} else {
					iTracker = 7.0;
					tempmonth = ps.getLibLiveMonthC4(webmartID, Integer.parseInt(year));
				}
				if (tempmonth < 10) {
					month = "0" + tempmonth;
				} else {
					month = "" + tempmonth;
				}
			}
			/*iTracker = 8.0;
			if (null != reporttype && reporttype.equalsIgnoreCase("counter")) {
				if (null == screen || screen.equalsIgnoreCase("LIVE")) {
					month = acc.getAccountMaxMonthLive(webmartID, accountcode, Integer.parseInt(year));
				} else {
					// month=acc.getAccountMaxMonthLive(webmartID, accountcode,
					// Integer.parseInt(year));
				}
			}*/
			String accountCode = accountcode;
			if (webmartID == 2106) {
				if (accountcode.startsWith("00")) {
					accountCode = accountCode.replaceAll("0+([1-9].*)", "$1");
					// String ss=str.replaceAll("0+([^0]*)", "$1");
				}
			}
			iTracker = 9.0;
			inputStream = dml.getFile(reportUrl, publisher, year, month, day, reporttype, reportname, format,
					accountCode, screen);
			iTracker = 10.0;
			if (null == inputStream) {
				inputStream = dml.getFile(reportUrl, publisher, year, month, day, reporttype, reportname, format,
						accountCode.toLowerCase(), screen);
			}
			iTracker = 11.0;
			if (null == inputStream) {
				inputStream = dml.getFile(reportUrl, publisher, year, month, day, reporttype, reportname, format,
						accountCode.toUpperCase(), screen);
			}
			// LOG.info("input Stream "+ inputStream);
			//rmd.log("input Stream " + inputStream);// 1/05/2019
			if (null == reportname || reportname.length() < 1) {
				reportname = "reports";
			}
			if (null == format || format.length() < 1) {
				format = "csv";
			}
			if (null != screen && (screen.equalsIgnoreCase("journal") || screen.equalsIgnoreCase("book"))) {
				reportname = reportname + "_(#" + accountCode + ")";
			}
			if (reportname.contains("111")) {
				reportname = reportname.replace("111", "&");
			}

			String simpleDate = new SimpleDateFormat("yyyyMMdd_hhmm").format(new Date());
			response = Response.ok(inputStream);
			response.header(contentType, attachment + reportname + "_" + simpleDate + "." + format + "\"");
		} catch (Exception e) {
			rmd.exception("FileDownloadService : getFile : iTracker : " + iTracker + " : " + e.toString());
			response = Response.status(404).entity("Unable to Load File");
			// LOG.info("Exception "+e.getMessage());
		}
		return response.build();

	}

	@GET
	@Path("dynamic")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDynamicReportStream(@QueryParam("accountCode") String accountCode,
			@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("report") String report,
			@QueryParam("type") String type, @QueryParam("webmartid") String webmartid) {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		iTracker = 1.0;
		webmartID = rmd.getWebmartID();
		iTracker = 2.0;
		userCode = rmd.getUserCode();
		DynamicReportFormatImpl dri = new DynamicReportFormatImpl(rmd);
		ResponseBuilder response = null;
		InputStream inputStream = null;
		int webmart = 0;
		String searchTerm = "";
		String searchTerm1 = "";
		try {
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			iTracker = 3.0;
			if (null != webmartid && webmartid.length() > 1) {
				webmart = Integer.parseInt(webmartid);
			} else {
				webmart = webmartID;
			}
			iTracker = 4.0;
			if (accountCode.contains("[{")) {
				searchTerm = accountCode.substring(accountCode.indexOf("[{") + 2, accountCode.indexOf("}]"));
				if (accountCode.contains(" - ")) {
					searchTerm1 = accountCode.substring(0, accountCode.indexOf(" - "));
				} else {
					searchTerm1 = accountCode.substring(0, accountCode.indexOf("[{"));
				}
			} else {
				searchTerm = accountCode.trim();
			}
			iTracker = 5.0;
			inputStream = dri.getDynamicReportInputStream(webmart, searchTerm, report, from, to, type);
			iTracker = 6.0;
			if (null == inputStream) {
				if (searchTerm1.length() > 2) {
					inputStream = dri.getDynamicReportInputStream(webmart, searchTerm1, report, from, to, type);
				}
			}
			if (null != type && type.equalsIgnoreCase("xls")) {
				type = "xlsx";
			}
			// LOG.info("input Stream "+ inputStream);
			rmd.log("input Stream " + inputStream);
			String simpleDate = new SimpleDateFormat("yyyyMMdd_hhmm").format(new Date());
			response = Response.ok(inputStream);
			response.header(contentType, attachment + report + "_" + simpleDate + "." + type + "\"");
		} catch (Exception e) {
			rmd.exception(
					"FileDownloadService : getDynamicReportStream : iTracker : " + iTracker + " : " + e.toString());
			// LOG.info("Exception "+e.getMessage());
		}
		return response.build();

	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("counter5reports")
	public Response getDynamicReportJson(@QueryParam("accountCode") String accountCode,
			@QueryParam("fromdate") String fromdate, @QueryParam("todate") String todate,
			@QueryParam("report") String report, @QueryParam("metricType") String metrictype,
			@QueryParam("dataType") String datatype, @QueryParam("accessType") String accesstype,
			@QueryParam("accessMethod") String accessmethod, @QueryParam("yop") String yop,
			@QueryParam("type") String type, @QueryParam("showattribute") String showattribute) throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		
		iTracker = 1.0;
		webmartID = rmd.getWebmartID();
		iTracker = 2.0;
		userCode = rmd.getUserCode();
		ResponseBuilder response = null;
		Counter5ReportImpl dynamicReports = null;
		Counter5DTO c5dto = new Counter5DTO();
		String previewType = "download";
		try {
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			iTracker=3.0;
			c5dto.setReportCode(report);
			iTracker = 4.0;
			c5dto.setAccountID(accountCode);
			iTracker = 5.0;
			c5dto.setFromDate(fromdate);
			iTracker = 6.0;
			c5dto.setToDate(todate);
			iTracker = 7.0;
			c5dto.setMetricType(metrictype);
			iTracker = 8.0;
			c5dto.setDataType(datatype);
			iTracker = 9.0;
			c5dto.setAccessType(accesstype);
			iTracker = 10.0;
			c5dto.setAccessMethod(accessmethod);
			iTracker = 11.0;
			c5dto.setYop(yop);
			iTracker = 12.0;
			c5dto.setReportAttributes(showattribute);
			iTracker = 13.0;
			c5dto.setWebmartID(webmartID);
			iTracker = 14.0;
			c5dto.setInstitutionID(accountCode);
			iTracker = 15.0;
			c5dto.setFileType(type);
			// LOG.info(" In Counter 5 Report Detail : " + c5dto.toString());
			dynamicReports = new Counter5ReportImpl(rmd);
			iTracker = 0.0;
			InputStream inputStream = dynamicReports.getCounter5ReportsStream(c5dto, previewType);
			String simpleDate = new SimpleDateFormat("yyyyMMdd_hhmm").format(new Date());
			response = Response.ok(inputStream);
			iTracker = 0.0;
			response.header(contentType, attachment + report + "_" + simpleDate + "." + type + "\"");
			
			/*
			 *  Save user Activity in database
			 */
			rmd.setUserActivity("Download Counter Report : "+report);
			rmd.setResponceStatus(InsightConstant.SUCCESS);
			new UserInfo().saveInfo(rmd);
		} catch (Exception e) {
			rmd.exception("FileDownloadService : getDynamicReportJson : iTracker : " + iTracker + " : " + e.toString());
			response = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			/*
			 *  Save user Activity in database
			 */
			rmd.setUserActivity("Download Counter Report : "+report);
			rmd.setResponceStatus(InsightConstant.FAIL);
			new UserInfo().saveInfo(rmd);
		}
		return response.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("counter5pubreports")
	public Response getDynamicReportJson(@QueryParam("fromdate") String fromdate, @QueryParam("todate") String todate,
			@QueryParam("report") String report, @QueryParam("filetype") String type,
			@QueryParam("frequency") String frequency, 
			@QueryParam("bookJournalName") String bookJournalName,
			@QueryParam("selectedDate") String selectedDate)
			throws Exception {
		double iTracker = 0.0;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		iTracker = 1.0;
		webmartID = rmd.getWebmartID();
		String webmardCode=rmd.getWebmartCode();
		iTracker = 2.0;
		userCode = rmd.getUserCode();
		ResponseBuilder response = null;
		PublisherReport5Impl dynamicReports = null;
		Counter5DTO c5dto = new Counter5DTO();
		String download = "download";
		String filePath="";
		if(todate!=null){
		String date[]=todate.split("-");
		filePath = "insight-reports/counter5/"+webmardCode+"/"+date[1]+"/"+date[0]+"/addl_reports/Sales_and_Marketing.csv";
		}
		
		InputStream	inputStream;
    	iTracker = 2.0;
		try {
			if (webmartID == 0) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			iTracker = 3.0;
			c5dto.setReportCode(report);
			
			iTracker = 4.0;
			if (null == fromdate || fromdate.trim().equals("")) {
				c5dto.setFromDate(todate);
			} else {
				c5dto.setFromDate(fromdate);
			}
			c5dto.setToDate(todate);
			
			// Book / Journal Name For Choose Book and Journal
			if (bookJournalName != null) {
				c5dto.setBookJournalName(bookJournalName);
				if(selectedDate !=null && (selectedDate.contains("-")||selectedDate.contains("/"))){
					
					selectedDate = selectedDate.contains("/")?selectedDate.replaceAll("/", "-"):selectedDate;
					
					c5dto.setFromDate(selectedDate);
					c5dto.setToDate(selectedDate);
				}else{
					c5dto.setFromDate(rmd.getLiveMonth()+"-"+rmd.getLiveYear());
					c5dto.setToDate(rmd.getLiveMonth()+"-"+rmd.getLiveYear());
				}
			}
			c5dto.setWebmartID(webmartID);
			c5dto.setFileType(type);
			c5dto.setFrequency(frequency);
			// LOG.info(" In Counter 5 Report Detail : " + c5dto.toString());
			// rmd.log(" In Counter 5 Report Detail : " + c5dto.toString());
			dynamicReports = new PublisherReport5Impl(rmd);
			iTracker = 5.0;
			if (report.equalsIgnoreCase("Sales_and_Marketing") && type.equalsIgnoreCase("csv")) {
				S3 s3Client = null;
				s3Client = new S3("AKIAI3U4NGRDVAJSWRGQ", "s53ZCnX3b+g/9Hc/uIM/xf+5HErl5u35MVMyM+E2",
						S3.REGION.EU_WEST_2);
					inputStream=s3Client.download("www.mpsinsight.com", filePath);
			}
			else if(report.equalsIgnoreCase("SITE_OVERVIEW_All_INST") && type.equalsIgnoreCase("csv"))
			{
				
				SITE_OVERVIEW_All_INST soai=new SITE_OVERVIEW_All_INST(c5dto,rmd);
				inputStream = soai.site_overviewDownload();
			}
			else
			{
				inputStream = dynamicReports.getCounter5ReportsStream(c5dto, download);
			}
			String simpleDate = new SimpleDateFormat("yyyyMMdd_hhmm").format(new Date());
			response = Response.ok(inputStream);
			response.header(contentType, attachment + report + "_" + simpleDate + "." + type + "\"");
			
			// Save user Activity in database//
			rmd.setUserActivity("Download Publisher Report : "+report);
			rmd.setResponceStatus(InsightConstant.SUCCESS);
			new UserInfo().saveInfo(rmd);
			
		} catch (Exception e) {
			
			rmd.exception("FileDownloadService : getDynamicReportJsonOverloaded : iTracker : " + iTracker + " : "
					+ e.toString());
			response = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
			// Save user Activity in database//
			rmd.setUserActivity("Download Publisher Report : "+report);
			rmd.setResponceStatus(InsightConstant.FAIL);
			new UserInfo().saveInfo(rmd);
		}
		return response.build();
	}

	// added by satyam kumkar singh 07/02/2019 for counter5pubbookreports
	// download
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("counter5pubbookreports")
	public Response getDynamicReportJson(@QueryParam("title_id") String titile_id,
			@QueryParam("data_type") String data_type) throws Exception {
		double iTracker = 0.0;
		PublisherReport5Impl dynamicReports = null;
		rmd = (RequestMetaData) servletRequest.getAttribute("RMD");
		iTracker = 1.0;
		String webmartCode = rmd.getWebmartCode();
		iTracker = 2.0;
		ResponseBuilder response = null;
		PublisherSettings pubSettings = null;
		Counter5DTO c5dto = new Counter5DTO();
		try {
			if (webmartCode == null) {
				throw new MyException(InsightConstant.ERROR_SESSION);
			}
			// LOG.info(" In Counter 5 Report Detail : " + c5dto.toString());
			// rmd.log(" In Counter 5 Report Detail : " + c5dto.toString());
			dynamicReports = new PublisherReport5Impl(rmd);
			pubSettings = new PublisherSettings(rmd);
			// String year=pubSettings.getPublisherLiveYear(webmartCode);
			// String Month=pubSettings.getPublisherLiveMonth(webmartCode);
			String year = rmd.getLiveYear();
			String Month = rmd.getLiveMonth();
			iTracker = 5.0;
			InputStream inputStream = dynamicReports.getCounter5BookReportsStream(webmartCode, titile_id, data_type,
					Month, year);
			String simpleDate = new SimpleDateFormat("yyyyMMdd_hhmm").format(new Date());
			response = Response.ok(inputStream);
			response.header(contentType, attachment + "_" + simpleDate + "." + data_type + "\"");
		} catch (Exception e) {
			rmd.exception("FileDownloadService : getDynamicReportJsonOverloaded : iTracker : " + iTracker + " : "
					+ e.toString());
			response = Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage());
		}
		return response.build();
	}

}
