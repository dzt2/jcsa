package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

import __backup__.TextMutation;

public class UIOI2MutaTranslator implements Text2MutaTranslator {

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		expression = CTypeAnalyzer.get_expression_of(expression);
		
		switch(mutation.get_mode()) {
		case PREV_INC_INS:	return AstMutation.UIOI(expression, true, COperator.increment);
		case PREV_DEC_INS:	return AstMutation.UIOI(expression, true, COperator.decrement);
		case POST_INC_INS:	return AstMutation.UIOI(expression, false, COperator.increment);
		case POST_DEC_INS:	return AstMutation.UIOI(expression, false, COperator.decrement);
		default: throw new IllegalArgumentException("Invalid operator");
		}
	}

}
