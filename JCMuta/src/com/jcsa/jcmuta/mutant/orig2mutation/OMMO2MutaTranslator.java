package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

import __backup__.TextMutation;

public class OMMO2MutaTranslator implements Text2MutaTranslator {

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		switch(mutation.get_mode()) {
		case POST_PREV_DEC:	return AstMutation.UIOR(expression, true, COperator.decrement);
		case POST_DEC_INC:	return AstMutation.UIOR(expression, false, COperator.increment);
		case PREV_POST_DEC:	return AstMutation.UIOR(expression, false, COperator.decrement);
		case PREV_DEC_INC:	return AstMutation.UIOR(expression, true, COperator.increment);
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_mode());
		}
	}

}
