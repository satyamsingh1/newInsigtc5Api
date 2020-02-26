package com.mps.insight.global;

import com.mps.insight.dto.RequestMetaData;

public class UserInfo {

	public void saveInfo(RequestMetaData rmd){
		try{
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO `c5_user_activity` (`publisher`,`usr_type`,`user_code`,`activity_type`,`activity_detail`,`activity_date`, `user_ip`, `state`) ")
			.append("VALUES(")
			.append("'"+rmd.getWebmartCode()+"', ")
			.append("'"+rmd.getUserType()+"', ")
			.append("'"+rmd.getUserCode()+"', ")
			.append("'"+rmd.getUserActivity()+"', ")
			.append("'"+rmd.toString()+"', ")
			.append("NOW(), ")
			.append("'"+rmd.getRemoteIP()+"', ")
			.append("'"+rmd.getResponceStatus()+"')");
			
			rmd.getInsightDao().executeDDLQuery(query.toString());
			
		}catch(Exception e){
			
		}
		
	}
	
}
