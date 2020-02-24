package com.mps.insight.global;
import java.util.HashMap;

public class TableMapper {

	
	public static final HashMap<String, String> TABALE = new HashMap<String, String>() {
		{
			// ####################  user_master  #########################
			put("user_table", "c5_user_master"); 
		    put("user_code", "user_code"); 
		    put("email_id", "email_id"); 
		    put("role_id", "role_id"); 
		    put("first_name", "first_name"); 
		    put("last_name", "last_name"); 
		    put("password", "password"); 
		    put("lp1", "lp1"); 
		    put("lp2", "lp2"); 
		    put("lp3", "lp3"); 
		    put("status", "`status`"); 
		    put("user_type", "user_type"); 
		    put("question", "question"); 
		    put("answer", "answer"); 
		    put("email_alert", "email_alert"); 
		    put("sushi", "sushi"); 
		    put("last_login", "last_login"); 
		    put("api_key", "api_key");
		 //   ####################  user_account_master  #########################
		    put("user_account_table", "c5_user_account_master"); 
		    put("account_code", "account_code");
		    put("role_id", "`role_id`");
		    put("status", "`status`");
		    put("description", "`description`");
		    
		    
		//   ####################  c5_account  #########################
		    put("account_table", "c5_accounts"); 
		    put("alphabet", "`ALPHABET`");
		    put("account_name", "`ACCOUNT_NAME`");
		    put("a_account_code", "`ACCOUNT_CODE`");
		    put("consortium_incl", "`CONSORTIUM_INCL`");
		    put("account_type", "`ACCOUNT_TYPE`");
		    put("product_code", "`PRODUCT_CODE`");
		    put("type", "`TYPE`");
		    put("state", "`STATE`");
		    put("country", "`COUNTRY`");
		    put("region", "`REGION`");
		    put("generate_reports", "`generate_reports`");
		    
		    
		//   ####################  c5_user_favorites  #########################
		    put("user_favorites_table", "c5_user_favorites"); 
		    put("uf_user_code", "`user_code`");
		    put("uf_account_code", "`account_code`");
		    put("uf_level", "`level`");
		
		//   ####################  c5_sushi_partner_accounts  #########################
		    put("sushi_partner_accounts_table", "c5_sushi_partner_accounts"); 
		    put("sp_user_code", "`user_code`");
		    put("sp_account_code", "`account_code`");
		
		 //################  Set report Code Staus for Rollback and Push Live ###############
		    put("roll_back", "2");
		    put("push_live", "1");
		    put("push_QA", "3");
		  /**********Counter-5 Report Table***************/
		    put("master_report_table","master_report");
		    put("c5_ebook_output_and_details_customer_details_table","c5_ebook_output_and_details_customer_details");
		    put("dr_master_table","dr_master");
		    put("tr_master_table","tr_master");
		    put("c5_ip_usage","c5_ip_usage");
		    put("c5_site_summary","c5_site_summary");
		    put("c5_accounts", "c5_accounts");
		    put("c5_account_parent_child", "c5_account_parent_child");
		    put("c5_ref_data_type","c5_ref_data_type");
		    put("c5_ref_access_type", "c5_ref_access_type");
		    put("c5_ref_access_method", "c5_ref_access_method");
		    put("c5_ip_address", "c5_ip_address");
		    put("c5_site_overview_by_month_new", "c5_site_overview_by_month_new");
		    put("c5_article_request_by_title","c5_article_request_by_title");
		    put("c5_dashboard_all", "c5_dashboard_all");
		    put("c5_title_feed_master", "c5_title_feed_master");
		    put("pr_master","pr_master");
		    
		}
	  
	};
	
		
}
