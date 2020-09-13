package com.jcsa.jcmutest.mutant.sec2mutant.mod;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;

public class SecStateNode {
	
	/* definitions */
	/** the graph where the node is created **/
	private SecStateGraph graph;
	/** the unit of description that define this node **/
	private SecStateUnit unit;
	/** the edges to this node from others in the graph **/
	private List<SecStateEdge> in_edges;
	/** the edges from this node to others in the graph **/
	private List<SecStateEdge> ou_edges;
	
	/* constructor */
	/**
	 * create an isolated node w.r.t. the unit in the graph
	 * @param graph
	 * @param unit
	 * @throws Exception
	 */
	protected SecStateNode(SecStateGraph graph, SecStateUnit unit) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(unit == null)
			throw new IllegalArgumentException("Invalid unit: null");
		else {
			this.graph = graph;
			this.unit = unit;
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
	 * @return the unit of description that define this node
	 */
	public SecStateUnit get_unit() { return this.unit; }
	/**
	 * @return the edges to this node from others in the graph
	 */
	public Iterable<SecStateEdge> get_in_edges() { return this.in_edges; }
	/**
	 * @return the edges from this node to others in the graph
	 */
	public Iterable<SecStateEdge> get_ou_edges() { return this.ou_edges; }
	/**
	 * @param target
	 * @param constraint
	 * @return connect from this source to the target with constraint 
	 * @throws Exception
	 */
	public SecStateEdge connect(SecStateNode target, SecDescription constraint) throws Exception {
		SecStateEdge edge = new SecStateEdge(this, 
				target, this.graph.get_unit(constraint));
		this.ou_edges.add(edge); target.in_edges.add(edge);
		return edge;
	}
	protected void delete() {
		this.graph = null;
		this.unit = null;
		this.in_edges.clear();
		this.ou_edges.clear();
		this.in_edges = null;
		this.ou_edges = null;
	}
	
}
