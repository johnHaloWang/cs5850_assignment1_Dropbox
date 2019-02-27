package com.cs5850.programming.Assignment1.DropboxApp;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ){
        System.out.println( "Hello World!" );
        DirectoryWatcher watcher = new DirectoryWatcher();
        try {
        	//service.setEnvironment("cs5850-johnhalowang", input, "Document/");
			watcher.run("AKIAI5HRCERPEMP2PHCQ", "lUS8GJqvusbx47nwjSKn+s/5P6SRRZ9ew1SBxIdf", "cs5850-johnhalowang", "/Users/johnwang/Documents/fileRoot","Document/", 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
