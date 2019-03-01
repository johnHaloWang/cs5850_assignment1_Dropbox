package com.cs5850.programming.Assignment1.DropboxApp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TestingThread2 extends Thread{
	private String DIRECTORY = "src/test/resources/DirectoryWatcherTestFolder";
	@Override
	public void run() {
		System.out.println("MyThread - START " + Thread.currentThread().getName());
		ArrayList<String> lines = new ArrayList<String>();
		String line = null;

		try {
			Thread.sleep(200);
			String str = "World";
		    BufferedWriter writer = new BufferedWriter(new FileWriter(DIRECTORY + "/" + "country.txt", true));
		    writer.append(' ');
		    writer.append(str);		     
		    writer.close();
		    System.out.println("done");

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
