package com.jcsa.jcmutest.mutant.cir2mutant.error;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * expr_error(statement, expression, original_value, mutation_value) in whcih
 * the original value hold by the expression is replaced by the mutatino value
 * @author yukimula
 *
 */
public class CirExpressionError extends CirStateError {
	
	protected CirExpressionError(CirStatement statement,
			CirExpression expression,
			SymExpression muta_expression) throws Exception {
		super(CirErrorType.expr_error, statement);
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(muta_expression == null)
			throw new IllegalArgumentException("Invalid muta_expression");
		else {
			this.expression = expression;
			this.muta_expression = muta_expression;
			this.orig_expression = SymFactory.parse(expression);
		}
	}
	
	/* definitions */
	/** the expression being mutated during executing the statement **/
	private CirExpression expression;
	/** it describes the value hold by the reference at the point **/
	private SymExpression orig_expression;
	/** the expression that replaces the value of the reference **/
	private SymExpression muta_expression;
	
	/* getters */
	/**
	 * @return the expression being mutated during executing the statement
	 */
	public CirExpression get_expression() { return this.expression; }
	/**
	 * @return the value hold by the reference at the point of the statement
	 */
	public SymExpression get_orig_expression() { return this.orig_expression; }
	/**
	 * @return the expression that replaces the value of the reference
	 */
	public SymExpression get_muta_expression() { return this.muta_expression; }
	@Override
	protected String generate_code() throws Exception {
		return this.expression.generate_code(true)
				+ ", " + this.muta_expression.generate_code();
	}
	
}
