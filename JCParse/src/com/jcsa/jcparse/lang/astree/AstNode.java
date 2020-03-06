package com.jcsa.jcparse.lang.astree;

import com.jcsa.jcparse.lang.text.CLocalable;

/**
 * Abstract syntax tree node, including:<br>
 * <code>
 * 	Ast
 * </code>
 * 
 * @author yukimula
 *
 */
public interface AstNode extends CLocalable {
	
	public static final int UNDEFINED_KEY = -1;
	
	/**
	 * get the abstract syntax tree where the node is defined
	 * @return
	 */
	public AstTree get_tree();
	/**
	 * key for access the AST-Node in a tree 
	 * (order of its BFS transverse)
	 * @return
	 */
	public int get_key();
	/**
	 * set the key for the node
	 * @param k
	 */
	public void set_key(int k);
	
	/**
	 * get the parent of this node
	 * 
	 * @return
	 */
	public AstNode get_parent();

	/**
	 * get the number of its children
	 * 
	 * @return
	 */
	public int number_of_children();

	/**
	 * get the kth child node of this node
	 * 
	 * @param k
	 * @return : null if k is out of index
	 */
	public AstNode get_child(int k);

	/**
	 * get the normalized code to which this node refers
	 * @return
	 * @throws Exception
	 */
	public String get_code() throws Exception;
}
