package com.mps.insight.c4.report;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mps.insight.global.InsightConstant;

public class DynamicDayMonthCreater {

	/**
	 * 
	 * @param month
	 *            :- this should be in format of MM-YYYY (e.g :- 01-01-2016)
	 * @param year
	 *            :- this should be in format of MM-YYYY (e.g :- 01-12-2017)
	 * @return string in form of Jan-2018,Feb-2018 etc
	 */

	public int getNumberofDay(int month, int year) {
		int day = 31;
		if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = 30;
		}
		if (month == 2) {
			if (year == 2020 || year == 2024) {
				day = 29;
			} else {
				day = 28;
			}
		}
		return day;
	}

	public String createSumMonthQueryC5(int todate, int toMonth, int toYear, int fromdate, int fromMonth,
			int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(M_").append(fromYear).append(zero).append(startMonth).append(") as `")
						.append(InsightConstant.MONTH_ARRAY[startMonth] + "-" + fromYear).append("`,");
				startMonth++;
			}

		}

		return sb.toString();
	}

	public String createSumMonthQueryC5(int toMonth, int toYear, int fromMonth, int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(M_").append(fromYear).append(zero).append(startMonth).append(") as `")
						.append(InsightConstant.MONTH_ARRAY[startMonth] + "-" + fromYear).append("`,");
				startMonth++;
			}

		}

		return sb.toString();
	}

	public String createTotalMonthSumQueryC5(int toMonth, int toYear, int fromMonth, int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		sb.append("(");
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(M_").append(fromYear).append(zero).append(startMonth).append(")+");
				startMonth++;
			}

		}
		sb.deleteCharAt(sb.toString().lastIndexOf("+"));
		sb.append(") as Reporting_Period_Total,");
		return sb.toString();
	}

	public String createDailyQueryForMonth(int today, int toMonth, int toYear, int fromday, int fromMonth,
			int fromYear) {
		DynamicDayMonthCreater ddc = new DynamicDayMonthCreater();
		int averageMonth = (toMonth - fromMonth) + 1;
		String zeroday = "";
		String zeromonth = "";
		String temp = "";
		StringBuilder sb = new StringBuilder();
		StringBuilder sbtemp = null;
		int tempMonth = 0;
		int tempMonth1 = 0;
		int counter = 0;
		
		
		if(fromMonth>toMonth && fromYear<toYear){
			toMonth = toMonth+12;
		}
		
		
		for (int i = fromMonth; i <= toMonth; i++) {
			int constI=i;
			int constYear=fromYear;
			
			if(constI>12){
				constI = constI-12;
				constYear ++;
			}
			
		
			counter++;
			if (constI < 10) {
				zeromonth = "0";
			} else {
				zeromonth = "";
			}
			if (constI == toMonth) {
				tempMonth = today;
			} else {
				tempMonth = getNumberofDay(constI, constYear);
			}

			if (constI == fromMonth) {
				tempMonth1 = fromday;
			} else {
				tempMonth1 = 1;
			}
			sb.append("(");
			sbtemp = new StringBuilder();
			for (int j = tempMonth1; j <= tempMonth; j++) {

				if (j < 10) {
					zeroday = "0";
				} else {
					zeroday = "";
				}
				sbtemp.append("D_").append(constYear).append(zeromonth).append(constI).append(zeroday + j + "+");

			}
			temp += sbtemp.toString();
			sb.append(sbtemp.substring(0, sbtemp.lastIndexOf("+")).toString() + ") as `"
					+ InsightConstant.MONTH_ARRAY[constI] + "-" + constYear + "`,");
		}

		String totalValueQuery = "";
		String averageValueQuery = "";

		totalValueQuery = "(" + temp.substring(0, temp.lastIndexOf("+")).toString() + ") as 'Total' ";
		averageValueQuery = "FLOOR((" + temp.substring(0, temp.lastIndexOf("+")).toString() + ")/" + counter
				+ ") as 'Average', ";

		String query = sb.toString() + " " + averageValueQuery + " " + totalValueQuery;
		//getDateByDay(today, toMonth, toYear, fromday, fromMonth, fromYear);
		return query;
	}

	/**
	 * Talha
	 */
	public String getDateByDay(int today, int toMonth, int toYear, int fromday, int fromMonth, int fromYear) {

		String fromdate = fromYear + "-" + (fromMonth < 10 ? "0" + fromMonth : fromMonth) + "-"
				+ (fromday < 10 ? "0" + fromday : fromday);
		String todate = toYear + "-" + (toMonth < 10 ? "0" + toMonth : toMonth) + "-"
				+ (today < 10 ? "0" + today : today);
		StringBuilder allDaySum = new StringBuilder();
		StringBuilder aliasDate = new StringBuilder();
		StringBuilder allDate = new StringBuilder();
		LocalDate start = LocalDate.parse(fromdate);
		LocalDate end = LocalDate.parse(todate);
		String query = null;
		Boolean leapCheck = false;
		while (!start.isAfter(end)) {
			String[] sp = start.toString().split("-");
			int day = Integer.parseInt(sp[2]);
			int month = Integer.parseInt(sp[1]);
			int year = Integer.parseInt(sp[0]);
			String tempdate = start.toString().replaceAll("-", "");
			allDaySum.append("D_" + tempdate + "+");
			aliasDate.append("D_" + tempdate + "+");
			if (day == 30) {
				if (month == 4 || month == 6 || month == 9 || month == 11) {
					String st = aliasDate.substring(0, aliasDate.lastIndexOf("+"));
					allDate.append("(" + st + ") as `" + InsightConstant.MONTH_ARRAY[month] + "-" + year + "`,");
					aliasDate = new StringBuilder();
				}
			}
			if (day == 31) {
				if (month != 2) {
					String st = aliasDate.substring(0, aliasDate.lastIndexOf("+"));
					allDate.append("(" + st + ") as `" + InsightConstant.MONTH_ARRAY[month] + "-" + year + "`,");
					aliasDate = new StringBuilder();
				}

			}
			if (day == 28) {
				if (month == 2) {
					LocalDate leap = LocalDate.of(year, 01, 01);
					leapCheck = leap.isLeapYear();
					if (!leap.isLeapYear()) {
						System.out.println("leap");
						String st = aliasDate.substring(0, aliasDate.lastIndexOf("+"));
						allDate.append("(" + st + ") as `" + InsightConstant.MONTH_ARRAY[month] + "-" + year + "`,");
						aliasDate = new StringBuilder();
					}
				}
			}
			if (day == 29) {
				if (month == 2) {
					String st = aliasDate.substring(0, aliasDate.lastIndexOf("+"));
					allDate.append("(" + st + ") as `" + InsightConstant.MONTH_ARRAY[month] + "-" + year + "`,");
					aliasDate = new StringBuilder();
				}
			}

			start = start.plusDays(1);
		}
		String floorDate = allDaySum.substring(0, allDaySum.lastIndexOf("+"));
		query = allDate + " " + "floor((" + floorDate + ")/2) as 'Average', (" + floorDate + ") as 'Total' ";

		return query;
	}

	public String getDateByDay(int today, int toMonth, int toYear, int fromday, int fromMonth, int fromYear, String frequency) {

		String fromdate = fromYear + "-" + (fromMonth < 10 ? "0" + fromMonth : fromMonth) + "-"
				+ (fromday < 10 ? "0" + fromday : fromday);
		String todate = toYear + "-" + (toMonth < 10 ? "0" + toMonth : toMonth) + "-"
				+ (today < 10 ? "0" + today : today);
		StringBuilder allDaySum = new StringBuilder();
		StringBuilder aliasDate = new StringBuilder();
		StringBuilder allDate = new StringBuilder();
		LocalDate start = LocalDate.parse(fromdate);
		LocalDate end = LocalDate.parse(todate);
		String query = null;
		Boolean leapCheck = false;
		int dayCount = 0;
		int monthCount = 0;
		int weekCount = 0;
		while (!start.isAfter(end)) {
			String[] sp = start.toString().split("-");
			int day = Integer.parseInt(sp[2]);
			int month = Integer.parseInt(sp[1]);
			int year = Integer.parseInt(sp[0]);
			String tempdate = start.toString().replaceAll("-", "");
			allDaySum.append("D_" + tempdate + "+");
			aliasDate.append("D_" + tempdate + "+");
			if (day == 30) {
				if (month == 4 || month == 6 || month == 9 || month == 11) {
					String st = aliasDate.substring(0, aliasDate.lastIndexOf("+"));
					allDate.append("(" + st + ") as `" + InsightConstant.MONTH_ARRAY[month] + "-"+year+"`,");
					aliasDate = new StringBuilder();
					monthCount = monthCount+1;
				}
			}
			if (day == 31) {
				if (month != 2) {
					String st = aliasDate.substring(0, aliasDate.lastIndexOf("+"));
					allDate.append("(" + st + ") as `" + InsightConstant.MONTH_ARRAY[month] + "-"+year+"`,");
					aliasDate = new StringBuilder();
					monthCount = monthCount+1;
				}

			}
			if (day == 28) {
				if (month == 2) {
					LocalDate leap = LocalDate.of(year, 01, 01);
					leapCheck = leap.isLeapYear();
					if (!leap.isLeapYear()) {
						String st = aliasDate.substring(0, aliasDate.lastIndexOf("+"));
						allDate.append("(" + st + ") as `" + InsightConstant.MONTH_ARRAY[month] + "-"+year+"`,");
						aliasDate = new StringBuilder();
						monthCount = monthCount+1;
					}
				}
			}
			if (day == 29) {
				if (month == 2) {
					String st = aliasDate.substring(0, aliasDate.lastIndexOf("+"));
					allDate.append("(" + st + ") as `" + InsightConstant.MONTH_ARRAY[month] + "-"+year+"`,");
					aliasDate = new StringBuilder();
					monthCount = monthCount+1;
				}
			}

			start = start.plusDays(1);
			dayCount = dayCount+1;
		}
		String floorDate = allDaySum.substring(0, allDaySum.lastIndexOf("+"));
		if("monthly".equalsIgnoreCase(frequency)){
			query = allDate + " " + "floor((" + floorDate + ")/"+monthCount+") as 'Average', (" + floorDate + ") as 'Total' ";
		}else if("daily".equalsIgnoreCase(frequency)){
			query = allDate + " " + "floor((" + floorDate + ")/"+dayCount+") as 'Average', (" + floorDate + ") as 'Total' ";
		}else{
			query = allDate + " " + "floor((" + floorDate + ")/"+dayCount+") as 'Average', (" + floorDate + ") as 'Total' ";
		}
		return query;
	}

	public String getDateDaybyDayforSiteSummary(int today, int toMonth, int toYear, int fromday, int fromMonth,
			int fromYear) {
		String fromdate = fromYear + "-" + (fromMonth < 10 ? "0" + fromMonth : fromMonth) + "-"
				+ (fromday < 10 ? "0" + fromday : fromday);
		String todate = toYear + "-" + (toMonth < 10 ? "0" + toMonth : toMonth) + "-"
				+ (today < 10 ? "0" + today : today);
		StringBuilder allDaySum = new StringBuilder();
		StringBuilder aliasDate = new StringBuilder();
		StringBuilder allDate = new StringBuilder();
		LocalDate start = LocalDate.parse(fromdate);
		LocalDate end = LocalDate.parse(todate);
		String query = null;
		int countDay = 0;
		while (!start.isAfter(end)) {
			String[] sp = start.toString().split("-");
			int day = Integer.parseInt(sp[2]);
			int month = Integer.parseInt(sp[1]);
			int year = Integer.parseInt(sp[0]);
			String tempdate = "D_" + start.toString().replaceAll("-", "");
			allDaySum.append(tempdate + "+");
			aliasDate.append(tempdate + " AS ");
			aliasDate.append("`" + day + "-" + InsightConstant.MONTH_ARRAY[month] + "-" + year + "`,");
			countDay=countDay+1;
			start = start.plusDays(1);
		}
		String floorDate = allDaySum.substring(0, allDaySum.lastIndexOf("+"));
		query = aliasDate + " " + "floor((" + floorDate + ")/"+countDay+") as 'Average', (" + floorDate + ") as 'Total' ";
		return query;

	}
	public String getDateDaybyDayforSiteSummaryDashboard(int today, int toMonth, int toYear, int fromday, int fromMonth,
			int fromYear) {
		String fromdate = fromYear + "-" + (fromMonth < 10 ? "0" + fromMonth : fromMonth) + "-"
				+ (fromday < 10 ? "0" + fromday : fromday);
		String todate = toYear + "-" + (toMonth < 10 ? "0" + toMonth : toMonth) + "-"
				+ (today < 10 ? "0" + today : today);
		StringBuilder allDaySum = new StringBuilder();
		StringBuilder aliasDate = new StringBuilder();
		StringBuilder allDate = new StringBuilder();
		LocalDate start = LocalDate.parse(fromdate);
		LocalDate end = LocalDate.parse(todate);
		String query = null;
		int countDay = 0;
		while (!start.isAfter(end)) {
			String[] sp = start.toString().split("-");
			int day = Integer.parseInt(sp[2]);
			int month = Integer.parseInt(sp[1]);
			int year = Integer.parseInt(sp[0]);
			String tempdate = "D_" + start.toString().replaceAll("-", "");
			allDaySum.append(tempdate + "+");
			aliasDate.append(tempdate + " AS ");
			aliasDate.append("`" + day + "-" + month + "-" + year + "`,");
			countDay=countDay+1;
			start = start.plusDays(1);
		}
		String floorDate = allDaySum.substring(0, allDaySum.lastIndexOf("+"));
		query = aliasDate + " " + "floor((" + floorDate + ")/"+countDay+") as 'Average', (" + floorDate + ") as 'Total' ";
		return query;
	}

	public String createDailyQueryC5(int today, int toMonth, int toYear, int fromday, int fromMonth, int fromYear) {

		String zeroday = "";
		String zeromonth = "";
		StringBuffer sb = new StringBuffer();
		StringBuffer sbAverage = new StringBuffer("(");
		StringBuffer sbtotal = new StringBuffer();
		int tempMonth = 0;
		int tempMonth1 = 0;
		int countAverage = 0;
		
		if(fromMonth>toMonth && fromYear<toYear){
			toMonth = toMonth+12;
		}
		
		
		for (int i = fromMonth; i <= toMonth; i++) {
			int constI=i;
			int constYear=fromYear;
			
			if(constI>12){
				constI = constI-12;
				constYear ++;
			}
			
			if (constI < 10) {
				zeromonth = "0";
			} else {
				zeromonth = "";
			}
			if (constI == toMonth) {
				tempMonth = today;
			} else {
				tempMonth = getNumberofDay(constI, constYear);
			}

			if (constI == fromMonth) {
				tempMonth1 = fromday;
			} else {
				tempMonth1 = 1;
			}
			for (int j = tempMonth1; j <= tempMonth; j++) {
				countAverage++;
				if (j < 10) {
					zeroday = "0";
				} else {
					zeroday = "";
				}
				sb.append("D_").append(constYear).append(zeromonth).append(constI).append(zeroday + j).append(" as `")
						.append(j + "-" + InsightConstant.MONTH_ARRAY[constI] + "-" + constYear + "`,");
				sbAverage.append("D_").append(constYear).append(zeromonth).append(constI).append(zeroday + j).append("+");

			}
			
		}
		sbtotal.append(sbAverage);
		sbAverage.append(")/" + countAverage + ") as `Average`,");
		sb.append("floor(" + sbAverage);
		sb.append(sbtotal + ") as `Total`");
		return sb.toString().replace("+)", ")");
	}

	public List<String> createDailyQueryC5List(int today, int toMonth, int toYear, int fromday, int fromMonth,
			int fromYear) {
		List<String> dayList = new ArrayList<>();
		String zeroday = "";
		String zeromonth = "";
		int tempMonth = 0;
		int tempMonth1 = 0;
		
		if(fromMonth>toMonth && fromYear<toYear){
			toMonth = toMonth+12;
		}
		
		for (int i = fromMonth; i <= toMonth; i++) {
			
			int constI=i;
			int constYear=fromYear;
			
			if(constI>12){
				constI = constI-12;
				constYear ++;
			}
			
			if (constI < 10) {
				zeromonth = "0";
			} else {
				zeromonth = "";
			}
			if (constI == toMonth) {
				tempMonth = today;
			} else {
				tempMonth = getNumberofDay(constI, constYear);
			}

			if (constI == fromMonth) {
				tempMonth1 = fromday;
			} else {
				tempMonth1 = 1;
			}

			for (int j = tempMonth1; j <= tempMonth; j++) {

				if (j < 10) {
					zeroday = "0";
				} else {
					zeroday = "";
				}
				dayList.add("D_" + constYear + "" + zeromonth + "" + constI + "" + zeroday + "" + j);

			}
		}

		return dayList;
	}

	public Map<String, String> createDailyQueryForMonthList(int today, int toMonth, int toYear, int fromday,
			int fromMonth, int fromYear) {
		Map<String, String> monthMap = new HashMap<>();
		String zeroday = "";
		String zeromonth = "";
		String temp = "";
		StringBuilder sb = new StringBuilder();
		StringBuilder monthName = new StringBuilder();
		StringBuilder sbtemp = null;
		int tempMonth = 0;
		int tempMonth1 = 0;
		int counter = 0;
		
		
		if(fromMonth>toMonth && fromYear<toYear){
			toMonth = toMonth+12;
		}
		
		for (int i = fromMonth; i <= toMonth; i++) {
			counter++;
			
			int constI=i;
			int constYear=fromYear;
			
			if(constI>12){
				constI = constI-12;
				constYear ++;
			}
			
			
			if (constI < 10) {
				zeromonth = "0";
			} else {
				zeromonth = "";
			}
			if (constI == toMonth) {
				tempMonth = today;
			} else {
				tempMonth = getNumberofDay(constI, constYear);
			}

			if (constI == fromMonth) {
				tempMonth1 = fromday;
			} else {
				tempMonth1 = 1;
			}
			sb.append("(");
			sbtemp = new StringBuilder();
			for (int j = tempMonth1; j <= tempMonth; j++) {

				if (j < 10) {
					zeroday = "0";
				} else {
					zeroday = "";
				}
				sbtemp.append("D_").append(constYear).append(zeromonth).append(constI).append(zeroday + j + "+");

			}
			temp += sbtemp.toString();
			monthName.append(InsightConstant.MONTH_ARRAY[constI] + "-" + constYear + ",");
			sb.append(sbtemp.substring(0, sbtemp.lastIndexOf("+")).toString() + ") as `"
					+ InsightConstant.MONTH_ARRAY[constI] + "-" + constYear + "`,");

			monthMap.put("months", temp);
			monthMap.put("monthsAsYear", sb.toString());
		}

		String totalValueQuery = "";
		String averageValueQuery = "";

		monthMap.put("monthName", monthName.toString());
		totalValueQuery = "(" + temp.substring(0, temp.lastIndexOf("+")).toString() + ") as 'Total' ";
		averageValueQuery = "FLOOR((" + temp.substring(0, temp.lastIndexOf("+")).toString() + ")/" + counter
				+ ") as 'Average', ";

		String query = sb.toString() + " " + averageValueQuery + " " + totalValueQuery;

		// totalValueQuery=ddc.createdtotalValueQueryForMonth(today, toMonth,
		// toYear, fromday, fromMonth, fromYear);
		// String
		// tvc=totalValueQuery.substring(0,totalValueQuery.lastIndexOf("+"));
		// totalValueQuery=sb.toString()+"("+tvc+") as
		// Total,"+"("+totalValueQuery.substring(0,totalValueQuery.lastIndexOf("+"))+")/"+counter
		// +" as Average";

		return monthMap;
	}
}
