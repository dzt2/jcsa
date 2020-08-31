package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;

/**
 * ins_expr(expr, -|~|!)
 * @author yukimula
 *
 */
public class SedInsExpressionError extends SedExpressionError {
	
	/**
	 * @return unary operator to be inserted
	 */
	public SedOperator get_ins_operator() {
		return (SedOperator) this.get_child(2);
	}

	@Override
	protected SedNode clone_self() {
		return new SedInsExpressionError();
	}

	@Override
	public String generate_code() throws Exception {
		return "ins_expr(" + this.get_orig_expression().generate_code() + 
					", " + this.get_ins_operator().generate_code() + ")";
	}

}
