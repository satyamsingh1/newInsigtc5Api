package com.mps.insight.c5.report.ieee;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.TableMapper;

public class SITE_OVERVIEW {

	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String tableName = TableMapper.TABALE.get("c5_site_overview_by_month_new");
	private String Query = "";
	private String monthSumQuery = "";
	private String monthQuery = "";
	private String groupByQuery="";
	// private String monthSumtotalquery ="";
	private String totalMonth = "";
	private String previewType = "";

	private Counter5DTO dto;

	public SITE_OVERVIEW(Counter5DTO dto, String previewType, RequestMetaData rmd) {
		this.dto = dto;
		this.previewType = previewType;
		this.rmd = rmd;
		run();
	}

	public void run() {
		includeMonthParameters();
		generateSITE_OVERVIEWQuery();
		
	}

	private String generateSITE_OVERVIEWQuery() {
		StringBuilder query = new StringBuilder();

		try {
			query.append(" SELECT userActivity,")
			.append(" SUM"+totalMonth+" AS Total_Reporting_Period,")
			.append(""+monthQuery)
			.append(" from "+tableName)
			.append(" where Institution_ID='"+dto.getInstitutionID()+"' ")
			.append(" GROUP BY userActivity,"+groupByQuery);
		

		} catch (Exception e) {
			// MyLogger.error("generateJR3GoaQuery : unable to create Query :
			// "+e.toString());
			rmd.exception("generateSITE_OVERVIEWQuery : unable to create Query : " + e.toString());
		}
		return Query = query.toString();
	}

	private void includeMonthParameters() {
		try {
			String[] fromarr = dto.getFromDate().split("-");
			String[] toarr = dto.getToDate().split("-");

			monthSumQuery = dmc.createSumMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));

			monthSumQuery = monthSumQuery.substring(0, monthSumQuery.toString().lastIndexOf(","));
			
			monthQuery = dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
			monthQuery = monthQuery.substring(0, monthQuery.lastIndexOf(","));
			groupByQuery = dmc.MonthGroupByQuery(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
			groupByQuery = groupByQuery.substring(0, groupByQuery.lastIndexOf(","));
			
			
			totalMonth = dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");

		} catch (Exception e) {
			rmd.exception("includeMonthParameters : unable to generate Month list for query : " + e.toString());
		}
	}

	public String getQuery() {
		rmd.log(Query.toString());
		return Query;
	}
	/*public MyDataTable getMDT(String query) {
		InsightDAO conn = rmd.getInsightDao();
		MyDataTable newMdt = new MyDataTable("transpose");
		
		try {
			MyDataTable mdt = conn.executeSelectQueryMDT(query);
			
			int rowCount = mdt.getRowCount();
			int colCount = mdt.getColumnCount();
			
			newMdt.addRow();
			newMdt.addColumn("INSTITUTION_ID", "0");
			
			for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
				newMdt.addColumn(mdt.getValue(rowIndex, "INSTITUTION_ID"), "");
			}
			
			int collIndex=1;
			
			for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
				int newRow = newMdt.getRowCount();
				newMdt.updateData(newRow, collIndex,  "PAGE_VIEW");
				newMdt.updateData(newRow, collIndex+1, mdt.getValue(rowIndex, "PAGE_VIEW"));
				
				newMdt.addRow();
				newRow = newMdt.getRowCount();
				newMdt.updateData(newRow, collIndex, "TOTAL_ITEM_REQUESTS");
				newMdt.updateData(newRow, collIndex+1, mdt.getValue(rowIndex, "TOTAL_ITEM_REQUESTS"));

				newMdt.addRow();
				newRow = newMdt.getRowCount();
				newMdt.updateData(newRow, collIndex, "VISITS");
				newMdt.updateData(newRow, collIndex+1, mdt.getValue(rowIndex, "VISITS"));
				newMdt.addRow();
				newRow = newMdt.getRowCount();
				newMdt.updateData(newRow, collIndex, "SEARCHES_RUN");
				newMdt.updateData(newRow, collIndex+1, mdt.getValue(rowIndex, "SEARCHES_RUN"));
				
				newMdt.addRow();
				newRow = newMdt.getRowCount();
				newMdt.updateData(newRow, collIndex , "TOTAL_ITEM_INVESTIGATIONS");
				newMdt.updateData(newRow, collIndex+1, mdt.getValue(rowIndex, "TOTAL_ITEM_INVESTIGATIONS"));
			}
			
				
		} catch (Exception e) {
			e.printStackTrace();		}
		
		
		return newMdt;
		
	}
*/	
}
