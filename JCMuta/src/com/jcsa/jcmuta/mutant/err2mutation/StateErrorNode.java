package com.jcsa.jcmuta.mutant.err2mutation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;

public class StateErrorNode {
	
	/* attributes */
	/** graph where the node created **/
	private StateErrorGraph graph;
	/** identifier of the node in the graph **/
	private int identifier;
	/** get the errors definint this node **/
	private List<StateError> errors;
	/** errors propagation from this node **/
	private List<StateErrorEdge> in;
	/** errors causing to this node in graph **/
	private List<StateErrorEdge> ou;
	
	/* constructor */
	/**
	 * create a node with state errors in the graph with respect to an identifier
	 * @param graph
	 * @param identifier
	 * @param source
	 * @throws Exception
	 */
	protected StateErrorNode(StateErrorGraph graph, int identifier, StateError source) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else {
			this.graph = graph; this.identifier = identifier;
			if(source == null)
				this.errors = new ArrayList<StateError>();
			else {
				this.errors = source.get_errors().extend(source);
			}
			this.in = new LinkedList<StateErrorEdge>();
			this.ou = new LinkedList<StateErrorEdge>();
		}
	}
	
	/* getters */
	/**
	 * get the graph where the node is created
	 * @return
	 */
	public StateErrorGraph get_graph() { return this.graph; }
	/**
	 * get the identifier of the node in graph
	 * @return
	 */
	public int get_identifier() { return this.identifier; }
	/**
	 * get the errors in this node
	 * @return
	 */
	public Iterable<StateError> get_errors() { return errors; }
	/**
	 * get the propagation from others to this node
	 * @return
	 */
	public Iterable<StateErrorEdge> get_in_edges() { return this.in; }
	/**
	 * get the propagation from this node to others
	 * @return
	 */
	public Iterable<StateErrorEdge> get_ou_edges() { return this.ou; }
	/**
	 * number of edges to this node from others
	 * @return
	 */
	public int get_in_degree() { return this.in.size(); }
	/**
	 * number of edges from this node to others
	 * @return
	 */
	public int get_ou_degree() { return this.ou.size(); }
	
	/* setter */
	/**
	 * propagate from this node to the target without constraints
	 * @param target
	 * @return
	 * @throws Exception
	 */
	protected StateErrorEdge propagate(StateErrorNode target) throws Exception {
		return this.propagate(target, null);
	}
	/**
	 * propagate from this node to the target
	 * @param target
	 * @param constraints
	 * @return
	 * @throws Exception
	 */
	protected StateErrorEdge propagate(StateErrorNode target, Iterable<SymExpression> constraints) throws Exception {
		StateErrorEdge edge = new StateErrorEdge(this, target);
		if(constraints != null) {
			for(SymExpression constraint : constraints) {
				edge.add_constraint(constraint);
			}
		}
		else {
			edge.add_constraint(SymFactory.new_constant(true));
		}
		this.ou.add(edge);
		target.in.add(edge);
		return edge;
	}
	
}
