package com.jcsa.jcmuta.project;

import java.io.File;

import com.jcsa.jcmuta.MutationUtil;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.parse.CTranslate;

/**
 * The source file refers to both the source code and mutations generated from it.
 * 
 * @author yukimula
 *
 */
public class MutaSourceFile {
	
	/* attributes */
	/** the files where the source file is created **/
	private MutaSourceFiles files;
	/** the source code file **/
	private File source_file;
	/** the mutant data file **/
	private File mutant_file;
	/** the syntactic tree **/
	private AstTree ast_tree;
	/** the C-like intermediate representation **/
	private CirTree cir_tree;
	/** the space of mutations generated from **/
	private MutantSpace mutant_space;
	
	/* constructor */
	/**
	 * create the source code file to be tested and compiled in the project.
	 * @param files
	 * @param source_file
	 * @throws Exception
	 */
	protected MutaSourceFile(MutaSourceFiles files, File source_file) throws Exception {
		if(files == null)
			throw new IllegalArgumentException("Invalid files: null");
		else if(source_file == null || !source_file.exists())
			throw new IllegalArgumentException("Invalid source file");
		else {
			/** set the parent node **/	this.files = files; 
			
			/** create the source code file as copy in the directory **/
			this.source_file = new File(
					files.get_source_directory().getAbsolutePath() 
					+ File.separator + source_file.getName());
			if(!this.source_file.getAbsolutePath().equals(source_file.getAbsolutePath())) 
				MutationUtil.copy_file(source_file, this.source_file);
			
			/** create the mutation data file for the source code **/
			this.mutant_file = new File(files.get_mutant_directory() 
					+ File.separator + source_file.getName() + ".mut");
			if(!this.mutant_file.exists()) this.mutant_file.createNewFile();
			
			/** parsing the abstract program information **/
			this.ast_tree = CTranslate.parse(this.source_file, 
					this.files.get_project().config.get_lang_standard(),
					this.files.get_project().config.get_sizeof_template());
			this.cir_tree = CTranslate.parse(this.ast_tree);
			
			/** create the mutation space generated from the source file **/
			this.mutant_space = new MutantSpace(this);
		}
	}
	
	/* getters */
	/**
	 * get the files where this file is defined
	 * @return
	 */
	public MutaSourceFiles get_files() { return this.files; }
	/**
	 * get the source code file to be tested and mutated
	 * @return
	 */
	public File get_source_file() { return this.source_file; }
	/**
	 * get the mutation data file to preserve the mutants
	 * @return
	 */
	public File get_mutant_file() { return this.mutant_file; }
	/**
	 * get the abstract syntactic tree of the source file
	 * @return
	 */
	public AstTree get_ast_tree() { return this.ast_tree; }
	/**
	 * get the C-like intermediate representation of the code
	 * @return
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * get the mutant spcace where the mutations generated from 
	 * the source file are preserved.
	 * @return
	 */
	public MutantSpace get_mutant_space() { return this.mutant_space; }
	
}
