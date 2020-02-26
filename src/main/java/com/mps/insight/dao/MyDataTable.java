package com.mps.insight.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

//
public class MyDataTable {

    private String tableName = "";
    private String schemaName = "";
    private String databaseName = "";
    
    private HashMap<Integer, HashMap<Integer, String>> rowsDataMap = new HashMap<Integer, HashMap<Integer, String>>();
    
    private HashMap<String, Column> columnNameMap = new HashMap<String, Column>();
    private HashMap<Integer, Column> columnIndexMap = new HashMap<Integer, Column>();
    
    private HashMap<String, Integer> rowKeyIndexMap = new HashMap<String, Integer>();
    private HashMap<Integer, String> rowIndexKeyMap = new HashMap<Integer, String>();
    
    private int startTime = 0; // in sec
    private int endTime = 0;  // in sec
    private int tat = 0;  //in sec
    
    //############################################################################
    //####################### CONSTRUCTORS & CLASSS initializers #################
    //constructor
    public MyDataTable(ResultSet resultset) throws Exception{
        start();
        try{
            if(resultset == null){
                throw new Exception("NULL resultSet");
            }
            
            //initializing class
            initialize();

            //filling result set columns data into data structure
            fillColumnData(resultset);

            //filling result set rows data into data structure
            fillRowData(resultset);
            
        }catch (Exception e) {
            throw new Exception("MyDataTable : " + e.toString().replaceAll("java.lang.Exception", ""));
        }finally{
            end();
        }
    }
    
    //constructor to generate virtual memory table
    public MyDataTable(String tableName){
        //initializing class
        initialize();
        this.tableName = tableName;
    }
    
    //method to initialize class
    private void initialize(){
        destroy();
        
        tableName = "";
        schemaName = "";
        databaseName = "";

        startTime = 0; // in sec
        endTime = 0;  // in sec
        tat = 0;
        
        rowsDataMap = new HashMap();
        
        columnNameMap = new HashMap();
        columnIndexMap = new HashMap();
        
        rowKeyIndexMap = new HashMap();
        rowIndexKeyMap = new HashMap();
    }
    
    //distroy object
    public void destroy(){
        try{
            tableName = null;
            schemaName = null;
            databaseName = null;

            startTime = 0; // in sec
            endTime = 0;  // in sec
            tat = 0;

            rowsDataMap = null;

            columnNameMap = null;
            columnIndexMap = null;

            rowKeyIndexMap = null;
            rowIndexKeyMap = null;
        }catch(Exception e){
        }finally{
            System.gc();
        }
    }
    //############################################################################
    //########################## private internal use methods used to initialize MyDataTable class , store data in memory.

    //Method fills column data into data structure
    private void fillColumnData(ResultSet rs) throws Exception {
        Column column = null;
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int columnCount = 0;
        try{
            columnCount = rsMetaData.getColumnCount();

            for(int i = 1 ; i <= columnCount ; i++){
                column = new Column();
                column.header = rsMetaData.getColumnName(i).toUpperCase();	//Column name/header
                column.name = rsMetaData.getColumnLabel(i).toUpperCase(); // Column Label //as, alias
                
                column.absoluteHeader = rsMetaData.getColumnName(i); //Column name/header
                column.absoluteName = rsMetaData.getColumnLabel(i); // Column Label //as, alias
                
                column.index = i; //Column index
                column.type = rsMetaData.getColumnTypeName(i).toUpperCase();  //Column type
                column.length = rsMetaData.getPrecision(i);  //Column length
                column.autoIncremented = rsMetaData.isAutoIncrement(i);
                column.caseSensitive = false;
                column.nullable = true;
                column.readOnly = false;
                column.schemaName = rsMetaData.getSchemaName(i);
                column.signed = false;
                column.tableName = rsMetaData.getTableName(i);

                columnIndexMap.put(i, column);
                columnNameMap.put(column.name.trim().toUpperCase(), column);
            }

            tableName = column.tableName;
            schemaName = column.schemaName;
        }
        catch (Exception e) {
            throw new Exception("DataTable : FillColumnData : " + e.toString());
        }
    }

    //Method fills rows data into data structure
    private void fillRowData(ResultSet rs) throws Exception {
        int rowNo = 0;
        int colNo = 0;
        double iTracker = 0.0;
        int columnCount = 0;
        try{            
            //setting result set at very first position to start iteration
            rs.beforeFirst();
            columnCount = getColumnCount();
            //row count loop
            while (rs.next()){
                iTracker = 0.0;
                
                rowNo += 1;//setting row index
                HashMap<Integer , String> rowData = new HashMap<Integer , String>();
                
                iTracker = 1.0;
                for(colNo = 1 ; colNo <= columnCount; colNo++){
                    
                    iTracker = 2.0;
                    if(null==rs.getObject(colNo)){
                        iTracker = 3.0;
                        rowData.put(colNo,null);
                    }else{
                        iTracker = 4.0;
                        rowData.put(colNo, rs.getObject(colNo).toString());
                    }
                }
                iTracker = 5.0;
                rowsDataMap.put(rowNo, rowData);
            }
            iTracker = 6.0;
        }
        catch (Exception e) {
            throw new Exception("MyDataTable : fillRowData : iTracker : " + iTracker +  "rowNo : " + rowNo + " : colNo : " + colNo + " : " + e.toString());
        }
        finally{
            try{
            	rs.beforeFirst();
        	}catch(Exception e1){}
        }
    }
    
    

//############################################################################
//################### Operation METHODS ########################################
    
    //method to add COLUMN to virtual memory table
    public boolean addColumn(String columnName, String defaultValue) throws Exception{
        
        try {
            Column column = null;
            
            if(columnName == null){
                throw new Exception("NULL columnName");
            }
            if(columnName.trim().equalsIgnoreCase("")){
                throw new Exception("BLANK columnName");
            }
            if(defaultValue == null){
                defaultValue = "";
            }
            //
            columnName = columnName.trim().toUpperCase();
            
            try{
                //getting column index for column to add , check for if column already exists
                column = getColumn(columnName);
                //column.defaultValue = defaultValue;
                //columnNameMap.put(columnName, column);
               //columnIndexMap.put(getColumnIndex(columnName), column);
            } catch(Exception e){
                int columnIndex = getColumnCount() + 1;
                column = new Column();
                column.name = columnName;
                column.header = columnName;
                column.defaultValue = defaultValue;
                column.index = columnIndex;
                
                //putting entry in column maps
                columnIndexMap.put((columnIndex), column);
                columnNameMap.put(columnName.trim().toUpperCase(), column);
            }
            return true;
        } catch (Exception e) {
            throw new Exception("addColumn : " + e.toString());
        }
    }
    //method to add COLUMN to virtual memory table
    public boolean addColumn(String columnName, String defaultValue, String egnoreCase) throws Exception{
        
        try {
            Column column = null;
            
            if(columnName == null){
                throw new Exception("NULL columnName");
            }
            if(columnName.trim().equalsIgnoreCase("")){
                throw new Exception("BLANK columnName");
            }
            if(defaultValue == null){
                defaultValue = "";
            }
            //
            columnName = columnName.trim();
            
            try{
                //getting column index for column to add , check for if column already exists
                column = getColumn(columnName);
                //column.defaultValue = defaultValue;
                //columnNameMap.put(columnName, column);
               //columnIndexMap.put(getColumnIndex(columnName), column);
            } catch(Exception e){
                int columnIndex = getColumnCount() + 1;
                column = new Column();
                column.name = columnName;
                column.header = columnName;
                column.defaultValue = defaultValue;
                column.index = columnIndex;
                
                //putting entry in column maps
                columnIndexMap.put((columnIndex), column);
                columnNameMap.put(columnName.trim(), column);
            }
            return true;
        } catch (Exception e) {
            throw new Exception("addColumn : " + e.toString());
        }
    }
    //method to remove COLUMN from virtual memory table
    public boolean removeColumns(){
        try {
            columnIndexMap = new HashMap();
            columnNameMap = new HashMap();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    //method to remove COLUMN from virtual memory table
    public boolean removeColumn(String columnName){
        int columnIndex = 0;
        try {
            columnIndex = getColumnIndex(columnName);
            removeColumn(columnIndex);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    //method to remove COLUMN from virtual memory table
    public boolean removeColumn(int columnIndex){
        String sColumnName = null;
        try {
            sColumnName = getColumnName(columnIndex);
            columnIndexMap.remove(columnIndex);
            columnNameMap.remove(sColumnName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    //method to add row in virtual memory table
    public boolean addRow() throws Exception{        
        try {
            rowsDataMap.put(getRowCount() +1 , new HashMap<Integer, String>());
            return true;
        } catch (Exception e) {
            throw new Exception("DataTable : addRow() : " + e.toString());
        }
    }
     
    //updating MyDataTable data based on row index & column value
    public boolean updateData(int rowIndex, String columnName , String value) throws Exception{
        int columnIndex = -2;
        try {
            if(rowIndex <=0 || rowIndex > getRowCount() ){
                throw new Exception("MyDataTable : updateData : Invliad Row Index : " + rowIndex + " : total " + getRowCount() + " rows exists");
            }
            
            if(columnName == null){
                throw new Exception("INVALID/NULL Column Name");
            }
            columnName = columnName.trim().toUpperCase();
            if(columnName.equalsIgnoreCase("")){
                throw new Exception("INVALID/BLANK Column Name");
            }
            //getting column index from columnname
            columnIndex = getColumnIndex(columnName);
            if(columnIndex <=0 || columnIndex > getColumnCount() ){
                throw new Exception("MyDataTable : updateData : Invliad Column Index : " + columnIndex + " : total " + getColumnCount() + " columns exists");
            }
            
            //updating data
            return updateData(rowIndex, columnIndex, value);
        } catch (Exception e) {
            throw new Exception("DataTable : UpdateData : rowIndex=" + rowIndex + " : columnName=" + columnName + " : " + e.toString().replaceAll("java.lang.Exception", ""));
        }     
    }

    //updating MyDataTable data based on row index & column index
    public boolean updateData(int rowIndex, int columnIndex , String value) throws Exception{
        HashMap<Integer, String> row = new HashMap<Integer, String>();
        try {

            if(rowIndex <=0 || rowIndex > getRowCount() ){
                throw new Exception("MyDataTable : updateData : Invliad Row Index : " + rowIndex + " : total " + getRowCount() + " rows exists");
            }
            if(columnIndex <=0 || columnIndex > getColumnCount() ){
                throw new Exception("MyDataTable : updateData : Invliad Column Index : " + columnIndex + " : total " + getColumnCount() + " columns exists");
            }
            
            //check for valid row
            row = rowsDataMap.get(rowIndex);
            if(row == null) {throw new Exception("MyDataTable : updateData : Now row found at index : " + rowIndex);}
            //putting data in column map of particular row
            row.put(columnIndex, value);
            
            //putting modifieed data in HashMap
            rowsDataMap.put(rowIndex, row);
            
            return true;
        } catch (Exception e) {
            throw new Exception("DataTable : UpdateData : rowIndex=" + rowIndex + " : columnIndex=" + columnIndex + " : " + e.toString().replaceAll("java.lang.Exception", ""));
        }
    }
    
    //method to update datatable in database
    public ArrayList<String> getBulkInsertUpdateQuery(String tableName) throws Exception{
        
        String[] saRetVal = null;
        ArrayList<String> alBulkQuery = new ArrayList<String>();
        
        int rowNo = 0;
        int colNo = 0;
        
        StringBuilder sb_1 = new StringBuilder();
        StringBuilder sb_2 = new StringBuilder();
        StringBuilder sb_3 = new StringBuilder();
        
        boolean bProcessSubQryColDuplicatekey = true;
        int counter = 0;
        double iTracker = 0.0;
        int rowCount = 0;
        int columnCount = 0;
        try {
            
            rowCount = getRowCount();
            columnCount = getColumnCount();
            
            //sub query for columns
            sb_1.append("INSERT INTO ");
            sb_1.append("`").append(tableName).append("`");
            sb_1.append(" (");
            
            //sub query for oon duplicate key update
            sb_3.append(" ON DUPLICATE KEY UPDATE ");
            
            //row loop
            for(rowNo = 1 ; rowNo <= rowCount; rowNo++){
                sb_2.append("('");
                
                //column loop
                for(colNo = 1; colNo <= columnCount; colNo++){
                    
                    //creating sub query for columns
                    if(bProcessSubQryColDuplicatekey){
                        String columnName = "`" + getColumnName(colNo) + "`";
                        //appending column name query sub string
                        sb_1.append(columnName.toUpperCase()).append(",");

                        //appending on duplicate key query sub string
                        sb_3.append(columnName).append(" = VALUES(").append(columnName).append("),");
                    }
                    
                    //creating value
                    String value = getValue(rowNo,colNo);
                    if(value == null || value.trim().equalsIgnoreCase("")){
                        value = getColumn(colNo).defaultValue;
                    }else{
                        value = value.replaceAll("\\\\", "/");
                    }
                    sb_2.append(value).append("','");
                }
                //appending 
                sb_2.replace(sb_2.length() -2, sb_2.length(), "),");
                
                
                
                //process for sub query for column & on duplicate key values
                if(bProcessSubQryColDuplicatekey){
                    sb_1.replace(sb_1.length() -1, sb_1.length(), ") VALUES ");
                    sb_3.replace(sb_3.length() -1, sb_3.length(), "");
                    bProcessSubQryColDuplicatekey = false;
                }
                
                counter += 1;
                if(counter == 500){
                    sb_2.replace(sb_2.length() -1, sb_2.length(), "");
                    alBulkQuery.add(sb_1.toString() + " " + sb_2.toString() + " " + sb_3.toString());
                    
                    counter = 0;
                    sb_2 = new StringBuilder();
                }
            }
            
            
            if(counter > 0){
                sb_2.replace(sb_2.length() -1, sb_2.length(), "");
                alBulkQuery.add(sb_1.toString() + " " + sb_2.toString() + " " + sb_3.toString());
            }
            
            return alBulkQuery;
        } catch (Exception e) {
            throw new Exception("getBulkInsertUpdateQuery : " + iTracker + e.toString());
        }finally{
        }
    }
    
    //updated data will be commited in database
    public int commit(Connection conn) throws Exception{
        //start request
        start();
        
        Statement smt = null;
        String[] saBulkQuery = null;
        int queryNo = 0;
        int recordsAffected = 0;
        ArrayList<String> queryList = null;
        boolean autoCommit = false;
        String query = "";
        try {
            //getting default autoCommit mode of connection
            autoCommit = conn.getAutoCommit();
            
            //enabling transaction mode setting autocommit as false
            conn.setAutoCommit(false);
            
            queryList = getBulkInsertUpdateQuery(tableName);
            try{
            smt = conn.createStatement();
            }catch(SQLException ex){
            	throw ex;
            }
            
            for(queryNo = 0 ; queryNo < queryList.size() ; queryNo++){
                query = "";
                query = queryList.get(queryNo);
                recordsAffected += smt.executeUpdate(query);
            }
            
            ///commiting records data
            conn.commit();
            
            return recordsAffected;
        } catch (Exception e) {
            conn.rollback();
            throw new Exception("MyDataTable : commit : " + e.toString() + " : " + query);
        }finally{
            //setting default audtocommit mode
        	if(smt!=null){
            	smt.close();
            }
            conn.setAutoCommit(autoCommit);
            
            //end request
            end();
            System.gc();
        }                        
    }
    
    //method to genetare csv data of data table
    public String getCsvData(String separator) throws Exception{
    
        StringBuffer sb = null;
        int colNo = 0;
        int rowNo = 0;
        int rowCount = 0;
        int columnCount = 0;
        try {
            rowCount = getRowCount();
            columnCount = getColumnCount();
            
            if(columnCount <= 0){
                throw new Exception("Invliad column count for row : " + columnCount);
            }
            
            if(separator == null){separator = "";}
            if(separator.trim().equalsIgnoreCase("")){separator = ",";}
            
            //writing column header in csv
            sb = new StringBuffer();
            for(colNo = 1; colNo <= columnCount; colNo++){
            	String colName = columnIndexMap.get(colNo).absoluteName;
            	if(colName.equalsIgnoreCase("")){
            		colName = columnIndexMap.get(colNo).header;
            	}
            	colName = colName.equalsIgnoreCase("") ? columnIndexMap.get(colNo).absoluteHeader : colName;
                sb.append(colName);
                if(colNo != columnCount){sb.append(separator);}
            }
            sb.append(System.getProperty("line.separator"));
            
            //writing row values in csv
            for(rowNo = 1; rowNo <= rowCount; rowNo++){
                HashMap<Integer, String> rowData = rowsDataMap.get(rowNo);
                for(colNo = 1; colNo <= columnCount; colNo++){
                	if(null!=rowData.get(colNo)){
                    sb.append("\""+String.valueOf(rowData.get(colNo))+"\"");
                	}else{
                		sb.append("");
                	}
                    if(colNo != columnCount){sb.append(separator);}
                }
                sb.append(System.getProperty("line.separator"));
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            throw new Exception("generateCSV : " + e.toString());
        }
    }
    
    public String getCsvDataWithQuote(String separator) throws Exception{
        
        StringBuffer sb = null;
        int colNo = 0;
        int rowNo = 0;
        int rowCount = 0;
        int columnCount = 0;
        try {
            rowCount = getRowCount();
            columnCount = getColumnCount();
            
            if(columnCount <= 0){
                throw new Exception("Invliad column count for row : " + columnCount);
            }
            
            if(separator == null){separator = "";}
            if(separator.trim().equalsIgnoreCase("")){separator = ",";}
            
            //writing column header in csv
            sb = new StringBuffer();
            for(colNo = 1; colNo <= columnCount; colNo++){
                
                if(columnIndexMap.get(colNo).absoluteName.equalsIgnoreCase("")){
                	sb.append("\""+columnIndexMap.get(colNo).header+"\"");
                }else{
                	sb.append("\""+columnIndexMap.get(colNo).absoluteName+"\"");
                }
                if(colNo != columnCount){sb.append(separator);}
            }
            sb.append(System.getProperty("line.separator"));
            
            //writing row values in csv
            for(rowNo = 1; rowNo <= rowCount; rowNo++){
                HashMap<Integer, String> rowData = rowsDataMap.get(rowNo);
                for(colNo = 1; colNo <= columnCount; colNo++){
                	if(null!=rowData.get(colNo)){
                    sb.append("\""+new String(rowData.get(colNo).toString().getBytes("utf-8"))+"\"");
                	}else{
                		sb.append("");
                	}
                    if(colNo != columnCount){sb.append(separator);}
                }
                sb.append(System.getProperty("line.separator"));
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            throw new Exception("generateCSV : " + e.toString());
        }
    }
    
     //method to genetare tsv (Tab seperated values) data of data table
    public String getTsvData() throws Exception{
        String separator = "\t";
        StringBuffer sb = null;
        int colNo = 0;
        int rowNo = 0;
        int rowCount = 0;
        int columnCount = 0;
        try {
            rowCount = getRowCount();
            columnCount = getColumnCount();
            
            if(columnCount <= 0){
                throw new Exception("Invliad column count for row : " + columnCount);
            }
            
            if(separator == null){separator = "";}
            /*if(separator.trim().equalsIgnoreCase("")){separator = ",";}*/
            
            //writing column header in csv
            sb = new StringBuffer();
            for(colNo = 1; colNo <= columnCount; colNo++){
            	String colName=columnIndexMap.get(colNo).absoluteName;
            	colName =  colName.equalsIgnoreCase("")?columnIndexMap.get(colNo).name : colName;
                sb.append(colName);
                if(colNo != columnCount){sb.append(separator);}
            }
            sb.append(System.getProperty("line.separator"));
            
            //writing row values in csv
            for(rowNo = 1; rowNo <= rowCount; rowNo++){
                HashMap<Integer, String> rowData = rowsDataMap.get(rowNo);
                for(colNo = 1; colNo <= columnCount; colNo++){
                	if(null!=rowData.get(colNo)){
                        sb.append(rowData.get(colNo).toString());
                    	}else{
                    		sb.append("");
                    	}
                    //sb.append(rowData.get(colNo).toString());
                    if(colNo != columnCount){sb.append(separator);}
                }
                sb.append(System.getProperty("line.separator"));
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            throw new Exception("getTsvData : " + e.toString());
        }
    }
    
    public String getTsvDataWithQuote() throws Exception{
        String separator = "\t";
        StringBuffer sb = null;
        int colNo = 0;
        int rowNo = 0;
        int rowCount = 0;
        int columnCount = 0;
        try {
            rowCount = getRowCount();
            columnCount = getColumnCount();
            
            if(columnCount <= 0){
                throw new Exception("Invliad column count for row : " + columnCount);
            }
            
            if(separator == null){separator = "";}
            /*if(separator.trim().equalsIgnoreCase("")){separator = ",";}*/
            
            //writing column header in csv
            sb = new StringBuffer();
            for(colNo = 1; colNo <= columnCount; colNo++){
            	if(columnIndexMap.get(colNo).absoluteName.equalsIgnoreCase("")){
                	sb.append("\""+columnIndexMap.get(colNo).header+"\"");
                }else{
                	sb.append("\""+columnIndexMap.get(colNo).absoluteName+"\"");
                }
                if(colNo != columnCount){sb.append(separator);}
            }
            sb.append(System.getProperty("line.separator"));
            
            //writing row values in csv
            for(rowNo = 1; rowNo <= rowCount; rowNo++){
                HashMap<Integer, String> rowData = rowsDataMap.get(rowNo);
                for(colNo = 1; colNo <= columnCount; colNo++){
                	if(null!=rowData.get(colNo)){
                        sb.append("\""+rowData.get(colNo).toString()+"\"");
                    	}else{
                    		sb.append("");
                    	}
                    //sb.append(rowData.get(colNo).toString());
                    if(colNo != columnCount){sb.append(separator);}
                }
                sb.append(System.getProperty("line.separator"));
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            throw new Exception("getTsvData : " + e.toString());
        }
    }
    
    //method to generate json array data of data table
    public JsonArray getJsonData() throws Exception{
        int colNo = 0;
        int rowNo = 0;
        int rowCount = 0;
        int columnCount = 0;
        JsonArrayBuilder jsonTable = Json.createArrayBuilder();
        JsonObjectBuilder jsonRow = null;
        try {
            rowCount = getRowCount();
            columnCount = getColumnCount();
            
            if(columnCount <= 0){
                throw new Exception("Invliad column count for row : " + columnCount);
            }
            //writing row values in csv
            for(rowNo = 1; rowNo <= rowCount; rowNo++){
            	jsonRow = null;
            	jsonRow = Json.createObjectBuilder();
                for(colNo = 1; colNo <= columnCount; colNo++){
                	String absu_col_name=columnIndexMap.get(colNo).absoluteName==""?columnIndexMap.get(colNo).header : columnIndexMap.get(colNo).absoluteName;
                	//jsonRow.add(columnIndexMap.get(colNo).absoluteName, this.getValue(rowNo, colNo));
                	jsonRow.add(absu_col_name, this.getValue(rowNo, colNo));
                }
                jsonTable.add(jsonRow);
            }
            
            return jsonTable.build();
            
        } catch (Exception e) {
            throw new Exception("getJsonData : " + e.toString());
        }
    }
    
    
    //method to generate json array data of data table
    public JsonObject getJsonRecords() throws Exception{
        int colNo = 0;
        int rowNo = 0;
        int rowCount = 0;
        int columnCount = 0;
        JsonObjectBuilder jsonTable = Json.createObjectBuilder();
        JsonArrayBuilder jsonHeaderArray = null;
        JsonArrayBuilder jsonRowArray = null;
        
        try {
            rowCount = getRowCount();
            columnCount = getColumnCount();
            if(columnCount <= 0){
                throw new Exception("Invliad column count for row : " + columnCount);
            }
            
            //generating Json header array
            jsonHeaderArray = Json.createArrayBuilder();
            for(colNo = 1; colNo <= columnCount; colNo++){
            	jsonHeaderArray.add(columnIndexMap.get(colNo).absoluteName);
            }
            jsonTable.add("headers", jsonHeaderArray);
            //
            
            
            //writing row values in csv
            jsonRowArray = Json.createArrayBuilder();
            for(rowNo = 1; rowNo <= rowCount; rowNo++){
            	JsonArrayBuilder jsonTemp = Json.createArrayBuilder();
                for(colNo = 1; colNo <= columnCount; colNo++){
                	jsonTemp.add(this.getValue(rowNo, colNo));                	
                }
                jsonRowArray.add(jsonTemp);
            }
            jsonTable.add("records", jsonRowArray);
            //
            
            return jsonTable.build();
            
        } catch (Exception e) {
            throw new Exception("getJsonData : " + e.toString());
        }
    }
    
    public JsonArray getJsonKeyValue() throws Exception{
        int colNo = 0;
        int rowNo = 0;
        int rowCount = 0;
        int columnCount = 0;
        JsonObjectBuilder jsonTable = Json.createObjectBuilder();
        JsonArrayBuilder jsonRowArray = null;
        
        try {
            rowCount = getRowCount();
            columnCount = getColumnCount();
            if(columnCount <= 0){
                throw new Exception("Invliad column count for row : " + columnCount);
            }
            
            jsonRowArray = Json.createArrayBuilder();
            for(rowNo = 1; rowNo <= rowCount; rowNo++){
                for(colNo = 1; colNo <= columnCount; colNo++){
                	String headerName="";
                	headerName=columnIndexMap.get(colNo).absoluteName;
                	if(headerName.equalsIgnoreCase("") || headerName.equalsIgnoreCase(" ")){
                		headerName=columnIndexMap.get(colNo).name;
                	}
                	jsonTable.add(headerName, this.getValue(rowNo, colNo));              	
                }
                jsonRowArray.add(jsonTable);
            }
            return jsonRowArray.build();
            
        } catch (Exception e) {
            throw new Exception("getJsonData : " + e.toString());
        }
    }
    
    public JsonArray getJsonWithKeyValue() throws Exception{
        int colNo = 0;
        int rowNo = 0;
        int rowCount = 0;
        int columnCount = 0;
        JsonObjectBuilder jsonTable = Json.createObjectBuilder();
        JsonArrayBuilder jsonRowArray = null;
        
        try {
            rowCount = getRowCount();
            columnCount = getColumnCount();
            if(columnCount <= 0){
                throw new Exception("Invliad column count for row : " + columnCount);
            }
            
            jsonRowArray = Json.createArrayBuilder();
            for(rowNo = 1; rowNo <= rowCount; rowNo++){
                for(colNo = 1; colNo <= columnCount; colNo++){
                	jsonTable.add("key", columnIndexMap.get(colNo).absoluteName);
                	jsonTable.add("value", this.getValue(rowNo, colNo));
                	//jsonTable.add(columnIndexMap.get(colNo).absoluteName, this.getValue(rowNo, colNo));              	
                	jsonRowArray.add(jsonTable);
                }
                
            }
            return jsonRowArray.build();
            
        } catch (Exception e) {
            throw new Exception("getJsonData : " + e.toString());
        }
    }
    
    public JsonObject getJson() throws Exception{
        int colNo = 0;
        int rowNo = 0;
        int rowCount = 0;
        int columnCount = 0;
        JsonArrayBuilder jsonTable = Json.createArrayBuilder();
        JsonObjectBuilder jsonRow = null;
        JsonObjectBuilder json = null;
        try {
            rowCount = getRowCount();
            columnCount = getColumnCount();
            
            if(columnCount <= 0){
                throw new Exception("Invliad column count for row : " + columnCount);
            }
            
     
            
            //writing row values in csv
            for(rowNo = 1; rowNo <= rowCount; rowNo++){
            	jsonRow = null;
            	jsonRow = Json.createObjectBuilder();
                for(colNo = 1; colNo <= columnCount; colNo++){
                	jsonRow.add(columnIndexMap.get(colNo).absoluteName, this.getValue(rowNo, colNo));
                }
                jsonTable.add(jsonRow);
            }
           
            //returning json object
            json = Json.createObjectBuilder();
            json.add("data", jsonTable);
            return json.build();
        } catch (Exception e) {
            throw new Exception("getJson : " + e.toString());
        }
    }
    
    
//############################################################################
//########################## GET  METHODS ####################################
    
    //method to get row count from a result set
    public int getRowCount(){
        return rowsDataMap.size();
	}

    //method to get Column count from a result set
    public int getColumnCount() throws Exception{
		if(columnIndexMap.size() == columnNameMap.size()){
            return columnIndexMap.size();
        }else{
            throw new Exception("Column Count Mis-Match for Index and Name : Column Index Count=" + columnIndexMap.size() +  " : Column Name Count=" + columnNameMap.size());
        }
	}

    //methods to get last executed query time in seconds
    public int getRequestTime(){
        tat = Integer.parseInt("" + (endTime - startTime + 5));
        return tat;
    }
    
    //methods to access particular item from rowsdata via row index and column index
	public String getValue(int rowIndex, int columnIndex) throws Exception {
        double iTracker = 0.0;
        try {
            //check for valid row index
            iTracker = 1.0;
            if(rowIndex > getRowCount()){
                iTracker = 2.0;
                throw new Exception("Invalid Row Index : " + rowIndex + " : Total " + getRowCount() + " Rows exists in MyDataTable");
            }

            //check for valid column index
            iTracker = 3.0;
            if(columnIndex > getColumnCount()){
                iTracker = 4.0;
                throw new Exception("Invalid Column Index : " + columnIndex + " : Total " + getColumnCount() + " Columns exists in MyDataTable");
            }

            //check if row exists
            iTracker = 5.0;
            HashMap<Integer, String> row = rowsDataMap.get(rowIndex);
            iTracker = 6.0;
            if(row == null){
                iTracker = 7.0;
                throw new Exception("No row found at Index : " + rowIndex);
            }
            //check if column exists
            iTracker = 8.0;
            String value = row.get(columnIndex);
            iTracker = 9.0;
            if(value == null){
                iTracker = 10.0;
                value = getColumn(columnIndex).defaultValue;
            }
            iTracker = 11.0;
            return value;
        } catch (Exception e) {
            throw new Exception("MyDataTable : getValue : iTracker=" + iTracker + " : " + e.toString().replaceAll("java.lang.Exception", ""));
        }
	}

	//methods to access particular item from rowsdata via row index and column name
	public String getValue(int rowIndex, String  columnName) throws Exception {
		int columnIndex = getColumnIndex(columnName);
		return getValue(rowIndex, columnIndex);
	}
    
    //method to get column index from name
    public int getColumnIndex(String columnName) throws Exception{
        try {
            return columnNameMap.get(columnName.trim().toUpperCase()).index;
        } catch (Exception e) {
            throw new Exception("MyDataTable : getColumnIndex : no Index found with name : " + columnName + " : in total " + getColumnCount() + " Columns");
        }
    }
        
    //method to get column name from index
    public String getColumnName(int columnIndex) throws Exception{
        try {
            return columnIndexMap.get(columnIndex).name.trim().toUpperCase();
        } catch (Exception e) {
            throw  new Exception("MyDataTable : getColumnName : No Column found at index : " + columnIndex + " : in total " + getColumnCount() + " Columns");
        }
    }
    
    public String getAbsoluteColumnName(int columnIndex) throws Exception{
        try {
            return columnIndexMap.get(columnIndex).absoluteName.trim();
        } catch (Exception e) {
            throw  new Exception("MyDataTable : getColumnName : No Column found at index : " + columnIndex + " : in total " + getColumnCount() + " Columns");
        }
    }
    
    //method to access getColumn data from external class via column index
	public Column getColumn(int columnIndex) throws Exception{
		if (columnIndex <= 0 || columnIndex > getColumnCount()) {
            throw new Exception("MyDataTable : getColumn : Invalid Column Index : " + columnIndex + " total " + getColumnCount() + " exists");
        }
		return columnIndexMap.get(columnIndex);
	}

	//method to access getColumn data from external class via column name
	public Column getColumn(String columnName) throws Exception{
		
        if( columnName == null || columnName.trim().equalsIgnoreCase("")) {
            throw new Exception("Column : Invalid/Blank/NULL Column Name");
        }

		Column column = columnNameMap.get(columnName.trim().toUpperCase());
		if (column == null) {
            throw new Exception("MyDataTable : getColumn : Column : " + columnName + " : does not exists.");
        }
		return column;
	}

	//method to access getRow data from external class via row index
	public HashMap<Integer, String> getRow(int index) throws Exception{
        if(index > getRowCount()){
            throw new Exception("Invalid Row Index : " + index + " : Total " + getRowCount() + " Rows exists in MyDataTable");
        }
        return  rowsDataMap.get(index);
	}

    //method to get key of a particular row
    public String getRowKey(int rowIndex) throws Exception{
        double iTracker = 0.0;
        String rowKey = "";
        try {
            if(rowIndex > getRowCount()){
                throw new Exception("Invalid Row Index : " + rowIndex + " : Total " + getRowCount() + " Rows exists in MyDataTable");
            }
            
            rowKey = rowIndexKeyMap.get(rowIndex);
            if(rowKey == null){
                //throw new Exception("NULL Row Key found for Index : " + rowIndex);
                rowKey = "";
            }
            
            return rowKey;
        } catch (Exception e) {
            throw new Exception("MyDataTable : getRowKey : iTracker=" + iTracker + " : " + e.toString().replaceAll("java.lang.Exception", ""));
        }
    }

    //method to get ROW of a particular key
    public int getRowIndex(String rowKey) throws Exception{
        double iTracker = 0.0;
        int rowIndex = -2;
        try{
            if(rowKey == null){
                throw new Exception("NULL Row Key value");
            }
            rowKey = rowKey.trim().toUpperCase();
            if(rowKey.equalsIgnoreCase("")){
                throw new Exception("BLANK Row Key value");
            }
            
            if(rowKeyIndexMap.get(rowKey) == null){
                rowIndex = -2;
                //throw new Exception("No/Zero Row index found for row key : " + rowKey);
            }else{
                rowIndex = rowKeyIndexMap.get(rowKey);
            }
            
            return rowIndex;
        }catch(Exception e){
            throw new Exception("MyDataTable : getRowIndex : iTracker=" + iTracker + " : " + e.toString().replaceAll("java.lang.Exception", ""));
        }
    }
    
    //method to set key of a particular Row
    public boolean setRowKey(int rowIndex, String rowKey) throws Exception{
        if(rowKey == null){
            throw new Exception("MyDataTable : setRowKey : Cannot set NULL key for rowIndex : " + rowIndex);
        }
        rowKey = rowKey.trim().toUpperCase();
        if(rowKey.equalsIgnoreCase("")){
            throw new Exception("MyDataTable : setRowKey : Cannot set BLANK key for rowIndex : " + rowIndex);
        }
        rowIndexKeyMap.put(rowIndex, rowKey);
        rowKeyIndexMap.put(rowKey, rowIndex);
        return true;
    }

    //################# methos to track performance of request/transaction ############
    //set start time
    private void start(){
        startTime = (int) (Calendar.getInstance().getTimeInMillis()/1000);
    }
    
    //set end time
    private void end(){
        endTime = (int) (Calendar.getInstance().getTimeInMillis()/1000);
    }
    
    //set get turn around time
    public int getTat(){
        return (endTime - startTime);
    }
    
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    
	public String getSchemaName() {
		return schemaName;
	}
	
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	public void setTat(int tat) {
		this.tat = tat;
	}
    
//############################################################################
//########################### Inner/Sub classes used for getRow and getColumn to store data


	//structure class for COLUMN attributes
	public class Column{
        public String name = "";
        public String header = "";
        public String absoluteName = "";
        public String absoluteHeader = "";
        public String type = "";
        public String tableName = "";
        public String schemaName = "";
        public String database = "";
        public int length = -2;
        public int index = 0;
        public boolean autoIncremented = false;
        public boolean caseSensitive = false;
        public boolean nullable = false;
        public boolean signed = false;
        public boolean readOnly = false;
        public String defaultValue = "";
        public boolean isUpdatedColumn = false;        
    }

    
}//END CLASS
