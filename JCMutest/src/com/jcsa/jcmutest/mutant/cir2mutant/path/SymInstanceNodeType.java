package com.jcsa.jcmutest.mutant.cir2mutant.path;

/**
 * The type of symbolic instance node.
 * 
 * @author yukimula
 *
 */
public enum SymInstanceNodeType {
	/** the nodes before reaching mutated statements **/	path_node,
	/** the mutated statement where mutation seeded **/		muta_node,
	/** the state error node as root of inner statement **/	root_erro,
	/** the state error node generated from inner ones **/	next_erro,
}
