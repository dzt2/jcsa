package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;

public class AstExecutionEdge {
	
	/* attributes */
	/** type of the execution flow **/
	private CirExecutionFlowType type;
	/** the source node from which the edge points to another **/
	private AstExecutionNode source;
	/** the target node to which the edge points from another **/
	private AstExecutionNode target;
	
	/* constructor */
	/**
	 * @param type the type of the execution flow
	 * @param source the source node from which the edge points to another
	 * @param target the target node to which the edge points from another
	 * @throws IllegalArgumentException
	 */
	protected AstExecutionEdge(CirExecutionFlowType type,
			AstExecutionNode source, AstExecutionNode target) 
					throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target");
		else {
			this.type = type;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return type of the execution flow
	 */
	public CirExecutionFlowType get_type() { return this.type; }
	/**
	 * @return the source node from which the edge points to another
	 */
	public AstExecutionNode get_source() { return this.source; }
	/**
	 * @return the target node to which the edge points from another
	 */
	public AstExecutionNode get_target() { return this.target; }
	
}
