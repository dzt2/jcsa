package com.jcsa.jcmutest.project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.parse.CTranslate;
import com.jcsa.jcparse.parse.code.CodeGeneration;

/**
 * It provides interfaces to manage the code files and compilation in the 
 * mutation test project.<br>
 * 	|--	[code]			// code files directory						<br>
 * 	|--	|--	[cfiles]	// source files before pre-processed		<br>
 * 	|--	|--	[ifiles]	// source files after pre-processing		<br>
 * 	|--	|--	[sfiles]	// source files with instrumentation		<br>
 * 	|--	|--	[mfiles]	// source files seeded with mutation		<br>
 * 	|--	|--	[hfiles]	// header files to compile programs			<br>
 * 	|--	|--	[lfiles]	// library files for linking program		<br>
 * @author yukimula
 *
 */
public class MuTestProjectCode {
	
	/* definition */
	private MuTestProject project;
	private Map<String, MuTestProgram> programs;
	protected MuTestProjectCode(MuTestProject project) {
		this.project = project;
		this.programs = new HashMap<String, MuTestProgram>();
	}
	
	/* getters */
	/**
	 * @param dir
	 * @return the files in directory
	 */
	private List<File> list_of(File dir) {
		File[] files = dir.listFiles();
		List<File> list = new ArrayList<File>();
		if(files != null) {
			for(File file : files) {
				list.add(file);
			}
		}
		return list;
	}
	/**
	 * @return the source code files before pre-processed
	 */
	public List<File> get_cfiles() { 
		return this.list_of(this.project.get_files().get_cfiles_directory()); 
	}
	/**
	 * @return the source code files after pre-processed
	 */
	public List<File> get_ifiles() { 
		return this.list_of(this.project.get_files().get_ifiles_directory()); 
	}
	/**
	 * @return the source code files with instrumentation
	 */
	public List<File> get_sfiles() { 
		return this.list_of(this.project.get_files().get_sfiles_directory()); 
	}
	/**
	 * @return the source code files seeded with mutation
	 */
	public List<File> get_mfiles() { 
		return this.list_of(this.project.get_files().get_mfiles_directory()); 
	}
	/**
	 * @return the library files used for linking program
	 */
	public List<File> get_lfiles() { 
		return this.list_of(this.project.get_files().get_lfiles_directory()); 
	}
	/**
	 * @return [hfiles, config]
	 */
	public List<File> get_hdirs() {
		List<File> hdirs = new ArrayList<File>();
		hdirs.add(this.project.get_files().get_hfiles_directory());
		hdirs.add(this.project.get_files().get_config_directory());
		return hdirs;
	}
	/**
	 * @return the executional file being compiled and executed
	 */
	public File get_exe_file() {
		return new File(this.project.get_files().get_efiles_directory().
				getAbsolutePath() + "/" + this.project.get_name() + ".exe");
	}
	/**
	 * @param ifile
	 * @return the program parsed from the ifile
	 */
	public MuTestProgram get_program(File ifile) {
		String key = ifile.getName();
		if(!this.programs.containsKey(key)) {
			return null;
		}
		else {
			return this.programs.get(key);
		}
	}
	
	/* set code files and mutants */
	/**
	 * @param cfiles xxx.c files before pre-processed
	 * @throws Exception
	 */
	private void in_cfiles(Iterable<File> cfiles) throws Exception {
		File directory = this.project.get_files().get_cfiles_directory();
		FileOperations.delete_in(directory);
		for(File source : cfiles) {
			File target = new File(directory.getAbsolutePath() + "/" + source.getName());
			FileOperations.copy(source, target);
		}
	}
	/**
	 * generate xxx.i by pre-processing the source code files (xxx.c)
	 * @throws Exception
	 */
	private void do_preprocess() throws Exception {
		/* 1. declarations */
		List<File> cfiles = this.get_cfiles();
		MuTestProjectConfig config = this.project.get_config();
		File ifiles_directory = 
				this.project.get_files().get_ifiles_directory();
		
		/* 2. delete the old intermediate code files */
		FileOperations.delete_in(ifiles_directory);
		
		/* 3. initializer the compilation parameters */
		List<String> parameters = new ArrayList<String>();
		for(String parameter : config.get_compile_parameters()) {
			parameters.add(parameter);
		}
		parameters.add("-imacros");
		parameters.add(config.get_preprocess_macro_file().getAbsolutePath());
		
		/* 4. perform pre-processing compilation for each c file */
		for(File cfile : cfiles) {
			File ifile = new File(ifiles_directory.getAbsolutePath() + "/" + cfile.getName());
			if(!config.get_command_util().do_preprocess(
					config.get_compiler(), cfile, ifile, 
					this.get_hdirs(), parameters)) {
				throw new RuntimeException("Failed to pre-process " + ifile.getAbsolutePath());
			}
		}
	}
	/**
	 * generate the AST and CIR tree of the ifiles.
	 * @throws Exception
	 */
	private void do_parsing() throws Exception {
		MuTestProjectConfig config = this.project.get_config();
		this.programs.clear();
		for(File ifile : this.get_ifiles()) {
			CRunTemplate sizeof_template = new CRunTemplate(config.get_sizeof_template_file());
			AstTree ast_tree = CTranslate.parse(ifile, config.get_lang_standard(), sizeof_template);
			CirTree cir_tree = CTranslate.parse(ast_tree, sizeof_template);
			MuTestProgram program = new MuTestProgram(ast_tree, cir_tree, sizeof_template);
			this.programs.put(ifile.getName(), program);
		}
	}
	/**
	 * load all the mutants from mfiles/ w.r.t. each program
	 * @throws Exception
	 */
	private void load_mutants() throws Exception {
		File directory = this.project.get_files().get_mutants_directory();
		for(MuTestProgram program : this.programs.values()) {
			File mfile = new File(directory.getAbsolutePath() + 
					"/" + program.get_ifile().getName() + ".m");
			if(mfile.exists()) {
				program.get_mutant_space().load(mfile);
			}
		}
	}
	/**
	 * generate the instrumental program from source code in ifiles/*.c
	 * @throws Exception
	 */
	private void do_instrument() throws Exception {
		/* 1. delete all the instrumental files in the sfiles/*.c */
		File sfile_directory = this.project.get_files().get_sfiles_directory();
		MuTestProjectConfig config = this.project.get_config();
		FileOperations.delete_in(sfile_directory);
		File instrument_txt_file = project.get_files().get_instrument_txt_file();
		
		/* 2. generate the instrumental file in the sfiles/***.c */
		for(File ifile : this.get_ifiles()) {
			AstTree ast_tree = CTranslate.parse(ifile, config.get_lang_standard(), 
							new CRunTemplate(config.get_sizeof_template_file()));
			File sfile = new File(sfile_directory.getAbsolutePath() + "/" + ifile.getName());
			String code = CodeGeneration.instrument_code(ast_tree, instrument_txt_file);
			if(FileOperations.write(sfile, code)) {
				throw new RuntimeException("Failed to instrument " + sfile.getAbsolutePath());
			}
		}
	}
	/**
	 * set xxx.c files, generate xxx.i and xxx.s files for testing,
	 * parse programs from xxx.i files and load mutation files
	 * @param cfiles source code files before pre-processed
	 * @throws Exception
	 */
	public void set_cfiles(Iterable<File> cfiles) throws Exception {
		this.in_cfiles(cfiles);
		this.do_preprocess();
		this.do_parsing();
		this.do_instrument();
		this.load_mutants();
	}
	/**
	 * generate the mutations w.r.t. the specified operators for each program
	 * of the xxx.i in ifiles of the test project.
	 * @param mutation_classes
	 * @throws Exception
	 */
	public void set_mutants(Iterable<MutaClass> mutation_classes) throws Exception {
		File directory = this.project.get_files().get_mutants_directory();
		for(MuTestProgram program : this.programs.values()) {
			File mfile = new File(directory.getAbsolutePath() + 
					"/" + program.get_ifile().getName() + ".m");
			program.get_mutant_space().load(mfile);
		}
	}
	
}
