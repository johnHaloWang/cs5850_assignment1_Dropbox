/**
 * 
 */
package com.cs5850.programming.Assignment1.DropboxApp;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

//import org.junit.Assert.*;
import java.lang.reflect.Field;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * @author johnhalowang
 *
 */


public class DirectoryWatcherTest {
	
	private AWSS3Service mockedService;
	private DirectoryWatcher watcher;
	private static AWSCredentials credentials;
	private String DIRECTORY = "src/test/resources/DirectoryWatcherTestFolder";
	private File[] EXPECTED_FILE_LIST;
	private File[] EXPECTED_FILE_LIST2;
	private final String accesskey = "accesskey";
	private final String secretkey = "sercrekey";
        
	@Before
	public void setUp() throws IOException {
		
		System.out.println("** DirectoryWatcherTest Setup **");
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
		credentials = new BasicAWSCredentials(accesskey, secretkey);
		mockedService = mock(AWSS3Service.class, RETURNS_DEEP_STUBS);
		watcher = new DirectoryWatcher();
		File[] list = watcher.returnFileList(this.DIRECTORY);
		for (File i : list) {
			i.delete();
		}
		for (File i : EXPECTED_FILE_LIST) {
			i.createNewFile();
		}
	}
	
	@After
	public void cleanup() {
		System.out.println("** Cleaning up the test env **");	
	}
	
	@Test
	public void testReadAWSSetting() throws FileNotFoundException, IOException, ParseException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {

		System.out.println("** Fire testReadAWSSetting **");
		watcher.readAWSSetting();
		Field credentialsCheck = DirectoryWatcher.class.getDeclaredField("credentials");
		credentialsCheck.setAccessible(true);
		Field s3clientCheck = DirectoryWatcher.class.getDeclaredField("s3client");
		s3clientCheck.setAccessible(true);
		AWSCredentials fieldCredentials = (AWSCredentials) credentialsCheck.get(watcher);
		assertNotNull(fieldCredentials.getAWSAccessKeyId());
		assertNotNull(fieldCredentials.getAWSSecretKey());
		assertNotNull(s3clientCheck);
		System.out.println("** complete testReadAWSSetting **");
	}
	
	@Test
	public void testRun() throws IOException, InterruptedException, ParseException   {
		
		System.out.println("** Fire testRun **");
		TestingThread t1 = new TestingThread();
		Thread.sleep(30);
		t1.setDIRECTORY(DIRECTORY);
		t1.start();
		watcher.run("cs5850-johnhalowang", DIRECTORY, "Document/", 1);
		/** set another s3 for testing result **/
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader("./.aws/Credentials.json"));
		JSONObject jsonObject = (JSONObject) obj;
		String accessKeyId = (String) jsonObject.get("accessKeyId");
		String secretAccessKey = (String) jsonObject.get("secretAccessKey");
		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(
						new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey)))
				.withRegion(Regions.US_EAST_1).build();
		AWSS3Service service = new AWSS3Service(s3client);
		service.setEnvironment("cs5850-johnhalowang", DIRECTORY);
		
		File[] listOfLocal  = watcher.returnFileList(DIRECTORY);
		ObjectListing listOfCloud = s3client.listObjects("cs5850-johnhalowang");//service.listObjects();
		System.out.println(listOfCloud.getObjectSummaries().size());
		System.out.println(listOfLocal.length);
		for (S3ObjectSummary os : listOfCloud.getObjectSummaries()) {
			String[] data = os.getKey().split("/");
			System.out.println(data[data.length - 1]);
		}
		//assertThat(listOfLocal.length, is(equalTo(listOfCloud.getObjectSummaries().size())));
		System.out.println("** Complete testFun **");
	}
	
	@Test
	public void testRunWatchAddDelete() throws IOException, InterruptedException {

		System.out.println("testRunWatch");
		TestingThread t2 = new TestingThread();
		Thread.sleep(20);
		t2.setDIRECTORY(DIRECTORY);
		t2.start();
		watcher.runWatch(DIRECTORY, mockedService, 1);
		verify(mockedService).uploadFile("newFile1.txt");
		verify(mockedService).deleteFile("students.json");
	}
	
	@Test
	public void testSync() throws InterruptedException, IOException {
		ObjectListing mockedListObject = mock(ObjectListing.class, RETURNS_DEEP_STUBS);
		when(mockedService.listObjects()).thenReturn(mockedListObject);
		TestingThread t2 = new TestingThread();
		t2.setDIRECTORY(DIRECTORY);
		t2.start();
		Thread.sleep(30);
		watcher.sync(DIRECTORY, mockedService);
		verify(mockedListObject).getObjectSummaries();
		verify(mockedService).uploadFile("test.xml");
		verify(mockedService).uploadFile("country.txt");
		verify(mockedService).uploadFile("employee.json");
	}

	@Test
	public void testSetCredentials()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
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
