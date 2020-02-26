package com.mps.insight.c5.report.library;

import java.net.URLDecoder;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;

public class TR {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private String metricTypeFilter = "";
	private String accessTypeFilter = "";
	private String accessMethodFilter = "";
	private String dataTypeFilter = "";
	private String yopFilter = "";
	private String showAttributeFilter = "";
	private String Query = "";
	private String monthSumQuery = "";
	private String monthSumMonthNameQuery="";
	private String totalMonth = "";
	private Counter5DTO dto;
	private String previewType = "";
	private String groupBy = "";

	public TR(Counter5DTO dto, String previewType, RequestMetaData rmd) {
		try {
			this.dto = dto;
			this.previewType = previewType;
			this.rmd = rmd;
			run();
		} catch (Exception e) {
			rmd.exception(e.toString());
		}
	}

	public void run() throws Exception {
		includeMonthParameters();
		getReportFilters();
		generateTRQuery();
		setHeaderParmeter();
	}

	private void setHeaderParmeter() {
		try {
			String chAccessMethodAll = accessMethodFilter.substring(accessMethodFilter.lastIndexOf("(")+1, accessMethodFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(",", "|").trim();
			
			String defaultMtricType = metricTypeFilter
					.substring(metricTypeFilter.lastIndexOf("(") + 1, metricTypeFilter.lastIndexOf(")"))
					.replaceAll("'", "").replaceAll(" ,", "; ");
			//String defaultDataType = "Data_Type=Book|Journal|Newspaper_or_Newsletter|Other|Report|Thesis_or_Dissertation";
			String defaultDataType ="Data_Type=" + dataTypeFilter
			.substring(dataTypeFilter.lastIndexOf("(") + 1, dataTypeFilter.lastIndexOf(")"))
			.replaceAll("'", "").replaceAll(",", "|").replaceAll(" ", "").trim();
			
			String defaultAccessType = "Access_Type=" + accessTypeFilter
					.substring(accessTypeFilter.lastIndexOf("(") + 1, accessTypeFilter.lastIndexOf(")"))
					.replaceAll("'", "").replaceAll(",", "|").replaceAll(" ", "").trim();;
			String defaultAccessMethod = "Access_Method=Regular|TDM";
			String[] chmatric=defaultMtricType.split(";");
			String[] chDataType=defaultDataType.split("\\|");
			String[] chAccessMethod=chAccessMethodAll.split("\\|");
	        String[] chAccessType=defaultAccessType.split("\\|");
			if(dto.getMetricType()==null||chmatric.length==8)
			{
				defaultMtricType="";
			}
			else
			{
				 defaultMtricType = metricTypeFilter.substring(metricTypeFilter.lastIndexOf("(")+1, metricTypeFilter.lastIndexOf(")")).replaceAll("'","").replaceAll(" ,","; ").trim();
				
			}
			if (dataTypeFilter.contains("'-',' '")||chDataType.length==6) {
				defaultDataType="";
				//defaultDataType = "Data_Type=Book|Courses|Journal|Newspaper_or_Newsletter|Other|Report|Standard|Thesis_or_Dissertation";
			} else {
				defaultDataType = "Data_Type=" + dataTypeFilter
						.substring(dataTypeFilter.lastIndexOf("(") + 1, dataTypeFilter.lastIndexOf(")"))
						.replaceAll("'", "").replaceAll(",", "|").replaceAll(" ", "").trim();
			}
			if (accessTypeFilter.contains("'-',' '")||chAccessType.length==3) {
				defaultAccessType="";
				//defaultAccessType = "Access_Type=Controlled|OA_Gold";
			} else {
				defaultAccessType = "Access_Type=" + accessTypeFilter
						.substring(accessTypeFilter.lastIndexOf("(") + 1, accessTypeFilter.lastIndexOf(")"))
						.replaceAll("'", "").replaceAll(",", "|").replaceAll(" ", "").trim();
			}
			if (accessMethodFilter.contains("'-',' '")||chAccessMethod.length==2) {
				defaultAccessMethod="";
				//defaultAccessMethod = "Access_Method=Regular|TDM";
			} else {
				defaultAccessMethod = "Access_Method=" + accessMethodFilter
						.substring(accessMethodFilter.lastIndexOf("(") + 1, accessMethodFilter.lastIndexOf(")"))
						.replaceAll("'", "").replaceAll(",", "|").replaceAll(" ","").trim();
			}
			
			
			dto.setDataType(defaultDataType);
			dto.setReportDescription("Title Master Report");
			dto.setMetricType(defaultMtricType);//defaultMtricType);
			if(defaultDataType==""||defaultAccessMethod=="")
			{
			dto.setReportFilters(defaultDataType + "" + defaultAccessType + "" + defaultAccessMethod);
			}
			else
			{
				dto.setReportFilters(defaultDataType + "; " + defaultAccessType + "; " + defaultAccessMethod);	
			}
			dto.setReportAttributes("Attributes_To_Show=Data_Type|Section_Type|YOP|Access_Type|Access_Method");
			dto.setException("");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	private void getReportFilters() throws Exception {
		StringBuilder error = new StringBuilder();
		String metricType = dto.getMetricType();
		String accessType = dto.getAccessType();
		String accessMethod = dto.getAccessMethod();
		String dataType = dto.getDataType();
		String yop = dto.getYop();
		String showAttribute = dto.getReportAttributes();

		try {
			metricType = URLDecoder.decode(metricType, "UTF-8");
		} catch (Exception e) {
		}
		try {
			accessType = URLDecoder.decode(accessType, "UTF-8");
		} catch (Exception e) {
		}
		try {
			accessMethod = URLDecoder.decode(accessMethod, "UTF-8");
		} catch (Exception e) {
		}
		try {
			dataType = URLDecoder.decode(dataType, "UTF-8");
		} catch (Exception e) {
		}
		try {
			yop = URLDecoder.decode(yop, "UTF-8");
		} catch (Exception e) {
		}
		groupBy = InsightConstant.TR_GROUPBY;

		error.append("Selected Report Filter ");
		// Map metric type
		try {
			StringBuilder metricTypeIn = new StringBuilder();
			if (metricType == null || metricType.equalsIgnoreCase("null") || metricType.equalsIgnoreCase("")
					|| metricType.equalsIgnoreCase(" ")) {
				error.append(" : No METRIC TYPE Selected..");
				metricTypeFilter = "metric_type IN(" + InsightConstant.TR_DEFAULT_METRIC_TYPE + ")";
			} else if (metricType.contains(",") || metricType.contains("%2C")) {

				String[] metricTypeInclude = metricType.split(",");
				for (int i = 0; i < metricTypeInclude.length; i++) {
					metricTypeIn.append("'" + metricTypeInclude[i] + "' ,");
				}
				metricTypeFilter = "metric_type IN(" + metricTypeIn.substring(0, metricTypeIn.lastIndexOf(",")) + ")";
			} else {
				metricTypeFilter = "metric_type IN('" + metricType + "')";
			}
		} catch (Exception e) {
			error.append(" : Unable to Map Metric type for Report : " + e.toString());
		}

		// Map access type
		try {
			StringBuilder accessTypeIn = new StringBuilder();
			if (accessType == null || accessType.equalsIgnoreCase("null") || accessType.equalsIgnoreCase("")
					|| accessType.equalsIgnoreCase(" ")) {
				error.append(" : No ACCESS TYPE Selected..");
				accessTypeFilter = "access_type Not IN('-',' ')";
			} else if (accessType.contains(",")) {
				String[] accessTypeInclude = accessType.split(",");
				for (int i = 0; i < accessTypeInclude.length; i++) {
					accessTypeIn.append("'" + accessTypeInclude[i] + "' ,");
				}
				accessTypeFilter = "access_type IN(" + accessTypeIn.substring(0, accessTypeIn.lastIndexOf(",")) + ")";
			} else {
				accessTypeFilter = "access_type IN('" + accessType + "')";
			}
		} catch (Exception e) {
			error.append(" : Unable to Map Access type for Report : " + e.toString());
		}

		// Map access Method
		try {
			StringBuilder accessMethodIn = new StringBuilder();
			if (accessMethod == null || accessMethod.equalsIgnoreCase("null") || accessMethod.equalsIgnoreCase("")
					|| accessMethod.equalsIgnoreCase(" ")) {
				error.append(" : No ACCESS METHOD Selected..");
				accessMethodFilter = "Access_Method Not IN('-',' ')";
			} else if (accessMethod.contains(",")) {
				String[] accessMethodInclude = accessMethod.split(",");
				for (int i = 0; i < accessMethodInclude.length; i++) {
					accessMethodIn.append("'" + accessMethodInclude[i] + "' ,");
				}
				accessMethodFilter = "Access_Method IN(" + accessMethodIn.substring(0, accessMethodIn.lastIndexOf(","))
						+ ")";
			} else {
				accessMethodFilter = "Access_Method IN('" + accessMethod + "')";
			}

		} catch (Exception e) {
			error.append(" : Unable to Map Access Method for Report : " + e.toString());
		}

		// Map data Type
		try {
			StringBuilder dataTypeIn = new StringBuilder();
			if (dataType == null || dataType.equalsIgnoreCase("null") || dataType.equalsIgnoreCase("")
					|| dataType.equalsIgnoreCase(" ")) {
				error.append(" : No DATA TYPE Selected..");
				dataTypeFilter = "data_type Not IN('-',' ','Proceedings')";
			} else if (dataType.contains(",")) {
				String[] dataTypeInclude = dataType.split(",");
				for (int i = 0; i < dataTypeInclude.length; i++) {
					dataTypeIn.append("'" + dataTypeInclude[i] + "' ,");
				}
				dataTypeFilter = "data_type IN(" + dataTypeIn.substring(0, dataTypeIn.lastIndexOf(",")) + ")";
			} else {
				dataTypeFilter = "data_type IN('" + dataType + "')";
			}

		} catch (Exception e) {
			error.append(" : Unable to Map Data Type for Report : " + e.toString());
		}

		// Map Section Type
		try {
			Integer yopYear = 0000;
			yopYear = Integer.parseInt(yop);
			yopFilter = "yop IN('" + yopYear + "')";

		} catch (Exception e) {
			error.append(" : Unable to Get YOP for Report : " + e.toString());
			yopFilter = "yop Not IN('0000')";
		}
		// Map TR Title for Show Attributes
		try {
			//String trTitle = InsightConstant.TR_MASTER_TITLE;
			String trTitle = getDefaultTitle();
			StringBuilder showAttributeIn = new StringBuilder();
			if (showAttribute == null || showAttribute.equalsIgnoreCase("null")) {
				error.append(" : No SHOW ATTRIBUTES Selected..");
				showAttributeFilter = trTitle + ", " + optionalTitles();//InsightConstant.TR_OPTIONAL_TITLE + ", ";
				groupBy = groupBy + "," + InsightConstant.TR_OPTIONAL_TITLE;
			} else if (showAttribute.contains(",")) {
				String[] showAttributeInInclude = showAttribute.split(",");
				for (int i = 0; i < showAttributeInInclude.length; i++) {
					String showAttributeI = showAttributeInInclude[i];

					showAttributeIn.append("`" + showAttributeI + "`, ");
				}
				showAttributeFilter = trTitle + ", " + showAttributeIn.toString();
			} else {
				showAttributeFilter = trTitle + ", ";
			}

			showAttributeFilter = showAttributeFilter + "`Metric_Type`, ";

		} catch (Exception e) {
			throw new Exception("Unable to create TR master Report Title : " + e.toString());

		}
		rmd.log(" : " + error);
	}

	private String generateTRQuery() {
		StringBuilder query = new StringBuilder();
		try {
			
			query.append("SELECT ");
			query.append(" " + showAttributeFilter + " ");
			query.append(" SUM" + totalMonth + " AS `Reporting_Period_Total`,");
			query.append(" " + monthSumQuery + " ");
			query.append(" from " + TableMapper.TABALE.get("master_report_table") + " where");
			query.append(" Institution_ID='" + dto.getInstitutionID() + "' ");
			if (dto.getPublisher().equalsIgnoreCase("ieeecs")) {
				query.append(" AND title_id IN(SELECT journal_id FROM `c5_subsription_feed` WHERE institution_id='"+ dto.getInstitutionID()+"') ");
			}
			query.append(" AND Institution_ID != ''  AND Title_ID != ''  AND Parent_Title != ''");
			query.append(" AND " + metricTypeFilter + " AND " + accessTypeFilter + " AND " + accessMethodFilter
					+ " AND " + dataTypeFilter + " AND " + yopFilter);
			query.append(" and " + totalMonth + " > 0 ");
			query.append(" GROUP BY " + groupBy);
			query.append(" ORDER BY `parent_title`, yop,section_type, metric_type");

			
			if (dto.getPublisher().equalsIgnoreCase("iopscience")) {
				StringBuilder queryIOP = new StringBuilder();
				queryIOP.append(" select  Title, Publisher, Publisher_ID, Platform,`DOI`"
						+ ",`Proprietary_ID`,`ISBN`,`Print_ISSN`, `Online_ISSN`, `URI`,"
						+ " `Data_Type`,Section_type, `YOP`,`Access_Type`,"
						+ "`Access_Method`,`Metric_Type`,  "
						+ " SUM(`Reporting_Period_Total`) "
						+ "as Reporting_Period_Total, "+monthSumMonthNameQuery+" from"
								+ " ("+query.toString()+ ") master_data GROUP BY title ,"
										+ "`publisher`,`Publisher_ID`,`Platform`,`DOI`"
										+ ",`proprietary_id`,`ISBN`,`Print_ISSN`,"
										+ "`Online_ISSN`, `URI`, `Metric_type`,"
										+ "`Data_Type`,`Section_Type`,`YOP`,`Access_Type`,"
										+ "`Access_Method` ");
				query = queryIOP;
			}
			
			if (previewType.equalsIgnoreCase("preview")) {
				query.append(" limit 500 ");
			} else {

			}
		} catch (Exception e) {
			rmd.exception("generateTRQuery : unable to create Query : " + e.toString());
		}
		return this.Query = query.toString();
	}

	private void includeMonthParameters() {
		try {
			String[] fromarr = dto.getFromDate().split("-");
			String[] toarr = dto.getToDate().split("-");

			// SUM(M_201801) AS `Jan-2018`,SUM(M_201802) AS
			// `Feb-2018`...........
			monthSumQuery = dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
			monthSumMonthNameQuery = dmc.createMonthQueryC5MonthName(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));
			
			monthSumQuery = monthSumQuery.substring(0, monthSumQuery.lastIndexOf(","));
			monthSumMonthNameQuery = monthSumMonthNameQuery.substring(0, monthSumMonthNameQuery.lastIndexOf(","));
			// ((M_201801)+(M_201802)+(M_201803)+(M_201804)+(M_201805)+(M_201806)+(M_201807)+(M_201808)+(M_201809))
			totalMonth = dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");

		} catch (Exception e) {
			rmd.exception("includeMonthParameters : unable to generate Month list for query : " + e.toString());
		}
	}

	public String getQuery() {
		rmd.log(Query.toString());
		return Query;
	}

	private String getDefaultTitle(){
		return "`Parent_Title` AS 'Title',`Parent_publisher` AS Publisher, Publisher_ID, CASE WHEN platform='' THEN 'NA' ELSE platform END AS `Platform`,`Parent_DOI` AS 'DOI',CONCAT('"+dto.getPublisher()+"',':',`Title_ID`) AS `Proprietary_ID`,`parent_ISBN` AS 'ISBN',`Parent_Print_ISSN` AS 'Print_ISSN', `Parent_Online_ISSN` AS 'Online_ISSN', `Parent_URI` AS `URI`";
	}
	private String optionalTitles(){
		return "`Data_Type`,CASE WHEN metric_type='Unique_Title_Requests' THEN '' "
				+ "WHEN metric_type='Unique_Title_Investigations' THEN '' "
				+ "ELSE section_type END AS `Section_Type`, `YOP`,`Access_Type`,`Access_Method`,";
	
	}

}
