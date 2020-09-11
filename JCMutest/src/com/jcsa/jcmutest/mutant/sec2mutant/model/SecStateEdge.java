package com.jcsa.jcmutest.mutant.sec2mutant.model;

/**
 * It presents the transition between symbolic state in graph.
 * 
 * @author yukimula
 *
 */
public class SecStateEdge {
	
	/* attributes */
	/** type of the state transition **/
	private SecStateEdgeType type;
	/** source from which the edge points to another **/
	private SecStateNode source;
	/** target to which the edge points from another **/
	private SecStateNode target;
	/** the constraint on which the transition allowed **/
	private SecStateUnit constraint;
	
	/* constructor */
	/**
	 * create a state transition edge from source node to target node
	 * with the specified constraint.
	 * @param type
	 * @param source
	 * @param target
	 * @param constraint
	 * @throws Exception
	 */
	protected SecStateEdge(SecStateEdgeType type, SecStateNode source, 
			SecStateNode target, SecStateUnit constraint) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else if(!constraint.is_constraint())
			throw new IllegalArgumentException(constraint.toString());
		else {
			this.type = type;
			this.source = source;
			this.target = target;
			this.constraint = constraint;
		}
	}
	
	/* getters */
	/**
	 * @return type of the state transition
	 */
	public SecStateEdgeType get_type() { return this.type; }
	/**
	 * @return source from which the edge points to another
	 */
	public SecStateNode get_source() { return this.source; }
	/**
	 * @return target to which the edge points from another
	 */
	public SecStateNode get_target() { return this.target; }
	/**
	 * @return the constraint on which the transition allowed
	 */
	public SecStateUnit get_constraint() { return this.constraint; }
	
}
