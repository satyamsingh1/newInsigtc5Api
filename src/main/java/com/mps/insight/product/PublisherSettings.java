package com.mps.insight.product;

import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.redis.Redis;


public class PublisherSettings {
	@Context
	
	//private static final Logger log = LoggerFactory.getLogger(PublisherSettings.class);
	private RequestMetaData rmd; 
	
	public PublisherSettings(RequestMetaData rmd) {
		this.rmd = rmd;
	}
	String jsonData = "";
	ResponseBuilder rb = null;
	MyDataTable mdt = null;
	JsonArray jarray = null;
	JsonObject jsonRecords = null;
	String yearquery=" AND YEAR=";
	String noRecord="No Record Found for webmart id : ";

	/*public PublisherSettings() throws MyException {
		super();
	}*/

	public JsonArray getLiveMonthAndYear(int webmart_id) {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmart_id);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			// executing Query
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append(
					"SELECT YEAR AS liveYear,MONTH AS liveMonth,SUBSTRING_INDEX(SUBSTRING_INDEX(SUBSTRING_INDEX(state,';',1),' ',1),'::',-1) AS")
					.append(" startDate,SUBSTRING_INDEX(SUBSTRING_INDEX(SUBSTRING_INDEX(SUBSTRING_INDEX(state,';',2),';',-1),' ',1),'::',-1) AS")
					.append(" deliveryDate,SUBSTRING_INDEX(SUBSTRING_INDEX(SUBSTRING_INDEX(state,';',-2),';',1),'::',-1) AS processingStatus")
					.append(" FROM `system` WHERE webmart_id=" + webmart_id + " AND ACTION='reports_to_qa'");
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			if(mdt.getRowCount()<1){
				rmd.exception("Data not Found in Database : Query : "+stb.toString()+" : Live Month : "+rmd.getLiveMonth()+" : Live year : "+rmd.getLiveYear());
			}
			rmd.log("getLiveMonthAndYear : Query =" + stb.toString());
			iTracker=5.0;
			jarray = mdt.getJsonData();
			
		} catch (Exception e) {
			rmd.exception("PublisherSettings : getLiveMonthAndYear : Json Data :" + jarray.toString()+" : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if (mdt != null) {
				mdt.destroy();
			}
			mdt = null;
		}
		return jarray;
	}

	public int getPubLiveMonth(int webmartID, int year) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		int liveMonth = 0;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT MAX(MONTH) AS liveMonth FROM `report_inventory` WHERE webmart_id=" + webmartID
					+ yearquery +year+ " AND STATUS IN ('LIVE','QA')");
			iTracker=4.0;
			
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				iTracker=5.0;
				int rowCount = mdt.getRowCount();
				if (rowCount <= 0) {
					throw new MyException(noRecord+webmartID);
				}
				liveMonth = Integer.parseInt(mdt.getValue(1, "liveMonth"));

				for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {

					liveMonth = Integer.parseInt(mdt.getValue(1, "liveMonth"));

				}
				
		} catch (Exception e) {
			rmd.exception("PublisherSettings : getPubLiveMonth : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return liveMonth;
	}

	public int getLiveYear(int webmartID) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		int liveYear = 0;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT MAX(YEAR) AS liveYear FROM `report_inventory` WHERE webmart_id=" + webmartID
					+ " AND STATUS='LIVE'");
			iTracker=4.0;
			if (liveYear == 0) {
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				int rowCount = mdt.getRowCount();
				if (rowCount <= 0) {
					throw new MyException(noRecord+webmartID);
				}
				liveYear = Integer.parseInt(mdt.getValue(1, "liveYear"));
			}

		} catch (Exception e) {
			rmd.exception("PublisherSettings : getLiveYear : iTracker : "+iTracker+" : "+e.toString());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return liveYear;
	}
	
	public int getLiveMonth(int webmartID) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		int liveMonth = 0;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT data_value FROM `publisher_settings` WHERE data_category='Live_month' AND data_key='"+publisher_name+"'");
			iTracker=4.0;
			if (liveMonth == 0) {
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				int rowCount = mdt.getRowCount();
				if (rowCount <= 0) {
					throw new MyException(noRecord+webmartID);
				}
				liveMonth = Integer.parseInt(mdt.getValue(1, "data_value"));
			}

		} catch (Exception e) {
			rmd.exception("PublisherSettings : getLiveMonth : iTracker : "+iTracker+" : "+e.toString());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return liveMonth;
	}

	public int getLiveMonth(String publisher) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		int liveMonth = 0;
		try {
			iTracker=1.0;
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher);
			}
			
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT data_value FROM `publisher_settings` WHERE data_category='Live_month' AND data_key='"+publisher+"'");
			iTracker=4.0;
			if (liveMonth == 0) {
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				int rowCount = mdt.getRowCount();
				if (rowCount <= 0) {
					throw new MyException(noRecord+publisher);
				}
				liveMonth = Integer.parseInt(mdt.getValue(1, "data_value"));
			}

		} catch (Exception e) {
			rmd.exception("PublisherSettings : getLiveMonth : iTracker : "+iTracker+" : "+e.toString());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return liveMonth;
	}
	//Added for gettting live year from  publisher setting table (20190221 kuldeep singh)
	public int getLiveYear(String publisher) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		int liveYear = 0;
		try {
			iTracker=1.0;
			insightDao = InsightDAO.getInstance(publisher);
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT data_value FROM `publisher_settings` WHERE data_category='LIVE_YEAR' AND data_key='"+publisher+"'");
			iTracker=4.0;
			if (liveYear == 0) {
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				int rowCount = mdt.getRowCount();
				if (rowCount <= 0) {
					throw new MyException("no Live Year found for "+publisher);
				}
				liveYear = Integer.parseInt(mdt.getValue(1, "data_value"));
			}

		} catch (Exception e) {
			rmd.exception("PublisherSettings : getLiveMonth : iTracker : "+iTracker+" : "+e.toString());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return liveYear;
	}
	
	public String getPublisherCode(int webmartID) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		String publisherCode = "";
		//insightDao = InsightDAO.getInstance();
		Redis redis = new Redis();
		String publisher_name ="error";
		try {
			iTracker=1.0;
			publisherCode=redis.getValueFromRedisWithKey(webmartID+"_publisher_name");
			iTracker=2.0;
			if(publisherCode==null || publisherCode.length()<2 || publisherCode.equalsIgnoreCase("Error")){
				if(webmartID<1) {
					return publisherCode;
				}
				iTracker=3.0;
				if(webmartID==0){
					throw new MyException(noRecord +webmartID);
				}else{
					publisher_name=getPublisherCode(webmartID);
				}
				
				iTracker=4.0;
				if(rmd.getInsightDao()!=null){
					insightDao = rmd.getInsightDao();
				}else{
					insightDao = InsightDAO.getInstance(publisher_name);
				}
				StringBuilder stb = new StringBuilder();
				iTracker=5.0;
				stb.append("SELECT code FROM publishers_master WHERE webmart_id=" + webmartID);
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				int rowCount = mdt.getRowCount();
				iTracker=6.0;
				if (rowCount <= 0) {
					throw new MyException(noRecord + webmartID);
				}
				iTracker=7.0;
				publisherCode = mdt.getValue(1, "code");
				iTracker=8.0;
				redis.setValueToRedisWithKey(webmartID+"_publisher_name", publisherCode);
			}
			
		} catch (Exception e) {
			rmd.exception("PublisherSettings : getPublisherCode : iTracker : "+iTracker+" : "+e.toString());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}

		return publisherCode;
	}

	public int getSetNo(int webmartID) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		//insightDao = InsightDAO.getInstance();
		int setNo = 0;
		Redis redis=new Redis();
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			iTracker=3.0;
			int liveYear = getLiveYear(webmartID);
			iTracker=4.0;
			int liveMonth = getPubLiveMonth(webmartID, liveYear);
			StringBuilder stb = new StringBuilder();
			iTracker=5.0;
			stb.append("SELECT set_no AS setNo FROM `feed_sets` WHERE webmart_id=" + webmartID + yearquery + liveYear
					+ " AND MONTH=" + liveMonth);
			iTracker=6.0;
			String redisSetNo=redis.getValueFromRedisWithKey(webmartID+"_set_no");
			if(redisSetNo!=null){
				setNo=Integer.parseInt(redisSetNo);
			}
			iTracker=7.0;
			if(setNo==0){
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rowCount = mdt.getRowCount();
			if (rowCount <= 0) {
				throw new MyException(noRecord+webmartID);
			}
			setNo = Integer.parseInt(mdt.getValue(1, "setNo"));
			}
			iTracker=8.0;
			redis.setValueToRedisWithKey(webmartID+"_set_no", setNo+"");
		} catch (Exception e) {
			rmd.exception("PublisherSettings : getSetNo : iTracker : "+iTracker+" : "+e.toString());
			
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/

		}

		return setNo;
	}
	
	public int getSetNoByMonth_Year(int webmartID,int year,int month) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		//insightDao = InsightDAO.getInstance();
		int setNo = 0;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			int liveYear = year;
			int liveMonth = month;
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT set_no AS setNo FROM `feed_sets` WHERE webmart_id=" + webmartID + yearquery + liveYear
					+ " AND MONTH=" + liveMonth);
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			int rowCount = mdt.getRowCount();
			if (rowCount <= 0) {
				throw new MyException(noRecord+webmartID);
			}
			setNo = Integer.parseInt(mdt.getValue(1, "setNo"));
			
		} catch (Exception e) {
			rmd.exception("PublisherSettings : getSetNoByMonth_Year : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
*/
		}

		return setNo;
	}

	public JsonObject getProductTrendType(int webmartID) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		JsonObjectBuilder jsonTable = Json.createObjectBuilder();
		String publisher_name;
		StringBuilder stb = new StringBuilder();
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			iTracker=3.0;
			stb.append("SELECT full_text AS `FULL-TEXT`,")
					.append("`database` AS `DATABASE`,platform AS `PLATFORM`,ft_book AS `FT BOOK`,ft_journal AS `FT JOURNAL`,")
					.append("`ft_standard` AS `FT STANDARD`,`ft_conference` AS `FT CONFERENCE`,`ft_proceeding` AS `FT PROCEEDING`,")
					.append("`ft_multimedia` AS `FT MULTIMEDIA`,`db_search` AS `DB SEARCH`,`db_record_view` AS `DB RECORD VIEW`,")
					.append("`db_result_click` AS `DB RESULT CLICK`,`pf_search` AS `PF SEARCH`,`pf_record_view` AS `PF RECORD VIEW`")
					.append(",`pf_result_click` AS `PF RESULT CLICK` FROM `dashboard_product_type_v2` WHERE webmart_id="
							+ webmartID);

			// gettinfg connection
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			if(mdt.getRowCount()<1){
				rmd.exception("Data not Found in Database : Query : "+stb.toString()+" : Live Month : "+rmd.getLiveMonth()+" : Live year : "+rmd.getLiveYear());
			}
			iTracker=4.0;
			JsonArray productArray = mdt.getJsonData();
			Set<String> productName = null;
			JsonObject obj = null;
			for (int i = 0; i < productArray.size(); i++) {
				obj = productArray.getJsonObject(i);
				//rmd.log("obj.keySet().toString() : " + obj.keySet());
				productName = obj.keySet();
			}

			for (String string : productName) {
				if (null!=obj && !obj.get(string).toString().contains("\"0\"")) {
					jsonTable.add(string, InsightConstant.productMaster.get(string));
				}
			}
		} catch (Exception e) {
			rmd.exception("PublisherSettings : getProductTrendType : getInstitutionAndGroup : Query :iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			mdt = null;
		}
		return jsonTable.build();
	}

	// *********************************************For library
	// Login***********************************************
	public int getLibLiveMonth(int webmartID, int year) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		int liveMonth = 0;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			/*stb.append("SELECT MAX(MONTH) AS liveMonth FROM `report_inventory` WHERE webmart_id=" + webmartID
					+ yearquery + year + " AND STATUS='LIVE'");*/
			
			stb.append("SELECT data_value AS liveMonth "
					+ "FROM `publisher_settings` "
					+ "WHERE data_category='LIVE_MONTH' "
					+ "AND data_key='"+publisher_name+"'");
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			int rowCount = mdt.getRowCount();
			if (rowCount <= 0) {
				throw new MyException(noRecord+webmartID);
			}
			liveMonth = Integer.parseInt(mdt.getValue(1, 1));

		} catch (Exception e) {
			rmd.exception("PublisherSettings : getLibLiveMonth : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return liveMonth;
	}
// 
	public int getLibLiveMonthC4(int webmartID, int year) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		int liveMonth = 0;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			/*stb.append("SELECT MAX(MONTH) AS liveMonth FROM `report_inventory` WHERE webmart_id=" + webmartID
					+ yearquery + year + " AND STATUS='LIVE'");*/
			
			stb.append("SELECT data_value AS liveMonth "
					+ "FROM `publisher_settings` "
					+ "WHERE data_category='LIVE_MONTH_C4' "
					+ "AND data_key='"+publisher_name+"'");
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			int rowCount = mdt.getRowCount();
			if (rowCount <= 0) {
				throw new MyException(noRecord+webmartID);
			}
			if(year==2019){
			liveMonth = Integer.parseInt(mdt.getValue(1, 1));}
			else{
				liveMonth=12;
			}

		} catch (Exception e) {
			rmd.exception("PublisherSettings : getLibLiveMonth : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return liveMonth;
	}
	
	
	public int getMinLibLiveMonthC4(int webmartID, int year) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		int liveMonth = 0;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			/*stb.append("SELECT MAX(MONTH) AS liveMonth FROM `report_inventory` WHERE webmart_id=" + webmartID
					+ yearquery + year + " AND STATUS='LIVE'");*/
			
			stb.append("SELECT MAX(MONTH) AS liveMonth "
					+ "FROM `generated_reports` "
					+ "WHERE year="+year+"");
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			int rowCount = mdt.getRowCount();
			if (rowCount <= 0) {
				throw new MyException(noRecord+webmartID);
			}
			liveMonth = Integer.parseInt(mdt.getValue(1, 1));

		} catch (Exception e) {
			rmd.exception("PublisherSettings : getLibLiveMonth : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return liveMonth;
	}
	
	
	
	
	public int getPublisherIDFromWebmartID(int webmart_id) {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		int pubID = 0;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmart_id);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT id FROM publishers_master WHERE webmart_id=" + webmart_id);
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			int rowCount = mdt.getRowCount();
			if (rowCount <= 0) {
				throw new MyException(noRecord+webmart_id);
			}
			pubID = Integer.parseInt(mdt.getValue(1, 1));

		} catch (Exception e) {
			rmd.exception("PublisherSettings : getPublisherIDFromWebmartID : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return pubID;
	}
	
	public String getPublisherIDFromCode(String publisherCode) throws Exception {
		double iTracker=0.0;
		
		InsightDAO insightDao = null;
		String publisherID = "";
		
		Redis redis=new Redis();
		String webmartID=null;
		
		try {
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisherCode);
			}
			StringBuilder stb = new StringBuilder();
			iTracker=1.0;
			stb.append("SELECT data_value,webmart_id FROM publisher_settings WHERE data_category='PUBLISHER_ID' and data_key='" + publisherCode+"'");
			iTracker=2.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			iTracker=3.0;
			publisherID = mdt.getValue(1, "data_value");
			iTracker=4.0;
			webmartID=mdt.getValue(1, "webmart_id");
			iTracker=5.0;
			String publisherName = redis.getValueFromRedisWithKey(webmartID+"_publisher_name");
			iTracker=6.0;
			if(null==publisherName||publisherName.equalsIgnoreCase("error")){
				redis.setValueToRedisWithKey(webmartID+"_publisher_name", publisherCode);
			}
			
		} catch (Exception e) {
			rmd.exception("PublisherSettings : getPublisherIDFromCode : iTracker : "+iTracker+" : "+e.toString());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}

		return publisherID;
	}
	
	public int getWebmartIDFromCode(String publisherCode) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		int webmartID = 0;
	
		try {
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisherCode);
			}
			StringBuilder stb = new StringBuilder();
			iTracker=1.0;
			stb.append("SELECT webmart_id FROM publishers_master WHERE code='" + publisherCode+"'");
			iTracker=2.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			iTracker=3.0;
			webmartID = Integer.parseInt(mdt.getValue(1, "webmart_id"));
			
		} catch (Exception e) {
			rmd.exception("PublisherSettings : getWebmartIDFromCode : iTracker : "+iTracker+" : "+e.toString());
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}

		return webmartID;
	}
	
	public String getAccountMonthStatus(int webmartID, int year,String accountid,String status) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		String liveMonth = "";
		String reportStatus="";
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			iTracker=3.0;
			if(status.contains("LIVE")){
				reportStatus="'LIVE'";
			}else if(status.contains("QA")){
				reportStatus="'QA','ROLLBACK'";
			}else{
				reportStatus=status;
			}
			StringBuilder stb = new StringBuilder();
			iTracker=4.0;
			stb.append("SELECT MAX(MONTH) FROM `report_inventory` WHERE webmart_id='"+webmartID+"' AND YEAR='")
			.append(year+"' AND STATUS in ("+reportStatus+") AND account_id='"+accountid+"'");
			iTracker=5.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			int rowCount = mdt.getRowCount();
			if (rowCount <= 0) {
				liveMonth="";
			}else{
				liveMonth = mdt.getValue(1, 1);
			}	

		} catch (Exception e) {
			rmd.exception("PublisherSettings : getAccountMonthStatus : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return liveMonth;
	}
	
	public JsonArray getAccountLiveYearList(int webmartID,String accountid,String status) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		JsonArray jb=null;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT DISTINCT r.year AS YEAR FROM report_inventory r ")
			.append("WHERE webmart_id="+webmartID+" AND account_id='"+accountid)
			.append("' AND r.status='LIVE' ORDER BY YEAR DESC LIMIT 5");
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jb=mdt.getJsonData();
		} catch (Exception e) {
			rmd.exception("PublisherSettings : getAccountLiveYearList : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return jb;
	}
	
	public JsonArray getPublisherAndUserAccess(int webmartID,String role,String userType) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		JsonArray jb=null;
		String publisher_name;
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT menu_name,CASE WHEN (role_access LIKE '%"+role+"%' AND")
			.append(" (webmart_id=0 OR webmart_id LIKE '%,"+webmartID+"' OR webmart_id LIKE '"+webmartID+",%')) THEN 1 ELSE 0 END AS `permission` FROM menu_master WHERE module='"+userType+"'");
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			jb=mdt.getJsonData();

		} catch (Exception e) {
			rmd.exception("PublisherSettings : getPublisherAndUserAccess : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return jb;
	}
	
	public JsonArray getPubDashBoardView(int webmartID) {

		double iTracker=0.0;
		InsightDAO insightDao = null;
		JsonArray jo=null;
		String publisher_name;
		StringBuilder stb = new StringBuilder();
		try {
			iTracker=1.0;
			publisher_name=getPublisherCode(webmartID);
			iTracker=2.0;
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(publisher_name);
			}
			

			// executing Query
			iTracker=3.0;
			stb.append("SELECT d.component,CASE WHEN d.state=1 THEN TRUE ELSE FALSE END AS `status` ")
			.append("FROM dashboard_component_master d WHERE webmart_id="+webmartID);
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			jo=mdt.getJsonData();
		} catch (Exception e) {
			rmd.exception("PublisherSettings : getPubDashBoardView : iTracker : "+iTracker+" : Query =" + stb.toString() + " : Query : Success " + " MDT size :"+mdt.getRowCount()+e.toString());
		} finally {
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		return jo;
	}
	
	
	//Added by Kuldeep Singh (20190105)
	
	//get publisher year from publisher setting table
	public String getPublisherLiveYear(String webmartCode) {
		 InsightDAO insightDao = null;
		 String currentYear ="0000";
		 StringBuilder query=new StringBuilder();
		 
		try {
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(webmartCode);
			}
			query.append("SELECT `data_value` as 'value' FROM `publisher_settings` WHERE data_category='LIVE_YEAR' AND data_key='"+webmartCode+"' Limit 1");
			MyDataTable result = insightDao.executeSelectQueryMDT(query.toString());
			currentYear=result.getValue(1, "value");
		
		} catch (Exception e) {
			rmd.exception("unable to get Year form Publisher Setting : "+query.toString()+" :"+e.toString());
		}
		
		
		return currentYear;
		
	}
	
	//get publisher month from publisher setting table 
	public String getPublisherLiveMonth(String webmartCode) {
		 InsightDAO insightDao = null;
		 String currentMonth ="00";
		 StringBuilder query=new StringBuilder();
		 
		try {
			if(rmd.getInsightDao()!=null){
				insightDao = rmd.getInsightDao();
			}else{
				insightDao = InsightDAO.getInstance(webmartCode);
			}
			query.append("SELECT `data_value` as 'value' FROM `publisher_settings` WHERE data_category='LIVE_MONTH' AND data_key='"+webmartCode+"' Limit 1");
			MyDataTable result = insightDao.executeSelectQueryMDT(query.toString());
			currentMonth=result.getValue(1, "value");
		
		} catch (Exception e) {
			rmd.exception("unable to get Year form Publisher Setting : "+query.toString()+" :"+e.toString());
		}
		
		
		return currentMonth;
		
	}

	
}