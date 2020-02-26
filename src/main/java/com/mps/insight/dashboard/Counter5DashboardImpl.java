package com.mps.insight.dashboard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.json.JsonObjectBuilder;

import com.mps.insight.c4.report.DynamicDayMonthCreater;
import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dao.QueryTemplate;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;
import com.mps.insight.dashboard.Counter5GraphFormatImpl;
import com.mps.insight.product.PublisherSettings;
import com.mps.redis.Redis;

public class Counter5DashboardImpl {
	private RequestMetaData rmd;

	public Counter5DashboardImpl(RequestMetaData rmd) {
		this.rmd = rmd;
	}

	// private static final Logger log =
	// LoggerFactory.getLogger(Counter5DashboardImpl.class);
	String from = " From ";
	String title = "SELECT 'ALL User' AS `data_category`, data_type, Access_type, metric_type,";
	String where = "WHERE Metric_Type = 'Unique_Title_Requests' ";
	Counter5GraphFormatImpl cgl = new Counter5GraphFormatImpl(rmd);

	// added by satyam
	public String getCounter5DashboardCountryData(int webmartID) throws Exception {
		QueryTemplate template = new QueryTemplate(rmd);
		PublisherSettings ps = new PublisherSettings(rmd);
		String result = null;
		String publisher = rmd.getPublisherNameFromRedis(webmartID);
		int year = ps.getLiveYear(webmartID);
		int month = ps.getPubLiveMonth(webmartID, year);
		Redis redis = new Redis();
		result = redis.getValueFromRedisWithKey(webmartID + "_geo_country");
		if (null == result) {
			MyDataTable mdt = null;
			String query = getQueryCountry(month, year, publisher);
			mdt = template.getMDTofQueryWithWebmartID(webmartID, query);
			result = cgl.getArrayformatCountry(mdt);
			redis.setValueToRedisWithKey(webmartID + "_geo_country", result);
		}
		rmd.log("webmartID :- " + webmartID);
		return result;
	}

	public String getCounter5DashboardGraphData(int webmartID) throws Exception {
		QueryTemplate template = new QueryTemplate(rmd);
		PublisherSettings ps = new PublisherSettings(rmd);
		String result = null;
		int year = ps.getLiveYear(webmartID);
		int month = ps.getPubLiveMonth(webmartID, year);
		Redis redis = new Redis();
		result = redis.getValueFromRedisWithKey(webmartID + "_no_licence");
		if (null == result) {
			MyDataTable mdt = null;
			String query = getQueryNoLicence(month, year);
			mdt = template.getMDTofQueryWithWebmartID(webmartID, query);
			result = cgl.getJsonFormatNoLicence(mdt, year, month);
			redis.setValueToRedisWithKey(webmartID + "_no_licence", result);
		}
		rmd.log("webmartID :- " + webmartID);
		return result;
	}

	public String getCounter5SiteSummaryDashboardGraphData(int webmartID) throws Exception {
		QueryTemplate template = new QueryTemplate(rmd);
		PublisherSettings ps = new PublisherSettings(rmd);
		DynamicDayMonthCreater dmc = new DynamicDayMonthCreater();
		Redis redis = new Redis();
		int liveyear = ps.getLiveYear(webmartID);
		int livemonth = ps.getPubLiveMonth(webmartID, liveyear);
		int toMonth = 0;
		int toYear = 0;
		String fromdate = liveyear + "-" + (livemonth < 10 ? "0" + livemonth : livemonth) + "-01";
		String result = null;
		List<String> monthList = new ArrayList<>();
		List<Integer> drillDownMonthlist = new ArrayList<>();
		List<Integer> drillDownYearlist = new ArrayList<>();
		LocalDate start = LocalDate.parse(fromdate);
		for (int i = 0; i < 12; i++) {
			String[] sp = start.minusMonths(i).toString().split("-");
			int day = Integer.parseInt(sp[2]);
			int month = Integer.parseInt(sp[1]);
			int year = Integer.parseInt(sp[0]);
			monthList.add(InsightConstant.MONTH_ARRAY[month] + "-" + year);
			drillDownMonthlist.add(month);
			drillDownYearlist.add(year);
			if (i == 11) {
				toMonth = month;
				toYear = year;
			}
		}
		Collections.reverse(drillDownMonthlist);
		Collections.reverse(drillDownYearlist);
		Collections.reverse(monthList);
		int today = dmc.getNumberofDay(livemonth, liveyear);
		result = redis.getValueFromRedisWithKey(webmartID + "_site_summary");
		if (null == result) {
			MyDataTable mdt = null;
			// Month Create for Query -1
			String monthQuery = dmc.getDateByDay(today, livemonth, liveyear, 1, toMonth, toYear);
			String query = "SELECT TYPE, " + monthQuery + " " + from + " " + TableMapper.TABALE.get("c5_site_summary")
					+ " ";
			mdt = template.getMDTofQueryWithWebmartID(webmartID, query);
			if (mdt.getRowCount() < 1) {
				rmd.exception("Data not Found in Database : Query : " + query.toString() + " : Live Month : "
						+ livemonth + " : Live year : " + liveyear);
			}
			JsonObjectBuilder jsonobj = cgl.getJsonFormatSiteSummary(mdt, monthList);
			// Month Create for Query -2
			monthQuery = dmc.getDateDaybyDayforSiteSummaryDashboard(today, livemonth, liveyear, 1, toMonth, toYear);

			query = "SELECT TYPE, " + monthQuery + " " + from + " " + TableMapper.TABALE.get("c5_site_summary") + " ";
			mdt = template.getMDTofQueryWithWebmartID(webmartID, query);
			if (mdt.getRowCount() < 1) {
				rmd.exception("Data not Found in Database : Query : " + query.toString() + " : Live Month : "
						+ livemonth + " : Live year : " + liveyear);
			}
			JsonObjectBuilder daily = cgl.getDrillDownJsonFormatSiteSummary(mdt, monthList, drillDownMonthlist,
					drillDownYearlist);
			jsonobj.add("drilldown", daily);
			result = jsonobj.build().toString();
			redis.setValueToRedisWithKey(webmartID + "_site_summary", result);
		}

		rmd.log("webmartID :- " + webmartID);
		return result;
	}

	public String getCounter5LimitExceededGraphData(int webmartID) throws Exception {
		PublisherSettings ps = new PublisherSettings(rmd);
		int year = ps.getLiveYear(webmartID);
		int month = ps.getPubLiveMonth(webmartID, year);
		String publisher = ps.getPublisherCode(webmartID);
		Redis redis = new Redis();
		String result = null;
		result = redis.getValueFromRedisWithKey(webmartID + "_limit_exceeded");
		if (null == result) {
			JsonObjectBuilder jsonobj = cgl.getRequestSummary(publisher, year, month);
			result = jsonobj.build().toString();
			redis.setValueToRedisWithKey(webmartID + "_limit_exceeded", result);
		}

		rmd.log("webmartID :- " + webmartID);
		return result;
	}

	public String getCounter5RequestSummaryGraphData(int webmartID) throws Exception {
		PublisherSettings ps = new PublisherSettings(rmd);
		int year = ps.getLiveYear(webmartID);
		int month = ps.getPubLiveMonth(webmartID, year);
		String publisher = ps.getPublisherCode(webmartID);
		Redis redis = new Redis();
		String result = null;
		result = redis.getValueFromRedisWithKey(webmartID + "_request_summary");
		if (null == result) {
			JsonObjectBuilder jsonobj = cgl.getRequestSummary(publisher, year, month);
			result = jsonobj.build().toString();
			redis.setValueToRedisWithKey(webmartID + "_request_summary", result);
		}

		rmd.log("webmartID :- " + webmartID);
		return result;
	}

	// change by satyam 26/02/2019
	public String getQueryNoLicence(int month, int year) {
		DynamicMonthCreater dmc = new DynamicMonthCreater();
		String monthQuery = dmc.createSumMonthQueryC5(month, year, 1, year);
		monthQuery = monthQuery.substring(0, monthQuery.lastIndexOf(","));
		String query = "SELECT data_type, " + monthQuery + " FROM `" + TableMapper.TABALE.get("master_report_table")
				+ "` WHERE metric_type='No_License' AND institution_type IN('Institution','') GROUP BY data_type "
				+ "UNION ALL " + "SELECT data_type, " + monthQuery + " FROM `"
				+ TableMapper.TABALE.get("dr_master_table")
				+ "` WHERE metric_type='No_License' AND institution_type IN('Institution','') GROUP BY data_type";

		return query;
	}

	// added by satyam
	public String getQueryCountry(int month, int year, String publisher) {
		String query = " select  `country` ,M_" + year + "" + (month > 9 ? month : "0" + month)
				+ " as `count` from `c5_daskboard_country_high_ip`" + " where publisher ='" + publisher
				+ "' order by `count` desc";
		return query;
	}

	public String getRequestQuery(int month, int year) {
		String groupby = " GROUP BY data_type,Access_type, Metric_type ";
		DynamicMonthCreater dmc = new DynamicMonthCreater();
		String monthQuery = dmc.createSumMonthQueryC5(month, year, 1, year);
		monthQuery = monthQuery.substring(0, monthQuery.lastIndexOf(","));
		String totalQuery = dmc.createTotalMonthSumQueryC5(month, year, 1, year);
		String query = title + " " + totalQuery + " " + monthQuery + " " + from + " "
				+ TableMapper.TABALE.get("tr_master_table") + " " + where + "" + groupby + "UNION ALL " + title + " "
				+ totalQuery + " " + monthQuery + from + "" + TableMapper.TABALE.get("dr_master_table") + " " + where
				+ "" + groupby + "UNION ALL " + title + " " + totalQuery + " " + monthQuery + from + " pr_master "
				+ where + "" + groupby;

		return query;
	}

	public String getRequestQueryDashboardAll(int month, int year) {
		DynamicMonthCreater dmc = new DynamicMonthCreater();
		String monthQuery = dmc.createSumMonthQueryC5(month, year, 1, year);
		monthQuery = monthQuery.substring(0, monthQuery.lastIndexOf(","));
		String query = "SELECT master_report AS `TYPE`, " + monthQuery + " FROM `"
				+ TableMapper.TABALE.get("c5_dashboard_all") + "` WHERE  " + "metric_type='Unique_Title_Requests' "
				+ "AND data_category='ALL User' GROUP BY master_report";
		/*
		 * String query = title + " " + totalQuery + " " + monthQuery + " " +
		 * from + " tr_master " + where + "UNION ALL " + title + " " +
		 * totalQuery + " " + monthQuery + from + " dr_master " + where +
		 * "UNION ALL " + title + " " + totalQuery + " " + monthQuery + from +
		 * " pr_master " + where;
		 */
		return query;
	}

	public String getSiteSummaryC5GraphData(int webmartID) throws Exception {
		QueryTemplate template = new QueryTemplate(rmd);
		PublisherSettings ps = new PublisherSettings(rmd);
		String result = null;
		int year = ps.getLiveYear(webmartID);
		int month = ps.getPubLiveMonth(webmartID, year);
		Redis redis = new Redis();
		result = redis.getValueFromRedisWithKey(webmartID + "_site_summary_yearly");
		if (null == result) {
			MyDataTable mdt = null;
			String query = getSiteSummaryC5Query(webmartID, month, year);
			mdt = template.getMDTofQueryWithWebmartID(webmartID, query);
			if (mdt.getRowCount() < 1) {
				rmd.exception("Data not Found in Database : Query : " + query + " : Live Month : " + rmd.getLiveMonth()
						+ " : Live year : " + rmd.getLiveYear());
			}
			result = cgl.getSiteSummaryC5(mdt, year, month, 0);
			redis.setValueToRedisWithKey(webmartID + "_site_summary_yearly", result);
		}
		rmd.log("webmartID :- " + webmartID);
		return result;
	}

	public String getSiteSummaryC5Query(int webmart_id, int month, int year) {
		String query = "";
		StringBuilder tempquery = new StringBuilder();
		tempquery.append("select `TYPE`,");
		DynamicDayMonthCreater dmc = new DynamicDayMonthCreater();
		int y = 0;
		if (webmart_id == 2107) {
			y = 2019;
		} else {
			y = 2015;
		}

		for (int i = y; i <= year; i++) {
			int monthtemp = 12;
			if (i == year) {
				monthtemp = month;
			}
			for (int j = 1; j <= monthtemp; j++) {
				String monthzero = "";
				if (j < 10) {
					monthzero = "0";
				}
				int temp = dmc.getNumberofDay(j, i);
				for (int k = 1; k <= temp; k++) {
					String dayzero = "";
					if (k < 10) {
						dayzero = "0";
					}
					tempquery.append("D_" + i + monthzero + j + dayzero + k + " as `" + i + "-" + monthzero + j + "-"
							+ dayzero + k + "`,");
				}
			}

		}
		query = tempquery.substring(0, tempquery.length() - 1);
		query = query + " from " + TableMapper.TABALE.get("c5_site_summary");
		return query;
	}

	public String consolidateDatabaseSummary(int webmartID) throws Exception {
		PublisherSettings ps = new PublisherSettings(rmd);
		int liveyear = ps.getLiveYear(webmartID);
		int livemonth = ps.getPubLiveMonth(webmartID, liveyear);
		String publisher = ps.getPublisherCode(webmartID);
		String fromdate = "2019-10-01";
		String todate = liveyear + "-" + (livemonth < 10 ? "0" + livemonth : livemonth) + "-01";
		LocalDate start = LocalDate.parse(fromdate);
		LocalDate end = LocalDate.parse(todate);
		List<String> monthList = new ArrayList<>();
		StringBuilder monthtemp = new StringBuilder();
		String monthFinal = null;
		String result = null;
		try {
			while (!start.isAfter(end)) {
				String[] sp = start.toString().split("-");
				int day = Integer.parseInt(sp[2]);
				int month = Integer.parseInt(sp[1]);
				int year = Integer.parseInt(sp[0]);
				monthtemp.append("SUM(M_");
				monthtemp.append(year + (month < 10 ? "0" + month : "" + month));
				monthtemp.append(") AS `" + InsightConstant.MONTH_ARRAY[month] + "`,");
				monthList.add(InsightConstant.MONTH_ARRAY[month] + "-" + year + "");
				start = start.plusMonths(1);
			}
			monthFinal = monthtemp.substring(0, monthtemp.lastIndexOf(","));
			MyDataTable mdt = cgl.consolidateDatabaseQuery(publisher, monthFinal);
			JsonObjectBuilder jsonobj = cgl.consolidateDatabaseJsonFormate(mdt, monthList);
			result = jsonobj.build().toString();

		} catch (Exception e) {
			rmd.exception(e.toString());
		}
		return result;
	}

}