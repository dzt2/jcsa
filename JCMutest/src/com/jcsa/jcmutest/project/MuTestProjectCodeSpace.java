package com.jcsa.jcmutest.project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.txt2mutant.MutaCodeGeneration;
import com.jcsa.jcmutest.project.util.FileOperations;

/**
 * <code>
 * 	|--	[code]			// code files directory						<br>
 * 	|--	|--	[cfiles]	// source files before pre-processed		<br>
 * 	|--	|--	[ifiles]	// source files after pre-processing		<br>
 * 	|--	|--	[sfiles]	// source files with instrumentation		<br>
 * 	|--	|--	[mfiles]	// source files seeded with mutation		<br>
 * 	|--	|--	[hfiles]	// header files to compile programs			<br>
 * 	|--	|--	[lfiles]	// library files for linking program		<br>
 * 	|--	|--	[mutants]	// the data file preserving mutations		<br>
 * </code>
 * @author yukimula
 *
 */
public class MuTestProjectCodeSpace {
	
	/* definitions */
	/** the project that defines the code space as well **/
	private MuTestProject project;
	/** mapping from code file name to management item **/
	private Map<String, MuTestProjectCodeFile> code_files;
	/**
	 * create the code space w.r.t. the given mutation testing project
	 * @param project
	 * @throws Exception
	 */
	protected MuTestProjectCodeSpace(MuTestProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
			this.code_files = new HashMap<String, MuTestProjectCodeFile>();
			File[] cfiles = this.get_cfiles_directory().listFiles();
			if(cfiles != null) {
				for(File cfile : cfiles) {
					MuTestProjectCodeFile code_file = new MuTestProjectCodeFile(this, cfile);
					this.code_files.put(code_file.get_name(), code_file);
				}
			}
		}
	}
	
	/* getters */
	/**
	 * @return the project that defines the code space as well
	 */
	public MuTestProject get_project() { return this.project; }
	/**
	 * @return project/code/cfiles/ in which code files are not pre-processed.
	 */
	public File get_cfiles_directory() { 
		return this.project.get_files().get_cfiles_directory(); 
	}
	/**
	 * @return project/code/ifiles/ in which code files are pre-processed.
	 */
	public File get_ifiles_directory() { 
		return this.project.get_files().get_ifiles_directory(); 
	}
	/**
	 * @return project/code/ifiles/ in which code files are instrumented.
	 */
	public File get_sfiles_directory() { 
		return this.project.get_files().get_sfiles_directory(); 
	}
	/**
	 * @return project/code/mfiles/ in which code files are mutated with some mutants.
	 */
	public File get_mfiles_directory() { 
		return this.project.get_files().get_mfiles_directory(); 
	}
	/**
	 * @return project/code/hfiles/ in which header files are linked and included.
	 */
	public File get_hfiles_directory() { 
		return this.project.get_files().get_hfiles_directory(); 
	}
	/**
	 * @return project/code/lfiles/ in which library files are linked and used.
	 */
	public File get_lfiles_directory() { 
		return this.project.get_files().get_lfiles_directory(); 
	}
	/**
	 * @return project/code/mutants/ in which mutation data files are produced.
	 */
	public File get_mutants_directory() { 
		return this.project.get_files().get_mutants_directory();
	}
	public Iterable<File> get_cfiles() {
		return FileOperations.list_files(this.get_cfiles_directory());
	}
	public Iterable<File> get_ifiles() {
		return FileOperations.list_files(this.get_ifiles_directory());
	}
	public Iterable<File> get_sfiles() {
		return FileOperations.list_files(this.get_sfiles_directory());
	}
	public Iterable<File> get_mfiles() {
		return FileOperations.list_files(this.get_mfiles_directory());
	}
	public Iterable<File> get_lfiles() {
		return FileOperations.list_files(this.get_lfiles_directory());
	}
	/**
	 * @return hfiles + config
	 */
	public Iterable<File> get_hdirs() {
		List<File> hdirs = new ArrayList<File>();
		hdirs.add(project.get_files().get_hfiles_directory());
		hdirs.add(project.get_files().get_config_directory());
		return hdirs;
	}
	/**
	 * @return the items that manage the code files under analysis of project.
	 */
	public Iterable<MuTestProjectCodeFile> get_code_files() {
		return this.code_files.values();
	}
	/**
	 * @param cfile
	 * @return the item that manages the code file as specified
	 */
	public MuTestProjectCodeFile get_code_file(File cfile) {
		if(cfile == null || !this.code_files.containsKey(cfile.getName()))
			return null;	/* the code file is not input to code space */
		else
			return this.code_files.get(cfile.getName());
	}
	
	/* setters */
	/**
	 * delete the original code files and set the new code items for specified input source code files.
	 * @param cfiles xxx.c files
	 * @param hfiles xxx.h files
	 * @param lfiles xxx.lib files
	 * @throws Exception
	 */
	protected void set_cfiles(Iterable<File> cfiles, Iterable<File> hfiles, Iterable<File> lfiles) throws Exception {
		/* 1. delete the existing code and data files */
		FileOperations.delete_in(this.get_cfiles_directory());
		FileOperations.delete_in(this.get_ifiles_directory());
		FileOperations.delete_in(this.get_sfiles_directory());
		FileOperations.delete_in(this.get_mfiles_directory());
		FileOperations.delete_in(this.get_hfiles_directory());
		FileOperations.delete_in(this.get_lfiles_directory());
		FileOperations.delete_in(this.get_mutants_directory());
		for(MuTestProjectCodeFile code_file : this.code_files.values()) {
			code_file.delete();
		}
		this.code_files.clear();
		
		/* 2. reset the source code files and their data */
		for(File hfile : hfiles) {
			if(hfile.getName().endsWith(".h"))
				FileOperations.copy(hfile, new File(this.get_hfiles_directory().getAbsolutePath() + "/" + hfile.getName())); 
		}
		for(File lfile : lfiles) {
			FileOperations.copy(lfile, new File(this.get_lfiles_directory().getAbsolutePath() + "/" + lfile.getName())); 
		}
		
		/* 3. build up the item of code files into memory */
		for(File cfile : cfiles) {
			MuTestProjectCodeFile code_file = new MuTestProjectCodeFile(this, cfile);
			this.code_files.put(code_file.get_name(), code_file);
		}
	}
	/**
	 * update the space of mutations for each code file in the space by setting the mutation operators
	 * @param mutation_classes
	 * @throws Exception
	 */
	protected void set_mutants(Iterable<MutaClass> mutation_classes) throws Exception {
		for(MuTestProjectCodeFile code_file : this.code_files.values()) {
			code_file.set(mutation_classes);
		}
	}
	/**
	 * seed the mutation in source code and generate the mutated version in mfiles/ directory
	 * @param mutant
	 * @throws Exception
	 */
	protected void set_mfiles(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			/* 1. get the code-file in which the mutation is injected */
			MuTestProjectCodeFile mutant_file = this.get_code_file(mutant.
					get_mutation().get_location().get_tree().get_source_file());
			/* 2. when the mutant does not belong to any existing code file */
			if(mutant_file == null) {
				throw new IllegalArgumentException("Invalid code_file: null");
			}
			/* 3. generate the code files under the mfiles/ for the mutant */
			else {
				FileOperations.delete_in(this.get_mfiles_directory());
				for(MuTestProjectCodeFile code_file : this.get_code_files()) {
					if(code_file == mutant_file) {
						MutaCodeGeneration.generate(mutant, code_file.get_mfile());
					}
					else {
						FileOperations.copy(code_file.get_ifile(), code_file.get_mfile());
					}
				}
			}
		}
	}
	
}
