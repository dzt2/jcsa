package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;

/**
 * Each node in the killable dependence graph refers to either a mutant or a SymCondition
 * 
 * @author yukimula
 *
 */
public class KillDependenceNode {
	
	/* definitions */
	/** the killable dependence graph where the node is created **/
	private KillDependenceGraph graph;
	/** the representative {Mutant|SymCondition} by this node **/
	private SymCondition condition;	
	/** the set of edges depending on this node **/
	private Collection<KillDependenceEdge> in_edges;
	/** the set of edges depended by this node in graph **/
	private Collection<KillDependenceEdge> ou_edges;
	
	/* constructor */
	/**
	 * create an isolated node in the graph w.r.t. mutant or symbolic condition in killing analysis
	 * @param graph
	 * @param representative
	 * @throws IllegalArgumentException
	 */
	protected KillDependenceNode(KillDependenceGraph graph, SymCondition condition) throws IllegalArgumentException {
		if(graph == null) {
			throw new IllegalArgumentException("Invallid graph: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			this.graph = graph;
			this.condition = condition;
			this.in_edges = new LinkedList<KillDependenceEdge>();
			this.ou_edges = new LinkedList<KillDependenceEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the killable dependence graph where the node is created
	 */
	public KillDependenceGraph get_graph() { return this.graph; }
	/**
	 * @return the node refers to a symbolic condition
	 */
	public SymCondition get_condition() { return (SymCondition) this.condition; }
	/** 
	 * @return  the set of edges depending on this node 
	 */
	public Iterable<KillDependenceEdge> get_in_edges() { return this.in_edges; }
	/**
	 * @return the set of edges depended by this node in graph
	 */
	public Iterable<KillDependenceEdge> get_ou_edges() { return this.ou_edges; }
	
	/* setters */
	/**
	 * @param type
	 * @param target
	 * @return link this node to the given node of target
	 * @throws IllegalArgumentException
	 */
	protected KillDependenceEdge link_to(KillDependenceType type, KillDependenceNode target) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type as null");
		}
		else if(target == null || target.graph != this.graph || 
				this.condition.equals(target.condition)) {		/* ignore */
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			Queue<KillDependenceNode> queue = new LinkedList<KillDependenceNode>();
			Set<KillDependenceNode> records = new HashSet<KillDependenceNode>();
			
			queue.add(this); records.add(this);
			while(!queue.isEmpty()) {
				KillDependenceNode node = queue.poll();
				for(KillDependenceEdge edge : node.ou_edges) {
					if(edge.get_target().condition.equals(target.condition)) {
						if(edge.get_type() == type) {
							return edge;
						}
						else {
							throw new IllegalArgumentException("Unmatched: " + type);
						}
					}
					else if(!records.contains(edge.get_target())) {
						queue.add(edge.get_target()); 
						records.add(edge.get_target());
					}
				}
			}
			
			KillDependenceEdge edge = new KillDependenceEdge(type, this, target);
			edge.get_source().ou_edges.add(edge); edge.get_target().in_edges.add(edge);
			return edge;
		}
	}
	
}
