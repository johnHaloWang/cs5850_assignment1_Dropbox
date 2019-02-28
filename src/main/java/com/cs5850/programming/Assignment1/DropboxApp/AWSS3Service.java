/**
 * 
 */
package com.cs5850.programming.Assignment1.DropboxApp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
//import org.mockito.InjectMocks;

//import com.amazonaws.regions.Regions;

//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
//import com.amazonaws.services.s3.model.CopyObjectResult;
//import com.amazonaws.services.s3.model.DeleteObjectsRequest;
//import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/** AWS S3Service utility class 
 * @author johnhalowang
 * @version 1.0
 * @since 1.0
*/
public class AWSS3Service {
	
	//private static AWSS3Service instance;
	private final AmazonS3 s3client;
	//private static AWSCredentials credentials;
    private String bucketName; 
    private String watchFolder;
    private String s3target;
    
    /**
     * 
     * The default constructor for class AWSS3Service
     *
     * @param input String variable as the local folder path 
     * @version 1.0
     * @since version 1.00
     */
    @SuppressWarnings("deprecation")
	public AWSS3Service() { 
        this(new AmazonS3Client() {
        }); 
    } 
    
    /**
     * 
     * The default constructor for class AWSS3Service
     *
     * @param input String variable as the local folder path 
     * @version 1.0
     * @since version 1.00
     */
    public AWSS3Service(AmazonS3 s3client) {
        this.s3client = s3client;
    }
	
	/**
    *
    * The method ensure there is only one single instance of 
    * 	AWSS3Service based on the singleton patter 
    * 	1. it also set up the credentials for AWS account for access
    * 	2. set-up the Amazon S3 client
    *
    * @param input String variable as the local folder path 
    * @return AWSS3Service
    * @version 1.0
    * @since version 1.00
    */
//	public static synchronized AWSS3Service getInstance(AmazonS3 s3clientInput){
//		if(instance == null){
//			synchronized (AWSS3Service.class) {
//	            if(instance == null){
//	                instance = new AWSS3Service();
//	                //put your accesskey and secretkey here
//	                //set-up the credentials
//	                credentials = new BasicAWSCredentials(
//	                  "AKIAI5HRCERPEMP2PHCQ", 
//	                  "lUS8GJqvusbx47nwjSKn+s/5P6SRRZ9ew1SBxIdf"
//	                );
//	                //set-up the client
//	                s3clientInput = AmazonS3ClientBuilder
//	                        .standard()
//	                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
//	                        .withRegion(Regions.US_EAST_1)
//	                        .build();
//	                s3client = s3clientInput;
//	            }
//	        }
//		}
//		return instance;
//	}
	
	/**
    * Tested
    * The method setup the environment for AWSS3Service class 
    * 	1. the bucket name for S3
    * 	2. local watched folder path
    *   3. S3 folder path
    *
    * @param bucketNameInput String variable as the S3 bucket name
    * @param watchFolderInput String variable as the watched folder path
    * @param s3targetinput String variable as the S3 folder path
    * @return nothing
    * @version 1.0
    * @since version 1.00
    */
	public void setEnvironment(String bucketNameInput, String watchFolderInput, String s3targetInput){
    	this.bucketName = bucketNameInput;
    	this.watchFolder = watchFolderInput;
    	this.s3target = s3targetInput; 
    }
    
	/**
    * Tested
    * The method check if the S3 bucket exist or not based on input  
    *
    * @param bucketNameInput String variable as the S3 bucket name
    * @return true/false
    * @version 1.0
    * @since version 1.00
    */
    public boolean doesBucketExist(String bucketName) { 
        return s3client.doesBucketExist(bucketName); 
    } 
    
    /**
    * Tested
    * The method creates a S3 bucket based on input  
    *
    * @param bucketNameInput String variable as the S3 bucket name
    * @return nothing
    * @version 1.0
    * @since version 1.00
    */
    public Bucket createBucket(String bucketName) { 
        return s3client.createBucket(bucketName); 
    } 

    /**
    * Tested
    * The method return a list of buckets  
    *
    * @return List object
    * @version 1.0
    * @since version 1.00
    */
    public List<Bucket> listBuckets() { 
        return s3client.listBuckets(); 
    }

    /**
    * Tested
    * The method delete a S3 bucket based on input  
    *
    * @param bucketNameInput String variable as the S3 bucket name
    * @return nothing
    * @version 1.0
    * @since version 1.00
    */
    public void deleteBucket(String bucketName) { 
        s3client.deleteBucket(bucketName); 
    }  
    
    /**
    * Tested
    * The method put file object in the s3 bucket a S3 bucket based on input  
    *
    * @param bucketNameInput String variable as the S3 bucket name
    * @param key String variable the key (in this case is the name) 
    * @param file File variable the file object to be placed there
    * @return nothing
    * @version 1.0
    * @since version 1.00
    */
    public PutObjectResult putObject(String bucketName, String key, File file) {
        return s3client.putObject(bucketName, key, file);
    }
    
    /**
    * Tested
    * The method return a ObjectList representing all the items in the designated s3 bucket 
    * 	based on the AWSS3Service's environment variable -- bucketName  
    *
    * @return ObjectList
    * @version 1.0
    * @since version 1.00
    */
    public ObjectListing listObjects() {
        return s3client.listObjects(bucketName);//bucketName + '/' + s3target);
    }
    
    /**
    * Tested
    * The method return a single S3Object item in the designated s3 bucket 
    * 	based on the input of bucketName, objectKey
    *
    * @param bucketNameInput String variable as the S3 bucket name
    * @param objectKey String variable (in this case is the name) 
    * @return S3Object
    * @version 1.0
    * @since version 1.00
    */
    public S3Object getObject(String bucketName, String objectKey) {
        return s3client.getObject(bucketName, objectKey);
    } 
    
    
    /**
    * Tested
    * The method delete a item in the designated s3 bucket 
    * 	based on the input of bucketName, objectKey
    *
    * @param bucketNameInput String variable as the S3 bucket name
    * @param objectKey String variable (in this case is the name) 
    * @return nothing
    * @version 1.0
    * @since version 1.00
    */
    public void deleteObject(String bucketName, String objectKey) {
        s3client.deleteObject(bucketName, objectKey);
    }
    
    /**
    * Tested
    * The method will upload a file (object->string->filename) in the local drive (per-set up in
    * setEnvionment method), to the s3 bucket 
    * 	that current AWSS3Service is pointing to 
    * 	based on input
    *
    * @param object Object variable as the file name
    * @return nothing
    * @version 1.0
    * @since version 1.00
    */
    public void uploadFile(Object object){
    	this.putObject(
           bucketName,
           s3target + object, 
            new File(watchFolder +"/" + object)
        );
    }
    
    /**
    *
    * The method will delete a file (object->string->filename) from the s3 bucket 
    * 	that current AWSS3Service is pointing to 
    * 	based on input
    *
    * @param object Object variable as the file name
    * @return nothing
    * @version 1.0
    * @since version 1.00
    */
    public void deleteFile(Object fileName){
    	this.deleteObject(bucketName, s3target + fileName);
    }    
}
