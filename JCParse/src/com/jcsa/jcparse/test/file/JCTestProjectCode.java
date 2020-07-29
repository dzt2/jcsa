package com.jcsa.jcparse.test.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 	It provides the management on source code under test in the project.
 * 	
 * 	@author yukimula
 *	
 */
public class JCTestProjectCode {
	
	/* constructor */
	/** the test project that performs on the code **/
	private JCTestProject project;
	/**
	 * create the code space in the test project.
	 * @param project
	 */
	protected JCTestProjectCode(JCTestProject project) {
		this.project = project;
	}
	
	/* getters */
	/**
	 * @param files
	 * @return the list of files in the array (non-null)
	 */
	private Iterable<File> list_of_files(File[] files) {
		List<File> file_list = new ArrayList<File>();
		if(files != null) {
			for(File file : files) {
				if(file != null && file.exists())
					file_list.add(file);
			}
		}
		return file_list;
	}
	/**
	 * @return the set of source code files {xxx.c} before pre-processed
	 */
	public Iterable<File> get_source_code_files() {
		return this.list_of_files(this.project.
				get_project_files().get_c_file_directory().listFiles());
	}
	/**
	 * @return the set of source code files {xxx.i} after pre-processing
	 */
	public Iterable<File> get_intermediate_files() {
		return this.list_of_files(this.project.
				get_project_files().get_i_file_directory().listFiles());
	}
	/**
	 * @return the set of source code files {xxx.i} being instrumented
	 */
	public Iterable<File> get_instrumental_files() {
		return this.list_of_files(this.project.
				get_project_files().get_s_file_directory().listFiles());
	}
	/**
	 * @return the set of header files {xxx.h} being included in pre-processing
	 * 		   as well as the compilation phasis.
	 */
	public Iterable<File> get_header_files() {
		return this.list_of_files(this.project.
				get_project_files().get_h_file_directory().listFiles());
	}
	/**
	 * @return the set of library files {xxx.lib} being linked in compilation
	 */
	public Iterable<File> get_library_files() {
		return this.list_of_files(this.project.
				get_project_files().get_l_file_directory().listFiles());
	}
	/**
	 * @return the set of diretories where the header files are used
	 */
	public Iterable<File> get_header_directories() {
		List<File> hdirs = new ArrayList<File>();
		hdirs.add(this.project.get_project_files().get_h_file_directory());
		hdirs.add(this.project.get_project_files().get_config_directory());
		return hdirs;
	}
	/**
	 * @return the executional file {xxx.exe} being compiled
	 */
	public File get_executional_file() {
		return new File(
				this.project.get_project_files().get_exe_directory().getAbsolutePath() + 
				"/" + this.project.get_name() + ".exe");
	}
	
	/* actions */
	
	
	
	
}
