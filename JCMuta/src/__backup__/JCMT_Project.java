package __backup__;

import java.io.File;

/**
 * Java-implemented C program Mutation Testing project.
 * The organization is shown as follow:<br>
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
 * 	|-- score/<br>
 * </code>
 * @author yukimula
 *
 */
public class JCMT_Project {
	
	protected JCMTResource 		resource;
	protected CodeManager 		code_manager;
	protected TestManager 		test_manager;
	public JCMT_Project(File root) throws Exception {
		resource = new JCMTResource(this, root);
		code_manager = new CodeManager(resource.get_source());
		test_manager = new TestManager(resource.get_test());
	}
	
	public JCMTResource get_resource() {
		return resource;
	}
	public CodeManager get_code_manager() {
		return this.code_manager;
	}
	public TestManager get_test_manager() {
		return this.test_manager;
	}
	public TestOracleManager get_oracle_manager(
			File cfile, CodeMutationType type) throws Exception {
		if(cfile == null)
			throw new IllegalArgumentException("invalid cfile: null");
		else if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else {
			ResultResource result = this.resource.get_result();
			CFileOutputDirectory cfile_dir = result.get_code_output_directory(cfile);
			OutputDirectory directory = cfile_dir.get_output_directory(type);
			return directory.get_oracle();
		}
	}

}
