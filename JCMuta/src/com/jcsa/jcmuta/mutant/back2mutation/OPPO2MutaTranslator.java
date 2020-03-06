package com.jcsa.jcmuta.mutant.back2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

import __backup__.TextMutation;

public class OPPO2MutaTranslator implements Text2MutaTranslator {

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		switch(mutation.get_mode()) {
		case POST_PREV_INC:
			return AstMutation.UIOR(expression, true, COperator.increment);
		case POST_INC_DEC:
			return AstMutation.UIOR(expression, false, COperator.decrement);
		case PREV_POST_INC:
			return AstMutation.UIOR(expression, false, COperator.increment);
		case PREV_INC_DEC:
			return AstMutation.UIOR(expression, true, COperator.decrement);
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_mode());
		}
	}

}
