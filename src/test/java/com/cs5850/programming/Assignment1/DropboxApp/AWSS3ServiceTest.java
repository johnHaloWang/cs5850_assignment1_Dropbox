/**
 * 
 */
package com.cs5850.programming.Assignment1.DropboxApp;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;


/**
 * @author johnhalowang
 *
 */
public class AWSS3ServiceTest {

	private AmazonS3 mockedS3; 
    private AWSS3Service mockedService;
    private static final String BUCKET_NAME = "bucket_name"; 
    private static final String KEY_NAME = "key_name"; 
    
    @Before 
    public void setUp() { 
        mockedS3 = mock(AmazonS3.class);
        mockedService = new AWSS3Service(mockedS3);
        ObjectListing mockedListObject = mock(ObjectListing.class, RETURNS_DEEP_STUBS);
        when(mockedService.listObjects()).thenReturn(mockedListObject);
    }
    
    @After
	public void cleanup() {
		System.out.println("Cleaning up the test env");
	}
    
    @Test 
    public void testEnv() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException { 
    	mockedService.setEnvironment(BUCKET_NAME, KEY_NAME);
    	Field privateStringField1 = AWSS3Service.class.getDeclaredField("bucketName");
    	privateStringField1.setAccessible(true);
    	String fieldValue1 = (String) privateStringField1.get(mockedService);
    	assertSame(fieldValue1, BUCKET_NAME);
    	Field privateStringField2 = AWSS3Service.class.getDeclaredField("watchFolder");
    	privateStringField2.setAccessible(true);
    	String fieldValue2 = (String) privateStringField2.get(mockedService);
    	assertSame(fieldValue2, KEY_NAME);
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
    	mockedService.setEnvironment(BUCKET_NAME, KEY_NAME);
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
    	mockedService.setEnvironment(BUCKET_NAME, KEY_NAME);
        mockedService.deleteFile(KEY_NAME); 
        verify(mockedS3).deleteObject(BUCKET_NAME, KEY_NAME); 
    }
    
    @Test 
    public void whenVerifyingUploadFile_thenCorrect() { 
    	File file = mock(File.class); 
        PutObjectResult result = mock(PutObjectResult.class); 
        when(mockedS3.putObject(BUCKET_NAME, KEY_NAME, file)).thenReturn(result); 
    	mockedService.setEnvironment(BUCKET_NAME, KEY_NAME);
        mockedService.uploadFile(KEY_NAME); 
        assertEquals(mockedService.putObject(BUCKET_NAME, KEY_NAME, file), result); 
        verify(mockedS3).putObject(BUCKET_NAME, KEY_NAME, file); 
    }
    
    @Test
    public void whenVerifyListBuckets_thenCorrect() { 
    	List<Bucket> mockedBucket = new LinkedList<Bucket>();
    	mockedBucket.add(new Bucket("A"));
    	mockedBucket.add(new Bucket("B"));
    	mockedBucket.add(new Bucket("C"));
    	
        when(mockedS3.listBuckets()).thenReturn(mockedBucket);
        mockedService.listBuckets();
    	verify(mockedS3).listBuckets();
    }
}
