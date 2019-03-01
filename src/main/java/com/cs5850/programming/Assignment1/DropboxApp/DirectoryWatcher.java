/**
 * 
 */
package com.cs5850.programming.Assignment1.DropboxApp;

import java.io.IOException;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeUnit;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
//import org.apache.commons.io.FileUtils;

import com.amazonaws.regions.Regions;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
//import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

/**
 * Local folder watch and sync with AWS S3 utility
 * @author johnhalowang
 * @version 1.0
 * @since 1.0
 */
public class DirectoryWatcher {
	
	private AWSCredentials credentials;
	//private AWSS3Service service;
	//private AmazonS3 s3client;

	/**
	 * 
	 * Main method that runs when the program is started.
	 * 
	 * @param args command-line arguments
	 * @return nothing
	 * @throws IOException, InterruptedException
	 * @version 1.0
	 * @since version 1.00
	 * "AKIAI5HRCERPEMP2PHCQ", "lUS8GJqvusbx47nwjSKn+s/5P6SRRZ9ew1SBxIdf"
	 * String accesskey, String secretkey
	 */
	public void run(String accesskey, 
					String secretkey, 
					String bucketName, 
					String localPath, 
					String cloudFolder,
					int runDurationInMin) throws IOException, InterruptedException{
	

	    //set-up credentials
		setCredentials(accesskey, secretkey);
		
        //set-up the client
		AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
		AWSS3Service service = new AWSS3Service(s3client);
	    service.setEnvironment(bucketName, localPath, cloudFolder);
		sync(localPath, service);
		this.runWatch(localPath, service, 1);
		return;
	}


    
	/**
    * Tested
    * The method set-up the credentials 
    *
    * @param accesskey, String variable for accesskey for AWS account
    * @param secretkey, String variable for secretkey for AWS account
    * @return nothing
    * @version 1.0
    * @since version 1.00
    */
	public void setCredentials(String accesskey, String secretkey){
		credentials = new BasicAWSCredentials(
				accesskey, 
				secretkey
		);
	}
	
	/**
    * 
    * The method watches a local folder and if changes happened, 
    * 	the change will apply to the cloud S3 folder
    *
    * @param watchFolder, String variable as the local folder path 
    * @return nothing
    * @throws IOException, InterruptedException
    * @version 1.0
    * @since version 1.00
    */
	public void runWatch(String watchFolder, AWSS3Service service, int runTimeInMin) throws IOException, InterruptedException{
		System.out.println("fire runWatch function");
		WatchService watchService = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(watchFolder);
        path.register(watchService, 
        		StandardWatchEventKinds.ENTRY_CREATE, 
        		StandardWatchEventKinds.ENTRY_DELETE, 
        		StandardWatchEventKinds.ENTRY_MODIFY);
   
        long startTime = System.currentTimeMillis();
        long endTime;
        WatchKey key = watchService.poll(runTimeInMin, TimeUnit.MINUTES);
        while (key != null) {        	
        	List<WatchEvent<?>>watchEvents = key.pollEvents();
            for (WatchEvent<?> event : watchEvents) {
                if(event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                    System.out.println("Event kind:" + event.kind() 
                    + ", file: " + event.context());                   
                    service.uploadFile(event.context().toString());
                 }
                 else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    System.out.println("Event kind:" + event.kind() 
                    + ", file: " + event.context());
                    service.deleteFile(event.context().toString());  
                 }
                 else if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    System.out.println("Event kind:" + event.kind() 
                    + ", file: " + event.context());
                    service.uploadFile(event.context().toString());
                }
            }
            endTime = System.currentTimeMillis();
            key.reset();
            if((endTime-startTime)/6000 > runTimeInMin)
            	break;
            	
         }
        watchService.close();
	}
		
	/**
    * Tested
    * The method return the File array from the local watch folder  
    *
    * @param input, String variable as the local folder path 
    * @return File object array
    * @version 1.0
    * @since version 1.00
    */
	public File[] returnFileList(String input){
		System.out.println("fire returnFileList function");
		File folder = new File(input);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}
	
	/**
    * Tested
    * The method sync local watch folder and S3 cloud bucket folder 
    *
    * @param input, String variable as the local folder path
    * @return nothing
    * @version 1.0
    * @since version 1.00
    */
	public boolean sync(String input, AWSS3Service service){
		
		System.out.println("fire sync function");
		//get the File list from the local watched folder
    	File[] listOfLocal = returnFileList(input);
    	//get the File list from the S3 cloud
    	ObjectListing listOfCloud = service.listObjects();
    	//create a hash map used file name as the key, last modified time as value0
    	HashMap<String, Date> h = new HashMap<String, Date>();
    	//insert all file name and last modified to the h -- hash map
    	for(S3ObjectSummary os : listOfCloud.getObjectSummaries()) {
    		String [] data = os.getKey().split("/");
    		//String fileName = data[data.length-1];
            h.put(data[data.length-1], os.getLastModified());
    	}
    	
		/* go through the local folder's file element 
		 * if it exists in map meaning it found it, 
		 * 		then remove from the map 
		 * 		further compare the last modified data is different then upload the local version
		 * (else) if it is not in map , 
		 * 		need to upload cloud, 
		 * 		do nothing to the map    
		 * 
		 * note: this version does consider the problem that if the object is directory, 
		 * 		but the author considers that will not happened in usage
		 * 	
		 */
		for (int i = 0; i < listOfLocal.length; i++) {	
			  if (listOfLocal[i].isFile()) {
				  //if it exists in map meaning it is find. remove from map
				  if(h.containsKey(listOfLocal[i].getName())){
					  Date c = new Date(listOfLocal[i].lastModified() * 1000);
					  if( c.equals(h.get(listOfLocal[i].getName()))== false){
						  service.uploadFile(listOfLocal[i].getName());
					  }
					  h.remove(listOfLocal[i].getName());
				  }
				  //if it does not exist, it means that the item needs need to be added. do nothing to map
				  else
					  service.uploadFile(listOfLocal[i].getName());
			  }
		}
		
		/*
		 * the rest of the item in map -- means does not exists in the local watched folder,
		 * it will be removed 
		 */		
		for (String key : h.keySet()) { service.deleteFile(key); }
		return true;
	}
	/**
	 * 
	 * The default constructor for class DirectoryWatcher
	 *
	 * 
	 * @version 1.0
	 * @since version 1.00
	 */
	public DirectoryWatcher() {

	}

}
