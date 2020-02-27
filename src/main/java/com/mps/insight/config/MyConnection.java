package com.mps.insight.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
public class MyConnection {
	@Autowired
	private DataSource rscDataSource;
	@Autowired
	private DataSource ieeecsDataSource;
	@Autowired
	private DataSource mmsDataSource;
    
	public Connection getConnection(String publisher) throws SQLException {
		if (publisher.equalsIgnoreCase("rsc")) {

			return rscDataSource.getConnection();
		} else if (publisher.equalsIgnoreCase("ieeecs")) {

			return ieeecsDataSource.getConnection();

		}
		else if (publisher.equalsIgnoreCase("mms")) {

			return mmsDataSource.getConnection();

		}
		return null;

	}

}
