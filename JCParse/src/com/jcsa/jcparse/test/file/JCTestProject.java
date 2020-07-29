package com.jcsa.jcparse.test.file;

import java.io.File;

/**
 * 	The C-testing project to execute software testing on program provided.
 * 	
 * 	@author yukimula
 *	
 */
public class JCTestProject {
	
	/* attributes */
	/** the file structure provided by the project **/
	private JCTestProjectFiles files;
	/** the configuration data used in test project **/
	private JCTestConfig config;
	
	/**
	 * @param root the root directory where test project is created
	 * @throws Exception
	 */
	private JCTestProject(File root) throws Exception {
		this.files = new JCTestProjectFiles(root);
		this.config = new JCTestConfig();
	}
	
	/* getters */
	/**
	 * @return the name of test project.
	 */
	public String get_name() { return this.get_root_directory().getName(); }
	/**
	 * @return the root directory where the test project is created
	 */
	public File get_root_directory() { return this.files.get_root(); }
	/**
	 * @return the file structure created by the test project in C.
	 */
	public JCTestProjectFiles get_project_files() { return this.files; }
	/**
	 * @return the configuration data used to build up testing.
	 */
	public JCTestConfig get_config() { return this.config; }
	
}
