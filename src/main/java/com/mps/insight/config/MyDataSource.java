package com.mps.insight.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class MyDataSource {
	@Bean(name="rscDataSource")
	public DataSource rscDataSource()
	{
		DataSource dataSource=new DriverManagerDataSource("jdbc:mysql://10.31.1.193:3308/rsc?useSSL=false", "insight", "Mps@123*");
	  return dataSource;
	}
	
	@Bean(name="ieeecsDataSource")
	public DataSource ieeecsDataSource()
	{
		DataSource dataSource=new DriverManagerDataSource("jdbc:mysql://10.31.1.193:3308/ieeecs?useSSL=false", "insight", "Mps@123*");
	  return dataSource;
	}
	@Bean(name="mmsDataSource")
	public DataSource mmsDataSource()
	{
		DataSource dataSource=new DriverManagerDataSource("jdbc:mysql://10.31.1.193:3308/mms?useSSL=false", "insight", "Mps@123*");
	  return dataSource;
	}
	
	

}
