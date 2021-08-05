package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * It defines the extension type from a cir-mutation tree node to any of its
 * children.
 *
 * @author yukimula
 *
 */
public enum CirMutationTreeFlow {
	/** cov_stmt|eva_expr 	--> cov_stmt|eva_expr **/	execute,
	/** eva_expr|cov_stmt 	--> any_error **/			infect,
	/** any_error			-->	any_error **/			propagate,
}
