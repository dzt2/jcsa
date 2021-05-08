package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;

/**
 * The node in the symbolic instance tree for killing a mutation.
 * 
 * @author yukimula
 *
 */
public class SymInstanceTreeNode extends SymInstanceContent {
	
	/* definitions */
	/** the tree where the node is created **/
	private SymInstanceTree tree;
	/** the edge pointing to this one or null if it is root **/
	private SymInstanceTreeEdge in_edge;
	/** the edges from this node to its children **/
	private List<SymInstanceTreeEdge> ou_edges;
	/**
	 * create a root node in the tree
	 * @param tree
	 * @param node_instance
	 * @throws Exception
	 */
	protected SymInstanceTreeNode(SymInstanceTree tree, SymInstance node_instance) throws Exception {
		super(node_instance);
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			this.tree = tree;
			this.in_edge = null;
			this.ou_edges = new ArrayList<SymInstanceTreeEdge>();
		}
	}
	/**
	 * create a child node under the parent
	 * @param parent
	 * @param edge_instance
	 * @param node_instance
	 * @throws Exception
	 */
	private SymInstanceTreeNode(SymInstanceTreeNode parent, SymInstance edge_instance, SymInstance node_instance) throws Exception {
		super(node_instance);
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else {
			this.tree = parent.tree;
			this.in_edge = new SymInstanceTreeEdge(parent, this, edge_instance);
			this.ou_edges = new ArrayList<SymInstanceTreeEdge>();
			parent.ou_edges.add(this.in_edge);
		}
	}
	/**
	 * @param edge_instance
	 * @param node_instance
	 * @return create a child node under this one and add it into the children list
	 * @throws Exception
	 */
	protected SymInstanceTreeNode add_child(SymInstance edge_instance, SymInstance node_instance) throws Exception {
		return new SymInstanceTreeNode(this, edge_instance, node_instance);
	}
	
	/* getters */
	/**
	 * @return the tree where the node is created
	 */
	public SymInstanceTree get_tree() { return this.tree; }
	/**
	 * @return the edge pointing to this one or null if it is root
	 */
	public SymInstanceTreeEdge get_in_edge() { return this.in_edge; }
	/**
	 * @return the edges from this node to its children
	 */
	public Iterable<SymInstanceTreeEdge> get_ou_edges() { return this.ou_edges; }
	/**
	 * @return the number of output edges from this node
	 */
	public int get_ou_degree() { return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth output edge from this node
	 * @throws IndexOutOfBoundsException
	 */
	public SymInstanceTreeEdge get_ou_edge(int k) throws IndexOutOfBoundsException { return this.ou_edges.get(k); }
	
	/* inference */
	/**
	 * @return whether the tree node is a root
	 */
	public boolean is_root() { return this.in_edge == null; }
	/**
	 * @return whether the tree node is a leaf (no children)
	 */
	public boolean is_leaf() { return this.ou_edges.isEmpty(); }
	/**
	 * @return whether the input edge is not null
	 */
	public boolean has_in_edge() { return this.in_edge != null; }
	/**
	 * @return the parent noed pointing to this one
	 */
	public SymInstanceTreeNode get_parent() {
		if(this.in_edge == null) {
			return null;
		}
		else {
			return this.in_edge.get_source();
		}
	}
	/**
	 * @param k
	 * @return the kth child from this one to direct children edges
	 * @throws IndexOutOfBoundsException
	 */
	public SymInstanceTreeNode get_child(int k) throws IndexOutOfBoundsException {
		return this.ou_edges.get(k).get_target();
	}
	@Override
	public List<SymInstanceTreeEdge> get_prev_path() {
		if(this.in_edge == null) {
			return new ArrayList<SymInstanceTreeEdge>();
		}
		else {
			return this.in_edge.get_prev_path();
		}
	}
	@Override
	public Collection<List<SymInstanceTreeEdge>> get_post_paths() {
		Collection<List<SymInstanceTreeEdge>> post_paths = new ArrayList<List<SymInstanceTreeEdge>>();
		for(SymInstanceTreeEdge edge : this.ou_edges) {
			post_paths.addAll(edge.get_post_paths());
		}
		return post_paths;
	}
	
}
