package com.mps.insight.c5.report;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import com.mps.aws.S3;
import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.c5.report.additional.EBOOK_ACCESS_CHAPTER;
import com.mps.insight.c5.report.additional.IpArtReqMonth;
import com.mps.insight.c5.report.additional.JR1_GOA;
import com.mps.insight.c5.report.additional.TR_NO_LIC_DEN;
import com.mps.insight.c5.report.ieee.OA_USAGE_REPORT;
import com.mps.insight.c5.report.ieee.SITE_OVERVIEW;
import com.mps.insight.c5.report.ieee.TR_ABS_REQ_EBOOK;
import com.mps.insight.c5.report.ieee.TR_ART_ABS_VIEW;
import com.mps.insight.c5.report.ieee.TR_ART_REQ_CONSORTIA_MEMBER;
import com.mps.insight.c5.report.ieee.TR_ART_REQ_PARTNER;
import com.mps.insight.c5.report.ieee.TR_ART_REQ_TITLE;
import com.mps.insight.c5.report.ieee.TR_ART_REQ_TYPE;
import com.mps.insight.c5.report.ieee.TR_CONF;
import com.mps.insight.c5.report.ieee.TR_CONF_YOP;
import com.mps.insight.c5.report.ieee.TR_EDU_COURSE;
import com.mps.insight.c5.report.ieee.TR_LIC_DEN_MONTH;
import com.mps.insight.c5.report.ieee.TR_MIT_EBOOK;
import com.mps.insight.c5.report.ieee.TR_STD;
import com.mps.insight.c5.report.ieee.TR_WILEY_EBOOK;
import com.mps.insight.c5.report.ieeecs.Proceedings;
import com.mps.insight.c5.report.library.DR;
import com.mps.insight.c5.report.library.DR_D1;
import com.mps.insight.c5.report.library.DR_D2;
import com.mps.insight.c5.report.library.IR;
import com.mps.insight.c5.report.library.IR_A1;
import com.mps.insight.c5.report.library.IR_M1;
import com.mps.insight.c5.report.library.MasterReportHeader;
import com.mps.insight.c5.report.library.PR;
import com.mps.insight.c5.report.library.PR_P1;
import com.mps.insight.c5.report.library.TR;
import com.mps.insight.c5.report.library.TR_B1;
import com.mps.insight.c5.report.library.TR_B2;
import com.mps.insight.c5.report.library.TR_B3;
import com.mps.insight.c5.report.library.TR_J1;
import com.mps.insight.c5.report.library.TR_J2;
import com.mps.insight.c5.report.library.TR_J3;
import com.mps.insight.c5.report.library.TR_J4;
import com.mps.insight.c5.report.publisher.Oa_report;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.EmailDTO;
import com.mps.insight.dto.OaDto;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;
import com.mps.insight.impl.ExcelFormatImpl;
import com.mps.insight.impl.MailSenderManager;
import com.mps.insight.product.Counter5ReportsDao;
import com.mps.insight.product.PublisherSettings;

public class Counter5ReportImpl {

	private RequestMetaData rmd = null;

	// private static final Logger LOG =
	// LoggerFactory.getLogger(Counter5ReportImpl.class);
	Counter5ReportsDao c5dao = null;
	Counter5ReportAttributImpl att = null;
	DynamicMonthCreater dmc = new DynamicMonthCreater();

	public Counter5ReportImpl(RequestMetaData rmd) {
		this.rmd = rmd;
	}

	public JsonArray getCounter5ReportsList(int webmartID) {
		c5dao = new Counter5ReportsDao(rmd);
		JsonArrayBuilder jarraybuild = Json.createArrayBuilder();
		JsonObjectBuilder jobuilder = Json.createObjectBuilder();
		PublisherSettings ps = null;
		// JsonArray job = null;
		try {
			ps = new PublisherSettings(rmd);
			String pubname = ps.getPublisherCode(webmartID);
			MyDataTable mdt = c5dao.getCounter5ReportsList(pubname, "LIBRARY");

			int rowCount = mdt.getRowCount();
			// int columnCount=mdt.getColumnCount();
			for (int rowNo = 1; rowNo <= rowCount; rowNo++) {

				jobuilder.add("key", mdt.getValue(rowNo, 1));
				jobuilder.add("value", mdt.getValue(rowNo, 2));
				jobuilder.add("desc", mdt.getValue(rowNo, 2));

				jarraybuild.add(jobuilder);
			}
			// job = mdt.getJsonWithKeyValue();

		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		return jarraybuild.build();
	}

	public JsonArray getCounter5ReportsList(int webmartID,String Institution_ID, String reportSection) {
		int rowCount = -2;
		int columnCount= -2;
		MyDataTable mdt = null;
		PublisherSettings ps = null;
		
		JsonArrayBuilder jarraybuild = Json.createArrayBuilder();
		JsonObjectBuilder jobuilder = Json.createObjectBuilder();
		ArrayList<Integer> dateList = new ArrayList<>();
		boolean isValidReport = false;
		try {
			c5dao = new Counter5ReportsDao(rmd);
			
			ps = new PublisherSettings(rmd);
			String pubname = ps.getPublisherCode(webmartID);
			int liveYear = ps.getLiveYear(webmartID);
			int pmonth = ps.getPubLiveMonth(webmartID, liveYear);
			String pubmonth = pmonth<10 ? "0"+pmonth : ""+pmonth;
			String pubMonth = "M_"+liveYear+pubmonth;
			mdt = c5dao.getCounter5ReportsList(pubname, "LIBRARY", Institution_ID, pubMonth);
			rowCount = mdt.getRowCount();
			columnCount=mdt.getColumnCount();
			String fromDate = "";
			String toDate = "";
			String columnName = "";
			dateList = new ArrayList<>();
			String userType=rmd.getUserType();
			
			//iterating each report
			for (int rowNo = 1; rowNo <= rowCount; rowNo++) {
				isValidReport = false;
				
				//iterating columns for report
				for(int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
					columnName = mdt.getColumnName(columnIndex).toUpperCase();
					//iterating on column name for from and to date, looking for possitve value
					if(columnName.toUpperCase().contains("M_20")){
						int value = -2; 
						try{
							value = Integer.parseInt(mdt.getValue(rowNo, columnIndex));
						}catch (Exception e) {
							value = -33;
							//logger
						}
						
						int dateFomrat = Integer.parseInt(columnName.split("_")[1].trim());
						
						//check for positive value = 1 for valid /report month
						if(userType.trim().equalsIgnoreCase("Publisher")  && reportSection.equalsIgnoreCase("account") && value == 1){
							//valid report for Publisher > 0 states month reports are in QA, roll back or live
							isValidReport = true;
							dateList.add(dateFomrat);
						}else if(userType.trim().equalsIgnoreCase("Publisher") && reportSection.equalsIgnoreCase("processing") && value > 0){
							isValidReport = true;
							dateList.add(dateFomrat);
						}else if( userType.trim().equalsIgnoreCase("LibraryClients") && value == 1){
						
							//valid report for Library client 1 states month reports are live
							isValidReport = true;
							dateList.add(dateFomrat);
						}else{
							//nothing to do not valid report
						}
					}
				}//end column loop
				
				//check for valid report and entry i json builder
				if(isValidReport){
					jobuilder.add("institution_id", mdt.getValue(rowNo, "Institution_id"));
					jobuilder.add("key", mdt.getValue(rowNo, "report_key"));
					jobuilder.add("value", mdt.getValue(rowNo, "report_name"));
					jobuilder.add("desc", mdt.getValue(rowNo, "report_description"));
					jobuilder.add("type", mdt.getValue(rowNo, "report_type"));
					jobuilder.add("frequency", mdt.getValue(rowNo, "report_frequency"));
					
					Collections.sort(dateList, (a, b) -> a.compareTo(b));
					fromDate = dateList.get(0).toString();
					toDate = dateList.get(dateList.size() - 1).toString();
					//int dateFormat = Integer.parseInt(toDate.substring(4, 6));
					//reformatting from date & to date
					String day = "01";
					fromDate = fromDate.substring(0, 4) + "-" + fromDate.substring(4, 6) + "-" + day;
					//manipulate day as per month and leap year
					//day = "30";
					int noOfDays=30;
					Calendar monthStart = new GregorianCalendar(Integer.parseInt(toDate.substring(0, 4)), (Integer.parseInt(toDate.substring(4, 6)) - 1), 1);
					noOfDays = monthStart.getActualMaximum(Calendar.DAY_OF_MONTH);
					toDate = toDate.substring(0, 4) + "-" + toDate.substring(4, 6) + "-" + noOfDays;
					
					jobuilder.add("fromdate", fromDate);
					jobuilder.add("todate", toDate);
					
					jarraybuild.add(jobuilder);
				}
				
				
			}//end row loop
			
		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		return jarraybuild.build();
	}

	// get the range for library live month
	public String getDateRange(int webmartID, String Institution_ID) {
		int rowCount = -2;
		int columnCount = -2;
		MyDataTable mdt = null;
		PublisherSettings ps = null;
		Set<Integer> dateList = new HashSet<>();
		String columnName = "";
		StringBuilder range=new StringBuilder();
		try {
			c5dao = new Counter5ReportsDao(rmd);
			ps = new PublisherSettings(rmd);
			String pubname = ps.getPublisherCode(webmartID);
			int liveYear = ps.getLiveYear(webmartID);
			int pmonth = ps.getPubLiveMonth(webmartID, liveYear);
			String pubmonth = pmonth < 10 ? "0" + pmonth : "" + pmonth;
			String pubMonth = "M_" + liveYear + pubmonth;
			mdt = c5dao.getCounter5ReportsList(pubname, "LIBRARY", Institution_ID, pubMonth);
			rowCount = mdt.getRowCount();
			columnCount = mdt.getColumnCount();
			

			for (int rowNo = 1; rowNo <= rowCount; rowNo++) {
				for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
					columnName = mdt.getColumnName(columnIndex).toUpperCase();
					// iterating on column name for from and to date, looking for possitve value
					if (columnName.toUpperCase().contains("M_20")) {
						int value = -2;
						try {
							value = Integer.parseInt(mdt.getValue(rowNo, columnIndex));
						} catch (Exception e) {
							value = -33;
						}

						// check for possitive value = 1 for valid /report month
						if (value == 1) { // valid report
							int dateFomrat = Integer.parseInt(columnName.split("_")[1].trim());
							dateList.add(dateFomrat);
						}
					}
				} // end column loop for monthly status of report
			} // end row loop for all reports

			// check for valid report and entry i json builder

			// sort the dateSet
			 ArrayList<Integer> dateLists = new ArrayList<>(dateList);
			Collections.sort(dateLists, (a, b) -> a.compareTo(b));
			
			for (int i = 0; i < dateLists.size(); i++) {
				range.append(dateLists.get(i)+",");
				
			}
			

		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		
		return range.toString();
	}

	public JsonObject getCounter5ReportsAttributeDetail(int webmartID, String reportID) {
		c5dao = new Counter5ReportsDao(rmd);
		JsonArrayBuilder jabMetricType = Json.createArrayBuilder();
		JsonArrayBuilder jabDataType = Json.createArrayBuilder();
		JsonArrayBuilder jabAccessType = Json.createArrayBuilder();
		JsonArrayBuilder jabAccessMethod = Json.createArrayBuilder();
		JsonObjectBuilder jobFinal = Json.createObjectBuilder();
		JsonObjectBuilder jobTypeBuilder = null;
		try {
			MyDataTable mdt = c5dao.getReportMetricTypeDetail(webmartID, reportID);

			int rowCount = mdt.getRowCount();

			for (int i = 1; i <= rowCount; i++) {
				String type = mdt.getValue(i, "type");
				if (type.equalsIgnoreCase("metricType")) {
					// Add Json object
					jobTypeBuilder = Json.createObjectBuilder();
					jobTypeBuilder.add("Key", mdt.getValue(i, "value"));
					jobTypeBuilder.add("value", mdt.getValue(i, "value"));
					// Add json Object into array
					jabMetricType.add(jobTypeBuilder);

				} else if (type.equalsIgnoreCase("dataType")) {
					jobTypeBuilder = Json.createObjectBuilder();
					jobTypeBuilder.add("Key", mdt.getValue(i, "value"));
					jobTypeBuilder.add("value", mdt.getValue(i, "value"));

					jabDataType.add(jobTypeBuilder);
				} else if (type.equalsIgnoreCase("accessType")) {
					jobTypeBuilder = Json.createObjectBuilder();
					jobTypeBuilder.add("Key", mdt.getValue(i, "value"));
					jobTypeBuilder.add("value", mdt.getValue(i, "value"));

					jabAccessType.add(jobTypeBuilder);
				} else if (type.equalsIgnoreCase("accessMethod")) {
					jobTypeBuilder = Json.createObjectBuilder();
					jobTypeBuilder.add("Key", mdt.getValue(i, "value"));
					jobTypeBuilder.add("value", mdt.getValue(i, "value"));

					jabAccessMethod.add(jobTypeBuilder);
				} else {

				}
			}
			jobFinal.add("metricType", jabMetricType);
			jobFinal.add("dataType", jabDataType);
			jobFinal.add("accessType", jabAccessType);
			jobFinal.add("accessMethod", jabAccessMethod);

		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		return jobFinal.build();
	}

	public MyDataTable getCounter5Reports(Counter5DTO dto, String previewType) {
		c5dao = new Counter5ReportsDao(rmd);
		MyDataTable mdt = null;
		String query = null;
		try {
			String reportCode = getReportCode(dto);
			dto.setReportCodeAlias(dto.getReportCode());
			dto.setReportCode(reportCode);

			query = getQuery(dto, previewType);
			rmd.log("Executing Query : " + query);
			mdt = c5dao.getDynamicReportJson(dto.getWebmartID(), query);
				
			if(mdt.getRowCount()<1){
				rmd.error("No Result found in MDT, for query : "+query);
			}
		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		return mdt;
	}

	public String getCounter5ReportsCsv(Counter5DTO dto, String previewType) {
		c5dao = new Counter5ReportsDao(rmd);
		String query = null;
		StringBuilder sb =null;
		try {
			String reportCode = getReportCode(dto);
			dto.setReportCodeAlias(dto.getReportCode());
			dto.setReportCode(reportCode);

			query = getQuery(dto, previewType);
			rmd.log("Executing Query : " + query);
			if(rmd.getWebmartCode().equalsIgnoreCase("IEEE") && dto.getReportCode().equalsIgnoreCase("IR")){
				sb = c5dao.getIRRecordData(dto, query);
			}
				
		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		return sb.toString();
	}

	private String getReportCode(Counter5DTO dto) {
		String reportCode = "";
		try {
			Counter5ReportsDao reportDao = new Counter5ReportsDao(rmd);
			MyDataTable mdt = reportDao.getReportDetailByReportAlias(dto.getPublisher(), dto.getReportCode());
			reportCode = mdt.getValue(1, "code");
		} catch (Exception e) {
			rmd.exception(e.toString());
		}
		return reportCode;
	}

	public String getQuery(Counter5DTO dto, String previewType) {
		String query = "";

		try {
			switch (dto.getReportCode()){

			case "TR_J1" :
				TR_J1 trJ1 = new TR_J1(dto, previewType, rmd);
				query = trJ1.getQuery();
				break;
			case "TR_J2" :
				TR_J2 trJ2 = new TR_J2(dto, previewType, rmd);
				query = trJ2.getQuery();
				break;
			case "TR_J1_GOA" :
				JR1_GOA jr3Goa = new JR1_GOA(dto, previewType, rmd);
				query = jr3Goa.getQuery();
				break;
			case "TR_J3" :
				TR_J3 trJ3 = new TR_J3(dto, previewType, rmd);
				query = trJ3.getQuery();
				break;
			case "TR_J4" :
				TR_J4 trJ4 = new TR_J4(dto, previewType, rmd);
				query = trJ4.getQuery();
				break;
			case "TR_NO_LIC_DEN":
				TR_NO_LIC_DEN trNoLicDen = new TR_NO_LIC_DEN(dto, previewType, rmd);
				query = trNoLicDen.getQuery();
				break;
			case "IR":
				IR ir = new IR(dto, previewType, rmd);
				query = ir.getQuery();
				break;
			case "IR_A1":
				IR_A1 irA1 = new IR_A1(dto, previewType, rmd);
				query = irA1.getQuery();
				break;
			case "IR_M1":
				IR_M1 irM1 = new IR_M1(dto, previewType, rmd);
				query = irM1.getQuery();
				break;
			case "PR":
				PR pr = new PR(dto, previewType, rmd);
				query = pr.getQuery();
				break;
			case "PR_P1":
				PR_P1 prp1 = new PR_P1(dto, previewType, rmd);
				query = prp1.getQuery();
				break;
			case "TR":
				TR tr = new TR(dto, previewType, rmd);
				query = tr.getQuery();
				break;
			case "TR_B1":
				TR_B1 tr_b1 = new TR_B1(dto, previewType, rmd);
				query = tr_b1.getQuery();
				break;
			case "TR_B2":
				TR_B2 tr_b2 = new TR_B2(dto, previewType, rmd);
				query = tr_b2.getQuery();
				break;
			case "TR_B3":
				TR_B3 tr_b3 = new TR_B3(dto, previewType, rmd);
				query = tr_b3.getQuery();
				break;
			case "DR_D1":
				DR_D1 dr_d1 = new DR_D1(dto, previewType, rmd);
				query = dr_d1.getQuery();
				break;
			case "DR_D2":
				DR_D2 dr_d2 = new DR_D2(dto, previewType, rmd);
				query = dr_d2.getQuery();
				break;
			case "DR":
				DR dr = new DR(dto, previewType, rmd);
				query = dr.getQuery();
				break;
			case "EBOOK_ACCESS_CHAPTER":
				EBOOK_ACCESS_CHAPTER ebook_access_chapter = new EBOOK_ACCESS_CHAPTER(dto, previewType, rmd);
				query = ebook_access_chapter.getQuery();
				break;
			case "IP_ART_REQ_MONTH":
				IpArtReqMonth ip_art_req_month = new IpArtReqMonth(dto, previewType, rmd);
				query = ip_art_req_month.getQuery();
				break;
			case "TR_CONF":
				TR_CONF tr_conf = new TR_CONF(dto, previewType, rmd);
				query = tr_conf.getQuery();
				break;
			case "TR_CONF_YOP":
				TR_CONF_YOP tr_conf4 = new TR_CONF_YOP(dto, previewType, rmd);
				query = tr_conf4.getQuery();
				break;
			case "TR_WILEY_EBOOK":
				TR_WILEY_EBOOK tr_wibcrmi = new TR_WILEY_EBOOK(dto, previewType, rmd);
				query = tr_wibcrmi.getQuery();
				break;
			case "TR_MIT_EBOOK":
				TR_MIT_EBOOK tr_mitEbook = new TR_MIT_EBOOK(dto, previewType, rmd);
				query = tr_mitEbook.getQuery();
				break;
			case "TR_EDU_COURSE":
				TR_EDU_COURSE edu_cour = new TR_EDU_COURSE(dto, previewType, rmd);
				query = edu_cour.getQuery();
				break;
			case "TR_STD":
				TR_STD standard = new TR_STD(dto, previewType, rmd);
				query = standard.getQuery();
				break;
			case "JR1_GOA":
				JR1_GOA jr_goa = new JR1_GOA(dto, previewType, rmd);
				query = jr_goa.getQuery();
				break;
			case "TR_ART_REQ_CONSORTIA_MEMBER":
				TR_ART_REQ_CONSORTIA_MEMBER art_req_member = new TR_ART_REQ_CONSORTIA_MEMBER(dto, previewType, rmd);
				query = art_req_member.getQuery();
				break;
			case "TR_ART_REQ_TITLE":
				TR_ART_REQ_TITLE art_req_title = new TR_ART_REQ_TITLE(dto, previewType, rmd);
				query = art_req_title.getQuery();
				break;
			case "TR_ART_REQ_TYPE": 
				TR_ART_REQ_TYPE art_req_type = new TR_ART_REQ_TYPE(dto, previewType, rmd);
				query = art_req_type.getQuery();
				break;
			case "TR_ABS_REQ_EBOOK":
				TR_ABS_REQ_EBOOK tr_abs_req_ebook = new TR_ABS_REQ_EBOOK(dto, previewType, rmd);
				query = tr_abs_req_ebook.getQuery();
				break;
			case "SITE_OVERVIEW":
				SITE_OVERVIEW tr_abs_req = new SITE_OVERVIEW(dto, previewType, rmd);
				query = tr_abs_req.getQuery();
				break;
			case "TR_ART_ABS_VIEW":
				TR_ART_ABS_VIEW tr_art_abs_view = new TR_ART_ABS_VIEW(dto, previewType, rmd);
				query = tr_art_abs_view.getQuery();
				break;
			case "TR_ART_REQ_PARTNER":
				TR_ART_REQ_PARTNER tr_art_req_partner = new TR_ART_REQ_PARTNER(dto, previewType, rmd);
				query = tr_art_req_partner.getQuery();
				break;
			case "TR_LIC_DEN_MONTH":
				TR_LIC_DEN_MONTH tr_lic_den_month = new TR_LIC_DEN_MONTH(dto, previewType, rmd);
				query = tr_lic_den_month.getQuery();
				break;
			case "OA_USAGE":
				OA_USAGE_REPORT oa_usage_report = new OA_USAGE_REPORT(dto, previewType, rmd);
				query = oa_usage_report.getQuery();
				break;
			case "PROCEEDING":
				Proceedings proceedings = new Proceedings(dto, previewType, rmd);
				query = proceedings.getQuery();
				break;
			default:
				query = getDynamicQuery(dto, previewType);
			}		
		} catch (Exception e) {
			rmd.error(e.toString());
		}
		return query;
	}

	public String getDynamicQuery(Counter5DTO dto, String previewType) throws Exception {
		att = new Counter5ReportAttributImpl(rmd);
		String fixedtitle = att.getReportsTitle(dto.getReportCode());
		String customtitle = att.getReportsTitleCustom(dto);
		String[] fromarr = dto.getFromDate().split("-");
		String[] toarr = dto.getToDate().split("-");
		String tablename = TableMapper.TABALE.get("tr_master_table");
		String monthquery = null;
		String monthtotalquery = "";
		String monthtotalqueryAs = "";
		String monthSumtotalquery = "";
		String dataorder = att.getOrderAndGroupQuery(dto.getReportCode());

		String wherecondition = att.createWhereCondition(dto);
		String totalAs = "Reporting_Period_Total";

		try {
			tablename = att.getReportsTable(dto.getReportCode());

			if ("IP_ART_REQ_MONTH".equalsIgnoreCase(dto.getReportCode())
					|| "TR_J1_GOA".equalsIgnoreCase(dto.getReportCode())) {
				monthquery = dmc.createSumMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
				monthSumtotalquery = dmc.createTotalMonthSumQueryC5(Integer.parseInt(toarr[0]),
						Integer.parseInt(toarr[1]), Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));

				monthSumtotalquery = monthquery + " " + monthSumtotalquery;
				monthtotalquery = dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), totalAs);

			} else {

				if ("ART_REQ_TITLE".equalsIgnoreCase(dto.getReportCode())) {
					totalAs = "YTD Total";
				}
				monthquery = dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
				monthtotalquery = dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
						Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), totalAs);

				monthtotalqueryAs = monthtotalquery + "as `" + totalAs + "`,";
			}

			StringBuilder finalquery = new StringBuilder();
			finalquery.append("select " + fixedtitle).append(customtitle);
			if ("ART_REQ_TITLE".equalsIgnoreCase(dto.getReportCode())) {
				finalquery.append(monthquery)
						.append(monthtotalqueryAs.substring(0, monthtotalqueryAs.toString().lastIndexOf(",")));
			} else {
				finalquery.append(monthtotalqueryAs)
						.append(monthquery.substring(0, monthquery.toString().lastIndexOf(",")));

			}
			finalquery.append(" from " + tablename + " where ");

			finalquery.append(wherecondition).append("AND " + monthtotalquery + " > 0 ").append(dataorder);
			if (previewType.equalsIgnoreCase("preview")) {
				finalquery.append(" limit 500 ");
			} else {

			}

			// rmd.log("Query : "+finalquery.toString());
			return finalquery.toString();

		} catch (Exception e) {
			rmd.log(e.toString());
			throw e;
		}

	}

	public JsonObject getCounter5ReportsJson(Counter5DTO dto, String previewType) {
		att = new Counter5ReportAttributImpl(rmd);
		MyDataTable mdt = null;
		JsonArray data = null;
		JsonArray header = null;
		JsonObjectBuilder finaljson = Json.createObjectBuilder();
		HashMap<String, String> headerdetail = null;
		PublisherSettings ps = null;
		try {
			ps = new PublisherSettings(rmd);
			dto.setPublisher(ps.getPublisherCode(dto.getWebmartID()));
			
			if(dto.getReportCode().equalsIgnoreCase("OA_USAGE")){
				mdt=getOaUsageReport(dto);
				data = mdt.getJsonKeyValue();
				 
			}else{
				mdt = getCounter5Reports(dto, previewType);
				data = mdt.getJsonKeyValue();
			}
			
			
			// Generate Header with Filteres for Master Reports
			String ReportCode = dto.getReportCode();
			if ("DR".equalsIgnoreCase(ReportCode) || "PR".equalsIgnoreCase(ReportCode)
					|| "TR".equalsIgnoreCase(ReportCode) || "IR".equalsIgnoreCase(ReportCode)) {
				MasterReportHeader headerNew = new MasterReportHeader(dto, previewType, rmd);
				headerdetail = headerNew.getHeaderTemplate();
				header = att.geHeaderJson(headerdetail);
			} else {

				headerdetail = att.getCounter5ReportHeader(dto);
				header = att.geHeaderJson(headerdetail);
			}

			finaljson.add("header", header);
			finaljson.add("data", data);

		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		return finaljson.build();
	}



	public InputStream getCounter5ReportsStream(Counter5DTO dto, String previewType) {
		att = new Counter5ReportAttributImpl(rmd);
		InputStream io = null;
		MyDataTable mdt = null;
		String finalfile = "";
		String datafile = "";
		String headerData = "";
		PublisherSettings ps = null;
		ExcelFormatImpl excelimpl = new ExcelFormatImpl(rmd);
		double iTracker = 0.0;
		try {
			iTracker = 1.0;
			ps = new PublisherSettings(rmd);
			dto.setPublisher(ps.getPublisherCode(dto.getWebmartID()));

			String metricType = null;
			String accessType = null;
			String accessMethod = null;
			String dataType = null;;
			String yop= null;
			
			try{
				metricType = dto.getMetricType();
				accessType = dto.getAccessType();
				accessMethod = dto.getAccessMethod();
				dataType = dto.getDataType();
				yop = dto.getYop();
				
				try{metricType =  URLDecoder.decode(metricType, "UTF-8");}catch(Exception e){}
				try{accessType =  URLDecoder.decode(accessType, "UTF-8");}catch(Exception e){}
				try{accessMethod =  URLDecoder.decode(accessMethod, "UTF-8");}catch(Exception e){}
				try{dataType =  URLDecoder.decode(dataType, "UTF-8");}catch(Exception e){}
				try{yop =  URLDecoder.decode(yop, "UTF-8");}catch(Exception e){}
			
			//
			iTracker = 2.0;
			if(rmd.getWebmartCode().equalsIgnoreCase("IEEE") && dto.getReportCode().equalsIgnoreCase("IR") &&
					(metricType == null||metricType.equalsIgnoreCase("null")|| metricType.equalsIgnoreCase("") || metricType.equalsIgnoreCase(" "))
					&& (accessType == null||accessType.equalsIgnoreCase("null")|| accessType.equalsIgnoreCase("") || accessType.equalsIgnoreCase(" "))
					&& (accessMethod == null||accessMethod.equalsIgnoreCase("null")|| accessMethod.equalsIgnoreCase("") || accessMethod.equalsIgnoreCase(" "))
					&& (dataType == null||dataType.equalsIgnoreCase("null")|| dataType.equalsIgnoreCase("") || dataType.equalsIgnoreCase(" "))
					&& accessType == null||accessType.equalsIgnoreCase("null")|| accessType.equalsIgnoreCase("") || accessType.equalsIgnoreCase(" ")){
				Long totalRow=new Counter5ReportsDao(rmd).getTotalRowCounts(dto);
				if(totalRow >1000000){
					//Map metric type 
							S3 s3Client = null;
							s3Client = new S3("AKIAI3U4NGRDVAJSWRGQ", "s53ZCnX3b+g/9Hc/uIM/xf+5HErl5u35MVMyM+E2",
							S3.REGION.EU_WEST_2);
							InputStream inputStream = s3Client.download("www.mpsinsight.com", "insight-reports/counter5/ieee/2019/"+dto.getToDate().substring(0,2)+"/ir_"+dto.getInstitutionID()+".csv");
							if(totalRow >1000000 && dto.getFileType().trim().equalsIgnoreCase("tsv")){
								String tsv="tsv is not available for this report.";
								return new ByteArrayInputStream(tsv.getBytes());
							}else if(totalRow >1000000 && ("xls".equalsIgnoreCase(dto.getFileType().trim())
									|| "xlsx".equalsIgnoreCase(dto.getFileType().trim())))
							{
								String xls="xls is not available for this report.";
								return new ByteArrayInputStream(xls.getBytes());
							}
							
							/*StringBuilder stringBuilder = new StringBuilder();
							String line = null;
						
							try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {	
								while ((line = bufferedReader.readLine()) != null) {
									stringBuilder.append(line+"\n");
								}
							}catch(Exception e){
								MyLogger.error("unable to rade input stream "+e.toString());
							}
							s3Client.destroy();*/
							return inputStream;
							}
							
			
			/*if(rmd.getWebmartCode().equalsIgnoreCase("IEEE") && dto.getReportCode().equalsIgnoreCase("IR")){
				String datafilepath = getCounter5ReportsCsv(dto, previewType);
				BufferedReader br;
				
				br = new BufferedReader(new FileReader(new File(datafilepath)));
				
				String line="";
				while((line=br.readLine())!=null){
					datafile.concat(line);
					datafile.concat("\n");
				}
				br.close();
			*/
			}
			}catch(Exception e){}
			
			if("OA_USAGE".equalsIgnoreCase(dto.getReportCode())){
				mdt = getOaUsageReport(dto);
			}else{
				mdt = getCounter5Reports(dto, previewType);
			}
				
				if (mdt == null) {
					throw new Exception("NULL Mdt for report  ");
				}
				if(rmd.getWebmartCode().equalsIgnoreCase("IEEE")&& "TR".equalsIgnoreCase(dto.getReportCode())){
					mdt = validateData(mdt, dto.getReportCode());
				}else if(rmd.getWebmartCode().equalsIgnoreCase("IEEE")&& "IR_A1".equalsIgnoreCase(dto.getReportCode())){
					mdt = validateData(mdt, dto.getReportCode());
				}
				
				
				rmd.log(mdt.getRowCount() + " : records found for report");
			
				iTracker = 3.0;
				if (dto.getFileType().trim().equalsIgnoreCase("csv")) {
					iTracker = 4.0;
					datafile = mdt.getCsvDataWithQuote(",");
				} else if (dto.getFileType().trim().equalsIgnoreCase("tsv")) {
					iTracker = 5.0;
					datafile = mdt.getTsvData();
				} else if ("xls".equalsIgnoreCase(dto.getFileType().trim())
						|| "xlsx".equalsIgnoreCase(dto.getFileType().trim())) {
					iTracker = 6.0;
					HashMap<String, String> c5 = att.getExcelReportHeader(dto);
					io = excelimpl.getExcelReports(mdt, c5);
				} else {
					iTracker = 7.0;
				}

			
		//}
			iTracker = 8.0;
			// getting herder from repot header DTO.... not required in case of XLSfile
			if ("xls".equalsIgnoreCase(dto.getFileType()) || "xlsx".equalsIgnoreCase(dto.getFileType())) {
				// header not required in case of XLS files
				iTracker = 9.0;
			} else {
				iTracker = 10.0;
				headerData = getReportHeaderForStream(dto);
				finalfile = headerData + datafile;
				iTracker = 10.1;
				io = new ByteArrayInputStream(finalfile.getBytes());
				iTracker = 10.2;
			}

		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + iTracker + " : " + e.getMessage() + " : "
					+ dto.toString());
		}
		return io;
	}

	public String reportsSendToMail(int webmartID, String accountCode, String report, String from, String to,
			String fileformat, EmailDTO edto, String previewType) {

		MailSenderManager mailsender = new MailSenderManager();
		List<BodyPart> attach = new ArrayList<>();
		BodyPart attachedPart = null;
		InputStream io = null;
		DataSource ds = null;

		Counter5DTO dto = new Counter5DTO();
		String[] reportType;
		double iTracker = 0.0;
		try {

			iTracker = 1.0;
			reportType = fileformat.split(",");

			iTracker = 2.0;
			dto.setWebmartID(webmartID);
			dto.setAccountID(accountCode);
			dto.setInstitutionID(accountCode);
			dto.setReportCode(report);
			dto.setFromDate(from);
			dto.setToDate(to);

			iTracker = 3.0;
			// loop for multiple format type, attachment
			for (String format : reportType) {
				attachedPart = new MimeBodyPart();
				iTracker = 3.1;
				dto.setFileType(format);

				iTracker = 3.2;
				// getting report io stream
				io = getCounter5ReportsStream(dto, previewType);
				iTracker = 3.3;

				ds = new ByteArrayDataSource(io, "application/octet-stream");
				iTracker = 3.4;
				attachedPart.setFileName(report + "." + format);
				iTracker = 3.5;
				attachedPart.setDataHandler(new DataHandler(ds));
				iTracker = 3.6;
				attach.add(attachedPart);
			}
			iTracker = 4.0;
			edto.setAttachment(attach);

			iTracker = 4.1;
			// check for blank message body
			if (edto.getMessage() == null || edto.getMessage().trim().equalsIgnoreCase("")) {
				edto.setMessage("Hi " + edto.getFirstName() + " \n reports are attached here \n Regard \n MPSINSIGHT");
			}

			iTracker = 5.0;
			// check for blank user email
			if (null == edto.getUseremail() || edto.getUseremail().length() < 2) {
				edto.setUseremail("support@mpsinsight.com");
			}

			mailsender.sendMailWithAttachment(edto);

		} catch (Exception e) {
			rmd.log(e.toString() + " : " + iTracker);
			return InsightConstant.ERROR;
		}
		return InsightConstant.SUCCESS;
	}

	public String getReportHeaderForStream(Counter5DTO dto) {
		att = new Counter5ReportAttributImpl(rmd);
		HashMap<String, String> c5 = att.getCounter5ReportHeader(dto);
		String seperator = "";
		if (dto.getFileType().equalsIgnoreCase("csv")) {
			seperator = ",";
		} else if (dto.getFileType().equalsIgnoreCase("tsv")) {
			seperator = "	";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Report_Name" + seperator + "" + c5.get("Report_Name") + "\n");
		sb.append("Report_ID" + seperator + "" + c5.get("Report_ID") + "\n");
		sb.append("Release" + seperator + "" + c5.get("Release") + "\n");
		sb.append("Institution_Name" + seperator + "" + c5.get("Institution_Name") + "\n");
		sb.append("Institution_ID" + seperator + "" + c5.get("Institution_ID") + "\n");
		sb.append("Metric_Types" + seperator + "" + c5.get("Metric_Types") + "\n");
		sb.append("Report_Filters" + seperator + "" + c5.get("Report_Filters") + "\n");
		sb.append("Report_Attributes" + seperator + "" + c5.get("Report_Attributes") + "\n");

		sb.append("Exceptions" + seperator + "" + c5.get("Exceptions") + "\n");

		sb.append("Reporting_Period" + seperator + "" + c5.get("Reporting_Period") + "\n");
		sb.append("Created" + seperator + "" + c5.get("Created") + "\n");
		sb.append("Created_By" + seperator + "" + c5.get("Created_By") + "\n\n");

		return sb.toString();
	}

	private MyDataTable getOaUsageReport(Counter5DTO dto){
		MyDataTable mdt_oa = new MyDataTable("OA_USAGE_Report");
		try {
			dto.setReportCodeAlias(dto.getReportCode());
			dto.setReportCode(dto.getReportCode());
			Connection con= rmd.getInsightDao().getSqlConnection();
			 Oa_report oa=new Oa_report();
			 ArrayList<OaDto> oa_report_records= oa.getReport(dto,con);
			 mdt_oa.addColumn("title_id", "");
			 mdt_oa.addColumn("Title Name", "");
			 mdt_oa.addColumn("Data_Type", "");
			 mdt_oa.addColumn("Total Usage", "");
			 mdt_oa.addColumn("OA Usage", "");
			 mdt_oa.addColumn("Subscribed OA Usage", "");
			 
			 for (OaDto oaDto : oa_report_records) {
				 mdt_oa.addRow();
				 int rowIndex = mdt_oa.getRowCount();
				 mdt_oa.updateData(rowIndex, "title_id", oaDto.getTitleId());
				 mdt_oa.updateData(rowIndex, "Title Name", oaDto.getTitleName());
				 mdt_oa.updateData(rowIndex, "Data_Type",  oaDto.getDataType());
				 mdt_oa.updateData(rowIndex, "Total Usage", ""+oaDto.getTotalUses());
				 mdt_oa.updateData(rowIndex, "OA Usage", ""+oaDto.getOaUses());
				 mdt_oa.updateData(rowIndex, "Subscribed OA Usage", ""+oaDto.getSuscribOa() );
			}
			 
			/* JsonArrayBuilder jsonRowArray = null;
		        jsonRowArray = Json.createArrayBuilder();
		        JsonObjectBuilder jsonTable = Json.createObjectBuilder();
			 for (OaDto b : oa_report_records) {
		        	jsonTable.add("title_id", b.getTitleId());
		        	jsonTable.add("Title_Name", b.getTitleName());
		        	jsonTable.add("data_type",  b.getDataType());
		        	jsonTable.add("Total_Usage", b.getTotalUses());
		        	jsonTable.add("OA Usage", b.getOaUses());
		        	jsonTable.add("Subscribed_OA_Usage", b.getSuscribOa() );
		        	jsonRowArray.add(jsonTable);
		        }
			 data=jsonRowArray.build();*/
			// data = mdt_oa.getJsonKeyValue();
		} catch (Exception e) {
			// TODO: handle exception
		}
		 
		 return mdt_oa;
	
	}
	
	private MyDataTable validateData(MyDataTable mdt, String reportCode) {
		MyDataTable trMdt=new MyDataTable(mdt.getTableName());
		try {
			
			if(mdt.getRowCount()>0){
				int rowCount = mdt.getRowCount();
				int colCount = mdt.getColumnCount();
				
				for (int i = 1; i <= colCount; i++) {
					
					trMdt.addColumn(mdt.getAbsoluteColumnName(i), "", "egnoreCase");
				}
				
				
				for (int rowIndex = 1; rowIndex <=rowCount; rowIndex++) {
					trMdt.addRow();
					for (int colIndex =1; colIndex <=colCount; colIndex++) {
						String value=mdt.getValue(rowIndex, colIndex);
						String colName = mdt.getColumnName(colIndex);
						if(colName.equalsIgnoreCase("ISBN")||colName.equalsIgnoreCase("ISSN")){
							value = validateISBN(value);
						}else if(colName.equalsIgnoreCase("URI")||colName.equalsIgnoreCase("Parent_URI")){
							value = validateURI(value);
						}else if(colName.equalsIgnoreCase("Publication_Date")&& "IR_A1".equalsIgnoreCase(reportCode)){
							value=valideateDate(value, InsightConstant.IR_A1_PUBLICATION_DATE_INPUT_FORMATE, InsightConstant.IR_A1_PUBLICATION_DATE_OUTPUT_FORMATE);
						}else if(colName.equalsIgnoreCase("Authors")){
							value = validateAuthors(value);
						}
						
						trMdt.updateData(rowIndex, colIndex, value);
					}
				}
			}
			
		} catch (Exception e) {
			return mdt;
		}
		return trMdt;
	}

	private String validateAuthors(String value) {
		String validAuthor="";
		
		try {
			if(value.contains(" ;")||value.contains(";")){
				validAuthor = value.replaceAll(" ;", "; ");
			}
		} catch (Exception e) {
			validAuthor =  value;
		}
		return validAuthor;
	}

	private String validateURI(String uri) {
		StringBuilder validURI =new StringBuilder();
		try {
			// value should be in valid format URI 'http://test.org/'

			if(!uri.contains("://")&&!uri.equalsIgnoreCase("")){
				validURI.append("http://"+uri);
			}else if(uri.contains("htttp")){
				validURI.append(uri.replace("htttp", "http"));
			}else{
				validURI.append(uri);
			}
			
		} catch (Exception e) {
			return uri;
		}
		return validURI.toString();
	}

	private String validateISBN(String value) {
		StringBuilder validISBN =new StringBuilder();
		try {
			// value should be in valid format ISBN '978-11-19247-11-1'

			if(!value.contains("-")){
				char[] chars = value.toCharArray();
				for (int i = 0; i < chars.length; i++) {
					if(i==3||i==5||i==10||i==12){
						validISBN.append("-"+chars[i]);
						
					}else{
						validISBN.append(chars[i]);
					}
					
				}
			}else{
				validISBN.append(value);
			}
			
		} catch (Exception e) {
			return value;
		}
		return validISBN.toString();
	}
	
	/*
	private String validateDtaType(String dataType) {
		String validDataType ="";
		try {
			// value should be in valid format URI 'http://test.org/'

			if("Conference".equalsIgnoreCase(dataType)||"Courses".equalsIgnoreCase(dataType)||"Standard".equalsIgnoreCase(dataType)){
				validDataType ="Journal";
			}else{
				validDataType = dataType;
			}
			
		} catch (Exception e) {
			return dataType;
		}
		return validDataType;
	}
	
	*/
	
	private String valideateDate(String date, String inputDateFormate, String outputDateFormate){
		String validDate="";
		
		try {
			if(date.equalsIgnoreCase("")){
				date="01/01/1970";
			}
			Date parsedDate = new SimpleDateFormat(inputDateFormate).parse(date);
			DateFormat dateFormat = new SimpleDateFormat(outputDateFormate);
			validDate = dateFormat.format(parsedDate);
			
		
		} catch (Exception e) {
			validDate = date;
		}
		return validDate;
	}
}
