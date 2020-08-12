package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VINCMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstExpression operand = (AstExpression) location;
		if(source.get_operator() == MutaOperator.inc_constant) {
			int param = ((Integer) source.get_parameter()).intValue();
			return "((" + operand.generate_code() + ") + " + param + ")";
		}
		else {
			double param = ((Double) source.get_parameter()).doubleValue();
			return "((" + operand.generate_code() + ") * " + param + ")";
		}
	}

}
