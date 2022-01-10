package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * It specifies the category of store unit to preserve value(s) in an abstract
 * execution state in the context of mutation analysis.
 * 
 * @author yukimula
 *
 */
public enum CirStoreClass {
	
	/**	it represents a statement entity to be executed in state	**/	stmt,
	
	/** a conditional expression used as some of (sub-)condition 	**/	cond,
	/**	an expression entity to be used as operand or righ-value	**/	expr,
	/**	a reference expression that is defined at the assignment	**/	dvar,
	
	/** a virtual reference expression which is defined at some.	**/	vdef,
	
}
