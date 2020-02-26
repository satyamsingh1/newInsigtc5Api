package com.mps.insight.c5.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.mps.insight.c4.report.DynamicDayMonthCreater;
import com.mps.insight.c5.report.publisher.ArticleAccessByJournal;
import com.mps.insight.c5.report.publisher.ArticleRequestByMonth;
import com.mps.insight.c5.report.publisher.BookUsageAllInstitution;
import com.mps.insight.c5.report.publisher.BookUsageAllUser;
import com.mps.insight.c5.report.publisher.ConferencesUsageByMonth;
import com.mps.insight.c5.report.publisher.ConsortiaSummaryReport;
import com.mps.insight.c5.report.publisher.DBUsageAllInstAllUser;
import com.mps.insight.c5.report.publisher.EbookChapterRequestByMonth;
import com.mps.insight.c5.report.publisher.EbookOutput;
import com.mps.insight.c5.report.publisher.EbookOutputDetail;
import com.mps.insight.c5.report.publisher.EducationalCoursesAllInstitution;
import com.mps.insight.c5.report.publisher.FachDataReport;
import com.mps.insight.c5.report.publisher.HighestAccessIP;
import com.mps.insight.c5.report.publisher.HighestAccessInstitution;
import com.mps.insight.c5.report.publisher.IeeeMCBookUsageInstitution;
import com.mps.insight.c5.report.publisher.IeeeMCBookUsageUser;
import com.mps.insight.c5.report.publisher.JournalByTitlePubYear;
import com.mps.insight.c5.report.publisher.JournalUsageAllInstitution;
import com.mps.insight.c5.report.publisher.JournalUsageAllUser;
import com.mps.insight.c5.report.publisher.MemberDownloadReport;
import com.mps.insight.c5.report.publisher.MitBookUsageInstitution;
import com.mps.insight.c5.report.publisher.MitBookUsageUser;
import com.mps.insight.c5.report.publisher.MitEbookChapterRequestByMonth;
import com.mps.insight.c5.report.publisher.MonthlyDatabaseDownloadAccess;
import com.mps.insight.c5.report.publisher.MonthlyDownloadAccessCustomer;
import com.mps.insight.c5.report.publisher.MonthlyJournalDownloadAccessByArticle;
import com.mps.insight.c5.report.publisher.ReferralReports;
import com.mps.insight.c5.report.publisher.SITE_OVERVIEW_All_INST;
import com.mps.insight.c5.report.publisher.SalesAndMarketing;
import com.mps.insight.c5.report.publisher.SectionRequest;
import com.mps.insight.c5.report.publisher.SiteSummaryReport;
import com.mps.insight.c5.report.publisher.StandardsUsageByMonthAllInstitutionalUser;
import com.mps.insight.c5.report.publisher.TopAccessBook;
import com.mps.insight.c5.report.publisher.TopAccessJournal;
import com.mps.insight.c5.report.publisher.TopInternalSearchRun;
import com.mps.insight.c5.report.publisher.TotalSearchRun;
import com.mps.insight.c5.report.publisher.WileyBookChapterRequestByMonth;
import com.mps.insight.c5.report.publisher.WileyBookUsageInstitution;
import com.mps.insight.c5.report.publisher.WileyBookUsageUser;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.product.Counter5ReportsDao;

public class PublisherReport5AttributImpl {

	Counter5ReportsDao c5dao = null;
	private RequestMetaData rmd = null;

	public PublisherReport5AttributImpl(RequestMetaData rmd) {
		this.rmd = rmd;
	}

	public JsonArray geHeaderJson(HashMap<String, String> report) {

		JsonObjectBuilder header = Json.createObjectBuilder();
		JsonArrayBuilder arr = Json.createArrayBuilder();
		for (Map.Entry<String, String> entry : report.entrySet()) {
			header.add(InsightConstant.KEY, entry.getKey());
			header.add(InsightConstant.VALUE, entry.getValue());
			arr.add(header);
		}

		return arr.build();
	}

	public MyDataTable getPublisherMDT(Counter5DTO dto, String download) {
		MyDataTable mdt = null;
		try {
			
			switch (dto.getReportCode()) {
			case "MONTH_JOUR_DOWN_ACCESS_CUST":
				MonthlyDownloadAccessCustomer mondown = new MonthlyDownloadAccessCustomer(dto, rmd);
				mdt = mondown.getReport(download);
				break;
			case "Referrals_Report":
				ReferralReports refrepo = new ReferralReports(dto, rmd);
				mdt = refrepo.getReport(download);
				break;
			case "SITE_OVERVIEW_All_INST":
				SITE_OVERVIEW_All_INST soai = new SITE_OVERVIEW_All_INST(dto, rmd);
				mdt = soai.getReport(download);
				break;
			case "EDU_COURSES_ALL_INST":
				EducationalCoursesAllInstitution eduCou = new EducationalCoursesAllInstitution(dto, rmd);
				mdt = eduCou.getReport(download);
				break;
			case "MONTH_HIGH_ACCESS_INST_BOOK":
				HighestAccessInstitution highaccinstbook = new HighestAccessInstitution(dto, rmd);
				mdt = highaccinstbook.getReport(download);
				break;
			case "ARTICLE_REQUEST_BY_MONTH":
				ArticleRequestByMonth arbm = new ArticleRequestByMonth(dto, rmd);
				mdt = arbm.getReport(download);
				break;
			case "CONF_USAGE_MONTH":
				ConferencesUsageByMonth conf = new ConferencesUsageByMonth(dto, rmd);
				mdt = conf.getReport(download);
				break;
			case "JOUR_USAGE_ALL_INST":
				JournalUsageAllInstitution jo = new JournalUsageAllInstitution(dto, rmd);
				mdt = jo.getReport(download);
				break;
			case "JOUR_USAGE_ALL_USER":
				JournalUsageAllUser journalUser = new JournalUsageAllUser(dto, rmd);
				mdt = journalUser.getReport(download);
				break;
			
			case "DB_USAGE_ALL_INST":// added by satyam for ASM 
				DBUsageAllInstAllUser dBUsageAllInst = new DBUsageAllInstAllUser(dto, rmd);
				mdt = dBUsageAllInst.getReport(download, "Inst");
				break;
			case "DB_USAGE_ALL_USER":// added by satyam for ASM 
				DBUsageAllInstAllUser dBUsageAllUser = new DBUsageAllInstAllUser(dto, rmd);
				mdt = dBUsageAllUser.getReport(download, "User");
				break;
				
			case "DB_USAGE_ALL_INST_ALL_USER":
				DBUsageAllInstAllUser dBUsageAllInstAllUser = new DBUsageAllInstAllUser(dto, rmd);
				mdt = dBUsageAllInstAllUser.getReport(download, "All_Inst_All_User");
				break;
				
			case "MONTH_DATABASE_DOWN_ACCESS":
				MonthlyDatabaseDownloadAccess monthdata = new MonthlyDatabaseDownloadAccess(dto, rmd);
				mdt = monthdata.getReport(download);
				break;
			case "MIT_EBOOK_CHAPTER":
				MitEbookChapterRequestByMonth mitEbookChapterRequestByMonth = new MitEbookChapterRequestByMonth(dto,
						rmd);
				mdt = mitEbookChapterRequestByMonth.getReport(download);
				break;
			case "WILEY_EBOOK_CHAPTER":
				WileyBookChapterRequestByMonth willeyebookchapter = new WileyBookChapterRequestByMonth(dto, rmd);
				mdt = willeyebookchapter.getReport(download);
				break;
			case "EBOOK_ACCESS_CHAPTER":
				EbookChapterRequestByMonth ebookaccesschapter = new EbookChapterRequestByMonth(dto, rmd);
				mdt = ebookaccesschapter.getReport(download);
				break;
			case "BOOK_USAGE_ALL_INST":
				BookUsageAllInstitution bookusageinst = new BookUsageAllInstitution(dto, rmd);
				mdt = bookusageinst.getReport(download);
				break;
			case "BOOK_USAGE_ALL_USER":
				BookUsageAllUser bookusageuser = new BookUsageAllUser(dto, rmd);
				mdt = bookusageuser.getReport(download);
				break;
			case "MONTH_JOUR_DOWN_ACCESS_ART":
				MonthlyJournalDownloadAccessByArticle monthacc = new MonthlyJournalDownloadAccessByArticle(dto, rmd);
				mdt = monthacc.getReport(download);
				break;
			case "ART_ACCESS_BY_JOUR":
				ArticleAccessByJournal artaccessbyjour = new ArticleAccessByJournal(dto, rmd);
				mdt = artaccessbyjour.getReport(download);
				break;
			case "CONSORTIA_SUMMARY":
				ConsortiaSummaryReport conso = new ConsortiaSummaryReport(dto, rmd);
				mdt = conso.getReport(download);
				break;
			case "EBOOK_OUTPUT":// saurabh
				EbookOutput ebookOutput = new EbookOutput(dto, rmd);
				mdt = ebookOutput.getReport(download);
				break;
			case "EBOOK_OUTPUT_DETAIL":
				EbookOutputDetail ebookOutputDetail = new EbookOutputDetail(dto, rmd);
				mdt = ebookOutputDetail.getReport(download);
				break;
			case "TOP_ACCESS_BOOK":
				TopAccessBook topAccessBook = new TopAccessBook(dto, rmd);
				mdt = topAccessBook.getReport(download);
				break;
			case "TOP_ACCESS_JOUR":
				TopAccessJournal topAccessJournal = new TopAccessJournal(dto, rmd);
				mdt = topAccessJournal.getReport(download);
				break;
			case "WILEY_BOOK_USAGE_INST":
				WileyBookUsageInstitution wbui = new WileyBookUsageInstitution(dto, rmd);
				mdt = wbui.getReport(download);
				break;
			case "WILEY_BOOK_USAGE_USER":
				WileyBookUsageUser wbuu = new WileyBookUsageUser(dto, rmd);
				mdt = wbuu.getReport(download);
				break;
			case "Section_Request":
				SectionRequest sectionrequest = new SectionRequest(dto, rmd);
				mdt = sectionrequest.getReport(download);
				break;
			case "STN_USAGE":
				StandardsUsageByMonthAllInstitutionalUser standardsUsage = new StandardsUsageByMonthAllInstitutionalUser(
						dto, rmd);
				mdt = standardsUsage.getReport(download);
				break;
			case "MIT_BOOK_USAGE_INST":
				MitBookUsageInstitution mbui = new MitBookUsageInstitution(dto, rmd);
				mdt = mbui.getReport(download);
				break;
			case "MIT_BOOK_USAGE_USER":
				MitBookUsageUser mbuu = new MitBookUsageUser(dto, rmd);
				mdt = mbuu.getReport(download);
				break;
			case "IEEE_M_C_BOOK_USAGE_INST":
				IeeeMCBookUsageInstitution imcbui = new IeeeMCBookUsageInstitution(dto, rmd);
				mdt = imcbui.getReport(download);
				break;
			case "IEEE_M_C_BOOK_USAGE_USER":
				IeeeMCBookUsageUser imcbuu = new IeeeMCBookUsageUser(dto, rmd);
				mdt = imcbuu.getReport(download);
				break;
			case "Sales_and_Marketing":
				SalesAndMarketing salesandmarkting = new SalesAndMarketing(dto, rmd);
				mdt = salesandmarkting.getReport(download);
				break;
			case "JOUR_TITLE_PUB_YEAR":
				JournalByTitlePubYear jour = new JournalByTitlePubYear(dto, rmd);
				mdt = jour.getReport(download);
				break;
			case "HIGH_ACCESS_INST_DAILY":
				HighestAccessInstitution highestinst = new HighestAccessInstitution(dto, rmd);
				mdt = highestinst.getReport(download);
				break;
			case "HIGH_ACCESS_INST_MONTHLY":
				HighestAccessInstitution highestinstm = new HighestAccessInstitution(dto, rmd);
				mdt = highestinstm.getReport(download);
				break;
			case "SITE_SUMMARY_DAILY":
				SiteSummaryReport ssr = new SiteSummaryReport(dto, rmd);
				mdt = ssr.getReport(download);
				break;
			case "SITE_SUMMARY_WEEKLY":
				SiteSummaryReport ssw = new SiteSummaryReport(dto, rmd);
				mdt = ssw.getReport(download);
				break;
			case "SITE_SUMMARY_MONTHLY":
				SiteSummaryReport ssm = new SiteSummaryReport(dto, rmd);
				mdt = ssm.getReport(download);
				break;
			case "TOTAL_SEARCH_RUN":
				TotalSearchRun tsr = new TotalSearchRun(dto, rmd);
				mdt = tsr.getReport(download);
				break;
			case "TOP_INTERNAL_SEARCH":
				TopInternalSearchRun tisr = new TopInternalSearchRun(dto, rmd);
				mdt = tisr.getReport(download);
				break;
			case "Fach_Data_report":
				FachDataReport fdr = new FachDataReport(dto, rmd);
				mdt = fdr.getReport(download);
				break;
			case "Member_Download_report":
				MemberDownloadReport mdr = new MemberDownloadReport(dto, rmd);
				mdt = mdr.getReport(download);
				break;
			case "HIGH_IP_USAGE":
				HighestAccessIP hai = new HighestAccessIP(dto, rmd);
				mdt = hai.getReport(download);
				break;
			default:
				mdt = null;
			}
		} catch (Exception e) {
			rmd.exception("Exception in getPublisherMDT " + e.getMessage());
			;
		}
		return mdt;
	}

	public HashMap<String, String> getCounter5ReportHeader(Counter5DTO c5dto) {

		HashMap<String, String> c5 = new LinkedHashMap<String, String>();
		DynamicDayMonthCreater ddc = new DynamicDayMonthCreater();
		try {
			c5dao = new Counter5ReportsDao(rmd);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			String strDate = formatter.format(new Date());

			MyDataTable reportName = c5dao.getReportDetailByCode(c5dto.getPublisher(), c5dto.getReportCode());
			StringBuilder tempMetricType = new StringBuilder();
			StringBuilder reportfilter = new StringBuilder();
			StringBuilder reportAttribute = new StringBuilder();

			String Reporting_Period = "";
			String[] fromdate = null;
			String[] todate = null;
			int day = 0;
			try {
				fromdate = c5dto.getFromDate().split("-");
				todate = c5dto.getToDate().split("-");
				day = ddc.getNumberofDay(Integer.parseInt(todate[0]), Integer.parseInt(todate[1]));

			} catch (Exception e) {
			}

			if (null != c5dto.getFrequency() && c5dto.getFrequency().contains("DAILY")) {
				Reporting_Period = "Begin_Date=" + fromdate[2] + "-" + fromdate[1] + "-" + fromdate[0] + "; End_Date="
						+ todate[2] + "-" + todate[1] + "-" + todate[0];
			} else if (c5dto.getReportCode().equalsIgnoreCase("TOP_ACCESS_BOOK")
					|| c5dto.getReportCode().equalsIgnoreCase("TOP_ACCESS_JOUR")) {
				if(c5dto.getFromDate() !=null || c5dto.getToDate()!=null){				
					Reporting_Period = "Begin_Date=" + InsightConstant.MONTH_ARRAY[Integer.parseInt(todate[0])]
							+ "-" + todate[1] + "; End_Date="
							+ InsightConstant.MONTH_ARRAY[Integer.parseInt(todate[0])] + "-" + todate[1];
				}else{
					Reporting_Period = "Begin_Date=" + InsightConstant.MONTH_ARRAY[Integer.parseInt(rmd.getLiveMonth())]
							+ "-" + rmd.getLiveYear() + "; End_Date="
							+ InsightConstant.MONTH_ARRAY[Integer.parseInt(rmd.getLiveMonth())] + "-" + rmd.getLiveYear();
				}

			} else if(null != c5dto.getFrequency() && c5dto.getFrequency().contains("WEEKLY")) {

				Reporting_Period = "Begin_Date=" + fromdate[2] + "-" + fromdate[1] +"-" +fromdate[0]+"; End_Date=" + todate[2] + "-"
						+ todate[1] + "-" + todate[0];
			}
			else {

				Reporting_Period = "Begin_Date=" + fromdate[1] + "-" + fromdate[0] + "-01; End_Date=" + todate[1] + "-"
						+ todate[0] + "-" + day;
			}

			switch (c5dto.getReportCode()) {
			case "JOUR_TITLE_PUB_YEAR":
				tempMetricType.append("Total_Item_Requests;");
				break;
			case "ARTICLE_REQUEST_BY_MONTH":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "SITE_OVERVIEW_All_INST":
				tempMetricType.append("");
				break;
			case "CONF_USAGE_MONTH":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "EDU_COURSES_ALL_INST":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "HIGH_ACCESS_INST_DAILY":
				if(rmd.getWebmartCode().toString().equalsIgnoreCase("asm")){
				tempMetricType.append("Total_Item_Investigations;");
				}else
				tempMetricType.append("Total_Item_Requests;");
				break;
			case "HIGH_ACCESS_INST_MONTHLY":
				if(rmd.getWebmartCode().toString().equalsIgnoreCase("asm")){
					tempMetricType.append("Total_Item_Investigations;");
					}else
					tempMetricType.append("Total_Item_Requests;");
				break;
			case "JOUR_USAGE_ALL_INST":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "DB_USAGE_ALL_INST":// added by satyam for ASM 
				tempMetricType.append("Searches_Regular; Total_Item_Investigations; Total_Item_Requests; Unique_Item_Investigations; Unique_Title_Investigations;");
				break;
			case "MIT_BOOK_USAGE_USER":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "MIT_BOOK_USAGE_INST":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "STN_USAGE":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "WILEY_BOOK_USAGE_INST":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "WILEY_BOOK_USAGE_USER":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "WILEY_EBOOK_CHAPTER":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "IEEE_M_C_BOOK_USAGE_INST":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "MIT_EBOOK_CHAPTER":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case "EBOOK_OUTPUT":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case"EBOOK_OUTPUT_DETAIL":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests; Total_Item_Investigations; Unique_Item_Investigations;");
				break;
			case "MONTH_DATABASE_DOWN_ACCESS":
				tempMetricType.append("Searches_Regular; Total_Item_Requests; Unique_Item_Requests; Total_Item_Investigations; Unique_Title_Investigations;");
				break;
			case "MONTH_JOUR_DOWN_ACCESS_CUST":
				tempMetricType.append("Total_Item_Requests; Total_Item_Investigations; Unique_Item_Requests; Unique_Item_Investigations; Total_Item_Investigations;");
				break;
			case "Section_Request":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case"IEEE_M_C_BOOK_USAGE_USER":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case"JOUR_USAGE_ALL_USER":
				tempMetricType.append("Total_Item_Requests; Unique_Item_Requests;");
				break;
			case"DB_USAGE_ALL_USER"://added by satyam for ASM 
				tempMetricType.append("Searches_Regular; Total_Item_Investigations; Total_Item_Requests; Unique_Item_Investigations; Unique_Title_Investigations;");
				break;
			case"OA_USAGE":
				tempMetricType.append("Total_Item_Requests;");
				break;
			default:
				break;
			}
			
			c5.put(InsightConstant.REPORT_NAME, reportName.getValue(1, 1));
			c5.put(InsightConstant.REPORT_ID, c5dto.getReportCode());
			c5.put(InsightConstant.RELEASE, "5");
			c5.put(InsightConstant.INSTITUTION_NAME, c5dto.getPublisher());
			c5.put(InsightConstant.INSTITUTION_ID, c5dto.getWebmartID() + "");
			if (null == c5dto.getMetricType() || c5dto.getMetricType().length() < 2) {
				c5.put(InsightConstant.METRIC_TYPES, tempMetricType.toString());
			} else {
				c5.put(InsightConstant.METRIC_TYPES, c5dto.getMetricType());
			}
			c5.put(InsightConstant.REPORT_FILTERS, reportfilter.toString());
			c5.put(InsightConstant.REPORT_ATTRIBUTES, reportAttribute.toString());
			c5.put(InsightConstant.EXCEPTIONS, "");
			c5.put(InsightConstant.REPORTING_PERIOD, Reporting_Period);
			c5.put(InsightConstant.CREATED, strDate);
			c5.put(InsightConstant.CREATED_BY, "MPS for " + rmd.getWebmartCode().toUpperCase());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return c5;
	}

	public HashMap<String, String> getExcelReportHeader(Counter5DTO dto) {

		HashMap<String, String> c5 = getCounter5ReportHeader(dto);
		HashMap<String, String> detail = new HashMap<String, String>();
		detail.put("rowcount", "13");

		detail.put("R1C1", InsightConstant.REPORT_NAME);
		detail.put("R1C2", c5.get(InsightConstant.REPORT_NAME));
		// 2nd row
		detail.put("R2C1", InsightConstant.REPORT_ID);
		detail.put("R2C2", c5.get(InsightConstant.REPORT_ID));

		// 3rd row
		detail.put("R3C1", InsightConstant.RELEASE);
		detail.put("R3C2", c5.get(InsightConstant.RELEASE));

		// 4th row
		detail.put("R4C1", InsightConstant.INSTITUTION_NAME);
		detail.put("R4C2", c5.get(InsightConstant.INSTITUTION_NAME));
		// 5th row
		detail.put("R5C1", InsightConstant.INSTITUTION_ID);
		detail.put("R5C2", c5.get(InsightConstant.INSTITUTION_ID));
		// 6th row
		detail.put("R6C1", InsightConstant.METRIC_TYPES);
		detail.put("R6C2", c5.get(InsightConstant.METRIC_TYPES));
		// 7th row
		detail.put("R7C1", InsightConstant.REPORT_FILTERS);
		detail.put("R7C2", c5.get(InsightConstant.REPORT_FILTERS));
		// 8th row
		detail.put("R8C1", InsightConstant.REPORT_ATTRIBUTES);
		detail.put("R8C2", c5.get(InsightConstant.REPORT_ATTRIBUTES));
		// 9th row
		detail.put("R9C1", InsightConstant.EXCEPTIONS);
		detail.put("R9C2", c5.get(InsightConstant.EXCEPTIONS));
		// 10th row
		detail.put("R10C1", InsightConstant.REPORTING_PERIOD);
		detail.put("R10C2", c5.get(InsightConstant.REPORTING_PERIOD));
		// 11th row
		detail.put("R11C1", InsightConstant.CREATED);
		detail.put("R11C2", c5.get(InsightConstant.CREATED));
		// 12th row
		detail.put("R12C1", InsightConstant.CREATED_BY);
		detail.put("R12C2", c5.get(InsightConstant.CREATED_BY));
		// 13th row
		detail.put("R13C1", "");
		detail.put("R13C2", c5.get(""));

		return detail;
	}

}
