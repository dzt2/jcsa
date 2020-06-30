package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

import __backup__.TextMutation;

public class STRC2MutaTranslator implements Text2MutaTranslator {

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		switch(mutation.get_mode()) {
		case TRAP_ON_TRUE:	return AstMutation.BTRP(expression, true);
		case TRAP_ON_FALSE:	return AstMutation.BTRP(expression, false);
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_mode());
		}
	}

}
