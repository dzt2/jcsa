package com.jcsa.jcparse.lopt.analysis.flow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * The program influence graph in which each node represents a program element (statement or expression)
 * while the edge represents their relationships such that one directly influences on another.
 * 
 * @author yukimula
 *
 */
public class CInfluenceGraph {
	
	/* constructor */
	/** the mapping from statement instance to the nodes referring to it **/
	private Map<CirInstanceNode, List<CInfluenceNode>> instances_nodes;
	/** create an empty influence graph **/
	private CInfluenceGraph() {
		instances_nodes = new HashMap<CirInstanceNode, List<CInfluenceNode>>();
	} 
	
	/* getters */
	/**
	 * get the number of nodes created in this graph
	 * @return
	 */
	public int size() {
		int size = 0;
		for(List<CInfluenceNode> nodes : instances_nodes.values()) {
			size = size + nodes.size();
		}
		return size;
	}
	/**
	 * get the set of instances of statements referring to the nodes in this graph
	 * @return
	 */
	public Iterable<CirInstanceNode> get_instances() { return instances_nodes.keySet(); }
	/**
	 * whether there are nodes created in this graph referring to the instance of the statement as provided
	 * @param instance
	 * @return
	 */
	public boolean has_nodes(CirInstanceNode instance) { return instances_nodes.containsKey(instance); }
	/**
	 * get the set of nodes in this graph referring to the given instance of specified statement.
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public Iterable<CInfluenceNode> get_nodes(CirInstanceNode instance) throws Exception {
		if(instances_nodes.containsKey(instance)) return instances_nodes.get(instance);
		else { throw new IllegalArgumentException("Invalid instance as: " + instance); }
	}
	/**
	 * whether there is the node with respect to the CIR source node of specified instance
	 * @param instance
	 * @param cir_source
	 * @return
	 */
	public boolean has_node(CirInstanceNode instance, CirNode cir_source) {
		if(this.instances_nodes.containsKey(instance)) {
			for(CInfluenceNode node : instances_nodes.get(instance)) {
				if(node.get_cir_source() == cir_source) return true;
			}
			return false;
		}
		else {
			return false;
		}
	}
	/**
	 * get the node with respect to the CIR source node of specified instance
	 * @param instance
	 * @param cir_source
	 * @return
	 */
	public CInfluenceNode get_node(CirInstanceNode instance, CirNode cir_source) {
		if(this.instances_nodes.containsKey(instance)) {
			for(CInfluenceNode node : instances_nodes.get(instance)) {
				if(node.get_cir_source() == cir_source) return node;
			}
			throw new IllegalArgumentException("Undefined cir_source: " + cir_source);
		}
		else {
			throw new IllegalArgumentException("Undefined instance: " + instance);
		}
	}
	
	/* setters */
	/**
	 * create a new node with respect to the source node in the instance of the statement.
	 * @param instance
	 * @param cir_source
	 * @return
	 * @throws Exception
	 */
	protected CInfluenceNode new_node(CirInstanceNode instance, CirNode cir_source) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else if(cir_source == null)
			throw new IllegalArgumentException("Invalid cir_source: null");
		else {
			if(!this.instances_nodes.containsKey(instance))
				this.instances_nodes.put(instance, new LinkedList<CInfluenceNode>());
			List<CInfluenceNode> nodes = this.instances_nodes.get(instance);
			
			for(CInfluenceNode node : nodes) {
				if(node.get_cir_source() == cir_source) return node;
			}
			
			CInfluenceNode node = new CInfluenceNode(this, instance, cir_source);
			nodes.add(node);
			return node;
		}
	}
	/**
	 * create an edge from source to the target with respect to the given type
	 * @param type
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	protected CInfluenceEdge connect(CInfluenceEdgeType type, CInfluenceNode source, CInfluenceNode target) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type as null");
		else if(source == null || source.get_graph() != this)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null || target.get_graph() != this)
			throw new IllegalArgumentException("Invalid target: null");
		else { return source.link_with(type, target); }
	}
	
	/* generator */
	public static CInfluenceGraph graph(CirInstanceGraph program_graph) throws Exception {
		CInfluenceGraph influence_graph = new CInfluenceGraph();
		CInfluenceBuilder.build(program_graph, influence_graph);
		return influence_graph;
	}
	
}
