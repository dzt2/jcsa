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
	/**
	 * @param unit the unit of which node is the head
	 * @throws IllegalArgumentException
	 */
	private AstExecutionPath(AstExecutionUnit unit) throws IllegalArgumentException {
		if(unit == null)
			throw new IllegalArgumentException("Invalid unit: null");
		else {
			this.nodes = new ArrayList<AstExecutionNode>();
			this.nodes.add(new AstExecutionNode(this, 0, unit));
		}
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
	/**
	 * @return the first node in the execution path
	 */
	public AstExecutionNode get_head_node() { return this.nodes.get(0); }
	/**
	 * @return the final node in the execution path
	 */
	public AstExecutionNode get_tail_node() { return this.nodes.get(nodes.size() - 1); }
	
	/* setter */
	/**
	 * append a new node w.r.t. the unit and in_flow w.r.t. the flow type
	 * @param flow_type
	 * @param next_unit
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected AstExecutionNode add(AstExecutionFlowType flow_type,
			AstExecutionUnit next_unit) throws IllegalArgumentException {
		AstExecutionNode source = this.get_tail_node();
		AstExecutionNode target = new AstExecutionNode(this, this.nodes.size(), next_unit);
		AstExecutionFlow flow = new AstExecutionFlow(flow_type, source, target);
		source.ou_flow = flow; 
		target.in_flow = flow;
		this.nodes.add(target);
		return target;
	}
	
}
