package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * The category of store unit to preserve value (interpretation) of abstract
 * execution states established at some point in mutation testing context.
 * 
 * @author yukimula
 *
 */
public enum CirStoreClass {
	
	/**	stmt:CirStatement:sym_statement		**/	stmt,
	
	/**	usep:CirExpression:sym_expression	**/	usep,
	
	/**	defp:CirExpression:sym_reference	**/	defp,
	
	/**	vdef:CirExpression:sym_reference	**/	vdef,
	
}
