package com.jcsa.jcparse.flwa.dominate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.flwa.CirInstance;
import com.jcsa.jcparse.flwa.graph.CirInstanceEdge;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;

/**
 * The node in dominance graph of C program.
 *
 * @author yukimula
 *
 */
public class CDominanceNode {

	/* attributes */
	/** the graph where the node is created **/
	private CDominanceGraph graph;
	/** the instance of statement or flow that the node represents **/
	private CirInstance instance;
	/** the set of nodes that directly dominate this node in graph **/
	private List<CDominanceNode> in;
	/** the set of nodes that are directly dominated by this node **/
	private List<CDominanceNode> ou;

	/* constructor */
	/**
	 * create a node in the graph with respect to the instance provided
	 * @param graph
	 * @param instance
	 * @throws Exception
	 */
	protected CDominanceNode(CDominanceGraph graph, CirInstance instance) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.graph = graph; this.instance = instance;
			this.in = new LinkedList<>();
			this.ou = new LinkedList<>();
		}
	}

	/* getters */
	/**
	 * get the dominance graph where this node belongs to
	 * @return
	 */
	public CDominanceGraph get_graph() { return this.graph; }
	/**
	 * get the instance of flow or statement that the node represents
	 * @return
	 */
	public CirInstance get_instance() { return this.instance; }
	/**
	 * get the set of nodes that directly dominate this node
	 * @return
	 */
	public Iterable<CDominanceNode> get_in_nodes() { return in; }
	/**
	 * get the set of nodes that are directly dominated by this node
	 * @return
	 */
	public Iterable<CDominanceNode> get_ou_nodes() { return ou; }
	/**
	 * get the number of nodes that directly dominate this one
	 * @return
	 */
	public int get_in_degree() { return this.in.size(); }
	/**
	 * get the number of nodes that are directly dominated by this node
	 * @return
	 */
	public int get_ou_degree() { return this.ou.size(); }
	/**
	 * get the kth node that directly dominates this node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CDominanceNode get_in_node(int k) throws IndexOutOfBoundsException { return in.get(k); }
	/**
	 * get the kth node that is directly dominated by this node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CDominanceNode get_ou_node(int k) throws IndexOutOfBoundsException { return ou.get(k); }
	/**
	 * whether the node represents an execution of statement in program
	 * @return
	 */
	public boolean is_exec() { return instance instanceof CirInstanceNode; }
	/**
	 * whether this node represents the execution flow in program graph
	 * @return
	 */
	public boolean is_flow() { return instance instanceof CirInstanceEdge; }
	/**
	 * get the execution that the node represents or null if the node refers to flow
	 * @return
	 */
	public CirExecution get_execution() {
		if(instance instanceof CirInstanceNode)
			return ((CirInstanceNode) instance).get_execution();
		else return null;
	}
	/**
	 * get the flow that the node represents or null if the node refers to execution
	 * @return
	 */
	public CirExecutionFlow get_flow() {
		if(instance instanceof CirInstanceEdge)
			return ((CirInstanceEdge) instance).get_flow();
		else return null;
	}

	/* setters */
	/**
	 * link this node to the target node as declaring that this node directly
	 * dominates the target node in the graph.
	 * @param node
	 * @throws Exception
	 */
	protected void dominate(CDominanceNode node) throws Exception {
		if(node == null || node.graph != this.graph)
			throw new IllegalArgumentException("Invalid graph: null");
		else {
			for(CDominanceNode next : this.ou) {
				if(next == node) { return; }
			}
			this.ou.add(node); node.in.add(this);
		}
	}

	/* path generator */
	/**
	 * @return the sequence of execution flows that lead to this node from others
	 * 		   which include only the true_flow, fals_flow, call_flow, retr_flow.
	 * @throws Exception
	 */
	public List<CirExecutionFlow> get_dominance_path() throws Exception {
		List<CirExecutionFlow> edges = new ArrayList<>();
		CDominanceNode node = this;

		while(node != null) {
			if(node.is_flow()) {
				CirExecutionFlow flow = node.get_flow();
				switch(flow.get_type()) {
				case true_flow:
				case fals_flow:
				case call_flow:
				case retr_flow:	edges.add(flow);
				default: 		break;
				}
			}

			if(node.get_in_degree() > 0) {
				node = node.get_in_node(0);
			}
			else {
				node = null;
			}
		}

		for(int k = 0; k < edges.size() / 2; k++) {
			CirExecutionFlow x = edges.get(k);
			CirExecutionFlow y = edges.get(edges.size() - 1);
			edges.set(k, y); edges.set(edges.size() - k - 1, x);
		}

		return edges;
	}

}
