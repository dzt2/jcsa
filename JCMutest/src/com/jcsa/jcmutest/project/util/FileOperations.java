package com.jcsa.jcmutest.project.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * It provides general interfaces to manage the operations on file system.
 * 
 * @author yukimula
 *
 */
public class FileOperations {
	
	/**
	 * @param dir
	 * @return true if the directory is or has been created. 
	 * @throws Exception
	 */
	public static boolean mkdir(File dir) throws Exception {
		if(dir == null) {
			throw new IllegalArgumentException("Null directory");
		}
		else if(dir.exists()) {
			return dir.isDirectory();
		}
		else {
			dir.mkdir();
			while(!dir.exists());
			return true;
		}
	}
	
	/**
	 * @param file
	 * @param text
	 * @return true if the text is written on the specified file
	 * @throws Exception
	 */
	public static boolean write(File file, String text) throws Exception {
		if(file == null) {
			throw new IllegalArgumentException("Invalid file: null");
		}
		else {
			FileWriter writer = new FileWriter(file);
			writer.write(text); writer.close();
			return true;
		}
	}
	
	/**
	 * @param file
	 * @return read the text in the input file
	 * @throws Exception
	 */
	public static String read(File file) throws Exception {
		if(file == null || !file.exists()) {
			throw new IllegalArgumentException("Invalid file: null");
		}
		else {
			FileReader reader = new FileReader(file);
			StringBuilder buffer = new StringBuilder();
			char[] buff = new char[1024]; int length;
			while((length = reader.read(buff)) >= 0) {
				buffer.append(buff, 0, length);
			}
			reader.close();
			return buffer.toString();
		}
	}
	
	/**
	 * delete the file if the file is non-directory;
	 * or delete all the files including itself if it is a directory.
	 * @param file
	 * @param remain
	 * @throws Exception
	 */
	public static void delete(File file) throws Exception {
		if(file == null || !file.exists())
			throw new IllegalArgumentException("Invalid file: null");
		else {
			if(file.isDirectory()) {
				File[] children = file.listFiles();
				if(children != null) {
					for(File child : children) {
						delete(child);
					}
				}
			}
			file.delete();
			while(file.exists());	// wait until file is deleted
		}
	}
	
	/**
	 * copy the source file to the target file
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	public static void copy(File source, File target) throws Exception {
		if(source == null || !source.exists())
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			FileInputStream in = new FileInputStream(source);
			FileOutputStream ou = new FileOutputStream(target);
			byte[] buffer = new byte[1024 * 16]; int length;
			while((length = in.read(buffer)) >= 0) {
				ou.write(buffer, 0, length);
			}
			in.close(); ou.close();
		}
	}
	
}
