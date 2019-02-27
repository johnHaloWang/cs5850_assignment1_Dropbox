/**
 * 
 */
package com.cs5850.programming.Assignment1.DropboxApp;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

//import org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.*;

import java.io.File;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.AmazonS3;

/**
 * @author johnhalowang
 *
 */


public class DirectoryWatcherTest {
	private AmazonS3 s3; 
	private AWSS3Service service;
	private static AWSCredentials credentials;
    private String DIRECTORY = "src/test/resources/DirectoryWatcherTestFolder";
    private File[] EXPECTED_FILE_LIST;
    private final String accesskey = "accesskey";
    private final String secretkey = "sercrekey";
    private static final String BUCKET_NAME = "bucket_name"; 
    private static final String KEY_NAME = "key_name"; 
    private static final String BUCKET_NAME2 = "bucket_name2";
    private static final String KEY_NAME2 = "key_name2";
    
	@Before 
    public void setUp() { 
        
        EXPECTED_FILE_LIST = new File[4];
        EXPECTED_FILE_LIST[0] = new File(DIRECTORY + "/" + "test.xml");
        EXPECTED_FILE_LIST[1] = new File(DIRECTORY + "/" + "employee.json");
        EXPECTED_FILE_LIST[2] = new File(DIRECTORY + "/" + "students.json");
        EXPECTED_FILE_LIST[3] = new File(DIRECTORY + "/" + "country.txt");
		credentials = new BasicAWSCredentials(
				accesskey, 
				secretkey
		);
		s3 = mock(AmazonS3.class);
		service = mock(AWSS3Service.class);
		//service = new AWSS3Service(s3);
		
    }
	
	@After
	public void cleanup() {
		System.out.println("Cleaning up the test env");
	}
	
	@Test
	public void testReturnFileList() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DirectoryWatcher watcher = new DirectoryWatcher();
		Method method = DirectoryWatcher.class.getDeclaredMethod("returnFileList", String.class);
		method.setAccessible(true);
		File[] result =  (File [])method.invoke(watcher, DIRECTORY);
		Arrays.sort(result);
		Arrays.sort(EXPECTED_FILE_LIST);
		assertThat(result, is(equalTo(EXPECTED_FILE_LIST))); 
	}
	
	@Test
	public void testSetCredentials() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException  {
		DirectoryWatcher watcher = new DirectoryWatcher();
		watcher.setCredentials(accesskey, secretkey);
		Field credentialsCheck = DirectoryWatcher.class.getDeclaredField("credentials");
		credentialsCheck.setAccessible(true);
		AWSCredentials fieldCredentials = (AWSCredentials) credentialsCheck.get(watcher);
		assertThat(fieldCredentials.getAWSAccessKeyId(), is(equalTo(credentials.getAWSAccessKeyId())));
		assertThat(fieldCredentials.getAWSSecretKey(), is(equalTo(credentials.getAWSSecretKey())));

	}
	
//	@Test
//	public void testSync() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException  {
//		DirectoryWatcher watcher = new DirectoryWatcher();
//		//service.setEnvironment(BUCKET_NAME, KEY_NAME, KEY_NAME2);		
//		//File file = mock(File.class); 
//		//set-up credentials
//		watcher.setCredentials("accesskey", "secretkey");
//				
////		        //set-up the client
////				AmazonS3 s3client = AmazonS3ClientBuilder
////		                .standard()
////		                .withCredentials(new AWSStaticCredentialsProvider(credentials))
////		                .withRegion(Regions.US_EAST_1)
////		                .build();
////				
//				//AWSS3Service 
//				//AWSS3Service service = new AWSS3Service(s3client);
//	    service.setEnvironment("bucketName", "localPath", "cloudFolder");
//		watcher.sync(DIRECTORY, service);
//		verify(watcher).sync(DIRECTORY, service);
//		 
//	}

}
