package __backup__;

/**
 * The type of the influence edge in influence graph can be one of the following type:<br>
 * <code>
 * 	exec_a	: call_stmt		-->	call_stmt.function<br>
 * 	exec_c	: if_stmt		-->	if_stmt.condition<br>
 * 	exec_e	: assign_stmt	-->	assign_stmt.rvalue<br>
 * 	exec_t	: if_stmt.expr	--> statement*<br>
 * 	exec_f	: if_stmt.expr	--> statement*<br>
 * 	exec_p	: call_stmt.fun	--> call_stmt.arg<br.
 * 	
 * 	pas_du	: assign.lvalue	-->	statement.expr<br>
 * 	pas_ud	: assign.rvalye --> assign.lvalue<br>
 * 	pas_ap	: call_stmt.arg	-->	init_assign.rvalue<br>
 * 	pas_rw	: retr_stmt.lval--> wait_stmt.rvalue<br>
 * 
 * 	gen_fw	: wait_expr.func-->	wait_expr<br>
 * 	gen_af	: call_stmt.arg	-->	wait_expr.function<br>
 * 	gen_cp	: expression	--> expression.parent<br>
 * </code>
 * @author yukimula
 *
 */
public enum CirInfluenceEdgeType {
	
	/** call_stmt --> call_stmt.fun **/			exec_a,
	/** if_stmt --> if_stmt.condition **/		exec_c,
	/** assignment --> assignment.rvalue **/	exec_e,
	/** if_stmt.condition --> statement* **/	exec_t,
	/** if_stmt.condition --> statement* **/	exec_f,
	/** call_stmt.fun --> call_stmt.arg **/		exec_p,
	
	/** define_use [refer --> refer] **/		pas_du,
	/** use_define [rvalue --> lvalue] **/		pas_ud,
	/** call_stmt.arg --> init_assign.rval **/	pas_ap,
	/** retr_stmt.lval --> wait_stmt.rval **/	pas_rw,
	
	/** wait_expr.function --> wait_expr **/	gen_fw,
	/** call_stmt.arg --> wait_stmt.func **/	gen_af,
	/** expression --> expression.parent **/	gen_cp,
	
}
