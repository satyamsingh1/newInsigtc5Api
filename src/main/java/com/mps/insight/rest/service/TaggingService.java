package com.mps.insight.rest.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.insight.global.Utils;
import com.mps.insight.kafka.KafkaService;

/**
 * This Class is used for getting log details 
 * It is also used for collecting feed details; 
 * 
 */
@Path("tagging")
public class TaggingService {

	private static final Logger log = LoggerFactory.getLogger(TaggingService.class);

	String tokenid = null;
	@Context HttpHeaders headers;
	@Context UriInfo uriDetails;
	@Context HttpServletRequest request;
	/**
	 * 
	 * @param token it should be append in request header, it will validate user and valid request
	 * @param headers not required as it will be fetch by request default
	 * @param uriDetails not required as it will be fetch by request default
	 * @param request not required as it will be fetch by request default
	 */
	public TaggingService(@HeaderParam("token") String token,@Context HttpHeaders headers, @Context UriInfo uriDetails,@Context HttpServletRequest request) {
		this.tokenid = token;
		this.headers=headers;
		this.uriDetails=uriDetails;
		this.request=request;
	}
	/**
	 * This method collect the log data and send it to process and store
	 * to database
	 * 
	 * @param data is detail of parameter used in request for specific
	 * Action OR URl in JSON format
	 * @return string success or error in responce
	 */

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("log")
	public Response logTagging(@FormParam("data")String data) {

		ResponseBuilder rb = null;
		Utils utils = new Utils();
		Document doc = null;
		Long longIP = 0L;
		boolean status = false;
		try {

			try {
				doc = Document.parse(data);
			} catch (Exception e) {
				doc = new Document();
				doc.put("data", data);
			}

			//
			String address = request.getRemoteAddr();
			if (address.equalsIgnoreCase("127.0.0.1") || address.equalsIgnoreCase("localhost")) {
				try {
					address = request.getHeader("X-Forwarded-For").toString();
				} catch (Exception e) {
				}
				;
			}

			if (!doc.containsKey("ip_address"))
				doc.put("ip_address", address);
			try {
				longIP = utils.ipToLong(address.trim());
			} catch (Exception e) {
				longIP = -3L;
			}
			// request parameters
			if (!doc.containsKey("long_ip_address"))
				doc.put("long_ip_address", longIP);
			if (!doc.containsKey("request_method"))
				doc.put("request_method", "post");
			if (!doc.containsKey("status_code"))
				doc.put("status_code", "200");
			if (!doc.containsKey("token"))
				doc.put("token", tokenid);
			if (!doc.containsKey("request_date_time"))
				doc.put("request_date_time", new Date().toString());
			if (!doc.containsKey("host"))
				doc.put("host", request.getRemoteHost());
			if (!doc.containsKey("context_type"))
				doc.put("context_type", request.getContentType());
			if (!doc.containsKey("user"))
				doc.put("user", request.getRemoteUser());
			if (!doc.containsKey("session_id"))
				doc.put("session_id", request.getRequestedSessionId());
			if (!doc.containsKey("uri"))
				doc.put("uri", request.getRequestURI());
			if (!doc.containsKey("url"))
				doc.put("url", request.getRequestURL().toString());
			if (!doc.containsKey("translated_path"))
				doc.put("translated_path", request.getPathTranslated());
			// header parameters
			if (!doc.containsKey("user_agent") && headers.getRequestHeader("user-agent") != null)
				doc.put("user_agent", headers.getRequestHeader("user-agent").toString());
			if (!doc.containsKey("origin") && headers.getRequestHeader("origin") != null)
				doc.put("origin", headers.getRequestHeader("origin").toString());
			if (!doc.containsKey("referer_url") && headers.getRequestHeader("referer") != null)
				doc.put("referer_url", headers.getRequestHeader("referer").toString());

			// sending msg to kafka service
			KafkaService kafkaService = new KafkaService();
			status = kafkaService.sendLog(doc);
			kafkaService.disconnect();

			// setting status for producer request
			if (status) {
				rb = Response.status(200).entity("{\"result\":\"success\"}");
			} else {
				rb = Response.status(500).entity("{\"result\":\"fail\"}");
			}

		} catch (Exception e) {
			log.error(e.toString());
			rb = Response.status(500).entity("{\"result\":\"error\"}");
		}

		return rb.build();
	}

	/**
	 * This method collect the feed data and send it to process and store
	 * to database
	 * 
	 * @param data is feed detail mapping in json format, this data should be detail of database data like institution mapping,accounts mapping,user detail mapping etc.
	 * @return string success or error in responce
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("feed")
	public Response feedTagging(@FormParam("data")String data) {

		ResponseBuilder rb = null;
		Utils utils = new Utils();
		Document doc = null;
		Long longIP = 0L;
		boolean status = false;
		try {

			try {
				doc = Document.parse(data);
			} catch (Exception e) {
				doc = new Document();
				doc.put("data", data);
			}

			//
			String address = request.getRemoteAddr();
			if (address.equalsIgnoreCase("127.0.0.1") || address.equalsIgnoreCase("localhost")) {
				try {
					address = request.getHeader("X-Forwarded-For").toString();
				} catch (Exception e) {
				}
				;
			}

			if (!doc.containsKey("ip_address"))
				doc.put("ip_address", address);
			try {
				longIP = utils.ipToLong(address.trim());
			} catch (Exception e) {
				longIP = -3L;
			}
			// request parameters
			if (!doc.containsKey("long_ip_address"))
				doc.put("long_ip_address", longIP);
			if (!doc.containsKey("request_method"))
				doc.put("request_method", "post");
			if (!doc.containsKey("status_code"))
				doc.put("status_code", "200");
			if (!doc.containsKey("token"))
				doc.put("token", tokenid);
			if (!doc.containsKey("request_date_time"))
				doc.put("request_date_time", new Date().toString());
			if (!doc.containsKey("context_type"))
				doc.put("context_type", request.getContentType());
			if (!doc.containsKey("user"))
				doc.put("user", request.getRemoteUser());
			if (!doc.containsKey("session_id"))
				doc.put("session_id", request.getRequestedSessionId());
			if (!doc.containsKey("uri"))
				doc.put("uri", request.getRequestURI());
			if (!doc.containsKey("url"))
				doc.put("url", request.getRequestURL().toString());
			// header parameters
			if (!doc.containsKey("user_agent") && headers.getRequestHeader("user-agent") != null)
				doc.put("user_agent", headers.getRequestHeader("user-agent").toString());
			if (!doc.containsKey("origin") && headers.getRequestHeader("origin") != null)
				doc.put("origin", headers.getRequestHeader("origin").toString());
			if (!doc.containsKey("referer_url") && headers.getRequestHeader("referer") != null)
				doc.put("referer_url", headers.getRequestHeader("referer").toString());

			// sending msg to kafka service
			KafkaService kafkaService = new KafkaService();
			status = kafkaService.sendLog(doc);
			kafkaService.disconnect();

			// setting status for producer request
			if (status) {
				rb = Response.status(200).entity("{\"result\":\"success\"}");
			} else {
				rb = Response.status(500).entity("{\"result\":\"fail\"}");
			}

		} catch (Exception e) {
			log.error(e.toString());
			rb = Response.status(500).entity("{\"result\":\"error\"}");
		}

		return rb.build();
	}

}
