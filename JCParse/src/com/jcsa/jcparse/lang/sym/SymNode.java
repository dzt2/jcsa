package com.jcsa.jcparse.lang.sym;

import java.util.LinkedList;
import java.util.List;

/**
 * 	The symbolic representation of an expression or test constraint, parsed from either
 * 	<code>CirExpression</code> or <code>AstExpression</code> by following syntax.<br>
 * 	<br>
 * 	<code>
 * 	SymNode
 * 	|--	SymExpression		{data_type}
 * 	|--	|--	SymBasicExpression		{identifier|constant|literal}
 * 	|--	|--	SymUnaryExpression		{+, -, ~, !, assign, *, &}
 * 	|--	|--	SymBinaryExpression		{-, /, %, <<, >>, <, <=, >=, >, ==, !=}
 * 	|--	|--	SymMultiExpression		{+, *, &, |, ^, &&, ||}
 * 	|--	|--	SymFieldExpression		{.}
 * 	|--	|--	SymInitializerList		
 * 	|--	|--	SymFunCallExpression	
 * 	|--	|--	SymReference			{AstNode|CirNode}
 * 	|-- SyField				{name}
 * 	|--	SymArgumentList	
 * 	</code>
 * 	@author yukimula
 */
public abstract class SymNode {
	
	/* attributes */
	/** parent where this node is its child **/
	private SymNode parent;
	/** the children defined under the node **/
	private List<SymNode> children;
	/** index of this node as the child of its parent **/
	private int child_index;
	/** the token used to refine this node **/
	private Object token;
	
	/* constructor */
	/**
	 * create an isolated symbolic node in memory
	 * @param token
	 */
	protected SymNode(Object token) {
		this.parent = null;
		this.children = new LinkedList<SymNode>();
		this.child_index = -1;
		this.token = token;
	}
	
	/* parent */
	/**
	 * @return the parent of this node or null if it is root
	 */
	public SymNode get_parent() { return this.parent; }
	/**
	 * @return true if this.get_parent() == null
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return the root that can trace to this node
	 */
	public SymNode get_root() {
		SymNode root = this;
		while(!root.is_root()) {
			root = root.get_parent();
		}
		return root;
	}
	/**
	 * @return the index of this node in its parent as child
	 */
	public int get_child_index() { return this.child_index; }
	
	/* children */
	/**
	 * @return number of children in this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child under this node
	 * @throws IndexOutOfBoundsException
	 */
	public SymNode get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	/**
	 * @return the children under this node
	 */
	public Iterable<SymNode> get_children() { return this.children; }
	/**
	 * add a child (isolated) at the tail of the node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(SymNode child) throws IllegalArgumentException {
		if(child == null || child.parent != null)
			throw new IllegalArgumentException("Invalid child as null");
		else {
			child.parent = this;
			child.child_index = this.children.size();
			this.children.add(child);
		}
	}
	/**
	 * @return whether the node is a leaf without any child
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	
	/* token */
	/**
	 * @return true if this.get_token() != null
	 */
	public boolean has_token() { return this.token != null; }
	/**
	 * @return token to refine this node
	 */
	public Object get_token() { return this.token; }
	
	/* clone method */
	/**
	 * @return clone an isolated node w.r.t. this node
	 */
	protected abstract SymNode clone_self();
	@Override
	public SymNode clone() {
		SymNode parent = this.clone_self();
		for(SymNode child : this.children) {
			parent.add_child(child.clone());
		}
		return parent;
	}
	
	/* code generator */
	/**
	 * @param ast_code whether to generate ast_code or cir_code
	 * @return code that describes this node
	 * @throws Exception
	 */
	protected abstract String generate_code(boolean ast_code) throws Exception;
	/**
	 * @return this.generate_code(true)
	 * @throws Exception
	 */
	public String generate_ast_code() throws Exception { return this.generate_code(true); }
	/**
	 * @return this.generate_code(false)
	 * @throws Exception
	 */
	public String generate_cir_code() throws Exception { return this.generate_code(false); }
	@Override
	public String toString() { 
		try {
			return this.generate_code(true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
}
