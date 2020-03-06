package com.jcsa.jcmuta;

/**
 * The class of mutation operator (as MutaOperator)
 * @author yukimula
 *
 */
public enum MutaClass {
	
	/* trapping mutation class */
	/** trap_on_true | trap_on_false **/				BTRP,
	/** trap_on_case(expression, val) **/				CTRP,
	/** trap_on_expression(expression) **/				ETRP,
	/** trap_on_statement(statement) **/				STRP,
	/** trap_at_time(statement, times) **/				TTRP,
	/** trap_on_pos | trap_on_neg | trap_on_zro **/ 	VTRP,
	
	/* statement mutation class */
	/** break_to_continue | continue_to_break **/		SBCR,
	/** ins_break | ins_continue (statement) **/		SBCI,
	/** while_to_do | do_to_while(statement) **/		SWDR,
	/** set_label(goto_statement, label) **/			SGLR,
	/** delete_statement(statement) **/					STDL,
	
	/* unary operator mutation class */
	/** prev_inc_to_post_inc | prev_dec_to_prev_inc **/	UIOR,
	/** prev_inc_ins(expr) | post_dec_ins(expr) **/		UIOI,
	/** prev_inc_del(expr) | post_dec_del(expr) **/		UIOD,
	/** inc_value | mul_value **/						VINC,
	/** ins_arith_neg | ins_bitws_rsv | ins_logic_not
	 *  ins_abs_call | ins_nabs_call **/				UNOI,
	/** del_arith_neg, del_bitws_rsv, del_logic_not **/	UNOD,
	
	/* operator mutation class */
	OAAN, OABN, OALN, OARN,
	OBAN, OBBN, OBLN, OBRN,
	OLAN, OLBN, OLLN, OLRN,
	ORAN, ORBN, ORLN, ORRN,
	OEAA, OEBA,
	OAAA, OABA, OAEA,
	OBAA, OBBA, OBEA,
	
	/** delete_operand(expression, operand) **/			OPDL,	// TODO unable to design
	
	/* value mutation class */
	/** set_true | set_false **/						VBRP,
	/** set_constant(x, y) **/							VCRP,
	/** set_reference(x, y) **/							VRRP,
	/** set_return_val(retr_statement, expr) **/		SRTR,
	
	/* semantic mutation operators */
	/** equal_with_to_assign **/						EQAR,
	/** ins_empty_stmt(condition_stmt) **/				OSBI,
	/** ins_elif_in_if(if_statement) **/				OIFI,
	/** set_elif_as_else(if_statement) **/				OIFR,
	/** ins_default(switch_statement) **/				ODFI,
	/** set_default(case_statement) **/					ODFR,
	/** equal_with_to_compare, not_equal_to_compare **/	OFLT,
	
}
