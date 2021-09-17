package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

/**
 * The category of store unit to be annotated with a logical predicate.
 * 
 * @author yukimula
 *
 */
public enum CirStoreClass {
	/** it denotes the logic formula defined from path constraint	**/	cond,
	/** a register preserving computational results of expression 	**/	expr,
	/** it denotes the memory addresses of user-defined variables	**/	refr,
	/** it denotes the statement to be executed or not in testing 	**/	stmt,
}
