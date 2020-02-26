package com.mps.insight.global;

import java.util.HashMap;
import java.util.Map;

public class InsightConstant {
	public static final String[] MONTH_ARRAY_DASH = { "", "`Jan`", "`Feb`", "`Mar`", "`Apr`", "`May`", "`Jun`", "`Jul`", "`Aug`", "`Sep`",
			"`Oct`", "`Nov`", "`Dec`" };
	
	public static final String[] MONTH_ARRAY = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
			"Oct", "Nov", "Dec" };
	
	public static final String[] MONTH_ARRAY_2019 = { "", "Jan-2019", "Feb-2019", "Mar-2019", "Apr-2019", "May-2019", "Jun-2019", "Jul-2019", "Aug-2019", "Sep-2019",
			"Oct-2019", "Nov-2019", "Dec-2019" };
	
	public static final String[] MONTH_ARRAY_DASHBOARD = { "", "`Dec`", "`Nov`", "`Oct`", "`Sep`", "`Aug`", "`Jul`", "`Jun`", "`May`", "`Apr`",
			"`Mar`", "`Feb`", "`Jan`" };
	
	public static final String USAGE_REPORTS = "USAGE_REPORTS";
	public static final String GROUP_TYPE = "Account";
	public static final String PATH_SEPERATOR = "/";
	public static Map<String, String> productMaster = new HashMap<String, String>() {
		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		{
			put("FULL-TEXT", "FULL-TEXT~ALL");
			put("DATABASE", "DATABASE~ALL");
			put("PLATFORM", "PLATFORM~ALL");
			put("ALL", "ALL~ALL");
			put("FT BOOK", "FULL-TEXT~BOOK");
			put("FT JOURNAL", "FULL-TEXT~JOURNAL");
			put("FT STANDARD", "FULL-TEXT~STANDARD");
			put("FT CONFERENCE", "FULL-TEXT~CONFERENCE");
			put("FT MULTIMEDIA", "FULL-TEXT~MULTIMEDIA");
			put("FT PROCEEDING", "FULL-TEXT~PROCEEDING");
			put("DB SEARCH", "DATABASE~SEARCH");
			put("DB RECORD VIEW", "DATABASE~RECORD");
			put("DB RESULT CLICK", "DATABASE~RESULT");
			put("PF SEARCH", "PLATFORM~SEARCH");
			put("PF RECORD VIEW", "PLATFORM~RECORD");
			put("PF RESULT CLICK", "PLATFORM~RESULT");
		}
	};

	public static final String START_DATE = "start_date";
	public static final String END_DATE = "end_date";
	public static final String PROCESSING_MONTH = "processing_month";
	public static final String CAT_LOG_PROCESSING = "log_processing";
	public static final String ACTION_PROCESS = "process";
	public static final String CAT_REPORT_GENERATION = "report_generation";
	public static final String ACTION_COUNTER_REPORTS = "counter_reports";
	public static final String PUBLISHER_CONTACT = "PUBLISHER_CONTACT";

	/* Support Email ids */
	public static final String IOPSCIENCE_SUPPORT_EMAILID = "kapil.singh@mpslimited.com";
	public static final String IEEE_SUPPORT_EMAILID = "kapil.singh@mpslimited.com";
	public static final String RSC_SUPPORT_EMAILID = "kapil.singh@mpslimited.com";
	public static final String RCNI_SUPPORT_EMAILID = "kapil.singh@mpslimited.com";

	public static final String PUB_USER_ARRAY = "'ADMIN','REPORT USER','CUSTOMER CARE'";
	public static final String LIB_USER_ARRAY = "'ADMIN','REPORT USER'";
	public static final String JR1_QUERY = "`Publisher`,`Platform`,`JournalDOI` as `Journal DOI`,`ProprietaryIdentifier` as `Proprietary Identifier`,`PrintISSN` as `Print ISSN`,`OnlineISSN`,";
	public static final String JR1GOA_QUERY = "`Publisher`,`Platform`,`JournalDOI` as `Journal DOI`,`ProprietaryIdentifier` as `Proprietary Identifier`,`PrintISSN` as `Print ISSN`,`OnlineISSN` as `Online ISSN`,";
	public static final String JR2_QUERY = "`Publisher`,`Platform`,`JournalDOI` as `Journal DOI`,`ProprietaryIdentifier` as `Proprietary Identifier`,`PrintISSN` as `Print ISSN`,`OnlineISSN` as `Online ISSN`,`AccessDeniedCategory` as `Access denied category`,";
	public static final String JR3_QUERY="`journal`,`publisher`,`platform`,`journalDOI`,`proprietaryIdentifier`,`printISSN`,`onlineISSN`,`pageType`";
	public static final String JR4_QUERY = "`CollectionReport`,`SearchesRunReport`,";
	public static final String JR5_QUERY = "`Journal`,`Publisher`,`Platform`,`JournalDOI` as `Journal DOI`,`ProprietaryIdentifier` as `Proprietary Identifier`,`PrintISSN` as `Print ISSN`,`OnlineISSN` as `Online ISSN`,`ArticlesInPress` as `ArticlesInPress`,";
	public static final String ARBT_QUERY = "";
	// public static final String SITE_OVERVIEW_QUERY="title";
	// public static final String
	// CONFERENCES_QUERY="`journal`,`publisher`,`platform`,`journalDOI`,`proprietaryIdentifier`,`printISSN`,`onlineISSN`,`pageType`";
	// public static final String
	// STANDARDS_QUERY="`journal`,`publisher`,`platform`,`journalDOI`,`proprietaryIdentifier`,`printISSN`,`onlineISSN`,`pageType`";
	public static final String BR2_QUERY = "`Publisher`,`Platform`,`BookDoi` as `Book DOI`,`ProprietaryIdentifier` as `Proprietary Identifier`,`ISBN`,`ISSN`,";
	public static final String BR2_GROUP_BY = "`Publisher`,`Platform`,`BookDoi`,`ProprietaryIdentifier`,`ISBN`,`ISSN`";
	public static final String BR3_QUERY = "`Publisher`,`Platform`,`BookDoi` as `Book DOI`,`ProprietaryIdentifier` as `Proprietary Identifier`,`ISBN`,`ISSN`,`AccessDeniedCategory` as `Access denied category`,";
	public static final String TR1_QUERY = "`Publisher`,`Platform`,`TitleDOI` as `Title DOI`,`ProprietaryIdentifier` as `Proprietary Identifier`,`PrintId`,`OnlineId`,`Datatype` as `Data Type`,";
	public static final String TR2_QUERY = "`Publisher`,`Platform`,`TitleDOI` as `Title DOI`,`ProprietaryIdentifier` as `Proprietary Identifier`,`PrintId`,`OnlineId`,`DataType` as `Data Type`,`AccessDeniedCategory` as `Access denied category`,";
	public static final String TR3_QUERY = "`Publisher`,`Platform`,`TitleDOI` as `Title DOI`,`ProprietaryIdentifier` as `Proprietary Identifier`,`PrintId`,`OnlineId`,`DataType` as `Data Type`,`pageType`,";
	public static final String DB1_QUERY = "`Publisher`,`UserActivity`,";
	public static final String DB2_QUERY = "`Publisher`,`Platform`,`AccessDeniedCategory` as `Access denied category`,";
	public static final String MULTIMEDIA_QUERY = "`Publisher`,`Platform`,";
	//public static final String PR1_QUERY = "`Publisher`,`UserActivity`,";
	public static final String EBOOK_CHAPTER_QUERY = "BookId,BookTitle,ISBN,CopyrightYear,ChapterTitle,ChapterId,";
	public static final String CONSOTIUM_OVERVIEW_REPORT_QUERY = "BusinessPartnerId,City,Metric,";
	public static final String INSTITUTIONAL_OVERVIEW_REPORT_QUERY = "Title,ISBN,EIsbn,PIssn,EIssn,SubjectCollection,CopyrightPublication,Metric,";
	// public static final String
	// EBOOK_CHAPTER_MIT_QUERY="`publisher`,`userActivity`,";
	public static final String TR_BOOK_MASTER_TITLE = "`Title`,`Publisher`,`Publisher_ID`,`Platform`,`DOI`,`Proprietary_ID`,`ISBN`,`Print_ISSN`,`Online_ISSN`,`URI`,";
	public static final String TR_JOURNAL_MASTER_TITLE = "`Parent_Title` AS title,`Publisher`,`Publisher_ID`,`Platform`,`DOI`,`Proprietary_ID`,`Print_ISSN`,`Online_ISSN`,`URI`,";
	
	//TR_J1 Counter Report
	public static final String TR_J1_MASTER_TITLE = " `parent_title` AS Title,`Parent_publisher` AS Publisher,`Publisher_ID`,`Platform`, parent_doi AS `DOI`, parent_proprietary_id AS `Proprietary_ID`,`parent_Print_ISSN` AS Print_ISSN,`parent_Online_ISSN` AS Online_ISSN,`parent_URI` AS URI,`Metric_Type`";
	public static final String TR_J1_WHERE_CONDITION = "Metric_Type IN ('Total_Item_Requests','Unique_Item_Requests') AND Data_Type='Journal' AND Access_Method='Regular' AND Access_Type='Controlled'  AND Institution_ID != ''  AND Title_ID != ''  AND Parent_Title != ''";
	public static final String TR_J1_GROUPBY = "`parent_title` ,`Parent_publisher`,`Publisher_ID`,`Platform`, parent_doi , parent_proprietary_id ,`parent_Print_ISSN`,`parent_Online_ISSN`,`parent_URI`,`Metric_Type`,`Title_ID`";		

	//TR_J2 Counter Report
	public static final String TR_J2_MASTER_TITLE = "`parent_title` AS Title,`Parent_publisher` AS Publisher,`Publisher_ID`,`Platform`, parent_doi AS `DOI`, parent_proprietary_id AS `Proprietary_ID`,`parent_Print_ISSN` AS Print_ISSN,`parent_Online_ISSN` AS Online_ISSN,`parent_URI` AS `URI`,`Metric_Type`";
	public static final String TR_J2_WHERE_CONDITION = "Metric_Type IN ('Limit_Exceeded','No_License') AND Data_Type='Journal' AND Access_Method='Regular'  AND Institution_ID != ''  AND Title_ID != ''  AND parent_Title != ''";
	public static final String TR_J2_GROUPBY = "`parent_title`,`Parent_publisher`,`Publisher_ID`,`Platform`, parent_doi, parent_proprietary_id,`parent_Print_ISSN`,`parent_Online_ISSN`,`parent_URI`,`Metric_Type`,`Title_ID`";
	
	//TR_J2 Counter Report
	public static final String TR_J2_MASTER_TITLE_IEEECS = "`parent_title` AS Title,`Parent_publisher` AS Publisher,`Publisher_ID`,`Platform`, parent_doi AS `DOI` ";
	public static final String TR_J2_WHERE_CONDITION_IEEECS = "Metric_Type='Total_Item_Requests' AND Data_Type='Journal' AND Access_Method='Regular' AND `Access_Type` NOT IN ('OA_GOLD') AND Item_ID NOT IN('-',' ') AND data_type !='Proceedings'";
	public static final String TR_J2_GROUPBY_IEEECS = "`parent_title`,`Parent_publisher`,`Publisher_ID`,`Platform`, parent_doi, parent_proprietary_id,`parent_Print_ISSN`,`parent_Online_ISSN`,`parent_URI`,`Title_ID`";
		
		
	//JR1_GOA Counter Report
	public static final String J1_GOA_MASTER_TITLE = "`Parent_Title` AS title ,`Publisher`,`Publisher_ID`,`Platform`,`DOI`,`Proprietary_ID`,`Print_ISSN`,`Online_ISSN`,`URI`,`Metric_Type`";
	public static final String J1_GOA_WHERE_CONDITION = "`Metric_Type` IN ('Total_Item_Requests','Unique_Item_Requests') AND `Data_Type`='Journal' AND Access_type='OA_Gold'";
	public static final String J1_GOA_GROUPBY ="GROUP BY `Parent_Title` ,`Publisher`,`Publisher_ID`,`Platform`,`DOI`,`Proprietary_ID`,`Print_ISSN`,`Online_ISSN`,`URI`,`Metric_Type`";
	
	//Standard Counter additional report
	public static final String STANDARD_MASTER_TITLE = " `parent_title` AS Title,`Publisher`,`Publisher_ID`,`Platform`, parent_doi AS `DOI`, parent_proprietary_id AS `Proprietary_ID`,`parent_Print_ISSN` AS Print_ISSN,`parent_Online_ISSN` AS Online_ISSN,`parent_URI` AS URI,`Metric_Type`,";
	public static final String STANDARD_WHERE_CONDITION = "Metric_Type IN ('Total_Item_Requests','Unique_Item_Requests') AND Data_Type='Standard' AND Access_Method NOT IN('-',' ') AND access_type NOT IN('-',' ')  AND Institution_ID != ''  AND Title_ID != ''  AND Parent_Title != ''";
	public static final String STANDARD_GROUPBY =" GROUP BY `parent_title` ,`Publisher`,`Publisher_ID`,`Platform`, parent_doi , parent_proprietary_id ,`parent_Print_ISSN`,`parent_Online_ISSN`,`parent_URI`,`Metric_Type`";
	
	//TR_License_Denial additional report
	public static final String TR_LIC_DEN_MONTH_MASTER_TITLE = " `parent_title` AS Title,`Publisher`,`Publisher_ID`,`Platform`, parent_doi AS `DOI`, parent_proprietary_id AS `Proprietary_ID`,`parent_Print_ISSN` AS Print_ISSN ,`parent_Online_ISSN` AS Online_ISSN,`parent_URI`AS URI,`Metric_Type`,title_type AS Title_type,";
	public static final String TR_LIC_DEN_MONTH_WHERE_CONDITION = "Metric_Type IN ('Limit_Exceeded','No_License') AND Data_Type NOT IN ('BOOK') AND Access_Method='Regular'  AND Institution_ID != ''  AND Title_ID != ''  AND parent_Title != '' ";
	public static final String TR_LIC_DEN_MONTH_GROUPBY =" GROUP BY `parent_title`,`Publisher`,`Publisher_ID`,`Platform`, parent_doi, parent_proprietary_id,`parent_Print_ISSN`,`parent_Online_ISSN`,`parent_URI`,`Metric_Type`,title_type ";
	
	//TR_ABS_REQ_EBOOK additional report
	public static final String TR_ABS_REQ_EBOOK_MASTER_TITLE = " `Title_ID`,`parent_title` AS Title,Publisher,ISBN,Metric_Type,";
	public static final String TR_ABS_REQ_EBOOK_WHERE_CONDITION = " Institution_ID != ''  AND Item_ID != ''  AND Item != '' AND metric_type IN('Total_Item_Investigations','Unique_Title_Investigations') AND access_type NOT IN('-',' ') AND Access_Method NOT IN('-',' ') AND data_type='BOOK' AND section_type NOT IN('-',' ') AND yop NOT IN('-',' ') ";
	public static final String TR_ABS_REQ_EBOOK_GROUPBY =" GROUP BY `Title_ID`,`parent_title`,Publisher,Title_type,ISBN,Metric_Type  ";
	
	//TR_ART_REQ_PARTNER additional report
	public static final String TR_ART_REQ_PARTNER_MASTER_TITLE = " title_type AS Title_Type ,Metric_type AS Metric_Type,";
	public static final String TR_ART_REQ_PARTNER_WHERE_CONDITION = " Metric_Type IN (\"Total_Item_Requests\",\"Unique_Item_Requests\") AND title_type IN (\"VDE Conferences\",\"SMPTE Conferences\",\"BIAI Journals\",\"IBM Journals\",\"MITP Journals\",\"TUP Journals\",\"SMPTE Journals\",\"AGU Journals\",\"BLT Journals\",\"CPSS Journals\",\"URSI Journals\",\"CMP Journals\",\"CES Journals\",\"OUP Journals\",\"SAIEE Journals\",\"SMPTE Standards\",\"Wiley-IEEE eBook\",\"M&C eBook\",\"MIT eBook\",\"NOW eBook\",\"SAE eBook\") ";
	public static final String TR_ART_REQ_PARTNER_GROUPBY =" GROUP BY title_type , Metric_type ";
	
	//IP_ART_REQ_MONTH additional report
	public static final String IP_ART_REQ_MONTH_MASTER_TITLE = " `Title_ID`,`parent_title` AS Title,Publisher,ISBN,Metric_Type,";
	public static final String IP_ART_REQ_MONTH_WHERE_CONDITION = " Institution_ID != ''  AND Item_ID != ''  AND Item != '' AND metric_type IN('Total_Item_Requests') AND access_type NOT IN('-',' ') AND Access_Method NOT IN('-',' ') AND data_type='JOURNAL' AND section_type NOT IN('-',' ') AND yop NOT IN('-',' ') ";
	public static final String IP_ART_REQ_MONTH_GROUPBY =" GROUP BY `Title_ID`,`parent_title`,Publisher,Title_type,ISBN,Metric_Type  ";
	
	//SITE_OVERVIEW additional report
	public static final String SITE_OVERVIEW_MASTER_TITLE = " `Title_ID`,`parent_title` AS Title,Publisher,ISBN,Metric_Type,";
	public static final String SITE_OVERVIEW_WHERE_CONDITION = " Institution_ID != ''  AND Item_ID != ''  AND Item != '' AND metric_type IN('Total_Item_Investigations') AND access_type NOT IN('-',' ') AND Access_Method NOT IN('-',' ') AND data_type='BOOK' AND section_type NOT IN('-',' ') AND yop NOT IN('-',' ') ";
	public static final String SITE_OVERVIEW_GROUPBY =" GROUP BY `Title_ID`,`parent_title`,Publisher,Title_type,ISBN,Metric_Type  ";
		
	// change by satyam 07/02/2019 for TR_ART_ABS_VIEW
	 public static final String TR_ART_ABS_VIEW_MASTER_TITLE="`Parent_Title`,`Item`,`issue_volume`,`issue_no`,`page_no`";
	 public static final String TR_ART_ABS_VIEW_WHERE_CONDITION="Metric_Type='Total_Item_Investigations' AND Data_Type !='Book' AND Access_Method='Regular' AND Access_Type  IN ('Controlled','OA_GOLD') AND Institution_ID != '' AND Title_ID != '' AND Parent_Title != ''";
	public static final String TR_ART_ABS_VIEW_GROUPBY="Item_ID,`DOI`,`Parent_Title`,`Item`,`issue_volume`,`issue_no`,`page_no`";
	
	//  change by satyam kumar singh 07/02/2019 for TR_ART_REQ_TITLE
	public static final String TR_ART_REQ_TITLE_MASTER_TITLE="`Parent_Title`,`Item`,`issue_volume`,`issue_no`,`page_no`,Metric_Type";
	public static final String TR_ART_REQ_TITLE_WHERE_CONDITION="Metric_Type in ('Total_Item_requests','Unique_Item_requests') AND Data_Type NOT IN ('Book','courses') AND Access_Method='Regular' AND Access_Type IN ('Controlled','OA_GOLD')  AND Institution_ID != ''  AND Title_ID != ''  AND Parent_Title != '' ";
	public static final String TR_ART_REQ_TITLE_GROUPBY="Item_ID,`DOI`,`Parent_Title`,`Item`,`issue_volume`,`issue_no`,`page_no`,Metric_Type";
	
	// change by satyam kumar singh 07/02/2019 for TR_ART_REQ_CONSORTIA_MEMBERMASTER
	public static final  String TR_ART_REQ_CONSORTIA_MEMBERMASTER_TITLE="`child_institution_id`, `child_institutionName`,`metric_type`";
	public static final String TR_ART_REQ_CONSORTIA_MEMBER_WHERE_CONDITION = "Metric_Type IN ('Total_Item_requests','Unique_Item_requests')";
	
	// added by satyam 
	public  static final String TR_ART_REQ_TYPE_MASTER_TITLE="  CASE WHEN title_type IN (\"VDE Conferences\",\"SMPTE Conferences\") THEN 'Partner Conferences'  WHEN title_type IN (\"BIAI Journals\",\"IBM Journals\",\"MITP Journals\",\"TUP Journals\",\"SMPTE Journals\",\"AGU Journals\",\"BLT Journals\",\"CPSS Journals\",\"URSI Journals\",\"CMP Journals\",\"CES Journals\",\"OUP Journals\",\"SAIEE Journals\") THEN 'Partner Journals'  WHEN title_type IN (\"SMPTE Standards\") THEN 'Partner Standards'\r\n" + 
			"           WHEN title_type IN (\"Wiley-IEEE eBook\",\"M&C eBook\",\"MIT eBook\",\"NOW eBook\",\"SAE eBook\") THEN 'Partner eBook' WHEN title_type IN(\"IEEE Journals\", \"CSEE Journals\") THEN 'IEEE Journals'\r\n" + 
			"           ELSE title_type\r\n" + 
			"        END ";
	
	public static final String TR_ART_REQ_TYPE_WHERE_CONDITION=" Metric_Type IN (\"Total_Item_Requests\",\"Unique_Item_Requests\") AND data_type !=\" \" AND title_type NOT IN (\"eBook\",\"courses\") ";
	
	
	//TR_CONF4 additional report
	public static final String TR_CONF_YOP_MASTER_TITLE = " `parent_title` AS Title,`Publisher`,`Publisher_ID`,`Platform`, parent_doi AS `DOI`, parent_proprietary_id AS `Proprietary_ID`,`parent_Print_ISSN` AS Print_ISSN,`parent_Online_ISSN` AS Online_ISSN,`parent_URI` AS URI,`YOP`,`Metric_Type`,";
	public static final String TR_CONF_YOP_WHERE_CONDITION = "`Metric_Type` IN ('Total_Item_Requests','Unique_Item_Requests') AND `Data_Type`='Conference'  AND Access_Method='Regular' AND Access_Type IN ('Controlled','OA_GOLD') AND Institution_ID != ''  AND Title_ID != ''  AND Parent_Title != ''";
	public static final String TR_CONF_YOP_GROUPBY =" GROUP BY `Parent_Title`,`Publisher`,`Publisher_ID`,`Platform`,`parent_doi`,`parent_proprietary_id`,`parent_Print_ISSN`,`parent_Online_ISSN`,`parent_URI`,`YOP`,`Metric_Type`";
	
	//TR_WIBCRMI additional report
	public static final String TR_WIBCRMI_MASTER_TITLE = " `Item`,`Publisher`,`Publisher_ID`,`Platform`,`Authors`,`Publication_Date`,`Article_Version`,`DOI`,`Proprietary_ID`,`ISBN`,`Print_ISSN`,`Online_ISSN`,`URI`,`Parent_Title`,`Parent_Data_Type`,`Parent_DOI`,`Parent_Proprietary_ID`,`Parent_ISBN`,`Parent_Print_ISSN`,`Parent_Online_ISSN`,`Parent_URI`,`Metric_Type`,";
	public static final String TR_WIBCRMI_WHERE_CONDITION = "Metric_Type IN ('Total_Item_Requests','Unique_Title_Requests') AND Data_Type IN ('Book') AND Title_type LIKE '%WILEY%' AND Access_Method = 'Regular' AND Item_ID NOT IN('-',' ') AND Item NOT IN('-',' ') ";
	public static final String TR_WIBCRMI_GROUPBY =" GROUP BY `Item`,`Publisher`,`Publisher_ID`,`Platform`,`Authors`,`Publication_Date`,`Article_Version`,`DOI`,`Proprietary_ID`,`ISBN`,`Print_ISSN`,`Online_ISSN`,`URI`,`Parent_Title`,`Parent_Data_Type`,`Parent_DOI`,`Parent_Proprietary_ID`,`Parent_ISBN`,`Parent_Print_ISSN`,`Parent_Online_ISSN`,`Parent_URI`,`Metric_Type` ";
	
	//TR_J3 Counter Report
	//public static final String TR_J3_MASTER_TITLE = "`Title` AS Title ,`Publisher`,`Publisher_ID`,`Platform`,`DOI`,`Proprietary_ID`,`Print_ISSN`,`Online_ISSN`,`URI`,`Access_Type`,`Metric_Type`";
	//change by KSV on 2018-12-14 for title ==> parent_title and title_id as Proprietary_ID

	//public static final String TR_J3_WHERE_CONDITION = "Metric_Type IN ('Total_Item_Investigations','Total_Item_Requests','Unique_Item_Requests','Unique_Item_Investigations') AND Data_Type='Journal' AND Access_Method='Regular'  AND Institution_ID != ''  AND Title_ID != ''  AND Title != ''";
	//change by KSV on 2018-12-14 for title ==> parent_title and blank Access_Type
	public static final String TR_J3_WHERE_CONDITION = "Metric_Type IN ('Total_Item_Investigations','Total_Item_Requests','Unique_Item_Requests','Unique_Item_Investigations') AND Data_Type='Journal' AND Access_Method='Regular'  AND Institution_ID != ''  AND Title_ID != ''  AND Parent_Title != '' AND Access_Type != ''" ;
	//change by KSV on 2018-12-14 for title ==> parent_title and title_id as Proprietary_ID
	public static final String TR_J3_GROUPBY ="GROUP BY `Parent_Title` ,`Parent_publisher`,`Publisher_ID`,`Platform`,`parent_doi`,`Title_ID`,`parent_Print_ISSN`,`parent_Online_ISSN`,`parent_URI`,`Access_Type`,`Metric_Type`";
		
	//TR_J4 Counter Report
	public static final String TR_J4_MASTER_TITLE = "`parent_title` AS Title,`Parent_publisher` AS Publisher,`Publisher_ID`,`Platform`, parent_doi AS `DOI`, title_id AS `Proprietary_ID`,`parent_Print_ISSN` AS Print_ISSN,`parent_Online_ISSN` AS Online_ISSN,`parent_URI` AS URI,`YOP`,`Metric_Type`";
	public static final String TR_J4_WHERE_CONDITION = "`Metric_Type` IN ('Total_Item_Requests','Unique_Item_Requests') AND `Data_Type`='Journal'  AND Access_Method='Regular' AND Access_Type='Controlled' AND Institution_ID != ''  AND Title_ID != ''  AND Parent_Title != ''";
	public static final String TR_J4_GROUPBY ="GROUP BY `Parent_Title`,`Parent_publisher`,`Publisher_ID`,`Platform`,`parent_doi`,`title_id`,`parent_Print_ISSN`,`parent_Online_ISSN`,`parent_URI`,`YOP`,`Metric_Type`";
		
	//TR_J4 Counter Report
	public static final String TR_NO_LIC_TITLE = "Title, Publisher, Publisher_ID, Platform, DOI, Proprietary_ID, Print_ISSN, Online_ISSN, URI,`Metric_Type`";
	public static final String TR_NO_LIC_WHERE_CONDITION = "Access_Method='Regular' AND Metric_Type IN ('No_License') AND Data_Type IN ('Journal', 'Conference', 'Standard')  AND Institution_ID != ''  AND Title_ID != ''  AND Title != ''";
	
	//IR_Master Report
	public static final String IR_MASTER_TITLE_1 = "`Item`,`Publisher`,`Publisher_ID`,`Platform`";
	public static final String IR_MASTER_TITLE_2 ="`DOI`,`Proprietary_ID`,`ISBN`,`Print_ISSN`,`Online_ISSN`,`URI`";
	public static final String IR_OPTIONAL_TITLE_1 = "`Authors`,`Publication_Date`,`Article_Version`";
	public static final String IR_OPTIONAL_TITLE_2 = "`Parent_Title`,`Parent_Data_Type`,`Parent_DOI`,`Parent_Proprietary_ID`,`Parent_ISBN`,`Parent_Print_ISSN`,`Parent_Online_ISSN`,`Parent_URI`,`Component_Title`,`Component_Data_Type`,`Component_DOI`,`Component_Proprietary_ID`,`Component_ISBN`,`Component_Print_ISSN`,`Component_Online_ISSN`,`Component_URI`,`Data_Type`,`Section_Type`,`YOP`,`Access_Type`,`Access_Method`";
	
	
	public static final String IR_DEFAULT_METRIC_TYPE ="'Total_Item_Investigations' ,'Total_Item_Requests' ,'Unique_Item_Investigations' ,'Unique_Title_Investigations' ,'Unique_Item_Requests' ,'No_License' ,'Limit_Exceeded'";
	public static final String IR_MASTER_WHERE_CONDITION = "Institution_ID != ''  AND Item_ID != ''  AND Item != ''"; 
	
	//IR_A1 Counter Report
	public static final String IR_A1_MASTER_TITLE_P1 = "`Item`,`Publisher`,`Publisher_ID`,`Platform`,`Authors`,`Publication_Date`,`Article_Version`,`DOI`,  ";
	public static final String IR_A1_MASTER_TITLE_P2 = "`Print_ISSN`,`Online_ISSN`,`URI`,`Parent_Title`,Parent_Authors, Parent_Article_Version,`Parent_DOI`,"
			+ "`Parent_Proprietary_ID`,`Parent_Print_ISSN`,`Parent_Online_ISSN`,`Parent_URI`, Access_Type,`Metric_Type`";
	public static final String IR_A1_WHERE_CONDITION = "Metric_Type in ('Total_Item_Requests','Unique_Item_Requests') AND Data_Type IN ('Journal','Article') AND Access_Method = 'Regular' AND Section_Type = 'Article'  AND Item_ID NOT IN('-',' ') AND Item NOT IN('-',' ') "; 
	public static final String IR_A1_GROUP_CONDITION = "GROUP BY `Item`,`Publisher`,`Publisher_ID`,`Platform`,`Authors`,`Publication_Date`,`Article_Version`,`DOI`,`Proprietary_ID`,`Print_ISSN`,`Online_ISSN`,`URI`,`Parent_Title`,Parent_Authors, Parent_Article_Version,`Parent_DOI`,`Parent_Proprietary_ID`,`Parent_Print_ISSN`,`Parent_Online_ISSN`,`Parent_URI`, Access_Type, `Metric_Type`";
	public static final String IR_A1_PUBLICATION_DATE_INPUT_FORMATE="dd/mm/yyyy";
	public static final String IR_A1_PUBLICATION_DATE_OUTPUT_FORMATE="yyyy-mm-dd";
	
	//IR_M1 (Multimedia) Counter Report
	public static final String IR_M1_MASTER_TITLE = "`Item`,`Publisher`,`Publisher_ID`,`Platform`,`Article_Version`,`DOI`,`Proprietary_ID`,`URI`,`Metric_Type`";
	public static final String IR_M1_WHERE_CONDITION ="Metric_Type='Total_Item_Requests' AND Data_Type='Multimedia' AND Access_Method='Regular'  AND Institution_ID != ''  AND Item_ID != ''  AND Item != ''";
	
	//DR Report
	public static final String DR_MASTER_TITLE="`Database`,`Publisher`,`Publisher_ID`,`Platform`";
	public static final String DR_OPTIONAL_TITLE="`Data_Type`,`Access_Method`,";
	public static final String DR_DEFAULT_METRIC_TYPE ="'Searches_Automated' ,'Searches_Federated' ,'Searches_Regular' ,'Total_Item_Investigations' ,'Total_Item_Requests' ,'Unique_Item_Investigations' ,'Unique_Title_Investigations' ,'Unique_Item_Requests' ,'Unique_Title_Request' ,'Limit_Exceeded' ,'No_License'";
	public static final String DR_WHERE_CONDITION ="Institution_ID !='' AND Title_ID!='' AND `Database`!=''";
	
	//DR_D1 
	public static final String DR_D1_MASTER_TITLE="`Database`,`Publisher`,`Publisher_ID`,`Platform`,`Proprietary_ID`, `Metric_Type`";
	public static final String DR_D1_WHERE_CONDITION ="Metric_Type IN ('Searches_Automated','Total_Item_Requests','Total_Item_Investigations','Searches_Regular','Searches_Federated') AND Data_Type='Database' AND Access_Method in ('Regular') AND Access_Type in('Controlled','OA_Gold', 'Other_Free_To_Read')  AND Institution_ID != ''  AND Title_ID != ''  AND `Database` != ''";
	
	
	//DR_D2
	public static final String DR_D2_MASTER_TITLE="`Database`,`Publisher`,`Publisher_ID`,`Platform`,`Proprietary_ID`,`Metric_Type`";
	public static final String DR_D2_WHERE_CONDITION ="Metric_Type IN ('No_License','Limit_Exceeded') AND Institution_ID != ''  AND Title_ID != ''  AND `Database` != ''";
		
	
	public static final String TR_DEFAULT_METRIC_TYPE="'Total_Item_Investigations' ,'Total_Item_Requests' ,'Unique_Item_Investigations' ,'Unique_Item_Requests' ,'Unique_Title_Investigations' ,'Unique_Title_Requests' ,'Limit_Exceeded' ,'No_License'";
	public static final String TR_MASTER_TITLE="`Parent_Title` AS 'Title',`Parent_publisher` AS Publisher, `Publisher_ID`,`Platform`,`Parent_DOI` as 'DOI',`Title_ID` as `Proprietary_ID`,`parent_ISBN` As 'ISBN',`Parent_Print_ISSN` as 'Print_ISSN', `Parent_Online_ISSN` as 'Online_ISSN', `Parent_URI` as `URI`";
	public static final String TR_OPTIONAL_TITLE="`Data_Type`,`Section_Type`,`YOP`,`Access_Type`,`Access_Method`";
	//addeed by kapil for report corection
	public static final String TR_GROUPBY ="`Parent_Title` ,`Parent_publisher`,`Publisher_ID`,`Platform`,`Parent_DOI`,`Title_ID`,`Parent_ISBN`,`Parent_Print_ISSN`,`parent_Online_ISSN`, `Parent_URI`, `Metric_type`";
	
	// added by satyam for mit ebook IEEE Library Report, 2019-01-17
	public static final String MIT_EBOOK_MASTER_TITLE="`Item`,`Publisher`,`Publisher_ID`,`Platform`,`Authors`,`Publication_Date`,`Article_Version`,`DOI`,`Proprietary_ID`,`ISBN`,`Print_ISSN`,`Online_ISSN`,`URI`,`Parent_Title`,`Parent_Data_Type`,`Parent_DOI`,`Parent_Proprietary_ID`,`Parent_ISBN`,`Parent_Print_ISSN`,`Parent_Online_ISSN`,`Parent_URI`,`Metric_Type`";
	public static final String MIT_EBOOK_WHERE_CONDITION="Metric_Type IN ('Total_Item_Requests','Unique_Title_Requests') AND Data_Type IN ('Book') AND Title_type LIKE \"%MIT%\" AND Access_Method = 'Regular' AND Item_ID NOT IN('-',' ') AND Item NOT IN('-',' ') ";

	
	public static final String EDU_COUR = "Title, Publisher, Publisher_ID, Platform, DOI, Proprietary_ID, Print_ISSN, Online_ISSN, URI,";
	public static final String TR_NO_LICEN_DENI = "Title, Publisher, Publisher_ID, Platform, DOI, Proprietary_ID, Print_ISSN, Online_ISSN, URI,";
	public static final String IP_ARTICLE_SEARCH = "IP,";
	public static final String IP_ARTICLE_VIEW = "IP,";
	public static final String EBOOK_INVEST_TITLE = "Title, Publisher, Publisher_ID, Platform, DOI, Proprietary_ID, ISBN, Print_ISSN, Online_ISSN,";
	public static final String TR_JOURNAL4_MASTER_TITLE = "`Parent_Title` AS `Title` ,`Publisher`,`Publisher_ID`,`Platform`,`DOI`,`Proprietary_ID`,`Print_ISSN`,`Online_ISSN`,`URI`,";
	//public static final String DR_MASTER_TITLE = "`Database`,`Publisher`,`Publisher_ID`,`Platform`,`Proprietary_ID`,";
	public static final String PR_MASTER_TITLE = "`Platform`,";
	public static final String PR_DEFAULT_METRIC_TYPE ="'searches_platform','Total_Item_Investigations' ,'Total_Item_Requests' ,'Unique_Item_Investigations' ,'Unique_Item_Requests' ,'Unique_Title_Investigations' ,'Unique_Title_Requests'";
	
	public static final String EBOOK_ACCESS_CHAPTER="`Metric_Type`";
	public static final String TR_CONF=" `parent_title` AS Title,`Publisher`,`Publisher_ID`,`Platform`, parent_doi AS `DOI`, parent_proprietary_id AS `Proprietary_ID`,`parent_Print_ISSN` AS Print,`parent_Online_ISSN` AS Online_ISSN,`parent_URI` AS URI,`Metric_Type`";
	public static final String TR_CONF_WHERE_CONDITION = " Metric_Type IN ('Total_Item_Requests','Unique_Item_Requests') AND Data_Type='Conference' AND Access_Method NOT IN('-',' ') AND access_type NOT IN('-',' ')  AND Institution_ID != ''  AND Title_ID != ''  AND Parent_Title != ''";
	public static final String TR_CONF_MASTER_TITLE_1 = " `parent_title` ,`Publisher`,`Publisher_ID`,`Platform`, parent_doi , parent_proprietary_id ,`parent_Print_ISSN`,`parent_Online_ISSN`,`parent_URI`,`Metric_Type`";
	public static final String EDU_COUR_1=" `parent_title` AS Title,`Publisher`,`Publisher_ID`,`Platform`, parent_doi AS `DOI`, parent_proprietary_id AS `Proprietary_ID`,`parent_Print_ISSN` AS Print_ISSN,`parent_Online_ISSN` AS Online_ISSN,`parent_URI` AS URI,`Metric_Type`";
	public static final String EDU_COUR_1_WHERE_CONDITION = " Metric_Type IN ('Total_Item_Requests','Unique_Item_Requests') AND Data_Type='Courses' AND Access_Method NOT IN('-',' ') AND access_type NOT IN('-',' ')  AND Institution_ID != ''  AND Title_ID != ''  AND Parent_Title != ''";
	public static final String EDU_COUR_1_MASTER_TITLE_1 = " `parent_title` ,`Publisher`,`Publisher_ID`,`Platform`, parent_doi , parent_proprietary_id ,`parent_Print_ISSN`,`parent_Online_ISSN`,`parent_URI`,`Metric_Type`";
	public static final String IP_ARTICLE_TITLE = "`ip` as `IP`,";
	public static final String TURNAWAYS_BY_TYPE = "Title_Type,";
	public static final String PARTNER_REQUEST_BY_TYPE = "Title_Type,";
	public static final String WILEY_IEEE_EBOOK_CHAPTER = "Item, Publisher, Publisher_ID, Platform, `Authors`, Publication_Date, Article_Version AS `Chapter_Version`, "
			+ " DOI, Proprietary_ID, ISBN, Print_ISSN, Online_ISSN, URI,"
			+ " Parent_Title, Parent_Data_Type, Parent_DOI,Parent_Proprietary_ID, Parent_ISBN, Parent_Print_ISSN, Parent_Online_ISSN,Parent_URI,";
	public static final String MIT_EBOOK_CHAPTER = "Item, Publisher, Publisher_ID, Platform, `Authors`, Publication_Date, Article_Version AS `Chapter_Version`, "
			+ "DOI, Proprietary_ID, ISBN, Print_ISSN, Online_ISSN, URI,  Parent_Title, Parent_Data_Type, Parent_DOI,Parent_Proprietary_ID, "
			+ "Parent_ISBN, Parent_Print_ISSN, Parent_Online_ISSN,Parent_URI,";
	public static final String ARTICLE_REQUEST_TITLE = "`DOI`, `Title`, `Item`, `Volume`, `Issue`, `Page`,";
	
	public static final String ERROR_RESPONSE = "{\"error\":\"";
	public static final String ERROR_SESSION = "{\"error\":\" Invalid Session \"}";
	public static final String SUCCESS_RESPONSE = "{\"success\":\"";
	
	public static final String JR1 = "JR1";
	public static final String JR1GOA = "JR1GOA";
	public static final String JR2 = "JR2";
	public static final String JR3 = "JR3";
	public static final String JR4 = "JR4";
	public static final String JR5 = "JR5";
	public static final String BR1 = "BR1";
	public static final String BR2 = "BR2";
	public static final String BR3 = "BR3";
	public static final String TR1 = "TR1";
	public static final String TR2 = "TR2";
	public static final String TR3 = "TR3";
	public static final String DB1 = "DB1";
	public static final String DB1TABLE = "DataBaseReport1";
	public static final String DB2 = "DB2";
	public static final String PR1 = "PR1";
	public static final String PR1TABLE = "PlatformReport1";
	public static final String STANDARDS = "Standards";
	public static final String CONFERENCES = "Conferences";
	public static final String CONSOTIUM_OVERVIEW_REPORT = "Consortium_Overview_Report";
	public static final String INSTITUTIONAL_OVERVIEW_REPORT = "Institutional_Overview_Report";
	public static final String ARTICLE_REQUEST_BY_CONSORTIA_MEMBER_REPORT = "consortia_member_i";
	
	
	
	public static final String TR_J1 = "TR_J1";
	public static final String TR_J2 = "TR_J2";
	public static final String TR_J3 = "TR_J3";
	public static final String TR_J4 = "TR_J4";
	public static final String TR_B1 = "TR_B1";
	public static final String TR_B2 = "TR_B2";
	public static final String TR_B3 = "TR_B3";
	public static final String DR_D1 = "DR_D1";
	public static final String DR_D2 = "DR_D2";
	public static final String PR_P1 = "PR_P1";
	public static final String IR_A1 = "IR_A1";
	public static final String IR_M1 = "IR_M1";
	public static final String CSV = "csv";
	public static final String TSV = "tsv";
	public static final String XLSX = "xlsx";
	public static final String XLS = "xls";
	public static final String ERROR = "error";
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	public static final String NAME = "name";
	public static final String DATA = "data";
	public static final String KEY = "key";
	public static final String VALUE = "value";
	public static final String DESC = "desc";
	public static final String REPORT_NAME = "Report_Name";
	public static final String REPORT_ID = "Report_ID";
	public static final String RELEASE = "Release";
	public static final String INSTITUTION_NAME = "Institution_Name";
	public static final String INSTITUTION_ID = "Institution_ID";
	public static final String RECORD = "record";
	public static final String SERIES = "series";
	public static final String METRIC_TYPES = "Metric_Types";
	public static final String REPORT_FILTERS = "Report_Filters";
	public static final String REPORT_ATTRIBUTES = "Report_Attributes";
	public static final String EXCEPTIONS = "Exceptions";
	public static final String REPORTING_PERIOD = "Reporting_Period";
	public static final String CREATED = "Created";
	public static final String CREATED_BY = "Created_By";
	public static final String ARTICLE_REQ_BY_TYPE = "article_req_by_type";
	public static final String EBOOK_CHAPTER = "ebook_chapter";
	public static final String EBOOK_CHAPTER_MIT = "ebook_chapter_mit";
	
	
	public static final String PR_MASTER_TITLE_1 = "`Platform`";
	public static final String PR_OPTIONAL_TITLE = "";
	
	public static final String PR_P1_MASTER_TITLE_1 = "`Platform`,`Metric_Type`";
	public static final String PR_P1_WHERE_CONDITION = "Metric_Type IN ('Total_Item_Requests','Unique_Item_Requests','Unique_Title_Requests') ";
	public static final String PR_P1_WHERE_CONDITION2="metric_type IN ('Searches_Platform')";
	
	public static final String TR_B1_MASTER_TITLE_1 = "`Parent_Title` as 'Title',`Publisher`,`Publisher_ID`,`Platform`,`Parent_DOI` as 'DOI',`parent_Proprietary_ID` As 'Proprietary_ID',`ISBN` As 'ISBN',`Parent_Print_ISSN` As 'Print_ISSN',`Parent_Online_ISSN` As 'Online_ISSN',`URI`,`Access_Type`,`Metric_Type`";
	public static final String TR_B1_GOUPBY = "`Parent_Title`,`Publisher`,`Publisher_ID`,`Platform`,`Parent_DOI`,`parent_Proprietary_ID`,`Praent_ISBN`,`Parent_Print_ISSN`,`Parent_Online_ISSN`,`URI`,`Access_Type`,`Metric_Type`";
	
	public static final String TR_B2_MASTER_TITLE_1 = "`Parent_Title` as 'Title',`Parent_publisher` AS Publisher,`Publisher_ID`,`Platform`,`Parent_DOI` as 'DOI', `Parent_Proprietary_ID` as 'Proprietary_ID',`Parent_ISBN` as 'ISBN',`parent_Print_ISSN` as 'Print_ISSN',`Parent_Online_ISSN` As 'Online_ISSN',`Parent_URI` as 'URI', `YOP` as 'YOP', `Metric_Type`";
	public static final String TR_B2_GROUPBY = "`Parent_Title`,`Parent_publisher`,`Publisher_ID`,`Platform`,`Parent_DOI`,`Parent_Proprietary_ID`,`parent_ISBN`,`Parent_Print_ISSN`,`Parent_Online_ISSN`,`Parent_URI`, `YOP`, `Metric_Type`";
	
	public static final String TR_B3_MASTER_TITLE = "`Parent_Title` as 'Title',`Parent_publisher` AS Publisher,`Publisher_ID`,`Platform`,`Parent_DOI` as 'DOI',`parent_Proprietary_ID` As 'Proprietary_ID',`Parent_ISBN` As 'ISBN',`Parent_Print_ISSN` As 'Print_ISSN',`Parent_Online_ISSN` As 'Online_ISSN',`Parent_URI` AS 'URI',`YOP` as 'YOP',`Access_Type`,`Metric_Type`";
	public static final String TR_B3_GROUPBY = "`Parent_Title` ,`Parent_publisher`,`Publisher_ID`,`Platform`,`Parent_DOI` ,`parent_Proprietary_ID` ,`Parent_ISBN`,`Parent_Print_ISSN`,`Parent_Online_ISSN`,`Parent_URI`,`YOP`,`Access_Type`,`Metric_Type`";
	public static final String TR_B3_WHERE_CONDITION = "Metric_Type IN ('Total_Item_Investigations','Total_Item_Requests','Unique_Item_Investigations','Unique_Item_Requests','Unique_Title_Investigations','Unique_Title_Requests') AND Access_Method='Regular' AND Data_Type='Book'  AND Access_Type not in('','-') AND Parent_Title not in('','-')";

	
	public static final String TR_B1_WHERE_CONDITION = "Metric_Type IN ('Total_Item_Requests','Unique_Title_Requests') AND Data_Type='Book' AND Access_Method='Regular' AND Access_Type='Controlled'  AND Institution_ID not in('', '-')  AND Title_ID not in ('','-')  AND Parent_Title not in('','-')";
	public static final String TR_B2_WHERE_CONDITION = "Metric_Type IN ('Limit_Exceeded','No_License') AND Access_Method='Regular' AND Data_Type='Book'  AND Institution_ID not in('','-') AND Parent_Title not in('-','')";
	public static final String MONGO_URI = "mongodb://192.168.80.29:27017"; //send it to config file
	public static final String KAFKA_BROKERS = "192.168.80.29:9092";
	public static final String LOG_TOPIC_NAME="insight_tag_log";
	public static final String FEED_TOPIC_NAME="insight_tag_feed";
	public static final String CLIENT_ID="insightclient";
	public static final String GROUP_ID_CONFIG="insightgroup";
	
	
	public static final String EBOOK_ACCESS_CHAPTER_WHERE_CONDITION ="Institution_ID != ''";
	public static final String IP_ART_REQ_WHERE_CONDITION ="Metric_Type IN ('Total_Item_requests','Unique_Item_requests') and data_type !='BOOK' AND Institution_ID != ''";
	public static final String IP_ART_REQ_MONTH="IP, Metric_Type";
	public static final String DR_GROUPBY = null;
	
}
