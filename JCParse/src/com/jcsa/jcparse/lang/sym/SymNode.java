package com.jcsa.jcparse.lang.sym;

import java.util.LinkedList;
import java.util.List;

/**
 * The symbolic language describes the expression, field and statement in C program.
 * <br>
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SymNode									{source: AstNode|CirNode}		<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SymUnit																	<br>
 * 	|--	SymLabel							{statement|execution}			<br>
 * 	|--	SymField							{name: String}					<br>
 * 	|--	SymOperator							{operator: COperator}			<br>
 * 	|--	SymArgumentList														<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SymExpression							{data_type: CType}				<br>
 * 	|--	SymBasicExpression													<br>
 * 	|--	|--	SymIdExpression					{name: String}					<br>
 * 	|--	|--	SymConstant						{constant: CConstant}			<br>
 * 	|--	|--	SymLiteral						{literal: String}				<br>
 * 	|--	SymInitializerList													<br>
 * 	|--	SymFieldExpression													<br>
 * 	|--	SymCallExpression													<br>
 * 	|--	SymUnaryExpression					{-, ~, !, &, *, cast}			<br>
 * 	|--	SymBinaryExpression					{-, /, %, <<, >>, <, ..., >}	<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * <br>
 * @author yukimula
 *
 */
public abstract class SymNode {
	
	/* definitions */
	/** the source from which the node is parsed or null **/
	private Object source;
	/** the parent of the node or null if it is the root **/
	private SymNode parent;
	/** the index of the node as the child of its parent **/
	private int index;
	/** the children under this node **/
	private List<SymNode> children;
	
	/* constructor */
	/**
	 * create an isolated node without parent and children
	 * @param source
	 */
	protected SymNode() {
		this.source = null;
		this.parent = null;
		this.index = -1;
		this.children = new LinkedList<SymNode>();
	}
	
	/* getters */
	/**
	 * @return whether the node corresponds to any source
	 */
	public boolean has_source() { return this.source != null; }
	/**
	 * @return the source from which the node is parsed or null
	 */
	public Object get_source() { return this.source; }
	/**
	 * @return whether the node is a root
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return the parent of the node or null if it is the root
	 */
	public SymNode get_parent() { return this.parent; }
	/**
	 * @return the index of the node under its parent or -1 if it's root
	 */
	public int get_child_index() { return this.index; }
	/**
	 * @return the children under this node
	 */
	public Iterable<SymNode> get_children() { return this.children; }
	/**
	 * @return the number of children under this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child under the node
	 * @throws IndexOutOfBoundsException
	 */
	public SymNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * @return whether the node contains no children.
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	
	/* implication */
	@Override
	public SymNode clone() {
		SymNode parent = null;
		while(parent == null) {
			try {
				parent = this.construct();
			}
			catch(Exception ex) {
				ex.printStackTrace();
				parent = null;
			}
		}
		for(int k = parent.number_of_children(); 
				k < this.number_of_children(); k++) {
			parent.children.add(this.children.get(k).clone());
		}
		parent.set_source(this.source);
		return parent;
	}
	@Override
	public String toString() {
		while(true) {
			try {
				return this.generate_code();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SymNode) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	
	/* setters */
	/**
	 * set the source of the SymNode
	 * @param source
	 */
	protected void set_source(Object source) { this.source = source; }
	/**
	 * @return the isolated copy of this node
	 * @throws Exception
	 */
	protected abstract SymNode construct() throws Exception;
	/**
	 * add the child in the tail of the parent
	 * @param child
	 * @throws Exception
	 */
	protected void add_child(SymNode child) throws Exception {
		if(child == null)
			throw new IllegalArgumentException("Invalid child: null");
		else {
			if(child.parent != null) {
				child = child.clone();
			}
			child.parent = this;
			child.index = this.children.size();
			this.children.add(child);
		}
	}
	/**
	 * @return generate the code that describes this expression
	 * @throws Exception
	 */
	public abstract String generate_code() throws Exception;
	
}
