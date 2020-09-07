package com.jcsa.jcmutest.mutant.sel2mutant;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.cons.SelConstraint;

/**
 * The edge between SegNode describes the relations that link from a source
 * event to target event where the latter is the consequence of the former.
 * 
 * @author yukimula
 *
 */
public class SegEdge {
	
	/* definitions */
	/** constraint required for the edge to occur **/
	private SelConstraint constraint;
	/** the node from which the edge extends to **/
	private SegNode source;
	/** the node extended from the source via the edge **/
	private SegNode target;
	/**
	 * @param constraint constraint required for the edge to occur
	 * @param source the node from which the edge extends to another
	 * @param target the node extended from the source via the edge
	 * @throws Exception
	 */
	protected SegEdge(SelConstraint constraint, 
			SegNode source, SegNode target) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target");
		else {
			this.constraint = constraint;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return constraint required for the edge to occur
	 */
	public SelConstraint get_constraint() {
		return this.constraint;
	}
	/**
	 * @return the node from which the edge extends to another
	 */
	public SegNode get_source() {
		return this.source;
	}
	/**
	 * @return the node extended from the source via the edge
	 */
	public SegNode get_target() {
		return this.target;
	}
	
}
