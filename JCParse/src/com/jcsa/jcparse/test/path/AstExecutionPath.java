package com.jcsa.jcparse.test.path;

import java.util.ArrayList;
import java.util.List;

public class AstExecutionPath {
	
	/* constructor */
	/** the sequence of nodes in the path **/
	private List<AstExecutionNode> nodes;
	/**
	 * create an empty path of execution in form of AST-node
	 */
	public AstExecutionPath() { 
		this.nodes = new ArrayList<AstExecutionNode>(); 
	}
	
	/* getters */
	/**
	 * @return the number of nodes in the path
	 */
	public int length() { return this.nodes.size(); }
	/**
	 * @return the sequence of execution node in the path
	 */
	public Iterable<AstExecutionNode> get_nodes() { return this.nodes; }
	/**
	 * @param k
	 * @return the kth node in the sequence of the path
	 * @throws IndexOutOfBoundsException
	 */
	public AstExecutionNode get_node(int k) throws IndexOutOfBoundsException {
		return this.nodes.get(k);
	}
	/**
	 * @param unit
	 * @return the node w.r.t. the unit
	 * @throws IllegalArgumentException
	 */
	public AstExecutionNode new_node(AstExecutionUnit unit) throws IllegalArgumentException {
		AstExecutionNode node = new AstExecutionNode(this, this.nodes.size(), unit);
		this.nodes.add(node);
		return node;
	}
	
}
