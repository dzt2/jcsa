package com.jcsa.jcmutest.project.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

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
	
	/** the maximal length of bytes that can be read to String **/
	public static final int MAX_READ_LENGTH = 1024 * 1024 * 32;
	
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
			while((length = reader.read(buff)) >= 0 && 
					buffer.length() <= MAX_READ_LENGTH) {
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
		if(file == null)
			throw new IllegalArgumentException("Invalid file: " + file);
		else if(file.exists()) {
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
			throw new IllegalArgumentException("Invalid source: " + source.getAbsolutePath());
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

	/**
	 * delete all the files under the directory
	 * @param dir
	 * @throws Exception
	 */
	public static void delete_in(File dir) throws Exception {
		if(dir == null || !dir.isDirectory()) {
			throw new IllegalArgumentException("Not directory");
		}
		else {
			File[] files = dir.listFiles();
			if(files != null) {
				for(File file : files) {
					delete(file);
				}
			}
		}
	}

	/**
	 * @param dir
	 * @return the list of files in the directory
	 * @throws Exception
	 */
	public static List<File> list_files(File dir) {
		List<File> flist = new ArrayList<>();
		File[] files = dir.listFiles();
		if(files != null) {
			for(File file : files) {
				flist.add(file);
			}
		}
		return flist;
	}

	/**
	 * copy all the files in source to the directory of target
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	public static void copy_all(File source, File target) throws Exception {
		if(source == null || !source.exists())
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			if(source.isDirectory()) {
				if(!target.exists()) {
					FileOperations.mkdir(target);
				}

				File[] sfiles = source.listFiles();
				if(sfiles != null) {
					for(File sfile : sfiles) {
						File tfile = new File(target.getAbsolutePath() + "/" + sfile.getName());
						copy_all(sfile, tfile);
					}
				}
			}
			else {
				copy(source, target);
			}
		}
	}

	/**
	 * whether two files are identical with their bytes
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public static boolean compare(File source, File target) throws Exception {
		if(source == null || target == null)
			throw new IllegalArgumentException("Invalid input as null");
		else if(!source.exists() || !target.exists()) {
			return !source.exists() && !target.exists();
		}
		else {
			return FileOperations.read(source).equals(FileOperations.read(target));
		}
	}

}
