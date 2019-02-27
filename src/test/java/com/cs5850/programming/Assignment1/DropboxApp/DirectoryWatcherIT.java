/**
 * 
 */
package com.cs5850.programming.Assignment1.DropboxApp;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.experimental.categories.Category;
import org.junit.runners.model.TestTimedOutException;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author johnwang
 *
 */
@Category(IntegrationTest.class)
public class DirectoryWatcherIT {

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

	public void test() throws IOException, InterruptedException {		
		System.out.println( "Integration Test" );
	    DirectoryWatcher watcher = new DirectoryWatcher();
	    watcher.run("AKIAI5HRCERPEMP2PHCQ", "lUS8GJqvusbx47nwjSKn+s/5P6SRRZ9ew1SBxIdf", "cs5850-johnhalowang", "/Users/johnwang/Documents/fileRoot","Document/", 1);

	}

}
