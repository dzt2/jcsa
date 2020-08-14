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
 * 	|--	[test]			// test files directory						<br>
 * 	|--	|--	[mutants]	// the data file preserving mutations		<br>
 * </code>
 * @author yukimula
 *
 */
public class MuTestCodeSpace {
	
	/* definition */
	private MuTestProject project;
	private Map<String, MuTestCodeFile> files;
	protected MuTestCodeSpace(MuTestProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
			this.files = new HashMap<String, MuTestCodeFile>();
		}
	}
	
	/* getters */
	/**
	 * @return mutation test project in which the code part is defined
	 */
	public MuTestProject get_project() { return this.project; }
	public List<File> get_cfiles() { return FileOperations.list_files(project.get_files().get_cfiles_directory()); }
	public List<File> get_ifiles() { return FileOperations.list_files(project.get_files().get_ifiles_directory()); }
	public List<File> get_sfiles() { return FileOperations.list_files(project.get_files().get_sfiles_directory()); }
	public List<File> get_mfiles() { return FileOperations.list_files(project.get_files().get_mfiles_directory()); }
	public List<File> get_hfiles() { return FileOperations.list_files(project.get_files().get_hfiles_directory()); }
	/**
	 * @return [hfiles, config] as the header directories used in compilation
	 */
	public List<File> get_hdirs() {
		List<File> hdirs = new ArrayList<File>();
		hdirs.add(project.get_files().get_hfiles_directory());
		hdirs.add(project.get_files().get_config_directory());
		return hdirs;
	}
	/**
	 * @return the library files used to compile the program.
	 */
	public List<File> get_lfiles() {
		return FileOperations.list_files(project.get_files().get_lfiles_directory());
	}
	/**
	 * @return the set of cfile, ifile, sfile, mfile and mutant-space
	 */
	public Iterable<MuTestCodeFile> get_code_files() {
		return this.files.values();
	}
	
	/* setters */
	/**
	 * delete all the cfiles, ifiles, sfiles, hfiles, lfiles, and mutants/ 
	 * @throws Exception
	 */
	private void clear() throws Exception {
		MuTestProjectFiles pfiles = project.get_files();
		FileOperations.delete_in(pfiles.get_cfiles_directory());
		FileOperations.delete_in(pfiles.get_ifiles_directory());
		FileOperations.delete_in(pfiles.get_sfiles_directory());
		FileOperations.delete_in(pfiles.get_mfiles_directory());
		FileOperations.delete_in(pfiles.get_hfiles_directory());
		FileOperations.delete_in(pfiles.get_lfiles_directory());
		FileOperations.delete_in(pfiles.get_mutants_directory());
		this.files.clear();
	}
	/**
	 * input the cfiles, hfiles and lfiles, and building the code file space
	 * @param cfiles xxx.c files before pre-processing
	 * @param hfiles xxx.h header files for compilation
	 * @param lfiles xxx.lib library files to compile
	 * @throws Exception
	 */
	protected void input_code_files(Iterable<File> cfiles, Iterable<File> hfiles, Iterable<File> lfiles) throws Exception {
		this.clear();
		
		File hdir = project.get_files().get_hfiles_directory();
		for(File hfile : hfiles) {
			if(hfile.getName().endsWith(".h")) {
				File target = new File(hdir.getAbsolutePath() + "/" + hfile.getName());
				FileOperations.copy(hfile, target);
			}
		}
		
		File ldir = project.get_files().get_lfiles_directory();
		for(File lfile : lfiles) {
			File target = new File(ldir.getAbsolutePath() + "/" + lfile.getName());
			FileOperations.copy(lfile, target);
		}
		
		for(File cfile : cfiles) {
			if(cfile.getName().endsWith(".c")) {
				String name = cfile.getName();
				MuTestCodeFile code_file = new MuTestCodeFile(this, name);
				code_file.set_cfile(cfile);
			}
		}
	}
	/**
	 * update the mutants w.r.t. each code file in the space
	 * @param mutation_classes
	 * @throws Exception
	 */
	protected void update_mutations(Iterable<MutaClass> mutation_classes) throws Exception {
		for(MuTestCodeFile code_file : this.files.values()) {
			code_file.set_mutant_space(mutation_classes);
		}
	}
	/**
	 * Generate the xxx.c seeded with mutant in mfiles/
	 * @param mutant
	 * @return whether the mutant is generated with some code file in mfiles
	 * @throws Exception
	 */
	protected boolean generate_mfiles(Mutant mutant) throws Exception {
		FileOperations.delete_in(project.get_files().get_mfiles_directory());
		boolean found = false;
		for(MuTestCodeFile code_file : this.files.values()) {
			if(code_file.get_mutant_space() == mutant.get_space()) {
				MutaCodeGeneration.generate(mutant, code_file.get_mfile());
				found = true;
			}
			else {
				FileOperations.copy(code_file.get_ifile(), code_file.get_mfile());
			}
		}
		return found;
	}
	
}
