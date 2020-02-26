package com.mps.insight.c4.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.mps.insight.dto.AccountDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.Account;

public class Counter4Header {

	private RequestMetaData rmd=null;
	public Counter4Header(RequestMetaData rmd){
		this.rmd = rmd;
	}
	public HashMap<String, String> getC4HeaderDetailMap(int webmartID, String accountCode, String report, String from,
			String to, String fileformat) {
		HashMap<String, String> detail = new HashMap<>();
		DynamicConstant dc = new DynamicConstant();
		String[] arrstr = null;
		arrstr = to.split("-");
		
		String day ;
		
		Calendar monthStart = new GregorianCalendar(Integer.parseInt(arrstr[1]), Integer.parseInt(arrstr[0])- 1, 1);
		int lastDay = monthStart.getActualMaximum(Calendar.DAY_OF_MONTH);
		day = lastDay<10 ? "0"+lastDay : ""+lastDay;
				
		/*switch (arrstr[0]) {
		case "02":
			day = "28";
		case "04":
			day = "30";
		case "06":
			day = "30";
		case "09":
			day = "30";
		case "11":
			day = "30";
		default: day="31";
		}*/
		to = arrstr[1] + "-" + arrstr[0];
		arrstr = from.split("-");

		from = arrstr[1] + "-" + arrstr[0];

		try {
			
			String reportname = dc.getReportName(report);
			String reportdesc = dc.getReportDescription(report);
			Account account = new Account(rmd);
			AccountDTO adto = new AccountDTO();
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			String strDate = formatter.format(new Date());
			adto = account.getAccountByCode(accountCode, webmartID);
			String reportingperiodname = "Period covered by Report:";
			String reportingperiodvalue = from + "-01 to " + to + "-" + day;
			String daterunName = "Date run:";
			String daterunValue = strDate;
			//detail.put("daterun", strDate);
			detail.put("fileformat", fileformat);
			detail.put("reportCode", report);
			if (report.equalsIgnoreCase("JR1") || report.equalsIgnoreCase("JR1GOA") || report.equalsIgnoreCase("JR2")
					|| report.equalsIgnoreCase("JR4") || report.equalsIgnoreCase("BR2")
					|| report.equalsIgnoreCase("BR3") || report.equalsIgnoreCase("DB1") || report.equalsIgnoreCase("PR1")
					|| report.equalsIgnoreCase("DB2")) {
				detail.put("rowcount", "7");
				// 1st row
				detail.put("R1C1", reportname);
				detail.put("R1C2", reportdesc);
				// 2nd row
				detail.put("R2C1", adto.getAccountName());
				if (report.equalsIgnoreCase("BR2")) {
					detail.put("R2C2", "Section Type:");
				} else {
					detail.put("R2C2", "");
				}
				// 3rd row
				detail.put("R3C1", "");
				if (report.equalsIgnoreCase("BR2")) {
					detail.put("R3C2", "Chapter");
				} else {
					detail.put("R3C2", "");
				}

				// 4th row
				detail.put("R4C1", reportingperiodname);
				detail.put("R4C2", "");
				// 5th row
				detail.put("R5C1", reportingperiodvalue);
				detail.put("R5C2", "");
				// 6th row
				detail.put("R6C1", daterunName);
				detail.put("R6C2", "");
				// 7th row
				detail.put("R7C1", daterunValue);
				detail.put("R7C2", "");
			}

			if (report.equalsIgnoreCase("article_req_by_type")
					|| report.equalsIgnoreCase("Standards")|| report.equalsIgnoreCase("Conferences")) {
				
				detail.put("rowcount", "4");

				// 1st row
				detail.put("R1C1", reportname);
				detail.put("R1C2", reportdesc);
				// 2nd row
				detail.put("R2C1", adto.getAccountName());
				detail.put("R2C2", "");

				// 3rd row
				detail.put("R3C1", daterunName);
				detail.put("R3C2", "");

				// 4th row
				detail.put("R4C1", daterunValue);
				detail.put("R4C2", "");
			}
			
			if (report.equalsIgnoreCase("ebook_chapter") || report.equalsIgnoreCase("ebook_chapter_mit")
					 || report.equalsIgnoreCase("site_overview_table")) {
				
				detail.put("rowcount", "4");

				// 1st row
				detail.put("R1C1", "Report :");
				detail.put("R1C2", reportname);
				// 2nd row
				detail.put("R2C1", adto.getAccountName());
				detail.put("R2C2", "");

				// 3rd row
				detail.put("R3C1", "Description:");
				detail.put("R3C2", reportdesc);

				// 4th row
				detail.put("R4C1", "Run on:");
				detail.put("R4C2", daterunValue);
			}

		} catch (Exception e) {
			// LOG.error("Exception in header detail Counter 4 :
			// "+e.getMessage());
		}
		return detail;
	}

}
