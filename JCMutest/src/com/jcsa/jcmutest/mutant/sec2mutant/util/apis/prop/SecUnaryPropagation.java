package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.prop;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The propagation from operand to the unary-expression as:<br>
 * {negative, bit_not, logic_not, address_of, dereference, assign, wait}<br>
 * and only SecExpressionError in operand is allowed.
 * 
 * @author yukimula
 *
 */
public abstract class SecUnaryPropagation implements SecPropagation {

	@Override
	public void propagate(CirStatement statement, CirNode location, SecStateError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CirExpression expression = (CirExpression) location;
		if(error instanceof SecExpressionError) {
			CirExpression operand = ((SecExpressionError) error).
					get_orig_expression().get_expression().get_cir_source();
			if(operand.get_parent() == expression) {
				if(error instanceof SecSetExpressionError) {
					this.process_set_expression(statement, expression, (SecSetExpressionError) error, propagations);
				}
				else if(error instanceof SecAddExpressionError) {
					this.process_add_expression(statement, expression, (SecAddExpressionError) error, propagations);
				}
				else if(error instanceof SecInsExpressionError) {
					this.process_ins_expression(statement, expression, (SecInsExpressionError) error, propagations);
				}
				else if(error instanceof SecUnyExpressionError) {
					this.process_uny_expression(statement, expression, (SecUnyExpressionError) error, propagations);
				}
				else {
					throw new IllegalArgumentException(error.generate_code());
				}
			}
		}
	}
	
	/**
	 * @param statement 
	 * @param expression
	 * @param error set_expr(operand, operand')
	 * @param propagations
	 * @throws Exception
	 */
	protected abstract void process_set_expression(CirStatement statement,
			CirExpression expression, SecSetExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
	/**
	 * @param statement
	 * @param expression
	 * @param error add_expr(operand, oprt, operand')
	 * @param propagations
	 * @throws Exception
	 */
	protected abstract void process_add_expression(CirStatement statement,
			CirExpression expression, SecAddExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
	/**
	 * @param statement
	 * @param expression
	 * @param error ins_expr(operand, oprt, operand')
	 * @param propagations
	 * @throws Exception
	 */
	protected abstract void process_ins_expression(CirStatement statement,
			CirExpression expression, SecInsExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
	/**
	 * @param statement
	 * @param expression
	 * @param error uny_expr(operand, oprt, operand')
	 * @param propagations
	 * @throws Exception
	 */
	protected abstract void process_uny_expression(CirStatement statement,
			CirExpression expression, SecUnyExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
}
