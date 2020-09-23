package com.jcsa.jcmutest.mutant.sec2mutant.util;

import java.util.LinkedList;
import java.util.List;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecExecutionConstraint;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * Each node in state graph is an execution-constraint or state-error.
 * 
 * @author yukimula
 *
 */
public class SecStateNode {
	
	/* attributes */
	/** the graph where the node is created **/
	private SecStateGraph graph;
	/** the description that defines this node **/
	private SecDescription description;
	/** edges that point to this node from others **/
	private List<SecStateEdge> in_edges;
	/** edges that point from this node to others **/
	private List<SecStateEdge> ou_edges;
	
	/* constructor */
	/**
	 * create an isolated node w.r.t. the description within the graph
	 * @param graph
	 * @param description
	 * @throws Exception
	 */
	protected SecStateNode(SecStateGraph graph, SecDescription description) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(description == null)
			throw new IllegalArgumentException("Invalid description");
		else if(description instanceof SecExecutionConstraint
				|| description instanceof SecStateError) {
			this.graph = graph;
			this.description = description;
			this.in_edges = new LinkedList<SecStateEdge>();
			this.ou_edges = new LinkedList<SecStateEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the graph where the node is created
	 */
	public SecStateGraph get_graph() { return this.graph; }
	/**
	 * @return whether the node is defined as state error
	 */
	public boolean is_state_error() {
		return this.description instanceof SecStateError;
	}
	/**
	 * @return whether the node is defined as constraint of reaching statement
	 */
	public boolean is_constraint() {
		return this.description instanceof SecExecutionConstraint;
	}
	/**
	 * @return get the constraint for reaching a target statement or null
	 */
	public SecExecutionConstraint get_constraint() {
		if(this.description instanceof SecExecutionConstraint)
			return (SecExecutionConstraint) this.description;
		else
			return null;
	}
	/**
	 * @param contexts
	 * @return the constraint optimized from the contexts or null
	 * @throws Exception
	 */
	public SecConstraint get_constraint(CStateContexts contexts) throws Exception {
		SecConstraint constraint = this.get_constraint();
		if(constraint != null)
			constraint = constraint.optimize(contexts);
		return constraint;
	}
	/**
	 * @return get the state error that the node describes
	 */
	public SecStateError get_state_error() {
		if(this.description instanceof SecStateError)
			return (SecStateError) this.description;
		else
			return null;
	}
	/**
	 * @param contexts
	 * @return the state errors exteded from the source or null
	 * @throws Exception
	 */
	public Iterable<SecStateError> get_state_errors(CStateContexts contexts) throws Exception {
		SecStateError state_error = this.get_state_error();
		if(state_error == null) {
			return null;
		}
		else {
			return state_error.extend(contexts);
		}
	}
	/**
	 * @return the edges that point to this node from the others
	 */
	public Iterable<SecStateEdge> get_in_edges() { return in_edges; }
	/**
	 * @return the edges that point from this node to the others
	 */
	public Iterable<SecStateEdge> get_ou_edges() { return ou_edges; }
	@Override
	public String toString() {
		return this.description.toString();
	}
	
	/* setters */
	/**
	 * @param type
	 * @param target
	 * @param constraint
	 * @return the edge that connects this node to the target with the specified
	 *  	   type and constraint for error propagation.
	 * @throws Exception
	 */
	public SecStateEdge link_to(SecStateEdgeType type, SecStateNode target, 
			SecConstraint constraint) throws Exception {
		SecStateEdge edge = new SecStateEdge(type, constraint, this, target);
		this.ou_edges.add(edge);
		target.in_edges.add(edge);
		return edge;
	}
	/**
	 * delete this node from the graph
	 */
	protected void delete() {
		this.graph = null;
		this.description = null;
		this.in_edges.clear();
		this.ou_edges.clear();
		this.in_edges = null;
		this.ou_edges = null;
	}
	
}
