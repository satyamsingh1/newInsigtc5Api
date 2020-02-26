package com.mps.insight.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncoderDecoder {

	private static final Logger log = LoggerFactory.getLogger(EncoderDecoder.class);
	private SecretKeySpec secretKey;
    private byte[] key;
 
    public EncoderDecoder() {
    	 MessageDigest sha = null;
    	 String myKey="1987kumar";
         try {
             key = myKey.getBytes("UTF-8");
             sha = MessageDigest.getInstance("SHA-1");
             key = sha.digest(key);
             key = Arrays.copyOf(key, 16);
             secretKey = new SecretKeySpec(key, "AES");
         }
         catch (NoSuchAlgorithmException e) {
             log.error("Exception in EncoderDecoder constructor : "+e.getMessage());
         }
         catch (UnsupportedEncodingException e) {
        	 log.error("Exception in EncoderDecoder constructor : "+e.getMessage());
         }
	}

    public String encrypt(String strToEncrypt)
    {
       try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
        	log.error("Exception in encrypt method : "+e.getMessage());
        }
        return null;
    }
 
    public String decrypt(String strToDecrypt)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e)
        {
        	log.error("Exception in decrypt method : "+e.getMessage());
        }
        return null;
    }
    
    
    //saurabh
	public  String get_SHA_1_SecurePassword(String passwordToHash)
	   {
	       String generatedPassword = null;
	       try {
	    	   MessageDigest md = MessageDigest.getInstance("SHA-1");
	       
	           byte[] bytes = md.digest(passwordToHash.getBytes());
	           StringBuilder sb = new StringBuilder();
	           for(int i=0; i< bytes.length ;i++)
	           {
	               sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	           }
	           generatedPassword = sb.toString();
	           
	           
	       }
	       catch (NoSuchAlgorithmException e)
	       {
	           e.printStackTrace();
	       }
	       return generatedPassword;
	   }
	
	/*public static void main(String[] args) {
		EncoderDecoder ed = new EncoderDecoder();
		String pwd = ed.decrypt("T2Kq+crSOodA7Rp0hj+43w==");
		System.out.println(pwd);
	}*/
}
