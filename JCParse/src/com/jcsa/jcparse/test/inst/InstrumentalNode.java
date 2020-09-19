package com.jcsa.jcparse.test.inst;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * The node in instrumental tree.
 * 
 * @author yukimula
 *
 */
public class InstrumentalNode {
	
	/* attributes */
	/** the tree where the node is created **/
	private InstrumentalTree tree;
	/** the index of the node in its tree **/
	private int index;
	/** the index of the node in its parent **/
	private int child_index;
	/** the first and final line executed in the location **/
	private InstrumentalLine[] lines;
	/** the parent of the node or null if it is root **/
	private InstrumentalNode parent;
	/** the children created under this node **/
	private List<InstrumentalNode> children;
	
	/* constructor */
	/**
	 * create a instrumental node w.r.t. the tree and specified index
	 * @param tree
	 * @param index
	 * @throws Exception
	 */
	protected InstrumentalNode(InstrumentalTree tree, int index) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			this.tree = tree;
			this.index = index;
			this.child_index = -1;
			this.lines = new InstrumentalLine[2];
			this.lines[0] = null;
			this.lines[1] = null;
			this.parent = null;
			this.children = new LinkedList<InstrumentalNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the tree where the node is created
	 */
	public InstrumentalTree get_tree() { return this.tree; }
	/**
	 * @return the index of the node in its tree
	 */
	public int get_node_id() { return this.index; }
	/**
	 * @return the index of the node in its parent 
	 */
	public int get_child_index() { return this.child_index; }
	/**
	 * @return the first line to which the node corresponds
	 */
	public InstrumentalLine get_beg_line() { return this.lines[0]; }
	/**
	 * @return the final line to which the node corresponds
	 */
	public InstrumentalLine get_end_line() { return this.lines[1]; }
	/**
	 * @return the location where the node corresponds
	 */
	public AstNode get_location() { return this.lines[0].get_location(); }
	/**
	 * @return the parent of the node or null if it is root
	 */
	public InstrumentalNode get_parent() { return this.parent; }
	/**
	 * @return the children created under this node
	 */
	public Iterable<InstrumentalNode> get_children() { return this.children; }
	/**
	 * @return the number of the children created under this node
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
	 * set the first line in the node
	 * @param line
	 * @throws IllegalArgumentException
	 */
	protected void set_beg_line(InstrumentalLine line) throws IllegalArgumentException {
		if(line != null)
			this.lines[0] = line;
		else
			throw new IllegalArgumentException("Invalid line: null");
	}
	/**
	 * set the final line in the node
	 * @param line
	 * @throws IllegalArgumentException
	 */
	protected void set_end_line(InstrumentalLine line) throws IllegalArgumentException {
		if(line != null)
			this.lines[1] = line;
		else
			throw new IllegalArgumentException("Invalid line: null");
	}
	/**
	 * add a new child to the parent
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(InstrumentalNode child) throws IllegalArgumentException {
		if(child == null || child.parent != null)
			throw new IllegalArgumentException("Invalid child: null");
		else {
			child.parent = this;
			child.child_index = this.children.size();
			this.children.add(child);
		}
	}
	
}
