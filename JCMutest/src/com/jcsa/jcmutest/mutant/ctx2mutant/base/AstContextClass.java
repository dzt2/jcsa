package com.jcsa.jcmutest.mutant.ctx2mutant.base;

public enum AstContextClass {
	
	/** cov_time(statement;	 min_times, max_times) 	**/	cov_time,
	/** eva_cond(statement;	 condition, must_need) 	**/	eva_cond,
	/** sed_muta(location;	 mutant_ID, clas_oprt) 	**/	sed_muta,
	
	/** set_stmt(statement;	 orig_exec, muta_exec) 	**/	set_stmt,
	/** set_flow(statement;	 orig_next, muta_next) 	**/	set_flow,
	/** set_expr(expression; orig_expr, muta_expr)  **/	set_expr,
	
}
