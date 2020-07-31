package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;

public class AstExecutionEdge {
	
	/* attributes */
	/** the type of the flow from source to target **/
	private CirExecutionFlowType type;
	/** the source node from which the flow points **/
	private AstExecutionNode source;
	/** the target node to which this flow points **/
	private AstExecutionNode target;
	
	/* constructor */
	/**
	 * @param type the type of the flow from source to target 
	 * @param source the source node from which the flow points
	 * @param target the target node to which this flow points
	 * @throws IllegalArgumentException
	 */
	protected AstExecutionEdge(CirExecutionFlowType type, 
			AstExecutionNode source, AstExecutionNode target) 
					throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.type = type;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the flow from source to target
	 */
	public CirExecutionFlowType get_type() { return this.type; }
	/**
	 * @return the source node from which the flow points
	 */
	public AstExecutionNode get_source() { return this.source; }
	/**
	 * @return the target node to which this flow points
	 */
	public AstExecutionNode get_target() { return this.target; }
	
}
