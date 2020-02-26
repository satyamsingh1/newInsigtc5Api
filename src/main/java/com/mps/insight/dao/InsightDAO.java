/*####################################################
Creator: kapil Singh Verma
File: MyDataBase.java
Date: 29-June-2017
29-June-2017 : Update: 1.0   || (initial draft)  ## please add comments below for future updates with date tims stamp, summary and update version ##
29-June-2017 : Update: 1.1   || (summary) 

####################################################*/

package com.mps.insight.dao;

import java.io.FileNotFoundException;
import java.io.InputStream;
//SQL libraries import
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;


public class InsightDAO extends AbstractMyDataBase{

    //constant
    private long DEFAULT_RECONNECT_DURATION = 10 * 60; //min * sec = sec
    //private static final Logger LOG = LoggerFactory.getLogger(InsightDAO.class);
    
    //constructor variables
    
    
    
	private String databaseDriver;
    private String databaseUrl;
    private String databaseUser; 
    private String databasePassword;
    
    //constructor variables
    private Connection sqlConnection = null;

    //variables
    private long onlineTimeStamp = -2;//min * sec * milisec
    private long transactionStartTime = 0; //in seconds
    private long transactionEndTime = 0;  // in seconds
    
    /* pre-initialized instance of the singleton */
    private static InsightDAO INSTANCE = null;
	       
    //##############
	//disabling default constructor
    
    //parameterized constructor as datasource as parameter
    /* Private constructor     */
   
    
    //public static InsightDAO getInstance() throws Exception{
    	
    	/*
    	synchronized(InsightDAO.class){
    		//check for null and closed connection
	        if (INSTANCE == null || INSTANCE.getSqlConnection().isClosed()){
				INSTANCE = new InsightDAO("datasource");
	        }
    	}
        return INSTANCE ;*/
        
        //return new InsightDAO("datasource_beta");
     //}
    
    public static InsightDAO getInstance(String publisher) throws Exception{
    	
    	/*
    	synchronized(InsightDAO.class){
    		//check for null and closed connection
	        if (INSTANCE == null || INSTANCE.getSqlConnection().isClosed()){
				INSTANCE = new InsightDAO("datasource");
	        }
    	}
        return INSTANCE ;*/
        
        return  getDynamicInstance(publisher);
     }
    
    
    public static InsightDAO getDynamicInstance(String publisher) throws Exception{
    	
    	Properties prop = new Properties();
		InputStream inputStream = InsightDAO.class.getClassLoader().getResourceAsStream("application.properties");

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file 'application.properties' not found in the classpath");
		}
		
		// get the property value and print it out
		String userName = prop.getProperty("spring.datasource.username");
		String password = prop.getProperty("spring.datasource.password");
		String driver = prop.getProperty("spring.datasource.driver");
		//String driver ="com.mysql.cj.jdbc.Driver";
		//String database = prop.getProperty("dynamic.database");

		String database = publisher;
		String url = prop.getProperty("spring.datasource.url");
        url = url+"/"+database+"?useSSL=false";
        
        return new InsightDAO(driver, url, userName, password);
     }
    
    //parameterized constructor
    public InsightDAO(Connection sqlConnection) throws Exception{
        this.databaseDriver = "";
        this.databaseUrl = "";
        this.databaseUser = "";
        //this.databasePassword = "";
        
        try {
    		if(sqlConnection == null) {
        		throw new Exception("NULL Connection");
        	}else if(sqlConnection.isClosed()) {
        		throw new Exception("Closed Connection");
        	}else {
        		this.sqlConnection = sqlConnection;
        	}
    	}catch(Exception e) {
    		throw e;
    	}
    }
    
	//parameterised costructor  - parametrs will be passed by user class
	public InsightDAO(String driver, String url, String user ,String password) throws Exception {
        
		try {
			
			//Null Check
    		if(null==driver) {
    			throw new Exception("NULL Database Driver");
    			}
    		if(null==url) {
    			throw new Exception("NULL Database Url");
    			}
    		if(null==user) {
    			throw new Exception("NULL Database User");
    			}
    		if(null==password) {
    			throw new Exception("NULL Database Password");
    			}
    		
    		//initializing/setting parameters
    		this.databaseDriver = driver.trim();
    		this.databaseUrl = url.trim();
    		this.databaseUser = user.trim();
    		this.databasePassword = password.trim();
    		
    		//Blank Check
    		if("".equalsIgnoreCase(this.databaseDriver)) {
    			throw new Exception("BLANK Database Driver");
    			}
    		if("".equalsIgnoreCase(this.databaseUrl)) {
    			throw new Exception("BLANK Database Url");
    			}
    		if("".equalsIgnoreCase(this.databaseUser)) {
    			throw new Exception("BLANK Database User");
    			}
    		if("".equalsIgnoreCase(this.databasePassword)) {
    			throw new Exception("BLANK Database Password");
    			}
    		
    		connect();
		}catch(Exception e) {
			throw e;
		}
	}
	
    //method to get active sqlConnection
    public Connection getSqlConnection(){		
        return sqlConnection;
	}
	
    //method to disconnect from database
    public void disconnect() {		
        try{
        	if(null!=sqlConnection){
            sqlConnection.close();
        	}
        }catch (Exception exp){
        }finally{
            sqlConnection = null;
            System.gc();
        }
	}
	
    //method to refresh the connection object for memory free
    private void connect() throws Exception{
        onlineTimeStamp = Calendar.getInstance().getTimeInMillis()/1000;
        if(!databaseDriver.equalsIgnoreCase("") &&  !databaseUrl.equalsIgnoreCase("") && !databaseUser.equalsIgnoreCase("") && !databasePassword.equalsIgnoreCase("") ){
            disconnect();
            sqlConnection = getConnection(databaseDriver, databaseUrl, databaseUser, databasePassword);
        }else if(sqlConnection != null && !sqlConnection.isClosed()){
            //class object is made by passing connection object itself
            //can't refresh the object
        }else{
            throw new Exception("connect() : Logic fail for database connection" 
                    + " : databaseDriver=" + databaseDriver 
                    + " : databaseUrl=" + databaseUrl 
                    + " : databaseUser=" + databaseUser 
                    + " : sqlConnection=" + sqlConnection);
        }
        
    }
    
    //method to check DB online time
    private void refresh(){
        transactionStartTime = Calendar.getInstance().getTimeInMillis()/1000;
        try{
            //check for database online duration
            if(onlineTimeStamp > 0 && (Calendar.getInstance().getTimeInMillis()/1000 - onlineTimeStamp) >= DEFAULT_RECONNECT_DURATION){
                connect();
            }
        }catch(Exception e){}
        finally{
        	System.gc();
        	}
    }

    //methods to get last executed query time in seconds
    public long getTat(){
        return (transactionEndTime - transactionStartTime);
    }
    
    //method to check table if not exists make copy of template table
    public boolean checkAndCreateTable(final String tableToCheck , final String templateTable) throws Exception{
        boolean bRetVal = false;
        Statement smt = null;
        ResultSet rs = null;
        transactionStartTime = Calendar.getInstance().getTimeInMillis()/1000;
        try {
            //refreshing resources for freeing memory & objects
            refresh();
            
            String sCheckQuery = "select distinct TABLE_NAME from information_schema.TABLES where "
                    + " TABLE_SCHEMA = (select database()) and "
                    + " TABLE_NAME='" + tableToCheck.trim() + "'";
            try{
            smt = sqlConnection.createStatement();
            }catch(Exception e1){
            	
            }
            rs = smt.executeQuery(sCheckQuery);
            
            if(rs != null){
                if(getRowCount(rs) <= 0){
                    String sQuery = "CREATE TABLE '" + tableToCheck.trim() + "' LIKE '" + templateTable.trim() + "'";
                    bRetVal = executeDDLQuery(sqlConnection, sQuery);
                }
            }else{
                bRetVal = true;
            }
            return bRetVal;
        } catch (Exception e) {
            throw e;
        }finally{
        	if(smt!=null){
            	smt.close();
            }
            transactionEndTime = Calendar.getInstance().getTimeInMillis()/1000;
            
        }
        
    }
  
    
    //############### implementing Abstract methods via Overload
    
    //method to validate for NULL or closed Connection
    public boolean validateConnection() throws Exception{
        return validateConnection(sqlConnection);
    }
    
    //method to get Database Size
    public double getDatabaseSize()throws Exception{
        refresh();
        return getDatabaseSize(sqlConnection, sqlConnection.getCatalog(), "MB");
    }
    
    //method to get Database Size
    public double getDatabaseSize(String database)throws Exception{
        refresh();
        return getDatabaseSize(sqlConnection, database, "MB");
    }
    
    //method to get Database Size
    public double getDatabaseSize(String database, String unit) throws Exception{
        refresh();
        return getDatabaseSize(sqlConnection, database, unit);
    }
    
    //method to execute select query on current database
    private ResultSet executeSelectQuery(String selectQuery) throws Exception{
        refresh();
        return executeSelectQuery(sqlConnection, selectQuery);
    }
    
    //method to execute select query in MyDataTable
    public MyDataTable executeSelectQueryMDT(String selectQuery) throws Exception{
        Statement stmt = null;
        ResultSet rs = null;
        MyDataTable mdt = null;
        try{
            //query execution
        	try{
        		
        		stmt = sqlConnection.createStatement();
        		rs = stmt.executeQuery(selectQuery);
            	
        	}catch(SQLException ex){
        		throw ex;
        	}
            mdt = new MyDataTable(rs);
            return mdt;
        }catch (Exception exp) {
        	if(exp.toString().length() > 200){
        		throw new Exception("executeSelectQueryMDT : substring to 200 char" + exp.toString().substring(0,199) + " : " + selectQuery);
        	}else{
        		throw new Exception("executeSelectQueryMDT : " + exp.toString() + " : " + selectQuery);	
        	}
        }finally{
        	if(stmt != null){
        		stmt.close();
        		}
        	if(rs != null){
            	rs.close();
            }
        }	
    }
    
    
    public ResultSet executeSelectQueryRS(String selectQuery) throws Exception{
        Statement stmt = null;
        ResultSet rs = null;
        try{
            //query execution
        	try{
        		
        		stmt = sqlConnection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
	                    java.sql.ResultSet.CONCUR_READ_ONLY);
        		stmt.setFetchSize(Integer.MIN_VALUE);
        		rs = stmt.executeQuery(selectQuery);
            	
        	}catch(SQLException ex){
        		throw ex;
        	}
            return rs;
        }catch (Exception exp) {
        	if(exp.toString().length() > 200){
        		throw new Exception("executeSelectQueryMDT : substring to 200 char" + exp.toString().substring(0,199) + " : " + selectQuery);
        	}else{
        		throw new Exception("executeSelectQueryMDT : " + exp.toString() + " : " + selectQuery);	
        	}
        }	
    }
    
    private int getRowCount(String selectQuery) throws SQLException {
    	int rowCount = 0;
    	Statement stmt = null;
    	ResultSet rs =null;
		try {
			stmt = sqlConnection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);

        	rs = stmt.executeQuery("Select count(*) AS rowcount from ("+selectQuery+") z");
        	rs.next();
        	rowCount = rs.getInt(1);
		} finally {
        	if(stmt != null){
        		stmt.close();
        		}
        	if(rs != null){
            	rs.close();
            }
        }
		return rowCount;
	}

	//method to execute inert or update query on current database
    public long executeInsertUpdateQuery(String updateQuery) throws Exception{
        refresh();
        return executeInsertUpdateQuery(sqlConnection, updateQuery);
    }
    
    //method to execute DDL query on current database
    public boolean executeDDLQuery(String ddlQuery) throws Exception {
        refresh();
        return executeDDLQuery(sqlConnection, ddlQuery);
    }
    
    //method to get list of tables in a current database
    public ArrayList<String> getTables() throws Exception{
        refresh();
        return getTables(sqlConnection, sqlConnection.getCatalog());
    }
    
    //method to get list of tables in a specific database
    public ArrayList<String> getTables(String database) throws Exception{
        refresh();
        return getTables(sqlConnection, database);
    }
    
    //############### end of abstract methods made public
    

    
}//end class
