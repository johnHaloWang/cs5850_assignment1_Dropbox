/**
 * 
 */
package com.cs5850.programming.Assignment1.DropboxApp;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

//import org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.*;

import java.io.File;
import java.io.IOException;

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

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
/**
 * @author johnhalowang
 *
 */


public class DirectoryWatcherTest {
	private AmazonS3 mockedS3; 
	private AWSS3Service mockedService;
	private static AWSCredentials credentials;
    private String DIRECTORY = "src/test/resources/DirectoryWatcherTestFolder";
    private File[] EXPECTED_FILE_LIST;
    private File[] EXPECTED_FILE_LIST2;
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
        
        EXPECTED_FILE_LIST2 = new File[4];
        EXPECTED_FILE_LIST2[0] = new File("test.xml");
        EXPECTED_FILE_LIST2[1] = new File("employee.json");
        EXPECTED_FILE_LIST2[2] = new File("students.json");
        EXPECTED_FILE_LIST2[3] = new File("country.txt");
		credentials = new BasicAWSCredentials(
				accesskey, 
				secretkey
		);
		
		//mockedS3 = mock(AmazonS3.class, RETURNS_DEEP_STUBS);
		mockedService = mock(AWSS3Service.class, RETURNS_DEEP_STUBS);
		//ObjectListing mockedListObject = mock(ObjectListing.class, RETURNS_DEEP_STUBS);
	    //when(mockedService.listObjects()).thenReturn(mockedListObject);
//	    S3ObjectSummary mockedS3ObjectSummary1 = mock(S3ObjectSummary.class, RETURNS_DEEP_STUBS);
//	    S3ObjectSummary mockedS3ObjectSummary2 = mock(S3ObjectSummary.class, RETURNS_DEEP_STUBS);
//	    List<S3ObjectSummary> mockedList = new LinkedList();
//	    mockedList.add(mockedS3ObjectSummary1);
//	    mockedList.add(mockedS3ObjectSummary2);
//	    when(mockedListObject.getObjectSummaries()).thenReturn(mockedList);
		
		
		
	    
    }
	
	@After
	public void cleanup() {
		System.out.println("Cleaning up the test env");
		
	}
	@Test
	public void testRun() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException, InterruptedException  {
		System.out.println("testRun");
		DirectoryWatcher watcher = new DirectoryWatcher();
		watcher.run("AKIAI5HRCERPEMP2PHCQ", 
    		    "lUS8GJqvusbx47nwjSKn+s/5P6SRRZ9ew1SBxIdf", 
    		    "cs5850-johnhalowang", 
    		    "/Users/johnwang/Documents/fileRoot",
    		    "Document/", 
    		    1);
		
	}
	
	
	@Test
	public void testRunWatchAddDelete() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException, InterruptedException  {
		System.out.println("testRunWatch");
		DirectoryWatcher watcher = new DirectoryWatcher();
		File[] list = watcher.returnFileList(this.DIRECTORY);
		for(File i: list){
			i.delete();
		}
		for(File i: EXPECTED_FILE_LIST){
			i.createNewFile();
		}
		TestingThread t1 = new TestingThread();
		
		//TestingThread2 t2 = new TestingThread2();
		Thread.sleep(20);
		t1.start();

		
		watcher.runWatch(DIRECTORY, mockedService, 1);
		
		verify(mockedService).uploadFile("newFile1.txt");
		verify(mockedService).deleteFile("students.json");
	}
	
	@Test
	public void testSync() throws InterruptedException, IOException{
		
		ObjectListing mockedListObject = mock(ObjectListing.class, RETURNS_DEEP_STUBS);
	    when(mockedService.listObjects()).thenReturn(mockedListObject);
	    DirectoryWatcher watcher = new DirectoryWatcher();
	    File[] list = watcher.returnFileList(this.DIRECTORY);
		for(File i: list){
			i.delete();
		}
		for(File i: EXPECTED_FILE_LIST){
			i.createNewFile();
		}
	    TestingThread t2 = new TestingThread();
	    t2.start();
	    Thread.sleep(30);
	    watcher.sync(DIRECTORY, mockedService);
		verify(mockedListObject).getObjectSummaries();
		verify(mockedService).uploadFile("test.xml");
		verify(mockedService).uploadFile("country.txt");
		verify(mockedService).uploadFile("employee.json");

		

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
	
	@Test
	public void contextLoads() {
	}
	


}
