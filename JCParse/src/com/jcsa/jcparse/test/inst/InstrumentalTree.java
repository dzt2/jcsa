package com.jcsa.jcparse.test.inst;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;

public class InstrumentalTree {
	
	/* definitions */
	/** template to decode instrumental bytes **/
	private CRunTemplate template;
	/** abstract syntactic tree of code being instrumented **/
	private AstTree ast_tree;
	/** the nodes created in this tree, access by their id **/
	private List<InstrumentalNode> nodes;
	
	/* constructor */
	/**
	 * create a tree to record instrumental data
	 * @param template
	 * @param ast_tree
	 * @throws IllegalArgumentException
	 */
	protected InstrumentalTree(CRunTemplate template, AstTree 
			ast_tree) throws IllegalArgumentException {
		if(template == null)
			throw new IllegalArgumentException("Invalid template: null");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else {
			this.template = template;
			this.ast_tree = ast_tree;
			this.nodes = new ArrayList<InstrumentalNode>();
		}
	}
	
	/* getters */
	/**
	 * @return template to decode instrumental bytes
	 */
	public CRunTemplate get_template() { return this.template; }
	/**
	 * @return abstract syntactic tree of code being instrumented
	 */
	public AstTree get_ast_tree() { return this.ast_tree; }
	/**
	 * @return the nodes created in this tree, access by their id
	 */
	public Iterable<InstrumentalNode> get_nodes() { return this.nodes; }
	/**
	 * @return the number of tree nodes
	 */
	public int number_of_nodes() { return this.nodes.size(); }
	/**
	 * @param id
	 * @return the tree node w.r.t. the id as given
	 * @throws IndexOutOfBoundsException
	 */
	public InstrumentalNode get_node(int id) throws IndexOutOfBoundsException {
		return this.nodes.get(id);
	}
	/**
	 * @param location
	 * @return create a new node in the treew w.r.t. the location as given
	 * @throws IllegalArgumentException
	 */
	protected InstrumentalNode new_node(AstNode location) throws IllegalArgumentException {
		InstrumentalNode node = new InstrumentalNode(this, this.nodes.size(), location);
		this.nodes.add(node);
		return node;
	}
	
}
