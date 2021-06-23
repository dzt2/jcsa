package com.jcsa.jcmutest.mutant.cir2mutant.base;

/**
 * The refined type of SymbolicCondition to describe mutation semantics
 * 
 * @author yukimula
 *
 */
public enum SymOperator {
	
	/* constraint operator */
	/** constraints:cov_stmt(execution, statement, loop_times) **/			cov_stmt,
	/** constraints:eva_expr(execution, statement, sym_expression) **/		eva_expr,
	
	/* statement-related error */
	/** observation:add_stmt(execution, statement, null) **/				add_stmt,
	/** observation:del_stmt(execution, statement, null) **/				del_stmt,
	/** observation:trp_stmt(execution, statement, null) **/				trp_stmt,
	/** observation:mut_flow(execution, orig_target, muta_target) **/		mut_flow,
	
	/* boolean-related error */
	/** observation:not_bool(execution, expression, null) **/				chg_bool,
	/** observation:set_true(execution, expression, null) **/				set_true,
	/** observation:set_fals(execution, expression, null) **/				set_fals,
	/** observation:set_bool(execution, expression, sym_expression) **/		set_bool,
	
	/* numeric-related error */
	/** observation:chg_numb(execution, expression, null) **/				chg_numb,
	/** observation:set_post(execution, expression, null) **/				set_post,
	/** observation:set_negt(execution, expression, null) **/				set_negt,
	/** observation:set_zero(execution, expression, null) **/				set_zero,
	/** observation:set_npos(execution, expression, null) **/				set_npos,
	/** observation:set_nneg(execution, expression, null) **/				set_nneg,
	/** observation:set_nzro(execution, expression, null) **/				set_nzro,
	/** observation:set_numb(execution, expression, sym_expression) **/		set_numb,
	
	/* address-related error */
	/** observation:chg_addr(execution, expression, null) **/				chg_addr,
	/** observation:set_invp(execution, expression, null) **/				set_invp,
	/** observation:set_null(execution, expression, null) **/				set_null,
	/** observation:set_addr(execution, expression, sym_expression) **/		set_addr,
	
	/* non-numeric error */
	/** observation:chg_auto(execution, expression, null) **/				chg_auto,
	/** observation:set_auto(execution, expression, sym_expression) **/		set_auto,
	
	/* value scope error */
	/** observation:inc_scop(execution, expression, null) **/				inc_scop,
	/** observation:dec_scop(execution, expression, null) **/				dec_scop,
	/** observation:ext_scop(execution, expression, null) **/				ext_scop,
	/** observation:shk_scop(execution, expression, null) **/				shk_scop,
	
	/* top abstract value error */
	/** observation:mut_expr(execution, expression, null) **/				mut_expr,
	/** observation:mut_refr(execution, expression, null) **/				mut_refr,
	/** observation:mut_stat(execution, expression, null) **/				mut_stat,
	
}
