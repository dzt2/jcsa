package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UIOIMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstExpression expression = (AstExpression) location;
		String operand = expression.generate_code();
		switch(source.get_operator()) {
		case insert_prev_inc:	return "++" + operand;
		case insert_prev_dec:	return "--" + operand;
		case insert_post_inc:	return operand + "++";
		case insert_post_dec:	return operand + "--";
		default: throw new IllegalArgumentException(source.toString());
		}
	}

}
