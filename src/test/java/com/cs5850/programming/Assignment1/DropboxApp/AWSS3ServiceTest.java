/**
 * 
 */
package com.cs5850.programming.Assignment1.DropboxApp;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream; 

/**
 * @author johnhalowang
 *
 */
public class AWSS3ServiceTest {
	//@InjectMocks
	private AmazonS3 s3; 
	//@Mock
    private AWSS3Service service;
    
    private static final String BUCKET_NAME = "bucket_name"; 
    private static final String KEY_NAME = "key_name"; 
    private static final String BUCKET_NAME2 = "bucket_name2"; 
    private static final String KEY_NAME2 = "key_name2"; 
    @Before 
    public void setUp() { 
        s3 = mock(AmazonS3.class);
        service = new AWSS3Service(s3);
    }
    @After
	public void cleanup() {
		System.out.println("Cleaning up the test env");
	}
    @Test 
    public void testEnv() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException { 
    	service.setEnvironment(BUCKET_NAME, KEY_NAME, KEY_NAME2);
    	Field privateStringField1 = AWSS3Service.class.getDeclaredField("bucketName");
    	privateStringField1.setAccessible(true);
    	String fieldValue1 = (String) privateStringField1.get(service);
    	assertSame(fieldValue1, BUCKET_NAME);
    	Field privateStringField2 = AWSS3Service.class.getDeclaredField("watchFolder");
    	privateStringField2.setAccessible(true);
    	String fieldValue2 = (String) privateStringField2.get(service);
    	assertSame(fieldValue2, KEY_NAME);
    	Field privateStringField3 = AWSS3Service.class.getDeclaredField("s3target");
    	privateStringField3.setAccessible(true);
    	String fieldValue3 = (String) privateStringField3.get(service);
    	assertSame(fieldValue3, KEY_NAME2);

    } 
    
    @Test 
    public void whenInitializingAWSS3Service_thenNotNull() { 
        assertNotNull(new AWSS3Service()); 
    } 
	    
	@Test
	public void testS3BucketExit(){	
		service.doesBucketExist(BUCKET_NAME);
		verify(s3).doesBucketExist(BUCKET_NAME);
	}
	
	@Test 
    public void testCreationOfS3Bucket() { 
		service.createBucket(BUCKET_NAME); 
        verify(s3).createBucket(BUCKET_NAME); 
    } 
	
	@Test 
    public void whenVerifyingListBuckets_thenCorrect() { 
        service.listBuckets(); 
        verify(s3).listBuckets(); 
    }
	 @Test 
	    public void whenDeletingBucket_thenCorrect() { 
	        service.deleteBucket(BUCKET_NAME); 
	        verify(s3).deleteBucket(BUCKET_NAME); 
	    } 
	    
    @Test 
    public void whenVerifyingPutObject_thenCorrect() { 
        File file = mock(File.class); 
        PutObjectResult result = mock(PutObjectResult.class); 
        when(s3.putObject(BUCKET_NAME, KEY_NAME, file)).thenReturn(result); 
 
        assertEquals(service.putObject(BUCKET_NAME, KEY_NAME, file), result); 
        verify(s3).putObject(BUCKET_NAME, KEY_NAME, file); 
    }
    
    @Test 
    public void whenVerifyingListObjects_thenCorrect() { 
    	service.setEnvironment(BUCKET_NAME, KEY_NAME, KEY_NAME2);
        service.listObjects(); 
        verify(s3).listObjects(BUCKET_NAME); 
    } 
    
    @Test 
    public void whenVerifyingGetObject_thenCorrect() { 
        service.getObject(BUCKET_NAME, KEY_NAME); 
        verify(s3).getObject(BUCKET_NAME, KEY_NAME); 
    } 
    
    
    @Test 
    public void whenVerifyingDeleteObject_thenCorrect() { 
        service.deleteObject(BUCKET_NAME, KEY_NAME); 
        verify(s3).deleteObject(BUCKET_NAME, KEY_NAME); 
    }
//    public void uploadFile(Object object){
//    	this.putObject(
//           bucketName,
//           s3target + object, 
//            new File(watchFolder +"/" + object)
//        );
//    }
    @Test 
    public void whenUpLoad_thenCorrect() { 
    	service.setEnvironment(BUCKET_NAME, KEY_NAME, KEY_NAME2);
        service.uploadFile(KEY_NAME); 
        //verify(service).putObject(BUCKET_NAME, KEY_NAME2 + KEY_NAME, new File(KEY_NAME + '/' + KEY_NAME)); 
    }
//    public void downloadFile(Object fileName) throws IOException{
//    	S3Object s3object = this.getObject(bucketName, s3target + fileName);
//        S3ObjectInputStream inputStream = s3object.getObjectContent();
//        FileUtils.copyInputStreamToFile(inputStream, new File(watchFolder + fileName));
//    }
    @Test 
    public void whenDownLoad_thenCorrect() throws IOException{ 
    	//public void setEnvironment(String bucketNameInput, String watchFolderInput, String s3targetInput){
    	//service.setEnvironment(BUCKET_NAME, KEY_NAME, KEY_NAME2);
        service.downloadFile(KEY_NAME); 
        //verify(service).getObject(BUCKET_NAME, KEY_NAME2 + KEY_NAME); 
    }
}
