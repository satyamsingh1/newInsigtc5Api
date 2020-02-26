package com.mps.insight.impl;

import javax.json.JsonObject;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.AccountParentChild;
import com.mps.insight.product.ManageUser;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.product.SearchLibUser;
import com.mps.redis.Redis;

public class LibraryUserManager {
	
	private RequestMetaData rmd= null;
	
	ManageUser usermanager=new ManageUser(rmd);
	
	
	public LibraryUserManager(RequestMetaData rmd){
		this.rmd =rmd;
	}

	public JsonObject searchLibUser(int webmartID,String accountCode) throws Exception {
		Double iTracker =0.0; 
		MyDataTable mdt=null;
		PublisherSettings pubSetting=null;
		SearchLibUser libUser=null;
		AccountParentChild apc=null;
		String accounts="'"+accountCode+"'";
		String users="";
		
		try{
			iTracker =1.0; 
			pubSetting=new PublisherSettings(rmd);
			iTracker =2.0;
			apc=new AccountParentChild(rmd);
			iTracker =3.0;
			int setNo=0;
			if(setNo==0){
				iTracker =4.0;
				setNo=pubSetting.getSetNo(webmartID);
			}
			
			iTracker =5.0;
			libUser=new SearchLibUser(rmd);
			iTracker =6.0;
			mdt=apc.getParentAccounts(accountCode, setNo,webmartID);
			iTracker =7.0;
			int rowCount=mdt.getRowCount();
			for(int i=1;i<=rowCount;i++){
				accounts=accounts+",'"+mdt.getValue(i, "accountCode")+"'";
			}
			iTracker =8.0;
			mdt=libUser.getAllLibUserByPubID(webmartID);
			iTracker =9.0;
			rowCount=mdt.getRowCount();
			for(int i=1;i<=rowCount;i++){
				iTracker =10.0;
				users=users+",'"+mdt.getValue(i, "userID")+"'";
			}
			iTracker =11.0;
			users=users.substring(1);
			iTracker =12.0;
			mdt=libUser.searchAccountAllLibUser(users, accounts,webmartID);
			
		}catch(Exception e){
			rmd.exception("LibraryUserManager : searchLibUser : iTracker : "+iTracker+" : "+e.toString());
		}
		
		return mdt.getJson();
	}
	
	public JsonObject searchAccountLibUser(int webmartID,String accountCode,String search) throws Exception {
		Double iTracker = 0.0; 
		MyDataTable mdt=null;
		PublisherSettings pubSetting=null;
		SearchLibUser libUser=null;
		AccountParentChild apc=null;
		String accounts="'"+accountCode+"'";
		String users="";
		Redis redis=new Redis();
		try{
			iTracker = 1.0; 
			pubSetting=new PublisherSettings(rmd);
			iTracker = 2.0; 
			apc=new AccountParentChild(rmd);
			int setNo=0;
			iTracker = 3.0; 
			setNo=Integer.parseInt(redis.getValueFromRedisWithKey(webmartID+"setNo"));
			if(setNo==0){
				setNo=pubSetting.getSetNo(webmartID);
			}
			iTracker = 4.0; 
			libUser=new SearchLibUser(rmd);
			iTracker = 5.0; 
			mdt=apc.getParentAccounts(accountCode, setNo,webmartID);
			int rowCount=mdt.getRowCount();
			for(int i=1;i<=rowCount;i++){
				accounts=accounts+",'"+mdt.getValue(i, "accountCode")+"'";
			}
			iTracker = 6.0; 
			mdt=libUser.searchLibUser(search,webmartID);
			iTracker = 7.0; 
			rowCount=mdt.getRowCount();
			for(int i=1;i<=rowCount;i++){
				users=users+",'"+mdt.getValue(i, "userID")+"'";
			}
			iTracker = 8.0; 
			users=users.substring(1);
			iTracker = 9.0; 
			mdt=libUser.searchAccountAllLibUser(users, accounts,webmartID);
			
		}catch(Exception e){
			rmd.exception("LibraryUserManager : searchAccountLibUser : iTracker : "+iTracker+" : "+e.toString());
		}
		
		return mdt.getJson();
	}
	
	public String deleteLibUser(String userCode,int webmartID) {
		
		return usermanager.deleteUser(userCode,webmartID);
	}

}
