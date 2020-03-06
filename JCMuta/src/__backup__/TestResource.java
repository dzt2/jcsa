package __backup__;

import java.io.File;

/**
 * The resource directory for test cases is orgainized as follow:<br>
 * <code>
 * 	xxx/test/<br>
 * 	|-- testcase.db	[database for test cases]<br>
 * 	|-- inputs/		[directory of input data]<br>
 * 	|-- execK/		[cache list of execution]<br>
 * </code>
 * @author yukimula
 *
 */
public class TestResource {
	
	/* arguments */
	public static final String TEST_DB_NAME = "testcase.db";
	public static final String TEST_INPUT_NAME = "inputs";
	public static final String TEST_EXEC_PREFX = "_exec_";
	
	/* constructor */
	protected File root;
	protected File testDB;
	protected ImageDirectory inputs;
	protected FileCacheList exec_list;
	public TestResource(File root) throws Exception {
		if(root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			this.root = FileProcess.get_directory(root);
			
			this.testDB = FileProcess.file_of(root, TEST_DB_NAME);
			if(!this.testDB.exists())
				FileProcess.copy(JCMConfig.JCM_DB_TEMPLATE, this.testDB);
			
			this.inputs = ImageDirectory.create(
					FileProcess.file_of(root, TEST_INPUT_NAME));
			
			this.exec_list = new FileCacheList(root, TEST_EXEC_PREFX);
		}
	}
	
	/* getters */
	/**
	 * get the directory xxx/test/
	 * @return
	 */
	public File get_root() { return root; }
	/**
	 * get the file xxx/test/testcase.db
	 * @return
	 */
	public File get_testDB() { return testDB; }
	/**
	 * get the directory xxx/test/inputs/
	 * @return
	 */
	public ImageDirectory get_inputs() { return inputs; }
	/**
	 * get the cache directories for xxx/test/_exec_K/
	 * @return
	 */
	public FileCacheList get_exec_list() { return exec_list; }
	
}
