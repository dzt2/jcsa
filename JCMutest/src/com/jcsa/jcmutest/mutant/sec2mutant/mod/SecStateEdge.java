package com.jcsa.jcmutest.mutant.sec2mutant.mod;

public class SecStateEdge {
	
	/* definition */
	private SecStateUnit unit;
	private SecStateNode source;
	private SecStateNode target;
	protected SecStateEdge(SecStateNode source, SecStateNode 
			target, SecStateUnit constraint) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target");
		else if(constraint == null || !constraint.is_constraint())
			throw new IllegalArgumentException("Invalid constraint");
		else {
			this.source = source;
			this.target = target;
			this.unit = constraint;
		}
	}
	
	/* getters */
	/**
	 * @return the source node from which the edge points to another
	 */
	public SecStateNode get_source() { return this.source; }
	/**
	 * @return the target node to which the edge points from another
	 */
	public SecStateNode get_target() { return this.target; }
	/**
	 * @return the constraint that is required for the edge to occur
	 */
	public SecStateUnit get_constraint() { return this.unit; }
	
}
