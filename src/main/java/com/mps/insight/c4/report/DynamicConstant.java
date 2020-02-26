package com.mps.insight.c4.report;

public class DynamicConstant {
	
	public String getReportName(String report){
		String name="";
		switch(report){
		case "JR1":name="Journal Report 1 (R4)";
		break;
		case "JR2":name="Journal Report 2 (R4)";
		break;
		case "JR3":name="Journal Report 3 (R4)";
		break;
		case "JR4":name="Journal Report 4 (R4)";
		break;
		case "BR1":name="";
		break;
		case "BR2":name="Book Report 2 (R4)";
		break;
		case "BR3":name="Book Report 3 (R4)";
		break;
		case "article_req_by_type":name="Article Requests by Type";
		break;
		case "JR1GOA":name="Journal Report 1 GOA (R4)";
		break;
		case "DB1":name="Database Report 1 (R4)";
		break;
		case "DB2":name="";
		break;
		case "PR1":name="Platform Report 1 (R4)";
		break;
		case "Consortium_Overview_Report":name="Consortia Overview Report";
		break;
		case "Institutional_Overview_Report":name="Institutional Overview Report";
		break;
		case "site_overview_table":name="Site Overview Table";
		break;
		case "Standards":name="Standards";
		break;
		case "Conferences":name="Conferences";
		break;
		case "consortia_member_i":name="30";
		break;
		case "TR1":name="Title Report 1";
		break;
		case "TR2":name="Title Report Denied";
		break;
		case "TR3":name="Title Report ";
		break;
		case "ebook_chapter":name="Wiley-IEEE EBook Chapter request By Month and Institution";
		break;
		case "ebook_chapter_mit":name="MIT EBook Chapter request By Month and Institution";
		break;
		case "IPs_i":name="IP Address Report";
		break;
		case "multimedia":name="Multimedia Report";
		break;
		}
		return name;
	}
	
	public String getReportDescription(String report){
		String name="";
		switch(report){
		case "JR1":name="Number of Successful Full-Text Article Requests by Month and Journal";
		break;
		case "JR2":name="Access Denied to Full-text Articles by Month, Journal and Category";
		break;
		case "JR3":name="Number of Successful Item Requests by Month, Journal and Page Type";
		break;
		case "JR4":name="Total Searches Run by Month and Collection";
		break;
		case "BR1":name="";
		break;
		case "BR2":name="Number of Successful Section Requests by Month and Title";
		break;
		case "BR3":name="Access Denied to Content Items by Month, Title and Category";
		break;
		case "article_req_by_type":name="";
		break;
		case "JR1GOA":name="Number of Successful Gold Open Access Full-Text Article Requests by Month and Journal";
		break;
		case "DB1":name="Total Searches, Result Clicks and Record Views by Month and Database";
		break;
		case "DB2":name="";
		break;
		case "PR1":name="Total Searches, Result Clicks and Record Views by Month and Platform";
		break;
		case "Consortium_Overview_Report":name="a) Visits b) Page Views c) Fulltext Requests d) Licence denials";
		break;
		case "Institutional_Overview_Report":name="a) Fulltext Requests b) Licence denials";
		break;
		case "site_overview_table":name="";
		break;
		case "Standards":name="Number of Successful Article Requests by Month and Standard";
		break;
		case "Conferences":name="Number of Successful Article Requests by Month and Conference";
		break;
		case "consortia_member_i":name="";
		break;
		case "TR1":name="";
		break;
		case "TR2":name="";
		break;
		case "TR3":name="";
		break;
		case "ebook_chapter":name="Successful Full Text Chapter Request in either HTML or PDF format by All Users";
		break;
		case "ebook_chapter_mit":name="Successful Full Text Chapter Request in either HTML or PDF format by All Users";
		break;
		case "IPs_i":name="";
		break;
		case "multimedia":name="Number of Successful Multimedia Full Content Unit Requests by Month and Collection";
		break;
		}
		return name;
	}

}
