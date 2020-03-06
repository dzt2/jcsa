package __backup__;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The resource directory for xxx/project/test/_exec_K/<br>
 * <code>
 * 	test/_exec_K/<br>
 * 	|-- main.exe<br>
 * 	|-- exec.sh <br>
 * 	|-- [output]+<br>
 * </code>
 * @author yukimula
 *
 */
public class ExecResource {
	
	/* arguments */
	public static final String MAIN_NAME = "main.exe";
	public static final String EXEC_NAME = "exec.sh";
	
	/* constructor */
	protected File root;
	protected File cdir;
	protected File main;
	protected File exec;
	protected List<File> outputs;
	public ExecResource(File root, File cdir) throws Exception {
		if(root == null || !root.exists() || !root.isDirectory())
			throw new IllegalArgumentException("Invalid root: null");
		else if(cdir == null || !cdir.exists() || !cdir.isDirectory())
			throw new IllegalArgumentException("Invalid cdir: null");
		else {
			this.root = root; this.cdir = cdir;
			this.main = FileProcess.file_of(root, MAIN_NAME);
			this.exec = FileProcess.file_of(root, EXEC_NAME);
			this.outputs = new ArrayList<File>(); update();
		}
	}
	
	/* setter */
	/**
	 * whether this resource is available.
	 * In dynamic testing, a directory could be erased
	 * by other threads. As a result, the resource might
	 * become unavailable for access. The user need to 
	 * check and ensure that their access is valid.
	 * @return
	 */
	public boolean available() { return root.exists(); }
	/**
	 * update the output list
	 * @throws Exception
	 */
	public void update() throws Exception {
		outputs.clear();
		File[] files = this.root.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(!files[i].equals(main)
				&& !files[i].equals(exec)) {
				outputs.add(files[i]);
			}
		}
	}
	
	/* getters */
	/**
	 * get xxx/test/_exec_K/
	 * @return
	 */
	public File get_root() { return root; }
	/**
	 * get the code directory where the code files are 
	 * @return
	 */
	public File get_cdir() { return cdir; }
	/**
	 * get xxx/test/_exec_K/main.exe
	 * @return
	 */
	public File get_main() { return main; }
	/**
	 * get xxx/test/_exec_K/exec.sh
	 * @return
	 */
	public File get_exec() { return exec; }
	/**
	 * get xxx/test/_exec_K/[output]+
	 * @return
	 */
	public Iterator<File> get_outputs() { 
		return outputs.iterator(); 
	}
	
	/* clear */
	public void reset(File cdir) throws Exception {
		if(cdir == null || !cdir.exists() || !cdir.isDirectory())
			throw new IllegalArgumentException("Invalid code directory: null");
		else {
			this.clear(); FileProcess.copy(cdir, this.cdir);
		}
	}
	/**
	 * clear all the files under the exec-resource
	 * @throws Exception
	 */
	protected void clear() throws Exception {
		File[] files = root.listFiles();
		for(int i = 0; i < files.length; i++) {
			FileProcess.remove(exec);
		}
		this.outputs.clear();
	}
	/**
	 * clear the main.exe
	 * @throws Exception
	 */
	public void clear_main() throws Exception {
		FileProcess.remove(main);
	}
	/**
	 * clear the exec.sh
	 * @throws Exception
	 */
	public void clear_exec() throws Exception {
		FileProcess.remove(exec);
	}
	/**
	 * clear the output files
	 * @throws Exception
	 */
	public void clear_outputs() throws Exception {
		this.update();
		for(File output : outputs)
			FileProcess.remove(output);
		outputs.clear();
	}
	
}
