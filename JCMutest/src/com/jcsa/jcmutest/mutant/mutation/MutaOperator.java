package com.jcsa.jcmutest.mutant.mutation;

/**
 * Mutation operator for C programming language.
 * 
 * @author yukimula
 *
 */
public enum MutaOperator {
	
	/* BTRP */
	/** trap_on_true(expression) **/	trap_on_true,
	/** trap_on_false(expression) **/	trap_on_false,
	/* CTRP */
	/** trap_on_case(expression, int) **/	trap_on_case,
	/* ETRP & STRP & TTRP */
	/** trap_on_expression(expression) **/	trap_on_expression,
	/** trap_on_statement(statement) **/	trap_on_statement,
	/** trap_for_time(statement, int) **/	trap_for_time,
	/* VTRP */
	/** trap_on_pos(expression) **/			trap_on_pos,
	/** trap_on_zro(expression) **/			trap_on_zro,
	/** trap_on_neg(expression) **/			trap_on_neg,
	/** trap_on_nzro(expression) **/		trap_on_nzro,
	
	/* SBCR */
	/** break_to_continue(break_statement) **/		break_to_continue,
	/** continue_to_break(continue_statement) **/	continue_to_break,
	/* SWDR */
	/** while_to_do_while(while_statement) **/		while_to_do_while,
	/** do_while_to_while(do_while_statement) **/	do_while_to_while,
	/* SGLR */
	/** set_goto_label(goto_statement, label) **/	set_goto_label,
	/* STDL */
	/** delete_statement(statement) **/				delete_statement,
	
	/* UIOR */
	/** prev_inc_to_prev_dec(prev_inc_expr) **/		prev_inc_to_prev_dec,
	/** prev_inc_to_post_dec(prev_inc_expr) **/		prev_inc_to_post_dec,
	/** prev_inc_to_post_inc(prev_inc_expr) **/		prev_inc_to_post_inc,
	/** prev_dec_to_prev_inc(prev_dec_expr) **/		prev_dec_to_prev_inc,
	/** prev_dec_to_post_dec(prev_dec_expr) **/		prev_dec_to_post_dec,
	/** prev_dec_to_post_inc(prev_dec_expr) **/		prev_dec_to_post_inc,
	/** post_inc_to_post_dec(post_inc_expr) **/		post_inc_to_post_dec,
	/** post_inc_to_prev_dec(post_inc_expr) **/		post_inc_to_prev_dec,
	/** post_inc_to_prev_inc(post_inc_expr) **/		post_inc_to_prev_inc,
	/** post_dec_to_post_inc(post_dec_expr) **/		post_dec_to_post_inc,
	/** post_dec_to_prev_dec(post_dec_expr) **/		post_dec_to_prev_dec,
	/** post_dec_to_prev_inc(post_dec_expr) **/		post_dec_to_prev_inc,
	/* UIOI */
	/** insert_prev_inc(expression) **/				insert_prev_inc,
	/** insert_prev_dec(expression) **/				insert_prev_dec,
	/** insert_post_inc(expression) **/				insert_post_inc,
	/** insert_post_dec(expression) **/				insert_post_dec,
	/* UIOD */
	/** delete_prev_inc(expression) **/				delete_prev_inc,
	/** delete_prev_dec(expression) **/				delete_prev_dec,
	/** delete_post_inc(expression) **/				delete_post_inc,
	/** delete_post_dec(expression) **/				delete_post_dec,
	/* VINC */
	/** inc_constant(expression, integer) **/		inc_constant,
	/** mul_constant(expression, double) **/		mul_constant,
	/* UNOI */
	/** insert_arith_neg(expression) **/			insert_arith_neg,
	/** insert_bitws_rsv(expression) **/			insert_bitws_rsv,
	/** insert_logic_not(expression) **/			insert_logic_not,
	/** insert_abs_value(expression) **/			insert_abs_value,
	/** insert_nabs_value(expression) **/			insert_nabs_value,
	/* UNOD */
	/** delete_arith_neg(expression) **/			delete_arith_neg,
	/** delete_bitws_rsv(expression) **/			delete_bitws_rsv,
	/** delete_logic_not(expression) **/			delete_logic_not,
	
	/* VBRP */
	/** set_true(expression) **/					set_true,
	/** set_false(expression) **/					set_false,
	/* VCRP */
	/** set_integer(expression, int) **/			set_integer,
	/** set_double(expression, double) **/			set_double,
	/* VRRP */
	/** set_reference(expression, String) **/		set_reference,
	/* RTRP */
	/** set_return(expression, expression) **/		set_return,
	
	/* OAAN, OABN, OALN, OARN, OBAN, OBBN, OBLN, 
	 * OBRN, OLAN, OLBN, OLLN, OLRN, ORAN, ORBN,
	 * ORLN, ORRN, OEAA, OEBA, OAEA, OBEA, OAAA,
	 * OABA, OBAA, OBBA */
	/** set_operator(expression, operator) **/		set_operator,
	/** cmp_operator(expression, operator) **/		cmp_operator,
	
}
