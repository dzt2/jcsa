package com.jcsa.jcmutest.mutant.cir2mutant.paths;

import java.util.ArrayList;
import java.util.Collection;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * The node of symbolic instance(s) refers to a graph of symbolic instances (as state error)
 * @author yukimula
 *
 */
public class SymInstanceNode {
	
	/* definitions */
	/** the graph in which the node is created **/
	private SymInstanceGraph graph;
	/** the execution of statement where the node is evaluated **/
	private CirExecution execution;
	/** the symbolic state error being evaluated at this node **/
	private SymStateError state_error;
	/** the set of symbolic instance edges pointing to this node from others **/
	private Collection<SymInstanceEdge> in_edges;
	/** the set of symbolic instance edges pointing from this node to others **/
	private Collection<SymInstanceEdge> ou_edges;
	/**
	 * create an isolated node in the graph w.r.t. the execution of given statement
	 * @param graph
	 * @param execution
	 * @throws Exception
	 */
	protected SymInstanceNode(SymInstanceGraph graph, CirExecution execution, SymStateError state_error) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else {
			this.graph = graph;
			this.execution = execution;
			this.state_error = state_error;
			this.in_edges = new ArrayList<SymInstanceEdge>();
			this.ou_edges = new ArrayList<SymInstanceEdge>();
			if(this.state_error != null)
				this.graph.register_status(this.state_error);
		}
	}
	
	/* getters */
	/**
	 * @return the graph in which the node is created
	 */
	public SymInstanceGraph get_graph() { return this.graph; } 
	/**
	 * @return the execution of statement where the node is evaluated
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return whether there are state error with corresponding to the node
	 */
	public boolean has_state_error() { return this.state_error != null; }
	/**
	 * @return the symbolic state error being evaluated at this node
	 */
	public SymStateError get_state_error() { return this.state_error; }
	/**
	 * @return the set of symbolic instance edges pointing to this node from others
	 */
	public Iterable<SymInstanceEdge> get_in_edges() { return this.in_edges; }
	/**
	 * @return the set of symbolic instance edges pointing from this node to others
	 */
	public Iterable<SymInstanceEdge> get_ou_edges() { return this.ou_edges; }
	/**
	 * @return the number of edges pointing to this node
	 */
	public int get_in_degree() { return this.in_edges.size(); }
	/**
	 * @return the number of edges pointing from this node
	 */
	public int get_ou_degree() { return this.ou_edges.size(); }
	
	/* setters */
	/**
	 * remove this node along with its edges from the graph
	 */
	protected void delete() {
		if(this.graph != null) {
			this.graph = null;
			this.execution = null;
			this.state_error = null;
			for(SymInstanceEdge edge : this.ou_edges) {
				edge.delete();
			}
			this.in_edges.clear();
			this.ou_edges.clear();
		}
	}
	/**
	 * @param target
	 * @return connect this node to the target
	 * @throws Exception
	 */
	protected SymInstanceEdge link_to(SymInstanceNode target, SymConstraint constraint) throws Exception {
		for(SymInstanceEdge edge : this.ou_edges) {
			if(edge.get_target() == target) {
				return edge;
			}
		}
		SymInstanceEdge edge = new SymInstanceEdge(this, target, constraint);
		this.ou_edges.add(edge); target.in_edges.add(edge);
		return edge;
	}
	/**
	 * evaluate the state errors in the node w.r.t. the given contexts
	 * @param contexts
	 * @throws Exception
	 */
	protected Boolean evaluate(CStateContexts contexts) throws Exception {
		if(this.state_error == null)
			throw new IllegalArgumentException("No state error to be evaluated.");
		else
			return this.graph.get_status(this.state_error).evaluate(this.graph.get_cir_mutations(), contexts);
	}
	
}
