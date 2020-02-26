package com.mps.insight.c5.report.publisher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class SITE_OVERVIEW_All_INST {
	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao=null;
	
	 private Connection con =null;
    public SITE_OVERVIEW_All_INST(Counter5DTO c5Dto, RequestMetaData rmd) {
	// TODO Auto-generated constructor stub
	 try {
			this.dto = c5Dto;
		    c5dao = new Counter5ReportsDao(rmd);
		    this.con=rmd.getInsightDao().getSqlConnection();
		} catch (Exception e) {
			//handle exception
		}
    }
	 public MyDataTable getReport(String download) throws Exception{
			MyDataTable mdt=null;
			String query = "";
			try {
				query=" select 'SITE_OVERVIEW_All_INSTITUTION report not available for preview But ready for Download' as 'Preview not available' ";
				mdt=c5dao.getDynamicReportJson(dto.getWebmartID(), query);
				return mdt;
				
			} catch (Exception e) {
				throw e;
			}
					
		}
	 
	   public  InputStream site_overviewDownload() throws Exception  {
		   String[] toarr= null;
			String[] fromarr= null;
			String dynamicmonth = null;
			int toyear=0;
			int fromyear=0;
			int tomonth=0;
			int frommonth=0;
			toarr=dto.getToDate().split("-");
			fromarr=dto.getFromDate().split("-");
			toyear=Integer.parseInt(toarr[1]);
			tomonth=Integer.parseInt(toarr[0]);
			fromyear=Integer.parseInt(fromarr[1]);
			frommonth=Integer.parseInt(fromarr[0]);
			
			DynamicMonthCreater dmoncreate=new DynamicMonthCreater();
			dynamicmonth=dmoncreate.MonthGroupByQuery(tomonth, toyear, frommonth, fromyear);
			String[] dynamicmonthCh=dynamicmonth.split(",");
			List<String> months = new ArrayList<>();
			for(int i=0;i<dynamicmonthCh.length;i++)
			{
				months.add(dynamicmonthCh[i]);
			}
	        StringBuilder sb =getSiteOverviewByMonth(con, months);
	//        Files.write(new java.io.File("C:\\Users\\sumit.kumar\\Desktop\\demo.csv").toPath(), sb.toString().getBytes("utf-8"));
	     return new ByteArrayInputStream(sb.toString().getBytes());
	   }

	    public static StringBuilder getSiteOverviewByMonth(Connection con, List<String> months) throws Exception {
	        StringBuilder sb = new StringBuilder();

	        sb.append("Report:	Site Overview Table By Month (Non-COUNTER) - All Institutional Users\n"
	                + "Description:	Number of Non-COUNTER Requests by Month for All Institutions\n"
	                + "Granularity:	Monthly\n"
	                + "Time Coverage:	Year to Date\n"
	                + "User Type:	All Institutional Users\n"
	                + "Content Level:	All Content\n"
	                + "Run on: " + new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date()));
	        sb.append("\n\n");
	        Statement stmt = con.createStatement();
	        String q1 = "SELECT * FROM `c5_site_overview_by_month_new` ORDER BY `institution_id`, `userActivity` ";
	        ResultSet rs = stmt.executeQuery(q1);
	        
	        List<String> monthToData = null;
	        List<List<String>> listOfMonthDataRow = new ArrayList<>();
	        List<String> userActyList = new ArrayList<>();
	        String instId = "", instName = "";
	        String prevInstId = "", prevInstName = "";
	        boolean isFirstRecord = true, isHeaderProcess = true;
	        while (rs.next()) {
	            instId = rs.getString("institution_id");
	            instName = rs.getString("institution_name");
	            if (prevInstId.equals(instId) || isFirstRecord) {
	                monthToData = new ArrayList<>();
	                for (String m : months) {
	                    monthToData.add(rs.getString(m));
	                }
	                listOfMonthDataRow.add(monthToData);
	                userActyList.add(rs.getString("userActivity"));
	                prevInstId = instId;
	                prevInstName = instName;
	                isFirstRecord = false;
	            } else {
	                if (isHeaderProcess) {
	                    sb.append(getReportHeader(months, userActyList));
	                    isHeaderProcess = false;
	                }
	                sb.append(processReport(listOfMonthDataRow, userActyList, prevInstId, prevInstName));
	                //clear lists
	                monthToData.clear();
	                listOfMonthDataRow.clear();
	                userActyList.clear();

	                /////////////////////////////
	                monthToData = new ArrayList<>();
	                for (String m : months) {
	                    monthToData.add(rs.getString(m));
	                }
	                listOfMonthDataRow.add(monthToData);
	                userActyList.add(rs.getString("userActivity"));
	                prevInstId = instId;
	                prevInstName = instName;

	            }

	        }

	        //for last Record and one Record
	        if (isHeaderProcess) {
	            sb.append(getReportHeader(months, userActyList));
	        }
	        sb.append(processReport(listOfMonthDataRow, userActyList, instId, instName));

	        return sb;
	    }

	    public static String getReportHeader(List<String> months, List<String> userActyList) {
	        StringBuilder sb = new StringBuilder();
	        sb.append("\"\",");
	        sb.append("\"\",");
	        for (String month : months) {
	            for (String str : userActyList) {
	                sb.append("\"" + month + "\"");
	                sb.append(",");
	            }
	        }
	        sb.append("\n");
	        sb.append("\"InstID\",");
	        sb.append("\"InstName\",");
	        for (String month : months) {
	            for (String str : userActyList) {
	                sb.append("\"" + str + "\"");
	                sb.append(",");
	            }
	        }
	        return sb.toString().replaceAll(",$", "");
	    }

	    public static String processReport(List<List<String>> listOfMonthDataRow, List<String> userActyList, String instId, String instName) {
	        StringBuilder sb = new StringBuilder();
	        sb.append("\n");
	        sb.append("\"" + instId + "\",");
	        sb.append("\"" + instName + "\",");

	        for (int j = 0; j < listOfMonthDataRow.get(0).size(); j++) {
	            for (int i = 0; i < userActyList.size(); i++) {
	                sb.append("\"" + listOfMonthDataRow.get(i).get(j) + "\"");
	                sb.append(",");
	            }
	        }
	        return sb.toString().replaceAll(",$", "");
	    }

	    
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
