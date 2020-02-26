package com.mps.insight.c5.report.publisher;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.TableMapper;
import com.mps.insight.product.Counter5ReportsDao;

public class HighestAccessIP {

	private Counter5DTO dto = null;
	private Counter5ReportsDao c5dao = null;
	public HighestAccessIP(Counter5DTO c5Dto, RequestMetaData rmd) {

		try {

			this.dto = c5Dto;
		    c5dao = new Counter5ReportsDao(rmd);
		} catch (Exception e) {

		}
	}

	public MyDataTable getReport(String download) {
		MyDataTable mdt = null;
		String query = "";
		try {

			query = this.getQuery(download);
			mdt = c5dao.getDynamicReportJson(dto.getWebmartID(), query);
			return mdt;
		} catch (Exception e) {
			throw e;
		}
	}

	private String getQuery(String download) {
		String[] toarr= null;
		int toyear=0;
		int tomonth=0;
		int todate=0;
		StringBuilder query=new StringBuilder();
		String tableName =TableMapper.TABALE.get("c5_ip_usage");
		try {
			
			toarr=dto.getToDate().split("-");
		
			toyear=Integer.parseInt(toarr[2]);
			tomonth=Integer.parseInt(toarr[1]);
			todate=Integer.parseInt(toarr[0]);
			
			/*StringBuilder colomnname=new StringBuilder();
			colomnname.append("D_"+toyear);
			if(tomonth<10){
				colomnname.append("0");
			}
			colomnname.append(tomonth);
			if(todate<10){
				colomnname.append("0");
			}
			colomnname.append(todate);
			*/
			query.append("SELECT @a:=@a+1 AS 'Sr_no', b.* FROM( ");
			query.append("SELECT page_view AS 'Page_Views', ip_address AS 'IP_Address', institution_name AS 'Institution', institution_code AS 'Institution_Id' ");  
			query.append("FROM  "+tableName+"  WHERE institution_name NOT LIKE '%web crawlers%' AND year='"+toyear+"' and month='"+tomonth+"' and DAY='"+todate+"' AND institution_id NOT IN('Other Total') ORDER BY page_view DESC ) b, (SELECT @a:= 0) AS a ");
			query.append("UNION ALL ");
			query.append("SELECT  'Other' AS 'Sr_no', c.* FROM (SELECT page_view AS 'Page_Views', '' AS 'IP_Address', '' AS 'Institution', '' AS 'Institution_Id'  ");
			query.append("FROM  "+tableName+"  WHERE year='"+toyear+"' and month='"+tomonth+"' and DAY='"+todate+"' AND institution_id IN('Other Total') "); 
			query.append(") c ");

			query.append("UNION ALL ");
			query.append("SELECT  'Total' AS 'Sr_no', SUM(page_view) AS 'Page_Views', '' AS 'IP_Address', '' AS 'Institution', '' AS 'Institution_Id' ");  
			query.append("FROM  "+tableName+" WHERE year='"+toyear+"' and month='"+tomonth+"' and DAY='"+todate+"' "); 
					 
			/*		 
			query.append("SELECT Page_Views, IP_Address, Institution, Institution_Id FROM ")
			.append("(SELECT "+colomnname.toString()+" AS 'Page_Views', ip_address AS 'IP_Address',")
			.append("institution_name AS 'Institution', institution_code AS 'Institution_Id'  ")
			.append("FROM "+tableName+" WHERE institution_name NOT LIKE '%web crawlers%' ORDER BY "+colomnname.toString()+" DESC LIMIT 50")
			.append(")a UNION ALL SELECT SUM("+colomnname.toString()+"), '' AS ip_address, '' AS institution_name, '' AS institution_code FROM ")
			.append("(SELECT "+colomnname.toString()+", '' AS ip_address, '' AS institution_name, '' AS institution_code FROM "+tableName+" ")
			.append(" WHERE institution_name NOT LIKE '%web crawlers%' ORDER BY "+colomnname.toString()+"  DESC LIMIT 50,100000000) b ")
			.append("UNION ALL SELECT SUM("+colomnname.toString()+"), '' AS ip_address, '' AS institution_name, '' AS institution_code ")
			.append("FROM "+tableName+" WHERE institution_name NOT LIKE '%web crawlers%'");
			*/
			if(download.equalsIgnoreCase("preview")){
				query.append(" Limit 500");
			}else{
				query.append("");
			}
			return query.toString();
		} catch (Exception e) {
			//
			throw e;
		}
	}

}
