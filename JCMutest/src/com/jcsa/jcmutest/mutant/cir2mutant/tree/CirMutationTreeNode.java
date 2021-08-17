package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * The node in CirMutationTree denotes a CirAttribute uniquely in RIP context.
 * 
 * @author yukimula
 *
 */
public class CirMutationTreeNode {
	
	/* attributes */
	/** the CIR-based mutation tree where the node is created **/
	private CirMutationTree 			tree;
	/** the type denotes in which step the node was evaluated **/
	private CirMutationTreeType			type;
	/** it maintains the data state hold by the node in testing **/
	private CirMutationTreeData 		data;
	/** the edge from its parent to this node or null if it's a root **/
	private CirMutationTreeEdge			in_edge;
	/** the edges from this node to all of its children extended from **/
	private List<CirMutationTreeEdge>	ou_edges;
	
	/* constructor */
	/**
	 * create a root in the tree w.r.t. the given attribute
	 * @param tree
	 * @param attribute
	 * @throws Exception
	 */
	protected CirMutationTreeNode(CirMutationTree tree, CirAttribute attribute) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.tree = tree;
			this.type = CirMutationTreeType.pre_condition;
			this.data = new CirMutationTreeData(attribute);
			this.in_edge = null;
			this.ou_edges = new ArrayList<CirMutationTreeEdge>();
		}
	}
	/**
	 * create a child node w.r.t. the parent using given node_type and edge of type
	 * @param parent
	 * @param type
	 * @param attribute
	 * @param flow_type
	 * @throws Exception
	 */
	private CirMutationTreeNode(CirMutationTreeNode parent, CirMutationTreeType type,
			CirAttribute attribute, CirMutationTreeFlow flow_type) throws Exception {
		if(parent == null) {
			throw new IllegalArgumentException("Invalid parent: null");
		}
		else if(type == null) {
			throw new IllegalArgumentException("Invalid node_type: null");
		}
		else if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else if(flow_type == null) {
			throw new IllegalArgumentException("Invalid flow_type: null");
		}
		else {
			this.tree = parent.tree;
			this.type = type;
			this.data = new CirMutationTreeData(attribute);
			this.in_edge = new CirMutationTreeEdge(flow_type, parent, this);
			this.ou_edges = new ArrayList<CirMutationTreeEdge>();
			parent.ou_edges.add(this.in_edge);
		}
	}
	/**	
	 * @param type		the type of child node
	 * @param attribute	the attribute of child
	 * @param flow_type	the type of edge from this node to the child
	 * @return create or return the child w.r.t. the attribute under this parent and using specified type and flow
	 * @throws Exception
	 */
	protected CirMutationTreeNode new_child(CirMutationTreeType type, 
			CirAttribute attribute, CirMutationTreeFlow flow_type) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else if(flow_type == null) {
			throw new IllegalArgumentException("Invalid flow_type: null");
		}
		else {
			return new CirMutationTreeNode(this, type, attribute, flow_type);
		}
	}
	
	/* getters */
	/**
	 * @return the CIR-based mutation tree where the node is created
	 */
	public CirMutationTree get_tree() { return this.tree; }
	/**
	 * @return the type denotes in which step the node was evaluated
	 */
	public CirMutationTreeType get_node_type() { return this.type; }
	/**
	 * @return it maintains the data state hold by the node in testing
	 */
	public CirMutationTreeData get_node_data() { return this.data; }
	/**
	 * @return the attribute that the node represents in the tree 
	 */
	public CirAttribute get_node_attribute() { return this.data.get_attribute(); }
	/**
	 * @return the execution where the node's attribute should be evaluated
	 */
	public CirExecution get_node_execution() { return this.data.get_attribute().get_execution(); }
	/**
	 * @return whether this node is a root without any parent
	 */
	public boolean is_root() { return this.in_edge == null; }
	/**
	 * @return the edge from its parent to this node or null if it's a root
	 */
	public CirMutationTreeEdge get_in_edge() { return this.in_edge; }
	/**
	 * @return the parent of this node or null if the node is root
	 */
	public CirMutationTreeNode get_parent() {
		if(this.in_edge == null) {
			return null;
		}
		else {
			return this.in_edge.get_source();
		}
	}
	/**
	 * @return whether the node is a leaf without any child
	 */
	public boolean is_leaf() { return this.ou_edges.isEmpty(); }
	/**
	 * @return the edges from this node to all of its children extended from
	 */
	public Iterable<CirMutationTreeEdge> get_ou_edges() { return this.ou_edges; }
	/**
	 * @return the number of edges from this node to all of its children extended from
	 */
	public int get_ou_degree() { return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth edge from this node to all of its children extended from
	 * @throws IndexOutOfBoundsException
	 */
	public CirMutationTreeEdge get_ou_edge(int k) throws IndexOutOfBoundsException {
		return this.ou_edges.get(k);
	}
	/**
	 * @param k
	 * @return the kth child created under this node
	 * @throws IndexOutOfBoundsException
	 */
	public CirMutationTreeNode get_child(int k) throws IndexOutOfBoundsException {
		return this.ou_edges.get(k).get_target();
	}
	/**
	 * @return the sequence from root to this node in the tree 
	 */
	public Iterable<CirMutationTreeEdge> get_path() {
		List<CirMutationTreeEdge> path = new ArrayList<CirMutationTreeEdge>();
		CirMutationTreeNode node = this;
		while(!node.is_root()) {
			path.add(node.in_edge);
			node = node.get_parent();
		}
		for(int k = 0; k < path.size() / 2; k++) {
			int i = k, j = path.size() - 1 - k;
			CirMutationTreeEdge ei = path.get(i);
			CirMutationTreeEdge ej = path.get(j);
			path.set(i, ej);
			path.set(j, ei);
		}
		return path;
	}
	
	
}
