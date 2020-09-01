package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * ins_expr(expr, oprt)
 * @author yukimula
 *
 */
public class SedInsExpressionError extends SedExpressionError {

	public SedInsExpressionError(CirStatement location, 
			SedExpression orig_expression, COperator operator) {
		super(location, orig_expression);
		this.add_child(new SedOperator(operator));
	}
	
	/**
	 * @return -|~|!
	 */
	public SedOperator get_ins_operator() {
		return (SedOperator) this.get_child(2);
	}

	@Override
	public String generate_content() throws Exception {
		return "ins_expr(" + this.get_ins_operator().generate_code()
				+ ", " + this.get_orig_expression().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedInsExpressionError(
				this.get_location().get_cir_statement(),
				this.get_orig_expression(),
				this.get_ins_operator().get_operator());
	}

}
