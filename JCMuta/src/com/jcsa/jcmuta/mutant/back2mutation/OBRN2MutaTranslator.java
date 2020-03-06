package com.jcsa.jcmuta.mutant.back2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

import __backup__.MutationMode;
import __backup__.TextMutation;

public class OBRN2MutaTranslator implements Text2MutaTranslator {
	
	private COperator get_operator(MutationMode mode) throws Exception {
		String replace = mode.toString();
		if(replace.endsWith("_GRT"))
			return COperator.greater_tn;
		else if(replace.endsWith("_GRE"))
			return COperator.greater_eq;
		else if(replace.endsWith("_SMT"))
			return COperator.smaller_tn;
		else if(replace.endsWith("_SME"))
			return COperator.smaller_eq;
		else if(replace.endsWith("_EQV"))
			return COperator.equal_with;
		else if(replace.endsWith("_NEQ"))
			return COperator.not_equals;
		else throw new IllegalArgumentException("Invalid operator: " + mode);
	}

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		expression = CTypeAnalyzer.get_expression_of(expression);
		COperator operator = this.get_operator(mutation.get_mode());
		return AstMutation.OBRN((AstBinaryExpression) expression, operator);
	}

}
