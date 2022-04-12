package com.jcsa.jcmutest.mutant.sta2mutant.abst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;

/**
 * 	It defines the syntactic tree of program context for state analysis.	<br>
 * 	
 * 	@author yukimula
 *
 */
public class AstContextTree {
	
	/* attributes */
	private	CRunTemplate					template;
	private	AstTree							ast_tree;
	private	CirTree							cir_tree;
	private	List<AstContextNode>			nodes;
	private	Map<AstNode, AstContextNode>	index;
	private	AstContextTree(CRunTemplate template, AstTree ast_tree, CirTree cir_tree) throws Exception {
		if(template == null) {
			throw new IllegalArgumentException("Invalid template: null");
		}
		else if(ast_tree == null) {
			throw new IllegalArgumentException("Invalid ast_tree: null");
		}
		else if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else {
			this.template = template; 
			this.ast_tree = ast_tree; 
			this.cir_tree = cir_tree;
			this.nodes = new ArrayList<AstContextNode>();
			this.index = new HashMap<AstNode, AstContextNode>();
			
			AstContextNode root = this.new_node(
					ast_tree.get_ast_root(), cir_tree.get_root());
			this.connect(ast_tree.get_ast_root(), root);
		}
	}
	
	/* getters */
	/**
	 * @return the sizeof template
	 */
	public	CRunTemplate	get_template()	{ return this.template; }
	/** 
	 * @return the abstract syntax tree
	 */
	public	AstTree			get_ast_tree()	{ return this.ast_tree; }
	/**
	 * @return the C-intermediate trees
	 */
	public	CirTree			get_cir_tree()	{ return this.cir_tree; }
	/**
	 * @return the list of nodes in the context tree
	 */
	public	Iterable<AstContextNode>	get_nodes() { return this.nodes; }
	/**
	 * @return the number of tree nodes in context
	 */
	public	int				number_of_nodes()	{ return this.nodes.size(); }
	/**
	 * @param node_id
	 * @return the node w.r.t. the id in the tree
	 * @throws IndexOutOfBoundsException
	 */
	public	AstContextNode	get_node(int node_id) throws IndexOutOfBoundsException { return this.nodes.get(node_id); }
	/**
	 * @param source
	 * @return whether there exists node w.r.t. the source
	 */
	public	boolean			has_node_of(AstNode source) { return this.index.containsKey(source); }
	/**
	 * @param source
	 * @return the tree node w.r.t. the input source node
	 */
	public	AstContextNode	get_node_of(AstNode source) {
		if(this.index.containsKey(source)) {
			return this.index.get(source);
		}
		else {
			return null;
		}
	}
	/**
	 * @return the root node of the tree (AstTranslationUnit)
	 */
	public	AstContextNode	get_root() { return this.nodes.get(0); }
	
	/* setters */
	/**
	 * @param source
	 * @param target
	 * @return it creates a new node w.r.t. the source-target pair
	 * @throws Exception
	 */
	protected	AstContextNode	new_node(AstNode source, CirNode target) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			AstContextNode node = new AstContextNode(this, 
					this.nodes.size(), source, target);
			this.nodes.add(node); return node;
		}
	}
	/**
	 * It connects the source to the given node.
	 * @param source
	 * @param node
	 * @throws Exception
	 */
	protected	void			connect(AstNode source, AstContextNode node) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(node == null) {
			throw new IllegalArgumentException("Invalid node as null");
		}
		else {
			this.index.put(source, node);
		}
	}
	
}
