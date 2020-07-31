package com.jcsa.jcparse.test.path;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;

public class AstExecutionPath {
	
	/* definitions */
	/** sequence of node in execution path **/
	private List<AstExecutionNode> nodes;
	/**
	 * private constructor for static parsing
	 */
	private AstExecutionPath() {
		this.nodes = new ArrayList<AstExecutionNode>();
	}
	
	/* getters */
	/**
	 * @return the length of the execution path
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @return the sequence of nodes in the path
	 */
	public Iterable<AstExecutionNode> get_nodes() { return nodes; }
	/**
	 * @param k
	 * @return the kth node in the path
	 * @throws IndexOutOfBoundsException
	 */
	public AstExecutionNode get_node(int k) throws IndexOutOfBoundsException {
		return this.nodes.get(k);
	}
	/** 
	 * @return the first node in the path
	 */
	public AstExecutionNode get_head() {
		if(this.nodes.isEmpty()) return null;
		else return this.nodes.get(0);
	}
	/**
	 * @return the final node in the path
	 */
	public AstExecutionNode get_tail() {
		if(this.nodes.isEmpty()) return null;
		else return this.nodes.get(nodes.size() - 1);
	}
	
	/* setters */
	/**
	 * @param type  the type of the execution unit
	 * @param location the location where the execution is performed
	 * @param status the bytes that describe the status of expression
	 * @return the newly created unit that defines the next node
	 * @throws IllegalArgumentException
	 */
	private AstExecutionUnit new_unit(AstExecutionType type, 
			AstNode location, byte[] status) throws IllegalArgumentException {
		return new AstExecutionUnit(type, location, status);
	}
	/**
	 * add the unit with its node in the tail of the path
	 * @param unit 
	 * @param flow_type the type of the execution flow from prior to the next
	 * @return the newly created node in the path.
	 * @throws Exception
	 */
	private AstExecutionNode add_node(AstExecutionUnit unit, CirExecutionFlowType flow_type) throws Exception {
		AstExecutionNode node = new AstExecutionNode(this, nodes.size(), unit);
		AstExecutionNode tail = this.get_tail();
		if(tail != null) {
			AstExecutionEdge edge = new AstExecutionEdge(flow_type, tail, node);
			tail.next_edge = edge; node.prev_edge = edge;
		}
		this.nodes.add(node); return node;
	}
	
	
	
	
}
