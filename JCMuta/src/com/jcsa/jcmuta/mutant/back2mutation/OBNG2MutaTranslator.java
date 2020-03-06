package com.jcsa.jcmuta.mutant.back2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

import __backup__.TextMutation;

public class OBNG2MutaTranslator implements Text2MutaTranslator {

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		expression = CTypeAnalyzer.get_expression_of(expression);
		return AstMutation.UNOI(expression, COperator.bit_not);
	}

}
