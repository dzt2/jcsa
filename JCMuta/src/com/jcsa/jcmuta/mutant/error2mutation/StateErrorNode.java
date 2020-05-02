package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.symb.StateConstraints;

public class StateErrorNode {
	
	/* attributes */
	/** graph where the node created **/
	private StateErrorGraph graph;
	/** identifier of the node in the graph **/
	private int identifier;
	/** the location where the error occurs **/
	private CirNode location;
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
			if(source == null) {
				this.location = null;
				this.errors = new ArrayList<StateError>();
			}
			else {
				this.location = null;
				this.errors = source.get_errors().extend(source);
				if(source.number_of_operands() > 0)
					this.location = (CirNode) source.get_operand(0);
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
	 * get the location where the error occurs in program
	 * @return
	 */
	public CirNode get_location() { return this.location; }
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
	protected StateErrorEdge propagate(StateErrorNode target, StateConstraints constraints) throws Exception {
		if(constraints == null) constraints = new StateConstraints(true); 
		StateErrorEdge edge = new StateErrorEdge(this, target, constraints);
		this.ou.add(edge); target.in.add(edge); return edge;
	}
	
}
