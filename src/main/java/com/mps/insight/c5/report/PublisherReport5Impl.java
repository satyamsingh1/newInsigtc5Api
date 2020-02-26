package com.mps.insight.c5.report;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.mps.insight.c5.report.publisher.Oa_report;
import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.OaDto;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;
import com.mps.insight.impl.ExcelFormatImpl;
import com.mps.insight.product.Counter5ReportsDao;
import com.mps.insight.product.PublisherSettings;

public class PublisherReport5Impl {

	//private static final Logger LOG = LoggerFactory.getLogger(PublisherReport5Impl.class);
	Counter5ReportsDao c5dao = null;
	PublisherReport5AttributImpl att = null;
	private RequestMetaData rmd = null;
	
	public PublisherReport5Impl(RequestMetaData rmd){
		this.rmd =rmd;
	}

	public JsonArray getCounter5ReportsList(int webmartID) {
		c5dao = new Counter5ReportsDao(rmd);
		JsonArrayBuilder jarraybuild = Json.createArrayBuilder();
		JsonObjectBuilder jobuilder = Json.createObjectBuilder();
		PublisherSettings ps = null;
		String fromDate = "";
		String toDate = "";
		ArrayList<Integer> dateList = new ArrayList<>();
		boolean isValidReport = false;
		String columnName = "";
		try {
			ps = new PublisherSettings(rmd);
			String pubname = ps.getPublisherCode(webmartID);
			int liveyear = ps.getLiveYear(webmartID);
			int livemonth = ps.getLibLiveMonth(webmartID, liveyear);
			MyDataTable mdt = c5dao.getCounter5ReportsList(pubname,liveyear,livemonth);
			int rowCount = mdt.getRowCount();
			int colCount = mdt.getColumnCount();
			for (int rowNo = 1; rowNo <= rowCount; rowNo++) {
				dateList = new ArrayList<>();
				isValidReport = false;
				
				for(int columnIndex = 1; columnIndex <= colCount; columnIndex++){
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
						
						//check for possitive value = 1 for valid /report month
						if(value == 1){ //valid report
							isValidReport = true;
							int dateFomrat = Integer.parseInt(columnName.split("_")[1].trim());
							dateList.add(dateFomrat);
							
						}
					}
				}//end column loop
				
				//check for valid report and entry i json builder
				if(isValidReport){
					jobuilder.add(InsightConstant.KEY, mdt.getValue(rowNo, "report_code"));
					jobuilder.add(InsightConstant.VALUE, mdt.getValue(rowNo, "report_name"));
					jobuilder.add(InsightConstant.DESC, mdt.getValue(rowNo, "report_type"));
					jobuilder.add("frequency", mdt.getValue(rowNo, "report_frequency"));
					
					Collections.sort(dateList, (a, b) -> a.compareTo(b));
					fromDate = dateList.get(0).toString();
					toDate = dateList.get(dateList.size() - 1).toString();
					
					//reformatting fromdate to days level
					fromDate = fromDate.substring(0, 4) + "-" + fromDate.substring(4, 6) + "-01";
					
					//reformatting to date to days level
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
		
	
	public MyDataTable getCounter5Reports(Counter5DTO dto, String download) {
		MyDataTable mdt = null;
		att = new PublisherReport5AttributImpl(rmd);
		try {
			mdt = att.getPublisherMDT(dto, download);
		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		return mdt;
	}
	//change by satyam 06/02/2019
	public  MyDataTable getBookDetailReport( String webmartCode,String title_id,String liveYear,String liveMonth,String data_type) throws Exception{
		List<String> reportSection = new ArrayList<>();
		InsightDAO insightDao = null;
		MyDataTable mdt = null;
		try {
			// passed title_id and liveyear as a dynamic and dont know about metric type and sata type
			insightDao =rmd.getInsightDao();
			rmd.log("getLiveMonth : webmart_code=" + webmartCode);
			StringBuilder stb = new StringBuilder();
			stb.append(" SELECT title_id, parent_title, doi, item, issue_volume, issue_no, page_no, SUM(M_"+liveYear+liveMonth+") AS counts FROM "+TableMapper.TABALE.get("master_report_table")+" WHERE title_id='"+title_id+"' AND data_type='"+data_type+"' AND M_"+liveYear+liveMonth+">0 AND metric_type='Total_Item_Requests' AND institution_type != 'group' GROUP BY title_id, parent_title, doi, item, issue_volume, issue_no, page_no ORDER BY counts DESC ");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			rmd.log(reportSection.toString());
		} catch (Exception e) {
			rmd.log("Exception in getReportDetail... "+e.getMessage());
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return mdt;
	}
	

	public JsonObject getCounter5ReportsJson(Counter5DTO dto, String download) {
		att = new PublisherReport5AttributImpl(rmd);
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
				mdt = getCounter5Reports(dto, download);
				data = mdt.getJsonKeyValue();
			}
			
			headerdetail = att.getCounter5ReportHeader(dto);
			header = att.geHeaderJson(headerdetail);
			finaljson.add("header", header);
			finaljson.add(InsightConstant.DATA, data);

		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		return finaljson.build();
	}

	public InputStream getCounter5ReportsStream(Counter5DTO dto, String download) {
		att = new PublisherReport5AttributImpl(rmd);
		InputStream io = null;
		MyDataTable mdt = null;
		String finalfile = "";
		String datafile = "";
		String headerData = "";
		PublisherSettings ps = null;
		ExcelFormatImpl excelimpl = new ExcelFormatImpl(rmd);
		try {
			ps = new PublisherSettings(rmd);
			dto.setPublisher(ps.getPublisherCode(dto.getWebmartID()));
			
			if(dto.getReportCode().equalsIgnoreCase("OA_USAGE")){
				mdt=getOaUsageReport(dto);
			}else{
				mdt = getCounter5Reports(dto, download);
			}
			
			HashMap<String, String> c5 = att.getExcelReportHeader(dto);
			if (dto.getFileType().equalsIgnoreCase(InsightConstant.CSV)) {
				datafile = mdt.getCsvDataWithQuote(",");
			} else if (dto.getFileType().equalsIgnoreCase(InsightConstant.TSV)) {
				datafile = mdt.getTsvData();
			} else if (InsightConstant.XLS.equalsIgnoreCase(dto.getFileType())
					|| InsightConstant.XLSX.equalsIgnoreCase(dto.getFileType())) {
				io = excelimpl.getExcelReports(mdt, c5);
			}
			if (InsightConstant.XLS.equalsIgnoreCase(dto.getFileType())
					|| InsightConstant.XLSX.equalsIgnoreCase(dto.getFileType())) {
				// io = excelimpl.getExcelReports(mdt, c5);
			} else {
				headerData = getReportHeaderForStream(dto);
				finalfile = headerData + datafile;
				io = new ByteArrayInputStream(finalfile.getBytes());
			}

		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		return io;
	}

	public String getReportHeaderForStream(Counter5DTO dto) {
		att = new PublisherReport5AttributImpl(rmd);
		HashMap<String, String> c5 = att.getCounter5ReportHeader(dto);
		String seperator = "";
		if (dto.getFileType().equalsIgnoreCase(InsightConstant.CSV)) {
			seperator = ",";
		} else if (dto.getFileType().equalsIgnoreCase(InsightConstant.TSV)) {
			seperator = "	";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Report_Name"+seperator+""+c5.get("Report_Name")+"\n");
		sb.append("Report_ID"+seperator+""+c5.get("Report_ID")+"\n");
		sb.append("Release"+seperator+""+c5.get("Release")+"\n");
		sb.append("Institution_Name"+seperator+""+c5.get("Institution_Name")+"\n");
		sb.append("Institution_ID"+seperator+""+c5.get("Institution_ID")+"\n");
		sb.append("Metric_Types"+seperator+""+c5.get("Metric_Types")+"\n");
		sb.append("Report_Filters"+seperator+""+c5.get("Report_Filters")+"\n");
		sb.append("Report_Attributes"+seperator+""+c5.get("Report_Attributes")+"\n");
		
		sb.append("Exceptions"+seperator+""+c5.get("Exceptions")+"\n");
		
		sb.append("Reporting_Period"+seperator+""+c5.get("Reporting_Period")+"\n");
		sb.append("Created"+seperator+""+c5.get("Created")+"\n");
		sb.append("Created_By"+seperator+""+c5.get("Created_By")+"\n\n");

		return sb.toString();
	}
	
	
	//change by satyam singh 06/02/2019
	public InputStream getCounter5BookReportsStream(String webmartCode, String title_id, String data_type,String liveMonth,String liveYear) {
		att = new PublisherReport5AttributImpl(rmd);
		InputStream io = null;
		MyDataTable mdt = null;
		String finalfile = "";
		String datafile = "";
		String headerData = "";
		PublisherSettings ps = null;
		ExcelFormatImpl excelimpl = new ExcelFormatImpl(rmd);
		try {
			ps = new PublisherSettings(rmd);
			mdt = getBookDetailReport(webmartCode,title_id,liveYear,liveMonth,data_type);
			//mdt store all data 
			//HashMap<String, String> c5 = att.getExcelReportHeader(dto);
			// if (InsightConstant.XLS.equalsIgnoreCase(dto.getFileType())
			//		|| InsightConstant.XLSX.equalsIgnoreCase(dto.getFileType())) {
			//	io = excelimpl.getExcelReports(mdt, c5);
			//}
		   // else {
			//	headerData = getReportHeaderForStream(dto);
				//finalfile = headerData + datafile;
				//io = new ByteArrayInputStream(finalfile.getBytes());
			//}

		} catch (Exception e) {
			rmd.exception("Exception in getCounter5ReportsList :" + e.getMessage());
		}
		return io;
	}

	private MyDataTable getOaUsageReport(Counter5DTO dto){
		MyDataTable mdt_oa = new MyDataTable("OA_USAGE_Report");
		try {
			
			if(dto.getInstitutionID()==null){
				dto.setReportCodeAlias(dto.getReportCode());
				dto.setReportCode(dto.getReportCode());
				Connection con= rmd.getInsightDao().getSqlConnection();
				 Oa_report oa=new Oa_report();
				 ArrayList<OaDto> oa_report_records= oa.getReport(dto,con);
				 mdt_oa.addColumn("UserID", "");
				 mdt_oa.addColumn("Customer", "");
				 //mdt_oa.addColumn("Data_Type", "");
				 mdt_oa.addColumn("Total Usage", "");
				 mdt_oa.addColumn("OA Usage", "");
				 mdt_oa.addColumn("Subscribed OA Usage", "");
				 
				 for (OaDto oaDto : oa_report_records) {
					 mdt_oa.addRow();
					 int rowIndex = mdt_oa.getRowCount();
					 mdt_oa.updateData(rowIndex, "UserID", oaDto.getTitleId());
					 mdt_oa.updateData(rowIndex, "Customer", oaDto.getTitleName());
					// mdt_oa.updateData(rowIndex, "Data_Type",  oaDto.getDataType());
					 mdt_oa.updateData(rowIndex, "Total Usage", ""+oaDto.getTotalUses());
					 mdt_oa.updateData(rowIndex, "OA Usage", ""+oaDto.getOaUses());
					 mdt_oa.updateData(rowIndex, "Subscribed OA Usage", ""+oaDto.getSuscribOa() );
				}
			}else{
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
}
