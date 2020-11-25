package com.jcsa.jcmutest.mutant.cir2mutant.path;

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
	private SymConstraint constraint;
	/**
	 * create the edge pointing from the source to the target
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected SymInstanceEdge(SymInstanceNode source, SymInstanceNode target, SymConstraint constraint) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			this.source = source;
			this.target = target;
			this.constraint = constraint;
			this.source.get_graph().register_status(constraint);
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
	public SymConstraint get_constraint() { return this.constraint; }
	
	/* setters */
	/**
	 * remove this edge from the node of the graph
	 */
	protected void delete() {
		if(this.constraint != null) {
			this.source = null;
			this.target = null;
			this.constraint = null;
		}
	}
	/**
	 * evaluate the constraints within the edge for error propagation
	 * @param contexts
	 * @throws Exception
	 */
	protected Boolean evaluate(CStateContexts contexts) throws Exception {
		SymInstanceGraph graph = this.source.get_graph();
		return graph.get_status(this.constraint).evaluate(graph.get_cir_mutations(), contexts);
	}
	
}
