package com.mps.insight.product;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.AccountDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class Account {

	private RequestMetaData rmd = null;
	//private static final Logger log = LoggerFactory.getLogger(Account.class);

	String jsonData = "";
	ResponseBuilder rb = null;
	MyDataTable mdt = null;
	JsonArray jarray = null;
	JsonObject jsonRecords = null;
	List<String> accountsList = new ArrayList<String>();
	PublisherSettings pubsetting=null;

	public Account(RequestMetaData rmd) throws Exception {
		this.rmd = rmd;
	}

	public JsonObject getInstitutionAndGroup(int webmartID, int setNo) {
		InsightDAO insightDao = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			/*stb.append("SELECT p.display_name AS accountType,COUNT(a.sub_type) AS value FROM accounts a, ")
					.append("publisher_account_types p WHERE a.webmart_id =" + webmartID + " AND a.set_no=" + setNo
							+ " AND a.trial=0 ")
					.append("AND (STATUS='' OR STATUS IS NULL) AND p.id=a.sub_type GROUP BY a.sub_type");
*/
			stb.append("SELECT account_type AS 'accountType', COUNT(account_type) AS 'value' ")
			.append(" FROM `"+TableMapper.TABALE.get("c5_accounts")+"` WHERE STATUS=1  GROUP BY account_type");
			
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			if(mdt.getRowCount()<1){
				rmd.exception("Data not Found in Database : Query : "+stb.toString()+" : Live Month : "+rmd.getLiveMonth()+" : Live year : "+rmd.getLiveYear());
			}
			
			int rowno=mdt.getRowCount();
			StringBuilder sb=null;
			for(int i=1;i<=rowno;i++){
				sb=new StringBuilder();
				sb.append(mdt.getValue(i, 1)+"s");
				mdt.updateData(i, 1, sb.toString());
			}
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.error("Exception in getInstitutionAndGroup : " + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jsonRecords;
	}

	public JsonObject getAccountsGroupType(int webmartID) throws Exception {
		InsightDAO insightDao = null;
		StringBuilder stb = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			stb.append(/*"select type as display_name, type as account_type from `accounts` group by type");*/
					"SELECT pat.display_name AS display_name, pat.account_type AS account_type FROM publisher_account_types pat WHERE pat.webmart_id="
							+ webmartID + " AND pat.group_type='" + InsightConstant.GROUP_TYPE + "'");
					
				//updated query	
			rmd.log("Favourites Group Type List :: =" + webmartID + " : Query : " + stb + ": Success");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("Exception in getting Account Group Type " + webmartID + " : " + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jsonRecords;
	}

	public AccountDTO getAccountByCodeAndSetNO(String code, int setNo,int webmartID) throws Exception {
		InsightDAO insightDao = null;
		StringBuilder stb = null;
		AccountDTO adto = new AccountDTO();
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			stb.append("SELECT a.account_code AS accountCode,a.account_name AS accountName,a.account_type AS accountType ")
			.append("FROM "+TableMapper.TABALE.get("c5_accounts")+" a WHERE a.account_code='" + code + "' AND a.STATUS>=1 ");
			rmd.log("getAccountByCodeAndSetNO :: code=" + code + " : Query : " + stb + ": Success");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			adto.setId(Integer.parseInt(mdt.getValue(1, 1)));
			adto.setAccountCode(mdt.getValue(1, 2));
			adto.setAccountName(mdt.getValue(1, 3));
			adto.setAccountType(mdt.getValue(1, 4));
		} catch (Exception e) {
			rmd.exception("Exception in getting getAccountByCodeAndSetNO " + code + " : " + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return adto;
	}

	public JsonObject getAccountsGeneratedReports(int webmartID, int year, String month, String accountCode,
			String screen) {
		StringBuilder stb = null;
		InsightDAO insightDao = null;
		MyDataTable mdt2=null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			stb.append("SELECT pr.sort_order AS `s no` ,rm.name AS `Report Type`, ")
			.append("rm.description AS Description,rm.file_name AS Filename,")
			.append("'"+screen+"' AS `status`,pr.category AS Category,'csv' AS `Report Format`,")
			.append("rm.id AS `reportID` FROM publisher_reports pr LEFT JOIN ")
			.append("`reports_master` rm ON pr.report_id=rm.id WHERE ")
			.append("pr.webmart_id="+webmartID+" AND rm.category IN ('ADDITIONAL','COUNTER') ")
			.append("GROUP BY reportID ORDER BY pr.sort_order");
			
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			stb=new StringBuilder();
			stb.append("SELECT DISTINCT(report_id),`format` FROM generated_reports WHERE ")
			.append("account_id='"+accountCode+"' AND webmart_id="+webmartID+" AND YEAR="+year+" AND ")
			.append("MONTH="+month+" AND report_id IN (SELECT id FROM `reports_master` WHERE ")
			.append("webmart_id="+webmartID+" AND category IN ('ADDITIONAL','COUNTER'))");
			mdt2=insightDao.executeSelectQueryMDT(stb.toString());
			HashMap<String,String> reportids=new HashMap<>();
			//int temprow=mdt2.getRowCount();
			for(int j=1;j<=mdt2.getRowCount();j++){
				reportids.put(mdt2.getValue(j, 1), mdt2.getValue(j, 2));
				//reportids.add(mdt2.getValue(j, 1));
			}
			for(int j=1;j<=mdt.getRowCount();j++){
				if(reportids.containsKey(mdt.getValue(j, "reportID"))){
					mdt.updateData(j, "Report Format", reportids.get(mdt.getValue(j, "reportID")));
				}else{
					mdt.updateData(j, "status", "QA");
					mdt.updateData(j, "Report Format", reportids.get(mdt.getValue(j, "reportID")));
				}
			}
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("GeneratedReports List :: =" + webmartID + " : Query : " + stb + ": Success");
			rmd.exception("Exception in getting GeneratedReports List for :: " + webmartID + " : " + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jsonRecords;
	}

	public String getUserFavoritesAccountCodes(int webmartID, int userId) {
		StringBuilder stb = null;
		String userFavorites = "";
		InsightDAO insightDao = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			stb.append("SELECT GROUP_CONCAT(DISTINCT account_code) as usrfav FROM `"+TableMapper.TABALE.get("user_favorites_table")+"` WHERE  "+TableMapper.TABALE.get("uf_user_code")+"='" + userId+"'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			rmd.log("User Favorites List :: =" + webmartID + " : Query : " + stb + ": Success");
			int rows = mdt.getRowCount();
			if (rows <= 0) {
				throw new Exception("No Record Found : User Favorites Account codes");
			}
			rmd.log("getUserFavoritesAccountCodes : webmart=" + webmartID + " : Query : Success");
			userFavorites = mdt.getValue(1, "usrfav");
		} catch (Exception e) {
			rmd.exception("Exception in getting User Favorites List for :: " + webmartID + " : " + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return userFavorites;
	}

	public JsonObject getUserFavoritesList(int webmartID, int setNo, String accountCodes) throws Exception {
		InsightDAO insightDao = null;
		StringBuilder stb = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			stb.append(
					"SELECT acc.code AS account_code, acc.name AS account_name, acc.type AS account_type, acc.set_no AS "
							+ "set_no FROM "+TableMapper.TABALE.get("user_favorites_table")+" uf, accounts acc WHERE acc.code=uf."+TableMapper.TABALE.get("uf_account_code")+" AND "
							+ "acc.set_no=" + setNo + "  AND acc.code IN("
							+ accountCodes
							+ ") AND acc.sub_type IN (SELECT id FROM `publisher_account_types` WHERE group_type ="
							+ InsightConstant.GROUP_TYPE + ") ORDER BY acc.name");
			rmd.log("Favourites List :: =" + webmartID + " : Query : " + stb + ": Success");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("Exception in getting Favourites List for :: " + webmartID + " : " + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jsonRecords;
	}

	public JsonObject getSearchResults(int webmartID, String accountName, String accountType)
			throws Exception {
		InsightDAO insightDao = null;
		StringBuilder stb = new StringBuilder(""); 
		String publisher_name;
		StringBuilder sbAccountTypeSubQuery = new StringBuilder(""); 
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			
			if (accountType == null  || accountType.trim().equalsIgnoreCase("")){
				//do nothing
			}else if(accountType.trim().equalsIgnoreCase("Consortia") || accountType.trim().equalsIgnoreCase("Group")){
				sbAccountTypeSubQuery.append(" AND a.account_type IN ('Consortia','Group')");
			}else if(accountType.trim().equalsIgnoreCase("Institution")){
				sbAccountTypeSubQuery.append(" AND a.account_type IN ('Institution')");
			}
			
			stb.append("SELECT DISTINCT a.account_code AS `code`,a.Account_name AS `Account Name`, a.Account_type AS `Account Type`");
			stb.append("FROM "+TableMapper.TABALE.get("c5_accounts")+" a WHERE (a.account_code LIKE '%"+accountName+"%' or a.account_name LIKE '%"+accountName+"%') and a.status>=1 ");
			stb.append(sbAccountTypeSubQuery.toString());
			stb.append(" ORDER BY a.account_name");
			
			rmd.log("getSearchResults List :: =" + webmartID + " : Query : " + stb + ": Success");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("Exception in getting getSearchResults List for :: " + webmartID + " : " + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jsonRecords;
	}
	
	public JsonObject getSearchResultsByCode(int webmartID, String accountName, String accountType, int setNo)
			throws Exception {
		InsightDAO insightDao = null;
		StringBuilder stb = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			if (accountType == null || accountType.equalsIgnoreCase("Any Account Type")){
				//stb.append(
						/*"SELECT a.code AS `code`,a.name AS `Account Name`, a.type AS `Account Type` FROM")
						.append(" accounts a WHERE a.code='"+accountName+"' AND a.set_no='"+setNo+"' ORDER BY a.name");
						
						*/
						
						//Adde by Kuldeep.sing 20180929 (replace code  = with like '%%')
						// and join with report statics
						
					stb.append("SELECT a.code AS `code`,a.name AS `Account Name`, a.type AS `Account Type` ")
					.append("FROM accounts a ")
					.append("RIGHT JOIN reports_statistics rs ON a.code=rs.institution_id ") 
					.append("WHERE a.code LIKE '%"+accountName+"%' AND a.set_no='"+setNo+"' ")
					.append("GROUP BY a.code,a.name, a.type ")
					.append("ORDER BY a.name");
		}else{
				stb.append(
						"SELECT a.code AS `code`,a.name AS `Account Name`, a.type AS `Account Type`") 
						.append(" FROM accounts a WHERE a.code='"+accountName+"' AND a.set_no='"+setNo+"' ")
						.append("AND a.type='"+accountType+"' ORDER BY a.name");
		}
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			
			if(mdt.getRowCount()<1){
				stb = new StringBuilder();
				stb.append(
						"SELECT a.code AS `code`,a.name AS `Account Name`, a.type AS `Account Type` FROM")
						.append(" accounts a WHERE a.code LIKE '%"+accountName+"%' AND a.set_no='"+setNo+"' ORDER BY a.name");
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
			}
			
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("getSearchResultsByCode List :: =" + webmartID + " : Query : " + stb + ": Success");
			rmd.exception("Exception in getting getSearchResultsByCode List for :: " + webmartID + " : " + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jsonRecords;
	}

	public JsonObject getMyFavoritesList(int webmartID, String userCode) throws Exception {
		InsightDAO insightDao = null;
		StringBuilder stb = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			stb.append(
					"SELECT  acc.account_code AS account_code, acc.account_name AS account_name,acc.account_type AS account_type "
							+ " FROM "+TableMapper.TABALE.get("user_favorites_table")+" uf, c5_accounts acc WHERE acc.account_code=uf."+TableMapper.TABALE.get("uf_account_code")+" AND "
							+ " acc.status>=1  AND uf."+TableMapper.TABALE.get("uf_user_code")+" IN('"
							+ userCode + "') AND acc.account_type  IN "
							+ " ('Institution','Consortia', 'dealer', 'Region') and uf.`level` IN('USER','PUBLISHER')"
							+" ORDER BY acc.account_name");
			rmd.log(" My Favorites List :: =" + webmartID + " : Query : " + stb + ": Success");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("Exception in getting My Favourites List for :: " + webmartID + " : " + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jsonRecords;
	}

	public String getAccountMaxMonthLive(int webmartID, String accountID, int year) throws Exception {
		InsightDAO insightDao = null;
		String monthYear = "";
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("PublisherSettings : Publisher=" + webmartID + " : Connection : Success");
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT MAX(MONTH) AS liveMonth FROM report_inventory WHERE account_id='" + accountID)
					.append("' AND webmart_id='" + webmartID + "' AND YEAR='" + year + "' AND STATUS='LIVE'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rows = mdt.getRowCount();
			if (rows <= 0) {
				throw new Exception("No Record Found : Month");
			}
			rmd.log("getMonthYear : webmart=" + webmartID + " : Query : Success");
			monthYear = mdt.getValue(1, "liveMonth");
		} catch (Exception e) {
			rmd.exception("Exception in getting live Year and month of publisher feed :: " + webmartID + " : "
					+ e.getMessage());
		} finally {

		/*	if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return monthYear;
	}

	public JsonArray getAllAvaiableLive(int webmartID, String accountID) throws Exception {
		InsightDAO insightDao = null;
		JsonArray jarray = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("PublisherSettings : Publisher=" + webmartID + " : Connection : Success");
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT (CASE WHEN a.status = 'QA' THEN month_year ELSE NULL END) AS QA,")
					.append("(CASE WHEN a.status = 'LIVE' THEN month_year ELSE NULL END) AS LIVE,(CASE WHEN ")
					.append("a.status = 'ROLLBACK' THEN month_year ELSE NULL END) AS ROLLBACK FROM ")
					.append("(SELECT CONCAT(MAX(ri.month),'-',ri.YEAR) AS month_year,ri.status FROM `report_inventory` ri ")
					.append("WHERE  ri.webmart_id=" + webmartID + " AND ri.account_id= '" + accountID + "' ")
					.append("AND (ri.status!='' AND ri.status IS NOT NULL) GROUP BY YEAR,ri.status ORDER BY  YEAR DESC) a;");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jarray = mdt.getJsonKeyValue();

		} catch (Exception e) {
			rmd.exception("Exception in getting all available live Year and month " + webmartID + " : " + e.getMessage());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jarray;
	}

	public List<String> getAllAccountsByPublisher(int webmartID) throws Exception {
		StringBuilder stb = null;
		int setNo = 0;
		InsightDAO insightDao = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			try {
				setNo = pubsetting.getSetNo(webmartID);
			} catch (Exception e) {
				rmd.log("Exception in retrieving set no for given period" + e);
				rb = Response.status(Response.Status.EXPECTATION_FAILED).entity("Error in set No ");
				throw (e);
			}
			//insightDao = InsightDAO.getInstance();
			stb = new StringBuilder();
			stb.append("SELECT a."+TableMapper.TABALE.get("account_name")+" as 'name', a."+TableMapper.TABALE.get("a_account_code")+" as 'code'  FROM "+TableMapper.TABALE.get("account_table")+" a  WHERE a.status>=1 group by a.account_name, a.account_code ORDER BY a.account_name");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rowCount = mdt.getRowCount();
			for (int i = 1; i <= rowCount; i++) {
				accountsList.add(mdt.getValue(i, "NAME") + "[{" + mdt.getValue(i, "CODE") + "}]");
			}
		} catch (Exception e) {
			rmd.exception("Exception in getting Accounts List for :: " + webmartID + " : " + e.getMessage());
		} finally {
		/*	if (insightDao != null) {
				insightDao.disconnect();
			}*/
			mdt = null;
		}
		return accountsList;
	}

	public JsonObject searchAccountsByAlphabet(int webmartID, String alphabet, int setNo) throws Exception {
		InsightDAO insightDao = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("SearchUserByAlphabet : Publisher=" + webmartID + " : Connection : Success");
			StringBuilder stb = new StringBuilder();
			String userType=rmd.getUserType().trim();
			String userCode=rmd.getUserCode();
			
			if(userType.equalsIgnoreCase("LibraryClients")) {  
				//change by Satyam on 2019-01-04 for user query check in accounts list
				if (alphabet.equalsIgnoreCase("#") || alphabet.equalsIgnoreCase("hash")){
					stb.append(
							"SELECT acc.account_code AS `Account Code`,acc.account_name AS `Account Name` ,acc.account_type AS `Account Type` FROM "+TableMapper.TABALE.get("c5_accounts")+" acc  LEFT JOIN "+TableMapper.TABALE.get("user_account_table")+" um ON acc.account_code= um.account_code  " + 
							"   LEFT JOIN  `publisher_account_types` pat ON acc.account_type = pat.account_type WHERE  um.user_code='"+userCode+"'  AND acc.status>=1 "
									+ " AND " + " pat.group_type='" + InsightConstant.GROUP_TYPE
									+ "' AND (UPPER(acc.account_name) NOT LIKE 'A%') AND "
									+ " (UPPER(acc.account_name) NOT LIKE 'B%') AND (UPPER(acc.account_name) NOT LIKE 'C%') AND (UPPER(acc.account_name) NOT "
									+ " LIKE 'D%') AND (UPPER(acc.account_name) NOT LIKE 'E%') AND (UPPER(acc.account_name) NOT LIKE 'F%') AND (UPPER(acc.account_name) "
									+ " NOT LIKE 'G%') AND (UPPER(acc.account_name) NOT LIKE 'H%') AND (UPPER(acc.account_name) NOT LIKE 'I%') AND (UPPER(acc.account_name) NOT LIKE 'J%') "
									+ " AND (UPPER(acc.account_name) NOT LIKE 'K%') AND (UPPER(acc.account_name) NOT LIKE 'L%') AND (UPPER(acc.account_name) NOT LIKE 'M%') "
									+ " AND (UPPER(acc.account_name) NOT LIKE 'N%') AND (UPPER(acc.account_name) NOT LIKE 'O%') AND (UPPER(acc.account_name) NOT LIKE 'P%') "
									+ " AND (UPPER(acc.account_name) NOT LIKE 'Q%') AND (UPPER(acc.account_name) NOT LIKE 'R%') AND (UPPER(acc.account_name) NOT LIKE 'S%') "
									+ " AND (UPPER(acc.account_name) NOT LIKE 'T%') AND (UPPER(acc.account_name) NOT LIKE 'U%') AND (UPPER(acc.account_name) NOT LIKE 'V%') "
									+ " AND (UPPER(acc.account_name) NOT LIKE 'W%') AND (UPPER(acc.account_name) NOT LIKE 'X%') AND (UPPER(acc.account_name) NOT LIKE 'Y%') "
									+ " AND (UPPER(acc.account_name) NOT LIKE 'Z%')");
						
				}else if(alphabet.equalsIgnoreCase("*") || alphabet.equalsIgnoreCase("All")||alphabet.equalsIgnoreCase("Star")){
					//change by Satyam on 2019-01-04 for user query check in accounts list
					  stb.append("SELECT acc.account_code AS `Account Code`,acc.account_name AS `Account Name` , acc.account_type AS `Account Type` FROM "+TableMapper.TABALE.get("c5_accounts")+" acc " +
						"LEFT JOIN "+TableMapper.TABALE.get("user_account_table")+" um " + 
						"ON acc.account_code= um.account_code "+	  
					  "LEFT JOIN `publisher_account_types` pat ON acc.account_type = pat.account_type  " +
					  "WHERE um.user_code='"+userCode+"' AND pat.group_type='Account' " +
					  " AND acc.status>=1 ");
						//real query  
					/*
					 * SELECT acc.code AS `Account Code`,acc.name AS `Account Name` , acc.type AS
					 * `Account Type` FROM accounts acc LEFT JOIN "+TableMapper.TABALE.get("user_account_table")+" um ON
					 * acc.code= um.account_id LEFT JOIN `publisher_account_types` pat ON
					 * acc.sub_type = pat.id WHERE um.user_id='1221200' AND pat.group_type='Account'
					 * AND set_no='20123'
					 */
						  
				}
				else{
					stb.append(
							"SELECT acc.account_code AS `Account Code`,acc.account_name AS `Account Name` , acc.account_type AS `Account Type` FROM "+TableMapper.TABALE.get("c5_accounts")+" acc  "
									+"LEFT JOIN "+TableMapper.TABALE.get("user_account_table")+" um ON acc.account_code= um.account_code  "
									+"LEFT JOIN `publisher_account_types` pat ON acc.account_type = pat.account_type WHERE um.user_code='"+userCode+"'  AND acc.status>=1"
									+ " AND " + " pat.group_type='" + InsightConstant.GROUP_TYPE + "' AND acc.account_name LIKE '"
				                    + alphabet + "%' " + " ORDER BY acc.account_name");
				
					/*
					 * SELECT acc.code AS `Account Code`,acc.name AS `Account Name` , acc.type AS
					 * `Account Type` FROM accounts acc LEFT JOIN "+TableMapper.TABALE.get("user_account_table")+" um ON
					 * acc.code= um.account_id LEFT JOIN `publisher_account_types` pat ON
					 * acc.sub_type = pat.id WHERE um.user_id='1221200' AND acc.set_no = 20123 AND
					 * pat.group_type='Account' AND acc.name LIKE 'A%' ORDER BY acc.name
					 */
				
				}
				
			}
			else if(userType.equalsIgnoreCase("publisher")) {
				if (alphabet.equalsIgnoreCase("#") || alphabet.equalsIgnoreCase("hash")){
						stb.append(
								"SELECT acc.account_code AS `Account Code`,acc.account_name AS `Account Name` ,acc.account_type AS `Account Type`  FROM "+TableMapper.TABALE.get("c5_accounts")+" acc  LEFT JOIN "
										+ " `publisher_account_types` pat ON acc.account_type = pat.account_type  WHERE acc.status>=1 "
										+ " AND " + " pat.group_type='" + InsightConstant.GROUP_TYPE
										+ "' AND (UPPER(acc.account_name) NOT LIKE 'A%') AND "
										+ " (UPPER(acc.account_name) NOT LIKE 'B%') AND (UPPER(acc.account_name) NOT LIKE 'C%') AND (UPPER(acc.account_name) NOT "
										+ " LIKE 'D%') AND (UPPER(acc.account_name) NOT LIKE 'E%') AND (UPPER(acc.account_name) NOT LIKE 'F%') AND (UPPER(acc.account_name) "
										+ " NOT LIKE 'G%') AND (UPPER(acc.account_name) NOT LIKE 'H%') AND (UPPER(acc.account_name) NOT LIKE 'I%') AND (UPPER(acc.account_name) NOT LIKE 'J%') "
										+ " AND (UPPER(acc.account_name) NOT LIKE 'K%') AND (UPPER(acc.account_name) NOT LIKE 'L%') AND (UPPER(acc.account_name) NOT LIKE 'M%') "
										+ " AND (UPPER(acc.account_name) NOT LIKE 'N%') AND (UPPER(acc.account_name) NOT LIKE 'O%') AND (UPPER(acc.account_name) NOT LIKE 'P%') "
										+ " AND (UPPER(acc.account_name) NOT LIKE 'Q%') AND (UPPER(acc.account_name) NOT LIKE 'R%') AND (UPPER(acc.account_name) NOT LIKE 'S%') "
										+ " AND (UPPER(acc.account_name) NOT LIKE 'T%') AND (UPPER(acc.account_name) NOT LIKE 'U%') AND (UPPER(acc.account_name) NOT LIKE 'V%') "
										+ " AND (UPPER(acc.account_name) NOT LIKE 'W%') AND (UPPER(acc.account_name) NOT LIKE 'X%') AND (UPPER(acc.account_name) NOT LIKE 'Y%') "
										+ " AND (UPPER(acc.account_name) NOT LIKE 'Z%')");
							
				}else if(alphabet.equalsIgnoreCase("*") || alphabet.equalsIgnoreCase("All")||alphabet.equalsIgnoreCase("Star")){
					
					  stb.append("SELECT acc.account_code AS `Account Code`,acc.account_name AS `Account Name`,acc.account_type AS `Account Type` "
							 +" FROM "+TableMapper.TABALE.get("c5_accounts")+" acc " +
					  "LEFT JOIN `publisher_account_types` pat ON acc.account_type = pat.account_type  " +
					  "WHERE  pat.group_type='Account' " + " AND acc.status>=1 ");
			}
				else{
					stb.append(
							"SELECT acc.account_code AS `Account Code`,acc.account_name AS `Account Name` , acc.account_type AS `Account Type` FROM "+TableMapper.TABALE.get("c5_accounts")+" acc LEFT JOIN "
									+ " `publisher_account_types` pat ON acc.account_type = pat.account_type  WHERE "
									 + " pat.group_type='" + InsightConstant.GROUP_TYPE + "' AND acc.status>=1 AND acc.account_name LIKE '"
									+ alphabet + "%' " + " ORDER BY acc.account_name");
			}
			
			}
			
		//change by Satyam on 2019-01-03 for user query check in accounts list
			/*
			 * if( userType =="Libray Clinet" && (userID != null ||
			 * !userID.trim().equalsIgnoreCase(""))) { stb = new StringBuilder(); stb.
			 * append("SELECT a.code AS accountCode,a.name AS accountName, a.type AS accountType"
			 * +"FROM "+TableMapper.TABALE.get("user_account_table")+" um "
			 * +"LEFT JOIN accounts a ON um.account_id=a.code"
			 * +"LEFT JOIN  role_master rm ON um.role_id=rm.role_id"
			 * +"WHERE user_id='+"userID+""'" +
			 * +"AND a.set_no=(SELECT MAX(set_no) FROM accounts)" +"UNION ALL" +
			 * +"SELECT `code`, `name`, `type` FROM `accounts`" +"WHERE  CODE IN("
			 * +"	SELECT child_id FROM account_parent_child" +"		WHERE parent_id IN("
			 * +"		SELECT a.code AS accountCode FROM "+TableMapper.TABALE.get("user_account_table")+" um"
			 * +"		LEFT JOIN  accounts a ON um.account_id=a.code"
			 * +"		LEFT JOIN role_master rm ON um.role_id=rm.role_id"
			 * +"		WHERE user_id='+"userID"+' \r\n" +
			 * +"		AND a.set_no=(SELECT MAX(set_no)FROM accounts)" +"		)" +"	) "
			 * +"AND set_no = (SELECT MAX(set_no) FROM accounts)")
			 * 
			 * }
			 */
			
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("Exception in getting user details by Alphabet " + webmartID + " : " + e.getMessage());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jsonRecords;
	}

	public String getAccountCodeByName(String Name,int webmartID) {
		InsightDAO insightDao = null;
		String accountCode = "";
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("getAccountCodeByName : Name=" + Name + " : Connection : Success");
			StringBuilder stb = new StringBuilder();
			//stb.append("SELECT CODE FROM accounts WHERE id = " + id);
			stb.append("SELECT account_code FROM `"+TableMapper.TABALE.get("c5_accounts")+"` WHERE account_name= "+Name);
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rows = mdt.getRowCount();
			if (rows <= 0) {
				throw new Exception("No Record Found : Month");
			}
			accountCode = mdt.getValue(1, "CODE");
			rmd.log("getAccountCodeByName : Name=" + Name + " : Query : Success");
		} catch (Exception e) {
			rmd.exception("Exception in getting account code :: " + Name + " : " + e.getMessage());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return accountCode;
	}

	public boolean verifyFavoritesUser(String userCode, String accountCode,int webmartID) {
		InsightDAO insightDao = null;
		boolean resFlag = false;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("verifyFavoritesUser : userCode=" + userCode + "Code=" + accountCode + " : Connection : Success");
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT * FROM "+TableMapper.TABALE.get("user_favorites_table")+" WHERE "+TableMapper.TABALE.get("uf_user_code")+"= '"+ userCode + "' AND  "+TableMapper.TABALE.get("uf_account_code")+" = '" + accountCode
					+ "'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rows = mdt.getRowCount();
			if (rows <= 0) {
				resFlag = false;
			} else if (rows == 1) {
				resFlag = true;
			}
			rmd.log("verifyFavoritesUser : userCode=" + userCode + "Code=" + accountCode + " : Query : Success");
		} catch (Exception e) {
			rmd.exception("Exception in getting favorites account details of :: " + userCode);
			e.printStackTrace();
		} finally {

		/*	if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return resFlag;
	}

	public boolean verifyFavoritesPublisher(String accountCode,int webmartID) {
		InsightDAO insightDao = null;
		boolean resFlag = false;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("verifyFavoritesPublisher : Code=" + accountCode + " : Connection : Success");
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT * FROM "+TableMapper.TABALE.get("user_favorites_table")+" WHERE account_code = '" + accountCode + "' AND "+TableMapper.TABALE.get("uf_level")+"='Publisher'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rows = mdt.getRowCount();
			if (rows <= 0) {
				resFlag = false;
			} else if (rows == 1) {
				resFlag = true;
			}
			rmd.log("verifyFavoritesPublisher Code=" + accountCode + " : Query : Success");
		} catch (Exception e) {
			rmd.exception("Exception in getting favorites account details of publisher" + " : " + e.getMessage());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return resFlag;
	}

	public JsonObject searchUsersByAccount(String accId, String userCode, int webmartId) {
		InsightDAO insightDao = null;
		try {
			pubsetting=new PublisherSettings(rmd);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT um."+TableMapper.TABALE.get("user_code")+" AS user_id, um."+TableMapper.TABALE.get("user_code")+" AS user_code, um."+TableMapper.TABALE.get("first_name")+" AS first_name, um."+TableMapper.TABALE.get("last_name")+" AS last_name,")
					.append(" um."+TableMapper.TABALE.get("user_type")+" AS user_type FROM "+TableMapper.TABALE.get("user_table")+" um LEFT OUTER JOIN "+TableMapper.TABALE.get("user_account_table")+" uam ON um."+TableMapper.TABALE.get("user_code")+"= uam."+TableMapper.TABALE.get("user_code")+" ")
					.append("WHERE uam."+TableMapper.TABALE.get("user_code")+" != '" + userCode + "' AND uam."+TableMapper.TABALE.get("account_code")+" = '" + accId+"'")
					.append(" AND um."+TableMapper.TABALE.get("status")+" !='Deleted'");

			rmd.log("query " + stb.toString());
			
			MyDataTable mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("Exception while executing query");
		} finally {

		/*	if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return jsonRecords;
	}

	public JsonObject searchUnlinkedUserAccount(String accountID, int webmartID, String email) {
		InsightDAO insightDao = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
		stb.append("SELECT u."+TableMapper.TABALE.get("user_code")+" AS `userID`, u."+TableMapper.TABALE.get("first_name")+" AS `First Name`,")
									.append("u."+TableMapper.TABALE.get("last_name")+" AS `Last Name`, u."+TableMapper.TABALE.get("email_id")+" AS `Email ID` FROM ")
									.append(""+TableMapper.TABALE.get("user_table")+" u WHERE u."+TableMapper.TABALE.get("status")+" NOT IN ('Deleted') ")
									.append("AND u."+TableMapper.TABALE.get("user_type")+"='LibraryClients' AND u."+TableMapper.TABALE.get("user_code")+" NOT ")
									.append("IN (SELECT uam."+TableMapper.TABALE.get("user_code")+" AS umid FROM "+TableMapper.TABALE.get("user_account_table")+" uam ")
									.append("WHERE uam."+TableMapper.TABALE.get("account_code")+"='" + accountID+ "') AND (u."+TableMapper.TABALE.get("user_code")+" LIKE '%" + email + "%' OR u."+TableMapper.TABALE.get("email_id")+" LIKE '%"+email+"%')");
							
			MyDataTable mdt = insightDao.executeSelectQueryMDT(stb.toString());
			rmd.log("query " + stb.toString());
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("Exception while executing query");
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return jsonRecords;
	}

	// Changes in query parameters (kuldeep singh 20190228)
	public String deleteUserAccount(String accountCode, String userCode) {
		InsightDAO insightDao = null;
		String response = null;
		try {
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			stb.append("DELETE FROM "+TableMapper.TABALE.get("user_account_table")+" WHERE "+TableMapper.TABALE.get("user_code")+" ='" + userCode+ "' AND "+TableMapper.TABALE.get("account_code")+" ='" + accountCode+"'");

			boolean flag = insightDao.executeDDLQuery(stb.toString());
			if(flag){
				response = "success";
			}else{
				response = "failed";
			}
			
			rmd.log("query " + stb.toString());
		} catch (Exception e) {
			rmd.exception("Exception while executing query");
			response = "fail";
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return response;
	}
	
	
	public String deleteUserAccount(int webmartID, int accountId, String userCode) {
		InsightDAO insightDao = null;
		String response = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			stb.append("DELETE FROM "+TableMapper.TABALE.get("user_account_table")+" WHERE "+TableMapper.TABALE.get("user_code")+" ='" + userCode+ "' AND "+TableMapper.TABALE.get("account_code")+" ='" + accountId+"'");

			boolean flag = insightDao.executeDDLQuery(stb.toString());
			if(flag){
				response = "success";
			}else{
				response = "failed";
			}
			
			rmd.log("query " + stb.toString());
		} catch (Exception e) {
			rmd.exception("Exception while executing query");
			response = "fail";
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return response;
	}

	public JsonObject checkAccountAtoZ(int webmartID) throws Exception {
		InsightDAO insightDao = null;
		String[] aTOz = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
				"t", "u", "v", "w", "x", "y", "z" };
		// JsonObjectBuilder job=Json.createObjectBuilder();
		JsonObjectBuilder job = Json.createObjectBuilder();
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("PublisherSettings : Publisher=" + webmartID + " : Connection : Success");
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT a.account_name AS accountCode FROM "+TableMapper.TABALE.get("c5_accounts")+" a WHERE a.status>=1");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int row = mdt.getRowCount();
			for (String c : aTOz) {
				for (int i = 1; i < row; i++) {
					if (mdt.getValue(i, 1).startsWith(c.toUpperCase())) {
						job.add(c, true);
						break;
					}
				}
			}
		} catch (Exception e) {
			rmd.exception("Exception in checkAccountAtoZ :: " + webmartID);
			e.printStackTrace();
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return job.build();
	}

	public AccountDTO getAccountByCode(String code, int webmartID) throws Exception {
		InsightDAO insightDao = null;
		StringBuilder stb = null;
		AccountDTO adto = new AccountDTO();
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			/*stb.append("SELECT a.id AS id,a.code AS accountCode,a.name AS accountName,a.type AS accountType ")
					.append("FROM accounts a WHERE a.code='" + code + "' AND webmart_id='" + webmartID + "' ORDER BY set_no DESC");*/
			stb.append(" SELECT a.account_code AS accountCode,a.account_name AS accountName,a.account_type AS accountType ")
				.append(" FROM "+TableMapper.TABALE.get("c5_accounts")+" a WHERE a.account_code='" + code + "'  AND a.status=1 ");
			rmd.log("getAccountByCodeAndSetNO :: code=" + code + " : Query : " + stb + ": Success");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			//adto.setId(Integer.parseInt(mdt.getValue(1, 1)));
			adto.setAccountCode(mdt.getValue(1, 1));
			adto.setAccountName(mdt.getValue(1, 2));
			adto.setAccountType(mdt.getValue(1, 3));
		} catch (Exception e) {
			rmd.exception("Exception in getting getAccountByCodeAndSetNO List for :: code " + code);
			return adto;
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return adto;
	}
	
	public JsonArray checkAccountFavorite(int webmartID, String accountID) throws Exception {
		InsightDAO insightDao = null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			rmd.log("PublisherSettings : Publisher=" + webmartID + " : Connection : Success");
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT user_code AS favoriteID, account_code AS accountID FROM "+TableMapper.TABALE.get("user_favorites_table"))
			.append(" WHERE "+TableMapper.TABALE.get("uf_account_code")+"='"+accountID+"' and user_code !=''");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jarray=mdt.getJsonWithKeyValue();
			
		} catch (Exception e) {
			rmd.exception("Exception in checkAccountFavorite :: " + webmartID +" : "+e.getMessage());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jarray;
	}
	
	//*********************************new method***************************
	public InputStream exportData(int webmartID) throws Exception {
		InsightDAO insightDao = null;
		StringBuilder stb = null;
		String csv =null;
		String publisher_name;
		InputStream io=null;
		
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			int setNo = pubsetting.getSetNo(webmartID);
			//int publisherId = pubsetting.getPublisherIDFromWebmartID(webmartID);
			stb = new StringBuilder();
			// stb.append("SELECT user_id,account_id,updated_by FROM
			// user_accounts")
			// .append(" WHERE webmart_id="+webmartID);
			stb.append("SELECT b.ACCOUNT_CODE AS 'Account_CODE', b.ACCOUNT_NAME AS 'Account_NAME',u.email_id  AS 'Email_Id',")
			.append("u.first_name  AS 'First_Name', u.last_name AS 'Last_Name',CAST(u.last_login AS DATE) AS 'Last_Login'")
			.append(" FROM "+TableMapper.TABALE.get("user_table")+" u JOIN "+TableMapper.TABALE.get("user_account_table")+" ")
			.append("ua  RIGHT JOIN "+TableMapper.TABALE.get("c5_accounts")+" b ON ua.account_code=b.ACCOUNT_CODE AND ")
			.append("b.status>=1 WHERE u.status!='Deleted' AND (u.email_id !=" + "\" \""
					+ " OR u.email_id IS NOT NULL) AND ")
			.append("b.ACCOUNT_CODE NOT LIKE 'REG%' GROUP BY b.ACCOUNT_CODE, b.ACCOUNT_NAME, u.email_id, u.first_name, u.last_name,u.last_login ORDER BY b.ACCOUNT_NAME");

			rmd.log("getExportData from "+TableMapper.TABALE.get("user_account_table")+" : Success");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			//Create new Mydatatable with one extra column (Serial Number)
			MyDataTable mdtWithSerialNo = new MyDataTable("export_account_list");
			int rowCount = mdt.getRowCount();
			int colCount = mdt.getColumnCount();
			//Added extra column
			mdtWithSerialNo.addColumn("S.No", "");
			for(int col=1; col<=colCount; col++){
				//rmd.log(mdt.getColumnName(col)+"");
				mdtWithSerialNo.addColumn(mdt.getColumnName(col), "");
			}
			
			colCount = mdtWithSerialNo.getColumnCount();
			//adding each row with s.No. 
		

		for (int row = 1; row <= rowCount; row++) {
			mdtWithSerialNo.addRow();

			for (int col = 1; col <= colCount; col++) {
				if (col == 1) {
					mdtWithSerialNo.updateData(row, col, String.valueOf(row));
				} else {
					String value = String.valueOf(mdt.getValue(row, col - 1));
					if(value==null || value.equalsIgnoreCase("null")){
						value = "-";
					}
					mdtWithSerialNo.updateData(row, col, value);
				}
			}
		}	
			csv = mdtWithSerialNo.getCsvData(",");

			io = new ByteArrayInputStream(csv.getBytes());
			
		} catch (Exception e) {
			rmd.exception("Exception in getting data for ::" + webmartID + ":" + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return io;
	}
	// *************************************************************************
	//change by satyam (correction 20190215  by kuldeep)
	public JsonObject getManualrolebackList(String webmartCode,String Year,String Month) throws Exception {
		InsightDAO insightDao = null;
		StringBuilder stb = null;
		MyDataTable mdt = null; 
		JsonObject jsonRecords = null;
		try {
			pubsetting=new PublisherSettings(rmd);
			//publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			stb.append("SELECT ir.`Institution_id` AS 'Account ID', ")
			.append("a."+TableMapper.TABALE.get("account_name")+" AS 'Account name', ")
			.append("'Live' AS `Status` ") 
			.append(" FROM `institution_reports_statistics` ir LEFT JOIN `"+TableMapper.TABALE.get("account_table")+"` a  ")
			.append("ON ir.institution_id=a.account_code ")
			.append("WHERE ir.`M_"+Year+""+Month+"` IN("+TableMapper.TABALE.get("push_live")+") ")
			.append("GROUP BY  Institution_id,a."+TableMapper.TABALE.get("account_name"));
				
			rmd.log("get Manualrole back :: =" + webmartCode + " : Query : " + stb + ": Success");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jsonRecords = mdt.getJson();
		} catch (Exception e) {
			rmd.exception("Exception in get Manual role back " + webmartCode + " : " + e.getMessage());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return jsonRecords;
	}
	
	public  MyDataTable getBookDetailReport(int webmartID,String title_id,int liveYear,int liveMonth,String data_type) throws Exception{
		List<String> reportSection = new ArrayList<>();
		InsightDAO insightDao = null;
		
		try {
			pubsetting=new PublisherSettings(rmd);
			// passed title_id and liveyear as a dynamic and dont know about metric type and sata type
			insightDao =rmd.getInsightDao();
			rmd.log("getLiveMonth : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append(" SELECT title_id, parent_title, doi, item, issue_volume, issue_no, page_no, SUM(M_"+liveYear+liveMonth+") AS counts FROM "+TableMapper.TABALE.get("master_report_table")+" WHERE title_id='"+title_id+"' AND data_type='"+data_type+"' AND M_201812>0 AND metric_type='Total_Item_Requests' AND institution_type != 'group' GROUP BY title_id, parent_title, doi, item, issue_volume, issue_no, page_no ORDER BY counts DESC ");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			rmd.log(reportSection.toString());
		} catch (Exception e) {
			rmd.log("Exception in getReportDetail... "+e.getMessage());
		} finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return mdt;
	}
}
	