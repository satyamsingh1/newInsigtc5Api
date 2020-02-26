package com.mps.insight.c5.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.AccountDTO;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;
import com.mps.insight.product.Account;
import com.mps.insight.product.Counter5ReportsDao;
import com.mps.redis.Redis;

public class Counter5ReportAttributImpl {

	private RequestMetaData rmd = null;

	public Counter5ReportAttributImpl(RequestMetaData rmd) {
		this.rmd = rmd;
	}

	Counter5ReportsDao c5dao = null;
	String tr_b1 = "TR_B1";
	String tr_b2 = "TR_B2";
	String tr_b3 = "TR_B3";
	String tr_j1 = "TR_J1";
	String tr_j2 = "TR_J2";
	String tr_j3 = "TR_J3";
	String tr_j4 = "TR_J4";

	public String getReportsTitle(String report) {
		StringBuilder title = new StringBuilder();

		if (report.equalsIgnoreCase(tr_j1) || report.equalsIgnoreCase(tr_j2) || report.equalsIgnoreCase(tr_j3)) {
			title.append(InsightConstant.TR_JOURNAL_MASTER_TITLE);
		} else if (report.equalsIgnoreCase(tr_b1) || report.equalsIgnoreCase(tr_b2) || report.equalsIgnoreCase(tr_b3)
				|| report.equalsIgnoreCase("TR")) {
			title.append(InsightConstant.TR_BOOK_MASTER_TITLE);
		} else if (report.equalsIgnoreCase("IR_A1")) {
			title.append(InsightConstant.IR_A1_MASTER_TITLE_P1).append(InsightConstant.IR_A1_MASTER_TITLE_P2);
		} else if (report.equalsIgnoreCase(tr_j4)) {
			title.append(InsightConstant.TR_JOURNAL4_MASTER_TITLE);
		} else if (report.equalsIgnoreCase("IR_M1")) {
			title.append(InsightConstant.IR_M1_MASTER_TITLE);
		} else if (report.equalsIgnoreCase("EDU_COUR")) {
			title.append(InsightConstant.EDU_COUR);
		} else if (report.equalsIgnoreCase("TR_NO_LIC_DEN")) {
			title.append(InsightConstant.TR_NO_LICEN_DENI);
		}
		if (report.equalsIgnoreCase("PR_P1") || report.equalsIgnoreCase("PR")) {
			title.append(InsightConstant.PR_MASTER_TITLE);
		} else if (report.equalsIgnoreCase("DR_D1") || report.equalsIgnoreCase("DR_D2")
				|| report.equalsIgnoreCase("DR")) {
			title.append(InsightConstant.DR_MASTER_TITLE);
		} else if (report.equalsIgnoreCase("IP_ART_REQ_MONTH")) {
			title.append(InsightConstant.IP_ARTICLE_TITLE);
		} else if (report.equalsIgnoreCase("ART_REQ_TITLE")) {
			title.append(InsightConstant.ARTICLE_REQUEST_TITLE);
		} else if (report.equalsIgnoreCase("TR_STD") || report.equalsIgnoreCase("TR_CONF")) {
			title.append(InsightConstant.TR_JOURNAL_MASTER_TITLE);
		} else if (report.equalsIgnoreCase("TR_J1_GOA")) {
			title.append(InsightConstant.J1_GOA_MASTER_TITLE);
		} else if (report.equalsIgnoreCase("IP_Article_request_search")) {
			title.append(InsightConstant.IP_ARTICLE_SEARCH);
		} else if (report.equalsIgnoreCase("IP_Article_request_views")) {
			title.append(InsightConstant.IP_ARTICLE_VIEW);
		} else if (report.equalsIgnoreCase("EBOOK_INVEST_TITLE")) {
			title.append(InsightConstant.EBOOK_INVEST_TITLE);
		}

		if (report.equalsIgnoreCase("MIT_EBOOK_CHAPTER")) {
			title.append(InsightConstant.MIT_EBOOK_CHAPTER);
		} else if (report.equalsIgnoreCase("WILEY_IEEE_EBOOK_CHAPTER")) {
			title.append(InsightConstant.WILEY_IEEE_EBOOK_CHAPTER);
		} else if (report.equalsIgnoreCase("TURNAWAY_BY_TYPE")) {
			title.append(InsightConstant.TURNAWAYS_BY_TYPE);
		} else if (report.equalsIgnoreCase("PARTNER_REQ_BY_TYPE")) {
			title.append(InsightConstant.PARTNER_REQUEST_BY_TYPE);
		} else if (report.equalsIgnoreCase("IR")) {
			title.append(InsightConstant.IR_MASTER_TITLE_1 + "," + InsightConstant.IR_MASTER_TITLE_2);
		}

		return title.toString();
	}

	public String getReportsTable(String report) throws Exception {
		String table = "";
		if (report.startsWith("TR_") && !report.equalsIgnoreCase(tr_j4) && !report.equalsIgnoreCase("TR_J1_GOA")) {
			table = TableMapper.TABALE.get("master_report_table");
		} else if (report.equalsIgnoreCase(tr_j4) || report.startsWith("IR_") || report.equalsIgnoreCase("TR_J1_GOA")) {
			table = TableMapper.TABALE.get("master_report_table");
		} else if (report.equalsIgnoreCase("PR_P1") || report.equalsIgnoreCase("PR")) {
			table = "pr_master";
		} else if (report.equalsIgnoreCase("DR_D1") || report.equalsIgnoreCase("DR_D2")
				|| report.equalsIgnoreCase("DR")) {
			table = TableMapper.TABALE.get("dr_master_table");
		} else if (report.equalsIgnoreCase("IP_ART_REQ_MONTH") || report.equalsIgnoreCase("IP_Article_request_search")
				|| report.equalsIgnoreCase("IP_Article_request_views")) {
			// table="ip_address";
			table = TableMapper.TABALE.get("c5_ip_address"); // change by ksv
																// for prefix of
																// C5 in IP
			// table 20180608
		} else if (report.equalsIgnoreCase("ART_REQ_TITLE")) {
			table = TableMapper.TABALE.get("c5_article_request_by_title");
		} else if (report.equalsIgnoreCase("EBOOK_INVEST_TITLE") || report.equalsIgnoreCase("EDU_COUR")) {
			table = TableMapper.TABALE.get("master_report_table");
		}
		if (report.equalsIgnoreCase("PARTNER_REQ_BY_TYPE")) {
			table = TableMapper.TABALE.get("master_report_table");
		} else if (report.equalsIgnoreCase("MIT_EBOOK_CHAPTER") || report.equalsIgnoreCase("WILEY_IEEE_EBOOK_CHAPTER")
				|| report.equalsIgnoreCase("IR")) {
			table = TableMapper.TABALE.get("master_report_table");
		} else if (report.equalsIgnoreCase("TURNAWAY_BY_TYPE") || report.equalsIgnoreCase("TR")) {
			table = TableMapper.TABALE.get("master_report_table");
		} else {
			throw new Exception("No Table found for report : " + report);
		}

		return table;
	}

	public JsonArray geHeaderJson(HashMap<String, String> report) {

		JsonObjectBuilder header = Json.createObjectBuilder();
		JsonArrayBuilder arr = Json.createArrayBuilder();
		for (Map.Entry<String, String> entry : report.entrySet()) {
			header.add("key", entry.getKey());
			header.add("value", entry.getValue());
			arr.add(header);
		}

		return arr.build();
	}

	public String getReportsTitleCustom(Counter5DTO dto) {
		StringBuilder title = new StringBuilder();
		String attributshow = dto.getReportAttributes();
		if (null != attributshow) {
			title.append(attributshow);
		}
		if (dto.getReportCode().equalsIgnoreCase(tr_b3) || dto.getReportCode().equalsIgnoreCase(tr_j3)) {
			title.append("`Access_Type`,");
		}
		if (dto.getReportCode().equalsIgnoreCase(tr_j4)) {
			title.append("`YOP`,");
		}
		if (dto.getReportCode().equalsIgnoreCase("TR") || dto.getReportCode().equalsIgnoreCase("IR")) {
			title.append("`Data_Type`,`Section_Type`,`YOP`,`Access_Type`,`Access_Method`,");
		}
		if (dto.getReportCode().equalsIgnoreCase("DR")) {
			title.append("`Data_Type`,`YOP`,`Access_Type`,`Access_Method`,");
		}
		if (dto.getReportCode().equalsIgnoreCase("PR")) {
			title.append("`YOP`,`Data_Type`,`Access_Type`,`Access_Method`,");
		}
		title.append("`Metric_Type`,");

		return title.toString();
	}

	public String createWhereCondition(Counter5DTO dto) {
		StringBuilder sb = new StringBuilder();
		// Webmart webmart=MyMetaData.webmart();
		sb.append("Institution_ID='" + dto.getInstitutionID() + "'");
		String accessMethod = "AND Access_Method='Regular' ";
		String accessType = "AND Access_Type='Controlled' ";
		String deniedMetricType = " AND Metric_Type IN ('Limit_Exceeded','No_License') ";
		String groupBy = "";
		if (dto.getReportCode().equalsIgnoreCase("TR") || dto.getReportCode().equalsIgnoreCase("DR")
				|| dto.getReportCode().equalsIgnoreCase("PR") || dto.getReportCode().equalsIgnoreCase("IR")) {

			if (null != dto.getAccessType() && dto.getAccessType().length() > 2) {
				String str = "";
				String[] accType = dto.getAccessType().split(",");
				for (String string : accType) {
					str = str + "'" + string.trim() + "',";
				}
				str = str.substring(0, str.lastIndexOf(","));
				sb.append(" and Access_Type IN (" + str + ")");
			}
			if (null != dto.getAccessMethod() && dto.getAccessMethod().length() > 2) {
				String str = "";
				String[] accType = dto.getAccessMethod().split(",");
				for (String string : accType) {
					str = str + "'" + string.trim() + "',";
				}
				str = str.substring(0, str.lastIndexOf(","));
				sb.append(" and Access_Method IN (" + str + ")");
			}
			if (null != dto.getDataType() && dto.getDataType().length() > 2) {
				String str = "";
				String[] accType = dto.getDataType().split(",");
				for (String string : accType) {
					str = str + "'" + string.trim() + "',";
				}
				str = str.substring(0, str.lastIndexOf(","));
				sb.append(" and Data_Type IN (" + str + ")");
			}
			if (null != dto.getMetricType() && dto.getMetricType().length() > 2) {
				String str = "";
				String[] accType = dto.getMetricType().split(",");
				for (String string : accType) {
					str = str + "'" + string.trim() + "',";
				}
				str = str.substring(0, str.lastIndexOf(","));
				sb.append(" and Metric_Type IN (" + str + ")");
			}
			if (null != dto.getYop() && dto.getYop().length() >= 2) {

				sb.append(" and YOP='" + dto.getYop() + "'");
			}
		} else {
			switch (dto.getReportCode()) {
			case "PR_P1":
				sb.append(
						" AND Metric_Type IN ('Searches_Platform','Total_Item_Requests','Unique_Item_Requests','Unique_Title_Requests') ")
						.append("AND Access_Method='Regular' AND Access_Type='Controlled' ");
				break;
			case "DR_D1":
				sb.append(
						" AND Metric_Type IN ('Searches_Automated','Total_Item_Requests','Total_Item_Investigations','Searches_Regular','Searches_Federated') ")
						.append("AND Data_Type='Database' " + accessMethod + accessType);
				break;
			case "DR_D2":
				sb.append(deniedMetricType).append(accessMethod + "AND Data_Type='Database' ");
				break;
			case "TR_B1":
				sb.append(" AND Metric_Type IN ('Total_Item_Requests','Unique_Title_Requests') ")
						.append("AND Data_Type='Book' " + accessMethod + accessType);
				break;
			case "TR_B2":
				sb.append(deniedMetricType).append(accessMethod + "AND Data_Type='Book' ");
				break;
			case "TR_B3":
				sb.append(
						" AND Metric_Type IN ('Total_Item_Investigations','Total_Item_Requests','Unique_Item_Investigations','Unique_Item_Requests','Unique_Title_Requests','Unique_Title_Investigations') ")
						.append("AND Data_Type='Book' " + accessMethod);
				break;
			case "TR_J1":
				sb.append(" AND Metric_Type IN ('Total_Item_Requests','Unique_Item_Requests') ")
						.append("AND Data_Type='Journal' " + accessMethod + accessType);
				break;
			case "TR_J1_GOA":
				sb.append(
						" AND `Metric_Type` IN ('Total_Item_Requests','Unique_Item_Requests') AND `Data_Type`='Journal' AND Access_type='OA_Gold' ");
				groupBy = "GROUP BY `Parent_Title` ,`Publisher`,`Publisher_ID`,`Platform`,`DOI`,`Proprietary_ID`,`Print_ISSN`,`Online_ISSN`,`URI`,`Metric_Type`";
				break;
			case "TR_J2":
				sb.append(deniedMetricType).append("AND Data_Type='Journal' " + accessMethod);
				break;
			case "TR_J3":
				sb.append(
						" AND Metric_Type IN ('Total_Item_Investigations','Total_Item_Requests','Unique_Item_Requests','Unique_Item_Investigations') ")
						.append("AND Data_Type='Journal' AND Access_Method='Regular' ");
				groupBy = "GROUP BY Title,Access_Type,Metric_Type";
				break;
			case "TR_J4":
				sb.append(
						" AND `Metric_Type` IN ('Total_Item_Requests','Unique_Item_Requests') AND `Data_Type`='Journal'  ");
				groupBy = "GROUP BY Title_ID,item_ID,`YOP`,Metric_Type ";
				break;
			case "TR_STD":
				sb.append(
						" AND Metric_Type IN ('Total_Item_Requests','Unique_Item_Requests') AND Data_Type = 'STANDARD' ")
						.append(accessMethod + accessType);
				break;
			case "TR_CONF":
				sb.append(
						" AND Metric_Type IN ('Total_Item_Requests','Unique_Item_Requests') AND Data_Type = 'CONFERENCE' ")
						.append(accessMethod + accessType);
				break;
			case "IR_A1":
				sb.append(" AND Metric_Type='Total_Item_Requests' AND Data_Type IN ('Journal','Article') AND ")
						.append("Access_Method = 'Regular' AND Section_Type = 'Article' ");
				break;
			case "IR_M1":
				sb.append(" AND Metric_Type='Total_Item_Requests' AND Data_Type='Multimedia' " + accessMethod);
				break;
			case "IP_ART_REQ_MONTH":
				/*
				 * if(dto.getPublisher().equalsIgnoreCase("ieee")){ sb.
				 * append(" AND data_type IN ('JOURNAL','CONFERENCE','STANDARD') AND access_method='Regular' AND Item_ID is not null "
				 * ); }else if(dto.getPublisher().equalsIgnoreCase("astm") ||
				 * dto.getPublisher().equalsIgnoreCase("rsc")){
				 * sb.append(" AND Item_ID is not null "+accessMethod); }
				 */
				// change by ksv for Item_id elimination from query 20180608
				sb.append(" AND data_type IN ('JOURNAL','CONFERENCE','STANDARD') AND access_method='Regular' ");
				break;
			case "ART_REQ_TITLE":
				sb.append(" AND Data_type IN ('STANDARD','JOURNAL') AND Metric_Type='Total_Item_Requests' ");
				break;
			case "IP_Article_request_search":
				sb.append(" AND Metric_Type='Searches_Regular' ");
				groupBy = "GROUP BY IP ";
				break;
			case "IP_Article_request_views":
				sb.append(" AND Metric_Type='Item_Investigations' ");
				groupBy = "GROUP BY IP ";
				break;
			case "EBOOK_INVEST_TITLE":
				sb.append(
						" AND Data_Type='Book' AND Access_Method='Regular' AND Title_Type LIKE '%eBook%' AND Metric_Type IN ('Total_Item_Investigations') ");
				break;
			case "TR_NO_LIC_DEN":
				sb.append(
						" AND Access_Method='Regular' AND Metric_Type IN ('No_License') AND Data_Type IN ('Journal', 'Conference', 'Standard') ORDER BY title ");
				break;
			case "EDU_COUR":
				sb.append(
						" AND Access_Type='Controlled' AND Access_Method='Regular' AND Metric_Type IN ('Total_Item_Requests','Unique_Item_Requests') AND Data_Type = 'Courses' ORDER BY title ");
				break;
			case "TURNAWAY_BY_TYPE":
				sb.append(
						" AND Data_Type IN ('JOURNAL','CONFERENCE','STANDARD') AND Access_Type='Controlled' AND Access_Method='Regular' AND Metric_Type IN ('Limit_Exceeded') ");
				groupBy = "GROUP BY Title_Type ";
				break;
			case "MIT_EBOOK_CHAPTER":
				sb.append(
						" AND Data_Type IN ('BOOK') AND Access_Method='Regular' AND Title_Type ='MIT eBook' AND Metric_Type IN ('Total_Item_Requests')");
				break;
			case "WILEY_IEEE_EBOOK_CHAPTER":
				sb.append(
						" AND Data_Type IN ('BOOK') AND Access_Method='Regular' AND Title_Type ='WILEY eBook' AND Metric_Type IN ('Total_Item_Requests') ");
				break;
			case "PARTNER_REQ_BY_TYPE":
				sb.append(
						" AND Data_Type IN ('JOURNAL','CONFERENCE','STANDARD','Book') AND Access_Type='Controlled' AND Access_Method='Regular' ")
						.append("AND Metric_Type IN ('Total_Item_Requests') AND Title_Type IN ('VDE Conferences','SMPTE Conferences','BIAI Journals','IBM Journals',")
						.append("'MITP Journals','TUP Journals','SMPTE Journals','SMPTE Standards','Wiley-IEEE eBook','M&C eBook','MIT eBook','AGU Journals',")
						.append("'BLT Journals','CPSS Journals','URSI Journals','CMP Journals','CES Journals','NOW eBook','OUP Journals') ")
						.append("GROUP BY Title_Type ORDER BY FIELD(Title_Type,'VDE Conferences','SMPTE Conferences','BIAI Journals','IBM Journals',")
						.append("'MITP Journals','TUP Journals','SMPTE Journals','SMPTE Standards','Wiley-IEEE eBook','M&C eBook','MIT eBook','AGU Journals',")
						.append("'BLT Journals','CPSS Journals','URSI Journals','CMP Journals','CES Journals','NOW eBook','OUP Journals')");
				break;
			default:
				sb.append(" ");
			}
		}
		sb.append(" AND Institution_ID != '' ");
		if (dto.getReportCode().contains("TR")) {
			sb.append(" AND Title_ID != '' ");
			sb.append(" AND Title != '' ");
		} else if (dto.getReportCode().contains("IR")) {
			sb.append(" AND Item_ID != '' ");
			sb.append(" AND Item != '' ");
		} else if (dto.getReportCode().contains("DR")) {
			sb.append(" AND Title_ID != '' ");
			sb.append(" AND `Database` != '' ");
		} else if (dto.getReportCode().contains("PR")) {
			sb.append(" AND Platform != '' ");
		}
		return sb.append(groupBy).toString();
	}

	public HashMap<String, String> getCounter5ReportHeader(Counter5DTO c5dto) {

		HashMap<String, String> c5 = new LinkedHashMap<String, String>();

		try {
			Redis redis = new Redis();
			c5dao = new Counter5ReportsDao(rmd);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			String strDate = formatter.format(new Date());
			MyDataTable reportName = c5dao.getReportDetailByCode(c5dto.getPublisher(), c5dto.getReportCode());
			StringBuilder tempMetricType = new StringBuilder();
			String institutionName = "";
			Account account = new Account(rmd);
			AccountDTO adto = account.getAccountByCode(c5dto.getAccountID(), c5dto.getWebmartID());
			institutionName = adto.getAccountName();
			StringBuilder reportfilter = new StringBuilder();
			StringBuilder reportAttribute = new StringBuilder();
			String ReportCode = c5dto.getReportCode();

			int webmartId = c5dto.getWebmartID();
			String attributeToShow = c5dto.getReportAttributes();
			/*
			 * if(c5dto.getReportCode().equalsIgnoreCase("TR") ||
			 * c5dto.getReportCode().equalsIgnoreCase("DR") ||
			 * c5dto.getReportCode().equalsIgnoreCase("PR")){
			 * reportAttribute.append(
			 * "Attributes_To_Show=YOP|Data_Type|Access_Type|Access_Method");
			 * if("TR".equalsIgnoreCase(c5dto.getReportCode())){ reportfilter.
			 * append("Data_Type=Book|Journal; Access_Type=Controlled|OA_Gold|Other_Free_To_Read; Access_Method=Regular|TDM;"
			 * ); } if("DR".equalsIgnoreCase(c5dto.getReportCode())){
			 * reportfilter.
			 * append("Data_Type=Database; Access_Type=Controlled|OA_Gold|Other_Free_To_Read; Access_Method=Regular|TDM;"
			 * ); } if("PR".equalsIgnoreCase(c5dto.getReportCode())){
			 * reportfilter.
			 * append("Data_Type=Platform; Access_Type=Controlled|OA_Gold|Other_Free_To_Read; Access_Method=Regular|TDM;"
			 * ); } } if(c5dto.getReportCode().equalsIgnoreCase("IR")){
			 * reportAttribute.append(
			 * "Attributes_To_Show=YOP|Data_Type|Section_Type|Access_Type|Access_Method"
			 * ); reportfilter.
			 * append("Data_Type=Journal|Article|Multimedia; Access_Type=Controlled|OA_Gold|Other_Free_To_Read; Access_Method=Regular|TDM;"
			 * ); }
			 */
			if ("TR".equalsIgnoreCase(ReportCode) || "DR".equalsIgnoreCase(ReportCode)
					|| "PR".equalsIgnoreCase(ReportCode) || "IR".equalsIgnoreCase(ReportCode)) {
				if ("IR".equalsIgnoreCase(ReportCode)) {
					if (attributeToShow == null || attributeToShow.equalsIgnoreCase("null")
							|| attributeToShow.equalsIgnoreCase("") || attributeToShow.equalsIgnoreCase(" ")) {
						reportAttribute.append(
								"Attributes_To_Show=Authors|Publication_Date|Article_Version|Parent_Title|Parent_Data_Type|Parent_DOI|Parent_Proprietary_ID|Parent_ISBN|Parent_Print_ISSN|Parent_Online_ISSN|Parent_URI|Component_Title|Component_Data_Type|Component_DOI|Component_Proprietary_ID|Component_ISBN|Component_Print_ISSN|Component_Online_ISSN|Component_URI|Data_Type|Section_Type|YOP|Access_Type|Access_Method");
					} else {
						reportAttribute.append(attributeToShow.replaceAll(",", "|"));
					}
				} else if ("TR".equalsIgnoreCase(ReportCode)) {
					if (attributeToShow == null || attributeToShow.equalsIgnoreCase("null")
							|| attributeToShow.equalsIgnoreCase("") || attributeToShow.equalsIgnoreCase(" ")) {
						reportAttribute
								.append("Attributes_To_Show=Data_Type|Section_Type|YOP|Access_Type|Access_Method");
					} else {
						reportAttribute.append(attributeToShow.replaceAll(",", "|"));
					}
				} else if ("PR".equalsIgnoreCase(ReportCode)) {
					if (attributeToShow == null || attributeToShow.equalsIgnoreCase("null")
							|| attributeToShow.equalsIgnoreCase("") || attributeToShow.equalsIgnoreCase(" ")) {
						reportAttribute.append("Attributes_To_Show=Data_Type|Access_Method");
					} else {
						reportAttribute.append(attributeToShow.replaceAll(",", "|"));
					}
				} else if ("DR".equalsIgnoreCase(ReportCode)) {
					if (attributeToShow == null || attributeToShow.equalsIgnoreCase("null")
							|| attributeToShow.equalsIgnoreCase("") || attributeToShow.equalsIgnoreCase(" ")) {
						reportAttribute.append("Attributes_To_Show=Data_Type|Access_Method");
					} else {
						reportAttribute.append(attributeToShow.replaceAll(",", "|"));
					}

				}

				// change by KSV on 2018-12-15 for global Master report filters
				/*
				 * String value =
				 * redis.getValueFromRedisWithKey(webmartId+"_"+ReportCode.
				 * toUpperCase()+"_metricType"); if(value==null ||
				 * value.equalsIgnoreCase("null")||value.equalsIgnoreCase(
				 * "Error")){ JsonObject jobj=new
				 * Counter5ReportImpl(rmd).getCounter5ReportsAttributeDetail(
				 * webmartId,ReportCode);
				 * redis.setValueToRedisWithKey(webmartId+"_"+ReportCode.
				 * toUpperCase()+"_metricType", jobj.toString()); value =
				 * jobj.toString(); }
				 */
				// change by KSV on 2018-12-15 for global Master report filters
				String value = redis.getValueFromRedisWithKey(ReportCode.toUpperCase() + "_Filters");
				if (value == null || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("Error")) {
					JsonObject jobj = new Counter5ReportImpl(rmd).getCounter5ReportsAttributeDetail(webmartId,
							ReportCode);
					redis.setValueToRedisWithKey(ReportCode.toUpperCase() + "_Filters", jobj.toString());
					value = jobj.toString();
				}

				JSONObject obj = new JSONObject(value);

				if (ReportCode.equals("PR")||ReportCode.equals("DR")||ReportCode.equals("TR")) {
					tempMetricType.append("");
				} else {
					JSONArray metricType = (JSONArray) obj.get("metricType");
					for (int i = 0; i < metricType.length(); i++) {
						JSONObject jsonObject = (JSONObject) metricType.get(i);
						String val = jsonObject.get("Key").toString();

						if (i + 1 == metricType.length()) {
							tempMetricType.append(val);
						} else {
							tempMetricType.append(val);
						}
					}
				}

				// JSONArray dataType = (JSONArray)obj.get("dataType");
				/*
				 * reportfilter.append("Data_Type="); for (int i = 0; i <
				 * dataType.length(); i++) { JSONObject jsonObject =
				 * (JSONObject) dataType.get(i); String val =
				 * jsonObject.get("Key").toString();
				 * 
				 * if(i+1 == dataType.length()){ reportfilter.append(val);
				 * }else{ reportfilter.append(val+"|"); } }
				 */
				String datatype = c5dto.getDataType();
				int ch=0;
				if(ReportCode.equals("PR")){
			      ch=13;
				}
			     else if(ReportCode.equals("DR"))
			     {
			    	  ch=8;
			     } else if(ReportCode.equals("TR"))
			     {
			    	  ch=8;
			     }
				
				if (datatype == null||ch==13||ch==8)
					reportfilter.append("");
				else
				{
					String datatypePR = c5dto.getDataType().replace(",", "|");;
					reportfilter.append(datatypePR);
				}

				if ("IR".equalsIgnoreCase(ReportCode)) {
					JSONArray accessType = (JSONArray) obj.get("accessType");
					reportfilter.append("Access_Type=");
					for (int i = 0; i < accessType.length(); i++) {
						JSONObject jsonObject = (JSONObject) accessType.get(i);
						String val = jsonObject.get("Key").toString().trim();

						if (i + 1 == accessType.length()) {
							reportfilter.append(val);
						} else {
							reportfilter.append(val + "|");
						}
					}
					reportfilter.append(";");
				} else {
					reportfilter.append(" ");
				}
				if (ReportCode.equals("PR")||ReportCode.equals("DR")||ReportCode.equals("TR")) {
					
					//reportfilter.append("");
				} else {
					
					JSONArray accessMethod = (JSONArray) obj.get("accessMethod");
					reportfilter.append("");
					for (int i = 0; i < accessMethod.length(); i++) {
						JSONObject jsonObject = (JSONObject) accessMethod.get(i);
						String val = jsonObject.get("Key").toString();

						if (i + 1 == accessMethod.length()) {
							reportfilter.append(val);
						} else {
							reportfilter.append(val + "|");
						}
					}
				}
			}

			if (c5dto.getReportCode().equalsIgnoreCase("DR_D1")) {
				reportfilter.append("Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("DR_D2")) {
				reportfilter.append("Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("OA_USAGE_REPORT")) {
				reportfilter.append("Data_Type=Journal|Article; Section_Type=Article;Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("IR_A1")) {
				reportfilter.append("Parent_Data_Type=Journal; Data_Type=Article; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("IR_M1")) {
				reportfilter.append("Data_Type=Multimedia; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("PR_P1")) {
				reportfilter.append("Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_B1")) {
				reportfilter.append("Data_Type=Book; Access_Type=Controlled; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_B2")) {
				reportfilter.append("Data_Type=Book; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_B3")) {
				reportfilter.append("Data_Type=Book; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_J1")) {
				reportfilter.append("Data_Type=Journal; Access_Type=Controlled; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_J1_GOA")) {
				reportfilter.append("");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_J2")) {
				reportfilter.append("Data_Type=Journal; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_J3")) {
				reportfilter.append("Data_Type=Journal; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_J4")) {
				reportfilter.append("Data_Type=Journal; Access_Type=Controlled; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_CONF")) {
				reportfilter.append("Data_Type=Conference; Access_Type=Controlled|OA_Gold; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_EDU_COURSE")) {
				reportfilter.append("Data_Type=Courses; Access_Type=Controlled; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_STD")) {
				reportfilter.append("Data_Type=Standard; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_CONF_YOP")) {
				reportfilter.append("Data_Type=Conference; Access_Type=Controlled|OA_Gold; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_WILEY_EBOOK")) {
				reportfilter.append("Data_Type=Book; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_MIT_EBOOK")) {
				reportfilter.append("Data_Type=Book; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_LIC_DEN_MONTH")) {
				reportfilter.append("Data_Type=Journal|Conference|Standard; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("SITE_OVERVIEW")) {
				reportfilter.append("");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_ART_REQ_PARTNER")) {
				reportfilter.append("Data_Type=All; Access_Type=Controlled|OA_Gold; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_ABS_REQ_EBOOK")) {
				reportfilter.append("Data_Type=Book; Access_Method=Regular");
				reportAttribute.append("");
			}
			// added by satyam 07/02/2019
			else if (c5dto.getReportCode().equalsIgnoreCase("IP_ART_REQ_MONTH")) {
				reportfilter.append(
						"Data_Type=Journal|Conference|Standard; Access_Type=Controlled|OA_Gold; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_ART_ABS_VIEW")) {
				reportfilter.append(
						"Data_Type=Journal|Conference|Standard; Access_Type=Controlled|OA_Gold; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_ART_REQ_CONSORTIA_MEMBER")) {
				reportfilter.append(
						"Data_Type=Journal|Conference|Standard|Book; Access_Type=Controlled|OA_Gold; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_ART_REQ_TITLE")) {
				reportfilter.append(
						"Data_Type=Journal|Conference|Standard; Access_Type=Controlled|OA_Gold; Access_Method=Regular");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("TR_ART_REQ_TYPE")) {
				reportfilter.append("Data_Type=All; Access_Type=Controlled|OA_Gold; Access_Method=Regular;");
				reportAttribute.append("");
			} else if (c5dto.getReportCode().equalsIgnoreCase("OA_USAGE")) {
				reportfilter.append(
						"Data_Type=Book|Courses|Conference|Journal|Newspaper_or_Newsletter|Other|Report|Standard|Thesis_or_Dissertation;Access_Type=Controlled|OA_Gold|Other_Free_To_Read;Access_Method=Regular|TDM;");
				reportAttribute.append("");
			}

			else {
			}
			/*
			 * if (c5dto.getReportCode().endsWith("_B3")) {
			 * reportAttribute.append("Attributes_To_Show=Access_Type"); } if
			 * (c5dto.getReportCode().equalsIgnoreCase("J1_GOA")) {
			 * reportfilter.append("Data_Type=Journal; "); } else if
			 * (c5dto.getReportCode().startsWith("TR_B")) {
			 * //reportfilter.append("Data_Type=Book; "); } else if
			 * (c5dto.getReportCode().equalsIgnoreCase("IP_ART_REQ_MONTH")) {
			 * reportfilter.append("Data_Type=JOURNAL|CONFERENCE|STANDARD; "); }
			 * else if (c5dto.getReportCode().startsWith("TR_STD")) {
			 * reportfilter.append("Data_Type=STANDARD; "); } else if
			 * (c5dto.getReportCode().equalsIgnoreCase("TR_CONF")) {
			 * reportfilter.append("Data_Type=CONFERENCE; "); } else if
			 * (c5dto.getReportCode().equalsIgnoreCase("IR_A1")) {
			 * //reportfilter.append("Data_Type=Journal|Article; ");
			 * //reportfilter.append("Section_Type=Article; "); } else if
			 * (c5dto.getReportCode().equalsIgnoreCase("IR_M1")) {
			 * //reportfilter.append("Data_Type=Multimedia; "); } else if
			 * (c5dto.getReportCode().equalsIgnoreCase("ART_REQ_TITLE")) {
			 * reportfilter.append("Data_Type=JOURNAL|STANDARD; "); } else if
			 * (c5dto.getReportCode().contains("EBOOK")) {
			 * reportfilter.append("Data_Type=BOOK; "); }
			 * 
			 * if (c5dto.getReportCode().equalsIgnoreCase(tr_j1)||
			 * c5dto.getReportCode().equalsIgnoreCase(tr_j4)) {
			 * reportfilter.append("Access_Type=Controlled; "); } if
			 * (c5dto.getReportCode().equalsIgnoreCase("J1_GOA")) {
			 * reportfilter.append("Access_Type=OA_GOLD; "); }
			 * 
			 * if(c5dto.getReportCode().equalsIgnoreCase("DR_D2") ||
			 * c5dto.getReportCode().equalsIgnoreCase("DR_D1") ){
			 * reportfilter.append(""); }
			 * 
			 * if(c5dto.getReportCode().startsWith("DR_D")){
			 * 
			 * }else if(c5dto.getReportCode().equalsIgnoreCase("IR_A1")){
			 * 
			 * }else if(c5dto.getReportCode().equalsIgnoreCase("IR_M1")){
			 * 
			 * }else if(c5dto.getReportCode().equalsIgnoreCase("PR_P1")){
			 * 
			 * }else if(c5dto.getReportCode().equalsIgnoreCase("TR_B1")
			 * ||c5dto.getReportCode().equalsIgnoreCase("TR_B2") ){
			 * 
			 * }else if(c5dto.getReportCode().equalsIgnoreCase("TR_J4") ){
			 * reportfilter.
			 * append("Data_Type=journal; Access_Type=controlled; Access_Method=regular"
			 * ); reportAttribute.append("Attributes_To_Show=YOP"); }else
			 * if(c5dto.getReportCode().equalsIgnoreCase("TR_J3") ){
			 * reportfilter.append("Data_Type=journal; Access_Method=regular");
			 * reportAttribute.append("Attributes_To_Show=Access_Type"); }else
			 * if(c5dto.getReportCode().equalsIgnoreCase("TR_J1")){
			 * reportfilter.
			 * append("Data_Type=journal; Access_Type=controlled; Access_Method=regular"
			 * ); }else{ reportfilter.append("Access_Method=Regular"); }
			 */
			if (c5dto.getReportCode().endsWith("2")) {
				tempMetricType.append("Limit_Exceeded; No_License");
			} else {
				if (c5dto.getReportCode().equalsIgnoreCase("PR_P1")) {
					tempMetricType.append(
							"Searches_Platform; Total_Item_Requests; Unique_Item_Requests; Unique_Title_Requests");
				} else if (c5dto.getReportCode().equalsIgnoreCase(tr_b1)
						|| c5dto.getReportCode().equalsIgnoreCase("TR_MIT_EBOOK")
						|| c5dto.getReportCode().equalsIgnoreCase("TR_WILEY_EBOOK")) {
					tempMetricType.append("Total_Item_Requests; Unique_Title_Requests");
				} else if (c5dto.getReportCode().equalsIgnoreCase("TR_ABS_REQ_EBOOK")) {
					tempMetricType.append("Total_Item_Investigations; Unique_Title_Investigations");
				} else if (c5dto.getReportCode().equalsIgnoreCase(tr_b3)) {
					tempMetricType
							.append("Total_Item_Investigations; Total_Item_Requests; Unique_Item_Investigations; ")
							.append("Unique_Item_Requests; Unique_Title_Investigations; Unique_Title_Requests");
				} else if (c5dto.getReportCode().equalsIgnoreCase("TR_J1")) {
					tempMetricType.append("Total_Item_Requests; Unique_Item_Requests");
				} else if (c5dto.getReportCode().equalsIgnoreCase(tr_j3)) {
					tempMetricType.append(
							"Total_Item_Investigations; Total_Item_Requests; Unique_Item_Investigations; Unique_Item_Requests");
				} else if (c5dto.getReportCode().equalsIgnoreCase(tr_j4)
						|| c5dto.getReportCode().equalsIgnoreCase("IP_ART_REQ_MONTH")) {
					tempMetricType.append("Total_Item_Requests; Unique_Item_Requests");
				} else if (c5dto.getReportCode().equalsIgnoreCase("TR_LIC_DEN_MONTH")) {
					tempMetricType.append("Limit_Exceeded; No_License");
				} else if (c5dto.getReportCode().equalsIgnoreCase("SITE_OVERVIEW")) {
					tempMetricType.append("");
				} else if (c5dto.getReportCode().equalsIgnoreCase("TR_ART_REQ_PARTNER")) {
					tempMetricType.append("Total_Item_Requests ; Unique_Item_Requests");
				} else if (c5dto.getReportCode().equalsIgnoreCase("TR_ART_ABS_VIEW")) {
					tempMetricType.append("Total_Item_Investigations; Unique_Item_Investigations");
				} else if (c5dto.getReportCode().equalsIgnoreCase("DR_D1")) {
					tempMetricType.append("Searches_Automated; Searches_Federated; ")
							.append("Searches_Regular; Total_Item_Investigations; Total_Item_Requests");
				} else if (c5dto.getReportCode().equalsIgnoreCase("IR_M1")
						|| c5dto.getReportCode().equalsIgnoreCase("ART_REQ_TITLE")
						|| c5dto.getReportCode().equalsIgnoreCase("OA_USAGE")) {
					tempMetricType.append("Total_Item_Requests");
				} else if (c5dto.getReportCode().equalsIgnoreCase("TR_STD")
						|| c5dto.getReportCode().equalsIgnoreCase("TR_CONF")
						|| c5dto.getReportCode().equalsIgnoreCase("TR_CONF_YOP")
						|| c5dto.getReportCode().equalsIgnoreCase("J1_GOA")
						|| c5dto.getReportCode().equalsIgnoreCase("IR_A1")
						|| c5dto.getReportCode().equalsIgnoreCase("TR_ART_REQ_TITLE")
						|| c5dto.getReportCode().equalsIgnoreCase("TR_ART_REQ_TYPE")
						|| c5dto.getReportCode().equalsIgnoreCase("TR_ART_REQ_CONSORTIA_MEMBER")
						|| c5dto.getReportCode().equalsIgnoreCase("TR_EDU_COURSE")
						|| c5dto.getReportCode().equalsIgnoreCase("OA_USAGE_REPORT")
						|| c5dto.getReportCode().equalsIgnoreCase("IP_ART_REQ_MONTH")) {
					tempMetricType.append("Total_Item_Requests; Unique_Item_Requests");
				} else if (c5dto.getReportCode().equalsIgnoreCase("PROCEEDING")) {
					tempMetricType.append("Total_Item_Requests; Unique_Item_Requests");
				} /*
					 * else if (c5dto.getReportCode().equalsIgnoreCase("PR")) {
					 * tempMetricType.
					 * append("Searches_Platform; Total_Item_Investigations; Total_Item_Requests; Unique_Item_Investigations; Unique_Item_Requests; Unique_Title_Investigations; Unique_Title_Requests"
					 * ); } else if
					 * (c5dto.getReportCode().equalsIgnoreCase("DR")) {
					 * tempMetricType.
					 * append("Searches_Automated; Searches_Federated; Searches_Regular; Total_Item_Investigations; Total_Item_Requests; Unique_Item_Investigations; Unique_Title_Investigations; Limit_Exceeded; No_License"
					 * ); } else if
					 * (c5dto.getReportCode().equalsIgnoreCase("TR")) {
					 * tempMetricType.
					 * append("Searches_Automated; Searches_Federated; Searches_Regular; Total_Item_Investigations; Total_Item_Requests; Unique_Item_Investigations; Unique_Title_Investigations; Limit_Exceeded; No_License"
					 * ); } else if
					 * (c5dto.getReportCode().equalsIgnoreCase("IR")) {
					 * tempMetricType.
					 * append("Total_Item_Investigations; Total_Item_Requests; Unique_Item_Investigations; Unique_Title_Investigations"
					 * ); }
					 */
			}

			String Reporting_Period = "";
			String[] fromdatet = c5dto.getFromDate().split("-");
			String[] todatet = c5dto.getToDate().split("-");
			String day;
			switch (todatet[0]) {
			case "02":
				day = "28";
				break;
			case "04":
				day = "30";
				break;
			case "06":
				day = "30";
				break;
			case "09":
				day = "30";
				break;
			case "11":
				day = "30";
				break;
			default:
				day = "31";
			}
			Reporting_Period = "Begin_Date=" + fromdatet[1] + "-" + fromdatet[0] + "-01; End_Date=" + todatet[1] + "-"
					+ todatet[0] + "-" + day;
			c5.put("Report_Name", reportName.getValue(1, 1));
			c5.put("Report_ID", c5dto.getReportCodeAlias().trim());
			c5.put("Release", "5");
			c5.put("Institution_Name", institutionName);
			c5.put("Institution_ID", c5dto.getPublisher() + ":" + c5dto.getAccountID());
			if (null == c5dto.getMetricType() || c5dto.getMetricType().length() < 2) {
				c5.put("Metric_Types", tempMetricType.toString());
				// c5.put("Metric_Types", "");
			} else {
				c5.put("Metric_Types", c5dto.getMetricType());
			}
			if (null == c5dto.getReportFilters() || c5dto.getReportFilters().length() < 2) {
				// c5.put("Metric_Types", tempMetricType.toString());
				c5.put("Report_Filters", reportfilter.toString());
			} else {
				c5.put("Report_Filters", c5dto.getReportFilters());
			}

			c5.put("Report_Attributes", reportAttribute.toString());
			c5.put("Exceptions", "");
			c5.put("Reporting_Period", Reporting_Period);
			c5.put("Created", strDate);
			c5.put("Created_By", "MPS for " + rmd.getWebmartCode().toUpperCase());

		} catch (Exception e) {
			// LOG.error("Exception in getCounter5ReportsList :" +
			// e.getMessage());
		}
		return c5;
	}

	public String getOrderAndGroupQuery(String reportCode) {
		String query = "";
		if (reportCode.equalsIgnoreCase("IP_ART_REQ_MONTH")) {
			query = " GROUP BY IP, Metric_Type ";

		} else if ("ART_REQ_TITLE".equalsIgnoreCase(reportCode)) {
			query = " ORDER BY `YTD Total` DESC ";
		}

		return query;
	}

	public HashMap<String, String> getExcelReportHeader(Counter5DTO dto) {

		HashMap<String, String> c5 = getCounter5ReportHeader(dto);
		HashMap<String, String> detail = new HashMap<String, String>();
		detail.put("rowcount", "13");
		detail.put("R1C1", "Report_Name");
		detail.put("R1C2", c5.get("Report_Name"));
		// 2nd row
		detail.put("R2C1", "Report_ID");
		detail.put("R2C2", c5.get("Report_ID"));
		// 3rd row
		detail.put("R3C1", "Release");
		detail.put("R3C2", c5.get("Release"));
		// 4th row
		detail.put("R4C1", "Institution_Name");
		detail.put("R4C2", c5.get("Institution_Name"));
		// 5th row
		detail.put("R5C1", "Institution_ID");
		detail.put("R5C2", c5.get("Institution_ID"));
		// 6th row
		detail.put("R6C1", "Metric_Types");
		detail.put("R6C2", c5.get("Metric_Types"));
		// 7th row
		detail.put("R7C1", "Report_Filters");
		detail.put("R7C2", c5.get("Report_Filters"));
		// 8th row
		detail.put("R8C1", "Report_Attributes");
		detail.put("R8C2", c5.get("Report_Attributes"));
		// 9th row
		detail.put("R9C1", "Exceptions");
		detail.put("R9C2", c5.get("Exceptions"));
		// 10th row
		detail.put("R10C1", "Reporting_Period");
		detail.put("R10C2", c5.get("Reporting_Period"));
		// 11th row
		detail.put("R11C1", "Created");
		detail.put("R11C2", c5.get("Created"));
		// 12th row
		detail.put("R12C1", "Created_By");
		detail.put("R12C2", c5.get("Created_By"));
		// 13th row
		detail.put("R13C1", "");
		detail.put("R13C2", c5.get(""));

		return detail;
	}
}
