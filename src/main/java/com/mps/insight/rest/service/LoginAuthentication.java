package com.mps.insight.rest.service;

import java.util.Calendar;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.exception.MyException;
import com.mps.insight.global.InsightConstant;
import com.mps.insight.global.TableMapper;
import com.mps.insight.global.UserInfo;
import com.mps.insight.product.PublisherSettings;
import com.mps.insight.security.EncoderDecoder;
import com.mysql.cj.log.Log;

@Path("login")
public class LoginAuthentication {

	private static final Logger LOG = LoggerFactory.getLogger(LoginAuthentication.class);

	InsightDAO insightDao = null;

	@SuppressWarnings("deprecation")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticate(@Context HttpServletRequest request, @Context UriInfo info,
			@FormParam("user") String user, @FormParam("pwd") String pwd, @FormParam("publisher") String publisher)
			throws Exception {

		LOG.info("LoginAuthentication Start: " + publisher + " : " + user + " : " + info.getAbsolutePath());
		insightDao = InsightDAO.getInstance(publisher);
		ResponseBuilder rb = null;
		MyDataTable mdt = null;
		String jsonData = "";
		PublisherSettings ps;
		String error = "";
		String user_code = "";
		int webmartID = -2;
		String webmartCode = "";
		StringBuilder stb = new StringBuilder();
		double iTracker = 0.0;
		RequestMetaData rmd = new RequestMetaData();
		try {

			iTracker = 1.0;
			// check for NULL
			if (user == null || user.trim().equalsIgnoreCase("")) {
				iTracker = 2.1;
				throw new Exception("Blank/Null User : " + user.toString());
			}
			if (pwd == null || pwd.trim().equalsIgnoreCase("")) {
				iTracker = 2.2;
				throw new Exception("Blank/Null Password : " + pwd.toString());
			}
			// check for blank
			if (null == publisher || publisher.equalsIgnoreCase("")) {
				iTracker = 2.3;
				throw new Exception("Blank/Null publisher : " + publisher.toString());
			}

			iTracker = 3.0;
			// String pubID = ps.getPublisherIDFromCode(publisher);
			user_code = validateUser(user, pwd, publisher);
			if (user_code.contains("FAILED :")) {
				error = user_code;
				throw new Exception("FAILED : validateUser : " + publisher + " : " + user + " : " + user_code);
			}

			iTracker = 4.0;

			// executing Query
			stb = new StringBuilder();
			stb.append("select * from publisher_settings where data_category ='WEBMART_ID' and data_key='")
					.append(publisher.trim().toUpperCase()).append("'");
			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			if (mdt != null && mdt.getRowCount() != 1) {
				iTracker = 5.0;
				throw new MyException(
						"Invalid/Multiple records found for publisher in publihser_settings : " + stb.toString());
			} else {
				try {
					iTracker = 6.0;
					webmartID = Integer.parseInt(mdt.getValue(1, "data_value"));
					webmartCode = publisher.trim();
				} catch (Exception e) {
					throw new Exception(
							"Exception in casting webmartID from pulisher_setting to Int variable, check for valid value in DB : "
									+ e.toString());
				}
			}

			/*
			 * stb.append(
			 * "SELECT u.id AS userID, u.code AS userCode, u.email_id AS emailID,"
			 * )
			 * .append("u.first_name AS firstName,u.last_name AS lastName, u.password AS token ,u.status AS userStatus,"
			 * )
			 * .append("u.user_type AS userType,r.role AS role, p.webmart_code AS webmartCode,p.webmart_id AS webmartID "
			 * ) .append(", 'P_SALES, P_ST' as disabledMenu ")
			 * .append("FROM "+TableMapper.TABALE.get("user_table")
			 * +" u LEFT JOIN role_master r ON u.role_id=r.role_id ")
			 * .append("LEFT JOIN publishers_master p ON u.publisher_id=p.id WHERE u.id='"
			 * ).append(user_id) .append("'");
			 */

			//
			iTracker = 7.0;
			stb = new StringBuilder();
			stb.append("SELECT u." + TableMapper.TABALE.get("user_code") + " AS userCode, u."
					+ TableMapper.TABALE.get("email_id") + " AS emailID,")
					.append("u." + TableMapper.TABALE.get("first_name") + " AS firstName,u."
							+ TableMapper.TABALE.get("last_name") + " AS lastName, u."
							+ TableMapper.TABALE.get("password") + " AS token ,u." + TableMapper.TABALE.get("api_key")
							+ " AS apikey, ")
					.append("u." + TableMapper.TABALE.get("sushi") + " AS sushiStatus,u."
							+ TableMapper.TABALE.get("status") + " AS userStatus,")
					.append("u." + TableMapper.TABALE.get("user_type")
							+ " AS userType,r.role AS role, 'webmartCode' AS webmartCode,'webmartID' AS webmartID ")
					.append(", 'P_SALES, P_ST' as disabledMenu , u." + TableMapper.TABALE.get("email_alert")
							+ " as `Email_Alert`")
					.append("FROM " + TableMapper.TABALE.get("user_table") + " u LEFT JOIN role_master r ON u."
							+ TableMapper.TABALE.get("role_id") + "=r.role_id ")
					.append("WHERE u." + TableMapper.TABALE.get("user_code") + "='").append(user_code).append("'");

			// MyDataTable is custom class for handling Result set Data
			// . It works like a table with Row and Column property
			iTracker = 8.0;
			mdt = null;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());

			// check for multiple entry in DB/ logical or DB BUG
			if (mdt.getRowCount() != 1) {
				iTracker = 9.0;
				throw new MyException("No/Invalid/Multiple User Record Found for : " + user + " : check "
						+ TableMapper.TABALE.get("user_table") + " table in DB");
			}

			// by kuldeep Sing 20190129 updata user's last Login

			StringBuilder updateQuery = new StringBuilder();
			updateQuery.append("UPDATE `" + TableMapper.TABALE.get("user_table")
					+ "` SET last_login=NOW() WHERE email_id LIKE '%" + user_code + "%'");
			insightDao.executeInsertUpdateQuery(updateQuery.toString());

			// by KSV on 20170802 for update of token
			iTracker = 10.0;
			authorization = new Authorization();
			Calendar cal = Calendar.getInstance();
			String date = cal.getWeekYear() + "" + cal.getTime().getMonth() + "" + cal.getTime().getDay() + ""
					+ cal.getTime().getHours();
			for (int i = 1; i <= mdt.getRowCount(); i++) {
				// generating and updating token string
				String token = date + "~#~" + mdt.getValue(i, "userCode") + "~#~" + webmartID;
				token = authorization.encryptData(token);
				mdt.updateData(i, "token", token);
				// updating webmart ID & Webmart Code in MDT
				mdt.updateData(i, "webmartID", webmartID + "");
				mdt.updateData(i, "webmartCode", webmartCode);
			}
			// End KSV

			iTracker = 11.0;
			StringBuffer customerID = new StringBuffer();
			MyDataTable mdtlib = null;
			if ("publisher".equalsIgnoreCase(mdt.getValue(1, "userType"))) {
			} else {
				StringBuilder stblibrary = new StringBuilder();
				stblibrary.append("SELECT account_code FROM ")
						.append(TableMapper.TABALE.get("user_account_table") + " WHERE user_code='" + user + "'");
				mdtlib = insightDao.executeSelectQueryMDT(stblibrary.toString());
				int rowCount = mdtlib.getRowCount();

				if(rowCount<1){
					LOG.error("Library User '"+user_code+"' not Linkded with any account !!!!");
				}
				for (int i = 1; i <= rowCount; i++) {
					customerID.append(mdtlib.getValue(i, "account_code")).append(",");
				}
				mdt.addColumn("customerID", "");
				mdt.updateData(mdt.getRowCount(), "customerID", customerID.substring(0, customerID.lastIndexOf(",")));
			}
			JsonArray userdetail = mdt.getJsonData();

			JsonObjectBuilder finalDetail = Json.createObjectBuilder();
			finalDetail.add("userDetail", userdetail);

			String role = "";
			String userType = "";

			iTracker = 12.0;
			if ("Publisher".equalsIgnoreCase(mdt.getValue(1, "userType"))) {
				iTracker = 13.0;
				userType = "publisher";
			} else {
				iTracker = 14.0;
				userType = "library";
			}

			if (mdt.getValue(1, "role").equalsIgnoreCase("ADMIN")) {
				iTracker = 15.0;
				role = "admin";
			} else if (mdt.getValue(1, "role").toLowerCase().contains("user")) {
				iTracker = 16.0;
				role = "report_user";
			} else if (mdt.getValue(1, "role").toLowerCase().contains("customer")) {
				iTracker = 17.0;
				role = "customer_care";
			}

			iTracker = 20.0;
			rmd.setUserCode(user_code);
			rmd.setWebmartID(webmartID);
			rmd.setWebmartCode(webmartCode);
			rmd.setInsightDao(insightDao);
			ps = new PublisherSettings(rmd);

			iTracker = 21.0;
			JsonArray accessdetail = ps.getPublisherAndUserAccess(webmartID, role, userType);
			JsonArray dashboardComponent = ps.getPubDashBoardView(webmartID);
			finalDetail.add("accessDetail", accessdetail);
			iTracker = 22.0;
			if (userType.equalsIgnoreCase("publisher")) {
				iTracker = 23.0;
				finalDetail.add("dashboardComponent", dashboardComponent);
			}

			iTracker = 24.0;
			jsonData = finalDetail.build().toString();
			iTracker = 25.0;

			rb = Response.status(200).entity(jsonData);
			LOG.info("LoginAuthentication : SUCCESS : " + publisher + " : " + user + " : JSON Response : " + jsonData);
			rmd.setUserActivity("Login");
			rmd.setResponceStatus(InsightConstant.SUCCESS);
			rmd.setRemoteIP(request.getRemoteAddr());
			new UserInfo().saveInfo(rmd);
		} catch (Exception e) {

			rmd.setUserActivity("Login");
			rmd.setResponceStatus(InsightConstant.FAIL);
			rmd.setRemoteIP(request.getRemoteAddr());
			new UserInfo().saveInfo(rmd);

			rb = Response.status(200).entity(InsightConstant.ERROR_RESPONSE + e.getMessage() + "\"}");
			LOG.info("LoginAuthentication : Exception : FAIL : " + iTracker + " : " + publisher + " : " + user + " : "
					+ e.toString());
		} finally {
			if (mdt != null) {
				mdt.destroy();
			}
		}
		return rb.build();
	}

	public String validateUser(String user, String pwd, String publisher) {
		MyDataTable mdt = null;
		MyDataTable c4mdt = null;
		String result = "";
		String userid = "";
		String password;
		String pass = pwd;
		String password1 = "";
		StringBuilder stb = new StringBuilder();
		EncoderDecoder ed = new EncoderDecoder();
		// saurabh
		try {

			if (publisher.equalsIgnoreCase("iopscience")) {
				pwd = ed.get_SHA_1_SecurePassword(pwd);
			}else{
				pwd = ed.encrypt(pwd);
			}
			
			
			
				stb.append("SELECT " + TableMapper.TABALE.get("user_code") + " as code, " + ""
						+ TableMapper.TABALE.get("password") + " as password " + "FROM "
						+ TableMapper.TABALE.get("user_table") + " " + "WHERE " + TableMapper.TABALE.get("user_code")
						+ "='" + user + "' " + "AND " + TableMapper.TABALE.get("status") + "!='Deleted'");
				mdt = insightDao.executeSelectQueryMDT(stb.toString());
				if (null != mdt && mdt.getRowCount() > 0 ) {
					userid = mdt.getValue(1, "code");
					password = mdt.getValue(1, "password");
					//System.out.println("password is :" + password);
					if (pwd.equals(password)) {
						result = userid;
					} else {
						result = "FAILED : password is invalid ";
					}
				} else {
					result = "FAILED : user not found ";
				}

			
			

			// stb.append("SELECT `id`,`password` FROM
			// "+TableMapper.TABALE.get("user_table")+" WHERE CODE='" + user +
			// "' AND status!='Deleted'");

			

		} catch (Exception e) {
			LOG.info("exception in query " + stb.toString());
			result = "FAILED : sql query";
		} finally {
			if (mdt != null) {
				mdt.destroy();
			}
		}
		return result;
	}

}// END class
