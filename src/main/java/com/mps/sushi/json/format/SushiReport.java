package com.mps.sushi.json.format;


import java.util.Map;


public class SushiReport {
	private Map<String, ReportItems> reportItemsList;
	private ReportHeader reportHeader;
	
	
	public Map<String, ReportItems> getReportItemsList() {
		return reportItemsList;
	}

	public void setReportItemsList(Map<String, ReportItems> reportItemsList) {
		this.reportItemsList = reportItemsList;
	}

	public ReportHeader getReportHeader() {
		return reportHeader;
	}
	
	public void setReportHeader(ReportHeader reportHeader) {
		this.reportHeader = reportHeader;
	}
	
}
