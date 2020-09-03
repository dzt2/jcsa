package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * app_expr(e1, o, e2): e1 ==> e1 o e2
 * @author yukimula
 *
 */
public class SedAppExpressionError extends SedAbsExpressionError {

	/* definitions */
	private SedOperator app_operator;
	private SedExpression app_operand;
	protected SedAppExpressionError(CirStatement statement, 
			CirExpression orig_expression, COperator operator,
			SedExpression add_expression) throws Exception {
		super(statement, orig_expression);
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if(add_expression == null)
			throw new IllegalArgumentException("Invalid add_expression");
		else {
			this.app_operand = add_expression;
			this.app_operator = new SedOperator(operator);
		}
	}
	
	/**
	 * @return the operator appended in the tail of original expression
	 */
	public SedOperator get_app_operator() { return this.app_operator; }
	
	/**
	 * @return the operand appended in the tail of the binary operator
	 */
	public SedExpression get_app_operand() { return this.app_operand; }

	@Override
	protected String generate_content() throws Exception {
		return "app_expr(" + this.get_orig_expression().generate_code() +
				", " + this.app_operator.get_operator().toString() + ", "
				+ this.app_operand.generate_code() + ")";
	}
	
}
