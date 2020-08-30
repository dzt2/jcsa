package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;

/**
 * ins_expr(expr, operator)
 * @author dzt2
 *
 */
public class SedInsExpressionError extends SedExpressionError {
	
	public SedOperator get_muta_operator() {
		return (SedOperator) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "ins_expr(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_muta_operator().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedInsExpressionError();
	}

}
