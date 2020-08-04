package com.jcsa.jcparse.test.path;

import java.util.ArrayList;
import java.util.List;

/**
 * The execution path defined based on AST-node
 * @author yukimula
 *
 */
public class AstExecutionPath {
	
	/* constructor */
	/** the sequence of execution path in form of AST-node **/
	private List<AstExecutionNode> nodes;
	protected AstExecutionPath() {
		this.nodes = new ArrayList<AstExecutionNode>();
	}
	
	/* getters */
	/**
	 * @return the number of nodes in the execution path
	 */
	public int number_of_nodes() { return this.nodes.size(); }
	/**
	 * @return the sequence of the execution nodes in path
	 */
	public Iterable<AstExecutionNode> get_nodes() { return this.nodes; }
	/**
	 * @param k
	 * @return the kth node in the sequence of execution path
	 * @throws IllegalArgumentException
	 */
	public AstExecutionNode get_node(int k) throws IllegalArgumentException {
		return this.nodes.get(k);
	}
	
	/* setter */
	protected AstExecutionNode new_node(AstExecutionUnit unit) throws IllegalArgumentException {
		AstExecutionNode node = new AstExecutionNode(this, nodes.size(), unit);
		this.nodes.add(node);
		return node;
	}
	
}
