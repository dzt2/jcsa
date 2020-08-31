package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;

public class SedAddExpressionError extends SedExpressionError {
	
	public SedOperator get_add_operator() {
		return (SedOperator) this.get_child(2);
	}
	
	public SedExpression get_add_operand() {
		return (SedExpression) this.get_child(3);
	}

	@Override
	protected SedNode clone_self() {
		return new SedAddExpressionError();
	}

	@Override
	public String generate_code() throws Exception {
		return "add_expr(" + this.get_orig_expression().generate_code() + 
				", " + this.get_add_operator().generate_code() + 
				", " + this.get_add_operand().generate_code() + ")";
	}
	
}
