package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

import __backup__.MutationMode;
import __backup__.TextMutation;

public class OLLN2MutaTranslator implements Text2MutaTranslator {
	
	private COperator get_operator(MutationMode mode) throws Exception {
		String replace = mode.toString();
		if(replace.endsWith("_LAN"))
			return COperator.logic_and;
		else if(replace.endsWith("_LOR"))
			return COperator.logic_or;
		else throw new IllegalArgumentException("Invalid operator: " + mode);
	}

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		expression = CTypeAnalyzer.get_expression_of(expression);
		COperator operator = this.get_operator(mutation.get_mode());
		return AstMutation.OLLN((AstLogicBinaryExpression) expression, operator);
	}

}
