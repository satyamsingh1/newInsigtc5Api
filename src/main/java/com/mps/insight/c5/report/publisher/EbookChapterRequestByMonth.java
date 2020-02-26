package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Counter5ReportsDao;

public class EbookChapterRequestByMonth {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private String coloumTitle = "`PARENT_ID` AS `Book ID`, `PARENT_TITLE` AS `Book Title`, `ISBN`, `ITEM` AS `Chapter Title`, `ITEM_ID` AS `Chapter ID`,";
	private String tableName = "c5_wiley_ebook_chapter_by_month"; 
	
	public EbookChapterRequestByMonth(Counter5DTO c5Dto, RequestMetaData rmd) {

		try {

			this.dto = c5Dto;
		    c5dao = new Counter5ReportsDao(rmd);
		} catch (Exception e) {

		}
	}

	public MyDataTable getReport(String download) {
		MyDataTable mdt = null;
		String query = "";
		try {

			query = this.getQuery(download);
			mdt = c5dao.getDynamicReportJson(dto.getWebmartID(), query);
			return mdt;
		} catch (Exception e) {
			throw e;
		}
	}

	private String getQuery(String download) {
		String[] toarr = null;
		String[] fromarr = null;
		String dynamicmonth = null;
		String dmonth1=null;
		String dmonth2=null;
		String dmonth3=null;
		int toyear = 0;
		int fromyear = 0;
		int tomonth = 0;
		int frommonth = 0;
		StringBuilder query = new StringBuilder();
		DynamicMonthCreater dmoncreate = new DynamicMonthCreater();
		// from
		// config/hardcode/tabvle/xtz/etc
		String whereClause = null;              
		String groupAndOrderCondition = null;
		String total="";
		try {
			if(dto.getReportCode().equalsIgnoreCase("MIT_EBOOK_CHAPTER")){
				tableName="c5_mit_ebook_chapter_by_month";	
			}else if(dto.getReportCode().equalsIgnoreCase("EBOOK_ACCESS_CHAPTER")){
				tableName="c5_ebook_access_by_chapter";
			}
			
			
			groupAndOrderCondition = groupAndOrderCondition();
			whereClause = whereCondition();
			toarr = dto.getToDate().split("-");
			fromarr = dto.getFromDate().split("-");
			toyear = Integer.parseInt(toarr[1]);
			tomonth = Integer.parseInt(toarr[0]);
			fromyear = Integer.parseInt(fromarr[1]);
			frommonth = Integer.parseInt(fromarr[0]);
			dynamicmonth = dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			dmonth1=dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear, null);
			dmonth2=dmoncreate.createSumMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			total=dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear,"YTD Total");
			String month=null;
		
			month=total+"AS `Reporting_Period_Total`,"+dynamicmonth.substring(0,dynamicmonth.lastIndexOf(","));
			dmonth3=dmonth2.substring(0,dmonth2.lastIndexOf(","));
		//	month=total.substring(0, total.lastIndexOf(","))+"As Reporting_Period_Total,"+dynamicmonth.substring(0,dynamicmonth.lastIndexOf(","));
			
			if(dto.getReportCode().equalsIgnoreCase("MIT_EBOOK_CHAPTER")){
				query.append("")
				.append("SELECT `PARENT_ID` AS 'Book ID', `PARENT_TITLE` AS 'Book Title', `ISBN`	AS 'ISBN', ")
				.append("`YOP` 'Copyright year',`ITEM` AS 'Chapter Title', `ITEM_ID` AS 'Chapter ID',")
				.append("  "+dmonth1+" AS Reporting_Period_Total,")
				.append(dmonth3)
				.append(" FROM `c5_mit_ebook_chapter_by_month` ")
				.append(" GROUP BY `PARENT_ID`,`PARENT_TITLE`,`ITEM_ID`,`ITEM`,`ISBN`,`YOP`");	
			}else if(dto.getReportCode().equalsIgnoreCase("EBOOK_ACCESS_CHAPTER")){
				query.append(getEbookAccessChapterQuery(dynamicmonth, total));	
			}
			
			
			
			
			if(download.equalsIgnoreCase("preview")){
				query.append(" Limit 500");
			}else{
				query.append("");
			}
			return query.toString();
		} catch (Exception e) {
			throw e;
		}

	}
	
	public String getEbookAccessChapterQuery(String dynamicmonth, String total) {
		
			coloumTitle = "SELECT DOI AS 'ChapterDOI', "
					+ "Item AS 'ChapterTitle', "
					+ "`YOP` AS 'PubYear', "
					+ "`Proprietary_ID` AS 'Chapterid', "
					+ "`Issue_no` AS 'Pubs_Chapter_ID', "
					+ "`Parent_Proprietary_ID` AS 'BookID', "
					+ "`Parent_Title` AS 'Book_Name', "
					+ "'' AS 'Edition', "
					+ "`Parent_Publisher` AS 'Publisher', "
					+ "`ISBN` AS 'Print ISBN', "
					+ "`ISBN` AS 'eISBN', "
					+ "`Parent_Print_ISSN` AS 'ISSN', "
					+ "`Platform` AS 'Platform', "
					+ "`Parent_DOI` AS 'BookDOI', "+dynamicmonth+ " sum("+total+") AS 'YTD-Total' "
					+ "FROM "+tableName+" "
					+ "WHERE metric_type in ('Total_Item_Requests') "
					+ "AND "+total+"> 0 GROUP BY DOI , "
					+ "Item, `YOP`,	`Proprietary_ID`, `Issue_no`,`Parent_Proprietary_ID`, "
					+ "`Parent_Title`,`Parent_Publisher`,`ISBN`,`Parent_Print_ISSN`,"
					+ "`Platform`,`Parent_DOI`, "+total+" ORDER BY Proprietary_ID ";
			
		
		return coloumTitle;
	}
	
	public String getGroupByTitle() {
		String GroupByTitle="";
		if (dto.getReportCode().equalsIgnoreCase("EBOOK_ACCESS_CHAPTER")) {
			GroupByTitle = " GROUP BY `DOI`,`Item`,`Publication_Date`,Item_ID,`Issue_no`,`Title_ID`,`Parent_Title`,`Article_Version`,`Publisher`,`ISBN`,`Print_ISSN`,`Online_ISSN`, `Platform `, `Parent_DOI`,";
		}else if(dto.getReportCode().equalsIgnoreCase("MIT_EBOOK_CHAPTER")){
		GroupByTitle = " GROUP BY `PARENT_ID`,`PARENT_TITLE`,`ISBN`,`ITEM`,`ITEM_ID`,";
		}
		return GroupByTitle;
	}
	
	
	public String whereCondition() {
		String querypart="";
		
		return querypart;
	}

	public String groupAndOrderCondition() {
		String query=" ORDER BY Institution_ID,`Database`";
		//if(dto.getReportCode().equalsIgnoreCase("EBOOK_ACCESS_CHAPTER")){
			query="";
		//}
		
		return query;
	}

}
