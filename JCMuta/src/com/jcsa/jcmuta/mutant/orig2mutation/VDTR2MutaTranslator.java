package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

import __backup__.TextMutation;

public class VDTR2MutaTranslator implements Text2MutaTranslator {

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		expression = CTypeAnalyzer.get_expression_of(expression);
		
		switch(mutation.get_mode()) {
		case TRAP_ON_POS:	return AstMutation.VTRP(expression, 'p');
		case TRAP_ON_NEG:	return AstMutation.VTRP(expression, 'n');
		case TRAP_ON_ZRO:	return AstMutation.VTRP(expression, '0');
		default: throw new IllegalArgumentException("Invalid operator");
		}
	}

}
