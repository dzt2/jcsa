package com.jcsa.jcmutest.mutant.sym2mutant.cond;

/**
 * The operator used to refine the symbolic condition description in the program context.
 * 
 * @author yukimula
 *
 */
public enum SymOperator {
	
	/* constraint operators */
	/** assertion:eva_expr(execution, statement, condition) **/				eva_expr,
	/** assertion:cov_stmt(execution, statement, condition) **/				cov_stmt,
	
	/* statement error operators */
	/** observation:mut_stmt(execution, statement, boolean) **/				mut_stmt,
	/** observation:mut_stmt(execution, statement, null) **/				trp_stmt,
	/** observation:mut_flow(execution, orig_stmt, muta_stmt) **/			mut_flow,
	
	/* boolean value error */
	/** observation:set_bool(execution, expression, muta_value) **/			set_bool,
	
	/* numeric value error */
	/** observation:set_numb(execution, expression, muta_value|null) **/ 	set_numb,
	/** observation:set_post(execution, expression, null) **/ 				set_post,
	/** observation:set_negt(execution, expression, null) **/ 				set_negt,
	/** observation:set_zero(execution, expression, null) **/ 				set_zero,
	
	/* address value error */
	/** observation:set_addr(execution, expression, muta_value|null) **/	set_addr,
	/** observation:set_invp(execution, expression, null) **/				set_invp,
	/** observation:set_null(execution, expression, null) **/				set_null,
	
	/* bytes body error */
	/** observation:set_byte(execution, expression, muta_value|null) **/	set_auto,
	
	/* numeric or address value error */
	/** observation:inc_scop(execution, expression, null) **/				inc_scop,
	/** observation:dec_scop(execution, expression, null) **/				dec_scop,
	/** observation:ext_scop(execution, expression, null) **/				ext_scop,
	/** observation:shk_scop(execution, expression, null) **/				shk_scop,
	
	/* abstract value error type */
	/** observation:mut_expr(execution, expression, null) **/				mut_expr,
	/** observation:mut_refr(execution, expression, null) **/				mut_refr,
	/** observation:mut_stat(execution, expression, null) **/				mut_stat,
	
}
