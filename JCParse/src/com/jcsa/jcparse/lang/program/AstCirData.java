package com.jcsa.jcparse.lang.program;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.program.types.AstCirDataType;

/**
 * 	It represents a connection from AstNode to CirNode with given types.
 * 	
 * 	@author yukimula
 *
 */
public class AstCirData {
	
	/* definitions */
	/** the original tree node to connect this data item **/
	private	AstCirNode		node;
	/** the type of the connection from AstNode to CirNode **/
	private	AstCirDataType	type;
	/** the C-intermediate representated node to be linked **/
	private	CirNode			loct;
	
	/* constructor */
	/**
	 * It creates a connection from AstNode to CirNode with given type
	 * @param node
	 * @param type
	 * @param loct
	 * @throws IllegalArgumentException
	 */
	protected AstCirData(AstCirNode node, AstCirDataType type, 
				CirNode loct) throws IllegalArgumentException {
		if(node == null) {
			throw new IllegalArgumentException("Invalid node: null");
		}
		else if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(loct == null) {
			throw new IllegalArgumentException("Invalid loct: null");
		}
		else { this.node = node; this.type = type; this.loct = loct; }
	}
	
	/* getters */
	/**
	 * @return the original tree node to connect this data item
	 */
	public AstCirNode		get_node() 		{ return this.node; }
	/**
	 * @return the type of the connection from AstNode to CirNode
	 */
	public AstCirDataType	get_type()		{ return this.type; }
	/**
	 * @return the abstract syntactic source from which it connects
	 */
	public AstNode			get_source()	{ return this.node.get_ast_source(); }
	/**
	 * @return the C-intermediate representative to which it refers
	 */
	public CirNode			get_target()	{ return this.loct; }
	
}
