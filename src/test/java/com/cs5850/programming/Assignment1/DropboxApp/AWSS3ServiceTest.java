/**
 * 
 */
package com.cs5850.programming.Assignment1.DropboxApp;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

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
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary; 

/**
 * @author johnhalowang
 *
 */
public class AWSS3ServiceTest {
	//@InjectMocks
	private AmazonS3 mockedS3; 
	//@Mock
    private AWSS3Service mockedService;
    
    private static final String BUCKET_NAME = "bucket_name"; 
    private static final String KEY_NAME = "key_name"; 
    private static final String BUCKET_NAME2 = "bucket_name2"; 
    private static final String KEY_NAME2 = "key_name2"; 
    @Before 
    public void setUp() { 
        mockedS3 = mock(AmazonS3.class);
        mockedService = new AWSS3Service(mockedS3);
        ObjectListing mockedListObject = mock(ObjectListing.class, RETURNS_DEEP_STUBS);
        when(mockedService.listObjects()).thenReturn(mockedListObject);
//        S3ObjectSummary mockedS3ObjectSummary = mock(S3ObjectSummary.class, RETURNS_DEEP_STUBS);
//        when(mockedListObject.getObjectSummaries()).thenReturn(mockedS3ObjectSummary);
    }
    @After
	public void cleanup() {
		System.out.println("Cleaning up the test env");
	}
    @Test 
    public void testEnv() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException { 
    	mockedService.setEnvironment(BUCKET_NAME, KEY_NAME, KEY_NAME2);
    	Field privateStringField1 = AWSS3Service.class.getDeclaredField("bucketName");
    	privateStringField1.setAccessible(true);
    	String fieldValue1 = (String) privateStringField1.get(mockedService);
    	assertSame(fieldValue1, BUCKET_NAME);
    	Field privateStringField2 = AWSS3Service.class.getDeclaredField("watchFolder");
    	privateStringField2.setAccessible(true);
    	String fieldValue2 = (String) privateStringField2.get(mockedService);
    	assertSame(fieldValue2, KEY_NAME);
    	Field privateStringField3 = AWSS3Service.class.getDeclaredField("s3target");
    	privateStringField3.setAccessible(true);
    	String fieldValue3 = (String) privateStringField3.get(mockedService);
    	assertSame(fieldValue3, KEY_NAME2);

    } 
    
    @Test 
    public void whenInitializingAWSS3Service_thenNotNull() { 
        assertNotNull(new AWSS3Service()); 
    } 
	    
	@Test
	public void testS3BucketExit(){	
		mockedService.doesBucketExist(BUCKET_NAME);
		verify(mockedS3).doesBucketExist(BUCKET_NAME);
	}
	
	@Test 
    public void testCreationOfS3Bucket() { 
		mockedService.createBucket(BUCKET_NAME); 
        verify(mockedS3).createBucket(BUCKET_NAME); 
    } 
	
	 @Test 
	    public void whenDeletingBucket_thenCorrect() { 
	        mockedService.deleteBucket(BUCKET_NAME); 
	        verify(mockedS3).deleteBucket(BUCKET_NAME); 
	    } 
	    
    @Test 
    public void whenVerifyingPutObject_thenCorrect() { 
        File file = mock(File.class); 
        PutObjectResult result = mock(PutObjectResult.class); 
        when(mockedS3.putObject(BUCKET_NAME, KEY_NAME, file)).thenReturn(result); 
 
        assertEquals(mockedService.putObject(BUCKET_NAME, KEY_NAME, file), result); 
        verify(mockedS3).putObject(BUCKET_NAME, KEY_NAME, file); 
    }
    
    @Test 
    public void whenVerifyingListObjects_thenCorrect() { 
    	mockedService.setEnvironment(BUCKET_NAME, KEY_NAME, KEY_NAME2);
        mockedService.listObjects(); 
        verify(mockedS3).listObjects(BUCKET_NAME); 
    } 
    
    @Test 
    public void whenVerifyingGetObject_thenCorrect() { 
        mockedService.getObject(BUCKET_NAME, KEY_NAME); 
        verify(mockedS3).getObject(BUCKET_NAME, KEY_NAME); 
    } 
    
    
    @Test 
    public void whenVerifyingDeleteObject_thenCorrect() { 
        mockedService.deleteObject(BUCKET_NAME, KEY_NAME); 
        verify(mockedS3).deleteObject(BUCKET_NAME, KEY_NAME); 
    }
    
    @Test 
    public void whenVerifyingDeleteFile_thenCorrect() { 
    	mockedService.setEnvironment(BUCKET_NAME, KEY_NAME, KEY_NAME2);
        mockedService.deleteFile(KEY_NAME); 
        verify(mockedS3).deleteObject(BUCKET_NAME, KEY_NAME2 + KEY_NAME); 
    }
    
    @Test 
    public void whenVerifyingUploadFile_thenCorrect() { 
    	File file = mock(File.class); 
        PutObjectResult result = mock(PutObjectResult.class); 
        when(mockedS3.putObject(BUCKET_NAME, KEY_NAME, file)).thenReturn(result); 
    	mockedService.setEnvironment(BUCKET_NAME, KEY_NAME, KEY_NAME2);
        mockedService.uploadFile(KEY_NAME); 
        assertEquals(mockedService.putObject(BUCKET_NAME, KEY_NAME, file), result); 
        verify(mockedS3).putObject(BUCKET_NAME, KEY_NAME, file); 
    }
//    @Test
//    public void whenVerifyingDownloadFile_thenCorrect() throws IOException { 
//    	//return s3client.getObject(bucketName, objectKey);
////    	S3Object s3object = getObject(bucketName, s3target + fileName);
////        S3ObjectInputStream inputStream = s3object.getObjectContent();
////        FileUtils.copyInputStreamToFile(inputStream, new File(watchFolder + fileName));
//
//    	S3Object mockedS3object = mock(S3Object.class, RETURNS_DEEP_STUBS);
//        when(mockedS3.getObject(BUCKET_NAME, KEY_NAME2)).thenReturn(mockedS3object);
//        S3Object mockedS3object2 = mock(S3Object.class, RETURNS_DEEP_STUBS);
//        when(mockedService.getObject(BUCKET_NAME, KEY_NAME2)).thenReturn(mockedS3object2);
////        S3ObjectInputStream mockedInputStream = mock(S3ObjectInputStream.class, RETURNS_DEEP_STUBS);
////        when(mockedS3object.getObjectContent()).thenReturn(mockedInputStream);
//        
//        mockedService.setEnvironment(BUCKET_NAME, KEY_NAME, KEY_NAME2);
//        mockedService.downloadFile(KEY_NAME); 
//    	//verify(mockedS3).getObject(BUCKET_NAME, KEY_NAME);
//    	//verify(mockedS3object).getObjectContent();
//    }
    
    @Test
    public void whenVerifyListBuckets_thenCorrect() { 
    	List<Bucket> mockedBucket = new LinkedList();
    	mockedBucket.add(new Bucket("A"));
    	mockedBucket.add(new Bucket("B"));
    	mockedBucket.add(new Bucket("C"));
    	
        when(mockedS3.listBuckets()).thenReturn(mockedBucket);
        mockedService.listBuckets();
    	verify(mockedS3).listBuckets();
    }
}
