package __backup__;

import java.io.File;

/**
 * The resource directory for Java-implemented C-program Mutation Testing
 * is organized as follow:<br>
 * <code>
 * 	xxx/project/<br>
 * 	|-- source/<br>
 * 	|-- |-- code/<br>
 * 	|-- |-- muta/<br>
 * 	|-- |-- _compile_K/<br>
 * 	|-- test/<br>
 * 	|-- |-- testcase.db<br>
 * 	|-- |-- inputs/<br>
 * 	|-- |-- _exec_K/<br>
 * 	|-- utility/<br>
 * 	|-- |-- cover/<br>
 * 	|-- |-- infect/<br>
 * 	|-- |-- strong/<br>
 * </code>
 * @author yukimula
 *
 */
public class JCMTResource {
	
	/* arguments */
	public static final String SOURCE_DIR_NAME	= "source";
	public static final String TEST_DIR_NAME 	= "test";
	public static final String REST_DIR_NAME 	= "result";
	
	/* constructor */
	protected File root;
	protected JCMT_Project project;
	protected CodeResource source;
	protected TestResource test;
	protected ResultResource result;
	public JCMTResource(JCMT_Project project, File root) throws Exception {
		if(root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			this.root = FileProcess.get_directory(root); this.project = project;
			this.source = new CodeResource(FileProcess.file_of(root, SOURCE_DIR_NAME));
			this.test = new TestResource(FileProcess.file_of(root, TEST_DIR_NAME));
			this.result = new ResultResource(project, FileProcess.file_of(root, REST_DIR_NAME));
		}
	}
	
	/**
	 * get the xxx/project/
	 * @return
	 */
	public File get_root() { return root; }
	/**
	 * get the xxx/project/source/
	 * @return
	 */
	public CodeResource get_source() { return source; }
	/**
	 * get the xxx/project/test/
	 * @return
	 */
	public TestResource get_test() { return test; }
	/**
	 * get the project/result/
	 * @return
	 */
	public ResultResource get_result() { return result; }
	
}
