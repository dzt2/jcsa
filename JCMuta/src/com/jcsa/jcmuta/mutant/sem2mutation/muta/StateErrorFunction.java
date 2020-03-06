package com.jcsa.jcmuta.mutant.sem2mutation.muta;

public enum StateErrorFunction {
	
	/** trapping() **/				trapping,
	/** active(statement) **/		active,
	/** disactive(statement) **/	disactive,
	
	/** mut_value **/				mut_value,
	/** not_value(expr) **/			not_value,
	/** mut_refer(lvalue) **/		mut_refer,
	
	/** inc_value(expr) **/			inc_value,
	/** dec_value(expr) **/			dec_value,
	/** neg_value(expr) **/			neg_value,
	/** rsv_value(expr) **/			rsv_value,
	
	/** set_bool(expr, boolean) **/	set_bool,
	/** set_value(expr, long) **/	set_value,
	/** dif_value(expr, val) **/	dif_value,
	
}
