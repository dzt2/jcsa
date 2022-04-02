package com.jcsa.jcmutest.mutant.cod2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UNODMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstUnaryExpression expression = (AstUnaryExpression) location;
		AstExpression operand = CTypeAnalyzer.get_expression_of(expression.get_operand());
		return "(" + operand.generate_code() + ")";
	}

}
