package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

/**
 * It defines the category of store unit where the annoataion is defined.
 * 
 * @author yukimula
 *
 */
public enum CirAnnotationClass {
	/** it denote a register to preserve the logical formula in path condition. **/	cond,
	/** it defines a register or memory cache saving the computational outputs. **/	expr,
	/** it denotes a user-declared variable preserving a data state in program. **/	refr,
	/** it refers to a pointer deciding whether a statement is executed or not. **/	stmt,
}
