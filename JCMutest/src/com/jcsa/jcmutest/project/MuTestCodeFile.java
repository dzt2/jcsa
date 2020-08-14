package com.jcsa.jcmutest.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.txt2mutant.MutaCodeGeneration;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.parse.CTranslate;

/**
 * It provides the interface to manage the resource of one single code file in code space,
 * including the ifile, sfile and mfile, as well as their AST, CIR and mutation space.
 * @author yukimula
 *
 */
public class MuTestCodeFile {
	
	/* definition */
	/** the code part in which the file is managed **/
	private MuTestCodeFiles files;
	/** the unique name of the files in code space **/
	private String name;
	/** the source code file before pre-processed **/
	private File cfile;
	/** the source code file after pre-processed **/
	private File ifile;
	/** the source code file with instrumentation **/
	private File sfile;
	/** the source code file seeded with mutation **/
	private File mfile;
	/** the data file that preserves mutants w.r.t. the file **/
	private File ufile;
	/** the template to compute sizeof operation in static **/
	private CRunTemplate sizeof_template;
	/** the abstract syntactic tree parsed from the c file **/
	private AstTree ast_tree;
	/** the C-intermediate representation generated from file **/
	private CirTree cir_tree;
	/** the space of mutations seeded in the file as specified **/
	private MutantSpace mutant_space;
	/**
	 * create a mutation-test-file unit w.r.t. the name
	 * @param code_part
	 * @param name
	 * @throws Exception
	 */
	protected MuTestCodeFile(MuTestCodeFiles files, String name) throws Exception {
		if(files == null)
			throw new IllegalArgumentException("Invalid code_part");
		else if(name == null || !name.endsWith(".c"))
			throw new IllegalArgumentException("Invalid name: null");
		else {
			this.files = files;
			this.name = name;
			MuTestProjectFiles pfiles = files.get_project().get_files();
			this.cfile = new File(pfiles.get_cfiles_directory().getAbsolutePath() + "/" + name);
			this.ifile = new File(pfiles.get_ifiles_directory().getAbsolutePath() + "/" + name);
			this.sfile = new File(pfiles.get_sfiles_directory().getAbsolutePath() + "/" + name);
			this.mfile = new File(pfiles.get_mfiles_directory().getAbsolutePath() + "/" + name);
			this.ufile = new File(pfiles.get_mutants_directory().getAbsolutePath() + "/" + name + ".m");
			/* update sizeof_template, ast_tree, cir_tree, mutant_space */
			this.update_ast_tree();
			this.update_cir_tree();
			this.update_mutant_space();
		}
	}
	
	/* getters */
	/**
	 * @return the code part in which the file is managed
	 */
	public MuTestCodeFiles get_code_part() { return this.files; }
	/**
	 * @return the unique name of the files in code space
	 */
	public String get_name() { return this.name; }
	/**
	 * @return the source code file before pre-processed
	 */
	public File get_cfile() { return this.cfile; }
	/**
	 * @return the source code file after pre-processed 
	 */
	public File get_ifile() { return this.ifile; }
	/**
	 * @return the source code file with instrumentation
	 */
	public File get_sfile() { return this.sfile; }
	/**
	 * @return the source code file seeded with mutation
	 */
	public File get_mfile() { return this.mfile; }
	/**
	 * @return the data file that preserves mutants w.r.t. the file
	 */
	public File get_mutant_data_file() { return this.ufile; }
	/**
	 * @return the template to compute sizeof operation in static
	 */
	public CRunTemplate get_sizeof_template() { return this.sizeof_template; }
	/**
	 * @return  the abstract syntactic tree parsed from the c file
	 */
	public AstTree get_ast_tree() { return ast_tree; }
	/**
	 * @return the C-intermediate representation generated from file
	 */
	public CirTree get_cir_tree() { return cir_tree; }
	/**
	 * @return the space of mutations seeded in the file as specified
	 */
	public MutantSpace get_mutant_space() { return mutant_space; }
	
	/* updating methods */
	/**
	 * update the AST in the file
	 * @throws Exception
	 */
	private void update_ast_tree() throws Exception {
		MuTestProjectConfig config = this.files.get_project().get_config();
		if(this.ifile.exists()) {
			this.sizeof_template = 
					new CRunTemplate(config.get_sizeof_template_file());
			this.ast_tree = CTranslate.parse(
					this.ifile, config.get_lang_standard(), sizeof_template);
			this.mutant_space = new MutantSpace(this.ast_tree);
		}
	}
	/**
	 * update the C-intermediate representation from AST
	 * @throws Exception
	 */
	private void update_cir_tree() throws Exception {
		if(this.ast_tree != null) {
			this.cir_tree = CTranslate.parse(ast_tree, sizeof_template);
		}
	}
	/**
	 * load the data of mutant data file to the mutant space
	 * @throws Exception
	 */
	private void update_mutant_space() throws Exception {
		if(this.mutant_space != null) {
			this.mutant_space.clear();
			if(this.ufile.exists()) {
				this.mutant_space.load(this.ufile);
			}
		}
	}
	/**
	 * input the cfile in the code space
	 * @param cfile
	 * @throws Exception
	 */
	private void update_cfile(File cfile) throws Exception {
		if(cfile == null || !cfile.exists())
			throw new IllegalArgumentException("Invalid cfile: null");
		else if(cfile.getAbsolutePath().equals(cfile.getAbsolutePath())) 
			throw new IllegalArgumentException("Invalid cpath: same");
		else {
			FileOperations.copy(cfile, this.cfile);
		}
	}
	/**
	 * update the xxx.i file
	 * @throws Exception
	 */
	private void update_ifile() throws Exception {
		MuTestProjectConfig config = files.get_project().get_config();
		MuCommandUtil command_util = config.get_command_util();
		FileOperations.delete(ifile);
		
		if(this.cfile.exists()) {
			/* prepare the parameters and do preprocess */
			List<File> hdirs = this.files.get_hdirs();
			List<String> parameters = new ArrayList<String>();
			parameters.add("-imacros");
			parameters.add(config.get_preprocess_macro_file().getAbsolutePath());
			for(String parameter : config.get_compile_parameters()) {
				parameters.add(parameter);
			}
			if(!command_util.do_preprocess(config.get_compiler(), 
					this.cfile, this.ifile, hdirs, parameters)) {
				throw new RuntimeException("Unable to get " + ifile.getAbsolutePath());
			}
		}
	}
	/**
	 * update the xxx.c in sfiles/ after update_ast_tree()
	 * @throws Exception
	 */
	private void update_sfile() throws Exception {
		FileOperations.delete(this.sfile);;
		MuTestProjectFiles pfiles = files.get_project().get_files();
		if(this.ast_tree != null) {
			String code = MutaCodeGeneration.
					instrument_code(ast_tree, pfiles.get_instrument_txt_file());
			FileOperations.write(this.sfile, code);
		}
	}
	
	/* setters */
	/**
	 * update cfile, ifile, ast_tree, cir_tree and sfile
	 * and reload all the mutants in the space
	 * @param cfile
	 * @throws Exception
	 */
	protected void set_cfile(File cfile) throws Exception {
		this.update_cfile(cfile);
		this.update_ifile();
		this.update_ast_tree();
		this.update_cir_tree();
		this.update_sfile();
		this.update_mutant_space();
	}
	/**
	 * re-generate the mutants in the space w.r.t. the AST of the ifile
	 * in the memory and update the mutants/xxx.c.m
	 * @param mutation_classes
	 * @throws Exception
	 */
	protected void set_mutant_space(Iterable<MutaClass> mutation_classes) throws Exception {
		if(mutation_classes == null)
			throw new IllegalArgumentException("Invalid mutation_classes");
		else if(this.ast_tree == null)
			throw new IllegalArgumentException("Invalid access: no ast");
		else {
			this.mutant_space.clear();
			this.mutant_space.update(mutation_classes);
			this.mutant_space.save(this.ufile);
		}
	}
	
}
