package com.jcsa.jcmutest.mutant.cir2mutant.rtree;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;


/**
 * It maintains the tree node to represent cir-mutation under analysis in
 * one single statement.
 * 
 * @author yukimula
 *
 */
public class CirMutationTree {
	
	/* definitions */
	/** the statement node where the tree is created **/
	private CirMutationNode node;
	/** the root node of the tree **/
	private CirMutationTreeNode root;
	
	/***
	 * create a tree to maintain the cir-mutations in statement
	 * @param mutation
	 * @throws Exception
	 */
	protected CirMutationTree(CirMutationNode node, CirMutation mutation) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else {
			this.node = node;
			this.root = new CirMutationTreeNode(this, mutation);
		}
	}
	
	/* getters */
	/**
	 * @return the statement node where the tree is created
	 */
	public CirMutationNode get_statement_node() { return this.node; }
	/**
	 * @return the root node of this tree
	 */
	public CirMutationTreeNode get_root() { return this.root; }
	
}
