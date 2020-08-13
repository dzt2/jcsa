package com.jcsa.jcmutest.project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.txt2mutant.MutaCodeGeneration;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.parse.CTranslate;

/**
 * Code space provides interface to manage the code files within project:<br>
 * 	|--	[code]			// code files directory						<br>
 * 	|--	|--	[cfiles]	// source files before pre-processed		<br>
 * 	|--	|--	[ifiles]	// source files after pre-processing		<br>
 * 	|--	|--	[sfiles]	// source files with instrumentation		<br>
 * 	|--	|--	[mfiles]	// source files seeded with mutation		<br>
 * 	|--	|--	[hfiles]	// header files to compile programs			<br>
 * 	|--	|--	[lfiles]	// library files for linking program		<br>
 * 	|--	[test]			// test files directory						<br>
 * 	|--	|--	[mutants]	// the data file preserving mutations		<br>		
 * @author yukimula
 *
 */
public class MuTestProjectCode {
	
	/* definition */
	private MuTestProject project;
	private Map<String, MuTestProgram> programs;
	protected MuTestProjectCode(MuTestProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project");
		else {
			this.project = project;
			this.programs = new HashMap<String, MuTestProgram>();
			this.update_programs();
		}
	}
	
	/* getters */
	/**
	 * @return the mutation test project that defines code space
	 */
	public MuTestProject get_project() { return this.project; }
	/**
	 * @param directory
	 * @return the list of files in the directory
	 */
	private List<File> files_of(File directory) {
		List<File> flist = new ArrayList<File>();
		File[] files = directory.listFiles();
		if(files != null) {
			for(File file : files) {
				flist.add(file);
			}
		}
		return flist;
	}
	/**
	 * @return xxx.c files in cfiles/ before pre-processing
	 */
	public List<File> get_cfiles() {
		return this.files_of(project.get_files().get_cfiles_directory());
	}
	/**
	 * @return xxx.c files in ifiles/ after pre-processing
	 */
	public List<File> get_ifiles() {
		return this.files_of(project.get_files().get_ifiles_directory());
	}
	/**
	 * @return xxx.c files in sfiles/ with instrumentation
	 */
	public List<File> get_sfiles() {
		return this.files_of(project.get_files().get_sfiles_directory());
	}
	/**
	 * @return xxx.c files in mfiles/ seeded with mutation
	 */
	public List<File> get_mfiles() {
		return this.files_of(project.get_files().get_mfiles_directory());
	}
	/**
	 * @return [hfiles, config] as directories with header files being linked
	 */
	public List<File> get_hdirs() {
		List<File> hdirs = new ArrayList<File>();
		hdirs.add(project.get_files().get_hfiles_directory());
		hdirs.add(project.get_files().get_config_directory());
		return hdirs;
	}
	/**
	 * @return the library files in lfiles/ for linked as ti compile program
	 */
	public List<File> get_lfiles() {
		return this.files_of(project.get_files().get_lfiles_directory());
	}
	/**
	 * @return the data files in mutants/ that preserve mutations generated
	 *         on each xxx.c file in cfiles/, ifiles/, sfiles/ and mfiles/.
	 */
	public List<File> get_mutant_files() {
		return this.files_of(project.get_files().get_mutants_directory());
	}
	/**
	 * @param cfile cfile | ifile | sfile | mfile
	 * @return the program instance w.r.t. the cfile | ifile | sfile | mfile
	 */
	public MuTestProgram get_program(File cfile) {
		String key = cfile.getName();
		if(!this.programs.containsKey(key)) {
			return null;
		}
		else {
			return this.programs.get(key);
		}
	}
	
	/* synchronize methods */
	/**
	 * @param cfile cfile | ifile | sfile | mfile
	 * @return the data file to preserve mutations seeded in cfile
	 */
	private File get_mutant_file(File cfile) {
		File directory = project.get_files().get_mutants_directory();
		return new File(directory.getAbsolutePath() + "/" + cfile.getName() + ".m");
	}
	/**
	 * update the program instance w.r.t. each xxx.c file in ifiles/ after
	 * pre-processed.
	 * @throws Exception
	 */
	private void update_programs() throws Exception {
		MuTestProjectConfig config = project.get_config();
		this.programs.clear();
		for(File ifile : this.get_ifiles()) {
			CRunTemplate sizeof_template = new CRunTemplate(
						config.get_sizeof_template_file());
			AstTree ast_tree = CTranslate.parse(ifile, config.
						get_lang_standard(), sizeof_template);
			CirTree cir_tree = CTranslate.parse(ast_tree, sizeof_template);
			MuTestProgram program = new 
						MuTestProgram(ast_tree, cir_tree, sizeof_template);
			this.programs.put(ifile.getName(), program);
		}
		this.update_mutant_spaces();
	}
	/**
	 * update the mutants in the spaces of each program of each ifile
	 * @throws Exception
	 */
	private void update_mutant_spaces() throws Exception {
		for(MuTestProgram program : this.programs.values()) {
			File mutant_file = this.get_mutant_file(program.get_ifile());
			program.get_mutant_space().load(mutant_file);
		}
	}
	/**
	 * It performs pre-processing and generate ifiles from cfiles as input
	 * @throws Exception
	 */
	private void update_ifiles() throws Exception {
		File directory = project.get_files().get_ifiles_directory();
		FileOperations.delete_in(directory);
		MuTestProjectConfig config = this.project.get_config();
		MuCommandUtil command_util = config.get_command_util();
		for(File cfile : this.get_cfiles()) {
			File ifile = new File(directory.getAbsolutePath() + "/" + cfile.getName());
			if(!command_util.do_preprocess(config.get_compiler(), 
					cfile, ifile, this.get_hdirs(), config.get_compile_parameters())) {
				throw new RuntimeException("Unable to get " + ifile.getAbsolutePath());
			}
		}
	}
	/**
	 * It removes the old instrumentation files and update them from ifiles
	 * @throws Exception
	 */
	private void update_sfiles() throws Exception {
		File directory = project.get_files().get_sfiles_directory();
		FileOperations.delete_in(directory);
		for(MuTestProgram program : this.programs.values()) {
			String code = MutaCodeGeneration.instrument_code(program.get_ast_tree(), 
									project.get_files().get_instrument_txt_file());
			File ifile = program.get_ifile();
			File sfile = new File(directory.getAbsolutePath() + "/" + ifile.getName());
			FileOperations.write(sfile, code);
		}
	}
	/**
	 * Copy all the files in ifiles/ to mfiles/ for initialization
	 * @throws Exception
	 */
	private void update_mfiles() throws Exception {
		File directory = project.get_files().get_mfiles_directory();
		FileOperations.delete_in(directory);
		for(File ifile : this.get_ifiles()) {
			File mfile = new File(directory.getAbsolutePath() + "/" + ifile.getName());
			FileOperations.copy(ifile, mfile);
		}
	}
	
	/* input-setters */
	/**
	 * delete all the code and mutants files in project and clear
	 * the program-instances buffer, including:<br>
	 * 	cfiles, ifiles, sfiles, mfiles, hfiles, lfiles, mutants.
	 */
	protected void clear() throws Exception {
		this.programs.clear();
		FileOperations.delete_in(project.get_files().get_cfiles_directory());
		FileOperations.delete_in(project.get_files().get_ifiles_directory());
		FileOperations.delete_in(project.get_files().get_sfiles_directory());
		FileOperations.delete_in(project.get_files().get_mfiles_directory());
		FileOperations.delete_in(project.get_files().get_hfiles_directory());
		FileOperations.delete_in(project.get_files().get_lfiles_directory());
		FileOperations.delete_in(project.get_files().get_mutants_directory());
	}
	/**
	 * remove cfiles/ directory and input the specified ones
	 * @param cfiles
	 * @throws Exception
	 */
	private void input_cfiles(Iterable<File> cfiles) throws Exception {
		File directory = project.get_files().get_cfiles_directory();
		FileOperations.delete_in(directory);
		for(File source : cfiles) {
			File target = new File(directory.getAbsolutePath() + "/" + source.getName());
			FileOperations.copy(source, target);
		}
	}
	/**
	 * remove hfiles/ directory and input the specified ones
	 * @param hfiles
	 * @throws Exception
	 */
	private void input_hfiles(Iterable<File> hfiles) throws Exception {
		File directory = project.get_files().get_hfiles_directory();
		FileOperations.delete_in(directory);
		for(File source : hfiles) {
			File target = new File(directory.getAbsolutePath() + "/" + source.getName());
			FileOperations.copy(source, target);
		}
	}
	/**
	 * remove lfiles/ directory and input the specified ones
	 * @param lfiles
	 * @throws Exception
	 */
	private void input_lfiles(Iterable<File> lfiles) throws Exception {
		File directory = project.get_files().get_lfiles_directory();
		FileOperations.delete_in(directory);
		for(File source : lfiles) {
			File target = new File(directory.getAbsolutePath() + "/" + source.getName());
			FileOperations.copy(source, target);
		}
	}
	/**
	 * @param cfiles xxx.c before pre-processing {this will clear original mutants!)
	 * @param hfiles xxx.h as header files
	 * @param lfiles xxx.lib as library files
	 * @throws Exception
	 */
	protected void set_cfiles(Iterable<File> cfiles, Iterable<File> hfiles, Iterable<File> lfiles) throws Exception {
		this.clear();
		this.input_cfiles(cfiles);
		this.input_hfiles(hfiles);
		this.input_lfiles(lfiles);
		this.update_ifiles();
		this.update_programs();
		this.update_sfiles();
		this.update_mfiles();
	}
	/**
	 * remove the mutations and generate new ones in each xxx.c files and save them in mutants/ directory
	 * @param mutation_classes
	 * @throws Exception
	 */
	protected void set_mutants(Iterable<MutaClass> mutation_classes) throws Exception {
		File directory = project.get_files().get_mutants_directory();
		FileOperations.delete_in(directory);
		for(MuTestProgram program : this.programs.values()) {
			File mfile = this.get_mutant_file(program.get_ifile());
			program.get_mutant_space().update(mutation_classes);
			program.get_mutant_space().save(mfile);
		}
	}
	
}
