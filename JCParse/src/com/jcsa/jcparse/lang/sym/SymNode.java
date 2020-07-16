package com.jcsa.jcparse.lang.sym;

import java.util.LinkedList;
import java.util.List;

/**
 * Symbolic representation as tree structure as following:<br>
 * 	|--	SymExpression
 * 	|--	|--	SymBasicExpression
 * 	|--	|--	|--	SymIdentifier		{name: String}
 * 	|--	|--	|--	SymConstant			{constant: CConstant}
 * 	|--	|--	|--	SymLiteral			{literal: String}
 * 	|--	|--	SymUnaryExpression		{operator: +, -, ~, !, &, *, assign}
 * 	|--	|--	SymBinaryExpression		{operator: -, /, %, <<, >>, <, <=, >, >=, ==, !=}
 * 	|--	|--	SymMultiExpression		{operator: +, *, &, |, ^, &&, ||}
 * 	|--	|--	SymInitializerList
 * 	|--	|--	SymFieldExpression		{operator: dot}
 * 	|--	|--	SymFunCallExpression
 * 	|--	SymField					{name: String}
 * 	|--	SymArgumentList
 * 	|--	|--	(SymExpression)*
 * @author yukimula
 *
 */
public abstract class SymNode {
	
	/* attributes */
	/** parent of this node or null if it's the root **/
	private SymNode parent;
	/** index of this node in its parent or -1 if it's root **/
	private int child_index;
	/** the children of which parents point to this **/
	private List<SymNode> children;
	
	/* constructor */
	/**
	 * abstract symbolic node
	 */
	protected SymNode() {
		this.parent = null;
		this.child_index = -1;
		this.children = new LinkedList<SymNode>();
	}
	
	/* getters */
	/**
	 * @return whether the node is root
	 */
	public boolean is_root() {
		return this.parent != null;
	}
	/**
	 * @return the parent of this node or null if it's root
	 */
	public SymNode get_parent() {
		return this.parent;
	}
	/**
	 * @return the index of the node as child in its parent
	 */
	public int get_child_index() {
		return this.child_index;
	}
	/**
	 * @return the children created in this node
	 */
	public Iterable<SymNode> get_children() {
		return this.children;
	}
	/**
	 * @return the number of children in this node
	 */
	public int number_of_children() {
		return this.children.size();
	}
	/**
	 * @param k
	 * @return get the kth child in this node
	 * @throws IndexOutOfBoundsException
	 */
	public SymNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * @return whether none of children in this node
	 */
	public boolean is_leaf() {
		return this.children.isEmpty();
	}
	/**
	 * @return the root where this node is defined
	 */
	public SymNode get_root() {
		SymNode root = this;
		while(!root.is_root()) {
			root = root.parent;
		}
		return root;
	}
	/**
	 * add the child in this node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(SymNode child) throws IllegalArgumentException {
		if(child == null || child.parent != null)
			throw new IllegalArgumentException("Invalid child: " + child);
		else {
			child.parent = this;
			child.child_index = this.children.size();
			this.children.add(child);
		}
	}
	@Override
	public SymNode clone() {
		SymNode parent = this.new_self();
		for(SymNode child : this.children) {
			parent.add_child(child.clone());
		}
		return parent;
	}
	/**
	 * @return the code of symbolic node in AST style
	 * @throws Exception
	 */
	public String generate_ast_code() throws Exception {
		return this.generate_code(true);
	}
	/**
	 * @return the code of symbolic node in CIR style
	 * @throws Exception
	 */
	public String generate_cir_code() throws Exception {
		return this.generate_code(false);
	}
	@Override
	public String toString() {
		try {
			return this.generate_code(true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/* utility methods */
	/**
	 * @return the clone of this node alone
	 */
	protected abstract SymNode new_self();
	/**
	 * @param ast_style
	 * @return generate the code in specified style.
	 * @throws Exception
	 */
	protected abstract String generate_code(boolean ast_style) throws Exception;
	
}
