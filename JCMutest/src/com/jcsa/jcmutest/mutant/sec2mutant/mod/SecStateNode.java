package com.jcsa.jcmutest.mutant.sec2mutant.mod;

import java.util.LinkedList;
import java.util.List;

public class SecStateNode {
	
	/* attributes */
	/** the graph in which the node is created **/
	private SecStateGraph graph;
	/** the unit that uniquely defines the node **/
	private SecStateUnit unit;
	/** edges that point to this node from others **/
	protected List<SecStateEdge> in;
	/** edges that point from this node to others **/
	protected List<SecStateEdge> ou;
	
	/* constructor */
	/**
	 * create an isolated node in state transition graph w.r.t. the unit as given
	 * @param graph
	 * @param unit
	 * @throws Exception
	 */
	protected SecStateNode(SecStateGraph graph, SecStateUnit unit) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(unit == null)
			throw new IllegalArgumentException("Invalid unit as null");
		else {
			this.graph = graph;
			this.unit = unit;
			this.in = new LinkedList<SecStateEdge>();
			this.ou = new LinkedList<SecStateEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the graph in which the node is created
	 */
	public SecStateGraph get_graph() { return this.graph; }
	/**
	 * @return the unit that uniquely defines the node
	 */
	public SecStateUnit get_unit() { return this.unit; }
	/**
	 * @return edges that point to this node from others
	 */
	public Iterable<SecStateEdge> get_in_edges() { return this.in; }
	/**
	 * @return edges that point from this node to others
	 */
	public Iterable<SecStateEdge> get_ou_edges() { return this.ou; }
	
	/* setters */
	/**
	 * remove this node from the graph
	 */
	protected void delete() {
		this.graph = null;
		this.unit = null;
		this.in.clear();
		this.ou.clear();
		this.in = null;
		this.ou = null;
	}
	
}
