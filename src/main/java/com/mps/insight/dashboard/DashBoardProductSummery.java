package com.mps.insight.dashboard;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.TableMapper;
import com.mps.insight.product.PublisherSettings;

public class DashBoardProductSummery {
	private static final Logger log = LoggerFactory.getLogger(DashBoardProductSummery.class);
	InsightDAO insightDao = null;
	PublisherSettings pubsetting = null;
	private RequestMetaData rmd =null;
	
	public DashBoardProductSummery(RequestMetaData rmd){
		this.rmd =rmd;
	}
	/*
	 * Kuldeep Singh 08082017 Get All child accounts connected with requested
	 * user Return JSON
	 */
	public JsonObject getChildAccount(String userCode, int setNo, int webmartID) {

		MyDataTable mdt = null; // for store result set as table
		JsonObjectBuilder jb = Json.createObjectBuilder();// Store each graph
															// value as json
		String publisher_name;
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			String query = "SELECT CODE, IF(NAME LIKE CONCAT('%', CODE, '%'), NAME, CONCAT(NAME,'-', CODE)) AS NAME FROM user_accounts AS ua, accounts AS a WHERE ua.code="
					+ userCode + " AND ua.account_code=a.code AND a.set_no=" + setNo + " ORDER BY NAME";
			log.info("query for getting child account : " + query);
			mdt = insightDao.executeSelectQueryMDT(query);

			for (int i = 1; i <= mdt.getRowCount(); i++) {
				jb.add(mdt.getValue(i, "CODE"), mdt.getValue(i, "NAME"));
			}

		} catch (Exception e) {
			log.error("unable to find child account " + e);
		}

		return jb.build();

	}

	/*
	 * Kuldeep Singh 09082017 Get one child account id if requested institution
	 * not found and default load at first load Return institution id as String
	 */
	public String getDefaultChildAccount(String userCode, int setNo, int webmartID) {

		MyDataTable mdt = null; // for store result set as table
		String code = null;
		String publisher_name;
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			String query = "SELECT a.account_code AS `CODE` FROM `"+TableMapper.TABALE.get("user_account_table")+"` AS ua, c5_accounts  AS a WHERE ua.user_code=" + userCode
					+ " AND ua.account_code=a.account_code  ORDER BY a.account_name  LIMIT  1";
			
			log.info("query for getting Default Child Account  : " + query);
			mdt = insightDao.executeSelectQueryMDT(query);
			if (mdt.getRowCount() > 0) {
				code = mdt.getValue(1, "CODE"); // getting institution id
			} else {
				log.error("data not found in DB for user id : " + userCode + " and set no : " + setNo);
			}

		} catch (Exception e) {
			log.error("unable to find defauld institution code : " + e);
		}
		
		return code;

	}

	/*
	 * Kuldeep Singh 09082017 Get account id from requested institution from
	 * name/ id of institution Return institution id as String
	 */
	public String getAccountCode(int webmartId, String instituteName) {
		String code = null;
		String table=TableMapper.TABALE.get("c5_accounts");
		MyDataTable mdt = null; // for store result set as table
		String publisher_name;
		try {

			if (instituteName.contains("-")) {
				instituteName = instituteName.substring(0, instituteName.indexOf("-"));
			}
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartId);
			insightDao =rmd.getInsightDao();
			log.info("connection found : " + insightDao);
			String query = "SELECT account_code FROM "+table+" WHERE  account_name LIKE '%"
					+ instituteName + "%' LIMIT 1";
			log.info("query for getting Account for : " + instituteName + " : " + query);
			mdt = insightDao.executeSelectQueryMDT(query);
			if (mdt.getRowCount() > 0) {// check for MDT is not null
				code = mdt.getValue(1, 1); // get only one record from MDT
			} else {
				String queryCode = "SELECT account_code FROM "+table+" WHERE account_code='"
						+ instituteName + "' LIMIT 1";
				mdt = insightDao.executeSelectQueryMDT(queryCode);
				if (mdt.getRowCount() > 0) {// check for MDT is not null
					code = mdt.getValue(1, 1);
				log.info("code found in DB for institution name : " + instituteName);
				}else{
					log.info("query for getting Account for : " + instituteName + " : " + query);
				}
				
				
				
			}
		} catch (Exception e) {
			log.error("code not found in DB for institution name : " + instituteName);
		}

		return code;

	}

	/*
	 * Kuldeep Singh 09082017 Collecting graph values as JSON Return graph
	 * values as JSON
	 */
	/*
	 * public JsonObject geLibDashBoardGraph(int webmartId, int inst){
	 * MyDataTable mdt=null;//for store result set as table
	 * DashBoardProductSummery dashBoardProductSummery=null; PublisherSettings
	 * publisherSettings = null; InsightDAO insightDao = null; JsonArrayBuilder
	 * jb = Json.createArrayBuilder();//Store each graph value as json
	 * JsonObjectBuilder jArrayBuilder=Json.createObjectBuilder(); //Store graph
	 * json as array JsonArrayBuilder
	 * recordArrayBuilder=Json.createArrayBuilder(); //Store graph array with
	 * name and data JsonObjectBuilder finalJson=Json.createObjectBuilder();
	 * //Store hole graph JSON in single JSON JsonArrayBuilder
	 * monthArrayBuilder=Json.createArrayBuilder();//Store month as JSON
	 * 
	 * String query=""; // for generating query String monthList=""; // for
	 * generation month list for collecting sum
	 * 
	 * try { dashBoardProductSummery=new DashBoardProductSummery();
	 * publisherSettings=new PublisherSettings(rmd);
	 * 
	 * int year = publisherSettings.getLiveYear(webmartId); // getting current/
	 * live year from report_inventory table int month =
	 * publisherSettings.getLibLiveMonth(webmartId, year); // getting live month
	 * from report_inventory table
	 * 
	 * List<String> monthMap = dashBoardProductSummery.getMonthMap(month, year);
	 * //getting last 6 month list as List
	 * 
	 * for(int i=0; i<monthMap.size(); i++){ monthList =
	 * "SUM("+monthMap.get(i)+"), "+monthList; } // getting month as
	 * SUM(Jan_2017), SUM(Feb_2017), SUM(Mar_2017) String
	 * s=monthList.substring(0, monthList.length()-2).toString(); //remove last
	 * , from month sum
	 * 
	 * List<String> monthMapHeader = dashBoardProductSummery.getMonthMap(month);
	 * // getting month name without year as list for(int
	 * i=monthMapHeader.size()-1; i>=0; i--){
	 * monthArrayBuilder.add(monthMapHeader.get(i)); } finalJson.add("header",
	 * monthArrayBuilder.build()); // store month for display in graph
	 * 
	 * DynamicDatabase dynamicDatabase=new DynamicDatabase(webmartId); boolean
	 * dbValid=dynamicDatabase.tableCheck("ArticleRequestsByType"); // check for
	 * ArticleRequestsByType table is available for requested publisher of not
	 * 
	 * dashBoardProductSummery=new DashBoardProductSummery(); HashMap<String,
	 * String> mdtTrend = dashBoardProductSummery.geLibTrend(webmartId); //
	 * getting Graph trend for requested library Set<String> key =
	 * mdtTrend.keySet(); for(String trend:key){
	 * 
	 * if(dbValid){ // if ArticleRequestsByType table is available then collect
	 * data from this table if("ART".equalsIgnoreCase(trend)){ query =
	 * "SELECT "+s+" FROM ArticleRequestsByType WHERE institution ='"
	 * +inst+"' AND CONVERT (institution, SIGNED)!=0"; }else
	 * if("BOOK".equalsIgnoreCase(trend)){ query = "SELECT "
	 * +s+" FROM ArticleRequestsByType WHERE title LIKE '%BOOK%' AND institution ='"
	 * +inst+"' AND CONVERT (institution, SIGNED)!=0"; }else
	 * if("JOURNALS".equalsIgnoreCase(trend)){ query = "SELECT "
	 * +s+" FROM ArticleRequestsByType WHERE title LIKE '%JOURNAL%' AND institution ='"
	 * +inst+"' AND CONVERT (institution, SIGNED)!=0"; }else
	 * if("CONFERENCES".equalsIgnoreCase(trend)){ query = "SELECT "
	 * +s+" FROM ArticleRequestsByType WHERE title LIKE '%CONFERENCE%' AND institution ='"
	 * +inst+"' AND CONVERT (institution, SIGNED)!=0"; }else
	 * if("STANDARDS".equalsIgnoreCase(trend)){ query = "SELECT "
	 * +s+" FROM ArticleRequestsByType WHERE title LIKE '%STANDARD%' AND institution ='"
	 * +inst+"' AND CONVERT (institution, SIGNED)!=0"; } }else{ //// if
	 * ArticleRequestsByType table is not available then collect data from
	 * different tables if("BOOK".equalsIgnoreCase(trend)){ query =
	 * "SELECT "+s+" FROM BookReport2 WHERE institution ='"
	 * +inst+"' AND CONVERT (institution, SIGNED)!=0"; }else
	 * if("JOURNALS".equalsIgnoreCase(trend)){ String monthSum=""; for(int i=0;
	 * i<monthMap.size(); i++){ monthSum =
	 * "(SUM("+monthMap.get(i)+"_pdf) + SUM("+monthMap.get(i)+"_html)) as "
	 * +monthMap.get(i)+", "+monthSum; } //generate sum with pdf and html String
	 * months=monthSum.substring(0, monthSum.length()-2).toString(); query =
	 * "SELECT "+months+" FROM JournalReport1 WHERE institution ='"
	 * +inst+"' AND CONVERT (institution, SIGNED)!=0"; }else
	 * if("CONFERENCES".equalsIgnoreCase(trend)){ //CONFERENCES are only
	 * available with ARBT table }else if("STANDARDS".equalsIgnoreCase(trend)){
	 * query = "SELECT "+s+" FROM StandardsUsageByMonth WHERE institution ='"
	 * +inst+"' AND CONVERT (institution, SIGNED)!=0"; }else
	 * if("DATABASES".equalsIgnoreCase(trend)){
	 * 
	 * JsonArrayBuilder jbDatabace = Json.createArrayBuilder();//Store each
	 * graph value as json JsonObjectBuilder
	 * jArrayBuilderDB=Json.createObjectBuilder(); //Store graph json as array
	 * JsonArrayBuilder recordArrayBuilderDB=Json.createArrayBuilder(); //Store
	 * graph array with name and data
	 * 
	 * query = "SELECT "+s+" FROM DataBaseReport1 WHERE institution ='"
	 * +inst+"' AND userActivity IN ('Regular Searches', 'Record Views') AND CONVERT (institution, SIGNED)!=0"
	 * ;
	 * 
	 * insightDao = dynamicDatabase.getInsightDao();
	 * mdt=insightDao.executeSelectQueryMDT(query); for (int i = 1; i <=
	 * mdt.getColumnCount(); i++) { jbDatabace.add(mdt.getValue(1, i));//stroe
	 * value as JSON } jArrayBuilderDB.add("name", "Searches"); // create array
	 * for json jArrayBuilderDB.add("data", jbDatabace.build());
	 * recordArrayBuilderDB.add(jArrayBuilderDB.build());
	 * 
	 * query = "SELECT "+s+" FROM DataBaseReport1 WHERE institution ='"
	 * +inst+"'  AND userActivity IN ('Result Clicks') AND CONVERT (institution, SIGNED)!=0"
	 * ;
	 * 
	 * insightDao = dynamicDatabase.getInsightDao();
	 * mdt=insightDao.executeSelectQueryMDT(query); for (int i = 1; i <=
	 * mdt.getColumnCount(); i++) { jbDatabace.add(mdt.getValue(1, i));//stroe
	 * value as JSON }
	 * 
	 * jArrayBuilderDB.add("name", "Clicks"); // create array for json
	 * jArrayBuilderDB.add("data", jbDatabace.build());
	 * recordArrayBuilderDB.add(jArrayBuilderDB.build());
	 * 
	 * jArrayBuilder.add("name", mdtTrend.get(trend)); // create array for json
	 * jArrayBuilder.add("data", recordArrayBuilderDB.build());
	 * recordArrayBuilder.add(jArrayBuilder);
	 * 
	 * 
	 * } } if(!trend.equalsIgnoreCase("DATABASES")){ insightDao =
	 * dynamicDatabase.getInsightDao();
	 * mdt=insightDao.executeSelectQueryMDT(query); for (int i = 1; i <=
	 * mdt.getColumnCount(); i++) { jb.add(mdt.getValue(1, i));//stroe value as
	 * JSON } jArrayBuilder.add("name", mdtTrend.get(trend)); // create array
	 * for json jArrayBuilder.add("data", jb.build());
	 * recordArrayBuilder.add(jArrayBuilder); } } finalJson.add("record",
	 * recordArrayBuilder); //store array as JSON } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * return finalJson.build();
	 * 
	 * }
	 */

	/*
	 * public List<String> getMonthMap(int month){ List<String> monthList=new
	 * ArrayList<String>(); HashMap<Integer, String> InstProgessMonthMap = new
	 * HashMap<Integer, String>(); try{ if (month <= 0 || month > 12) { throw
	 * new Exception("invalid Month : " + month); } month=month-1;
	 * InstProgessMonthMap.put(0, "Jan"); InstProgessMonthMap.put(1, "Feb");
	 * InstProgessMonthMap.put(2, "Mar"); InstProgessMonthMap.put(3, "Apr");
	 * InstProgessMonthMap.put(4, "May"); InstProgessMonthMap.put(5, "Jun");
	 * InstProgessMonthMap.put(6, "Jul"); InstProgessMonthMap.put(7, "Aug");
	 * InstProgessMonthMap.put(8, "Sep"); InstProgessMonthMap.put(9, "Oct");
	 * InstProgessMonthMap.put(10, "Nov"); InstProgessMonthMap.put(11, "Dec");
	 * 
	 * int counter = 0; for (counter = 0; counter < 6; counter++) { int
	 * currentMonth = month - counter; if(currentMonth<0){
	 * monthList.add(InstProgessMonthMap.get(currentMonth+12)); }else{
	 * monthList.add(InstProgessMonthMap.get(currentMonth)); }
	 * 
	 * } }catch(Exception e){
	 * 
	 * } return monthList;
	 * 
	 * }
	 * 
	 * public List<String> getMonthMap(int month, int year){ List<String>
	 * monthList=new ArrayList<String>(); HashMap<Integer, String>
	 * InstProgessMonthMap = new HashMap<Integer, String>(); try{ if (month <= 0
	 * || month > 12) { throw new Exception("invalid Month : " + month); }
	 * month=month-1; InstProgessMonthMap.put(0, "Jan");
	 * InstProgessMonthMap.put(1, "Feb"); InstProgessMonthMap.put(2, "Mar");
	 * InstProgessMonthMap.put(3, "Apr"); InstProgessMonthMap.put(4, "May");
	 * InstProgessMonthMap.put(5, "Jun"); InstProgessMonthMap.put(6, "Jul");
	 * InstProgessMonthMap.put(7, "Aug"); InstProgessMonthMap.put(8, "Sep");
	 * InstProgessMonthMap.put(9, "Oct"); InstProgessMonthMap.put(10, "Nov");
	 * InstProgessMonthMap.put(11, "Dec"); // validating year if (year <= 0 ||
	 * year > 2050) { throw new Exception("invalid year : " + year); }
	 * 
	 * int counter = 0; for (counter = 0; counter < 6; counter++) { int
	 * currentMonth = month - counter; if(currentMonth<0){
	 * monthList.add(InstProgessMonthMap.get(currentMonth+12)+"_"+(year-1));
	 * }else{ monthList.add(InstProgessMonthMap.get(currentMonth)+"_"+year); }
	 * 
	 * } }catch(Exception e){
	 * 
	 * } return monthList;
	 * 
	 * }
	 */

	/*
	 * public HashMap<String, String> geLibTrend(int webmartId){ MyDataTable
	 * mdt=null;//for store result set as table HashMap<String, String>
	 * trend=new HashMap<String, String>(); String
	 * tableName="dashboard_component_master_libraray";// table for query try {
	 * insightDao=InsightDAO.getInstance(); String
	 * query="SELECT component, STATUS, state, trend_type AS trendType FROM "
	 * +tableName+" WHERE webmart_id="+webmartId+"";
	 * 
	 * mdt=insightDao.executeSelectQueryMDT(query);
	 * 
	 * for (int rowIndex=1; rowIndex<=mdt.getRowCount(); rowIndex++){
	 * 
	 * int status=Integer.parseInt(mdt.getValue(rowIndex, "STATE"));
	 * if(status==1){ trend.put(mdt.getValue(rowIndex, "TRENDTYPE"),
	 * mdt.getValue(rowIndex, "COMPONENT"));
	 * 
	 * } }
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return trend;
	 * 
	 * }
	 */

}
