package com.jcsa.jcparse.lopt.analysis.flow;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * Program dependence graph
 * @author yukimula
 *
 */
public class CDependenceGraph {
	
	/* contructor */
	/** mapping from the execution instance to the dependence node created **/
	private Map<CirInstanceNode, CDependenceNode> nodes;
	/**
	 * create an empty dependence graph
	 */
	private CDependenceGraph() {
		this.nodes = new HashMap<CirInstanceNode, CDependenceNode>();
	}
	
	/* getters */
	/**
	 * get the number of nodes created in the graph
	 * @return
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * get the dependence nodes created in the graph
	 * @return
	 */
	public Iterable<CDependenceNode> get_nodes() { return nodes.values(); }
	/**
	 * get the set of instances of statements being executed
	 * @return
	 */
	public Iterable<CirInstanceNode> get_instances() { return nodes.keySet(); }
	/**
	 * whether there is an instance that refers to some node in the graph
	 * @param instance
	 * @return
	 */
	public boolean has_node(CirInstanceNode instance) { return nodes.containsKey(instance); }
	/**
	 * get the node with respect to the instance of the statement being executed.
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public CDependenceNode get_node(CirInstanceNode instance) throws Exception {
		if(this.nodes.containsKey(instance)) return nodes.get(instance);
		else throw new IllegalArgumentException("Undefined: " + instance);
	}
	
	/* setters */
	/**
	 * create a new node with respect to the instance of statement being executed
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	protected CDependenceNode new_node(CirInstanceNode instance) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			if(!this.nodes.containsKey(instance))
				this.nodes.put(instance, new CDependenceNode(this, instance));
			return this.nodes.get(instance);
		}
	}
	
	/* factory method... */
	/**
	 * construct the program dependence graph over the instance flow graph for 
	 * the program under analysis
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static CDependenceGraph graph(CirInstanceGraph input) throws Exception {
		CDependenceGraph output = new CDependenceGraph();
		CDependenceBuilder.builder.build(input, output);
		return output;
	}
	
}
