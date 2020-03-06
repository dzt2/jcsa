package __backup__;

import java.io.File;

/**
 * <code>
 *  xxx.c [directory]<br>
 * 	|-- coverage		<br>
 * 	|-- weakness		<br>
 * 	|-- stronger		<br>
 * 	|--|-- compile.txt	<br>
 * 	|--|-- result.0.db	<br>
 * 	|--|-- result.1.db	<br>
 * 	|--|-- ...... 		<br>
 * 	|--|-- result.n.db	<br>
 * 	|--|-- scores.db	<br>
 * </code>
 * @author yukimula
 *
 */
public class CFileOutputDirectory {
	
	protected ResultResource parent;
	protected File cfile;
	protected File directory;
	protected OutputDirectory children[];
	/**
	 * create an output director for each code file under project/results/.<br>
	 * If the directory exists, nothing happens; otherwise, a new-created 
	 * directory is built up from the code-file.<br>
	 * @param parent
	 * @param cfile
	 * @throws Exception
	 */
	protected CFileOutputDirectory(ResultResource parent, File cfile) throws Exception {
		if(parent == null)
			throw new IllegalArgumentException("invalid parent: null");
		else if(cfile == null || !cfile.exists())
			throw new IllegalArgumentException("undefined code: cfile");
		else {
			this.parent = parent;
			this.directory = new File(parent.root.getAbsolutePath()
					+ File.separator + cfile.getName());
			this.directory = FileProcess.get_directory(directory);
			
			children = new OutputDirectory[3];
			children[0] = new OutputDirectory(this, CodeMutationType.coverage);
			children[1] = new OutputDirectory(this, CodeMutationType.weakness);
			children[2] = new OutputDirectory(this, CodeMutationType.stronger);
		}
	}
	
	/* getters */
	/**
	 * <code>project/results/</code>
	 * @return
	 */
	public ResultResource get_parent() { return parent; }
	/**
	 * <code>project/results/xxx.c</code>
	 * @return
	 */
	public File get_root() { return directory; }
	/**
	 * source of <code>xxx.c</code>
	 * @return
	 */
	public File get_code_file() { return cfile; }
	/**
	 * <code>project/results/xxx.c/coverage/</code>
	 * @return
	 */
	public OutputDirectory get_coverage() { return children[0]; }
	/**
	 * <code>project/results/xxx.c/weakness/</code>
	 * @return
	 */
	public OutputDirectory get_weakness() { return children[1]; }
	/**
	 * <code>project/results/xxx.c/stronger/</code>
	 * @return
	 */
	public OutputDirectory get_stronger() { return children[2]; }
	/**
	 * get the output directory by specifying its name
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public OutputDirectory get_output_directory(CodeMutationType name) throws Exception {
		switch(name) {
		case coverage:	return this.children[0];
		case weakness:	return this.children[1];
		case stronger:	return this.children[2];
		default:	throw new IllegalArgumentException("unknown name: " + name.toString());
		}
	}
	
}
