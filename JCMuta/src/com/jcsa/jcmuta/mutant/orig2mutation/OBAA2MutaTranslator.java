package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

import __backup__.MutationMode;
import __backup__.TextMutation;

public class OBAA2MutaTranslator implements Text2MutaTranslator {
	
	private COperator get_operator(MutationMode mode) throws Exception {
		String replace = mode.toString();
		if(replace.endsWith("_ADD_A"))
			return COperator.arith_add_assign;
		else if(replace.endsWith("_SUB_A"))
			return COperator.arith_sub_assign;
		else if(replace.endsWith("_MUL_A"))
			return COperator.arith_mul_assign;
		else if(replace.endsWith("_DIV_A"))
			return COperator.arith_div_assign;
		else if(replace.endsWith("_MOD_A"))
			return COperator.arith_mod_assign;
		else throw new IllegalArgumentException("Invalid operator: " + mode);
	}

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		expression = CTypeAnalyzer.get_expression_of(expression);
		COperator operator = this.get_operator(mutation.get_mode());
		return AstMutation.OBAA((AstBinaryExpression) expression, operator);
	}

}
