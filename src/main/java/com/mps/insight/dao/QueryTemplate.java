package com.mps.insight.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.c4.report.DynamicDayMonthCreater;
import com.mps.insight.c4.report.DynamicMonthCreater;
import com.mps.insight.dto.Counter5DTO;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.MyLogger;
import com.mps.insight.global.TableMapper;
import com.mps.insight.product.PublisherSettings;

public class QueryTemplate {
	RequestMetaData rmd = null;
	private static final Logger log = LoggerFactory.getLogger(QueryTemplate.class);
	DynamicDayMonthCreater dmc = new DynamicDayMonthCreater();
	PublisherSettings pubsetting = null;

	public QueryTemplate(RequestMetaData rmd) {
		this.rmd = rmd;
	}

	public MyDataTable getMDTofQueryWithWebmartID(int webmartID, String dynamicQuery) {
		MyDataTable mdt = null;
		String publisher_name;
		try {
			pubsetting = new PublisherSettings(rmd);
			publisher_name = pubsetting.getPublisherCode(webmartID);
			mdt = getMDTofQueryWithPublisher(publisher_name, dynamicQuery);

		} catch (Exception e) {
			log.info("Exception in getMDTofQueryWithWebmartID : " + e.getMessage());
		}
		return mdt;
	}

	public MyDataTable getMDTofQueryWithPublisher(String publisher, String dynamicQuery) {
		InsightDAO insightDao = null;
		MyDataTable mdt = null;
		try {
			insightDao = InsightDAO.getInstance(publisher);
			if (null != dynamicQuery && dynamicQuery.length() > 4) {
				mdt = insightDao.executeSelectQueryMDT(dynamicQuery);
			}
		} catch (Exception e) {
			log.error("Exception in getMDTofQueryWithPublisher : query : " + dynamicQuery + " : " + e.getMessage());

		}
		return mdt;
	}

	public MyDataTable getRequestSummeryHeader(String publisher, int month, int year) {
		InsightDAO insightDao = null;
		DynamicMonthCreater dmonth = new DynamicMonthCreater();
		StringBuilder query = new StringBuilder();
		MyDataTable mdt = null;
		String dmonth1 = dmonth.createMonthQueryC5(month, year, 01, 2019);
		String dmonth2 = dmonth1.substring(0, dmonth1.lastIndexOf(","));

		try {
			insightDao = InsightDAO.getInstance(publisher);
			log.info("Dynamic DB Detail : " + insightDao.getSqlConnection().getMetaData().getDatabaseProductName());
			query.append("").append("SELECT `Data_Type`,").append(dmonth2)
					.append(" FROM `" + TableMapper.TABALE.get("master_report_table") + "` ")
					.append("WHERE Metric_Type=\"Total_Item_Requests\" AND `Institution_Type`=\"Institution\" GROUP BY `Data_Type` ")
					.append(" UNION ALL ").append("SELECT `Data_Type`,").append(dmonth2)
					.append(" FROM `" + TableMapper.TABALE.get("dr_master_table") + "` ")
					.append("WHERE Metric_Type=\"Searches_Regular\" AND `Institution_Type`=\"Institution\" GROUP BY `Data_Type` ")
					.append("UNION ALL ").append("SELECT `Data_Type`,").append(dmonth2)
					.append(" FROM `" + TableMapper.TABALE.get("pr_master") + "`")
					.append(" WHERE Metric_Type=\"Searches_Platform\" AND `Institution_Type`=\"Institution\" GROUP BY `Data_Type`");
			log.info("getDynamicReportJson : Query =" + query);
			mdt = insightDao.executeSelectQueryMDT(query.toString());

			if (mdt.getRowCount() < 1) {
				rmd.exception("Data not Found in Database : Query : " + query.toString() + " : Live Month : " + month
						+ " : Live year : " + year);
			}

		} catch (Exception e) {
			log.info("Exception in getMDTofQueryWithPublisher : " + e.getMessage());
		}
		return mdt;
	}

	public StringBuilder getIRData(Counter5DTO dto, String query) {
		StringBuilder sb = new StringBuilder();
		ResultSet rs = null;
		try {
			InsightDAO insightDao = InsightDAO.getInstance(rmd.getWebmartCode());
			log.info("Dynamic DB Detail : " + insightDao.getSqlConnection().getMetaData().getDatabaseProductName());
			log.info("getDynamicReportJson : Query =" + query);
			rs = insightDao.executeSelectQueryRS(query);
			ResultSetMetaData rsmetadata = rs.getMetaData();
			int colCount = rsmetadata.getColumnCount();
			String[] header = new String[colCount];
			for (int i = 1; i <= colCount; i++) {
				header[i - 1] = rsmetadata.getColumnName(i);
			}
			boolean autoflush = true;
			String file = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath())
					.getParent();
			PrintWriter out = new PrintWriter(
					new FileWriter(file + "" + File.separator + "ir_" + dto.getInstitutionID() + ".csv"), autoflush);
			while (rs.next()) {
				for (String string : header) {
					String value = String.valueOf(rs.getObject(string));
					out.append("\"" + value + "\",");
				}
				out.append("\n");

			}
			sb.append(file + "" + File.separator + "ir_" + dto.getInstitutionID() + ".csv");
			rs.close();
			out.close();

		} catch (Exception e) {
			try {
				rs.close();
			} catch (Exception e1) {
			}
			MyLogger.error(e.toString());
		}
		return sb;
	}
}
