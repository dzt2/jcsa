package com.jcsa.jcmuta.mutant.code2mutation;

public class MutaCodeTemplates {
	
	/** trap_on_expression(%s): [expression_code] **/
	public static final String trap_on_expr_template = "jcm_trap_on_expression(%s)";
	
	/**
	 * ('i,u,f'; [expression_code]; "true|false")
	 */
	public static final String trap_on_bool_template = "jcm_trap_on_boolean_%c(%s, jcm_%s_constant)";
	
	/**
	 * (data_type; 'i|u'; [expression_code]; [expression_code])
	 */
	public static final String trap_on_case_template = "(%s) jcm_trap_on_case_%c(%s, %s)";
	
	public static final String trap_on_stmt_template = "jcm_traps();";
	
	public static final String ini_trap_timer_template = "jcm_init_trap_timmer(%d);";
	public static final String inc_trap_timer_template = "jcm_incre_trap_timmer();";
	
	/** ([data_type]; [u|i|f]; [expression_code]; [pos|neg|zro]) **/
	public static final String trap_on_domain_template = "(%s) jcm_trap_on_domain_%c(%s, jcm_%s_domain)";
	
	public static final String trap_on_condition1_template = "jcm_trap_on_true_at_b(%s, %d)";
	
	public static final String trap_if_different_template = "jcm_trap_if_different(%s, %s)";
	
	/** [data_type_code]; [arith|bitws|logic|relation]; [i|u|f]; [operator_name]; 
	 *  [loperand_code]; [roperand_code]; [true|false]; **/
	public static final String delete_operand_template = "((%s) jcm_delete_%s_operand_%c"
			+ "(jcm_%s_operator, %s, %s, jcm_%s_constant))";
	
	/** [data_type_code]; [u|i|f]; [arith_neg|bitws_rsv|logic_not|abs_invoc|nabs_invoc]; [expr_code] **/
	public static final String insert_unary_operand_template = "((%s) "
			+ "jcm_insert_unary_operator_%c(jcm_%s_operator, %s))";
	
	
	public static final String abs_invocation_template = "((%s) jcm_abs_operation_%c(%s))";
	public static final String nabs_invocation_template = "((%s) jcm_nabs_operation_%c(%s))";
	
	public static final String delete_unary_operand_template = 
			"((%s) jcm_delete_unary_operator_%c(jcm_%s_operator, %s))";
	
	/** [data_type_code]; [arith|bitws|logic|relation] **/
	public static final String replace_operator_template = "((%s) jcm_%s_mutate_operator_%c"
			+ "(jcm_%s_operator, %s, %s, jcm_%s_operator))";
	
	/** [_weak?] [loperand] [roperand] **/
	public static final String real_equal_with_template = "jcm_real_equal_with%s(%s, %s)";
	public static final String real_not_equals_template = "jcm_real_not_equals%s(%s, %s)";
	
}
