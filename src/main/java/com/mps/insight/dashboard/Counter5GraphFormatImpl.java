package com.mps.insight.dashboard;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.mps.insight.c4.report.DynamicDayMonthCreater;
import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dao.QueryTemplate;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.Utils;
import com.mps.insight.product.PublisherSettings;

public class Counter5GraphFormatImpl {

	private RequestMetaData rmd;
	QueryTemplate queryTemplate = null;

	public Counter5GraphFormatImpl(RequestMetaData rmd) {
		this.rmd = rmd;

	}

	// private static final Logger log =
	// LoggerFactory.getLogger(Counter5GraphFormatImpl.class);

	public String getArrayformatCountry(MyDataTable mdt) throws Exception {
		int row = mdt.getRowCount();
		int col = mdt.getColumnCount();
		StringBuffer sb = new StringBuffer();
		JsonArrayBuilder main = Json.createArrayBuilder();
		JsonArrayBuilder jr = Json.createArrayBuilder();
		JsonObjectBuilder jo = Json.createObjectBuilder();
		// JsonArrayBuilder sub = Json.createArrayBuilder();
		for (int i = 1; i <= row; i++) {
			main.add(mdt.getValue(i, "country"));
			main.add(Integer.parseInt(mdt.getValue(i, "count")));
			jr.add(main);
			// sb.append("['"+mdt.getValue(i, "country")+"',"+mdt.getValue(i,
			// "count")+"],");

		}
		// main.add(sb.toString());

		jo.add("geo", jr);

		return jo.build().toString();
	}

	public String getJsonFormatNoLicence(MyDataTable mdt, int year, int month) {
		JsonObjectBuilder finalJson = Json.createObjectBuilder();
		JsonObjectBuilder dataJson = Json.createObjectBuilder();
		JsonArrayBuilder monthArrayBuilder = Json.createArrayBuilder();
		JsonArrayBuilder recordArrayBuilder = Json.createArrayBuilder();
		JsonArrayBuilder monthDataArrayBuilder = null;
		for (int i = 1; i <= 12; i++) {
			monthArrayBuilder.add(InsightConstant.MONTH_ARRAY[i] + "-" + year);
		}
		finalJson.add("header", monthArrayBuilder.build());
		try {
			int row = mdt.getRowCount();
			int col = mdt.getColumnCount();
			// int col=mdt.getColumnCount();
			for (int i = 1; i <= row; i++) {
				monthDataArrayBuilder = Json.createArrayBuilder();

				for (int j = 2; j <= col; j++) {
					monthDataArrayBuilder.add(Integer.parseInt(mdt.getValue(i, j)));
				}
				for (int k = 1; k <= 12 - month; k++) {
					monthDataArrayBuilder.add(0);
				}
				dataJson.add("type", "column");
				dataJson.add(InsightConstant.NAME, mdt.getValue(i, "data_type"));
				dataJson.add("colorByPoint", false);
				dataJson.add(InsightConstant.DATA, monthDataArrayBuilder.build());
				recordArrayBuilder.add(dataJson.build());

			}
		} catch (Exception e) {
			rmd.exception("Exception : " + e.getMessage());
		}
		finalJson.add(InsightConstant.SERIES, recordArrayBuilder);
		return finalJson.build().toString();
	}

	public JsonObjectBuilder getJsonFormatSiteSummary(MyDataTable mdt, List<String> monthList) {
		JsonObjectBuilder finalJson = Json.createObjectBuilder();
		JsonObjectBuilder dataJson = Json.createObjectBuilder();
		JsonArrayBuilder monthArrayBuilder = Json.createArrayBuilder();
		JsonArrayBuilder recordArrayBuilder = Json.createArrayBuilder();
		JsonObjectBuilder dataJsonBuilder = null;
		JsonArrayBuilder monthDataArrayBuilder = null;

		monthList.forEach(ls -> monthArrayBuilder.add(ls));
		finalJson.add("header", monthArrayBuilder.build());

		try {
			int row = mdt.getRowCount();
			for (int i = 1; i <= row; i++) {
				int counter = 1;
				String name = mdt.getValue(i, "TYPE");
				monthDataArrayBuilder = Json.createArrayBuilder();
				for (int j = 0; j < monthList.size(); j++) {
					dataJsonBuilder = Json.createObjectBuilder();
					dataJsonBuilder.add(InsightConstant.NAME, monthList.get(j));
					dataJsonBuilder.add("y", Integer.parseInt(mdt.getValue(i, 1 + counter)));
					dataJsonBuilder.add("drilldown", name.trim().replaceAll(" ", "_") + "_" + monthList.get(j));
					monthDataArrayBuilder.add(dataJsonBuilder);
					counter++;
				}
				dataJson.add(InsightConstant.NAME, mdt.getValue(i, "TYPE"));
				dataJson.add(InsightConstant.DATA, monthDataArrayBuilder.build());
				recordArrayBuilder.add(dataJson.build());

			}
		} catch (Exception e) {
			rmd.exception("Exception : " + e.getMessage());
		}
		finalJson.add(InsightConstant.RECORD, recordArrayBuilder);
		return finalJson;
	}

	// Added by Kuldeep Singh 20181017

	public JsonObjectBuilder getLimitExceded(String publisher, int year, int month) {
		JsonObjectBuilder finalJson = Json.createObjectBuilder();
		JsonArrayBuilder headerJson = Json.createArrayBuilder();
		JsonArrayBuilder monthArrayBuilder = Json.createArrayBuilder();

		try {
			PublisherSettings publisherSettings = new PublisherSettings(rmd);
			if (year == 0) {
				year = publisherSettings.getLiveYear(rmd.getWebmartID());
			}
			if (month == 0) {
				month = publisherSettings.getPubLiveMonth(rmd.getWebmartID(), year);
			}
			for (int i = 1; i <= month; i++) {
				monthArrayBuilder.add(InsightConstant.MONTH_ARRAY_DASH[i]);
			}

			finalJson.add("header", headerJson.build());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return finalJson;

	}

	public JsonObjectBuilder getRequestSummary(String publisher, int year, int month) {
		JsonObjectBuilder finalJson = Json.createObjectBuilder();
		JsonArrayBuilder headerJson = Json.createArrayBuilder();
		JsonArrayBuilder recordDataMonthlyJson = null;
		JsonObjectBuilder recordHeaderJson = null;
		JsonObjectBuilder recordDataJson = null;
		JsonObjectBuilder drillDown = Json.createObjectBuilder();
		JsonArrayBuilder seriesDataJson = Json.createArrayBuilder();

		try {
			queryTemplate = new QueryTemplate(rmd);
			MyDataTable headerMdt = queryTemplate.getRequestSummeryHeader(publisher, month, year);

			/********************* Header Generation ******************/
			int rowCount = headerMdt.getRowCount();
			int colCount = headerMdt.getColumnCount();

			for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
				for (int colIndex = 1; colIndex <= 1; colIndex++) {
					headerJson.add(headerMdt.getValue(rowIndex, colIndex));
				}

			}

			finalJson.add("header", headerJson.build());

			/*********************** Record Generation ***************/
			for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
				recordHeaderJson = Json.createObjectBuilder();
				recordDataMonthlyJson = Json.createArrayBuilder();

				recordHeaderJson.add("name", headerMdt.getValue(rowIndex, 1));
				for (int colIndex = 2; colIndex <= colCount; colIndex++) {
					recordDataJson = Json.createObjectBuilder();
					recordDataJson.add("name", headerMdt.getColumnName(colIndex));
					recordDataJson.add("y", Integer.parseInt(headerMdt.getValue(rowIndex, colIndex)));
					recordDataMonthlyJson.add(recordDataJson);
				}
				recordHeaderJson.add("data", recordDataMonthlyJson);
				recordHeaderJson.add("type", "column");
				recordHeaderJson.add("colorByPoint", false);
				seriesDataJson.add(recordHeaderJson.build());
			}

			finalJson.add("record", seriesDataJson.build());
			drillDown.add("allowPointDrilldown", false);
			finalJson.add("drilldown", drillDown.build());

		} catch (Exception e) {
			rmd.exception("Exception : " + e.getMessage());
		}
		return finalJson;
	}

	public JsonObjectBuilder getDrillDownJsonFormatSiteSummary(MyDataTable mdt, List<String> monthList,
			List<Integer> drillDownMonthlist,List<Integer> drillDownYearlist) {
		JsonObjectBuilder finalJson = Json.createObjectBuilder();
		JsonObjectBuilder dataJson = null;
		JsonArrayBuilder monthArrayBuilder = null;
		JsonArrayBuilder recordArrayBuilder = Json.createArrayBuilder();
		JsonArrayBuilder monthDataArrayBuilder = null;
		try {
			int row = mdt.getRowCount();
			// int col=mdt.getColumnCount();
			DynamicDayMonthCreater dmc = new DynamicDayMonthCreater();
			int today = 0;
			for (int i = 1; i <= row; i++) {
				int count = 1;
				for (int j = 0; j < monthList.size(); j++) {
					dataJson = Json.createObjectBuilder();
					dataJson.add("id", mdt.getValue(i, 1).trim().replaceAll(" ", "_") + "_" + monthList.get(j));
					dataJson.add(InsightConstant.NAME, mdt.getValue(i, 1));
					dataJson.add("type", "spline");
					today = dmc.getNumberofDay(drillDownMonthlist.get(j), drillDownYearlist.get(j));
					monthDataArrayBuilder = Json.createArrayBuilder();

					for (int k = 1; k <= today; k++) {
						monthArrayBuilder = Json.createArrayBuilder();
						monthArrayBuilder.add(k + "-" + monthList.get(j));
						monthArrayBuilder.add(Integer.parseInt(mdt.getValue(i, k + "-" +drillDownMonthlist.get(j)+"-"+ drillDownYearlist.get(j))));
						monthDataArrayBuilder.add(monthArrayBuilder);
					}
					dataJson.add(InsightConstant.DATA, monthDataArrayBuilder.build());
					recordArrayBuilder.add(dataJson);
				}
			}
		} catch (Exception e) {
			rmd.exception("Exception : " + e.getMessage());
		}
		finalJson.add("allowPointDrilldown", false);
		finalJson.add("series", recordArrayBuilder);
		return finalJson;
	}

	public String getSiteSummaryC5(MyDataTable mdt, int year, int month, int day) {
		JsonObjectBuilder finalJson = Json.createObjectBuilder();
		JsonArrayBuilder recordArrayBuilder = Json.createArrayBuilder();
		JsonObjectBuilder datajson = null;
		JsonArrayBuilder datajsonarray = null;
		JsonArrayBuilder datajsonarraydetail = null;
		Utils util = new Utils();
		try {
			int rowcount = mdt.getRowCount();
			int colcount = mdt.getColumnCount();
			for (int i = 1; i <= rowcount; i++) {
				datajson = Json.createObjectBuilder();
				datajsonarray = Json.createArrayBuilder();
				datajson.add(InsightConstant.NAME, mdt.getValue(i, 1));
				for (int j = 2; j <= colcount; j++) {
					datajsonarraydetail = Json.createArrayBuilder();
					long temp = util.DateStringToUnixTimeStamp(mdt.getColumnName(j));
					datajsonarraydetail.add(temp);
					try {
						datajsonarraydetail.add(Integer.parseInt(mdt.getValue(i, j)));
					} catch (Exception ex) {
						datajsonarraydetail.add(0);
					}
					datajsonarray.add(datajsonarraydetail);
				}
				datajson.add(InsightConstant.DATA, datajsonarray);
				recordArrayBuilder.add(datajson);
			}

		} catch (Exception e) {
			rmd.log("Exception " + e.getMessage());
		}

		finalJson.add("series", recordArrayBuilder);
		return finalJson.build().toString();
	}

	public JsonObjectBuilder consolidateDatabaseJsonFormate(MyDataTable mdt,List<String> monthList) {
		JsonObjectBuilder finalJson = Json.createObjectBuilder();
		JsonArrayBuilder headerJson = Json.createArrayBuilder();
		JsonArrayBuilder recordDataMonthlyJson = null;
		JsonObjectBuilder recordHeaderJson = null;
		JsonObjectBuilder recordDataJson = null;
		JsonObjectBuilder drillDown = Json.createObjectBuilder();
		JsonArrayBuilder seriesDataJson = Json.createArrayBuilder();
		JsonArrayBuilder monthArrayBuilder = Json.createArrayBuilder();
		try {
			/********************* Header Generation ******************/
			int rowCount = mdt.getRowCount();
			int colCount = mdt.getColumnCount();
			monthList.forEach(month->monthArrayBuilder.add(month));
			finalJson.add("header", monthArrayBuilder.build());
			/*********************** Record Generation ***************/
			for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
				recordHeaderJson = Json.createObjectBuilder();
				recordDataMonthlyJson = Json.createArrayBuilder();

				recordHeaderJson.add("name", mdt.getValue(rowIndex, 1));
				for (int colIndex = 2; colIndex <= colCount; colIndex++) {
					recordDataJson = Json.createObjectBuilder();
					recordDataJson.add("name", mdt.getColumnName(colIndex));
					recordDataJson.add("y", Integer.parseInt(mdt.getValue(rowIndex, colIndex)));
					recordDataMonthlyJson.add(recordDataJson);
				}
				recordHeaderJson.add("data", recordDataMonthlyJson);
				recordHeaderJson.add("type", "column");
				recordHeaderJson.add("colorByPoint", false);
				seriesDataJson.add(recordHeaderJson.build());
			}

			finalJson.add("record", seriesDataJson.build());
			drillDown.add("allowPointDrilldown", false);
			finalJson.add("drilldown", drillDown.build());

		} catch (Exception e) {
			rmd.exception("Exception : " + e.getMessage());
		}
		return finalJson;
	}
	public MyDataTable consolidateDatabaseQuery(String publisher, String month) {
		InsightDAO insightDao = null;
		StringBuilder query = new StringBuilder();
		MyDataTable mdt = null;
		try {
			insightDao = InsightDAO.getInstance(publisher);
			query.append("SELECT title_id,"+month);
			query.append("FROM `dr_master` WHERE metric_type='Searches_Regular' GROUP BY title_id;");
			mdt = insightDao.executeSelectQueryMDT(query.toString());
			if (mdt.getRowCount() < 1) {
				rmd.exception("No Data: Query : " + query.toString());
			}
		} catch (Exception e) {
			rmd.exception(" Exception : : " + e.getMessage()+": Query : " + query.toString());
		}
		return mdt;
	}
}
