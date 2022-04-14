package com.jcsa.jcparse.lang.program;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.program.types.AstCirNodeLink;
import com.jcsa.jcparse.lang.program.types.AstCirNodeType;

/**
 * 	It represents a functional simplified abstract syntactic node w.r.t. the 
 * 	CirNode(s) using AstCirData, and organized as tree + graph structure.
 * 	@author yukimula
 *
 */
public class AstCirNode {
	
	/* definitions */
	/** the tree in which this node is created **/
	private	AstCirTree			tree;
	/** the unique integer ID of this node in tree **/
	private	int					node_id;
	/** the syntax-directed type of this node based on its source **/
	private	AstCirNodeType		type;
	/** the syntactic node that this tree node refers and represents **/
	private	AstNode				source;
	/** the list of data-connection from AstNode to CirNode(s) related **/
	private	List<AstCirData>	data_list;
	/** the parent of this node or null if this node is a root **/
	private	AstCirNode			parent;
	/** the type of the link from its parent to this node or null if it's root **/
	private	AstCirNodeLink		link;
	/** the list of child nodes inserted under this one in tree-structure model **/
	private	List<AstCirNode>	children;
	/** the set of edges from other nodes to this AstCirNode as dependence **/
	private	List<AstCirEdge>	in_edges;
	/** the set of edges from this AstCirNode to other nodes as dependence **/
	private	List<AstCirEdge>	ou_edges;
	
	/* getters */
	/**
	 * @return the tree in which this node is created
	 */
	public	AstCirTree				get_tree()			{ return this.tree; }
	/**
	 * @return the unique integer ID of this node in tree
	 */
	public	int						get_node_id()		{ return this.node_id; }
	/**
	 * @return the syntax-directed type of this node based on its source
	 */
	public	AstCirNodeType			get_node_type()		{ return this.type; }
	/**
	 * @return the syntactic node that this tree node refers and represents
	 */
	public	AstNode					get_ast_source()	{ return this.source; }
	/**
	 * @return the list of data-connection from AstNode to CirNode(s) related
	 */
	public	Iterable<AstCirData>	get_data_items()	{ return this.data_list; }
	/**
	 * @return whether this node is a root without any parent
	 */
	public	boolean					is_root()			{ return this.parent == null; }
	/**
	 * @return the parent of this node or null if this node is a root 
	 */
	public	AstCirNode				get_parent()		{ return this.parent; }
	/**
	 * @return the type of the link from its parent to this node or null if it's root
	 */
	public	AstCirNodeLink			get_parent_link()	{ return this.link; }
	/**
	 * @return whether this node is a leaf without any child
	 */
	public	boolean					is_leaf()			{ return this.children.isEmpty(); }
	/**
	 * @return the list of child nodes inserted under this one in tree-structure model
	 */
	public	Iterable<AstCirNode>	get_children()		{ return this.children; }
	/**
	 * @return the set of edges from other nodes to this AstCirNode as dependence
	 */
	public	Iterable<AstCirEdge>	get_in_edges()		{ return this.in_edges; }
	/**
	 * @return the set of edges from this AstCirNode to other nodes as dependence
	 */
	public	Iterable<AstCirEdge>	get_ou_edges()		{ return this.ou_edges; }
	
	/* index */
	/**
	 * @return the number of data-connections from AstNode to CirNode related
	 */
	public	int			number_of_data_items()	{ return this.data_list.size(); }
	/**
	 * @return the number of children inserted under this node
	 */
	public	int			number_of_children()	{ return this.children.size(); }
	/**
	 * @return the number of dependence edges from others to this node
	 */
	public	int			get_in_degree()			{ return this.in_edges.size(); }
	/**
	 * @return the number of dependence edges from this node to others
	 */
	public	int			get_ou_degree()			{ return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth data-connection from this AstNode to the CirNode related
	 * @throws IndexOutOfBoundsException
	 */
	public	AstCirData	get_data_item(int k) throws IndexOutOfBoundsException { return this.data_list.get(k); }
	/**
	 * @param k
	 * @return the kth child inserted under this node
	 * @throws IndexOutOfBoundsException
	 */
	public	AstCirNode	get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	/**
	 * @param k
	 * @return the kth edge from other node to this one
	 * @throws IndexOutOfBoundsException
	 */
	public	AstCirEdge	get_in_edge(int k) throws IndexOutOfBoundsException { return this.in_edges.get(k); }
	/**
	 * @param k
	 * @return the kth edge from this node to the other
	 * @throws IndexOutOfBoundsException
	 */
	public	AstCirEdge	get_ou_edge(int k) throws IndexOutOfBoundsException { return this.ou_edges.get(k); }
	
	/* setters */
	/**
	 * It creates an isolated node in AstCirTree with specified ID and AST-source
	 * @param tree		the tree in which this node is created
	 * @param node_id	the unique integer ID of this node in tree
	 * @param source	the syntactic node that this node refer to
	 * @throws IllegalArgumentException
	 */
	protected AstCirNode(AstCirTree tree, int node_id, AstNode source) throws IllegalArgumentException {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as: null");
		}
		else if(node_id < 0) {
			throw new IllegalArgumentException("Invalid node_id: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			this.tree = tree; this.node_id = node_id;
			this.type = this.new_type(source);
			this.source = source;
			this.data_list = new ArrayList<AstCirData>();
			this.link = null; this.parent = null;
			this.children = new ArrayList<AstCirNode>();
			this.in_edges = new ArrayList<AstCirEdge>();
			this.ou_edges = new ArrayList<AstCirEdge>();
		}
	}
	/**
	 * @param source
	 * @return the inferred type from the source
	 * @throws IllegalArgumentException
	 */
	private	AstCirNodeType	new_type(AstNode source) throws IllegalArgumentException {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
}
