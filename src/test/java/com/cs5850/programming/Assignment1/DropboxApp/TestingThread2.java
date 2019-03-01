package com.cs5850.programming.Assignment1.DropboxApp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestingThread2 extends Thread{
	
	private String DIRECTORY;
	public void setDIRECTORY(String dir){
		DIRECTORY = dir;
	}
	public String getDIRECTORY(){
		return DIRECTORY;
	}
	@Override
	public void run() {
		System.out.println("MyThread - START " + Thread.currentThread().getName());

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
