package com.cs5850.programming.Assignment1.DropboxApp;

import java.io.IOException;

public class TestingThread extends Thread {

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
			Thread.sleep(20);
			java.io.File file1 = new java.io.File(DIRECTORY + "/" + "newFile1.txt");
			file1.createNewFile();
			Thread.sleep(20);
			java.io.File file2 = new java.io.File(DIRECTORY + "/" + "students.json");
			file2.delete();
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
