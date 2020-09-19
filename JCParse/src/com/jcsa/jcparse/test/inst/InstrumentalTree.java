package com.jcsa.jcparse.test.inst;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;

public class InstrumentalTree {
	
	/* definitions */
	/** template provides decode and encode of byte values **/
	private CRunTemplate template;
	/** abstract syntax tree of C program from which the instrumental
	 *  lines and nodes will be constructed **/
	private AstTree ast_tree;
	/** the set of tree nodes created **/
	private List<InstrumentalNode> nodes;
	/**
	 * create an empty tree for instrumental analysis
	 * @param template
	 * @param ast_tree
	 * @throws Exception
	 */
	private InstrumentalTree(CRunTemplate template, AstTree ast_tree) throws Exception {
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
	 * @return template provides decode and encode of byte values
	 */
	public CRunTemplate get_template() { return this.template; }
	/**
	 * @return abstract syntax tree of C program from which the instrumental
	 * 	 	   lines and nodes will be constructed
	 */
	public AstTree get_ast_tree() { return this.ast_tree; }
	/**
	 * @return the set of tree nodes created
	 */
	public Iterable<InstrumentalNode> get_nodes() { return nodes; }
	/**
	 * @return the number of tree nodes
	 */
	public int number_of_nodes() { return this.nodes.size(); }
	/**
	 * @param index
	 * @return the node w.r.t. the index in this tree
	 * @throws IndexOutOfBoundsException
	 */
	public InstrumentalNode get_node(int index) throws IndexOutOfBoundsException {
		return this.nodes.get(index);
	}
	/**
	 * @return the root of the node in this tree
	 */
	public InstrumentalNode get_root() { return this.nodes.get(0); }
	
	/* setters */
	/**
	 * @return create a new isolated node from the tree
	 */
	protected InstrumentalNode new_node(AstNode location) throws Exception {
		InstrumentalNode node = new InstrumentalNode(
					this, this.nodes.size(), location);
		this.nodes.add(node);
		return node;
	}
	
}
