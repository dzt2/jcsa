package com.jcsa.jcmutest.mutant.sym2mutant.tree;

/**
 * The operator used to describe symbolic condition in program context for killing a mutant.
 * @author yukimula
 *
 */
public enum SymOperator {
	
	/* constraint + statement error definitions */
	/** cov_stmt(execution, statement, sym_expression) **/			cov_stmt,
	/** eva_stmt(execution, statement, sym_expression) **/			eva_stmt,
	/** add_stmt(execution, statement, null) **/					add_stmt,
	/** del_stmt(execution, statement, null) **/					del_stmt,
	/** trp_stmt(execution, statement, null) **/					trp_stmt,
	/** mut_flow(execution, orig_flow, muta_flow) **/				mut_flow,
	
	/* boolean error annotations */
	/** chg_bool(execution, expression, null) **/					chg_bool,
	/** set_bool(execution, expression, sym_expression) **/			set_bool,
	/** set_true(execution, expression, null) **/					set_true,
	/** set_fals(execution, expression, null) **/					set_fals,
	
	/* numeric error annotations */
	/** chg_numb(execution, expression, null) **/					chg_numb,
	/** set_numb(execution, expression, sym_expression) **/			set_numb,
	/** set_post(execution, expression, null) **/					set_post,
	/** set_negt(execution, expression, null) **/					set_negt,
	/** set_zero(execution, expression, null) **/					set_zero,
	
	/* address error annotations */
	/** chg_addr(execution, expression, null) **/					chg_addr,
	/** set_addr(execution, expression, sym_expression) **/			set_addr,
	/** set_invp(execution, expression, null) **/					set_invp,
	/** set_null(execution, expression, null) **/					set_null,
	
	/* any type of value error */
	/** chg_auto(execution, expression, null) **/					chg_auto,
	/** set_auto(execution, expression, sym_expression) **/			set_auto,
	
	/* numeric value annotations */
	/** inc_value(execution, expression, null) **/					inc_value,
	/** dec_value(execution, expression, null) **/					dec_value,
	/** ext_value(execution, expression, null) **/					ext_value,
	/** shk_value(execution, expression, null) **/					shk_value,
	
	/* expression error type */
	/** mut_value(execution, expression, null) **/					mut_value,
	/** mut_refer(execution, expression, null) **/					mut_refer,
	/** mut_state(execution, expression, null) **/					mut_state,
	
}
