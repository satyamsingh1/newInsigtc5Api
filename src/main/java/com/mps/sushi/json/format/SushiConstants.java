package com.mps.sushi.json.format;

public class SushiConstants {
	public static final String TR = "Title Master Report";
	public static final String TR_J1 = "Journal Requests (excluding \"OA_Gold\")";
	public static final String TR_J2 = "Journal Access Denied";
	public static final String TR_J3 = "Journal Usage by Access Type";
	public static final String TR_J4 = "Journal Requests by YOP (excluding \"OA_Gold\")";
	public static final String TR_B1 = "Book Requests (excluding \"OA_Gold\")";
	public static final String TR_B2 = "Book Access Denied";
	public static final String TR_B3 = "Book Usage by Access Type";
	
	public static final String IR = "Item Master Report";
	public static final String IR_A1 = "Journal Article Requests";
	public static final String IR_M1 = "Multimedia Item Requests";
	
	public static final String PR = "Platform Master Report";
	public static final String PR_P1 = "Platform Usage";
	
	public static final String DR = "Database Master Report";
	public static final String DR_D1 = "Database Search and Item Usage";
	public static final String DR_D2 = "Database Access Denied";
	
	public static final String TITLE = "Title";
	public static final String ITEM = "Item";
	public static final String PLATFORM = "Platform";
	public static final String PUBLISHER = "Publisher";
	public static final String METRIC_TYPE = "Metric_Type";
	public static final String INSTITUTION_ID = "Institution_ID";
	
	public static final String TR_ID = "TR";
	public static final String TR_J1_ID = "TR_J1";
	public static final String TR_J2_ID = "TR_J2";
	public static final String TR_J3_ID = "TR_J3";
	public static final String TR_J4_ID = "TR_J4";
	public static final String TR_B1_ID = "TR_B1";
	public static final String TR_B2_ID = "TR_B2";
	public static final String TR_B3_ID = "TR_B3";
	public static final String IR_ID = "IR";
	public static final String IR_A1_ID = "IR_A1";
	public static final String IR_M1_ID = "IR_M1";	
	public static final String PR_ID = "PR";
	public static final String PR_P1_ID = "PR_P1";	
	public static final String DR_ID = "DR";
	public static final String DR_D1_ID = "DR_D1";
	public static final String DR_D2_ID = "DR_D2";
	public static final String CREATED_BY = "MPS Limited";
	
	public static final String F1000 = "Service Not Available";
	public static final String F1010 = "Service Busy";
	public static final String F1020 = "Client Has Made Too Many Requests";
	public static final String F1030 = "Insufficient Information to Process Request";
	public static final String E2000 = "Requestor Not Authorized to Access Service";
	public static final String E2010 = "Requestor is Not Authorized to Access Usage for Institution";
	public static final String E2020 = "APIKey Invalid";	
	public static final String E3000 = "Report Not Supported";
	public static final String E3010 = "Report Version Not Supported";
	public static final String E3020 = "Invalid Date Arguments";
	public static final String E3030 = "No Usage Available for Requested Dates";
	public static final String EW3031 = "Usage Not Ready for Requested Dates";	
	public static final String W3040 = "Partial Data Returned";
	public static final String W3050 = "Parameter Not Recognized in this Context";	
	public static final String EW3060 = "Invalid ReportFilter Value";
	public static final String EW3061 = "Incongruous ReportFilter Value";
	public static final String EW3062 = "Invalid ReportAttribute Value";
	public static final String EW3070 = "Required ReportFilter Missing";
	public static final String EW3071 = "Required ReportAttribute Missing";
	public static final String W3080 = "Limit Requested Greater than Maximum Server Limit";
	
	public static final String F1000_DATA = "Service is executing a request, but due to internal errors cannot complete the request.";
	public static final String F1010_DATA = "Service is too busy to execute the incoming request.";
	public static final String F1020_DATA = "Client Has Made Too Many Requests";
	public static final String F1030_DATA = "There is insufficient data in the request to begin processing";
	public static final String E2000_DATA = "Requestor Not Authorized to Access Service";
	public static final String E2010_DATA = "Requestor is Not Authorized to Access Usage for Institution";
	public static final String E2020_DATA = "The service being called requires a valid APIKey to access usage data and the key provided was not valid or not authorized for the data being requested.";	
	public static final String E3000_DATA = "Requested version of the data is not supported by the service.";
	public static final String E3010_DATA = "Requested version of the data is not supported by the service.";
	public static final String E3020_DATA = "Any format or logic errors involving date computations";
	public static final String E3030_DATA = "Service did not find any data for the date range specified.";
	public static final String EW3031_DATA = "Service has not yet processed the usage for one or more of the requested months, if some months are available that data should be returned.";	
	public static final String W3040_DATA = "Request could not be fulfilled in its entirety. Data that was available was returned.";
	public static final String W3050_DATA = "Request contained one or more parameters that are not recognized by the server in the context of the report being serviced.";	
	public static final String EW3060_DATA = "Request contained one or more filter values in the ReportDefinition that are not supported by the server.";
	public static final String EW3061_DATA = "A filter element includes multiple values in a pipedelimited list; however, the supplied values are not all of the same scope";
	public static final String EW3062_DATA = "Request contained one or more ReportAttribute values in the ReportDefinition that are not supported by the server.";
	public static final String EW3070_DATA = "A required filter was not included in the request.";
	public static final String EW3071_DATA = "A required report attribute was not included in the request.";
	public static final String W3080_DATA = "The requested value for limit (number of items to return) exceeds the server limit.";
	
}
