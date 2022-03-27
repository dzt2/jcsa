package com.jcsa.jcmutest.mutant.uni2mutant.base;

/**
 * 	It describes a uniformed abstract state being evaluated at some program code
 * 	location (combining ast_node, cir_node and cir_execution tuple).<br>
 * 	<br>
 * 	
 * 	<code>
 * 	UniAbstractState				[st_class, c_loct; lsh_value, rsh_value]	<br>
 * 	|--	UniConditionState			[st_class, c_stmt; lsh_value, rsh_value]	<br>
 * 	|--	|--	UniCoverTimesState		[cov_time, c_stmt; min_times, max_times]	<br>
 * 	|--	|--	UniConstraintState		[eva_cond, c_stmt; condition, must_need]	<br>
 * 	|--	|--	UniSeedMutantState		[sed_muta, c_stmt; mutant_ID, clas_oprt]	<br>
 * 	|--	UniPathErrorState			[s_class,  c_stmt; lsh_value, rsh_value]	<br>
 * 	|--	|--	UniBlockErrorState		[mut_stmt, c_stmt; orig_exec, muta_exec]	<br>
 * 	|--	|--	UniFlowsErrorState		[mut_flow, c_stmt; orig_next, muta_next]	<br>
 * 	|--	UniDataErrorState			[st_class, c_expr; orig_expr, parameter]	<br>
 * 	|--	|--	UniValueErrorState		[set_expr, c_expr; orig_expr, muta_expr]	<br>
 * 	|--	|--	UniIncreErrorState		[inc_expr, c_expr; orig_expr, different]	<br>
 * 	|--	|--	UniBixorErrorState		[xor_expr, c_expr; orig_expr, different]	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *	
 */
public abstract class UniAbstractState {
	
	
	
	
	
	
	
	
	
	
}
