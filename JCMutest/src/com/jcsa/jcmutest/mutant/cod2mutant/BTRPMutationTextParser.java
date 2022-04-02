package com.jcsa.jcmutest.mutant.cod2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class BTRPMutationTextParser extends MutationTextParser {

	/** operator, expression.generate_code **/
	private static final String template = "(jcm_%s(%s))";

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstExpression expression = (AstExpression) location;
		String operator = source.get_operator().toString();
		String expr_code = expression.generate_code();
		return String.format(template, operator, expr_code);
	}

}
