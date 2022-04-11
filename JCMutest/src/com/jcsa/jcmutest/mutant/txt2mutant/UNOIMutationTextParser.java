package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UNOIMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstExpression expression = (AstExpression) location;
		switch(source.get_operator()) {
		case insert_arith_neg:
			return "(-(" + expression.generate_code() + "))";
		case insert_bitws_rsv:
			return "(~(" + expression.generate_code() + "))";
		case insert_logic_not:
			return "(!(" + expression.generate_code() + "))";
		case insert_abs_value:
			return "(jcm_insert_abs_value((" + expression.generate_code() + ")))";
		case insert_nabs_value:
			return "(jcm_insert_nabs_value((" + expression.generate_code() + ")))";
		default: throw new IllegalArgumentException(source.toString());
		}
	}

}
