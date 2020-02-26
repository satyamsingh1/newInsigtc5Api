package com.mps.redis;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class Redis {
	
	private static final Logger LOG = LoggerFactory.getLogger(Redis.class);
	private Jedis jedis;
	private InputStream inputStream;
	private String propFileName = "config.properties";
	private String redisServerIP = "";
	private int redisServerPort = 6379;
	private int redisServerTimeout = 1000;
	
	public Redis() throws Exception {
		try {
			getRedisProperties();
			getRedisConnection();
		} catch (Exception e) {
			LOG.error("Error in Redis Class Constructor "+e.getMessage());
			//throw(e);
		}
	} 
	
	public String getRedisProperties() throws Exception{
		try{
			Properties prop = new Properties();
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
	
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			
			// get the property value and print it out
			redisServerIP = prop.getProperty("redis_server_ip");
			try{
				redisServerPort = Integer.parseInt(prop.getProperty("redis_server_port"));
			}catch(Exception e) {
				LOG.error("Exception in reading property redisServerIP file for Redis Cache. Defaut value is set: "+e.getMessage());
				redisServerPort=6379;
			}
			
			//
			try{
				redisServerTimeout = Integer.parseInt(prop.getProperty("redis_server_timeout"));
			}catch(Exception e) {
				LOG.error("Exception in reading property redisServerTimeout file for Redis Cache. Defaut value is set: "+e.getMessage());
				redisServerTimeout = 1000;
			}
			
			//LOG.info("redisServerIP : " + redisServerIP +" : redisServerPort : "+redisServerPort + " : redisServerTimeout : " + redisServerTimeout);
		} catch (Exception e) {
			LOG.error("Exception in reading property file for Redis Cache."+e.getMessage());
			throw(e);
		}
		return redisServerIP;
	}
	
	public void getRedisConnection() throws Exception{
		try{
			// Connecting to Redis server on localhost
			jedis = new Jedis(redisServerIP, redisServerPort, redisServerTimeout);
			
			/*// check whether server is running or not
			LOG.info("getRedisConnection : " + jedis.ping());*/
		} catch (Exception e){
			LOG.info("getRedisConnection : Exception : " + redisServerIP +" : "+redisServerPort + " : " + redisServerTimeout + " : " + e.getMessage());
		}
	}
	
	public String setAccountListIntoRedis(List<String> accountsList, int webmartID, int setNo) {
		String key = webmartID+"_account_list";
		int accountsListSize = -2;
		try {
			// set the data in redis string
			accountsListSize = accountsList.size();
			jedis.del(key);
			for (int a = 0; a < accountsListSize; a++) {
				jedis.lpush(key, accountsList.get(a));
			}
			
			LOG.info("setAccountListIntoRedis :  webmartID=" + webmartID + " accountsList Size : " + accountsListSize);
		} catch (Exception e) {
			LOG.info("Exception : setAccountListIntoRedis : Key = " + key + " : webmartID=" + webmartID +  " accountsList Size : " + accountsListSize + " : " + e.toString());
			//throw(e);
		}
		return "Setting Accounts List into Redis Cache completed.";
	}
	
	public List<String> getAccountListFromRedis(String key) {
		List<String> value = new ArrayList<String>();
		try {
			value = jedis.lrange(key, 0, -1);
			
		} catch (Exception e) {
			LOG.error("Exception : getAccountListFromRedis : Key = " + key + " : Value=" + value + " : " + e.toString());
			//throw(e);
		}
		return value;
	}
	
	
	public String setValueToRedisFromHashMap(HashMap<String, String> pubDetails,int webmartID) {
		Set<String> setKey=null;
		try {
			LOG.info("setValueToRedisFromHashMap : webmartID = " + webmartID + " : ValueMap=" + pubDetails.toString());
			
			setKey=pubDetails.keySet();
			for (String key : setKey) {
				jedis.del(key);
				jedis.set(key, pubDetails.get(key));
			}
		} catch (Exception e) {
			LOG.error("Exception : setValueToRedisFromHashMap : webmartID = " + webmartID + " : ValueMap=" + pubDetails.toString() + " : " + e.toString());
			return "error";
		}
		return "setValueToRedisFromHashMap into Redis Cache completed.";
	}
	
	public String getValueFromRedisWithKey(String key) {
			String value="";
		try {
			value=jedis.get(key);
			//LOG.info("getValueFromRedisWithKey : Key = " +key + " : Value=" + value);
		} catch (Exception e) {
			LOG.error("Exception : getValueFromRedisWithKey : Key = " + key + " : Value=" + value + " : " + e.toString());
			//throw(e);
			return "error";
		}
		return value;
	}
	
	
	public String setValueToRedisWithKey(String key,String value) {
	try {
		value=jedis.set(key,value);
		//LOG.info("setValueToRedisWithKey : Key = " +key + " : Value=" + value);
	} catch (Exception e) {
		LOG.error("Exception : setValueToRedisWithKey : Key = " + key + " : Value=" + value + " : " + e.toString());
		return "error";
	}
	return value;
}
}
