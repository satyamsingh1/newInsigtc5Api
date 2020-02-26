package com.mps.insight.product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;

public class SiteSummary {

	// private static final Logger log =
	// LoggerFactory.getLogger(SiteSummary.class);
	private RequestMetaData rmd;

	public SiteSummary(RequestMetaData rmd) {
		this.rmd = rmd;
	}

	String jsonData = "";
	ResponseBuilder rb = null;
	InsightDAO insightDao = null;
	MyDataTable mdt = null;
	JsonArray jarray = null;
	JsonObject jsonRecords = null;
	PublisherSettings pubsetting = null;

	public JsonArray getFullTextDetail(String webmartCode, int month, int year) {
		// String publisher_name;
		try {
			// pubsetting=new PublisherSettings(rmd);
			// publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao = InsightDAO.getInstance(webmartCode);
			rmd.log("SiteSummary : getFullTextDetail for Webmart =" + webmartCode);
			int preMonth = 0;
			int preyear = 0;
			if (month == 1) {
				preMonth = 12;
				preyear = year - 1;
			} else {
				preMonth = month - 1;
				preyear = year;
			}
			// executing Query
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT SUM(hits) AS 'HITS', SUM(CASE WHEN full_text IS NULL THEN 0 ELSE ")
					.append("full_text END)+SUM(CASE WHEN full_text_book IS NULL THEN 0 ELSE ")
					.append("full_text_book END) AS 'FULL-TEXT(ALL USERS)',SUM(searches_run) AS 'DATABASES(ALL USERS)',")
					.append("SUM(full_text) AS 'JOURNALS', SUM(full_text_book) AS 'BOOKS',SUM(page_views) AS 'PAGE VIEWS',")
					.append("SUM(searches_run) AS 'SEARCHES',SUM(visits) AS 'VISIT', `YEAR`, `MONTH` ")
					.append("FROM site_summary WHERE webmart_code = '" + webmartCode + "' AND YEAR IN ")
					.append("(" + year + "," + preyear + ") AND MONTH IN (" + month + "," + preMonth
							+ ") GROUP BY `year`, `month` ORDER BY YEAR DESC, MONTH DESC LIMIT 2");

			// Print Query
			if (stb.length() > 200) {
				rmd.log("getFullTextDetail : Query =" + stb.substring(0, 199) + " : Success");
			} else {
				rmd.log("getFullTextDetail : Query =" + stb.toString() + " : Success");
			}

			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			if (mdt.getRowCount() < 1) {
				rmd.exception("Data not Found in Database : Query : " + stb.toString() + " : Live Month : "
						+ rmd.getLiveMonth() + " : Live year : " + rmd.getLiveYear());
			}
			jarray = mdt.getJsonData();
		} catch (Exception e) {
			rmd.exception("Exception in getFullTextDetail... ");
		} finally {

			/*
			 * if (insightDao != null) { insightDao.disconnect(); } insightDao =
			 * null;
			 */

			mdt = null;
		}
		return jarray;
	}

	public JsonArray getPageViewDetail(String webmartCode, int month, int year) {
		try {
			insightDao = InsightDAO.getInstance(webmartCode);

			// executing Query
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT SUM(page_views) AS pageView,SUM(visits) AS visit,SUM(searches_run) AS searches FROM ")
					.append("site_summary WHERE webmart_code= '" + webmartCode + "' AND YEAR= " + year + " AND MONTH ="
							+ month);
			// Print Query
			if (stb.length() > 200) {
				rmd.log("Page View Query : Query =" + stb.substring(0, 199) + " : Success");
			} else {
				rmd.log("Page View Query : Query =" + stb.toString() + " : Success");
			}

			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			if (mdt.getRowCount() < 1) {
				rmd.exception("Data not Found in Database : Query : " + stb.toString() + " : Live Month : "
						+ rmd.getLiveMonth() + " : Live year : " + rmd.getLiveYear());
			}
			jarray = mdt.getJsonData();
		} catch (Exception e) {
			rmd.exception("Exception in getFullTextDetail... ");
		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); } insightDao =
			 * null;
			 */

			mdt = null;
		}
		return jarray;
	}

	public JsonObject getFullTextProgressTable(int webmartID, String productType, String product, int monthCount,
			int liveYear, int liveMonth) throws Exception {

		String t1 = "";
		String t2 = "";
		HashMap<Integer, String> InstProgessMonthMap = new HashMap<Integer, String>();
		String publisher_name;
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao = rmd.getInsightDao();
			// check for valid product and product type

			rmd.log("getFullTextProgressTable : webmartID =" + webmartID);
			InstProgessMonthMap.put(-11, "`Jan_p`");
			InstProgessMonthMap.put(-10, "`Feb_p`");
			InstProgessMonthMap.put(-9, "`Mar_p`");
			InstProgessMonthMap.put(-8, "`Apr_p`");
			InstProgessMonthMap.put(-7, "`May_p`");
			InstProgessMonthMap.put(-6, "`Jun_p`");
			InstProgessMonthMap.put(-5, "`Jul_p`");
			InstProgessMonthMap.put(-4, "`Aug_p`");
			InstProgessMonthMap.put(-3, "`Sep_p`");
			InstProgessMonthMap.put(-2, "`Oct_p`");
			InstProgessMonthMap.put(-1, "`Nov_p`");
			InstProgessMonthMap.put(0, "`Dec_p`");
			InstProgessMonthMap.put(1, "`Jan`");
			InstProgessMonthMap.put(2, "`Feb`");
			InstProgessMonthMap.put(3, "`Mar`");
			InstProgessMonthMap.put(4, "`Apr`");
			InstProgessMonthMap.put(5, "`May`");
			InstProgessMonthMap.put(6, "`Jun`");
			InstProgessMonthMap.put(7, "`Jul`");
			InstProgessMonthMap.put(8, "`Aug`");
			InstProgessMonthMap.put(9, "`Sep`");
			InstProgessMonthMap.put(10, "`Oct`");
			InstProgessMonthMap.put(11, "`Nov`");
			InstProgessMonthMap.put(12, "`Dec`");

			// validating year
			if (liveYear <= 0 || liveYear >= 2050 || ("" + liveYear).length() != 4) {
				throw new Exception("invalid liveYear : " + liveYear);
			}

			// validating Month
			if (liveMonth <= 0 || liveMonth > 12) {
				throw new Exception("invalid liveMonth : " + liveYear);
			}

			// validating Month Count, 2 4 , 6 quarter, yearly
			if (monthCount <= 0 || monthCount > 12) {
				throw new Exception("invalid monthCount : " + monthCount);
			}

			//
			int counter = 0;
			for (counter = 0; counter < (monthCount * 2); counter++) {
				int month = liveMonth - counter;
				if (counter < monthCount) {
					t1 = InstProgessMonthMap.get(month) + " + " + t1;
				} else {
					t2 = InstProgessMonthMap.get(month) + " + " + t2;
				}
			}
			// removinfg last extra + char
			t1 = t1.substring(0, t1.length() - 2); // latest months
			t2 = t2.substring(0, t2.length() - 2); // previous months
			// condition for search all the product listed in database
			String productSearchCondition = "";

			// check product TYPE

			if (product.equalsIgnoreCase("ALL")) {
				productSearchCondition = " AND product_type LIKE'%%' AND product LIKE '%%' ";
			} else {
				if (product.equalsIgnoreCase("DATABASE")) {
					product = "";
				} else if (product.equalsIgnoreCase("FULL-TEXT")) {
					product = "";
				}
				productSearchCondition = " AND product_type LIKE'%" + productType + "%' AND product LIKE '%" + product
						+ "%' ";
			}

			rmd.log("before Query : productSearchCondition =" + productSearchCondition);
			// insightDao = InsightDAO.getInstance();
			StringBuilder stb = new StringBuilder();
			stb.append(
					"SELECT * FROM (SELECT institution_id AS `Inst./Site ID`, institution_name AS `Inst./Site Name`,")
					.append(" product AS Product, product_type AS `Product Type`,")
					.append("FLOOR((" + t1 + ") - (" + t2 + "))/(" + t1 + "  + " + t2 + ")*100 AS `Difference(%)`, ")
					// .append(", page_type as `Page Type`,title as `Title`, ")
					.append("(" + t2 + ") AS '(" + t2.replaceAll("`", "") + ")', (" + t1 + ")  AS '("
							+ t1.replaceAll("`", "") + ")', ((" + t1 + ") - ")
					.append("(" + t2 + ")) AS `Diff`,(" + t1 + "  + " + t2 + " ) AS `Total`")
					.append(" FROM  dashboard_institution_progress_detail_v2  WHERE webmart_id=" + webmartID
							+ " AND YEAR=" + liveYear + " AND MONTH=" + liveMonth)
					.append(productSearchCondition + " AND  (" + t1 + ") > 30  AND  (" + t2
							+ " ) >=0 ORDER BY `Difference(%)` DESC LIMIT 5) incr ")
					.append("UNION ALL ")
					.append("SELECT * FROM (SELECT institution_id AS `Inst./Site ID`, institution_name AS `Inst./Site Name`,")
					.append(" product AS Product, product_type AS `Product Type`,")
					.append("FLOOR((" + t1 + ") - (" + t2 + "))/(" + t1 + "  + " + t2 + ")*100 AS `Difference(%)` , ")
					// .append(", page_type as `Page Type`,title as `Title`, ")
					.append("(" + t2 + "), (" + t1 + "), ((" + t1 + ") - ")
					.append("(" + t2 + ")) AS `Diff`,(" + t1 + "  + " + t2 + ") AS `Total`")
					.append(" FROM  dashboard_institution_progress_detail_v2  WHERE webmart_id=" + webmartID
							+ " AND YEAR=" + liveYear + " AND MONTH=" + liveMonth)
					.append(productSearchCondition + " AND  (" + t1 + ") > 30  AND  (" + t2
							+ " ) >=0 ORDER BY `Difference(%)` ASC LIMIT 5) decr");

			// Print Query
			if (stb.length() > 200) {
				rmd.log("getFullTextProgressTable : Query =" + stb.substring(0, 199) + " : Success");
			} else {
				rmd.log("getFullTextProgressTable : Query =" + stb.toString() + " : Success");
			}

			try {

				mdt = insightDao.executeSelectQueryMDT(stb.toString());

				if (mdt.getRowCount() < 1) {
					rmd.exception("Data not Found in Database : Query : " + stb.toString() + " : Live Month : "
							+ liveMonth + " : Live year : " + liveYear);
				}

				jsonRecords = mdt.getJson();

			} catch (Exception e) {
				rmd.exception("getFullTextProgressTable Json Data =" + jsonRecords.toString());
				rmd.exception("Exception in InstProgessDashboardDataGenerator : ");
			}

		} catch (Exception e) {
			rmd.exception("Exception in InstProgessDashboardDataGenerator : ");

		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			mdt = null;
		}

		return jsonRecords;
	}

	public JsonObject getHighestAccessedIP(int webmartID, int rowCount, int livemonth, int liveyear) {
		String publisher_name;
		StringBuilder stb = new StringBuilder();
		StringBuilder monthAlias = new StringBuilder();
		StringBuilder monthTotal = new StringBuilder();
		List<String> monthASList = new ArrayList<>();
		List<String> monthTotalList = new ArrayList<>();
		String fromdate = liveyear + "-" + (livemonth < 10 ? "0" + livemonth : livemonth) + "-01";
		LocalDate start = LocalDate.parse(fromdate);
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao = rmd.getInsightDao();
			rmd.log("getHighestAccessedIP : webmartID=" + webmartID);
			String monthList = "";
			String monthAdd = "";
			for (int i = 0; i < 12; i++) {
				String[] sp = start.minusMonths(i).toString().split("-");
				int day = Integer.parseInt(sp[2]);
				int month = Integer.parseInt(sp[1]);
				int year = Integer.parseInt(sp[0]);
				String date = "M_" + year + (month < 10 ? "0" + month : month);
				monthTotalList.add(date);
				monthASList.add(date + " as `" + InsightConstant.MONTH_ARRAY[month] + "-" + year + "`");
			}
			Collections.reverse(monthTotalList);
			Collections.reverse(monthASList);
			monthTotalList.forEach(month -> monthTotal.append(month + "+"));
			monthASList.forEach(month -> monthAlias.append(month + ","));
			monthAdd = monthTotal.substring(0, monthTotal.lastIndexOf("+"));
			monthList = monthAlias.substring(0, monthAlias.lastIndexOf(","));
			stb.append(
					"SELECT ip_address as `IP ADDRESS`,institution_id as `INST. ID`,institution_name as `INST. NAME`,("
							+ monthAdd + ") AS `YTD`, " + monthList)
					.append(" FROM `c5_daskboard_highest_ip` WHERE publisher='" + publisher_name
							+ "' ORDER BY `YTD` DESC LIMIT " + rowCount);
			// Print Query
			if (stb.length() > 200) {
				rmd.log("getHighestAccessedIP : Query =" + stb.substring(0, 199) + " : Success");
			} else {
				rmd.log("getHighestAccessedIP : Query =" + stb.toString() + " : Success");
			}
			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			if (mdt.getRowCount() < 1) {
				rmd.exception("Data not Found in Database : Query : " + stb.toString() + " : Live Month : " + livemonth
						+ " : Live year : " + liveyear);
			}
			jsonRecords = mdt.getJson();

		} catch (Exception e) {
			rmd.log("getHighestAccessedIP : Json Data =" + jsonRecords.toString());
			rmd.exception("Exception in getHighestAccessedIP...");
		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			mdt = null;
		}
		return jsonRecords;
	}

	public JsonObject getUnidentifiedIP(int webmartID, int rowCount, int livemonth, int liveyear) {
		String publisher_name;
		StringBuilder monthAlias = new StringBuilder();
		StringBuilder monthTotal = new StringBuilder();
		List<String> monthASList = new ArrayList<>();
		List<String> monthTotalList = new ArrayList<>();
		String fromdate = liveyear + "-" + (livemonth < 10 ? "0" + livemonth : livemonth) + "-01";
		LocalDate start = LocalDate.parse(fromdate);
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao = rmd.getInsightDao();
			rmd.log("getUnidentifiedIP : webmartID=" + webmartID);

			String monthList = "";
			String monthAdd = "";
			StringBuilder stb = new StringBuilder();

			for (int i = 0; i < 12; i++) {
				String[] sp = start.minusMonths(i).toString().split("-");
				int day = Integer.parseInt(sp[2]);
				int month = Integer.parseInt(sp[1]);
				int year = Integer.parseInt(sp[0]);
				String date = "M_" + year + (month < 10 ? "0" + month : month);
				monthTotalList.add(date);
				monthASList.add(date + " as `" + InsightConstant.MONTH_ARRAY[month] + "-" + year + "`");
			}
			Collections.reverse(monthTotalList);
			Collections.reverse(monthASList);
			monthTotalList.forEach(month -> monthTotal.append(month + "+"));
			monthASList.forEach(month -> monthAlias.append(month + ","));
			monthAdd = monthTotal.substring(0, monthTotal.lastIndexOf("+"));
			monthList = monthAlias.substring(0, monthAlias.lastIndexOf(","));
			stb.append(
					"SELECT ip_address as `IP ADDRESS`,country as `COUNTRY`,(" + monthAdd + ") AS `YTD`, " + monthList)
					.append(" FROM `c5_daskboard_un_identified_fulltext` WHERE publisher='" + publisher_name
							+ "' ORDER BY `YTD` DESC LIMIT " + rowCount);

			// Print Query
			if (stb.length() > 200) {
				rmd.log("getUnidentifiedIP : Query =" + stb.substring(0, 199) + " : Success");
			} else {
				rmd.log("getUnidentifiedIP : Query =" + stb.toString() + " : Success");
			}

			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			if (mdt.getRowCount() < 1) {
				rmd.exception("Data not Found in Database : Query : " + stb.toString() + " : Live Month : " + livemonth
						+ " : Live year : " + liveyear);
			}

			jsonRecords = mdt.getJson();
			// jsonRecords.put

		} catch (Exception e) {
			rmd.log("getUnidentifiedIP : Json Data =" + jsonRecords.toString());
			rmd.exception("Exception in getUnidentifiedIP... ");
		} finally {

			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			mdt = null;
		}
		return jsonRecords;
	}

	public JsonObject getDenielList(int webmartID, int rowCount, int month, int year) {
		String publisher_name;
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao = rmd.getInsightDao();
			rmd.log("getDenielList : webmartID=" + webmartID);

			String monthList = "";
			String monthAdd = "";

			for (int i = 1; i <= month; i++) {
				monthList = monthList + InsightConstant.MONTH_ARRAY_DASH[i] + ",";
				monthAdd = monthAdd + InsightConstant.MONTH_ARRAY_DASH[i] + "+";
			}

			monthAdd = monthAdd.substring(0, monthAdd.lastIndexOf("+"));
			monthList = monthList.substring(0, monthList.lastIndexOf(","));
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT `institution_id` as `Inst./Site ID`,`institution_name` as `Inst./Site Name`, ("
					+ monthAdd + ") AS `YTD`, " + monthList)
					.append(" FROM `dashboard_licence_denial_v2` WHERE webmart_id=" + webmartID + " and year=" + year
							+ " and month=" + month + " ORDER BY `YTD` DESC LIMIT " + rowCount);

			// Print Query
			if (stb.length() > 200) {
				rmd.log("getDenielList : Query =" + stb.substring(0, 199) + " : Success");
			} else {
				rmd.log("getDenielList : Query =" + stb.toString() + " : Success");
			}

			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jsonRecords = mdt.getJson();
			// jsonRecords.put("headername", "Identified Licence Denial (
			// Jan-2018 )");

		} catch (Exception e) {
			rmd.exception("getDenielList : Json Data =" + jsonRecords.toString());
			rmd.exception("Exception in getDenielList... ");
		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			mdt = null;
		}
		return jsonRecords;
	}

	public JsonObject getFullTextProgressTableMore(int webmartID, String productType, String product, int duration,
			int liveYear, int liveMonth, String order) {
		String t1 = "";
		String t2 = "";
		HashMap<Integer, String> InstProgessMonthMap = new HashMap<Integer, String>();
		String publisher_name;
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao = rmd.getInsightDao();
			// check for valid product and product type

			rmd.log("getFullTextProgressTableMore : webmartID =" + webmartID);
			InstProgessMonthMap.put(-11, "`Jan_p`");
			InstProgessMonthMap.put(-10, "`Feb_p`");
			InstProgessMonthMap.put(-9, "`Mar_p`");
			InstProgessMonthMap.put(-8, "`Apr_p`");
			InstProgessMonthMap.put(-7, "`May_p`");
			InstProgessMonthMap.put(-6, "`Jun_p`");
			InstProgessMonthMap.put(-5, "`Jul_p`");
			InstProgessMonthMap.put(-4, "`Aug_p`");
			InstProgessMonthMap.put(-3, "`Sep_p`");
			InstProgessMonthMap.put(-2, "`Oct_p`");
			InstProgessMonthMap.put(-1, "`Nov_p`");
			InstProgessMonthMap.put(0, "`Dec_p`");
			InstProgessMonthMap.put(1, "`Jan`");
			InstProgessMonthMap.put(2, "`Feb`");
			InstProgessMonthMap.put(3, "`Mar`");
			InstProgessMonthMap.put(4, "`Apr`");
			InstProgessMonthMap.put(5, "`May`");
			InstProgessMonthMap.put(6, "`Jun`");
			InstProgessMonthMap.put(7, "`Jul`");
			InstProgessMonthMap.put(8, "`Aug`");
			InstProgessMonthMap.put(9, "`Sep`");
			InstProgessMonthMap.put(10, "`Oct`");
			InstProgessMonthMap.put(11, "`Nov`");
			InstProgessMonthMap.put(12, "`Dec`");

			// validating year
			if (liveYear <= 0 || liveYear >= 2050 || ("" + liveYear).length() != 4) {
				throw new Exception("invalid liveYear : " + liveYear);
			}

			// validating Month
			if (liveMonth <= 0 || liveMonth > 12) {
				throw new Exception("invalid liveMonth : " + liveYear);
			}

			// validating Month Count, 2 4 , 6 quarter, yearly
			if (duration <= 0 || duration > 12) {
				throw new Exception("invalid monthCount : " + duration);
			}

			//
			int counter = 0;
			for (counter = 0; counter < (duration * 2); counter++) {
				int month = liveMonth - counter;
				if (counter < duration) {
					t1 = t1 + InstProgessMonthMap.get(month) + " + ";
				} else {
					t2 = t2 + InstProgessMonthMap.get(month) + " + ";
				}
			}
			// removinfg last extra + char
			t1 = t1.substring(0, t1.length() - 2); // latest months
			t2 = t2.substring(0, t2.length() - 2); // previous months
			// condition for search all the product listed in database
			String productSearchCondition = "";

			// check product TYPE

			if (product.equalsIgnoreCase("ALL")) {
				productSearchCondition = " AND product_type LIKE'%%' AND product LIKE '%%' ";
			} else {
				productSearchCondition = " AND product_type LIKE'%" + productType + "%' AND product LIKE '%" + product
						+ "%' ";
			}

			rmd.log("before Query : productSearchCondition =" + productSearchCondition);
			// insightDao = InsightDAO.getInstance();
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT institution_id AS `Inst./Site ID`, institution_name AS `Inst./Site Name`,")
					.append("product AS Product, product_type AS `Product Type`, FLOOR((" + t1 + "  ) ")
					.append("- (" + t2 + "  ))/(" + t1 + "  + " + t2 + " )*100 AS `Difference(%)`")
					.append(" FROM  dashboard_institution_progress_detail_v2  WHERE webmart_id=" + webmartID
							+ " AND YEAR=" + liveYear + " AND MONTH=" + liveMonth + " " + productSearchCondition)
					.append(" AND  (" + t1 + "  ) > 30  AND  (" + t2 + "  ) >=0 ORDER BY `Difference(%)` " + order);

			// Print Query
			if (stb.length() > 200) {
				rmd.log("getFullTextProgressTableMore : Query =" + stb.substring(0, 199) + " : Success");
			} else {
				rmd.log("getFullTextProgressTableMore : Query =" + stb.toString() + " : Success");
			}

			try {

				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				jsonRecords = mdt.getJson();

			} catch (Exception e) {
				rmd.exception("getFullTextProgressTable Json Data =" + jsonRecords.toString());
				rmd.exception(" Exception in InstProgessDashboardDataGenerator : ");
			}

		} catch (Exception e) {
			rmd.exception(" Exception in InstProgessDashboardDataGenerator : ");

		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			mdt = null;
		}

		return jsonRecords;
	}

	public MyDataTable getUserTechDetail(int webmartID, int year, int month) {
		String publisher_name;
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao = rmd.getInsightDao();

			// executing Query
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT device, os, browser, SUM(`" + InsightConstant.MONTH_ARRAY[month].toLowerCase()
					+ "`) AS total FROM dashboard_user_technology_analysis_v2 ")
					.append("WHERE webmart_id ='" + webmartID + "' AND MONTH='" + month + "' AND YEAR='" + year)
					.append("' GROUP BY device, os, browser ORDER BY device, os, browser");

			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			if (mdt.getRowCount() < 1) {
				rmd.exception("Data not Found in Database : Query : " + stb.toString() + " : Live Month : " + month
						+ " : Live year : " + year);
			}

			// Print Query
			if (stb.length() > 200) {
				rmd.log("Tech Detail Query  : Query  =" + stb.toString().substring(0, 199) + " : Query : Success "
						+ " MDT size :" + mdt.getRowCount());
			} else {
				rmd.log("Tech Detail Query  : Query  =" + stb.toString() + " : Query : Success " + " MDT size :"
						+ mdt.getRowCount());
			}

		} catch (Exception e) {
			rmd.exception("Exception in getFullTextDetail... ");
		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); } insightDao =
			 * null;
			 */
		}
		return mdt;
	}

	public JsonObject getPubDashBoardView(int webmartID) {
		JsonObject jo = null;
		String publisher_name;
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao = rmd.getInsightDao();

			// executing Query
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT d.component,CASE WHEN d.state=1 THEN TRUE ELSE FALSE END AS `status` ")
					.append("FROM dashboard_component_master d WHERE webmart_id=" + webmartID);

			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jo = mdt.getJson();

			// Print Query
			if (stb.length() > 200) {
				rmd.log("getPubDashBoardView  : Query  =" + stb.toString().substring(0, 199)
						+ " : Query : Success : MDT Size : " + mdt.getRowCount());
			} else {
				rmd.log("getPubDashBoardView : Query  =" + stb.toString() + " : Query : Success : MDT Size : "
						+ mdt.getRowCount());
			}

		} catch (Exception e) {
			rmd.exception("Exception in getFullTextDetail... ");
		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); } insightDao =
			 * null;
			 */
		}
		return jo;
	}
	// ******************************new method***************

	public JsonObject getPubDashBoarRecordByid(int webmartID, int liveYear, int liveMonth) {
		JsonObject jo = null;
		String publisher_name;
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao = rmd.getInsightDao();

			// executing Query
			StringBuilder stb = new StringBuilder();
			/*
			 * stb.
			 * append("SELECT Institution_ID, Institution_Name, data_type, Metric_type, SUM(M_201808), SUM(M_201809)"
			 * ) .append("FROM master_report WHERE institution_id = '680' ")
			 * .append("GROUP BY Institution_ID, Institution_Name, data_type, Metric_type "
			 * ) .append("UNION  ALL")
			 * .append(" SELECT Institution_ID, Institution_Name, data_type, Metric_type, SUM(M_201808), SUM(M_201809)"
			 * ) .append("FROM dr_master WHERE institution_id = '680' ")
			 * .append("GROUP BY Institution_ID, Institution_Name, data_type, Metric_type"
			 * );
			 */
			// stb.append("SELECT Institution_ID, Institution_Name, data_type,
			// Metric_type,M_201808,M_201809 ")
			stb.append("SELECT Institution_ID, Institution_Name, data_type, Metric_type,M_" + liveYear + "0" + liveMonth
					+ "").append(" FROM `Institution_Summary` WHERE institution_id !=''");

			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			rmd.log("Tech Detail Query =" + stb.toString() + " : Query : Success " + " MDT size :" + mdt.getRowCount());
			jo = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("Exception in getInstitutionSummary... ");
		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); } insightDao =
			 * null;
			 */
		}
		return jo;
	}
	// *********************************************

}
