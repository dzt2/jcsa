package com.jcsa.jcmutest.mutant.cir2mutant.base;

public enum CirAttributeType {
	/* constraint category */
	/** {condition, execution, statement, expression} **/	condition,
	/** {cov_count, execution, statement, integer} **/		cov_count,
	/* expression error type */
	/** {dif_error, execution, orig_expr, muta_expr} **/	dif_error,
	/** {val_error, execution, orig_expr, muta_expr} **/	val_error,
	/** {ref_error, execution, orig_refr, muta_refr} **/	ref_error,
	/** {sta_error, execution, orig_refr, muta_expr} **/	sta_error,
	/* statement error type */
	/** {flw_error, source, orig_target, muta_target} **/	flw_error,
	/** {blk_error, execution, statement, true|false} **/	blk_error,
	/** {trp_error, execution, statement, true} **/			trp_error,
}
