package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * set_expr(expr, expr)
 * @author yukimula
 *
 */
public class SedSetExpressionError extends SedExpressionError {

	public SedSetExpressionError(CirStatement location, 
			SedExpression orig_expression,
			SedExpression muta_expression) {
		super(location, orig_expression);
		this.add_child(muta_expression);
	}
	
	/**
	 * @return the expression to replace the original one
	 */
	public SedExpression get_muta_expression() {
		return (SedExpression) this.get_child(2);
	}

	@Override
	public String generate_content() throws Exception {
		return "set_expr(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_muta_expression().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedSetExpressionError(
				this.get_location().get_cir_statement(),
				this.get_orig_expression(),
				this.get_muta_expression());
	}
	
}
