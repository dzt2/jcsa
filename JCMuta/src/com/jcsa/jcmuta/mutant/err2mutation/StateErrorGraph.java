package com.jcsa.jcmuta.mutant.err2mutation;

import java.util.ArrayList;
import java.util.List;

public class StateErrorGraph {
	
	/* attributes */
	/** index to the entry **/
	public static final int BEG_INDEX = 0;
	/** index to the final exit **/
	public static final int END_INDEX = 1;
	/** errors set to create error **/
	private StateErrors errors;
	/** nodes defined in graph **/
	private List<StateErrorNode> nodes;
	
	/* constructor */
	/**
	 * create an empty set of graph of errors
	 */
	private StateErrorGraph() {
		this.errors = new StateErrors();
		this.nodes = new ArrayList<StateErrorNode>();
	}
	
	/* getters */
	/**
	 * get the error set
	 * @return
	 */
	public StateErrors get_error_set() { return this.errors; }
	/**
	 * get the number of nodes
	 * @return
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * get the nodes in the graph
	 * @return
	 */
	public Iterable<StateErrorNode> get_nodes() { return this.nodes; }
	/**
	 * get the node w.r.t. the identifier as given
	 * @param identifier
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public StateErrorNode get_node(int identifier) throws IndexOutOfBoundsException {
		return this.nodes.get(identifier);
	}
	/**
	 * create a new node w.r.t. source error
	 * @param source
	 * @return
	 * @throws Exception
	 */
	protected StateErrorNode new_node(StateError source) throws Exception {
		StateErrorNode node = new StateErrorNode(this, this.nodes.size(), source);
		this.nodes.add(node); return node;
	}
	
}
