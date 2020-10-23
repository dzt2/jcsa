package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorType;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;
import com.jcsa.jcparse.flwa.symbol.SymEvaluator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;


/**
 * <code>set_expr(expression, orig_val, muta_val)</code>: the value hold by that
 * specified expression is replaced as the muta_val, which shall be orig_val.<br>
 * 
 * @author yukimula
 *
 */
public class CirExpressionError extends CirStateError {
	
	/* definitions */
	/** the expression in which the state error will occur **/
	private CirExpression expression;
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
			SymExpression orig_val, SymExpression muta_val) throws Exception {
		super(CirErrorType.expr_error, expression.statement_of());
		if(muta_val == null)
			throw new IllegalArgumentException("Invalid muta_value: null");
		else {
			this.expression = expression;
			this.orig_val = orig_val;
			this.muta_val = muta_val;
		}
	}
	
	/* getters */
	/**
	 * @return the expression of which value will be mutated
	 */
	public CirExpression get_expression() { return this.expression; }
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
		return this.expression.generate_code(false) + ", " + 
				this.orig_val.generate_code() + ", " + 
				this.muta_val.generate_code();
	}

	@Override
	public Boolean validate(CStateContexts contexts) throws Exception {
		SymExpression orig_value = SymEvaluator.evaluate_on(orig_val, contexts);
		SymExpression muta_value = SymEvaluator.evaluate_on(muta_val, contexts);
		if(orig_value.equals(muta_value))
			return Boolean.FALSE;
		else if(orig_value instanceof SymConstant
				&& muta_value instanceof SymConstant) {
			return Boolean.TRUE;
		}
		else {
			return null;	/* undecidable */
		}
	}
	
}
