package com.mps.insight.global;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	
	public Long ipToLong(String addr) {
		 
		String[] ipAddressInArray = addr.split("\\.");

		long result = 0;
		for (int i = 3; i >= 0; i--) {
			long ip = Long.parseLong(ipAddressInArray[3 - i]);
			//left shifting 24,16,8,0 and bitwise OR
			//1. 192 << 24
			//1. 168 << 16
			//1. 1   << 8
			//1. 2   << 0
			result |= ip << (i * 8);		
		}
		return result;
	}
	//method to convert date to unixtimestamp format
    public long DateStringToUnixTimeStamp(String sDateTime) {
    	DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        //DateFormat formatter;
        //Date date = null;
        long unixtime = 0;
        //formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
        	Date date=formatter.parse(sDateTime);
            unixtime = date.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
            unixtime = 0;
        }
        return unixtime;
    }
	
}
