package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;

public class CirMutationEdge {
	
	/* definitions */
	/** the type of the mutation edge **/
	private CirMutationEdgeType type;
	/** the node from which the edge points to another **/
	private CirMutationNode source;
	/** the node to which the edge points from another **/
	private CirMutationNode target;
	/** constraint required for the edge to propagate **/
	private SymConstraint constraint;
	/** to record the state on the constraint of the edge **/
	private CirMutationStatus status;
	
	/* constructor */
	/**
	 * create an edge from source to the target with constraint
	 * @param source
	 * @param target
	 * @param constraint
	 * @throws Exception
	 */
	protected CirMutationEdge(CirMutationEdgeType type,
			CirMutationNode source, 
			CirMutationNode target, 
			SymConstraint constraint) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
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
			this.status = new CirMutationStatus();
		}
	}
	
	/* getters */
	public CirMutationEdgeType get_type() { return this.type; }
	/**
	 * @return the node from which the edge points to another
	 */
	public CirMutationNode get_source() { return this.source; }
	/**
	 * @return the node to which the edge points from another
	 */
	public CirMutationNode get_target() { return this.target; }
	/**
	 * @return the constraint required for the edge to propagate
	 */
	public SymConstraint get_constraint() { return this.constraint; }
	/**
	 * @return to record the state on the constraint of the edge
	 */
	public CirMutationStatus get_status() { return this.status; }
	
	/* setters */
	/**
	 * clear the status records in the edge constraint
	 */
	public void reset_status() { this.status.clear(); }
	/**
	 * append the records to the status of the edge constraint
	 * @param contexts
	 * @throws Exception
	 */
	public Boolean append_status(CStateContexts contexts) throws Exception {
		CirMutations cir_mutations = this.source.get_graph().get_cir_mutations();
		SymConstraint constraint = cir_mutations.optimize(this.constraint, contexts);
		return this.status.append(constraint);
	}
	
}
