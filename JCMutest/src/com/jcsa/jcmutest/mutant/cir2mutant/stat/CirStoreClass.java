package com.jcsa.jcmutest.mutant.cir2mutant.stat;

/**
 * The category of the store unit to preserve symbolic value as given type.
 * 
 * @author yukimula
 *
 */
public enum CirStoreClass {
	/** the store unit plays as a logical formula in conditions **/	cond,
	/** the store unit plays to preserve results for expression **/	expr,
	/** the store unit plays to preserve the value of variables **/	refr,
	/** the store unit plays to decide whether a statement will
	 *  be executed in the next step or nearly futures. **/			stmt,
}
