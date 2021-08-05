package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VTRPMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}

	private String param_code(Object parameter) throws Exception {
		if(parameter instanceof Boolean) {
			if(((Boolean) parameter).booleanValue()) {
				return "1";
			}
			else {
				return "0";
			}
		}
		else if(parameter instanceof Long
				|| parameter instanceof Double) {
			return parameter.toString();
		}
		else if(parameter instanceof AstExpression) {
			return "(" + ((AstExpression) parameter).generate_code() + ")";
		}
		else {
			return parameter.toString();
		}
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstExpression expression = (AstExpression) location;
		switch(source.get_operator()) {
		case trap_on_pos:
		case trap_on_zro:
		case trap_on_neg:
		{
			return "(jcm_" + source.get_operator() + "((" + expression.generate_code() + ")))";
		}
		case trap_on_dif:
		{
			return "(jcm_trap_on_dif((" + expression.generate_code() + "), " + this.param_code(source.get_parameter()) + "))";
		}
		default: throw new IllegalArgumentException(source.toString());
		}
	}

}
