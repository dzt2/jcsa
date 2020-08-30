package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;

/**
 * add_expr(expr, operator, expr)
 * @author yukimula
 *
 */
public class SedAddExpressionError extends SedExpressionError {
	
	public SedOperator get_muta_operator() {
		return (SedOperator) this.get_child(2);
	}
	public SedExpression get_muta_operand() {
		return (SedExpression) this.get_child(3);
	}
	
	@Override
	protected String generate_content() throws Exception {
		return "add_expr(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_muta_operator().generate_code() + ", "
				+ this.get_muta_operand().generate_code() + ")";
	}
	
	@Override
	protected SedNode clone_self() {
		return new SedAddExpressionError();
	}
	
}
