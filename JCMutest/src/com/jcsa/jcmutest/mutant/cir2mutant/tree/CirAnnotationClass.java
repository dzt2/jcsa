package com.jcsa.jcmutest.mutant.cir2mutant.tree;

public enum CirAnnotationClass {
	/** cov_stmt; eva_expr; **/								constraint,
	/** mut_stmt; mut_flow; trp_stmt; **/					stmt_error,
	/** mut_expr; mut_refr; mut_stat; **/ 					symb_error,
	/** set_bool; chg_bool; **/ 							bool_error,
	/** set_numb; set_post; set_npos; ... chg_numb; **/ 	numb_error,
	/** set_addr; set_invp; chg_addr; **/ 					addr_error,
	/** set_auto; chg_auto; **/ 							auto_error,
	/** inc_scop; dec_scop; ext_scop; shk_scop; **/ 		scop_error,
}
