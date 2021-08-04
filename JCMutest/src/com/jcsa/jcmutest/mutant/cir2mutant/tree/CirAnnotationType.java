package com.jcsa.jcmutest.mutant.cir2mutant.tree;

public enum CirAnnotationType {
	
	/* constraint */
	/** constraint:cov_stmt:execution:statement:integer **/			cov_stmt,
	/** constraint:eva_expr:execution:statement:condition **/		eva_expr,
	
	/* stmt_error */
	/** stmt_error:mut_stmt:execution:statement:{true|false} **/	mut_stmt,
	/** stmt_error:mut_flow:source:orig_target:muta_target **/		mut_flow,
	/** stmt_error:trp_stmt:execution:statement:null **/			trp_stmt,
	
	/* expr_error */
	/** bool_error:set_bool:execution:expression:{true|false} **/	set_bool,
	/** bool_error:chg_bool:execution:expression:null **/			chg_bool,
	
	/* numb_error */
	/** numb_error:set_numb:execution:expression:constant **/		set_numb,
	/** numb_error:set_post:execution:expression:null **/			set_post,
	/** numb_error:set_negt:execution:expression:null **/			set_negt,
	/** numb_error:set_npos:execution:expression:null **/			set_npos,
	/** numb_error:set_nneg:execution:expression:null **/			set_nneg,
	/** numb_error:set_nzro:execution:expression:null **/			set_nzro,
	/** numb_error:chg_numb:execution:expression:null **/			chg_numb,
	
	/* addr_error */
	/** addr_error:set_addr:execution:expression:{NULL} **/			set_addr,
	/** addr_error:set_invp:execution:expression:null **/			set_invp,
	/** addr_error:chg_addr:execution:expression:null **/			chg_addr,
	
	/* auto_error */
	/** auto_error:chg_auto:execution:expression:null **/			chg_auto,
	
	/* scop_error */
	/** scop_error:inc_scop:execution:expression:null **/			inc_scop,
	/** scop_error:dec_scop:execution:expression:null **/			dec_scop,
	/** scop_error:ext_scop:execution:expression:null **/			ext_scop,
	/** scop_error:shk_scop:execution:expression:null **/			shk_scop,
	
	/* symb_error */
	/** symb_error:mut_expr:execution:expression:symbolic **/		mut_expr,
	/** symb_error:mut_refr:execution:expression:symbolic **/		mut_refr,
	/** symb_error:mut_stat:execution:expression:symbolic **/		mut_stat,
	
}
