package com.jcsa.jcmutest.project;

import java.io.File;

import com.jcsa.jcmutest.project.util.MuCommandUtil;

/**
 * Mutation test project provides the top-perspective to manage the data and
 * testing process for mutation analysis over the C programs.
 * 
 * @author yukimula
 *
 */
public class MuTestProject {
	
	/* definitions */
	private MuTestProjectFiles files;
	private MuTestProjectConfig config;
	public MuTestProject(File root, MuCommandUtil command_util) throws Exception {
		if(root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			this.files = new MuTestProjectFiles(this, root);
			this.config = new MuTestProjectConfig(this, command_util);
		}
	}
	
	/* getters */
	/**
	 * @return the name of the mutation test project
	 */
	public String get_name() {
		return this.files.get_root().getName();
	}
	/**
	 * @return the files in the mutation test project
	 */
	public MuTestProjectFiles get_files() { return this.files; } 
	/**
	 * @return the configuration data in test project
	 */
	public MuTestProjectConfig get_config() { return this.config; }
	
}
