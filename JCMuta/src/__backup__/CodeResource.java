package __backup__;

import java.io.File;

/**
 * The resource directory of code is organized as follow:<br>
 * <code>
 * 	xxx/source/<br>
 * 	|-- code/ 	[original programs]<br>
 * 	|-- muta/ 	[mutation database]<br>
 * 	|-- mutac/	[context mutations]<br>
 * 	|-- execK/	[files for compile]<br>
 * </code>
 * @author yukimula
 */
public class CodeResource {
	
	/* arguments */
	public static final String CODE_DIR_NAME = "code";
	public static final String MUTA_DIR_NAME = "muta";
	public static final String CTXT_DIR_NAME = "mutac";
	public static final String COMP_DIR_PREV = "_compile_";
	
	/* constructor */
	protected File root;
	protected ImageDirectory code;
	protected ImageDirectory muta;
	protected ImageDirectory mutac;
	protected FileCacheList comps;
	public CodeResource(File root) throws Exception {
		if(root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			this.root = FileProcess.get_directory(root);
			code = ImageDirectory.create(FileProcess.file_of(root, CODE_DIR_NAME));
			muta = ImageDirectory.create(FileProcess.file_of(root, MUTA_DIR_NAME), ".db");
			mutac = ImageDirectory.create(FileProcess.file_of(root,CTXT_DIR_NAME), ".db");
			comps = new FileCacheList(this.root, COMP_DIR_PREV);
		}
	}
	
	/* getters */
	/**
	 * get the root directory: xxx/source/
	 * @return
	 */
	public File get_root() { return root; }
	/**
	 * get the xxx/source/code/
	 * @return
	 */
	public ImageDirectory get_code() { return code; }
	/**
	 * get the xxx/source/muta
	 * @return
	 */
	public ImageDirectory get_muta() { return muta; }
	/**
	 * get the xxx/source/mutac/
	 * @return
	 */
	public ImageDirectory get_mutac() { return mutac; }
	/**
	 * get the list of xxx/source/_compile_K
	 * @return
	 */
	public FileCacheList get_compile_list() { return comps; }
	
}
