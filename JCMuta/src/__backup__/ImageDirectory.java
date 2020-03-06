package __backup__;

import java.io.File;

/**
 * An image directory is an image to the 
 * files in another directory.
 * @author yukimula
 */
public class ImageDirectory {
	
	/* constructor */
	protected File source;
	private String postfix;
	protected ImageDirectory(File source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else { 
			this.source = FileProcess.get_directory(source); 
			this.postfix = null;
		}
	}
	protected ImageDirectory(File source, String postfix) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(postfix == null || postfix.trim().isEmpty())
			throw new IllegalArgumentException("Invalid postfix: null");
		else { 
			this.source = FileProcess.get_directory(source);
			this.postfix = postfix.trim();
		}
	}
	
	/* basic methods */
	private String key_of(File file) throws Exception {
		String path = FileProcess.name_of(file);
		if(postfix == null) return path;
		else return path + postfix;
	}
	private File file_of(String key) throws Exception {
		return FileProcess.file_of(source, key);
	}
	
	/**
	 * get the root directory
	 * @return
	 */
	public File get_root() { return source; }
	/**
	 * get all the image files in directory
	 * @return
	 */
	public File[] get_files() { return source.listFiles(); }
	
	/**
	 * whether there is image to the original file
	 * @param orig
	 * @return
	 * @throws Exception 
	 */
	public boolean has_of(File orig) throws Exception {
		if(orig == null) return false;
		else {
			File target = file_of(key_of(orig));
			return target.exists();
		}
	}
	/**
	 * get the image to the original file
	 * @param orig
	 * @return
	 * @throws Exception
	 */
	public File get_of(File orig) throws Exception {
		if(orig == null)
			throw new IllegalArgumentException("Invalid original file: null");
		else {
			File target = file_of(key_of(orig));
			if(target.exists()) return target;
			else throw new IllegalArgumentException("Undefined: " + orig);
		}
	}
	/**
	 * create a new empty image file for the source
	 * @param orig
	 * @return
	 * @throws Exception
	 */
	public File new_of(File orig) throws Exception {
		if(orig == null)
			throw new IllegalArgumentException("Invalid original file: null");
		else {
			File target = file_of(key_of(orig));
			if(! target.exists()) { target.createNewFile(); return target; }
			else throw new IllegalArgumentException("Duplicated: " + orig);
		}
	}
	/**
	 * set the content of image file as original
	 * @param orig
	 * @return
	 * @throws Exception
	 */
	public File put_of(File orig) throws Exception {
		if(orig == null)
			throw new IllegalArgumentException("Invalid original file: null");
		else {
			File target = file_of(key_of(orig));
			FileProcess.copy(orig, target); return target;
		}
	}
	/**
	 * set the content of original as template file
	 * @param orig
	 * @param temp
	 * @return
	 * @throws Exception
	 */
	public File put_of(File orig, File temp) throws Exception {
		if(orig == null)
			throw new IllegalArgumentException("Invalid original file: null");
		else {
			File target = file_of(key_of(orig));
			FileProcess.copy(temp, target); return target;
		}
	}
	/**
	 * delete an existing image file from the directory
	 * @param orig
	 * @return
	 * @throws Exception
	 */
	public boolean del_of(File orig) throws Exception {
		File file = get_of(orig);
		FileProcess.remove(file);
		return true;
	}
	
	/**
	 * remove all the image files under the directory
	 * @throws Exception
	 */
	public void clear() throws Exception {
		File[] files = source.listFiles();
		for(int i = 0; i < files.length; i++)
			FileProcess.remove(files[i]);
	}
	
	/**
	 * factory method to create image directory
	 * @param root
	 * @return
	 * @throws Exception
	 */
	public static ImageDirectory create(File root) throws Exception {
		return new ImageDirectory(root);
	}
	/**
	 * factory method to create image directory with specified 
	 * @param root
	 * @param postfix
	 * @return
	 * @throws Exception
	 */
	public static ImageDirectory create(File root, String postfix) throws Exception {
		return new ImageDirectory(root, postfix);
	}
}
