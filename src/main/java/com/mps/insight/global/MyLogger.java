package com.mps.insight.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLogger {
	private static final Logger LOG = LoggerFactory.getLogger(MyLogger.class);
	private String hk;
	
	public void info(String str){
		LOG.info(str);
	}
	public void debug(String str){
		LOG.debug(str);
	}
	public static void error(String str){
		LOG.error(str);
	}
	public String getHk() {
		return hk;
	}
	public void setHk(String hk) {
		this.hk = hk;
	}
	public static void log(String hk) {
		LOG.info(hk);
	}
	
}
