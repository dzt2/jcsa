package __backup__;

import java.io.File;

/**
 * Directory for Image(s) for the corresponding file.
 * Each file can refer to more than one image file in
 * this directory by specifying an integer ID. For 
 * example:<br>
 * 	<code>
 * 		file.c ==> file.c{ID}.{postfix}
 * </code>
 * @author yukimula
 */
public class ImagesDirectory {
	
	/* constructor */
	protected File source;
	protected String postfix;
	protected ImagesDirectory(File source, String postfix) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(postfix == null)
			throw new IllegalArgumentException("Invalid postfix: null");
		else {
			this.source = FileProcess.get_directory(source);
			this.postfix = postfix.trim();
		}
	}
	protected ImagesDirectory(File source) throws Exception {
		this(source, "");
	}
	
	/* mapping method */
	private String name_of(File file, int ID) throws Exception {
		String name = FileProcess.name_of(file);
		return name + ID + postfix;
	}
	private File file_of(String name) throws Exception {
		return FileProcess.file_of(source, name);
	}
	
	/* getters */
	/**
	 * get the root directory
	 * @return
	 */
	public File get_root() { return source; }
	/**
	 * get the files under the root directory
	 * @return
	 */
	public File[] get_files() { return source.listFiles(); }
	
	/* image access */
	/**
	 * whether there is an image file to original one with specified ID
	 * @param orig
	 * @param ID
	 * @return
	 * @throws Exception
	 */
	public boolean has_of(File orig, int ID) throws Exception {
		if(orig == null) return false;
		else return file_of(name_of(orig, ID)).exists();
	}
	/**
	 * get the image file of original with specified ID
	 * @param orig
	 * @param ID
	 * @return
	 * @throws Exception
	 */
	public File get_of(File orig, int ID) throws Exception {
		if(orig == null)
			throw new IllegalArgumentException("Invalid original file: null");
		else {
			File target = file_of(name_of(orig, ID));
			if(target.exists()) return target;
			else throw new IllegalArgumentException("Undefined: " + orig);
		}
	}
	/**
	 * create a new image file for given file (empty)
	 * @param orig
	 * @param ID
	 * @return
	 * @throws Exception
	 */
	public File new_of(File orig, int ID) throws Exception {
		if(orig == null)
			throw new IllegalArgumentException("Invalid original file: null");
		else {
			File target = file_of(name_of(orig, ID));
			if(! target.exists()) { target.createNewFile(); return target; }
			else throw new IllegalArgumentException("Duplicated: " + orig);
		}
	}
	/**
	 * put the original file's content to the target in the directory with specified ID
	 * @param orig
	 * @param ID
	 * @return
	 * @throws Exception
	 */
	public File put_of(File orig, int ID) throws Exception {
		if(orig == null)
			throw new IllegalArgumentException("Invalid original file: null");
		else {
			File target = file_of(name_of(orig, ID));
			FileProcess.copy(orig, target); return target;
		}
	}
	/**
	 * put the specified content to the image file of specified original
	 * file and ID
	 * @param orig
	 * @param ID
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public File put_of(File orig, int ID, File content) throws Exception {
		if(orig == null)
			throw new IllegalArgumentException("Invalid original file: null");
		else {
			File target = file_of(name_of(orig, ID));
			FileProcess.copy(content, target); return target;
		}
	}
	/**
	 * delete an existing image file under the directory
	 * @param orig
	 * @param ID
	 * @return
	 * @throws Exception
	 */
	public boolean del_of(File orig, int ID) throws Exception {
		File file = get_of(orig, ID);
		FileProcess.remove(file);
		return true;
	}
	
	/**
	 * clear all of the files under the directory
	 * @throws Exception
	 */
	public void clear() throws Exception {
		File[] files = source.listFiles();
		for(int i = 0; i < files.length; i++)
			FileProcess.remove(files[i]);
	}
	
	/* factory methods */
	/**
	 * factory method to create images directory
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static ImagesDirectory create(File source) throws Exception {
		return new ImagesDirectory(source, "");
	}
	/**
	 * factory method to create images directory with specified postfix
	 * @param source
	 * @param postfix
	 * @return
	 * @throws Exception
	 */
	public static ImagesDirectory create(File source, String postfix) throws Exception {
		return new ImagesDirectory(source, postfix);
	}
	
}
