package com.jcsa.jcparse.flwa.depend;

public enum CDependType {
	
	/* control dependence */
	/** stmt ==> if_stmt **/			predicate_depend,
	/** stmt ==> exit **/				stmt_exit_depend,
	/** stmt ==> call **/				stmt_call_depend,
	
	/* data dependence */
	/** statement ==> assignment **/	use_defin_depend,
	/** init_stmt ==> call_stmt **/		param_arg_depend,
	/** wait_stmt ==> retr_stmt **/		wait_retr_depend,
	
}
