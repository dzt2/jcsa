package com.jcsa.jcmutest.mutant.cir2mutant.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;

public class CirMutationNode {
	
	/* definitions */
	/** the graph where the node is created **/
	private CirMutationGraph graph;
	/** the symbolic condition this node defines **/
	private SymCondition condition;
	/** the edges pointing to this node **/
	private List<CirMutationEdge> in_edges;
	/** the edges pointing from this node **/
	private List<CirMutationEdge> ou_edges;
	
	/* constructor */
	/**
	 * create a unique node w.r.t. condition in the context of current graph
	 * @param graph
	 * @param condition
	 * @throws IllegalArgumentException
	 */
	protected CirMutationNode(CirMutationGraph graph, SymCondition condition) throws IllegalArgumentException {
		if(graph == null) {
			throw new IllegalArgumentException("Invalid graph as null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			this.graph = graph;
			this.condition = condition;
			this.in_edges = new LinkedList<CirMutationEdge>();
			this.ou_edges = new LinkedList<CirMutationEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the graph where the node is created 
	 */
	public CirMutationGraph get_graph() { return this.graph; }
	/**
	 * @return the symbolic condition this node defines
	 */
	public SymCondition get_condition() { return this.condition; }
	/**
	 * @return the edges pointing to this node
	 */
	public Iterable<CirMutationEdge> get_in_edges() { return this.in_edges; } 
	/**
	 * @return the edges pointing from this node
	 */
	public Iterable<CirMutationEdge> get_ou_edges() { return this.ou_edges; } 
	
	/* setters */
	/**
	 * @param type
	 * @param target
	 * @return link this node to target with specified type
	 * @throws Exception
	 */
	protected CirMutationEdge link_to(CirMutationType type, CirMutationNode target) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type as null");
		}
		else if(target == null || target.graph != this.graph || this.condition.equals(target.condition)) {
			throw new IllegalArgumentException("Invalid target: " + target);
		}
		else {
			/* initialization for BFT algorithm */
			Queue<CirMutationNode> queue = new LinkedList<CirMutationNode>();
			Set<CirMutationNode> records = new HashSet<CirMutationNode>();
			queue.add(this); records.add(this);
			
			/* to avoid transitivity dependence edges */
			while(!queue.isEmpty()) {
				CirMutationNode node = queue.poll();
				for(CirMutationEdge edge : node.ou_edges) {
					if(edge.get_target() == target) {
						return edge;
					}
					else {
						if(!records.contains(edge.get_target())) {
							records.add(edge.get_target());
							queue.add(edge.get_target());
						}
					}
				}
			}
			
			/* create direct new edge on the children layer */
			CirMutationEdge new_edge = new CirMutationEdge(type, this, target);
			this.ou_edges.add(new_edge); target.in_edges.add(new_edge); 
			return new_edge;
		}
	}
	
}
