package com.jcsa.jcparse.lang.irlang.unit;

import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;

/**
 * function_definition 	--> declarator 	function_body
 * 						--> implicator	function_body
 * @author yukimula
 *
 */
public interface CirFunctionDefinition extends CirExternalUnit {

	/* getters */
	/**
	 * get the declarator that declares the function with name and type
	 * @return
	 */
	public CirNameExpression get_declarator();
	/**
	 * get the function body
	 * @return
	 */
	public CirFunctionBody get_body();

	/* setter */
	/**
	 * set the declarator of the function with type and name
	 * @param declarator
	 * @throws IllegalArgumentException
	 */
	public void set_declarator(CirNameExpression declarator) throws IllegalArgumentException;
	/**
	 * set the function body in the node
	 * @param body
	 * @throws IllegalArgumentException
	 */
	public void set_body(CirFunctionBody body) throws IllegalArgumentException;

}
