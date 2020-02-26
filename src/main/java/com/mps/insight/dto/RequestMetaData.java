package com.mps.insight.dto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.slf4j.LoggerFactory;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.product.PublisherSettings;
import com.mps.redis.Redis;






/*@Provider*/
public class RequestMetaData {	
	
	@Context
	private UriInfo info;

	@Context 
	private ServletContext servletContext;
	
	private String logType="LOG";
	private RequestMetaData rmd;
	//private Integer userID  = 0;
	private Integer publisherID = 0;
	private String userCode ="";
	private String emailID ="";
	private String firstName ="";
	private String lastName ="";
	private String token ="";
	private String userStatus ="";
	private String userType ="";
	
	private String role = "";
	private String webmartCode ="";
	private Integer webmartID =0;
	
	private String liveYear ="";
	private String liveMonth ="";
	private InsightDAO insightDao;
	private String userActivity="";
	private String responceStatus="";
	private String remoteIP="";
	
	//logger variables
	private static final SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
    private boolean enablePrintln = true;
    private boolean deleteExistingLogFile = false;
    private long lastLongTS = 0L;
    private String logFilePath = File.separator + "insight" + File.separator + "counter5" + File.separator + "logs" + File.separator + formatterDate.format(new Date()) + ".log";
    
    public RequestMetaData(){
    	
    	initialize();
    }
    
    private void initialize(){
    	
    	try {
    		logFilePath = File.separator + "insight" + File.separator + "counter5" + File.separator + "logs" + File.separator + formatterDate.format(new Date()) + ".log";
		} catch (Exception e) {
			System.out.println("RequestMetaData.initialize() : " + e.toString());
		}
    	
    }
    
	/*public Integer getUserID() {
		return userID;
	}
	public void setUserID(Integer userID) {
		this.userID = userID;
	}*/
	public Integer getPublisherID() {
		return publisherID;
	}
	public void setPublisherID(int publisherID) {
		this.publisherID = publisherID;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getEmailID() {
		return emailID;
	}
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getWebmartCode() {
		return webmartCode;
	}
	public void setWebmartCode(String webmartCode) {
		this.webmartCode = webmartCode;
	}
	public Integer getWebmartID() {
		return webmartID;
	}
	public void setWebmartID(Integer webmartID) {
		this.webmartID = webmartID;
	}
	
	public void setLiveYear(String liveYear) {
		this.liveYear = liveYear;
	}

	public void setLiveMonth(String liveMonth) {
		this.liveMonth = liveMonth;
	}

	public RequestMetaData getRmd() {
		return rmd;
	}
	public void setRmd(RequestMetaData rmd) {
		this.rmd = rmd;
	}
	
	public InsightDAO getInsightDao() {
		return insightDao;
	}
	public void setInsightDao(InsightDAO insightDao) {
		this.insightDao = insightDao;
	}
	
	public String getUserActivity() {
		return userActivity;
	}

	public void setUserActivity(String userActivity) {
		this.userActivity = userActivity;
	}

	public String getResponceStatus() {
		return responceStatus;
	}

	public void setResponceStatus(String responceStatus) {
		this.responceStatus = responceStatus;
	}
	
	public String getRemoteIP() {
		return remoteIP;
	}

	public void setRemoteIP(String remoteIP) {
		this.remoteIP = remoteIP;
	}
	

	public void setParameters(UserDTO user, String token){
		try {
			setPublisherID(user.getPublisherID());
			setUserCode(user.getUserCode());
			setEmailID(user.getEmailID());
			setFirstName(user.getFirstName());
			setLastName(user.getLastName());
			setToken(token);
			setUserStatus(user.getStatus());
			setUserType(user.getUserType());
			setRole(user.getRole());
			setWebmartCode(user.getWebmartCode());
			setInsightDao(InsightDAO.getInstance(user.getWebmartCode()));
			
		} catch (Exception e) {

		}
	}
	
	public void setLiveYearMoth(){
		try {
			setLiveYear(getLiveYear());
			setLiveMonth(getLiveMonth());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void getInsightDaoConnection(String userid, String publisherCode){
		try {
			if(publisherCode!=null){
			
				setInsightDao(InsightDAO.getInstance(publisherCode));
			}
		} catch (Exception e) {
			
		}
	}
	public String getPublisherNameFromRedis(int webmartID){
		String publisherCode ="";
		try {
			if(webmartID>0){
				Redis redis = new Redis();
				publisherCode = redis.getValueFromRedisWithKey(webmartID+"_publisher_name");
				setInsightDao(InsightDAO.getInstance(publisherCode));
			}
		} catch (Exception e) {
			
		}
		return publisherCode;
	}
	
	public String getLiveYear(){
		String liveYear="0000";
		try {
			PublisherSettings ps =new PublisherSettings(rmd);
			liveYear = ps.getPublisherLiveYear(webmartCode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return liveYear;
	}
	
	public String getLiveMonth(){
		String liveMonth="00";
		try {
			PublisherSettings ps =new PublisherSettings(rmd);
			liveMonth = ps.getPublisherLiveMonth(webmartCode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return liveMonth;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequestMetaData [ userCode=").append(userCode).append(", emailID=").append(emailID)
				.append(", firstName=").append(firstName).append(", lastName=").append(lastName).append(", token=")
				.append(token).append(", userStatus=").append(userStatus).append(", userType=").append(userType)
				.append(", role=").append(role).append(", webmartCode=").append(webmartCode).append(", webmartID=")
				.append(webmartID).append(", liveYear=").append(liveYear).append(", liveMonth=")
				.append(liveMonth).append(", User Activity="+userActivity).append(", Responce Status="+responceStatus).append("]");
		return builder.toString();
	}
	
	
	public void log(String message) {

		try {
			
			String className = Thread.currentThread().getStackTrace()[2].getClassName();
			String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			int lineNo =  Thread.currentThread().getStackTrace()[2].getLineNumber();
			StringBuilder sbMsg = new StringBuilder();
			sbMsg.append(className)
				.append(".")
				.append(methodName)
				.append("(")
				.append(lineNo)
				.append(") : ")
	        	.append(webmartCode)
				.append("(")
				.append(webmartID)
				.append(") : ")
				.append(userCode)
				.append("(")
			//	.append(userID)
				.append(") : ")
				.append(message);

			LoggerFactory.getLogger(className).info(sbMsg.toString());
			//writeLog(message, logFilePath, enablePrintln, deleteExistingLogFile);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void error(String message) {		
		try {
			String className = Thread.currentThread().getStackTrace()[2].getClassName();
			String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			int lineNo =  Thread.currentThread().getStackTrace()[2].getLineNumber();
			StringBuilder sbMsg = new StringBuilder();
			sbMsg.append(className)
				.append(".")
				.append(methodName)
				.append("(")
				.append(lineNo)
				.append(") : ")
	        	.append(webmartCode)
				.append("(")
				.append(webmartID)
				.append(") : ")
				.append(userCode)
				.append("(")
			//	.append(userID)
				.append(") : ")
				.append(message);

			LoggerFactory.getLogger(className).error(sbMsg.toString());
			//writeLog("ERROR : " + message, logFilePath, enablePrintln, deleteExistingLogFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void exception(String message) {

		try {
			String className = Thread.currentThread().getStackTrace()[2].getClassName();
			String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			int lineNo =  Thread.currentThread().getStackTrace()[2].getLineNumber();
			StringBuilder sbMsg = new StringBuilder();
			sbMsg.append(className)
				.append(".")
				.append(methodName)
				.append("(")
				.append(lineNo)
				.append(") : ")
	        	.append(webmartCode)
				.append("(")
				.append(webmartID)
				.append(") : ")
				.append(userCode)
				.append("(")
			//	.append(userID)
				.append(") : ")
				.append(message);

			LoggerFactory.getLogger(className).error(sbMsg.toString());
			//writeLog("EXCEPTION : " + message, logFilePath, enablePrintln, deleteExistingLogFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public RequestMetaData getRMD() {
		// TODO Auto-generated method stub
		return this.rmd;
	}
	
	public void destroyConnection(){
		if (this.insightDao != null) {
				this.insightDao.disconnect();
			}
			this.insightDao = null;
		}
	
	
    //method to write log
    private boolean writeLog(String message, String logFilePath, boolean enablePrintln, boolean deleteExistingLogFile) {
    	BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;
        long currentLongTS = new Date().getTime();
        
        try{
            //check for existing file
            File f = new File(logFilePath.trim());
            if (f.exists() && deleteExistingLogFile) {
                try{
                    f.delete();
                }catch (Exception e){
                	//what to do
                }
            }

            //making directory if not exists
            File dirPath = new File(f.getParent());
            if (!dirPath.exists()){
            	dirPath.mkdirs();
            }

            //setting last long Time Stamp
            String datePrefix = formatterDateTime.format(new Date(currentLongTS));
            double deltaTS = (currentLongTS - lastLongTS)/1000.0;
            if(this.lastLongTS == 0){deltaTS = 0.0;}
            
            //adding time stamp on message text
            if (enablePrintln) {
                System.out.println(datePrefix + " : " + deltaTS + " sec : " + message);
            }
            //writing to file
            fileWriter = new FileWriter(logFilePath, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(datePrefix + " : " + deltaTS + " sec : " + message);
            bufferedWriter.newLine();

            return true;
        } catch (Exception exp) {
            System.out.println((new Date().toString()) + " : EXCEPTION writeLog : " + message + " : " + logFilePath + " : " + deleteExistingLogFile + " : " + exp.toString());
            exp.printStackTrace();
            return false;
        } finally {
            try {
            	this.lastLongTS = currentLongTS;
            	if(bufferedWriter != null)bufferedWriter.close();
            	if(fileWriter != null) fileWriter.close();
            } catch (Exception e) {}
            this.logType = "";
        }
    }
    
}
