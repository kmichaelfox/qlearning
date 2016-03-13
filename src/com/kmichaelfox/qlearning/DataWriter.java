package com.kmichaelfox.qlearning;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class DataWriter {
	BufferedReader in;
	PrintStream out;
	
	public DataWriter(String filename) {
		try {
		      //create a buffered reader that connects to the console, we use it so we can read lines
		      in = new BufferedReader(new InputStreamReader(System.in));

		      //create an print writer for writing to a file
		      out = new PrintStream(new FileOutputStream(filename));
		   }
		      catch(IOException e1) {
		        System.out.println("Error during reading/writing");
		   }
	}
	
	public void print(char c) {
		out.print(c);
	}
	
	public void print(String output) {
		out.print(output);
	}
	
	public void println(char c) {
		out.println(c);
	}
	
	public void println(String output) {
		out.println(output);
	}
	
	public void println() {
		out.println();
	}
	
	public void closeFile() {
		out.close();
	}
	
}
