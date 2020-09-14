package com.jcsa.jcmutest.mutant.sec2mutant.util;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcparse.lang.sym.SymContexts;

/**
 * It connects the state node in graph with specified constraint.
 * 
 * @author yukimula
 *
 */
public class SecStateEdge {
	
	/** the type of the propagation edge **/
	private SecStateEdgeType type;
	/** constraint required for error to propagate **/
	private SecConstraint constraint;
	/** the source node from which this edge point to another **/
	private SecStateNode source;
	/** the target node to which this edge point from another **/
	private SecStateNode target;
	
	/**
	 * @param type
	 * @param constraint
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected SecStateEdge(SecStateEdgeType type,
			SecConstraint constraint, SecStateNode
			source, SecStateNode target) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.type = type;
			this.constraint = constraint;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the propagation edge
	 */
	public SecStateEdgeType get_type() { return this.type; }
	/**
	 * @return constraint required for error to propagate
	 */
	public SecConstraint get_constraint() { return this.constraint; }
	/**
	 * @param contexts
	 * @return the constraint optimized from original one under contexts
	 * @throws Exception
	 */
	public SecConstraint get_constraint(SymContexts contexts) throws Exception {
		return this.constraint.optimize(contexts);
	}
	/**
	 * @return the source node from which this edge point to another
	 */
	public SecStateNode get_source() { return this.source; }
	/**
	 * @return the target node to which this edge point from another
	 */
	public SecStateNode get_target() { return this.target; }
	
}
