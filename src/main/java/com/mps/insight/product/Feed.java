package com.mps.insight.product;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserFavoriteDTO;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

/**
 * 
 * @author satyam
 *
 */
public class Feed {
	private RequestMetaData rmd;
	private static final Logger LOG = LoggerFactory.getLogger(Feed.class);
	ResponseBuilder rb = null;
	MyDataTable mdt = null;
	JsonArray jarray = null;
	JsonObject jsonRecord = null;
	PublisherSettings pubsetting = null;

	public Feed(RequestMetaData rmd) throws MyException {
		this.rmd = rmd;
	}

	public JsonArray getC5FeedList() throws Exception {
		InsightDAO insightDao = null;
		MyDataTable mdt = null;
		JsonArray jarray = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select feed_name from `c5_feed_statics` where status=1");
		insightDao = rmd.getInsightDao();
		mdt = insightDao.executeSelectQueryMDT(sb.toString());
		jarray = mdt.getJsonData();
		return jarray;
	}
	
	public JsonArray getFeedDateLiveDate(int liveMonth, int liveYear) {
		//String fromdate = "2020-01-01";
		String fromdate = liveYear+"-"+(liveMonth<10?"0"+liveMonth:liveMonth)+"-01";
		JsonArrayBuilder jr=Json.createArrayBuilder();
		LocalDate start = LocalDate.parse(fromdate);
		for (int i = 0; i < 6; i++) {
			String[] sp = start.minusMonths(i).toString().split("-");
			int month = Integer.parseInt(sp[1]);
			int year = Integer.parseInt(sp[0]);
			String completeDate=InsightConstant.MONTH_ARRAY[month].toUpperCase() + "-" + year;
			jr.add(completeDate);
			//System.out.println(InsightConstant.MONTH_ARRAY[month] + "-" + year);
		}
		return jr.build();
	}

	/**
	 * create by Satyam singh
	 * 
	 * @param feedType
	 * @param beginDate
	 * @param endDate
	 * @param webmartID
	 * @return InputStream
	 * @throws Exception
	 */
	public InputStream getC5FeedData(int webmartid, String feedType, String beginDate, String endDate)
			throws Exception {
		// endDate=JAN-2019 beginDate=FEB-2019
		InsightDAO insightDao = null;
		MyDataTable mdt = null;
		String datafile = "";
		String feedTypefinal = null;
		/***
		 * Here we store dynamic Month to use indexOf() to get month in number
		 * format Like JAN-> 1 , FEB->2
		 */
		List<String> monthList = new ArrayList<String>();
		monthList.add("");
		monthList.add("JAN");
		monthList.add("FEB");
		monthList.add("MAR");
		monthList.add("APR");
		monthList.add("MAY");
		monthList.add("JUN");
		monthList.add("JUL");
		monthList.add("AUG");
		monthList.add("SEP");
		monthList.add("OCT");
		monthList.add("NOV");
		monthList.add("DEC");

		String[] begin = beginDate.split("-");
		String[] end = endDate.split("-");
		int formYear = Integer.parseInt(begin[1]);
		int endYear = Integer.parseInt(end[1]);
		
		int fromMonth = monthList.indexOf(begin[0]);
		int toMonth = monthList.indexOf(end[0]);
		
		StringBuilder andsbmonthlist = new StringBuilder();
		StringBuilder casesbmonthlist = new StringBuilder();
		
		
		int monthloop = (endYear - formYear) * 12 + (toMonth - fromMonth + 1);
		int startMonth = fromMonth;
		
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				formYear = formYear + 1;
			
				casesbmonthlist.append("CASE WHEN M_" + formYear + (startMonth > 9 ? startMonth : "0" + startMonth)
						+ ">0 THEN 'Active ' ELSE 'Inactive' END AS M_" + formYear + (startMonth > 9 ? startMonth : "0" + startMonth) + ",");
				andsbmonthlist.append("  M_" + formYear + (startMonth > 9 ? startMonth : "0" + startMonth) + ">0 OR");

			} else {
				
				casesbmonthlist.append("CASE WHEN M_" + formYear + (startMonth > 9 ? startMonth : "0" + startMonth)
						+ ">0 THEN 'Active ' ELSE 'Inactive' END AS M_" + formYear + (startMonth > 9 ? startMonth : "0" + startMonth) + ",");
				andsbmonthlist.append("  M_" + formYear + (startMonth > 9 ? startMonth : "0" + startMonth) + ">0 OR");

				startMonth++;
			}

		}

		
		casesbmonthlist.deleteCharAt(casesbmonthlist.toString().lastIndexOf(","));
		StringBuilder finalQuery = new StringBuilder();
		/**
		 * InsightDAO.getInstance(publisher) we get connection
		 */
		insightDao = rmd.getInsightDao();
		String TableName = null;
		String TableCol = null;
		String Ip_TableCol=null;
		InputStream io = null;
		/**
		 * Title_TableCol variable use for all title feed column
		 */
		String Title_TableCol = "`TITLE_ID`,`TITLE`,`TITLE_TYPE`,`DATA_TYPE`,`ACCESS_TYPE`,`PUBLISHER`,`ISNI`,`PROPRIETARY_ID`,`PLATFORM`,`DOI`,`ISBN`,`PRINT_ISSN`,`ONLINE_ISSN`,`URI`,";
		/**
		 * Item_TableCol variable use for all item feed column
		 */
		String Item_TableCol = " `ITEM_ID`,`TITLE_ID`,`ITEM`,`PUBLICATION_DATE`,`ITEM_VERSION`,`SECTION_TYPE`,`ACCESS_TYPE`,`ISNI`,"
				+ "`PROPRIETARY_ID`,`PRINT_ISSN`,`ONLINE_ISSN`,`PUBLICATION_NO`,`ISSUE_VOLUME`,`ISSUE_NO`,`PAGE_NO`,`YOP`,"
				+ "`DOI`,`PUBLISHER`,`AUTHORS`,`URI`,";
		/**
		 * Account_TableCol variable use for all account feed column
		 */
		String Account_TableCol = "`ALPHABET`,`ACCOUNT_CODE`,`ACCOUNT_NAME`,`ACCOUNT_TYPE`,`PRODUCT_CODE`,`CONSORTIUM_INCL`,`TYPE`,`STATE`,"
				+ "`COUNTRY`,`REGION`,";
		/**
		 * Here we select feed type which is selected
		 */

		String Title_Consrtial_incl_Col = "`ACCOUNT_CODE`, `CONSORTIA_ACCOUNT_CODE`,";
		if (webmartid == 2107) {
			 Ip_TableCol = "`INSTITUTION_ID`,`INSTITUTION_NAME`,`ActualIP`,`IP_RANGE`,`START_IP`,`END_IP`,`Status`,";
		}
		else{
			 Ip_TableCol = "`INSTITUTION_ID`,`INSTITUTION_NAME`,`IP_RANGE`,`START_IP`,`END_IP`,`Status`,";
		}

		String database_subcription_TableCol = "`INSTITUTION_ID`,`INSTITUTION_NAME`,`ACCOUNT_TYPE`,`DATABASE_ID`,`PRODUCT_TYPE`,`Status`,";
		switch (feedType) {
		case "Database_subscription_feed":
			TableName = "`c5_database_subcription_feed`";
			TableCol = database_subcription_TableCol;
			feedTypefinal = "where status=1 ";
			break;
		case "Ip_feed":
			TableName = "`c5_ip_feed`";
			TableCol = Ip_TableCol;
			feedTypefinal = " where status=1 ";
			break;
		case "Journal_title_feed":
			TableName = "`c5_title_feed_master`";
			TableCol = Title_TableCol;
			feedTypefinal = " where data_type IN ('Journal','PROCEEDINGS','magazines','transactions','letters') ";
			break;
		case "Conference_title_feed":
			TableName = "`c5_title_feed_master`";
			TableCol = Title_TableCol;
			feedTypefinal = " where data_type='Conference'";
			break;
		case "Standard_title_feed":
			TableName = "`c5_title_feed_master`";
			TableCol = Title_TableCol;
			feedTypefinal = " where data_type='Standard'";
			break;
		case "Book_title_feed":
			TableName = "`c5_title_feed_master`";
			TableCol = Title_TableCol;
			feedTypefinal = " where data_type='Book'";
			break;
		case "Courses_title_feed":
			TableName = "`c5_title_feed_master`";
			TableCol = Title_TableCol;
			feedTypefinal = " where data_type='Courses'";
			break;
		case "Article_item_feed":
			TableName = "`c5_item_feed_master`";
			TableCol = Item_TableCol;
			feedTypefinal = " where section_type='Article' LIMIT 200000";
			break;
		case "Chapter_item_feed":
			TableName = "`c5_item_feed_master`";
			TableCol = Item_TableCol;
			feedTypefinal = " where section_type='Chapter' ";
			break;
		case "Consortia_accounts_feed":
			TableName = "`c5_accounts`";
			TableCol = Account_TableCol;
			feedTypefinal = " where  account_type='Consortia'";
			break;
		case "Institution_accounts_feed":
			TableName = "`c5_accounts`";
			TableCol = Account_TableCol;
			feedTypefinal = " where  account_type='Institution'";
			break;
		case "Dealers_accounts_feed":
			TableName = "`c5_accounts`";
			TableCol = Account_TableCol;
			feedTypefinal = " where  account_type='Dealers'";
			break;
		case "Region_accounts_feed":
			TableName = "`c5_accounts`";
			TableCol = Account_TableCol;
			feedTypefinal = " where  account_type='Region'";
			break;
		case "Database_title_feed":
			TableName = "`c5_title_feed_master`";
			TableCol = Title_TableCol;
			feedTypefinal = " where data_type='Database'";
			break;
		case "Consortium_Member_feed":
			TableName = "`c5_account_consoria_incl`";
			TableCol = Title_Consrtial_incl_Col;
			feedTypefinal = " where account_code !=''";
			break;
		default:
			break;
		}
		/**
		 * Here we create final
		 */
		finalQuery.append("select " + TableCol + " " + casesbmonthlist + " from " + TableName).append(feedTypefinal);

		mdt = insightDao.executeSelectQueryMDT(finalQuery.toString());
		/**
		 * getCsvDataWithQuote(",") this method to convert table data to CSV
		 * fromat
		 */
		datafile = mdt.getCsvDataWithQuote(",");
		io = new ByteArrayInputStream(datafile.getBytes());
		return io;
	}

	/**
	 * 
	 * @param webmartID
	 * @param allSetNo
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public JsonObject getFeeds(int webmartID, String allSetNo, String year, String month) throws Exception {
		InsightDAO insightDao = null;
		pubsetting = new PublisherSettings(rmd);
		insightDao = rmd.getInsightDao();
		// Map<String, String> feedMap = null;
		String[] setNo = null;
		String[] mon = null;
		StringBuilder sb = null;
		boolean preyear = false;
		String yeartemp = "";
		int i;
		try {
			sb = new StringBuilder();
			setNo = allSetNo.split(",");
			mon = month.split(",");

			if (mon[0].contains("12") || mon[1].contains("12")) {
				if (mon[1].contains("12") || mon[2].contains("12")) {
					preyear = true;
				}
			}

			for (i = 0; i < setNo.length; i++) {
				if (preyear && Integer.parseInt(mon[i].toString().trim()) > 10) {
					yeartemp = (Integer.parseInt(year) - 1) + "";

				} else {
					yeartemp = year;
				}
				if (setNo[i] != null)
					sb.append("MAX(CASE WHEN ff.set_no = ").append(setNo[i])
							.append(" THEN CONCAT_WS('/', ff.location, ff.file_name) END) AS `")
							.append(InsightConstant.MONTH_ARRAY[Integer.parseInt(mon[i].toString().trim())] + " "
									+ yeartemp.substring(2, yeartemp.length()))
							.append("`, ");
			}
			LOG.info("this is the query becoming " + sb);
			sb.substring(0, sb.length() - 2);
			LOG.info("this is the query becoming after removing ," + sb);
			// feedMap = new HashMap<String, String>();
			LOG.info("PublisherSettings : Publisher=" + webmartID + " : Connection : Success");
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT  d.data_value as `Feed Type`, " + sb.substring(0, sb.length() - 2)
					+ " FROM file_master fm " + " LEFT JOIN domain d ON fm.file_classifier = d.data_key "
					+ " LEFT JOIN feed_files ff ON d.data_key = ff.feed "
					+ " WHERE d.data_category='FEED_FILENAME' AND fm.webmart_id= " + webmartID
					+ " AND fm.type= 'Feeds' AND " + "ff.webmart_id = " + webmartID + " AND ff.set_no IN (" + allSetNo
					+ ") " + " GROUP BY d.data_value ");
			// rs= insightDao.executeSelectQuery(stb.toString());
			LOG.info("FeedMap :: =" + webmartID + " : Query : " + stb + " Success");
			// mdt = new MyDataTable(rs);
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jsonRecord = mdt.getJson();
		} catch (Exception e) {
			LOG.error("Exception in getting feeds  :: " + webmartID + allSetNo + " : " + e.getMessage());
		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			// insightDao = null;
		}
		return jsonRecord;
	}

	public String getMonthYear(int webmartID) throws Exception {
		InsightDAO insightDao = null;
		String monthYear = "";
		try {
			pubsetting = new PublisherSettings(rmd);
			insightDao = rmd.getInsightDao();
			LOG.info("PublisherSettings : Publisher=" + webmartID + " : Connection : Success");
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT MAX(CONCAT(r.year,r.month)) AS monthYear FROM feed_sets AS r WHERE r.webmart_id ="
					+ webmartID + " AND " + "(r.year< YEAR(NOW()) OR (r.year= YEAR(NOW()) AND r.month<=MONTH(NOW())))");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rows = mdt.getRowCount();
			if (rows <= 0) {
				throw new Exception("No Record Found : Month Year");
			}
			LOG.info("getMonthYear : webmart=" + webmartID + " : Query : Success");
			// setNo=Integer.parseInt(mdt.getValue(1, "setNo"));
			// rs= insightDao.executeSelectQuery(stb.toString());
			// while(rs.next())
			monthYear = mdt.getValue(1, "monthYear");
		} catch (Exception e) {
			LOG.error("Exception in getting live Year and month of publisher feed :: " + webmartID + " : "
					+ e.getMessage());
		} finally {

			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			// insightDao = null;

		}
		return monthYear;
	}

	public String getAllSetNo(int webmartID, String months, int year) throws Exception {
		StringBuilder setNo = new StringBuilder();
		InsightDAO insightDao = null;
		boolean preyear = false;
		int yeartemp = 0;
		try {
			pubsetting = new PublisherSettings(rmd);
			insightDao = rmd.getInsightDao();
			LOG.info("PublisherSettings : Publisher=" + webmartID + " : Connection : Success");
			StringBuilder stb = null;
			String[] montharr = months.split(",");

			if (montharr[0].contains("12") || montharr[1].contains("12")) {
				if (montharr[1].contains("12") || montharr[2].contains("12")) {
					preyear = true;
				}
			}
			for (String tempmonth : montharr) {
				if (preyear && Integer.parseInt(tempmonth.trim()) > 10) {
					yeartemp = year - 1;
					tempmonth = tempmonth.trim();
				} else {
					yeartemp = year;
					tempmonth = tempmonth.trim().length() == 2 ? tempmonth.trim() : "0" + tempmonth.trim();
					/*
					 * if (Integer.parseInt(tempmonth.trim()) == 10) { tempmonth
					 * = tempmonth.trim(); } else { tempmonth = "0" +
					 * tempmonth.trim(); }
					 */

				}
				stb = new StringBuilder();
				stb.append("SELECT GROUP_CONCAT(set_no) as setno FROM feed_sets WHERE webmart_id=" + webmartID
						+ " AND MONTH='" + tempmonth.trim() + "' and year=" + yeartemp
						+ " and Category='Monthly' order by setno");
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				if (mdt.getValue(1, "setno").length() < 2) {
					setNo.append("0,");
				} else {
					setNo.append(mdt.getValue(1, "setno") + ",");
				}
			}

			LOG.info("getAllSetNo : webmart=" + webmartID + " : Query : Success : set No : " + setNo);

		} catch (Exception e) {
			LOG.info("Exception in getting all set no. :: " + webmartID + "--" + months);
			e.printStackTrace();
		} finally {

			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			// insightDao = null;

		}
		return setNo.substring(0, setNo.lastIndexOf(","));
	}

	public JsonObject getFavoritesList(int webmartID) throws Exception {
		InsightDAO insightDao = null;
		StringBuilder stb = null;
		try {
			pubsetting = new PublisherSettings(rmd);
			insightDao = rmd.getInsightDao();
			stb = new StringBuilder();
			/*
			 * stb.append(
			 * "SELECT acc.code AS account_code, acc.name AS account_name, acc.type AS account_type, acc.set_no AS "
			 * + "set_no FROM "+TableMapper.TABALE.get("user_favorites_table")
			 * +" uf, accounts acc WHERE acc.code=uf.account_code AND uf.user_id IS NULL AND "
			 * + "uf.webmart_id=" + webmartID + " AND acc.set_no=" + setNo +
			 * " ORDER BY acc.name");
			 */

			/*
			 * stb.append(
			 * "SELECT acc.account_code AS account_code, acc.account_name AS account_name, acc.account_type AS account_type "
			 * +" FROM "+TableMapper.TABALE.get("user_favorites_table")
			 * +" uf, c5_accounts acc WHERE acc.account_code=uf."+TableMapper.
			 * TABALE.get("uf_account_code")+" AND uf."+TableMapper.TABALE.get(
			 * "uf_user_code")+" IS NULL AND " +
			 * " acc.status>=1   ORDER BY acc.account_name");
			 */

			stb.append(
					"SELECT acc.account_code AS account_code, acc.account_name AS account_name, acc.account_type AS account_type "
							+ " FROM " + TableMapper.TABALE.get("user_favorites_table")
							+ " uf, c5_accounts acc WHERE acc.account_code=uf."
							+ TableMapper.TABALE.get("uf_account_code") + " AND  "
							+ " acc.status>=1  AND acc.account_type  IN  ('Institution','Consortia', 'dealer', 'Region') AND uf.`level`='PUBLISHER' ORDER BY acc.account_name");

			LOG.info("Favourites List :: =" + webmartID + " : Query : " + stb + ": Success");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jsonRecord = mdt.getJson();
		} catch (Exception e) {
			LOG.error("Exception in getting Favourites List for :: " + webmartID + " : " + e.getMessage());
		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			// insightDao = null;
		}
		return jsonRecord;
	}

	public int getSetNo(int webmartID, int month, int year) throws Exception {
		InsightDAO insightDao = null;

		int setNo = 0;
		try {
			pubsetting = new PublisherSettings(rmd);
			insightDao = rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT set_no AS set_no FROM `feed_sets` WHERE webmart_id=" + webmartID + " AND YEAR=" + year
					+ " AND MONTH=" + month);
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rowCount = mdt.getRowCount();
			if (rowCount <= 0) {
				throw new Exception("No Record Found : Set Number");
			}
			LOG.info("getSetNo : webmart=" + webmartID + " : Query : Success");
			setNo = Integer.parseInt(mdt.getValue(1, "set_no"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			if (mdt != null) {
				mdt.destroy();
			}
			// insightDao = null;
		}
		return setNo;
	}

	public void addToFavorites(UserFavoriteDTO userFavorite) throws Exception {
		InsightDAO insightDao = null;
		Account account = null;
		boolean flag = false;
		DateFormat dateFormat;
		String updatedAt;
		try {
			pubsetting = new PublisherSettings(rmd);
			insightDao = rmd.getInsightDao();
			LOG.info("Start addToFavorites : Connection : Success");
			StringBuilder stb = new StringBuilder();

			dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			updatedAt = dateFormat.format(new Date(System.currentTimeMillis()));

			account = new Account(rmd);
			// Verify if Favorites account already exixt of user.
			flag = account.verifyFavoritesUser(userFavorite.getUserCode(), userFavorite.getAccountCode(),
					userFavorite.getWebmartId());
			if (flag == false) {
				stb.append("INSERT INTO " + TableMapper.TABALE.get("user_favorites_table") + " ("
						+ TableMapper.TABALE.get("uf_user_code") + ", " + TableMapper.TABALE.get("uf_account_code")
						+ ", updated_by, updated_at) ");
				stb.append("VALUES (");
				stb.append("'" + userFavorite.getUserCode() + "'");
				stb.append(", '" + userFavorite.getAccountCode() + "', '" + userFavorite.getUpdatedBy());
				stb.append("', '" + updatedAt + "')");
				// insightDao = InsightDAO.getInstance();
				insightDao.executeInsertUpdateQuery(stb.toString());
			}

		} catch (Exception e) {
			LOG.info("Exception in adding favorites");
			e.printStackTrace();
		} finally {

			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			// insightDao = null;
		}
	}

	public void removeFavorites(UserFavoriteDTO userFavorite) throws Exception {
		InsightDAO insightDao = null;
		Account account = null;
		boolean flag = false;
		try {
			pubsetting = new PublisherSettings(rmd);
			insightDao = rmd.getInsightDao();
			LOG.info("Start removeFavorites : Connection : Success");
			StringBuilder stb = new StringBuilder();
			account = new Account(rmd);
			// Verify if Favorites account already exixt of user.
			flag = account.verifyFavoritesUser(userFavorite.getUserCode(), userFavorite.getAccountCode(),
					userFavorite.getWebmartId());
			if (flag == true) {
				stb.append("DELETE FROM " + TableMapper.TABALE.get("user_favorites_table") + " WHERE "
						+ TableMapper.TABALE.get("uf_user_code") + " = '" + userFavorite.getUserCode() + "'"
						+ " AND account_code ='" + userFavorite.getAccountCode() + "'");
				// insightDao = InsightDAO.getInstance();
				insightDao.executeInsertUpdateQuery(stb.toString());
			}
		} catch (Exception e) {
			LOG.info("Exception in adding favorites");
			e.printStackTrace();
		} finally {

			/*
			 * if (insightDao != null) { insightDao.disconnect(); }
			 */
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			// insightDao = null;
		}
	}
	
	
}
