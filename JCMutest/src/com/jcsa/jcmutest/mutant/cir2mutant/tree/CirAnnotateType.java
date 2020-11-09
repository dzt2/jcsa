package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * The type of annotation to describe program state in C-intermediate representation code.
 * 
 * @author yukimula
 *
 */
public enum CirAnnotateType {
	
	/* constraint + statement error definitions */
	/** covr_stmt(statement, sym_expression) **/		covr_stmt,
	/** eval_stmt(statement, sym_expression) **/		eval_stmt,
	/** add_stmt(statement, null) **/					add_stmt,
	/** del_stmt(statement, null) **/					del_stmt,
	/** trap_stmt(statement, null) **/					trap_stmt,
	
	/* boolean error annotations */
	/** chg_bool(expression, null) **/					chg_bool,
	/** set_bool(expression, sym_expression) **/		set_bool,
	/** set_true(expression, null) **/					set_true,
	/** set_false(expression, null) **/					set_false,
	
	/* numeric error annotations */
	/** chg_numb(expression, null) **/					chg_numb,
	/** set_numb(expression, sym_expression) **/		set_numb,
	/** set_post(expression, null) **/					set_post,
	/** set_negt(expression, null) **/					set_negt,
	/** set_zero(expression, null) **/					set_zero,
	
	/* address error annotations */
	/** chg_addr(expression, null) **/					chg_addr,
	/** set_addr(expression, sym_expression) **/		set_addr,
	/** set_invp(expression, null) **/					set_invp,
	/** set_null(expression, null) **/					set_null,
	
	/* any type of value error */
	/** chg_auto(expression, null) **/					chg_auto,
	/** set_auto(expression, sym_expression) **/		set_auto,
	
	/* numeric value annotations */
	/** inc_value(expression, null) **/					inc_value,
	/** dec_value(expression, null) **/					dec_value,
	/** ext_value(expression, null) **/					ext_value,
	/** shk_value(expression, null) **/					shk_value,
	
	/* expression error type */
	/** mut_value(expression, null) **/					mut_value,
	/** mut_refer(expression, null) **/					mut_refer,
	/** mut_state(expression, null) **/					mut_state,
	
}
