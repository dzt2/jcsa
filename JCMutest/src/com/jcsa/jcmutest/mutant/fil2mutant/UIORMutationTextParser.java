package com.jcsa.jcmutest.mutant.fil2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UIORMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return expression;
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
		String opcode = operand.generate_code();

		switch(source.get_operator()) {
		case prev_inc_to_post_inc:
		case prev_dec_to_post_inc:
		case post_dec_to_post_inc:	return opcode + "++";
		case prev_inc_to_post_dec:
		case prev_dec_to_post_dec:
		case post_inc_to_post_dec:	return opcode + "--";
		case prev_dec_to_prev_inc:
		case post_dec_to_prev_inc:
		case post_inc_to_prev_inc:	return "++" + opcode;
		case prev_inc_to_prev_dec:
		case post_inc_to_prev_dec:
		case post_dec_to_prev_dec:	return "--" + opcode;
		default: throw new IllegalArgumentException("Invalid: " + source);
		}
	}

}
