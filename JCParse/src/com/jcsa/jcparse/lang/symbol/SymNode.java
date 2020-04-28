package com.jcsa.jcparse.lang.symbol;

/**
 * Structural description of symbolic node
 * 	SymNode
 * 	|--	Statement		
 * 	|--	Expression		
 * 	|--	Field			
 * 	|--	ArgumentList	
 * @author yukimula
 *
 */
public interface SymNode {
	
	/**
	 * get the parent of the node
	 * @return
	 */
	public SymNode get_parent();
	
	/**
	 * get the number of children
	 * @return
	 */
	public int number_of_children();
	
	/**
	 * get the children of the node
	 * @return
	 */
	public Iterable<SymNode> get_children();
	
	/**
	 * get the kth child under the node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public SymNode get_child(int k) throws IndexOutOfBoundsException;
	
}
