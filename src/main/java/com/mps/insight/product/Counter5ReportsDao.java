package com.mps.insight.product;

import java.sql.ResultSet;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dao.QueryTemplate;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.MyLogger;
import com.mps.insight.global.TableMapper;
import com.mysql.cj.xdevapi.Table;

public class Counter5ReportsDao {
	RequestMetaData rmd =null ;

	//private static final Logger log = LoggerFactory.getLogger(Counter5ReportsDao.class);
	PublisherSettings pubsetting = null;

	public Counter5ReportsDao(RequestMetaData rmd){
		this.rmd = rmd;
	}
	
	public MyDataTable getCounter5ReportsList(String publisher_name, String reportLevel) {
		double iTracker=0.0;
		QueryTemplate template = new QueryTemplate(rmd);
		MyDataTable mdt = null;

		try {
			rmd.log("getDynamicReportsList : webmart_id=" + publisher_name);
			if(reportLevel.equalsIgnoreCase("")){
				
			}
			StringBuilder stb = new StringBuilder();
			iTracker=1.0;
			stb.append("SELECT irm.report_code,irm.report_name,irm.report_type,irm.report_frequency ")
					.append("FROM institution_reports_statistics irm WHERE irm.status='1' AND irm.report_level='"
							+ reportLevel + "'");
			iTracker=2.0;
			mdt = template.getMDTofQueryWithPublisher(publisher_name,stb.toString());
		} catch (Exception e) {
			//log.info("Exception in getDynamicReportsList");
			rmd.exception("Counter5ReportsDao : getCounter5ReportsList : iTracker : "+iTracker+" : "+e.toString());
		}
		return mdt;
	}
	
	public MyDataTable getCounter5ReportsList(String publisher_name,int liveyear,int livemonth) {
		double iTracker=0.0;
		QueryTemplate template = new QueryTemplate(rmd);
		MyDataTable mdt = null;

		try {
			rmd.log("getDynamicReportsList : webmart_id=" + publisher_name);
			StringBuilder stb = new StringBuilder();
			iTracker=1.0;
			/*if(livemonth>9)
			{
				stb.append("SELECT irm.report_code,irm.report_name,irm.report_type,irm.report_frequency ")
				.append("FROM institution_reports_statistics irm WHERE irm.M_"+liveyear+livemonth+"='1' AND irm.report_type='PUBLISHER'")
				.append(" GROUP BY irm.report_code,irm.report_name,irm.report_type,irm.report_frequency ");
			}else
			{
			stb.append("SELECT irm.report_code,irm.report_name,irm.report_type,irm.report_frequency ")
					.append("FROM institution_reports_statistics irm WHERE irm.M_"+liveyear+"0"+livemonth+"='1' AND irm.report_type='PUBLISHER'")
					.append(" GROUP BY irm.report_code,irm.report_name,irm.report_type,irm.report_frequency ");
			}*/
			
			//
			/*stb = new StringBuilder();
			if(livemonth>9){
				stb.append("SELECT prs.report_code,prs.report_name,prs.report_type,prs.report_frequency,prs.M_"+liveyear+(livemonth-1)+" AS `fromDate`,prs.M_"+liveyear+livemonth+ " AS `toDate`  ")
				.append("FROM publisher_reports_statistics prs WHERE prs.report_type='PUBLISHER' and prs.status='1' ")
				.append(" GROUP BY prs.report_code,prs.report_name,prs.report_type,prs.report_frequency,prs.report_frequency,prs.M_"+liveyear+(livemonth-1)+",prs.M_"+liveyear+livemonth);
			}else{
				stb.append("SELECT prs.report_code,prs.report_name,prs.report_type,prs.report_frequency ")
					.append("FROM publisher_reports_statistics prs WHERE prs.report_type='PUBLISHER' and prs.status='1'")
					.append(" GROUP BY prs.report_code,prs.report_name,prs.report_type,prs.report_frequency ");
			}
			*/
			//query change by KSV on 2018-12-28
			stb = new StringBuilder("SELECT * FROM `publisher_reports_statistics` WHERE STATUS = 1 AND report_type='PUBLISHER'");
			iTracker=2.0;
			mdt = template.getMDTofQueryWithPublisher(publisher_name,stb.toString());
			
		} catch (Exception e) {
			//log.info("Exception in getDynamicReportsList");
			rmd.exception("Counter5ReportsDao : getCounter5ReportsList : iTracker : "+iTracker+" : "+e.toString());
		}
		return mdt;
	}
	
	public MyDataTable getCounter5ReportsList(String publisher_name, String reportLevel, String Institution_ID, String monthCode) {
		double iTracker=0.0;
	//	rmd = (RequestMetaData)servletRequest.getAttribute("RMD");
		QueryTemplate template = new QueryTemplate(rmd);
		MyDataTable mdt = null;
		
		try {
			//rmd.log("getCounter5ReportsList : publisher_name=" + publisher_name + " : Institution_ID : " + Institution_ID);
			StringBuilder stb = new StringBuilder();
			iTracker=1.0;
			//stb.append("SELECT * FROM `institution_reports_statistics` WHERE Institution_Id='"+Institution_ID + "' order by report_order asc" );
			stb.append(" SELECT irs.*, irm.report_code_alias AS `report_key` ")  
			.append(" FROM institution_reports_statistics  irs, insight_report_master irm ") 
			.append(" WHERE irs.institution_id = '"+Institution_ID + "' AND irs.report_code = irm.report_code ORDER BY irm.report_order ASC ");
			iTracker=2.0;
			mdt = template.getMDTofQueryWithPublisher(publisher_name, stb.toString());
		} catch (Exception e) {
			rmd.exception("Counter5ReportsDao : getCounter5ReportsList : iTracker : "+iTracker+" : "+e.toString());
			
		}
		return mdt;
	}

	public MyDataTable getReportMetricTypeDetail(int webmartID, String reportID) {
		double iTracker=0.0;
		QueryTemplate template = new QueryTemplate(rmd);
		MyDataTable mdt = null;
		StringBuilder sb = new StringBuilder();
		try {
			iTracker=1.0;
			
			//change by KSV 2018-12-15 for dropdown filters for Redis and fill
			rmd.log("getReportMetricTypeDetail : reportID=" + reportID);
			sb.append("SELECT filter_value AS `value`, filter_type AS 'type' FROM report_filters WHERE report_id = '"+ reportID.trim().toUpperCase() +"' ORDER BY `type`, `value`");
			iTracker=1.1;
			mdt = template.getMDTofQueryWithWebmartID(webmartID, sb.toString());
			iTracker=1.2;
			
			
			/*//by KSV for Redis Master report update update
			//
			if(reportID.equalsIgnoreCase("dr")){
				reportID="dr_master";
			}else if(reportID.equalsIgnoreCase("pr")){
				reportID="pr_master";
			}else if(reportID.equalsIgnoreCase("ir")){
				reportID="ir_master";
			}else if(reportID.equalsIgnoreCase("tr")){
				reportID="tr_master";
			}
			
			rmd.log("getReportMetricTypeDetail : webmart_id=" + webmartID);
			
			iTracker=2.0;
			sb.append("SELECT DISTINCT(metric_type) AS 'value', 'metricType' AS 'type' FROM "+reportID.toLowerCase().trim());
			sb.append(" UNION ");
			sb.append(" SELECT DISTINCT(data_type) AS 'value', 'dataType' AS 'type' FROM "+reportID.toLowerCase().trim());
			sb.append(" UNION ");
			sb.append(" SELECT DISTINCT(access_type) AS 'value', 'accessType' AS 'type' FROM " +reportID.toLowerCase().trim());
			sb.append(" UNION ");
			sb.append(" SELECT DISTINCT(access_method) AS 'value', 'accessMethod' AS 'type' FROM "+reportID.toLowerCase().trim());
			
			iTracker=3.0;
			//stb.append("SELECT Metric_Type AS `Metric Type` FROM c5_ref_metric_type WHERE " + reportID + "=1");
			mdt = template.getMDTofQueryWithWebmartID(webmartID, sb.toString());
			*/
			
		} catch (Exception e) {
			rmd.exception("Counter5ReportsDao : getReportMetricTypeDetail : iTracker : "+iTracker+" : "+e.toString());
			//log.info("Exception in getReportMetricTypeDetail");
		}
		return mdt;
	
	}

	public MyDataTable getReportDataTypeDetail(int webmartID, String reportID) {
		double iTracker=0.0;
		QueryTemplate template = new QueryTemplate(rmd);
		MyDataTable mdt = null;
		try {
			rmd.log("getReportDataTypeDetail : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			iTracker=1.0;
			stb.append("SELECT Data_Type AS `Data Type` FROM "+TableMapper.TABALE.get("c5_ref_data_type")+" where " + reportID + "=1");
			iTracker=2.0;
			mdt = template.getMDTofQueryWithWebmartID(webmartID, stb.toString());
		} catch (Exception e) {
			//log.info("Exception in getReportDataTypeDetail");
			rmd.exception("Counter5ReportsDao : getReportDataTypeDetail : iTracker : "+iTracker+" : "+e.toString());
		}

		return mdt;
	}

	public MyDataTable getReportAccessTypeDetail(int webmartID, String reportID) {
		double iTracker=0.0;
		QueryTemplate template = new QueryTemplate(rmd);
		MyDataTable mdt = null;
		try {
			rmd.log("getReportAccessTypeDetail : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			iTracker=1.0;
			stb.append("SELECT Access_Type AS `Access Type` FROM "+TableMapper.TABALE.get("c5_ref_access_type")+" where " + reportID + "=1");
			iTracker=2.0;
			mdt = template.getMDTofQueryWithWebmartID(webmartID, stb.toString());
			rmd.log("getReportAccessTypeDetail success");
		} catch (Exception e) {
			rmd.exception("Counter5ReportsDao : getReportAccessTypeDetail : iTracker : "+iTracker+" : "+e.toString());
			//log.info("Exception in getReportAccessTypeDetail");
		}
		return mdt;
	}

	public MyDataTable getReportAccessMethodDetail(int webmartID, String reportID) {
		double iTracker=0.0;
		QueryTemplate template = new QueryTemplate(rmd);
		MyDataTable mdt = null;
		try {
			rmd.log("getReportAccessMethodDetail : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			iTracker=1.0;
			stb.append("SELECT Access_Method AS `Access Method` FROM "+TableMapper.TABALE.get("c5_ref_access_method")+" WHERE " + reportID + "=1");
			iTracker=2.0;
			mdt = template.getMDTofQueryWithWebmartID(webmartID, stb.toString());
			rmd.log("getReportAccessMethodDetail success");
		} catch (Exception e) {
			rmd.exception("Counter5ReportsDao : getReportAccessMethodDetail : iTracker : "+iTracker+" : "+e.toString());
			//log.info("Exception in getReportAccessMethodDetail");
		}

		return mdt;
	}

	public MyDataTable getDynamicReportJson(int webmartID, String dynamicQuery) {
		double iTracker=0.0;
		QueryTemplate template = new QueryTemplate(rmd);
		MyDataTable mdt = null;
		try {
			iTracker=1.0;
			mdt = template.getMDTofQueryWithWebmartID(webmartID, dynamicQuery);

		} catch (Exception e) {
			rmd.exception("Counter5ReportsDao : getDynamicReportJson : iTracker : "+iTracker+" : "+e.toString());
			//log.info("Exception in getDynamicReportJson : " + e.getMessage());
		}
		return mdt;
	}

	public MyDataTable getReportDetailByCode(String publisher_name, String reportCode) {
		double iTracker=0.0;
		QueryTemplate template = new QueryTemplate(rmd);
		MyDataTable mdt = null;
		try {
			rmd.log("getReportDetailByCode : publisher_name=" + publisher_name);
			StringBuilder stb = new StringBuilder();
			iTracker=1.0;
			stb.append(
					"SELECT irm.report_name AS `name`,irm.report_type AS `type`,irm.report_description AS `description` ")
					.append("FROM insight_report_master irm WHERE irm.status='1' AND irm.report_code='"
							+ reportCode + "'");
			iTracker=2.0;
			mdt = template.getMDTofQueryWithPublisher(publisher_name, stb.toString());
			rmd.log("getReportDetailByCode success");
		} catch (Exception e) {
			//log.info("Exception in getReportDetailByCode");
			rmd.exception("Counter5ReportsDao : getReportDetailByCode : iTracker : "+iTracker+" : "+e.toString());
			
		}
		return mdt;
	}
	
	public MyDataTable getReportDetailByReportAlias(String publisher_name, String reportCode) {
		double iTracker=0.0;
		QueryTemplate template = new QueryTemplate(rmd);
		MyDataTable mdt = null;
		try {
			rmd.log("getReportDetailByCode : publisher_name=" + publisher_name);
			StringBuilder stb = new StringBuilder();
			iTracker=1.0;
			stb.append(
					"SELECT irm.report_code AS `code`, irm.report_name AS `name`,irm.report_type AS `type`,irm.report_description AS `description` ")
					.append("FROM insight_report_master irm WHERE irm.status='1' AND irm.report_code_alias='"
							+ reportCode + "'");
			iTracker=2.0;
			mdt = template.getMDTofQueryWithPublisher(publisher_name, stb.toString());
			rmd.log("getReportDetailByCode success");
		} catch (Exception e) {
			//log.info("Exception in getReportDetailByCode");
			rmd.exception("Counter5ReportsDao : getReportDetailByCode : iTracker : "+iTracker+" : "+e.toString());
			
		}
		return mdt;
	}

	public Long getTotalRowCounts(Counter5DTO dto) {
		QueryTemplate template = new QueryTemplate(rmd);
		Long totalRows=0l;
		
		try{
			String query= "select count(*) as 'total' from master_report where institution_id='"+dto.getInstitutionID()+"'";
			MyDataTable mdt = template.getMDTofQueryWithPublisher(dto.getPublisher(), query);
			totalRows = Long.parseLong(mdt.getValue(1, "total"));
		}catch(Exception e){
			
		}
		return totalRows;
	}

	public StringBuilder getIRRecordData(Counter5DTO dto, String query){
		StringBuilder sb = null;
		QueryTemplate template = new QueryTemplate(rmd);

		try {
			sb = template.getIRData(dto, query);
			
		} catch (Exception e) {
			MyLogger.error("Unable to create CSV file ");
		}
		return sb;
	}
}
