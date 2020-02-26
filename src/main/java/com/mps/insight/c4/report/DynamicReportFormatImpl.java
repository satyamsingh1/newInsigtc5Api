package com.mps.insight.c4.report;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.json.JsonArray;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import com.mps.insight.c4.report.library.BR2Report;
import com.mps.insight.c4.report.library.BR3Report;
import com.mps.insight.c4.report.library.Conferences;
import com.mps.insight.c4.report.library.ConsortiaMember;
import com.mps.insight.c4.report.library.DB1Report;
import com.mps.insight.c4.report.library.DB2Report;
import com.mps.insight.c4.report.library.EBookChapter;
import com.mps.insight.c4.report.library.JR1GOAReport;
import com.mps.insight.c4.report.library.JR1Report;
import com.mps.insight.c4.report.library.JR2Report;
import com.mps.insight.c4.report.library.JR3Report;
import com.mps.insight.c4.report.library.JR4Report;
import com.mps.insight.c4.report.library.PR1Report;
import com.mps.insight.c4.report.library.Standard;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.AccountDTO;
import com.mps.insight.dto.EmailDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.impl.ExcelFormatImpl;
import com.mps.insight.impl.MailSenderManager;
import com.mps.insight.product.Account;
import com.mps.insight.product.DynamicReports;

public class DynamicReportFormatImpl {

	//private static final Logger log = LoggerFactory.getLogger(DynamicReportFormatImpl.class);
	private RequestMetaData rmd= null; 
	public String journal="Journal";
	public String title="Title";
	
	public DynamicReportFormatImpl(RequestMetaData rmd){
		this.rmd = rmd;
	}
	public JsonArray getDynamicReportsJson(int webmartID, String accountCode, String report, String from, String to) {
		MyDataTable mdt = null;
		String query = getQuery(accountCode, report, from, to);
		JsonArray json = null;
		AccountDTO adto = null;
		try {
			Account account = new Account(rmd);
			adto = account.getAccountByCode(accountCode, webmartID);
			
			if (null==adto.getAccountCode()) {
				return null;
			}
			mdt = getDynamicReports(webmartID, query);
			if (mdt.getRowCount() < 1) {
				return null;
			}
			
			json = mdt.getJsonKeyValue();
		} catch (Exception e) {
			rmd.exception("error in getDynamicReportsJson ");
		}
		//rmd.log(json.toString());
		return json;
	}

	public InputStream getDynamicReportInputStream(int webmartID, String accountCode, String report, String from,
			String to, String fileformat) {
		MyDataTable mdt = null;
		InputStream io = null;
		String file = "";
		String query = getQuery(accountCode, report, from, to);
		ExcelFormatImpl excelimpl=new ExcelFormatImpl(rmd);
		mdt = getDynamicReports(webmartID, query);
		if (mdt.getRowCount() < 1) {
			return io;
		}
		String headerDetail = "";
		HashMap<String,String> headerDetailmap = getFileHeaderDetailMap(webmartID, accountCode, report, from, to, fileformat);
		try {
			if (InsightConstant.CSV.equalsIgnoreCase(fileformat)) {
				file = mdt.getCsvDataWithQuote(",");
			} else if (InsightConstant.TSV.equalsIgnoreCase(fileformat)) {
				file = mdt.getTsvDataWithQuote();
			} else if (InsightConstant.XLS.equalsIgnoreCase(fileformat) || InsightConstant.XLSX.equalsIgnoreCase(fileformat)) {
				io = excelimpl.getExcelReports(mdt, headerDetailmap);
			}
			headerDetail = getFileHeaderDetail(headerDetailmap, report);
			file = headerDetail + file;
			if (InsightConstant.XLS.equalsIgnoreCase(fileformat) || InsightConstant.XLSX.equalsIgnoreCase(fileformat)) {
				//file = mdt.getCsvDataWithQuote(",");
			}else{
			io = new ByteArrayInputStream(file.getBytes());
			}
		} catch (Exception e) {
			rmd.exception("Exception While Converting String to InputStream in getDynamicReportInputStream : "
					+ e.getMessage());
		}
		return io;
	}
	
	public String reportsSendToMail(int webmartID, String accountCode, String report, String from,
			String to, String fileformat,EmailDTO edto){
		String [] reportType=fileformat.split(",");
		BodyPart attachedPart = null;
		InputStream io=null;
		DataSource ds =null;
		List<BodyPart> attach=new ArrayList<>();
		MailSenderManager mailsender=new MailSenderManager();
		try{
		for (String format : reportType) {
			attachedPart = new MimeBodyPart();
			io=getDynamicReportInputStream(webmartID, accountCode, report, from, to, format);
			ds = new ByteArrayDataSource(io, "application/octet-stream");
			attachedPart.setFileName(report+"."+format);
			attachedPart.setDataHandler(new DataHandler(ds));
			attach.add(attachedPart);
		}
		edto.setAttachment(attach);
		//edto.setFirstName("Kumar");
		//edto.setSubject(edto.getSubject() );
		//edto.setReciever("kumarlav08010@gmail.com");
		edto.setMessage("Hi "+edto.getFirstName()+" \n reports are attached here \n Regard \n MPSINSIGHT");
		if(null==edto.getUseremail() || edto.getUseremail().length()<2){
			edto.setUseremail("support@mpsinsight.com");
		}
		mailsender.sendMailWithAttachment(edto);
		
		}catch(Exception e){
		return InsightConstant.ERROR;	
		}
		return InsightConstant.SUCCESS;
	}

	public String getFileHeaderDetail(HashMap<String,String> detail,String report) {
		StringBuilder sb = new StringBuilder();
		String fileformat=detail.get("fileformat");
		try {
			String seperator = "";
			if (null != fileformat && fileformat.equalsIgnoreCase(InsightConstant.CSV)) {
				seperator = ",";
			} else if (null != fileformat && fileformat.equalsIgnoreCase(InsightConstant.TSV)) {
				seperator = "\t";
			}
			int rowcount=Integer.parseInt(detail.get("rowcount").toString());
			for(int i=1;i<=rowcount;i++){
				sb.append(detail.get("R"+i+"C1")).append(seperator).append(detail.get("R"+i+"C2")).append("\n");
			}
			
		} catch (Exception e) {
			rmd.exception("Exception in Report Header String convert : "+e.getMessage());
		}
		return sb.toString();
	}
	
	public HashMap<String,String> getFileHeaderDetailMap(int webmartID, String accountCode, String report, String from, String to,
			String fileformat) {
		HashMap<String,String> detail=new HashMap<>();
		Counter4Header dc=new Counter4Header(rmd);
		try {
			detail=dc.getC4HeaderDetailMap(webmartID, accountCode, report, from, to, fileformat);
		} catch (Exception e) {
			rmd.exception("Exception in header detail Counter 4 : "+e.getMessage());
		}
		return detail;
	}

	public MyDataTable getDynamicReports(int webmartID, String query) {
		MyDataTable mdt = null;
		DynamicReports dr = new DynamicReports(rmd);
		mdt = dr.getDynamicReportJson(webmartID, query);
		return mdt;
	}

	
	public String getQuery(String accountCode, String report, String from, String to){
		
		String query = "";
		if(report.equalsIgnoreCase(InsightConstant.BR2)){
			BR2Report br2rpt = new BR2Report(accountCode, report, from, to);
			query = br2rpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.DB1)){
			DB1Report db1rpt =  new DB1Report(accountCode, report, from, to);
			query = db1rpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.DB2)){
			DB2Report db2rpt =  new DB2Report(accountCode, report, from, to);
			query = db2rpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.JR1)){
			JR1Report jr1rpt =  new JR1Report(accountCode, report, from, to);
			query = jr1rpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.JR3)){
			JR3Report jr3rpt =  new JR3Report(accountCode, report, from, to);
			query = jr3rpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.JR4)){
			JR4Report jr4rpt =  new JR4Report(accountCode, report, from, to);
			query = jr4rpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.PR1)){
			PR1Report pr1rpt =  new PR1Report(accountCode, report, from, to);
			query = pr1rpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.JR1GOA)){
			JR1GOAReport pr1rpt =  new JR1GOAReport(accountCode, report, from, to);
			query = pr1rpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.BR3)){
			BR3Report br3rpt =  new BR3Report(accountCode, report, from, to);
			query = br3rpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.JR2)){
			JR2Report jr2rpt =  new JR2Report(accountCode, report, from, to);
			query = jr2rpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.CONFERENCES)){
			Conferences cnfrpt =  new Conferences(accountCode, report, from, to);
			query = cnfrpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.EBOOK_CHAPTER)){
			EBookChapter ebcrpt =  new EBookChapter(accountCode, report, from, to);
			query = ebcrpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.STANDARDS)){
			Standard stdrpt =  new Standard(accountCode, report, from, to);
			query = stdrpt.getQuery();
		}else if(report.equalsIgnoreCase(InsightConstant.ARTICLE_REQUEST_BY_CONSORTIA_MEMBER_REPORT)){
			ConsortiaMember cmrpt =  new ConsortiaMember(accountCode, report, from, to);
			query = cmrpt.getQuery();
		}else{
			query = getSqlQuery(accountCode, report, from, to);
		}
		
		return query;
		
	}
	
	public String getSqlQuery(String accountCode, String report, String from, String to) {
		String tableName = getTableName(report);
		String headerQuery = getQueryHeader(report);
		String query1 = "";
		String query2 = "";
		String dynamicMonthlist = null;
		String dynamicMonthListTotal = null;
		List<String> tempQuery = new ArrayList<>();
		DynamicMonthCreater monthCreator = new DynamicMonthCreater();
		if (!checkReportTableFormatJR1(report)) {
			dynamicMonthlist = monthCreator.getMonthQuery(report, from, to);
			dynamicMonthListTotal = monthCreator.getHeaderTotalQuery(report, from, to);
		} else {

			rmd.log("dynamicMonthlist JR1  : " + dynamicMonthlist);
			tempQuery = monthCreator.getJR1TypeTotalMonthList(report, from, to);
			rmd.log("JR1 Query on 0 : " + tempQuery.get(0));
			rmd.log("JR1 Query on 1 : " + tempQuery.get(1));
			dynamicMonthlist = tempQuery.get(0);
			dynamicMonthListTotal = tempQuery.get(1);
		}
		String totalTitle = getTotalHeader(report);
		String temptotal = "";
		if (report.equalsIgnoreCase(InsightConstant.JR4) || report.equalsIgnoreCase(InsightConstant.EBOOK_CHAPTER)
				|| report.equalsIgnoreCase(InsightConstant.EBOOK_CHAPTER_MIT)) {
			temptotal = totalTitle.replaceAll("'", "");
		} else {
			temptotal = totalTitle;
		}
		if (report.equalsIgnoreCase(InsightConstant.JR2) || report.equalsIgnoreCase(InsightConstant.BR3) || report.equalsIgnoreCase(InsightConstant.DB2)) {
			query2 = "select 'Total for all " + temptotal + "' as " + temptotal + "," + headerQuery + " "
					+ dynamicMonthListTotal + " from " + tableName + " where institution='" + accountCode + "' AND "+
					"accessDeniedCategory='Access denied: concurrent/simultaneous user licence limit exceeded'"+
					"UNION ALL "+
					"select 'Total List Of " + temptotal + "' as " + temptotal + "," + headerQuery + " "
					+ dynamicMonthListTotal + " from " + tableName + " where institution='" + accountCode + "' AND "+
					"accessDeniedCategory='Access denied: content item not licenced'";
		}else{
			query2 = "select 'Total for all " + temptotal + "' as " + temptotal + "," + headerQuery + " "
					+ dynamicMonthListTotal + " from " + tableName + " where institution='" + accountCode + "'";
		}
		
		if(report.equalsIgnoreCase(InsightConstant.BR2)){
			query2 = query2 +" GROUP BY "+InsightConstant.BR2_GROUP_BY; 
		}
		query1 = "select " + totalTitle + "," + headerQuery + " " + dynamicMonthlist + " from " + tableName
				+ " where institution='" + accountCode + "'";
		String finalString = "";
		if (report.equalsIgnoreCase(InsightConstant.JR4)) {
			finalString = query2;
		} else if(report.equalsIgnoreCase("site_overview_table")){
			finalString = query1;
		}else {
			finalString = query2 + " UNION ALL " + query1;
		}

		rmd.log(
				"table : " + tableName + " headerQuery : " + headerQuery + " dynamic Month for Query : " + finalString);
		return finalString;
	}

	public String getTableName(String report) {
		String tableName = "";
		if (report.equalsIgnoreCase(InsightConstant.JR1)) {
			tableName = "JournalReport1";
		}
		if (report.equalsIgnoreCase(InsightConstant.JR2)) {
			tableName = "JournalReport2";
		}
		if (report.equalsIgnoreCase(InsightConstant.JR3)) {
			tableName = "JournalReport3";
		}
		if (report.equalsIgnoreCase(InsightConstant.JR4)) {
			tableName = "JournalReport4";
		}
		if (report.equalsIgnoreCase(InsightConstant.JR5)) {
			tableName = "JournalReport5";
		}
		if (report.equalsIgnoreCase(InsightConstant.BR1)) {
			tableName = "BookReport1";
		}
		if (report.equalsIgnoreCase(InsightConstant.BR2)) {
			tableName = "BookReport2";
			
		}
		if (report.equalsIgnoreCase(InsightConstant.BR3)) {
			tableName = "BookReport3";
		}
		if (report.equalsIgnoreCase(InsightConstant.ARTICLE_REQ_BY_TYPE)) {
			tableName = "ArticleRequestsByType";
		}
		if (report.equalsIgnoreCase(InsightConstant.JR1GOA)) {
			tableName = "JournalReport1GOA";
		}
		if (report.equalsIgnoreCase(InsightConstant.DB1)) {
			tableName = "DataBaseReport1";
		}
		if (report.equalsIgnoreCase(InsightConstant.DB2)) {
			tableName = "DataBaseReport2";
		}
		if (report.equalsIgnoreCase(InsightConstant.PR1)) {
			tableName = "PlatformReport1";
		}
		if (report.equalsIgnoreCase(InsightConstant.CONSOTIUM_OVERVIEW_REPORT)) {
			tableName = "ConsortiumOverviewReport";
		}
		if (report.equalsIgnoreCase(InsightConstant.INSTITUTIONAL_OVERVIEW_REPORT)) {
			tableName = "InstitutionalOverviewReport";
		}
		if (report.equalsIgnoreCase("site_overview_table")) {
			tableName = "SiteOverviewTable";
		}
		if (report.equalsIgnoreCase(InsightConstant.STANDARDS)) {
			tableName = "StandardsUsageByMonth";
		}
		if (report.equalsIgnoreCase(InsightConstant.CONFERENCES)) {
			tableName = "ConferencesUsageByMonth";
		}
		if (report.equalsIgnoreCase("consortia_member_i")) {
			tableName = "ArticleRequestsByConsortiaMember";
		}
		if (report.equalsIgnoreCase(InsightConstant.TR1)) {
			tableName = "TitleReport1";
		}
		if (report.equalsIgnoreCase(InsightConstant.TR2)) {
			tableName = "TitleReport2";
		}
		if (report.equalsIgnoreCase(InsightConstant.TR3)) {
			tableName = "TitleReport3";
		}
		if (report.equalsIgnoreCase(InsightConstant.EBOOK_CHAPTER)) {
			tableName = "EBookChapterIEEE";
		}
		if (report.equalsIgnoreCase(InsightConstant.EBOOK_CHAPTER_MIT)) {
			tableName = "EBookChapterMIT";
		}
		if (report.equalsIgnoreCase("IPs_i")) {
			tableName = "IPsArticleRequestsbyMonth";
		}
		if (report.equalsIgnoreCase("multimedia")) {
			tableName = "MultimediaContentRequest";
		}
		return tableName;
	}

	public String getQueryHeader(String report) {
		String header = "";
		if (report.equalsIgnoreCase(InsightConstant.JR1)) {
			header = InsightConstant.JR1_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.JR2)) {
			header = InsightConstant.JR2_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.JR3)) {
			header = InsightConstant.JR3_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.JR4)) {
			header = InsightConstant.JR4_QUERY;
		}
		if (report.equalsIgnoreCase("JR5")) {
			header = InsightConstant.JR5_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.BR1)) {
			header = "BookReport1";
		}
		if (report.equalsIgnoreCase(InsightConstant.BR2)) {
			header = InsightConstant.BR2_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.BR3)) {
			header = InsightConstant.BR3_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.ARTICLE_REQ_BY_TYPE)) {
			header = InsightConstant.ARBT_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.JR1GOA)) {
			header = InsightConstant.JR1GOA_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.DB1)) {
			header = InsightConstant.DB1_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.DB2)) {
			header = InsightConstant.DB2_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.PR1)) {
			header = InsightConstant.DB1_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.CONSOTIUM_OVERVIEW_REPORT)) {
			header = InsightConstant.CONSOTIUM_OVERVIEW_REPORT_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.INSTITUTIONAL_OVERVIEW_REPORT)) {
			header = InsightConstant.INSTITUTIONAL_OVERVIEW_REPORT_QUERY;
		}
		if (report.equalsIgnoreCase("site_overview_table")) {
			header = InsightConstant.ARBT_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.STANDARDS)) {
			header = InsightConstant.JR1_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.CONFERENCES)) {
			header = InsightConstant.JR1_QUERY;
		}
		if (report.equalsIgnoreCase("consortia_member_i")) {
			header = "ArticleRequestsByConsortiaMember";
		}
		if (report.equalsIgnoreCase(InsightConstant.TR1)) {
			header = InsightConstant.TR1_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.TR2)) {
			header = InsightConstant.TR2_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.TR3)) {
			header = InsightConstant.TR3_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.EBOOK_CHAPTER)) {
			header = InsightConstant.EBOOK_CHAPTER_QUERY;
		}
		if (report.equalsIgnoreCase(InsightConstant.EBOOK_CHAPTER_MIT)) {
			header = InsightConstant.EBOOK_CHAPTER_QUERY;
		}
		if (report.equalsIgnoreCase("multimedia")) {
			header = InsightConstant.MULTIMEDIA_QUERY;
		}
		return header;
	}

	public String getTotalHeader(String report) {
		String header = "";
		if (report.equalsIgnoreCase(InsightConstant.JR1) || report.equalsIgnoreCase(InsightConstant.JR2) || report.equalsIgnoreCase(InsightConstant.JR3)
				|| report.equalsIgnoreCase(InsightConstant.JR1GOA)) {
			header = journal;
		}
		if (report.equalsIgnoreCase(InsightConstant.DB1) || report.equalsIgnoreCase(InsightConstant.DB2)) {
			header = "Database_";
		}
		if (report.equalsIgnoreCase(InsightConstant.PR1)) {
			header = "Platform";
		}
		if (report.equalsIgnoreCase(InsightConstant.TR1) || report.equalsIgnoreCase(InsightConstant.TR2) || report.equalsIgnoreCase(InsightConstant.TR3)
				|| report.equalsIgnoreCase(InsightConstant.BR1) || report.equalsIgnoreCase(InsightConstant.BR2) || report.equalsIgnoreCase(InsightConstant.BR3)
				|| report.equalsIgnoreCase("multimedia")) {
			header = title;
		} else if (report.equalsIgnoreCase(InsightConstant.CONFERENCES) || report.equalsIgnoreCase(InsightConstant.STANDARDS)) {
			header = journal;
		} else if (report.equalsIgnoreCase("site_overview_table") || report.equalsIgnoreCase(InsightConstant.ARTICLE_REQ_BY_TYPE)) {
			header = title;
		} else if (report.equalsIgnoreCase(InsightConstant.EBOOK_CHAPTER) || report.equalsIgnoreCase(InsightConstant.EBOOK_CHAPTER_MIT)) {
			header = "'Chapter'";
		} else if (report.equalsIgnoreCase(InsightConstant.JR4)) {
			header = "'Collection'";
		} else if (report.equalsIgnoreCase(InsightConstant.CONSOTIUM_OVERVIEW_REPORT)) {
			header = "InstitutionName";
		} else if (report.equalsIgnoreCase(InsightConstant.INSTITUTIONAL_OVERVIEW_REPORT)) {
			header = "Section_type";
		} else if (report.equalsIgnoreCase("IPs_i")) {
			header = "IpAddress";
		}

		return header;
	}

	public boolean checkReportTableFormatJR1(String report) {
		boolean result = false;
		if (report.equalsIgnoreCase(InsightConstant.JR1) || report.equalsIgnoreCase(InsightConstant.JR1GOA) || report.equalsIgnoreCase(InsightConstant.STANDARDS)
				|| report.equalsIgnoreCase(InsightConstant.CONFERENCES)) {
			result = true;
		}
		return result;
	}

}
