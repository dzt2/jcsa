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
 * It provides the data resource for analysis of source code of a c file in project
 * space, including: cfile, ifile, sfile, mfile, ufile, ast_tree, cir_tree, mutant_space.
 * 
 * @author yukimula
 *
 */
public class MuTestProjectCodeFile {
	
	/* definitions */
	/** the code space in which the item created **/
	private MuTestProjectCodeSpace code_space;
	/** the c file before pre-processed **/
	private File cfile;
	/** the c file after pre-processing **/
	private File ifile;
	/** the c file with instrumentation **/
	private File sfile;
	/** the c file seeded with mutation **/
	private File mfile;
	/** the data file with generated mutants **/
	private File ufile;
	/** the template to compute sizeof operations **/
	private CRunTemplate sizeof_template;
	/** the abstract syntax tree parsed from ifile **/
	private AstTree ast_tree;
	/** the c-intermediate representation from ast **/
	private CirTree cir_tree;
	/** the space of mutants seeded in specified AST **/
	private MutantSpace mutant_space;
	/**
	 * create a code file item in mutation test project space
	 * @param code_space
	 * @param cfile rebuild the file if cfile is different from existing cfiles/xxx.c or reload the old data
	 * 		        if the cfile is identical with the cfiles/xxx.c as specified.
	 * @throws Exception
	 */
	protected MuTestProjectCodeFile(MuTestProjectCodeSpace code_space, File cfile) throws Exception {
		if(code_space == null)
			throw new IllegalArgumentException("Invalid code_sapce: null");
		else if(cfile == null || !cfile.exists() || !cfile.getName().endsWith(".c"))
			throw new IllegalArgumentException("Invalid " + cfile.getAbsolutePath());
		else {
			this.code_space = code_space;
			String name = cfile.getName();
			this.cfile = new File(code_space.get_cfiles_directory().getAbsolutePath() + "/" + name);
			this.ifile = new File(code_space.get_ifiles_directory().getAbsolutePath() + "/" + name);
			this.sfile = new File(code_space.get_sfiles_directory().getAbsolutePath() + "/" + name);
			this.mfile = new File(code_space.get_mfiles_directory().getAbsolutePath() + "/" + name);
			this.ufile = new File(code_space.get_mutants_directory().getAbsolutePath() + "/" + name + ".m");
			this.sizeof_template = new CRunTemplate(code_space.get_project().get_config().get_sizeof_template_file());
			this.ast_tree = null;
			this.cir_tree = null;
			this.mutant_space = null;
			
			/* set the cfile if they are different or load it */
			if(FileOperations.compare(cfile, this.cfile)) {
				this.load();
			}
			else {
				this.set(cfile);
			}
		}
	}
	
	/* getters */
	/**
	 * @return the code space in which the item created
	 */
	public MuTestProjectCodeSpace get_code_space() { return this.code_space; }
	/**
	 * @return the unique name of the code file in space
	 */
	public String get_name() { return this.cfile.getName(); }
	/**
	 * @return the c file before pre-processed
	 */
	public File get_cfile() { return this.cfile; }
	/**
	 * @return the c file after pre-processing
	 */
	public File get_ifile() { return this.ifile; }
	/**
	 * @return the c file with instrumentation
	 */
	public File get_sfile() { return this.sfile; }
	/**
	 * @return the c file seeded with mutation
	 */
	public File get_mfile() { return this.mfile; }
	/**
	 * @return the data file with generated mutations
	 */
	public File get_ufile() { return this.ufile; }
	/**
	 * @return the template to compute sizeof operations
	 */
	public CRunTemplate get_sizeof_template() { return this.sizeof_template; }
	/**
	 * @return the abstract syntax tree parsed from ifile
	 */
	public AstTree get_ast_tree() { return this.ast_tree; }
	/**
	 * @return the c-intermediate representation from ast
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @return the space of mutants seeded in specified AST
	 */
	public MutantSpace get_mutant_space() { return this.mutant_space; }
	
	/* setters */
	/**
	 * delete this item from the code space
	 */
	protected void delete() throws Exception {
		this.code_space = null;
		this.cfile = null;
		this.ifile = null;
		this.sfile = null;
		this.mfile = null;
		this.ufile = null;
		this.sizeof_template = null;
		this.ast_tree = null;
		this.cir_tree = null;
		this.mutant_space.clear();
		this.mutant_space = null;
	}
	/**
	 * load the data from existing code files
	 * @throws Exception
	 */
	private void load() throws Exception {
		/* 1. rebuild the ast-tree and cir-tree by parsing ifile */
		this.ast_tree = CTranslate.parse(ifile, 
				this.code_space.get_project().get_config().get_lang_standard(), 
				this.sizeof_template);
		this.cir_tree = CTranslate.parse(this.ast_tree, this.sizeof_template);
		this.mutant_space = new MutantSpace(this.ast_tree, this.cir_tree);
		
		/* 2. update the instrumental code file */
		String code = MutaCodeGeneration.instrument_code(ast_tree, this.
				code_space.get_project().get_files().get_instrument_txt_file());
		FileOperations.write(this.sfile, code);
		
		/* 3. load the mutations from mutant data file */
		if(this.ufile.exists()) {
			this.mutant_space.load(this.ufile);
		}
	}
	/**
	 * set the code file and re-generate ifile and reload data
	 * @param cfile
	 * @throws Exception
	 */
	private void set(File cfile) throws Exception {
		/* 0. declarations */
		MuTestProjectConfig config = this.code_space.get_project().get_config();
		MuCommandUtil command_util = config.get_command_util();
		
		/* 1. initialize the space by removing old files */
		FileOperations.delete(this.cfile);
		FileOperations.delete(this.ifile);
		FileOperations.delete(this.sfile);
		FileOperations.delete(this.mfile);
		FileOperations.delete(this.ufile);
		
		/* 2. copy cfile and generate ifile */
		FileOperations.copy(cfile, this.cfile);
		List<String> parameters = new ArrayList<String>();
		for(String parameter : config.get_compile_parameters()) {
			parameters.add(parameter);
		}
		parameters.add("-imacros");
		parameters.add(config.get_preprocess_macro_file().getAbsolutePath());
		if(!command_util.do_preprocess(config.get_compiler(), this.cfile, 
				this.ifile, this.code_space.get_hdirs(), parameters)) {
			throw new RuntimeException("Unable to pre-process " + cfile.getName());
		}
		
		/* 3. update sfile, ast-tree, cir-tree and mutant-space(empty) */
		this.load();
	}
	/**
	 * update the mutants space by setting its mutation class and save in data file
	 * @param mutation_classes
	 * @throws Exception
	 */
	protected void set(Iterable<MutaClass> mutation_classes) throws Exception {
		this.mutant_space.clear();
		this.mutant_space.update(mutation_classes);
		this.mutant_space.save(this.ufile);
	}
	
}
