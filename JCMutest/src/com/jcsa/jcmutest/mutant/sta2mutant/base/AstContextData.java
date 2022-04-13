package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It preserves the data that describes the state of the AstContextNode.
 * 	@author yukimula
 *
 */
public class AstContextData {
	
	/* attributes */
	/** the node of which AstNode is linked to this data state **/
	private	AstContextNode		node;
	/** the type of the data state from AstNode to CirNode  **/
	private	AstContextDataType	type;
	/** the CirNode as the location to preserve the state **/
	private	CirNode				store;
	/** the value that describes the state hold by the node **/
	private	SymbolExpression	value;
	
	/* constructor */
	/**
	 * It creates a data state for the node given CirNode as store to save value
	 * @param node
	 * @param type
	 * @param store
	 * @param value
	 * @throws IllegalArgumentException
	 */
	protected AstContextData(AstContextNode node, AstContextDataType type,
			CirNode store, SymbolExpression value) throws IllegalArgumentException {
		if(node == null) {
			throw new IllegalArgumentException("Invalid node: null");
		}
		else if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(store == null) {
			throw new IllegalArgumentException("Invalid store: null");
		}
		/*else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}*/
		else {
			this.node = node; this.type = type;
			this.store = store; this.value = value;
		}
	}
	
	/* getters */
	/**
	 * @return the node of which AstNode is linked to this data state
	 */
	public	AstContextNode		get_node() 	{ return this.node; }
	/**
	 * @return the type of the data state from AstNode to CirNode
	 */
	public 	AstContextDataType	get_type()	{ return this.type; }
	/**
	 * @return the CirNode as the location to preserve the state
	 */
	public 	CirNode				get_store()	{ return this.store; }
	/**
	 * @return the value that describes the state hold by the node
	 */
	public	SymbolExpression	get_value()	{ return this.value; }
	
}
