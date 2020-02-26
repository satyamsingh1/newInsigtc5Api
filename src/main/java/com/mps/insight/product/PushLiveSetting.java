package com.mps.insight.product;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.CommonDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.global.TableMapper;

public class PushLiveSetting {
	
	private static final Logger log = LoggerFactory.getLogger(PushLiveSetting.class);
	PublisherSettings pubsetting=null;
	private RequestMetaData rmd=null; 
	
	
	public PushLiveSetting(RequestMetaData rmd){
		this.rmd =rmd;
	}
	
	
	public JsonObject getPushLiveSetting(int webmartID) {
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		JsonObject tempObject=null;
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			log.info("getPushLiveSetting : webmart_id=" + webmartID);
			//int pubID=pubsetting.getPublisherIDFromWebmartID(searchCode);
			StringBuilder stb = new StringBuilder();
			stb.append("SELECT ps.data_category AS `Category`,ps.data_key AS `Data Key`,ps.data_value AS `Data Value` ")
			.append("FROM publisher_settings ps WHERE ps.webmart_id="+webmartID+" AND ps.data_category='LIVE_SETTINGS'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			tempObject=mdt.getJson();
			log.info(" tempObject : "+tempObject.toString());
			//log.info(reportSection.toString());
		} catch (Exception e) {
			log.info("Exception in getPushLiveSetting");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return tempObject;
	}
	public String setAccountReportsLive(int webmartID,String accountCode,UserDTO user) {
		InsightDAO insightDao = null;
		int year=0;
		int month=0;
		Long repInvreturn=null;
		Long genRepreturn=null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			year=pubsetting.getLiveYear(webmartID);
			month=pubsetting.getPubLiveMonth(webmartID, year);
			String currentMonth = month<10 ? "0"+month : ""+month;log.info("setAccountReportsLive : webmart_id=" + webmartID);
			//int pubID=pubsetting.getPublisherIDFromWebmartID(searchCode);
			StringBuilder stb = new StringBuilder();
			stb.append("UPDATE report_inventory SET STATUS='LIVE', updatedBy='"+user.getEmailID()+"',")
			.append("updatedAt=NOW() WHERE account_id='"+accountCode+"' MONTH='"+currentMonth+"' AND ")
			.append("YEAR="+year+" AND webmart_id='"+webmartID+"' AND STATUS='QA'");
			repInvreturn = insightDao.executeInsertUpdateQuery(stb.toString());
			log.info(repInvreturn+" setAllReportsLive");
			
			StringBuilder stb1 = new StringBuilder();
			stb1.append("UPDATE generated_report SET STATUS='LIVE',")
			.append("updatedBy='"+user.getEmailID()+"',updatedAt=NOW() WHERE account_id='"+accountCode+"' AND MONTH='"+currentMonth+"' AND ")
			.append("YEAR='"+year+"' AND webmart_id= '"+webmartID+"' AND STATUS='QA'  AND ")
			.append("report.id IN (SELECT id FROM reports_master WHERE category")
			.append(" IN (SELECT section FROM `report_sections` rs WHERE webmart_id='"+webmartID+"' AND")
			.append("category='USAGE_REPORTS' ORDER BY rs.order))");
			genRepreturn = insightDao.executeInsertUpdateQuery(stb1.toString());
			log.info(genRepreturn+" setAccountReportsRollBack");
			
			if(repInvreturn==genRepreturn){
				stb1 = new StringBuilder();
				stb1.append("UPDATE report_inventory SET live_at=NOW() WHERE MONTH='"+currentMonth+"' AND ")
				.append("YEAR='"+year+"' AND webmart_id='"+webmartID+"' AND STATUS='LIVE'");
				genRepreturn = insightDao.executeInsertUpdateQuery(stb1.toString());
				log.info(genRepreturn+" setAccountReportsRollBack");
				
			}
			//log.info(reportSection.toString());
		} catch (Exception e) {
			log.info("Exception in setAccountReportsLive");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return "success";
	}
	public String setAllReportsLive(int webmartID,UserDTO user) {
		InsightDAO insightDao = null;
		Long repInvreturn=null;
		Long genRepreturn=null;
		int year=0;
		int month=0;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			year=pubsetting.getLiveYear(webmartID);
			month=pubsetting.getPubLiveMonth(webmartID, year);
			String currentMonth = month<10 ? "0"+month : ""+month;
			log.info("setAllReportsLive : webmart_id=" + webmartID);
			//int pubID=pubsetting.getPublisherIDFromWebmartID(searchCode);
			StringBuilder stb = new StringBuilder();
			stb.append("UPDATE report_inventory SET STATUS='LIVE', updatedBy='"+user.getEmailID()+"', ")
			.append("updatedAt=NOW() WHERE MONTH='"+currentMonth+"' AND YEAR='" + year + "' AND ")
			.append("webmart_id='"+webmartID+"'  AND STATUS='QA'");
			repInvreturn = insightDao.executeInsertUpdateQuery(stb.toString());
			log.info(repInvreturn+" setAllReportsLive");
			
			StringBuilder stb1 = new StringBuilder();
			stb1.append("UPDATE generated_report SET STATUS='LIVE',")
			.append("updated_by='"+user.getEmailID()+"',updated_at=NOW() WHERE MONTH='"+currentMonth+"' AND ")
			.append("YEAR='"+year+"' AND webmart_id= '"+webmartID+"' AND STATUS='QA'  AND ")
			.append("report_id IN (SELECT id FROM reports_master WHERE category")
			.append(" IN (SELECT section FROM `report_sections` rs WHERE webmart_id='"+webmartID+"' AND")
			.append("category='USAGE_REPORTS' ORDER BY rs.order))");
			genRepreturn = insightDao.executeInsertUpdateQuery(stb1.toString());
			log.info(genRepreturn+" setAllReportsLive");
			
			if(repInvreturn==genRepreturn){
				stb1 = new StringBuilder();
				stb1.append("UPDATE report_inventory SET live_at=NOW() WHERE MONTH='"+currentMonth+"' AND ")
				.append("YEAR='"+year+"' AND webmart_id='"+webmartID+"' AND STATUS='LIVE'");
				genRepreturn = insightDao.executeInsertUpdateQuery(stb1.toString());
				log.info(genRepreturn+" setAllReportsLive");
				
			}
			
			//log.info(reportSection.toString());
		} catch (Exception e) {
			log.info("Exception in setAllReportsLive");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return "success";
	}
	
	public String setAccountReportsRollBack(int webmartID,String accountCode,UserDTO user) {
		InsightDAO insightDao = null;
		Long repInvreturn=null;
		Long genRepreturn=null;
		int year=0;
		int month=0;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			year=pubsetting.getLiveYear(webmartID);
			month=pubsetting.getPubLiveMonth(webmartID, year);
			log.info("setAccountReportsRollBack : webmart_id=" + webmartID);
			String currentMonth = month<10 ? "0"+month : ""+month;

			
			//int pubID=pubsetting.getPublisherIDFromWebmartID(searchCode);
			StringBuilder stb = new StringBuilder();
			stb.append("UPDATE report_inventory SET STATUS='ROLLBACK', updatedBy='"+user.getEmailID()+"',")
			.append("updatedAt=NOW() WHERE account_id='"+accountCode+"' MONTH='"+currentMonth+"' AND ")
			.append("YEAR="+year+" AND webmart_id='"+webmartID+"' AND STATUS='LIVE'");
			repInvreturn = insightDao.executeInsertUpdateQuery(stb.toString());
			log.info(repInvreturn+" setAccountReportsRollBack");
			
			StringBuilder stb1 = new StringBuilder();
			stb1.append("UPDATE generated_report SET STATUS='ROLLBACK',")
			.append("updatedBy='"+user.getEmailID()+"',updatedAt=NOW() WHERE account_id='"+accountCode+"' AND MONTH='"+currentMonth+"' AND ")
			.append("YEAR='"+year+"' AND webmart_id= '"+webmartID+"' AND STATUS='LIVE'  AND ")
			.append("report.id IN (SELECT id FROM reports_master WHERE category")
			.append(" IN (SELECT section FROM `report_sections` rs WHERE webmart_id='"+webmartID+"' AND")
			.append("category='USAGE_REPORTS' ORDER BY rs.order))");
			genRepreturn = insightDao.executeInsertUpdateQuery(stb1.toString());
			log.info(genRepreturn+" setAccountReportsRollBack");
			
			if(repInvreturn==genRepreturn){
				stb1 = new StringBuilder();
				stb1.append("UPDATE report_inventory SET rollback_at=NOW() WHERE MONTH='"+currentMonth+"' AND ")
				.append("YEAR='"+year+"' AND webmart_id='"+webmartID+"' AND STATUS='ROLLBACK'");
				genRepreturn = insightDao.executeInsertUpdateQuery(stb1.toString());
				log.info(genRepreturn+" setAccountReportsRollBack");
				
			}
			
			
			//log.info(reportSection.toString());
		} catch (Exception e) {
			log.info("Exception in setAccountReportsRollBack");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return "success";
	}
	public String setAllReportsRollBack(int webmartID,UserDTO user) {
		InsightDAO insightDao = null;
		Long repInvreturn=null;
		Long genRepreturn=null;
		int year=0;
		int month=0;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(webmartID);
			insightDao =rmd.getInsightDao();
			year=pubsetting.getLiveYear(webmartID);
			month=pubsetting.getPubLiveMonth(webmartID, year);
			String currentMonth = month<10 ? "0"+month : ""+month;
			log.info("setAllReportsRollBack : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("UPDATE report_inventory SET STATUS='ROLLBACK', updatedBy='"+user.getEmailID()+"', ")
			.append("updated_at=NOW() WHERE MONTH='"+currentMonth+"' AND YEAR='" + year + "' AND ")
			.append("webmart_id='"+webmartID+"'  AND STATUS='LIVE'");
			repInvreturn = insightDao.executeInsertUpdateQuery(stb.toString());
			log.info(repInvreturn+" setAllReportsLive");
			
			StringBuilder stb1 = new StringBuilder();
			stb1.append("UPDATE generated_report SET STATUS='ROLLBACK',")
			.append("updated_by='"+user.getEmailID()+"',updated_at=NOW() WHERE MONTH='"+currentMonth+"' AND ")
			.append("YEAR='"+year+"' AND webmart_id= '"+webmartID+"' AND STATUS='LIVE'  AND ")
			.append("report_id IN (SELECT id FROM reports_master WHERE category")
			.append(" IN (SELECT section FROM `report_sections` rs WHERE webmart_id='"+webmartID+"' AND")
			.append("category='USAGE_REPORTS' ORDER BY rs.order))");
			genRepreturn = insightDao.executeInsertUpdateQuery(stb1.toString());
			log.info(genRepreturn+" setAccountReportsRollBack");
			
			if(repInvreturn==genRepreturn){
				stb1 = new StringBuilder();
				stb1.append("UPDATE report_inventory SET rollback_at=NOW() WHERE MONTH='"+currentMonth+"' AND ")
				.append("YEAR='"+year+"' AND webmart_id='"+webmartID+"' AND STATUS='ROLLBACK'");
				genRepreturn = insightDao.executeInsertUpdateQuery(stb1.toString());
				
			}
		} catch (Exception e) {
			log.info("Exception in setAllReportsLive");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return "success";
	}
	
	public String updatePushLiveSetting(CommonDTO dto) {
		InsightDAO insightDao = null;
		long newId=0;
		String publisher_name;
		String result ="";
		try {
			pubsetting=new PublisherSettings(rmd);
			publisher_name=pubsetting.getPublisherCode(dto.getWebmartID());
			insightDao =rmd.getInsightDao();
			log.info("updatePushLiveSetting : webmart_id=" + dto.getWebmartID());
			StringBuilder stb = new StringBuilder();
			stb.append("UPDATE publisher_settings SET `data_key`='"+dto.getDataKey()+"',")
			.append("`data_value`='"+dto.getDataValue()+"',`updated_by`='"+dto.getUpdatedBy()+"',`updated_at`=NOW() ")
			.append("WHERE webmart_id='"+dto.getWebmartID()+"' AND `data_category`='LIVE_SETTINGS'");
			newId=insightDao.executeInsertUpdateQuery(stb.toString());
			log.info("Updated Successfully  updatePushLiveSetting : "+newId);
		} catch (Exception e) {
			log.info("Exception in updatePushLiveSetting");
			log.info("Updated Failed ");
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return result;
	}
	
	
	//change by satyam 06/02/2019
	public String setQaManualReportsRollBack(int webmartID,String institution_id) {
		InsightDAO insightDao = null;
		String result ="";
		int year=0;
		int month=0;
		long newId=0;
		try {
			String publisher = rmd.getWebmartCode();
			pubsetting=new PublisherSettings(rmd);
			insightDao =rmd.getInsightDao();
			//year=pubsetting.getLiveYear(publisher);
			//month=pubsetting.getLiveMonth(publisher);
			year=	Integer.parseInt(rmd.getLiveYear());
			month= Integer.parseInt(rmd.getLiveMonth());
			String currentMonth = month<10 ? "0"+month : ""+month;
			rmd.log("setAllQaReportsRollBack : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("UPDATE `institution_reports_statistics` SET M_"+year+""+currentMonth+"="+TableMapper.TABALE.get("roll_back")+" WHERE institution_id='"+institution_id+"' AND report_type IN('counter', 'ADDITIONAL') AND M_"+year+""+currentMonth+" != -1");
			newId=insightDao.executeInsertUpdateQuery(stb.toString());
			result="Updated Successfully :AllQaReportsRollBack"+newId;
			rmd.log(result);
		} catch (Exception e) {
			rmd.exception("Updated Failed:AllQaReportsRollBack : Exception in setAllQaReportsLive : "+e.toString()+" : "+result);
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return "success";
	}
	
	//change by satyam 06/02/2019
	public String setAllQaReportsRollBack(int webmartID,UserDTO user) {
		InsightDAO insightDao = null;
		String result ="";
		int year=0;
		int month=0;
		long newId=0;
		try {
			String publisher = rmd.getWebmartCode();
			pubsetting=new PublisherSettings(rmd);
			insightDao =rmd.getInsightDao();
			//year=pubsetting.getLiveYear(publisher);
			//month=pubsetting.getLiveMonth(publisher);
			year=	Integer.parseInt(rmd.getLiveYear());
			month= Integer.parseInt(rmd.getLiveMonth());
			String currentMonth = month<10?"0"+month:""+month;
			rmd.log("setAllQaReportsRollBack : webmart_id=" + webmartID);
			StringBuilder stb = new StringBuilder();
			stb.append("UPDATE `institution_reports_statistics` SET M_"+year+""+currentMonth+"="+TableMapper.TABALE.get("roll_back")+" WHERE report_type IN('counter', 'ADDITIONAL') AND M_"+year+""+currentMonth+" != -1");
			newId=insightDao.executeInsertUpdateQuery(stb.toString());
			result=newId+" : Accounts RolledBack Successfully";
		} catch (Exception e) {
			rmd.exception("Exception in setAllQaReportsLive");
			result=" Updated Failed:AllQaReportsRollBack";
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return result;
	}
	
	
	//change by satyam 06/02/2019
		public String setAllQaReportsLive(int webmartID,UserDTO user) {
			InsightDAO insightDao = null;
			int year=0;
			int month=0;
			String result="";
			try {
				String publisher = rmd.getWebmartCode();
				pubsetting=new PublisherSettings(rmd);
				insightDao =rmd.getInsightDao();
				//year=pubsetting.getLiveYear(publisher);
				year=	Integer.parseInt(rmd.getLiveYear());
				//month=pubsetting.getLiveMonth(publisher);
				month= Integer.parseInt(rmd.getLiveMonth());
				String currentMonth = month<10?"0"+month:""+month;
				rmd.log("set All Qa Reports-> Push Live : webmart_id=" + webmartID);
				StringBuilder stb = new StringBuilder();
				stb.append("UPDATE `institution_reports_statistics` SET M_"+year+""+currentMonth+"="+TableMapper.TABALE.get("push_live")+" WHERE report_type IN('counter', 'ADDITIONAL') AND M_"+year+""+currentMonth+" != -1");
				long newId = insightDao.executeInsertUpdateQuery(stb.toString());
				result=newId+" : Accounts RolledBack Successfully";
				
			} catch (Exception e) {
				rmd.exception("Exception in set All Qa Reports-> Push Live");
				result=" Unable to Push Reports Live 'Please Try Again' ";
				
			} 
			
			return result;
		}

		//saurabh
		public JsonObject getfeederData(String  dataTypeFeed,String begin,String end,int webmartID) {
			JsonObject jo=null;
			String publisher_name;
			InsightDAO insightDao = null;
			MyDataTable mdt = null;
			try {
				pubsetting=new PublisherSettings(rmd);
				publisher_name=pubsetting.getPublisherCode(webmartID);
				insightDao =rmd.getInsightDao();

				// executing Query
				StringBuilder stb = new StringBuilder();
				

				
			/*	String from = "01-2018";
				String to = "11-2019";*/
				
				String monthArr[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sept","Oct","Nov","Dec"};
				int countMonth =0;
				StringBuffer sb = new StringBuffer();
				
				String[] fromAray = begin.split("-");
				String[] toAray = end.split("-");
				
				 if((Integer.parseInt(toAray[1])-Integer.parseInt(fromAray[1]))==0)
				 {
					 		
					for(int i=Integer.parseInt(fromAray[0]);i<=Integer.parseInt(toAray[0]);i++) {
						
						if(i>9){
							sb.append("M_"+fromAray[1]+i+",");
							//	sb.append(monthArr[i-1]+"_"+toAray[1]+",");
								countMonth++;
						}else{
							sb.append("M_"+fromAray[1]+"0"+i+",");
							//	sb.append(monthArr[i-1]+"_"+toAray[1]+",");
								countMonth++;
							
						}
							
						}
						System.out.println(sb);
				 }
				
				 if((Integer.parseInt(toAray[1])-Integer.parseInt(fromAray[1]))==1) {
					 
					 
					 int fromMonthIndex=Integer.parseInt(fromAray[0]);
					 			 
					 for(int i=fromMonthIndex;i<=12;i++) {
							
							if(i>9){
								sb.append("M_"+fromAray[1]+i+",");
								//	sb.append(monthArr[i-1]+"_"+toAray[1]+",");
									countMonth++;
							}else{
								sb.append("M_"+fromAray[1]+"0"+i+",");
								//	sb.append(monthArr[i-1]+"_"+toAray[1]+",");
									countMonth++;
								
							}
							
						}
					 int toMonthIndex=Integer.parseInt(toAray[0]);
		 			 
					 for(int i=1;i<=toMonthIndex;i++) {
							
							if(i>9){
								sb.append("M_"+toAray[1]+i+",");
								//	sb.append(monthArr[i-1]+"_"+toAray[1]+",");
									countMonth++;
							}else{
								sb.append("M_"+toAray[1]+"0"+i+",");
								//	sb.append(monthArr[i-1]+"_"+toAray[1]+",");
									countMonth++;
								
							}
							
						}
					 
				 }
				 System.out.println("--------"+sb);
				 if((Integer.parseInt(toAray[1])-Integer.parseInt(fromAray[1]))==2) {
					 
					 int fromMonthIndex=Integer.parseInt(fromAray[0]);
					 int toMonthIndex=Integer.parseInt(toAray[0]);
					int countLoop=Integer.parseInt(fromAray[1]); 
					 for(int checkYearRange=Integer.parseInt(fromAray[1]);checkYearRange<=Integer.parseInt(toAray[1]);checkYearRange++) {
						 
						 System.out.println(checkYearRange);
						 
						 if(countLoop==Integer.parseInt(fromAray[1])) {
							 
							 for(int query=fromMonthIndex;query<=12;query++) {
								 
								 sb.append(monthArr[query-1]+"_"+checkYearRange+",");
								 countMonth++;
								 
							 }	 
						 }
						 
						 if(countLoop==Integer.parseInt(fromAray[1])+1) {
							 
							 for(int query=0;query<12;query++) {
								 
								 sb.append(monthArr[query]+"_"+checkYearRange+",");
								 countMonth++;
								 
							 }	 
						 }
						 
						 if(countLoop==Integer.parseInt(toAray[1])) {
							 
							 for(int query=0;query<toMonthIndex;query++) {
								 
								 sb.append(monthArr[query]+"_"+checkYearRange+",");
								 countMonth++;
								 
							 }	 
						 }
						 countLoop++;
						 
					 }
				 }
				
				
			
				
				
				stb.append("Select TITLE_ID,TITLE,TITLE_TYPE,DATA_TYPE,ACCESS_TYPE,PUBLISHER,ISNI,PROPRIETARY_ID,PLATFORM,DOI,ISBN,PRINT_ISSN,ONLINE_ISSN,URI,file_name,Status," +sb+" TimeStamp from c5_title_feed_master WHERE data_type='"+dataTypeFeed+"'" );

				System.out.println(stb);
				
				
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				jo=mdt.getJson();
				
				//Print Query 
				if(stb.length()>200){
					rmd.log("getPubDashBoardView  : Query  =" + stb.toString().substring(0, 199) + " : Query : Success : MDT Size : "+mdt.getRowCount());
				}else{
					rmd.log("getPubDashBoardView : Query  =" + stb.toString() + " : Query : Success : MDT Size : "+mdt.getRowCount());
				}
				
			} catch (Exception e) {
				rmd.exception("Exception in getFullTextDetail... ");
			} finally {
			}
			return jo;
			
		}
}
