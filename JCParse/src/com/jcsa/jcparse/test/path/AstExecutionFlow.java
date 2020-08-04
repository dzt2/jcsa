package com.jcsa.jcparse.test.path;

/**
 * [type, source, target]
 * @author yukimula
 *
 */
public class AstExecutionFlow {
	
	/* attributes */
	/** type of the execution flow **/
	private AstExecutionFlowType type;
	/** the node from which the flow points to another **/
	private AstExecutionNode source;
	/** the node to which the flow points from another **/
	private AstExecutionNode target;
	
	/* constructor */
	/**
	 * @param type type of the execution flow
	 * @param source the node from which the flow points to another
	 * @param target the node to which the flow points from another
	 * @throws IllegalArgumentException
	 */
	protected AstExecutionFlow(AstExecutionFlowType type,
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
	 * @return type of the execution flow
	 */
	public AstExecutionFlowType get_type() { return this.type; }
	/**
	 * @return the node from which the flow points to another
	 */
	public AstExecutionNode get_source() { return this.source; }
	/**
	 * @return the node to which the flow points from another
	 */
	public AstExecutionNode get_target() { return this.target; }
	
}
