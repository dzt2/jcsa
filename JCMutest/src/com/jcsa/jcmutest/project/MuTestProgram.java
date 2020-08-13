package com.jcsa.jcmutest.project;

import java.io.File;

import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.text.CText;

/**
 * It provides interface to manage the access the source code files and mutations
 * generated from.
 * 
 * @author yukimula
 *
 */
public class MuTestProgram {
	
	/* definitions */
	/** template to compute sizeof operation **/
	private CRunTemplate sizeof_template;
	/** abstract syntactic tree **/
	private AstTree ast_tree;
	/** C-intermediate representation **/
	private CirTree cir_tree;
	/** mutant space that manages the mutations of the program **/
	private MutantSpace mspace;
	/**
	 * @param ast_tree abstract syntactic tree
	 * @param cir_tree C-intermediate representation
	 * @param mspace mutant space that manages the mutations of the program
	 * @throws Exception
	 */
	protected MuTestProgram(AstTree ast_tree, CirTree cir_tree,
			CRunTemplate sizeof_template) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree");
		else if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree");
		else {
			this.ast_tree = ast_tree;
			this.cir_tree = cir_tree;
			this.sizeof_template = sizeof_template;
			this.mspace = new MutantSpace(ast_tree);
		}
	}
	
	/* getters */
	/**
	 * @return the xxx.i file after pre-processed from AST is parsed
	 */
	public File get_ifile() { return this.ast_tree.get_source_file(); }
	/**
	 * @return the source code text
	 */
	public CText get_code() { return this.ast_tree.get_source_code(); }
	/**
	 * @return abstract syntactic tree
	 */
	public AstTree get_ast_tree() { return this.ast_tree; }
	/**
	 * @return C-intermediate representation
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @return the mutant space
	 */
	public MutantSpace get_mutant_space() { return this.mspace; }
	/**
	 * @return template to compute sizeof operation
	 */
	public CRunTemplate get_sizeof_template() { return this.sizeof_template; }
	
}
