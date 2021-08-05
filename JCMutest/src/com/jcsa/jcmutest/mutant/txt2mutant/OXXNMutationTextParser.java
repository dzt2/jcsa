package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OXXNMutationTextParser extends MutationTextParser {

	private static final String template = "(jcm_%s_%s(%d, %s, %s))";

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		return source.get_location();
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstBinaryExpression expression = (AstBinaryExpression) location;
		String loperand = "(" + CTypeAnalyzer.get_expression_of(
				expression.get_loperand()).generate_code() + ")";
		String roperand = "(" + CTypeAnalyzer.get_expression_of(
				expression.get_roperand()).generate_code() + ")";
		COperator operator1 = expression.get_operator().get_operator();
		COperator operator2 = (COperator) source.get_parameter();

		switch(source.get_operator()) {
		case set_operator:
			return String.format(template, operator1.toString(), operator2.toString(), 0, loperand, roperand);
		case cmp_operator:
			return String.format(template, operator1.toString(), operator2.toString(), 1, loperand, roperand);
		default: throw new IllegalArgumentException("Invalid: " + source);
		}
	}

}
