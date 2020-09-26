package com.jcsa.jcmutest.mutant.cir2mutant.model;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * <code>set_expr(expression, orig_val, muta_val)</code>: the value hold by that
 * specified expression is replaced as the muta_val, which shall be orig_val.<br>
 * 
 * @author yukimula
 *
 */
public class CirExpressionError extends CirStateError {
	
	/* definitions */
	/** the original value hold by the expression in testing **/
	private SymExpression orig_val;
	/** the mutation value that will replace the original one **/
	private SymExpression muta_val;
	/**
	 * @param expression the expression of which value will be replaced
	 * @param muta_val mutation value that will replace the original one
	 * @throws Exception
	 */
	protected CirExpressionError(CirExpression expression, 
			SymExpression muta_val) throws Exception {
		super(CirErrorType.expr_error, expression.statement_of());
		if(muta_val == null)
			throw new IllegalArgumentException("Invalid muta_value: null");
		else {
			this.orig_val = SymFactory.parse(expression);
			this.muta_val = muta_val;
		}
	}
	
	/* getters */
	/**
	 * @return the expression of which value will be mutated
	 */
	public CirExpression get_expression() { return this.orig_val.get_cir_source(); }
	/**
	 * @return the original value hold by the expression in testing
	 */
	public SymExpression get_original_value() { return this.orig_val; }
	/**
	 * @return the mutation value that will replace the original one
	 */
	public SymExpression get_mutation_value() { return this.muta_val; }

	@Override
	protected String generate_code() throws Exception {
		return this.orig_val.generate_code() + ", " + this.muta_val.generate_code();
	}
	
}
