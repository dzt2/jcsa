package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class CTRPMutationTextParser extends MutationTextParser {

	/** switch_condition.code, case_condition.code **/
	private static final String template = "(jcm_trap_on_case(%s, %s))";

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstExpression expression = (AstExpression) location;
		AstExpression parameter = (AstExpression) source.get_parameter();
		String expr_code = "(" + expression.generate_code() + ")";
		String param_code = "(" + parameter.generate_code() + ")";
		return String.format(template, expr_code, param_code);
	}

}
