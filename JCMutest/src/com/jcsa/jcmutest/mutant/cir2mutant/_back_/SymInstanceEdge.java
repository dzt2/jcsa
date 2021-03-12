package com.jcsa.jcmutest.mutant.cir2mutant._back_;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;


/**
 * It represents the error propagation edge between symbolic instance node w.r.t. a constraint that is required.
 * @author yukimula
 *
 */
public class SymInstanceEdge {
	
	/* definitions */
	/** the type of the symbolic instance edge **/
	private SymInstanceEdgeType type;
	/** the symbolic execution node from which the edge points to another **/
	private SymInstanceNode source;
	/** the symbolic execution node to which the edge points from another **/
	private SymInstanceNode target;
	/** the set of symbolic constraints being evaluated on this edge **/
	private SymConstraint constraint;
	/** the instance that records the status of evaluating its constraint **/
	private SymInstanceStatus status;
	/**
	 * create the edge pointing from the source to the target
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected SymInstanceEdge(SymInstanceEdgeType type, SymInstanceNode source, 
			SymInstanceNode target, SymConstraint constraint) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type as null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			this.type = type;
			this.source = source;
			this.target = target;
			this.constraint = constraint;
			this.status = new SymInstanceStatus(this.constraint);
		}
	}
	
	/* getters */
	/**
	 * @return the type of the symbolic instance edge
	 */
	public SymInstanceEdgeType get_type() { return this.type; }
	/**
	 * @return the node from which the edge points to another
	 */
	public SymInstanceNode get_source() { return this.source; }
	/**
	 * @return the node to which the edge points from another
	 */
	public SymInstanceNode get_target() { return this.target; }
	/**
	 * @return the set of symbolic constraints being evaluated on this edge
	 */
	public SymConstraint get_constraint() { return this.constraint; }
	/**
	 * @return the instance that records the status of the constraint required in the propagation edge.
	 */
	public SymInstanceStatus get_status() { return this.status; }
	
	/* setters */
	/**
	 * remove this edge from the node of the graph
	 */
	protected void delete() {
		if(this.constraint != null) {
			this.source = null;
			this.target = null;
			this.constraint = null;
			this.status = null;
		}
	}
	
}
