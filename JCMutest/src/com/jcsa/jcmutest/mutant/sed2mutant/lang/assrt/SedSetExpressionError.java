package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;

/**
 * set_expr(expr, expr)
 * @author dzt2
 *
 */
public class SedSetExpressionError extends SedExpressionError {
	
	/**
	 * @return the expression to replace the original one
	 */
	public SedExpression get_muta_expression() {
		return (SedExpression) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "set_expr(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_muta_expression().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
