package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;

/**
 * 
 * @author yukimula
 *
 */
public class SymInstanceTreeNode extends SymInstanceNode {
	
	/* definitions */
	/**
	 * tree where the node is created
	 */
	private SymInstanceTree tree;
	/***
	 * the edge pointing to this node or null if the node is root
	 */
	private SymInstanceTreeEdge in_edge;
	/**
	 * the collection of edges from this node to its children
	 */
	private List<SymInstanceTreeEdge> ou_edges;
	/**
	 * create a root node in the tree
	 * @param tree
	 * @param instance
	 * @throws Exception
	 */
	protected SymInstanceTreeNode(SymInstanceTree tree, SymInstance instance) throws Exception {
		super(instance);
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
	 * @param instance
	 * @throws Exception
	 */
	private SymInstanceTreeNode(SymInstanceTreeNode parent, SymInstance edge_instance, SymInstance node_instance) throws Exception {
		super(node_instance);
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else {
			this.tree = parent.tree;
			this.in_edge = new SymInstanceTreeEdge(parent, this, parent.ou_edges.size(), edge_instance);
			this.ou_edges = new ArrayList<SymInstanceTreeEdge>();
			parent.ou_edges.add(this.in_edge);
		}
	}
	
	/* getters */
	/**
	 * @return tree where the node is created
	 */
	public SymInstanceTree get_tree() { return this.tree; }
	/**
	 * @return the edge pointing to this node or null if the node is root
	 */
	public SymInstanceTreeEdge get_in_edge() { return this.in_edge; }
	/**
	 * @return the collection of edges from this node to its children
	 */
	public Iterable<SymInstanceTreeEdge> get_ou_edges() { return this.ou_edges; }
	/**
	 * @return the number of children under the node
	 */
	public int get_ou_degree() { return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the edge from this node to its kth child node
	 * @throws IndexOutOfBoundsException
	 */
	public SymInstanceTreeEdge get_ou_edge(int k) throws IndexOutOfBoundsException { return this.ou_edges.get(k); }
	/**
	 * @return the node is root when input edge is null
	 */
	public boolean is_root() { return this.in_edge == null; }
	/**
	 * @return the node is leaf when output edges are none
	 */
	public boolean is_leaf() { return this.ou_edges.isEmpty(); }
	
	/* setter */
	/**
	 * @param edge_instance
	 * @param node_instance
	 * @return
	 * @throws Exception
	 */
	public SymInstanceTreeNode new_child(SymInstance edge_instance, SymInstance node_instance) throws Exception {
		return new SymInstanceTreeNode(this, edge_instance, node_instance);
	}
	
}
