package com.jcsa.jcmutest.mutant.cir2mutant.paths;

import java.util.ArrayList;
import java.util.Collection;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;

/**
 * It represents the error propagation edge between symbolic instance node w.r.t. a constraint that is required.
 * @author yukimula
 *
 */
public class SymInstanceEdge {
	
	/* definitions */
	/** the symbolic execution node from which the edge points to another **/
	private SymInstanceNode source;
	/** the symbolic execution node to which the edge points from another **/
	private SymInstanceNode target;
	/** the set of symbolic constraints being evaluated on this edge **/
	private Collection<SymConstraint> constraints;
	/**
	 * create the edge pointing from the source to the target
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected SymInstanceEdge(SymInstanceNode source, SymInstanceNode target) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.source = source;
			this.target = target;
			this.constraints = new ArrayList<SymConstraint>();
		}
	}
	
	/* getters */
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
	public Iterable<SymConstraint> get_constraints() { return this.constraints; }
	
	/* setters */
	/**
	 * add the symbolic constraint in the edge for being evaluated
	 * @param constraint
	 * @throws Exception
	 */
	protected void add_constraint(SymConstraint constraint) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else if(!this.constraints.contains(constraint)) {
			this.constraints.add(constraint);
			this.source.get_graph().register_status(constraint);
		}
	}
	/**
	 * remove this edge from the node of the graph
	 */
	protected void delete() {
		if(this.constraints != null) {
			this.source = null;
			this.target = null;
			this.constraints.clear();
			this.constraints = null;
		}
	}
	/**
	 * evaluate the constraints within the edge for error propagation
	 * @param contexts
	 * @throws Exception
	 */
	protected void evaluate(CStateContexts contexts) throws Exception {
		SymInstanceGraph graph = this.source.get_graph();
		for(SymConstraint constraint : this.constraints) {
			graph.get_status(constraint).evaluate(graph.get_cir_mutations(), contexts);
		}
	}
	
}
