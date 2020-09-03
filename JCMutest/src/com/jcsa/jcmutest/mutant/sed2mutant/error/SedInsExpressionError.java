package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * ins_expr(expr, oprt): expr ==> oprt(expr)
 * @author yukimula
 *
 */
public class SedInsExpressionError extends SedAbsExpressionError {

	/* definition */
	private SedOperator ins_operator;
	protected SedInsExpressionError(CirStatement statement, 
			CirExpression orig_expression, COperator operator) throws Exception {
		super(statement, orig_expression);
		this.ins_operator = new SedOperator(operator);
	}
	
	/**
	 * @return the unary operator inserted in the original expression
	 */
	public SedOperator get_ins_operator() {
		return this.ins_operator;
	}

	@Override
	protected String generate_content() throws Exception {
		return "ins_expr(" + this.get_orig_expression().generate_code() + 
				", " + this.ins_operator.get_operator().toString() + ")";
	}

}
