package com.mps.redis;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisClient {

	private static final Logger LOG = LoggerFactory.getLogger(JedisClient.class);
	private static JedisPool jedisPool = null;
	private final String propFileName = "config.properties";
	private InputStream inputStream;
	//
	private void buildPoolConfig() throws Exception{
		
		String redisServerIP = "";
		int redisServerPort = 6379;
		int redisServerTimeout = 1000;
	    JedisPoolConfig poolConfig = new JedisPoolConfig();
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
			poolConfig.setMaxTotal(200);
		    poolConfig.setMaxIdle(20);
		    poolConfig.setMinIdle(50);
		    poolConfig.setTestOnBorrow(true);
		    poolConfig.setTestOnReturn(true);
		    poolConfig.setTestWhileIdle(true);
		    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
		    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
		    poolConfig.setNumTestsPerEvictionRun(5);
		    poolConfig.setBlockWhenExhausted(true);
			//LOG.info("redisServerIP : " + redisServerIP +" : redisServerPort : "+redisServerPort + " : redisServerTimeout : " + redisServerTimeout);
		} catch (Exception e) {
			LOG.error("Exception in reading property file for Redis Cache."+e.getMessage());
			throw(e);
		}
	    
	    jedisPool = new JedisPool(poolConfig, "localhost");
	}
	
	//
	public static Jedis getJedis(){
		return jedisPool.getResource();
	}
	
}
