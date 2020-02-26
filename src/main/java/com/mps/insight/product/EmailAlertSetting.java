package com.mps.insight.product;

import javax.json.JsonObject;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.CommonDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;

public class EmailAlertSetting {
	
	RequestMetaData rmd;
	
//	private static final Logger LOG = LoggerFactory.getLogger(EmailAlertSetting.class);
	PublisherSettings pubsetting=null;
	public EmailAlertSetting(RequestMetaData rmd){
		this.rmd = rmd;
	}
	public JsonObject getEmailAlerts(int webmartID) {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		JsonObject tempObject=null;
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			rmd.log("getEmailAlerts : webmart_id=" + webmartID);
			//int pubID=pubsetting.getPublisherIDFromWebmartID(searchCode);
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT e.id as `ID`,e.alertName AS `AlertType`,e.firstName AS `First Name`,e.lastName AS `Last Name`,e.email_id AS `Email ID` ")
			.append("FROM email_alerts e LEFT JOIN email_categories ec ON e.alertName=ec.email_code ")
			.append("WHERE e.webmart_id=ec.webmart_id AND e.webmart_id="+webmartID)
			.append(" AND ec.email_category='EMAIL_ALERTS' ORDER BY e.alertName");
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			iTracker=5.0;
			tempObject=mdt.getJson();
			rmd.log(" tempObject : "+tempObject.toString());
			//LOG.info(reportSection.toString());
		} catch (Exception e) {
			rmd.exception("EmailAlertSetting : getEmailAlerts : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return tempObject;
	}
	
	public JsonObject getLibraryConfigSetting(int webmartID) {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		JsonObject tempObject=null;
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			rmd.log("getLibraryConfigSetting : webmart_id=" + webmartID);
			//int pubID=pubsetting.getPublisherIDFromWebmartID(searchCode);
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("SELECT ps.data_category AS `Category`,ps.data_key AS `Data Key`,ps.data_value AS `Data Value` ")
			.append("FROM publisher_settings ps WHERE ps.webmart_id="+webmartID+" AND ps.data_category='LIBRARY_OPTION'");
			iTracker=4.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			iTracker=5.0;
			tempObject=mdt.getJson();
			rmd.log(" tempObject : "+tempObject.toString());
			//LOG.info(reportSection.toString());
		} catch (Exception e) {
			rmd.exception("EmailAlertSetting : getLibraryConfigSetting : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return tempObject;
	}
	
	public String addEmailAlert(UserDTO udto){
		double iTracker=0.0;
		InsightDAO insightDao = null;
		String result="added";
		StringBuilder stb=null;
		MyDataTable mdt=null;
		long newId=0;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(udto.getWebmartID());
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			stb = new StringBuilder();
			iTracker=3.0;
			stb.append("INSERT INTO email_alerts (`webmart_id`,`alertName`,`firstName`,`lastName`,`email_id`,")
			.append("`updated_by`,`updated_at`) VALUES ('"+udto.getWebmartID()+"','")
			.append(udto.getAlertName()+"','"+udto.getFirstName()+"','"+udto.getLastName()+"','")
			.append(udto.getEmailID()+"','"+udto.getUpdatedBy()+"',NOW())");
			iTracker=4.0;
			newId=insightDao.executeInsertUpdateQuery(stb.toString());
			iTracker=5.0;
			if(newId==0){
				result="fail";
			}else{
				stb = new StringBuilder();
				iTracker=6.0;
				stb.append("select max(id) as `alertID` from email_alerts where webmart_id="+udto.getWebmartID()+" and ")
				.append("email_id='"+udto.getEmailID()+"' and alertName='"+udto.getAlertName()+"'");
				iTracker=7.0;
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				iTracker=8.0;
				result=mdt.getValue(1, "alertID");
			}
		}catch(Exception e){
			rmd.exception("EmailAlertSetting : addEmailAlert : iTracker : "+iTracker+" : "+e.toString());
			result="fail";
		}
		
		return result;
	}
	
	public String removeEmailAlert(String alertID,int webmartID){
		double iTracker=0.0;
		InsightDAO insightDao = null;
		String result="deleted";
		long newId=0;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
			stb.append("DELETE FROM `email_alerts` WHERE id='"+alertID+"' and webmart_id='"+webmartID+"'");
			iTracker=4.0;
			newId=insightDao.executeInsertUpdateQuery(stb.toString());
			iTracker=5.0;
			if(newId==0){
				result="fail";
			}
		}catch(Exception e){
			rmd.exception("EmailAlertSetting : removeEmailAlert : iTracker : "+iTracker+" : "+e.toString());
		}
		
		return result;
	}
	
	public String updatePushLiveSetting(CommonDTO dto) {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		long newId=0;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(dto.getWebmartID());
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			iTracker=3.0;
			rmd.log("updatePushLiveSetting : webmart_id=" + dto.getWebmartID());
			StringBuilder stb = new StringBuilder();
			iTracker=4.0;
			stb.append("UPDATE publisher_settings SET `data_key`='"+dto.getDataKey()+"',")
			.append("`data_value`='"+dto.getDataValue()+"',`updated_by`='"+dto.getUpdatedBy()+"',`updated_at`=NOW() ")
			.append("WHERE webmart_id='"+dto.getWebmartID()+"' AND `data_category`='LIVE_SETTINGS'");
			iTracker=5.0;
			newId=insightDao.executeInsertUpdateQuery(stb.toString());
			rmd.log(" updatePushLiveSetting : "+newId);
			//LOG.info(reportSection.toString());
		} catch (Exception e) {
			rmd.exception("EmailAlertSetting : updatePushLiveSetting : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return "Updated";
	}
	
	public String updateLibraryConfigSetting(CommonDTO dto) {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		long newId=0;
		String dataKey="Update_Account";
		String dataValue="No";
		iTracker=1.0;
		if(dto.getDataKey().contains("Update_Account")){
			dataKey="Update_Account";	
		}else if(dto.getDataKey().contains("Report_Email_Alerts")) {
			dataKey="Report_Email_Alerts";
		}
		iTracker=2.0;
		if(dto.getDataValue().contains("true")){
			dataValue="Yes";
		}else{
			dataValue="No";
		}
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=3.0;
			publisher_name=pubsetting.getPublisherCode(dto.getWebmartID());
			iTracker=4.0;
			insightDao =rmd.getInsightDao();
			rmd.log("updateLibraryConfigSetting : webmart_id=" + dto.getWebmartID());
			StringBuilder stb = new StringBuilder();
			iTracker=5.0;
			stb.append("UPDATE publisher_settings SET ")
			.append("`data_value`='"+dataValue+"',`updated_by`='"+dto.getUpdatedBy()+"',`updated_at`=NOW() ")
			.append("WHERE webmart_id='"+dto.getWebmartID()+"' AND `data_category`='LIBRARY_OPTION' AND `data_key`='"+dataKey+"'");
			iTracker=6.0;
			newId=insightDao.executeInsertUpdateQuery(stb.toString());
			rmd.log(" updatePushLiveSetting : "+newId);
			//LOG.info(reportSection.toString());
		} catch (Exception e) {
			rmd.exception("EmailAlertSetting : updateLibraryConfigSetting : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return "Updated";
	}

}
