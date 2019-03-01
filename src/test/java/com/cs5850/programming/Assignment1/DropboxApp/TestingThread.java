package com.cs5850.programming.Assignment1.DropboxApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class TestingThread extends Thread {
	private String DIRECTORY = "src/test/resources/DirectoryWatcherTestFolder";

	@Override
	public void run() {
		System.out.println("MyThread - START " + Thread.currentThread().getName());
		ArrayList<String> lines = new ArrayList<String>();
		String line = null;

		try {
			Thread.sleep(20);
			java.io.File file1 = new java.io.File(DIRECTORY + "/" + "newFile1.txt");
			file1.createNewFile();
			Thread.sleep(20);
			java.io.File file2 = new java.io.File(DIRECTORY + "/" + "students.json");
			file2.delete();
//			Thread.sleep(60);
//			String str = "World";
//		    BufferedWriter writer = new BufferedWriter(new FileWriter(DIRECTORY + "/" + "country.txt", true));
//		    writer.append(' ');
//		    writer.append(str);		     
//		    writer.close();
//		    System.out.println("done");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("MyThread - END " + Thread.currentThread().getName());
	}

}
