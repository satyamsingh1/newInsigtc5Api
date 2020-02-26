package com.mps.insight.c5.report.publisher;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.TableMapper;
import com.mps.insight.product.Counter5ReportsDao;

public class EbookOutputDetail {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	private String coloumTitle = " Institution_ID AS 'Account ID', Institution_Name AS 'Institution or Customer ID', CASE WHEN Institution_Type='Institution' THEN 'I' WHEN Institution_Type IN('Group', 'Consortia') THEN 'C' ELSE Institution_Type END AS 'Institution_Type', 'Total for all Titles' AS 'eBook ID',";
	private String groupTitle = " GROUP BY Institution_ID, Institution_Name, Institution_Type, Metric_Type ";
	public EbookOutputDetail(Counter5DTO c5Dto, RequestMetaData rmd) {

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
		String[] toarr= null;
		String[] fromarr= null;
		String dynamicmonth = null;
		String dynamicOnlyMonth = null;
		int toyear=0;
		int fromyear=0;
		int tomonth=0;
		int frommonth=0;
		String total="";
		
		
		StringBuilder query=new StringBuilder();
		DynamicMonthCreater dmoncreate = new DynamicMonthCreater();
		String tableName = TableMapper.TABALE.get("c5_ebook_output_and_details_customer_details_table"); 
		try {
			toarr = dto.getToDate().split("-");
			fromarr = dto.getFromDate().split("-");
			toyear = Integer.parseInt(toarr[1]);
			tomonth = Integer.parseInt(toarr[0]);
			fromyear = Integer.parseInt(fromarr[1]);
			frommonth = Integer.parseInt(fromarr[0]);
			dynamicmonth = dmoncreate.createMonthQueryC5(tomonth, toyear, frommonth, fromyear);
			dynamicOnlyMonth =    dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear,"");
			total=dmoncreate.createTotalMonthQueryC5(tomonth, toyear, frommonth, fromyear,"YTD Total");
			String month=null;
			String monthVal="";
			String monthName="";
			month=dynamicmonth.substring(0,dynamicmonth.lastIndexOf(","));
			monthVal = month.replaceAll("SUM\\(", "`").replaceAll("\\)", "`");
			monthName = monthVal.replaceAll("[`][A-Za-z]{3}[-][0-9]{4}[`]", "").replaceAll(" as ", "");
			query.append("SELECT a.* FROM (")
			.append("SELECT Institution_ID, Institution_Name, '' as Item_Id, '' as Institution_Type, 'Total for all item requests' AS Parent_Title, ' ' as Title_Id, '' as Metric_Type, ")
			.append( month )
			.append(" FROM "+tableName+"  WHERE ")
			.append("metric_type in ('Total_Item_Requests')   AND Institution_ID NOT IN('','-') AND Institution_Name NOT IN ('', ' ') ")
			.append("GROUP BY Institution_ID, Institution_Name ")
			.append(" UNION ALL ")
			.append("SELECT Institution_ID, Institution_Name,item_id, ")
			.append("CASE WHEN Institution_Type='Institution' THEN ")
			.append("'I' ")
			.append("WHEN Institution_Type='Group' THEN ")
			.append("'C' ")
			.append("WHEN Institution_Type='Consortia' THEN ")
			.append("'C' ")
			.append("ELSE Institution_Type ")
			.append("END")
			.append(", parent_title, title_id, metric_type, "+monthVal+" ")
			.append("FROM "+tableName+"  WHERE ")
			.append("metric_type in ('Total_Item_Requests', 'Total_Item_Investigations', 'Unique_Item_Investigations', 'Unique_Item_Requests')"
					+ "  AND Institution_ID NOT IN('','-',' ')  AND " +dynamicOnlyMonth +"> 0 ")
			.append("GROUP BY Institution_ID, Institution_Name, item_id, Institution_Type, parent_title, title_id, metric_type  ")
			.append(") a ")
			.append("ORDER BY a.Institution_Name, a.title_id ASC");
			
			if(download.equalsIgnoreCase("preview")){
				query.append(" Limit 500");
			}else{
				query.append("");
			}
			return query.toString();
		} catch (Exception e) {
			//
			throw e;
		}
	 
		
	}

}
