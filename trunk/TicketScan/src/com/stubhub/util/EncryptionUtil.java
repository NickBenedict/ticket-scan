package com.stubhub.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import android.os.Environment;
import android.util.Log;

import com.stubhub.Constants;
import java.io.File;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;


   public class EncryptionUtil {
	  private static final String KEY_FACTORY = "PBEWITHSHA-256AND256BITAES-CBC-BC";  
	  //private static final byte[] salt = {      (byte)0xA4, (byte)0x0B, (byte)0xC8, (byte)0x34,      (byte)0xD6, (byte)0x95, (byte)0xF3, (byte)0x13    };  
	  private byte[] salt = null;
	  private static final int KEY_ITERATION_COUNT = 100; 
	  public static Cipher encryptCipher;    
	  public static Cipher decryptCipher;
	  	
	   private static KeyStore ks;
	   private String password;
	   private static EncryptionUtil util = null;
	   private static String keystore_path = Environment.getDataDirectory()+"/data/com.stubhub/stubhub.bks";

		public static EncryptionUtil getEncryptionUtil(String pswd) {
			try {
				if(pswd==null) { //fail to get device ID
					pswd = Constants.DEFAULT_ENCRYPTION_PASSWORD;
				}
				if(util==null) {
					util = new EncryptionUtil();
					util.init(pswd);
				}
				return util;
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}

		}
		
		public void init(String pswd) throws Exception {
			this.password = pswd;
			this.salt = pswd.getBytes();
			File file = new File(keystore_path);
			if (!file.exists()) {// keystore not created yet
		        try {
		                file.createNewFile();
		        } catch (Exception e) {
		                e.printStackTrace();
		        }
		        
		        PBEKeySpec keyspec = new PBEKeySpec(password.toCharArray(),salt,KEY_ITERATION_COUNT,32);        
				SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_FACTORY);        
				SecretKey key = skf.generateSecret(keyspec);        
				
				ks = KeyStore.getInstance("bks");
				ks.load(null, null);
			    
				 
				KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(key);
	            ks.setEntry("stubhub_keyalias", skEntry, new KeyStore.PasswordProtection(password.toCharArray()));
				
	            java.io.FileOutputStream fos =
	                new java.io.FileOutputStream(keystore_path);
	            ks.store(fos, null);
	            fos.close();
			}
			else {
				ks = KeyStore.getInstance("bks");
				java.io.FileInputStream fis =
			        new java.io.FileInputStream(keystore_path);
			    ks.load(fis, null);
			    fis.close();
			}
			
		}

		public boolean createCiphers() {      
			boolean succeeded = false;        
			try {        
				
			    KeyStore.SecretKeyEntry skEntry = (SecretKeyEntry) ks.getEntry("stubhub_keyalias", new KeyStore.PasswordProtection(password.toCharArray()));
				Key key = skEntry.getSecretKey();
			    
				encryptCipher = Cipher.getInstance(KEY_FACTORY);
				encryptCipher.init(Cipher.ENCRYPT_MODE, key);          
				decryptCipher = Cipher.getInstance(KEY_FACTORY);
				decryptCipher.init(Cipher.DECRYPT_MODE, key);         
				succeeded = true;      
			} 
			catch (Exception ex) { 
				Log.e(Constants.TAG,"Error during cipher creation " + Log.getStackTraceString(ex));
				encryptCipher = null;        
				decryptCipher = null;      
			}       
			return succeeded;    
		}  
		
		
		public byte[] encrypt(String input) {
			try {
				if(encryptCipher==null||decryptCipher==null) {
					createCiphers();
				}
				return encryptCipher.doFinal(input.getBytes());
			}
			catch(Exception e) {
				Log.e(Constants.TAG,"Error doing encryption " + Log.getStackTraceString(e));
        		return null;
			}
			
		}
		
		public String decrypt(byte[] encryptionBytes) {
			try {
				if(encryptCipher==null||decryptCipher==null) {
					createCiphers();
				}
				return new String(decryptCipher.doFinal(encryptionBytes));
			}
			catch(Exception e) {
        		Log.e(Constants.TAG,"Error doing decryption " + Log.getStackTraceString(e));
        		return null;
        	}
		}
   } 