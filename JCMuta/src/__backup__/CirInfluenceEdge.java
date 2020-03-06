package __backup__;


/**
 * The edge in influence graph represents the influence such that source influences on the target,
 * of which type can be one of the following:<br>
 * 
 * <code>
 * 	exec_a	: call_stmt		-->	call_stmt.function|argument<br>
 * 	exec_c	: if_stmt		-->	if_stmt.condition<br>
 * 	exec_e	: assign_stmt	-->	assign_stmt.rvalue<br>
 * 	exec_t	: if_stmt.expr	--> statement*<br>
 * 	exec_f	: if_stmt.expr	--> statement*<br>
 * 	
 * 	pas_du	: assign.lvalue	-->	statement.expr<br>
 * 	pas_ud	: assign.rvalye --> assign.lvalue<br>
 * 	pas_ap	: call_stmt.arg	-->	init_assign.rvalue<br>
 * 	pas_rw	: retr_stmt.lval--> wait_stmt.rvalue<br>
 * 	pas_fw	: wait_expr.func-->	wait_expr<br>
 * 	
 * 	gen_af	: call_stmt.arg	-->	wait_expr.function<br>
 * 	gen_cp	: expression	--> expression.parent<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public class CirInfluenceEdge {
	/* attributes */
	/** the type of the influence **/
	private CirInfluenceEdgeType type;
	/** the source node from which this edge points to another **/
	private CirInfluenceNode source;
	/** the target node to which this edge points from another **/
	private CirInfluenceNode target;
	
	/* constructor */
	/**
	 * create an influence edge from source to target with respect to the type,
	 * which means the source influences on target with respect to a specific way.
	 * @param type
	 * @param source
	 * @param target
	 */
	protected CirInfluenceEdge(CirInfluenceEdgeType type, 
			CirInfluenceNode source, CirInfluenceNode target) {
		this.type = type; this.source = source; this.target = target;
	}
	
	/* getters */
	/**
	 * get the type of the influence
	 * @return
	 */
	public CirInfluenceEdgeType get_type() { return type; }
	/**
	 * get the source node that influences on another
	 * @return
	 */
	public CirInfluenceNode get_source() { return source; }
	/**
	 * get the target node being influenced by another
	 * @return
	 */
	public CirInfluenceNode get_target() { return target; }
	
}
