package __backup__;

import java.io.File;

/**
 * <code>
 * 	[project]/result/<br>
 * 		|-- xxx.c [directory]<br>
 * 		|-- xxx.c [directory]<br>
 * 		|-- xxx.c [directory]<br>
 * 		|--|-- coverage		<br>
 * 		|--|-- weakness		<br>
 * 		|--|-- stronger		<br>
 * 		|--|--|-- compile.txt	<br>
 * 		|--|--|-- result.0.db	<br>
 * 		|--|--|-- result.1.db	<br>
 * 		|--|--|-- ...... 		<br>
 * 		|--|--|-- result.n.db	<br>
 * 		|--|--|-- scores.db	<br>
 * 		|-- xxx.c [directory]<br>
 * </code>
 * @author yukimula
 *
 */
public class ResultResource {
	
	/* properties */
	protected File root;
	protected JCMT_Project project;
	/**
	 * create an result resource object to access files in results/...
	 * @param root
	 * @throws Exception
	 */
	public ResultResource(JCMT_Project project, File root) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("invalid project: null");
		else if(root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			this.project = project;
			this.root = FileProcess.get_directory(root);
		}
	}
	
	/* getters */
	/**
	 * get the test-project of the result space
	 * @return
	 */
	public JCMT_Project get_project() { return project; }
	/**
	 * project/results/
	 * @return
	 */
	public File get_root() { return root; }
	/**
	 * project/results/xxx.c/
	 * @param cfile
	 * @return
	 * @throws Exception
	 */
	public CFileOutputDirectory get_code_output_directory(File cfile) throws Exception {
		return new CFileOutputDirectory(this, cfile);
	}
	
}
