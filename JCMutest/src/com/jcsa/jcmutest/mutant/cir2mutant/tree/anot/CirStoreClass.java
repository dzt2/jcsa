package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

/**
 * It specifies the type of store unit used to preserve value that is annotated
 * with some program location in forms of C-intermediate representative code.
 * 
 * @author yukimula
 *
 */
public enum CirStoreClass {
	/** it denotes a logical formula which is defined from path constraint **/	cond,
	/** it defines a register preserving computation results of expression **/	expr,
	/** it refers to a memory-address where a variable is defined and used **/	refr,
	/** it denotes a pointer deciding whether a statement will be executed **/	stmt,
}
