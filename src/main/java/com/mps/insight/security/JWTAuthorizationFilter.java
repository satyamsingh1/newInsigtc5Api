package com.mps.insight.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.product.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
	private static final String HEADER = "token";
	private static final String PREFIX = "";
	private static final String SECRET = "insightMpsLimited";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		try {
			if (checkJWTToken(request, response)) {
				Claims claims = validateToken(request);
				if (claims.get("authorities") != null) {
					setUpSpringAuthentication(claims, request);
				} else {
					SecurityContextHolder.clearContext();
				}
			}
			chain.doFilter(request, response);
			
		} catch ( Exception e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		}
	}

	private Claims validateToken(HttpServletRequest request) {
		String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
		return Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwtToken).getBody();
	}

	
	/**
	 * Authentication method in Spring flow
	 * 
	 * @param claims
	 * @throws Exception 
	 */
	private void setUpSpringAuthentication(Claims claims, HttpServletRequest request) throws Exception {
		@SuppressWarnings("unchecked")
		List<String> authorities = (List<String>) claims.get("authorities");
		String userToken = claims.getId();
		RequestMetaData rmd = setRequestMetaData(userToken);
		request.setAttribute("RMD", rmd);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
				authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		SecurityContextHolder.getContext().setAuthentication(auth);

	}

	private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
		String authenticationHeader = request.getHeader(HEADER);
		if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
			return false;
		return true;
	}
	
	
	private RequestMetaData setRequestMetaData(String token) throws Exception {
		RequestMetaData rmd =null;
		try{
			if (token == null) {
				throw new Exception("Token : NULL");
			}
			if (token.trim().equalsIgnoreCase("")) {
				throw new Exception("Token : BLANK");
			}
			if (token.contains(" ")) {
				token=token.replace(" ", "+");
			}
			
			Users tempUser=new Users();
			String[] temp = token.split("~#~");
			if (temp == null) {
				throw new Exception("decode split : NULL");
			}
			if (temp.length != 3) {
				throw new Exception("decode split : Invalid Length");
			}

			rmd =new RequestMetaData();
			
			rmd.setUserCode(temp[1].trim());
			rmd.setWebmartID(Integer.parseInt(temp[2].trim()));
			UserDTO user = tempUser.getUserDetailByUserCode(rmd.getUserCode(),rmd.getWebmartID());			
			rmd.setParameters(user, token);
			rmd.setRmd(rmd);
			rmd.setLiveYearMoth();
		}catch(Exception e){
			throw e;
		}
		return rmd;
	}

}
