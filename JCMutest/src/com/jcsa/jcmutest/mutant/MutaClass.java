package com.jcsa.jcmutest.mutant;

/**
 * The class of mutation operators defined on C programming language.<br>
 * 	<br>
 * 	<code>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	trapping-mutation-group										|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	BTRP: trap_on_true | trap_on_false							|	<br>
 * 	|	CTRP: trap_on_case											|	<br>
 * 	|	ETRP: trap_on_expression									|	<br>
 * 	|	STRP: trap_on_statement										|	<br>
 * 	|	TTRP: trap_for_time											|	<br>
 * 	|	VTRP: trap_on_pos | trap_on_zro | trap_on_neg				|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	statement-mutation-group									|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	SBCR: break_to_continue | continue_to_break					|	<br>
 * 	|	SWDR: do_while_to_while | while_to_do_while					|	<br>
 * 	|	SGLR: set_goto_label										|	<br>
 * 	|	STDL: delete_statement										|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	unary-operator-mutation-group								|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	UIOR: prev_inc_to_prev_dec | prev_dec_to_post_inc ...		|	<br>
 * 	|	UIOI: insert_prev_inc | insert_post_dec						|	<br>
 * 	|	UIOD: delete_prev_dec | delete_post_inc						|	<br>
 * 	|	VINC: inc_constant | mul_constant							|	<br>
 * 	|	UNOI: insert_arith_neg | insert_bitws_rsv | insert_abs ...	|	<br>
 * 	|	UNOD: delete_arith_neg | delete_logic_not | ...				|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	reference-mutation-group									|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	VBRP: set_true | set_false									|	<br>
 * 	|	VCRP: set_integer | set_double								|	<br>
 * 	|	VRRP: set_reference											|	<br>
 * 	|	RTRP: set_return											|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	binary-operator-mutation-group								|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	OAAN, OABN, OALN, OARN										|	<br>
 * 	|	OBAN, OBBN, OBLN, OBRN										|	<br>
 * 	|	OLAN, OLBN, OLLN, OLRN										|	<br>
 * 	|	ORAN, ORBN, ORLN, ORRN										|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	assignment-mutation-group									|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	|	OEAA, OEBA, OAEA, OBEA										|	<br>
 * 	|	OAAA, OABA, OBAA, OBBA										|	<br>
 * 	+---------------------------------------------------------------+	<br>
 * 	</code>
 * 	<br>
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
	OEAA, OEBA, OAAA, OABA,
	OAEA, OBAA, OBBA, OBEA,

	/* value mutation class */
	/** set_true | set_false **/						VBRP,
	/** set_constant(x, y) **/							VCRP,
	/** set_reference(x, y) **/							VRRP,
	/** set_return_val(retr_statement, expr) **/		RTRP,

}
