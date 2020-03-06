package com.jcsa.jcparse.lang.astree.impl;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * To iterate the nodes in AST
 * 
 * @author yukimula
 */
public interface AstNodeIterator {
	/**
	 * whether there is the next node in iterator
	 * 
	 * @return
	 */
	public boolean has_next();

	/**
	 * get the next node from iterator
	 * 
	 * @return : null when no more nodes can be visited
	 * @throws Exception
	 */
	public AstNode get_next() throws Exception;
}
