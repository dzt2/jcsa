package com.jcsa.jcmuta.project;

import java.io.File;

/**
 * The files in mutation test project are organized as following:<br>
 * <code>
 * 	[project]
 * 	|--	[binary]
 * 	|--	[source]
 * 	|--	[mutant]
 * 	|--	[test]
 * 	|--	[inputs]
 * 	|-- [output]
 * 	|-- [output2]
 * 	|-- [result]
 * </code>
 * @author yukimula
 *
 */
public class MutaProject {
	
	protected File project_directory;
	protected MutaProjectConfig config;
	protected MutaTestSpace test_space;
	protected MutaSourceFiles source_files;
	protected MutaBinaries binaries;
	protected MutaTestResults results;
	
	/**
	 * create or 
	 * @param directory
	 * @throws Exception
	 */
	public MutaProject(File directory) throws Exception {
		if(directory == null)
			throw new IllegalArgumentException("Invalid directory: null");
		else {
			if(!directory.exists()) directory.mkdir();
			this.project_directory = directory;
			this.config = new MutaProjectConfig(this);
			this.test_space = new MutaTestSpace(this);
			this.source_files = new MutaSourceFiles(this);
			this.binaries = new MutaBinaries(this);
			this.results = new MutaTestResults(this);
		}
	}
	
	/* getters */
	public String get_name() { return this.project_directory.getName(); }
	/**
	 * get the directory where the project is created
	 * @return
	 */
	public File get_project_directory() { return this.project_directory; }
	/**
	 * get the configuration of the project
	 * @return
	 */
	public MutaProjectConfig get_config() { return this.config; }
	/**
	 * get the test space where the test cases are created
	 * @return
	 */
	public MutaTestSpace get_test_space() { return this.test_space; }
	/**
	 * get the source files being compiled, mutated and tested
	 * @return
	 */
	public MutaSourceFiles get_source_files() { return this.source_files; }
	/**
	 * get the execution space for executing the program against test cases
	 * @return
	 */
	public MutaBinaries get_binaries() { return this.binaries; }
	/**
	 * get the results generated in testing
	 * @return
	 */
	public MutaTestResults get_results() { return this.results; }
	
}
