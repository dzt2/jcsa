package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.ArrayList;
import java.util.List;

/**
 * BEG --> {path condition} --> execute(stmt)
 * execute(stmt) --> {infect condition} --> error+
 * error --> {propagation conditions} --> error+
 * error --> END
 * @author yukimula
 */
public class StateErrorGraph {
	
	/* attributes */
	/** index to the entry **/
	public static final int BEG_INDEX = 1;
	/** index to the final exit **/
	public static final int END_INDEX = 0;
	/** errors set to create error **/
	private StateErrors errors;
	/** nodes defined in graph **/
	private List<StateErrorNode> nodes;
	
	/* constructor */
	/**
	 * create an empty set of graph of errors
	 * @throws Exception 
	 */
	protected StateErrorGraph() throws Exception {
		this.errors = new StateErrors();
		this.nodes = new ArrayList<StateErrorNode>();
		this.new_node(null); this.new_node(null); 
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
	 * get the entry node
	 * @return
	 */
	public StateErrorNode get_beg_node() { return this.nodes.get(BEG_INDEX); }
	/**
	 * get the exit node
	 * @return
	 */
	public StateErrorNode get_end_node() { return this.nodes.get(END_INDEX); }
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
