package __backup__;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

public class FileProcess {
	
	private static final StringBuilder buff = new StringBuilder();
	
	/**
	 * get the prefix of directory where file is defined 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String path_of(File file) throws Exception {
		if(file == null)
			throw new IllegalArgumentException("Invalid file: null");
		else {
			String path = file.getAbsolutePath();
			
			int index = path.lastIndexOf('\\');
			if(index < 0) index = path.lastIndexOf('/');
			
			if(index < 0) path = path.trim();
			else path = path.substring(0, index).trim();
			
			return path;
		}
	}
	/**
	 * name of file does not contain the prefix of paths
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String name_of(File file) throws Exception {
		if(file == null)
			throw new IllegalArgumentException("Invalid file: null");
		else {
			String path = file.getAbsolutePath();
			
			int index = path.lastIndexOf('\\');
			if(index < 0) index = path.lastIndexOf('/');
			
			if(index < 0) path = path.trim();
			else path = path.substring(index + 1).trim();
			
			return path;
		}
	}
	/**
	 * file of the specified name under the given directory
	 * @param dir
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static File file_of(File dir, String name) throws Exception {
		if(dir == null)
			throw new IllegalArgumentException("Invalid dir: null");
		else if(name == null)
			throw new IllegalArgumentException("Invalid name: null");
		else {
			return new File(dir.getAbsolutePath() + File.separator + name);
		}
	}
	
	/**
	 * get the directory, if dir is not defined, then a
	 * new empty directory will be created at the specified
	 * file paths.
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	public static File get_directory(File dir) throws Exception {
		if(dir == null)
			throw new IllegalArgumentException("Invalid dir: null");
		else {
			if(!dir.exists()) {
				dir.mkdir(); while(!dir.exists());
			}
			else {
				if(!dir.isDirectory()) {
					throw new IllegalArgumentException(
							"Not directory!" + dir.getAbsolutePath());
				}
			}
			return dir;
		}
	}
	/**
	 * remove all the files under the file (directory)
	 * @param file
	 * @throws Exception
	 */
	public static void remove(File file) throws Exception {
		if(file == null)
			throw new IllegalArgumentException("Invalid file: " + file);
		else if(!file.exists()) return;
		else {
			if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(int i = 0; i < files.length; i++)
					remove(files[i]);
			}
			file.delete(); while(file.exists());
		}
	}
	/**
	 * copy the content to the target
	 * @param src
	 * @param trg
	 * @throws Exception
	 */
	public static void copy(File src, File trg) throws Exception {
		if(src == null || !src.exists())
			throw new IllegalArgumentException("Invalid source: " + src);
		else if(trg == null)
			throw new IllegalArgumentException("Invalid access: " + trg);
		else {
			remove(trg);
			
			if(src.isDirectory()) {
				trg.mkdir(); while(!trg.exists());
				
				String prefix = trg.getAbsolutePath();
				prefix = prefix + File.separator;
				
				File[] files = src.listFiles();
				for(int i = 0; i < files.length; i++) {
					File file = files[i];
					copy(file, new File(prefix + name_of(file)));
				}
			}
			else {
				FileInputStream in  = new FileInputStream(src);
				FileOutputStream ou = new FileOutputStream(trg);
				
				byte[] buffer = new byte[1024 * 16]; int length;
				while((length = in.read(buffer)) != -1) {
					ou.write(buffer, 0, length);
				}
				in.close(); ou.close();
			}
		}
	}
	/**
	 * write the text to specified target file
	 * @param text
	 * @param target
	 * @throws Exception
	 */
	public static void write(String text, File target) throws Exception {
		if(text == null)
			throw new IllegalArgumentException("Invalid text: null");
		else if(target == null || target.isDirectory())
			throw new IllegalArgumentException("Invalid access: " + target);
		else {
			FileWriter writer = new FileWriter(target);
			writer.write(text); writer.close();
		}
	}
	/**
	 * read the text from target file
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public static String read(File target) throws Exception {
		if(target == null || !target.exists() || target.isDirectory())
			throw new IllegalArgumentException("Invalid access: " + target);
		else {
			FileReader reader = new FileReader(target);
			char[] buffer = new char[1024 * 8]; int length;
			
			buff.setLength(0);
			while((length = reader.read(buffer)) != -1) {
				buff.append(buffer, 0, length);
			}
			reader.close();
			
			return buffer.toString();
		}
	}
	
}
