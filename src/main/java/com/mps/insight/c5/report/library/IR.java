package com.mps.insight.c5.report.library;

import java.net.URLDecoder;

import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;


public class IR {
	RequestMetaData rmd;
	DynamicMonthCreater dmc = new DynamicMonthCreater();
	private Counter5DTO dto;
	private String query = "";
	private String monthSumQuery="";
	private String totalMonth="";
	private String metricTypeFilter="";
	private String accessTypeFilter="";
	private String accessMethodFilter="";
	private String dataTypeFilter="";
	private String sectionTypeFilter ="";
	private String yopFilter ="";
	private String showAttributeFilter="";
	private String previewType="";
	public IR(Counter5DTO dto, String previewType,RequestMetaData rmd) {
		try {
			this.dto = dto;
			this.previewType = previewType;
			this.rmd = rmd;
			run();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void run() throws Exception {
		includeMonthParameters();
		getReportFilters();
		generateIRQuery();
		setHeaderParmeter();

	}

	private void setHeaderParmeter() {
		try {
			
			String defaultMtricType = metricTypeFilter.substring(metricTypeFilter.lastIndexOf("(")+1, metricTypeFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(" ,", "; ");
			String defaultDataType = "Data_Type=Article|Book|Database|Journal|Multimedia|Newspaper_or_Newsletter|Other|Report|Standard|Thesis_or_Dissertation";
			String defaultAccessType = "Access_Type=Controlled|OA_Gold|Other_Free_To_Read";
			String defaultAccessMethod = "Access_Method=Regular|TDM";
			
			if(dataTypeFilter.contains("'-',' '")){
				defaultDataType = "Data_Type=Article|Book|Dataset|Journal|Multimedia|Newspaper_or_Newsletter|Other|Report|Standard|Thesis_or_Dissertation";
			}else{
				defaultDataType = "Data_Type="+dataTypeFilter.substring(dataTypeFilter.lastIndexOf("(")+1, dataTypeFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(",", "|").trim();
			}
			
			if(accessTypeFilter.contains("'-',' '")){
				defaultAccessType = "Access_Type=Controlled|OA_Gold|Other_Free_To_Read";				
			}else{
				defaultAccessType = "Access_Type="+accessTypeFilter.substring(accessTypeFilter.lastIndexOf("(")+1, accessTypeFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(",", "|").trim();
			}
				
			if(accessMethodFilter.contains("'-',' '")){
				defaultAccessMethod = "Access_Method=Regular|TDM";
			}else{
				defaultAccessMethod = "Access_Method="+accessMethodFilter.substring(accessMethodFilter.lastIndexOf("(")+1, accessMethodFilter.lastIndexOf(")")).replaceAll("'", "").replaceAll(",", "|");
			}
			
			dto.setDataType(defaultDataType);
			dto.setReportDescription("Item Master Report");
			dto.setMetricType(defaultMtricType);
			dto.setReportFilters(defaultDataType+"; "+defaultAccessType+"; "+defaultAccessMethod);
			dto.setReportAttributes("Attributes_To_Show=Authors|Publication_Date|Article_Version|Parent_Title|Parent_Data_Type|Parent_DOI|Parent_Proprietary_ID|Parent_ISBN|Parent_Print_ISSN|Parent_Online_ISSN|Parent_URI|Component_Title|Component_Data_Type|Component_DOI|Component_Proprietary_ID|Component_ISBN|Component_Print_ISSN|Component_Online_ISSN|Component_URI|Data_Type|Section_Type|YOP|Access_Type|Access_Method");
			dto.setException("");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	private void getReportFilters() throws Exception {
		String metricType = dto.getMetricType();
		String accessType = dto.getAccessType();
		String accessMethod = dto.getAccessMethod();
		String dataType = dto.getDataType();
		String sectionType = dto.getSectionType();
		String yop = dto.getYop();
		String showAttribute = dto.getReportAttributes();
		
		try{metricType =  URLDecoder.decode(metricType, "UTF-8");}catch(Exception e){}
		try{accessType =  URLDecoder.decode(accessType, "UTF-8");}catch(Exception e){}
		try{accessMethod =  URLDecoder.decode(accessMethod, "UTF-8");}catch(Exception e){}
		try{dataType =  URLDecoder.decode(dataType, "UTF-8");}catch(Exception e){}
		try{yop =  URLDecoder.decode(yop, "UTF-8");}catch(Exception e){}
		
		
		//Map metric type 
		try {
			StringBuilder metricTypeIn=new StringBuilder();
			if(metricType == null||metricType.equalsIgnoreCase("null") 
					|| metricType.equalsIgnoreCase("") || metricType.equalsIgnoreCase(" ")){
				rmd.log("No METRIC TYPE Selected..");
				metricTypeFilter="metric_type IN("+InsightConstant.IR_DEFAULT_METRIC_TYPE+")";
			}else if(metricType.contains(",")){
				String[] metricTypeInclude = metricType.split(",");
				for (int i = 0; i < metricTypeInclude.length; i++) {
					metricTypeIn.append("'"+metricTypeInclude[i]+"' ,");
				}
				metricTypeFilter = "metric_type IN("+metricTypeIn.substring(0, metricTypeIn.lastIndexOf(","))+")";
			}else{
				metricTypeFilter = "metric_type IN('"+metricType+"')";
			}
		} catch (Exception e) {
			//MyLogger.error("Unable to Map Metric type for Report : "+e.toString());
			rmd.exception("Unable to Map Metric type for Report : "+e.toString());
		}
		
		
		//Map access type 
		try {
			StringBuilder accessTypeIn=new StringBuilder();
			if(accessType == null||accessType.equalsIgnoreCase("null") 
					|| accessType.equalsIgnoreCase("") || accessType.equalsIgnoreCase(" ")){
				rmd.log("No ACCESS TYPE Selected..");
				accessTypeFilter="access_type Not IN('-',' ')";
			} else if(accessType.contains(",")){
				String[] accessTypeInclude = accessType.split(",");
				for (int i = 0; i < accessTypeInclude.length; i++) {
					accessTypeIn.append("'"+accessTypeInclude[i]+"' ,");
				}
				accessTypeFilter = "access_type IN("+accessTypeIn.substring(0, accessTypeIn.lastIndexOf(","))+")";
			}else{
				accessTypeFilter = "access_type IN('"+accessType+"')";
			}
		} catch (Exception e) {
			rmd.exception("Unable to Map Access type for Report : "+e.toString());
		}
		
		//Map access Method 
		try {
			StringBuilder accessMethodIn=new StringBuilder();
			if(accessMethod == null||accessMethod.equalsIgnoreCase("null") 
					|| accessMethod.equalsIgnoreCase("") || accessMethod.equalsIgnoreCase(" ")){
				rmd.log("No ACCESS METHOD Selected..");
				accessMethodFilter="Access_Method Not IN('-',' ')";
			} else if(accessMethod.contains(",")){
				String[] accessMethodInclude = accessMethod.split(",");
				for (int i = 0; i < accessMethodInclude.length; i++) {
					accessMethodIn.append("'"+accessMethodInclude[i]+"' ,");
				}
				accessMethodFilter = "Access_Method IN("+accessMethodIn.substring(0, accessMethodIn.lastIndexOf(","))+")";
			}else{
				accessMethodFilter = "Access_Method IN('"+accessMethod+"')";
			}
			
		} catch (Exception e) {
			rmd.exception("Unable to Map Access Method for Report : "+e.toString());
		}
		
		//Map data Type 
		try {
			StringBuilder dataTypeIn=new StringBuilder();
			if(dataType == null||dataType.equalsIgnoreCase("null") 
					|| dataType.equalsIgnoreCase("") || dataType.equalsIgnoreCase(" ")){
				rmd.log("No DATA TYPE Selected..");
				dataTypeFilter="data_type Not IN('-',' ')";
			} else if(dataType.contains(",")){
				String[] dataTypeInclude = dataType.split(",");
				for (int i = 0; i < dataTypeInclude.length; i++) {
					dataTypeIn.append("'"+dataTypeInclude[i]+"' ,");
				}
				dataTypeFilter = "data_type IN("+dataTypeIn.substring(0, dataTypeIn.lastIndexOf(","))+")";
			}else{
				dataTypeFilter = "data_type IN('"+dataType+"')";
			}
			
		} catch (Exception e) {
			rmd.exception("Unable to Map Data Type for Report : "+e.toString());
		}
		
		//Map Section Type 
		try {
			StringBuilder sectionTypeIn=new StringBuilder();
			if(sectionType == null||sectionType.equalsIgnoreCase("null") 
					|| sectionType.equalsIgnoreCase("") || sectionType.equalsIgnoreCase(" ")){
				rmd.log("No SECTION TYPE Selected..");
				sectionTypeFilter="section_type Not IN('-',' ')";
			} else if(sectionType.contains(",")){
				String[] sectionTypeInclude = sectionType.split(",");
				for (int i = 0; i < sectionTypeInclude.length; i++) {
					sectionTypeIn.append("'"+sectionTypeInclude[i]+"' ,");
				}
				sectionTypeFilter = "section_type IN("+sectionTypeIn.substring(0, sectionTypeIn.lastIndexOf(","))+")";
			}else{
				sectionTypeFilter = "section_type IN('"+sectionType+"')";
			}
			
		} catch (Exception e) {
			rmd.exception("Unable to Map Section Type for Report : "+e.toString());
		}
			
		
		//Map Section Type 
		try {
			Integer yopYear =0000;
			yopYear = Integer.parseInt(yop);
			yopFilter="yop IN('"+yopYear+"')";
			
		} catch (Exception e) {
			rmd.exception("Unable to Get YOP for Report : "+e.toString());
			yopFilter="yop Not IN('-',' ')";
		}
		
		//Map TR Title for Show Attributes 
		try {
			String irTitle_1 = InsightConstant.IR_MASTER_TITLE_1;
			String irTitle_2 = InsightConstant.IR_MASTER_TITLE_2;
			
			StringBuilder showAttributeInOpt_1=new StringBuilder();
			StringBuilder showAttributeInOpt_2=new StringBuilder();
			
			if(showAttribute == null||showAttribute.equalsIgnoreCase("null")){
				rmd.log("No SHOW ATTRIBUTES Selected..");
				showAttributeFilter = irTitle_1 + ", "+InsightConstant.IR_OPTIONAL_TITLE_1+","+irTitle_2+","+InsightConstant.IR_OPTIONAL_TITLE_2+", ";
			} else if(showAttribute.contains(",")){
				
				
				String[] showAttributeInInclude = showAttribute.split(",");
				for (int i = 0; i < showAttributeInInclude.length; i++) {
					String showAttributeI = showAttributeInInclude[i];
					
					if(InsightConstant.IR_OPTIONAL_TITLE_1.toLowerCase().contains(showAttributeI.toLowerCase())){
						showAttributeInOpt_1.append("`"+showAttributeI+"`, ");
					}else if(InsightConstant.IR_OPTIONAL_TITLE_2.toLowerCase().contains(showAttributeI.toLowerCase())){
						showAttributeInOpt_2.append("`"+showAttributeI+"`, ");
					}
				}
				
				if(showAttributeInOpt_1.toString().length()>2 && showAttributeInOpt_2.toString().length()>2 ){
					showAttributeFilter = irTitle_1 + ", "+showAttributeInOpt_1.toString()+irTitle_2+", "+showAttributeInOpt_2.toString();
				}else if(showAttributeInOpt_1.toString().length()>2 && showAttributeInOpt_2.toString().length()<2 ){
					showAttributeFilter = irTitle_1 + ", "+showAttributeInOpt_1.toString()+irTitle_2+", ";
				}else if(showAttributeInOpt_1.toString().length()<2 && showAttributeInOpt_2.toString().length()>2 ){
					showAttributeFilter = irTitle_1 + ", "+irTitle_2+", "+showAttributeInOpt_2.toString();
				}else{
					showAttributeFilter = irTitle_1 + ", "+irTitle_2+", ";
				}
								
			}else{
				showAttributeFilter = irTitle_1 + ", "+irTitle_2+", ";
			}
			
			showAttributeFilter = showAttributeFilter + "`Metric_Type`, ";	
			
		} catch (Exception e) {
			throw new  Exception("Unable to create TR master Report Title : "+e.toString());
			
		}
		
	}
	
	private String getTableName(){
		String tableName="";
		try {
			//String instID=dto.getInstitutionID().substring(0,1);
			//tableName=dto.getPublisher().equalsIgnoreCase("ieee")? "z_ir_start_"+instID:TableMapper.TABALE.get("master_report_table");
			tableName=TableMapper.TABALE.get("master_report_table");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return tableName;
	}
	
	
	private String generateIRQuery() {
		StringBuilder query = new StringBuilder();
		try {
			query.append("SELECT * FROM (SELECT ");
			
			//query.append(" " + InsightConstant.IR_MASTER_TITLE + ", "); `Parent_Title`, AS title,`Publisher`,.....`Metric_Type`
			query .append(" "+showAttributeFilter+" ");
			
			query.append(" SUM" + totalMonth + " AS Reporting_Period_Total , ");
			query.append(" " + monthSumQuery + " ");
			query.append(" from " + getTableName() + " where");
			
			query.append(" Institution_ID='" + dto.getInstitutionID() + "' ");
			query.append(" and " + InsightConstant.IR_MASTER_WHERE_CONDITION); 
			query.append(" AND "+metricTypeFilter+" AND "+accessTypeFilter
					+" AND "+accessMethodFilter+" AND "+dataTypeFilter
					+" AND "+sectionTypeFilter+" AND "+yopFilter);
			query.append(" and " + totalMonth + " > 0 ");
			query.append("GROUP BY "+showAttributeFilter.substring(0, showAttributeFilter.lastIndexOf(",")));
			query.append(" )a ORDER BY a.`Item` "  ); 
			if(previewType.equalsIgnoreCase("preview")){
				query.append(" limit 500 ");
			}else{
				
			}
			
		} catch (Exception e) {
			rmd.exception("generateIRQuery : unable to create Query : "+e.toString());
		}
		return this.query = query.toString();
	}

	
	private void includeMonthParameters() {
		try {
			String[] fromarr = dto.getFromDate().split("-");
			String[] toarr = dto.getToDate().split("-");

			// (M_201801) AS `Jan-2018`,(M_201802) AS `Feb-2018`...........
			monthSumQuery = dmc.createMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]));

			monthSumQuery = monthSumQuery.substring(0, monthSumQuery.lastIndexOf(","));
			
			// ((M_201801)+(M_201802)+(M_201803)+(M_201804)+(M_201805)+(M_201806)+(M_201807)+(M_201808)+(M_201809))
			totalMonth = dmc.createTotalMonthQueryC5(Integer.parseInt(toarr[0]), Integer.parseInt(toarr[1]),
					Integer.parseInt(fromarr[0]), Integer.parseInt(fromarr[1]), "");

		} catch (Exception e) {
			rmd.exception("includeMonthParameters : unable to generate Month list for query : "+e.toString());
		}
	}

	public String getQuery() {
		rmd.log(query.toString());
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Long getTotalRowCounts() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
