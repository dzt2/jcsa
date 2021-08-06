package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.List;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;


public class CirMutationTreeNode {

	/* attributes */
	/** the tree where this node is created **/
	private CirMutationTree 			tree;
	/** the type of the node before when or after **/
	private CirMutationTreeType 		type;
	/** the status of attribute being accumulated **/
	private CirMutationTreeStatus		status;
	/** the edge from parent to this node or null **/
	private CirMutationTreeEdge			in_edge;
	/** the edges from this node linking to its children **/
	private List<CirMutationTreeEdge>	ou_edges;

	/* constructor */
	/**
	 * create a node w.r.t. the attribute and type in the context of tree model
	 * @param tree
	 * @param type
	 * @param attribute
	 * @throws IllegalArgumentException
	 */
	protected CirMutationTreeNode(CirMutationTree tree, CirMutationTreeType
			type, CirAttribute attribute) throws IllegalArgumentException {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(type == null) {
			throw new IllegalArgumentException("Invalid type as null");
		}
		else if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.tree = tree;
			this.type = type;
			this.status = new CirMutationTreeStatus(attribute);
			this.in_edge = null;
			this.ou_edges = new ArrayList<>();
		}
	}

	/* getters */
	/**
	 * @return the tree where this node is created
	 */
	public CirMutationTree 					get_tree() 			{ return this.tree; }
	/**
	 * @return the type of the node before when or after
	 */
	public CirMutationTreeType 				get_type() 			{ return this.type; }
	/**
	 * @return the attribute that the node represents in the tree
	 */
	public CirAttribute						get_attribute()		{ return this.status.get_attribute(); }
	/**
	 * @return the status of attribute being accumulated
	 */
	public CirMutationTreeStatus			get_status() 		{ return this.status; }
	/**
	 * @return the edge from parent to this node or null if the node is a root
	 */
	public CirMutationTreeEdge				get_in_edge() 		{ return this.in_edge; }
	/**
	 * @return the edges from this node linking to its children
	 */
	public Iterable<CirMutationTreeEdge> 	get_ou_edges()		{ return this.ou_edges; }
	/**
	 * @return the number of the edges from this node linking to its children
	 */
	public int 								get_ou_degree()		{ return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth edge from this node being linked to one of its child
	 */
	public CirMutationTreeEdge				get_ou_edge(int k)	{ return this.ou_edges.get(k); }

	/* implication */
	/**
	 * @return whether the node is a root without input edge and parent
	 */
	public boolean 							is_root()			{ return this.in_edge == null; }
	/**
	 * @return the parent pointing to this node directly or null if it is root.
	 */
	public CirMutationTreeNode				get_parent()		{
		if(this.in_edge == null) {
			return null;
		}
		else {
			return this.in_edge.get_source();
		}
	}
	/**
	 * @param k
	 * @return the kth child under the node
	 */
	public CirMutationTreeNode				get_child(int k)	{
		return this.get_ou_edge(k).get_target();
	}
	/**
	 * connect this node to a child using a flow-type, of which node type is specified
	 * @param child_type
	 * @param child_attribute
	 * @param edge_type
	 * @throws Exception
	 */
	protected CirMutationTreeEdge link(CirMutationTreeType child_type, CirAttribute
			attribute, CirMutationTreeFlow edge_type) throws IllegalArgumentException {
		if(child_type == null) {
			throw new IllegalArgumentException("Invalid child_type: null");
		}
		else if(attribute == null) {
			throw new IllegalArgumentException("Invalid: " + attribute);
		}
		else if(edge_type == null) {
			throw new IllegalArgumentException("Invalid edge_type: null");
		}
		else {
			for(CirMutationTreeEdge edge : this.ou_edges) {
				if(edge.get_target().get_attribute().equals(attribute)) {
					return edge;
				}
			}
			CirMutationTreeEdge edge = new CirMutationTreeEdge(edge_type, this,
					new CirMutationTreeNode(this.tree, child_type, attribute));
			edge.get_target().in_edge = edge; this.ou_edges.add(edge);
			return edge;
		}
	}
	
}
