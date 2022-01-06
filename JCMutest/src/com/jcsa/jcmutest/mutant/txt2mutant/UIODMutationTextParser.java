package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UIODMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstExpression operand;
		if(location instanceof AstIncreUnaryExpression) {
			operand = ((AstIncreUnaryExpression) location).get_operand();
		}
		else {
			operand = ((AstIncrePostfixExpression) location).get_operand();
		}
		operand = CTypeAnalyzer.get_expression_of(operand);
		return "(" + operand.generate_code() + ")";
	}

}
