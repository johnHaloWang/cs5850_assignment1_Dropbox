/**
 * 
 */
package com.cs5850.programming.Assignment1.DropboxApp;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author johnwang
 *
 */

public class DirectoryWatcherIT {
    private final String DIRECTORY = "src/test/resources/DirectoryWatcherTestFolder";
    private final String BUCKET_NAME = "cs5850-johnhalowang";
    private final String CLOUD_FOLDER = "Document/";
    private final int WATCH_FOR_IN_MIN = 3;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void test() throws IOException, InterruptedException, ParseException {		
		System.out.println( "Integration Test......." );
	    DirectoryWatcher watcher = new DirectoryWatcher();
		watcher.run(BUCKET_NAME, DIRECTORY, CLOUD_FOLDER, WATCH_FOR_IN_MIN);

	}

}
