package com.mps.insight.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyException extends Exception{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger("MPS Insight API");

	public MyException(String message) {
		super(message);
		logger.error(message);
	}
	
	public MyException(Throwable cause) {
		super(cause);
		logger.error(cause.getMessage());
	}
	
	public MyException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message, cause);
	}
	
	public MyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		logger.error(message, cause);
	}
		
}
