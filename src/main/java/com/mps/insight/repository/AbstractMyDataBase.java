/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mps.insight.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author Kapil Singh Verma
 */
public abstract class AbstractMyDataBase {

	public static final String BYTE = "BYTE";
	public static final String KB = "KB";
	public static final String MB = "MB";
	public static final String GB = "GB";

	// method to get Connection from app server datasource
	protected Connection getConnection() throws Exception {
		return getConnection("context.connection");
	}

	// parameterised costructor - parametrs will be passed by user class
	protected Connection getConnection(String driver, String url, String user, String password) throws Exception {
		Connection sqlConnection = null;
		try {
			// check for null;
			if (null == driver) {
				throw new SQLException("NULL driver");
			} else {
				driver = driver.trim();
			}
			if (null == url) {
				throw new SQLException("NULL url");
			} else {
				url = url.trim();
			}
			if (null == user) {
				throw new SQLException("NULL user");
			} else {
				user = user.trim();
			}
			if (null == password) {
				throw new SQLException("NULL password");
			} else {
			}

			// validating DB_Driver
			if (driver.equalsIgnoreCase(""))
				throw new Exception("BLANK driver : " + driver);
			// validating DB_URL
			if (url.equalsIgnoreCase(""))
				throw new Exception("BLANK url : " + url);
			// validating DB_USER
			if (user.equalsIgnoreCase(""))
				throw new Exception("BLANK user : " + user);
			// validating Password

			Class.forName(driver.trim());
			DriverManager.setLoginTimeout(30); // sec
			sqlConnection = DriverManager.getConnection(url.trim(), user.trim(), password);

			return sqlConnection;
		} catch (Exception exp) {
			throw new Exception(
					"getConnection(String driver, String url, String user ,String password) : " + exp.toString());
		}
	}

	// parameterized constructor as datasource as parameter
	protected Connection getConnection(String datasource) throws Exception {
		InitialContext initialContext = null;
		Context envContext = null;
		DataSource dataSource = null;
		Connection sqlConnection = null;
		try {
			
			initialContext = new InitialContext();
			envContext = (Context) initialContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup(datasource);
			try {
				
				return dataSource.getConnection();
				
				/*
				sqlConnection = dataSource.getConnection();
				
				// check for null Connection
				if (sqlConnection == null) {
					throw new SQLException("NULL Connection");
				}
				// check for closed Connection
				if (sqlConnection.isClosed()) {
					throw new SQLException("CLOSED Connection");
				}
				return sqlConnection;
				*/
			} catch (SQLException ex) {
				throw ex;
			}
			
		} catch (Exception e) {
			throw new Exception("getConnection(String datasource) : " + e.toString());
		} finally {
			System.gc();
		}
	}

	// method to validate Connection
	public boolean validateConnection(Connection sqlConnection) throws Exception {

		try {
			if (sqlConnection != null) {
				if (sqlConnection.isClosed()) { // sec * min = total seconds
					throw new Exception("validateConnection : CLOSED Connection");
				}
				if (!sqlConnection.isValid(60 * 2)) { // sec * min = total
														// seconds
					throw new Exception("validateConnection : INVALID Connection");
				}
			} else {
				throw new Exception("validateConnection : NULL Connection");
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	// method to execute Data Defination Language Query and return status of
	// same
	public boolean executeDDLQuery(Connection sqlConnection, String ddlQuery) throws Exception {
		Statement stmt = null;
		try {
			stmt = sqlConnection.createStatement();
			return stmt.execute(ddlQuery);
		} catch (Exception exp) {
			throw new Exception("executeDDLQuery : " + exp.toString() + " : " + ddlQuery);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			stmt = null;
		}
	}

	// method to execute select query and return result set for same
	public ResultSet executeSelectQuery(Connection sqlConnection, String selectQuery) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// query execution
			try {
				stmt = sqlConnection.createStatement();
			} catch (SQLException ex) {
				throw ex;
			}
			rs = stmt.executeQuery(selectQuery);
			return rs;
		} catch (Exception exp) {
			if(exp.toString().length() > 200){
        		throw new Exception("executeSelectQuery : substring to 200 char" + exp.toString().substring(0,199) + " : " + selectQuery);
        	}else{
        		throw new Exception("executeSelectQuery : " + exp.toString() + " : " + selectQuery);	
        	}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	// method to execute insert or update query
	public long executeInsertUpdateQuery(Connection sqlConnection, String updateQuery) throws Exception {

		Statement stmt = null;
		long recordseffected = 0;
		try {
			// creating statement
			try {
				stmt = sqlConnection.createStatement();
			} catch (SQLException ex) {
				throw ex;
			}
			// recordseffected = stmt.executeUpdate(sQuery);

			recordseffected = stmt.executeUpdate(updateQuery, Statement.RETURN_GENERATED_KEYS);

			return recordseffected;
		} catch (Exception exp) {
			if(exp.toString().length() > 200){
        		throw new Exception("executeInsertUpdateQuery : substring to 200 char" + exp.toString().substring(0,199) + " : " + updateQuery);
        	}else{
        		throw new Exception("executeInsertUpdateQuery : " + exp.toString() + " : " + updateQuery);	
        	}
		} finally {

			if (null != stmt) {
				stmt.close();
			}
			stmt = null;
		}
	}

	// method to get Row Count from result set
	public long getRowCount(ResultSet rs) throws Exception {

		long iRowCount = -2;
		try {
			// check for null or valid values
			if (rs != null && rs.next()) {
				rs.last();
				iRowCount = rs.getRow();
			} else {
				throw new Exception("NULL/INVALID result set");
			}
			return iRowCount;
		} catch (Exception e) {
			throw new Exception("getRowCount: " + e.getMessage());
		} finally {
			try {
				rs.beforeFirst();
			} catch (Exception e) {
			}
		}
	}

	// method to get Column Count from result set
	public int getColumnCount(ResultSet rs) throws Exception {

		int iColumnCount = -2;
		ResultSetMetaData rsMetaData;
		try {
			// check for null or valid values
			if (rs != null) {
				rsMetaData = rs.getMetaData();
				iColumnCount = rsMetaData.getColumnCount();
			} else {
				throw new Exception("NULL/INVALID result set");
			}
			return iColumnCount;
		} catch (Exception e) {
			throw new Exception("getColumnCount: " + e.toString().replaceAll("java.lang.Exception", ""));
		}
	}

	// method to get Database Size
	public double getDatabaseSize(Connection sqlConnection, String database, String unit) throws Exception {
		double size = 0.0;
		ResultSet rs = null;
		String query = null;
		String divisor = "1";
		try {
			if (unit.trim().toUpperCase().equalsIgnoreCase("BYTE")) {
				divisor = "1";
			} else if (unit.trim().toUpperCase().equalsIgnoreCase("KB")) {
				divisor = "1024";
			} else if (unit.trim().toUpperCase().equalsIgnoreCase("MB")) {
				divisor = "1024*1024";
			} else if (unit.trim().toUpperCase().equalsIgnoreCase("GB")) {
				divisor = "1024*1024*1024";
			} else {
				divisor = "1";
			}

			query = "SELECT table_schema, ROUND(SUM(data_length + index_length) / (" + divisor
					+ "), 2) AS size FROM information_schema.TABLES" + " where table_schema = '" + database
					+ "' GROUP BY table_schema";
			// check for null or valid values
			rs = executeSelectQuery(sqlConnection, query);
			while (rs.next()) {
				size = size + rs.getDouble("size");
			}
			return size;
		} catch (Exception e) {
			throw new Exception("getDatabaseSizeMB: " + e.toString().replaceAll("java.lang.Exception", ""));
		}
	}

	// method to get list of tables in a specific database
	public ArrayList<String> getTables(Connection sqlConnection, String database) throws Exception {

		ArrayList<String> tables = new ArrayList<String>();
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		String[] types = { "TABLE" };
		try {
			if (database == null) {
				throw new Exception("NULL datbase");
			}
			if (database.trim().equalsIgnoreCase("")) {
				throw new Exception("BLANK datbase");
			}

			dbmd = sqlConnection.getMetaData();
			rs = dbmd.getTables(database, null, "%", types);
			while (rs.next()) {
				tables.add(rs.getString("TABLE_NAME").trim().toUpperCase());
			}
			Collections.sort(tables);
			return tables;
		} catch (Exception e) {
			throw new Exception("getTables : " + e.toString().replaceAll("java.lang.Exception", ""));
		} finally {
			if (null != rs) {
				rs.close();
			}
		}

	}

}
