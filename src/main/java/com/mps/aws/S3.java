package com.mps.aws;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
//import com.mps.stats.MyMetaData;

//
public class S3 {
	private String accessKeyId="";
	private String secretAccessKey="";
	private BasicAWSCredentials credentials = null;
	private AmazonS3 s3Client = null;

	public static Regions REGION;
	Log log = LogFactory.getLog(getClass());
	//
	public S3(String accessKeyId, String secretAccessKey, Regions region) throws Exception{
	
		try {
			
			log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : Connection Initiated");
			if(accessKeyId == null) {throw new Exception("NULL accessKeyId");}
			if(accessKeyId.trim().contentEquals("")) {throw new Exception("BLANK accessKeyId");}
			
			if(secretAccessKey == null) {throw new Exception("NULL secretAccessKey");}
			if(secretAccessKey.trim().contentEquals("")) {throw new Exception("BLANK secretAccessKey");}
			
			this.accessKeyId = accessKeyId;
			this.secretAccessKey = secretAccessKey;
			
			log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : Validating Credentials Format");
			credentials = new BasicAWSCredentials(this.accessKeyId, this.secretAccessKey);
			
			/*
			log.info("S3 : Creating Configuration");
			//clientConfig = new ClientConfiguration();
			log.info("S3 : Setting Protocol");
			//clientConfig.setProtocol(Protocol.HTTP);
			*/
			
			log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : Creating Connection");
			//s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
			//s3Client = new AmazonS3Client(credentials, clientConfig);
			
			s3Client = AmazonS3ClientBuilder.standard()
					.withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
			log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : Connection Success");
			//log.info("S3 : Setting EndPoint");
			//s3Client.setEndpoint("backup_mps");
			
		} catch (Exception e) {
			log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : " + e.toString());
			throw e;
		}
	}


	
	//method to download the file from S3 bucket
	public void download(String bucketName,String sourcePath, String targetPath) throws Exception{
		
		ObjectMetadata objectMetadata = null;
		try {
			
			log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : Downloading File From Bucket : " + bucketName);
			log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : Downloading File : " + sourcePath);
			//getting object from bucket
			
			objectMetadata = s3Client.getObject(
		        new GetObjectRequest(bucketName, sourcePath),
		        new File(targetPath)
			);
			
			log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : File Download MetaData : objectMetadata.toString()");
			
			//method to get metadata info on file download
			if(objectMetadata == null) {
				throw new Exception("S3 : Download File Request Fail : " + objectMetadata.toString());
			}
			
			
			//downloading file
        	File file = new File(targetPath);
        	if(file.exists()) {
        		log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : File Download : Success");
        	}else {
        		log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : File Download : Fail");
        	}
			
        	/*
        	 * List<Bucket> buckets = null;
        	log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : Getting Bucket List : START");
			buckets = s3Client.listBuckets();
			for (Bucket bucket : buckets) {
				log.info(bucket.getName() + " : " +  StringUtils.fromDate(bucket.getCreationDate()));
			}
			log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : S3 : Getting Bucket List : END");
			*/
        	
        	
		}catch(Exception e) {
			log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()).toString() + " : " + e.toString());
			throw e;
		}
	}
	//method to download the file from S3 bucket overload
	@SuppressWarnings("unused")
	public InputStream download(String bucketName, String sourcePath) throws Exception{
		
		InputStream inputStream=null;
		S3Object objectMetadat = null;
		try {
			
			//log.info(MyMetaData.info() + "S3 : Downloading File : " + bucketName + " : " +  sourcePath);
			//getting object from bucket
			
			 objectMetadat = s3Client.getObject(new GetObjectRequest(bucketName, sourcePath));
			 inputStream = objectMetadat.getObjectContent();
			 //log.info(MyMetaData.info() + "S3 : File Download MetaData : objectMetadata.toString()");
			
			//method to get metadata info on file download
			if(objectMetadat == null) {
				throw new Exception("S3 : Download File Request Fail : " + objectMetadat.toString());
			}
			
			//log.info(MyMetaData.info() + "S3 : Downloading File : " + bucketName + " : " +  sourcePath + " : SUCCESS");
		}catch(Exception e) {
			//log.info(MyMetaData.info() + "S3 : Downloading File : " + bucketName + " : " +  sourcePath + " : FAIL : " + e.toString());			
			throw e;
		}
		return inputStream;
	}
	//method to destroy the class object and references
	public void destroy() {
		try {
			s3Client.shutdown();
		}catch(Exception e) {
			//log.info(MyMetaData.info() + " : S3 : Destroy : " + e.toString());
		}
		//log.info(MyMetaData.info() + " : S3 : Disconnected");
	}
	
}
