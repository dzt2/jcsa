package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

/**
 * The word is used to describe the feature hold by each concrete state error.
 * 
 * @author yukimula
 *
 */
public enum CirStateErrorWord {
	
	/* boolean error */	not_bool, set_true, set_false,
	
	/* numeric error */	chg_numb, set_pos, set_neg, set_zro,
	
	/* address error */	chg_addr, set_nullptr, set_invalid,
	
	/* traping error */	trapping, 
	
	/* path error */	chg_flow,
	
	/* other type */	chg_bytes,
	
	/* value changed */	inc_value, dec_value, ext_value, shk_value,
	
}
