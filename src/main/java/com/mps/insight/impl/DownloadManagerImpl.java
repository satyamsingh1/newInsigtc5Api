package com.mps.insight.impl;

import java.io.InputStream;

import com.mps.aws.S3;

public class DownloadManagerImpl {

	S3 awsS3 = null;
	
	public InputStream getFile(String url,String publisher,String year,String month,String day,
			String reporttype,String reportname,String format,String accountCode,String screen) {
		InputStream is = null;
		String fileurl="";
		String reportUrl="";
		if(null!=reporttype && reporttype.equalsIgnoreCase("feeds")){
			reportUrl =  "insight-feeds/";
			fileurl=reportUrl+getFeedUrl(publisher, url);
		}else if(null!=reporttype && reporttype.equalsIgnoreCase("publisher")){
		reportUrl =  "insight-reports/";
		fileurl=reportUrl+getFileUrl(publisher, year,month,day,reporttype,reportname,format,accountCode,screen);
		}else if(null!=reporttype && reporttype.equalsIgnoreCase("counter")){
			fileurl =  "insight-reports/" + publisher+"/"+year+"/"+month+"/"+accountCode+"/"+reportname+"."+format;;
		}
		
		try {

			awsS3 = new S3("AKIAI3U4NGRDVAJSWRGQ", "s53ZCnX3b+g/9Hc/uIM/xf+5HErl5u35MVMyM+E2", S3.REGION.EU_WEST_2);

			is = awsS3.download("www.mpsinsight.com", fileurl);

		} catch (Exception e) {

		}

		return is;
	}
	
	public void closeS3Connection(){
		if(this.awsS3!=null){
			this.awsS3.destroy();
			this.awsS3 = null;
		}
	}
	
	public String getFileUrl(String publisher,String year,String month,String day,String reporttype,
			String reportname,String format,String accountcode,String screen){
		String url="";
		if(reportname.contains("111")){
			reportname=reportname.replace("111", "&");
		}
		if(null!=reporttype && reporttype.equalsIgnoreCase("publisher")){
			
			if(null!=day && Integer.parseInt(day)>0){
				url=publisher+"/"+year+"/"+month+"/addl_reports/"+day+"/"+reportname+"."+format;
			}else{
				if(screen!=null && screen.length()>2){
					if(screen.equalsIgnoreCase("journal") || screen.equalsIgnoreCase("book")){
						if(accountcode.contains("/") || accountcode.contains(" ")){
							accountcode=accountcode.replace("/", "_");
							accountcode=accountcode.replace(" ", "_");
						}
					url=publisher+"/"+year+"/"+month+"/addl_reports/jrnl_reports/"+reportname+"_(#"+accountcode+")."+format;	
					}
					}else{
				url=publisher+"/"+year+"/"+month+"/addl_reports/"+reportname+"."+format;
				}
				
			}
		}
		return url;
	}
	
	public String getFeedUrl(String publisher,String fileurl){
		//String url="";
		
		
		return fileurl.substring(fileurl.indexOf(publisher+"/"));
	}

}
