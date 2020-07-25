package com.jcsa.jcparse.flwa.dominate;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.flwa.CirInstance;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;

/**
 * The graph that represents the dominance relationships in C program.
 * 
 * @author yukimula
 *
 */
public class CDominanceGraph {
	
	/* constructor */
	/** the mapping from the instance to the node that represents it **/
	private Map<CirInstance, CDominanceNode> nodes;
	/**
	 * create an empty graph to represents the dominance relationship in C program.
	 */
	private CDominanceGraph() {
		this.nodes = new HashMap<CirInstance, CDominanceNode>();
	}
	
	/* getters */
	/**
	 * get the number of nodes in the graph
	 * @return
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * get all the nodes belonging to the graph
	 * @return
	 */
	public Iterable<CDominanceNode> get_nodes() { return nodes.values(); }
	/**
	 * get the set of instances that the nodes in this graph represent
	 * @return
	 */
	public Iterable<CirInstance> get_instances() { return nodes.keySet(); }
	/**
	 * whether there is a node in the graph that represents the instance
	 * @param instance
	 * @return
	 */
	public boolean has_node(CirInstance instance) { return nodes.containsKey(instance); }
	/**
	 * get the node representing the instance in this graph
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public CDominanceNode get_node(CirInstance instance) throws Exception {
		if(nodes.containsKey(instance)) return this.nodes.get(instance);
		else throw new IllegalArgumentException("Invalid: " + instance);
	}
	
	/* setters */
	/**
	 * create a new node with respect to the instance in this graph
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	protected CDominanceNode new_node(CirInstance instance) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			if(!nodes.containsKey(instance))
				nodes.put(instance, new CDominanceNode(this, instance));
			return this.nodes.get(instance);
		}
	}
	
	/* factory methods */
	/**
	 * create the forward-dominance graph of the program flow graph
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static CDominanceGraph forward_dominance_graph(CirInstanceGraph input) throws Exception {
		CDominanceGraph output = new CDominanceGraph();
		CDominanceBuilder.builder.build(input, true, output);
		return output;
	}
	/**
	 * create the backward-dominance graph of the program flow graph
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static CDominanceGraph backward_dominance_graph(CirInstanceGraph input) throws Exception {
		CDominanceGraph output = new CDominanceGraph();
		CDominanceBuilder.builder.build(input, false, output);
		return output;
	}
	
}
