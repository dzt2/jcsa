package com.jcsa.jcparse.test.inst;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * It describes the instrumental information in a hierarchical way based on
 * the structure of abstract syntactic tree.
 * 
 * @author yukimula
 *
 */
public class InstrumentalNode {
	
	/* definitions */
	/** the tree in which the node is created **/
	private InstrumentalTree tree;
	/** the integer ID for obtaining the node from the tree **/
	private int id;
	/** the unit that describes the instrumentation on code **/
	private InstrumentalUnit unit;
	/** the parent of this node or null if it is the root **/
	private InstrumentalNode parent;
	/** the index of the node as the child of its parent or 
	 *  -1 if it has no parent **/
	private int child_index;
	/** the children added under this node **/
	private List<InstrumentalNode> children;
	
	/* constructor */
	/**
	 * create a tree node with specified ID on specified location
	 * @param tree
	 * @param id
	 * @param location
	 * @throws IllegalArgumentException
	 */
	protected InstrumentalNode(InstrumentalTree tree, int id, 
			AstNode location) throws IllegalArgumentException {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			this.tree = tree;
			this.id = id;
			this.unit = new InstrumentalUnit(location);
			this.parent = null;
			this.child_index = -1;
			this.children = new LinkedList<InstrumentalNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the tree where this node is created
	 */
	public InstrumentalTree get_tree() { return this.tree; }
	/**
	 * @return the integer ID that defines this node in its tree
	 */
	public int get_id() { return this.id; }
	/**
	 * @return instrumental data unit with recorded state
	 */
	public InstrumentalUnit get_unit() { return this.unit; }
	/**
	 * @return the location in which the instrumentation is injected
	 */
	public AstNode get_location() { return this.unit.get_location(); }
	/**
	 * @return whether the node is a root without parent
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return the parent of this node or null if it is root
	 */
	public InstrumentalNode get_parent() { return this.parent; }
	/**
	 * @return the index of the node as the child of its parent or 
	 *  	   -1 if it has no parent
	 */
	public int get_child_index() { return this.child_index; }
	/**
	 * @return whether the node is a leaf without andy children
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * @return the children created under this node
	 */
	public Iterable<InstrumentalNode> get_children() { return this.children; }
	/**
	 * @return the number of children created under this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child under this node
	 * @throws IndexOutOfBoundsException
	 */
	public InstrumentalNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	
	/* setters */
	/**
	 * add the child at the tail of the children under this node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(InstrumentalNode child) throws IllegalArgumentException {
		if(child == null || child.parent != null) 
			throw new IllegalArgumentException("Invalid child: " + child);
		else {
			child.parent = this;
			child.child_index = this.children.size();
			this.children.add(child);
		}
	}
	
}
