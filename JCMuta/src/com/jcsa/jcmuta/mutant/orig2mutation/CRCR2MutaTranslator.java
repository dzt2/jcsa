package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

import __backup__.TextMutation;

public class CRCR2MutaTranslator implements Text2MutaTranslator {

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		expression = CTypeAnalyzer.get_expression_of(expression);
		
		CConstant parameter = new CConstant();
		switch(mutation.get_mode()) {
		case CST_TOT_ZRO:	parameter.set_int(0); break;
		case CST_POS_ONE:	parameter.set_int(1); break;
		case CST_NEG_ONE:	parameter.set_int(-1); break;
		case CST_NEG_CST:	return AstMutation.UNOI(expression, COperator.negative);
		case CST_INC_ONE:	return AstMutation.VINC(expression, 1);
		case CST_DEC_ONE:	return AstMutation.VINC(expression, -1);
		default: throw new IllegalArgumentException("Invalid operator");
		}
		return AstMutation.VCRP(expression, parameter);
	}

}
