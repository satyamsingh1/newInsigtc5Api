package com.mps.insight.c5.report.publisher;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.OaDto;

public class Oa_report {
       
	public ArrayList<OaDto> getReport(Counter5DTO dto,Connection conn) throws SQLException {
	//	ArrayList<OaDto> records = new ArrayList<>();
		Map<String, Integer> oagoldMap = new HashMap<>();
		String[] toarr = dto.getToDate().split("-");
		ResultSet result_set_master =null;
		
		if(null==dto.getInstitutionID()){
			Statement stmt = conn.createStatement();
			String q1 = "SELECT user_inst_id as 'title_id', SUM(pdf_downloads+html_downloads) as 'totalCount' FROM `item_journal_oa` where YEAR='"+toarr[1]+"' AND MONTH='"+toarr[0]+"' GROUP BY user_inst_id ";
			ResultSet rs = stmt.executeQuery(q1);
			while (rs.next()) {
				
				oagoldMap.put(String.valueOf(rs.getInt("title_id")), rs.getInt("totalCount"));
			}

			String q2 = "SELECT "
					+ "oa.title_id AS 'UserID', "
					+ "oa.title_name AS 'Customer', "
					+ "oa.access_type, "
					+ "SUM(oa.M_"+toarr[1]+""+toarr[0]+") AS 'counts' "
							+ "FROM c5_oa_publisher oa "
							+ "LEFT JOIN c5_accounts a ON oa.title_id=a.account_code "
							+ "WHERE metric_type='Total_Item_Requests' "
							+ "AND a.M_"+toarr[1]+""+toarr[0]+">0 "
									+ "GROUP BY oa.title_id, oa.title_name, oa.access_type "
									+ "ORDER BY oa.title_id, oa.access_type ASC";
			result_set_master = stmt.executeQuery(q2);
			
			return getRecordsDtaPublisher(result_set_master, oagoldMap);
			
		}else{
			Statement stmt = conn.createStatement();
			String q1 = "SELECT user_inst_id, title_id, SUM(total) AS 'totalCount' FROM `item_journal_oa` WHERE user_inst_id='"+dto.getAccountID()+"' AND YEAR='"+toarr[1]+"' AND MONTH='"+toarr[0]+"' GROUP BY user_inst_id, title_id";
			ResultSet rs = stmt.executeQuery(q1);
			while (rs.next()) {
				
				oagoldMap.put(String.valueOf(rs.getInt("title_id")), rs.getInt("totalCount"));
			}

			String q2 = "SELECT institution_id, title_id, parent_title, data_type,access_type, SUM(M_"+toarr[1]+""+toarr[0]+") AS 'counts' FROM master_report WHERE institution_id='"+dto.getAccountID()+"' AND metric_type='Total_Item_Requests' AND M_"+toarr[1]+""+toarr[0]+">0 GROUP BY institution_id, title_id, parent_title, data_type,access_type ORDER BY title_id, access_type ASC";
			result_set_master = stmt.executeQuery(q2);
			
			return getRecordsDtaLibrary(result_set_master, oagoldMap);
		}
	}
	
	
	
	private ArrayList<OaDto> getRecordsDtaPublisher(ResultSet result_set_master, Map<String, Integer> oagoldMap ){
		ArrayList<OaDto> records = new ArrayList<>();
		
		try {
			String customer_prev = "";
			
			while (result_set_master.next()) {
				
				if (result_set_master.getString("Customer").equals(customer_prev)) {
					try {
						OaDto rec = records.get(records.size() - 1);
						int tatalCount = rec.getTotalUses() + result_set_master.getInt("counts");
						rec.setTotalUses(tatalCount);
						rec.setOaUses(result_set_master.getInt("counts"));
						records.set(records.size() - 1, rec);
					} catch (Exception e) {
						// TODO: handle exception
					}

				} else {
					try {
						OaDto rec = new OaDto();
						rec.setTitleId(result_set_master.getString("UserID"));
						rec.setTitleName(result_set_master.getString("Customer"));
						//rec.setDataType(result_set_master.getString("data_type"));
						rec.setTotalUses(result_set_master.getInt("counts"));
						// rec.setOaUses(rs1.getInt(""));
						int val = oagoldMap.get(result_set_master.getString("UserID")) != null ? oagoldMap.get(result_set_master.getString("UserID"))
								: 0;
						rec.setSuscribOa(val);
						records.add(rec);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				customer_prev = result_set_master.getString("Customer");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return records;
	}
	
	
	
	private ArrayList<OaDto> getRecordsDtaLibrary(ResultSet result_set_master, Map<String, Integer> oagoldMap ){
		ArrayList<OaDto> records = new ArrayList<>();
		try {
			String parent_title_prev = "";
			
			while (result_set_master.next()) {
				
				if (result_set_master.getString("parent_title").equals(parent_title_prev)) {
					OaDto rec = records.get(records.size() - 1);
					int tatalCount = rec.getTotalUses() + result_set_master.getInt("counts");
					rec.setTotalUses(tatalCount);
					rec.setOaUses(result_set_master.getInt("counts"));
					records.set(records.size() - 1, rec);

				} else {
					OaDto rec = new OaDto();
					rec.setTitleId(result_set_master.getString("title_id"));
					rec.setTitleName(result_set_master.getString("parent_title"));
					rec.setDataType(result_set_master.getString("data_type"));
					rec.setTotalUses(result_set_master.getInt("counts"));
					// rec.setOaUses(rs1.getInt(""));
					int val = oagoldMap.get(result_set_master.getString("title_id")) != null ? oagoldMap.get(result_set_master.getString("title_id"))
							: 0;
					rec.setSuscribOa(val);
					records.add(rec);
				}
				parent_title_prev = result_set_master.getString("parent_title");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return records;
	}
}
