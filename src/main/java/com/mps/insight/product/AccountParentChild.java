package com.mps.insight.product;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.AccountDTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.TableMapper;

public class AccountParentChild {
	RequestMetaData rmd = null;
	
	//private static final Logger LOG = LoggerFactory.getLogger(AccountParentChild.class);
	PublisherSettings pubsetting=null;
	
	public AccountParentChild(RequestMetaData rmd) {
		this.rmd =rmd;	
	}

	public MyDataTable getParents(int webmartID,String code) throws Exception{
		double iTracker=0.0;
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			rmd.log("getParents : webmart_id=" + webmartID);
			iTracker=3.0;
			int setNo=pubsetting.getSetNo(webmartID);
			StringBuilder stb = new StringBuilder();
			iTracker=4.0;
			
			stb.append("SELECT a.account_code AS 'ID', a.account_code AS 'code', ") 
			.append("a.account_name AS `Account Name`, ") 
			.append("a.account_type AS `Account Type` ") 
			.append("FROM `"+TableMapper.TABALE.get("c5_accounts")+"` a ")
			.append("WHERE `status`=1 AND a.account_type !='INSTITUTION' ") 
			.append("AND a.account_code in ( ")
				.append("SELECT b.account_code AS 'parent_id' ") 
				.append("FROM `"+TableMapper.TABALE.get("c5_account_parent_child")+"` b ") 
				.append("WHERE `status`=1 AND b.child_account_code in('"+code+"'))");
					
					
		/*	stb.append("SELECT a.id AS ID,a.code AS code,a.name AS `Account Name`,a.type AS `Account Type`") 
			.append(" FROM accounts a WHERE a.set_no='"+setNo+"' AND a.type='Group' ")
			.append("AND a.code=(SELECT b.parent_id FROM account_parent_child b WHERE ")
			.append("b.set_no='"+setNo+"' AND b.child_id='"+code+"')");*/
			
			
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
		} catch (Exception e) {
			rmd.exception("AccountParentChild : getParents : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return mdt;
	}
	
	public MyDataTable getChild(int webmartID,String code) throws Exception{
		double iTracker=0.0;
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			rmd.log("getChild : webmart_id=" + webmartID);
			iTracker=3.0;
			StringBuilder stb = new StringBuilder();
			iTracker=4.0;
			
			stb.append("SELECT a.account_code AS `ID`, ") 
			.append("a.account_code AS `code`, ") 
			.append("a.account_name AS `Account Name`, ")
			.append("a.account_type AS `Account Type` ") 
			.append("FROM "+TableMapper.TABALE.get("c5_accounts")+" a ") 
			.append("WHERE `status`='1' AND a.account_type='Institution' ") 
			.append("AND a.account_code IN (")
					.append("SELECT b.child_account_code AS 'child_id' ") 
					.append("FROM "+TableMapper.TABALE.get("c5_account_parent_child")+" b ")
					.append("WHERE `status`=1 AND b.account_code='"+code+"')");
			
			/*
			stb.append("SELECT a.id AS ID,a.code AS code,a.name AS `Account Name`,a.type AS `Account Type`") 
			.append(" FROM accounts a WHERE a.set_no='"+setNo+"' AND a.type='Institution' AND ")
			.append("a.code IN (SELECT b.child_id FROM account_parent_child b WHERE ")
			.append("b.set_no='"+setNo+"' AND b.parent_id='"+code+"')");
			*/
			
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			
			if(mdt.getRowCount()<1){
				StringBuilder query = new StringBuilder();
				query.append("SELECT a.id AS 'ID',  a.code AS 'code', a.name AS 'Account Name', a.type AS 'Account Type' ")
				.append("FROM accounts a WHERE a.id IN ")
				.append("(SELECT child_id FROM `account_parent_child` acp ")
				.append("WHERE parent_id=")
				.append("(SELECT id FROM accounts a WHERE CODE = '"+code+"' AND set_no=")
				.append("(SELECT MAX(set_no) FROM accounts))) ORDER BY NAME"); 
				mdt = insightDao.executeSelectQueryMDT(query.toString());
			}
		} catch (Exception e) {
			rmd.exception("AccountParentChild : getChild : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return mdt;
	}
	
	public MyDataTable getParentAccounts(String accountCode,int setNo,int webmartID) throws Exception{
		double iTracker=0.0;
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		AccountDTO adto=null;
		Account account=new Account(rmd);
		
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			iTracker=3.0;
			adto=account.getAccountByCodeAndSetNO(accountCode, setNo,webmartID);
			StringBuilder stb = new StringBuilder();
			iTracker=4.0;
			stb.append("SELECT a.id AS ID,a.code AS accountCode,a.name AS accountName,a.type AS accountType ")
			.append("FROM accounts a WHERE a.set_no='"+setNo+"' AND a.id IN (SELECT b.parent_id FROM ")
			.append("accounts a LEFT JOIN account_parent_child b ON a.id=b.child_id ")
			.append("WHERE a.set_no='"+setNo+"' AND b.child_id='"+adto.getId()+"')");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
		} catch (Exception e) {
			rmd.exception("AccountParentChild : getParentAccounts : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return mdt;
	}

	public String getAccountType(String accountCode) {
		Double iTracker=0.0;
		String accountType ="";
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			 String publisher_name = pubsetting.getPublisherCode(rmd.getWebmartID());
			iTracker=2.0;
			InsightDAO insightDao = rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			iTracker=4.0;
			stb.append("SELECT * FROM `"+TableMapper.TABALE.get("c5_accounts")+"` WHERE account_code='"+accountCode+"'");
			
			MyDataTable mdt = insightDao.executeSelectQueryMDT(stb.toString());
			accountType = mdt.getValue(1, "account_type");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return accountType;
	}

}
