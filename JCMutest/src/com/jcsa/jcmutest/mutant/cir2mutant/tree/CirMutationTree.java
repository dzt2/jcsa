package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymConditions;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;

public class CirMutationTree {
	
	/* attributes */
	private Mutant mutant;
	private CirMutationNode root;
	/**
	 * create an empty tree for representing killable execution of mutant m
	 * @param mutant
	 * @throws IllegalArgumentException
	 */
	protected CirMutationTree(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("invalid mutant as null");
		}
		else {
			this.mutant = mutant;
			this.root = new CirMutationNode(this, SymConditions.ast_kill(mutant));
		}
	}
	
	/* getters */
	/**
	 * @return the mutant that the tree describes its execution process
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return AST
	 */
	public AstTree get_ast_tree() { return this.mutant.get_space().get_ast_tree(); }
	/**
	 * @return CIR
	 */
	public CirTree get_cir_tree() { return this.mutant.get_space().get_cir_tree(); }
	/**
	 * @return the roots of this tree
	 */
	public CirMutationNode get_root() { return this.root; }
	
}
