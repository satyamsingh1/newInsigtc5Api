package com.mps.insight.dynamicdate;

import java.time.LocalDate;

import com.mps.insight.global.InsightConstant;

public class DynamicMonthCreaterCounter5 {
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
		int dayCount = 0;
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
			dayCount = dayCount + 1;
		}
		query = allDate.substring(0, allDate.lastIndexOf(","));
		System.out.println(query);
		return query;
	}
}
