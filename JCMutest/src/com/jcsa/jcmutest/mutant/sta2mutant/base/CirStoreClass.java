package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * The category of store unit to preserve values describing abstract execution
 * states in the mutation testing.
 * 
 * @author yukimula
 *
 */
public enum CirStoreClass {
	
	/**	stmt:CirStatement	{execution}	**/	stmt,
	
	/**	cond:CirExpression 	{boolean}	**/	cond,
	/**	usep:CirExpression 	{no-left}	**/	usep,
	/**	defp:CirExpression 	{on-left}	**/	defp,
	
	/** vcon:CirStatement:expression	**/	vcon,
	/**	vuse:CirExpression:identifier	**/	vuse,
	/**	vdef:CirStatement:identifier	**/	vdef,
	
}
