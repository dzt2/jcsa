package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

import __backup__.MutationMode;
import __backup__.TextMutation;

public class OEBA2MutaTranslator implements Text2MutaTranslator {
	
	private COperator get_operator(MutationMode mode) throws Exception {
		String replace = mode.toString();
		if(replace.endsWith("_BAN"))
			return COperator.bit_and_assign;
		else if(replace.endsWith("_BOR"))
			return COperator.bit_or_assign;
		else if(replace.endsWith("_BXR"))
			return COperator.bit_xor_assign;
		else if(replace.endsWith("_LSH"))
			return COperator.left_shift_assign;
		else if(replace.endsWith("_RSH"))
			return COperator.righ_shift_assign;
		else throw new IllegalArgumentException("Invalid operator: " + mode);
	}

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		expression = CTypeAnalyzer.get_expression_of(expression);
		COperator operator = this.get_operator(mutation.get_mode());
		return AstMutation.OEBA((AstAssignExpression) expression, operator);
	}

}
