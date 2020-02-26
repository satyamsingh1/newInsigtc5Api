package com.mps.insight.rest.service;

import javax.json.JsonArray;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.dto.EmailDTO;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.impl.MailSenderManager;
import com.mps.insight.product.Users;
import com.mps.insight.security.EncoderDecoder;

@Path("forgetpassword")
public class ForgetPasswordService {
	
	private static final Logger log = LoggerFactory.getLogger(ForgetPasswordService.class);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getsecquestion")
	public Response getSecurityQuestion(@FormParam("username") String email, @FormParam("publisher") String publisher)
			throws Exception {
		
		ResponseBuilder rb = null;
		Users users = new Users();
		int publisherId = 0;
		//PublisherSettings ps = new PublisherSettings(new RequestMetaData());
		//publisherId = Integer.parseInt(ps.getPublisherIDFromCode(publisher));
		log.info("SupportServices class getSecurityQuestion method : Start");
		try {
			JsonArray jarray = users.getUserByEmail(email.trim(),publisher);
			rb = Response.status(200).entity(jarray.toString());
		} catch (Exception e) {
			rb = Response.status(200).entity("{\"error\":\"" + e.getMessage() + "\"}");
			log.error("In getSecurityQuestion method Exception : " + e.getMessage());
		}
		log.info("SupportServices class getSecurityQuestion method : End");
		return rb.build();
	}

	@POST
	@Path("updatepassword")
	public Response updatePassword(@FormParam("user_code") String user_code,@FormParam("publisher") String publisher,
			@FormParam("question") String question,@FormParam("answer") String answer,
			@FormParam("password") String password) throws Exception {
		ResponseBuilder rb = null;
		Users users = new Users();
		EmailDTO emailDto = new EmailDTO();
		MailSenderManager mailSender = new MailSenderManager();
		EncoderDecoder ed=new EncoderDecoder();

		log.info("SupportServices class updatePassword method : Start");

		UserDTO userDto = users.getUserDetailByUserIDAndPublisher(user_code,publisher);
		try {
			if (userDto != null) {
				if (userDto.getAnswer().toUpperCase().equals(answer.toUpperCase())) {
					emailDto.setFirstName(userDto.getFirstName());
					emailDto.setLastName(userDto.getLastName());
					emailDto.setToken("resetPassword");
					emailDto.setMessage("Dear "+userDto.getFirstName()+""+userDto.getLastName()+"\n \t Your password updated scuccessfull. \n User Name : "+userDto.getUserCode()
							+"\n New Password is : " + password+"\n Question is : "+question+" \n Answer is : "+answer+" \n Publisher : "+publisher);
					emailDto.setUseremail(userDto.getEmailID().trim());
					emailDto.setReciever(userDto.getEmailID().trim());
					emailDto.setSubject("Password Reset : MPS Insight COUNTER 5 : Password Reset for <"+userDto.getFirstName()+" "+userDto.getLastName()+">");

					userDto.setUserCode(user_code);
					userDto.setWebmartCode(publisher);

					if (userDto.getUserCode() != null) {
						userDto.setUpdatedBy(userDto.getUserCode());
					} else {
						userDto.setUpdatedBy("System_reset_user");
					}
					if (password.equals(userDto.getPassword())) {
						rb = Response.status(200).entity("{\"error\":\"Try different password\"}");
					} else {
						if("iopscience".equalsIgnoreCase(publisher)){
							userDto.setPassword(ed.get_SHA_1_SecurePassword(password));
							userDto.setLastPassword3(ed.encrypt(password));
						}else{
							userDto.setPassword(ed.encrypt(password));
							//userDto.setLastPassword3(users.getCurrentPassword(userDto));
						}
						
						long rowsAffacted = users.updateUserPassword(userDto);
						if (rowsAffacted != 0) {
							mailSender.sendMail(emailDto);
							rb = Response.status(200).entity("{\"success\":\"updated\"}");
						}
					}

				} else {
					rb = Response.status(200).entity("{\"error\":\"Wrong Answer\"}");
				}
			}
		} catch (Exception e) {
			rb = Response.status(200).entity("{\"error\":\"" + e.getMessage() + "\"}");
			log.error("In updatePassword method Exception : " + e.getMessage());
		}
		log.info("SupportServices class updatePassword method : End");
		return rb.build();
	}
}
