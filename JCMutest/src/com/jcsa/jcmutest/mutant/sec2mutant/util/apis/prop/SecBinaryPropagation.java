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
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SecBinaryPropagation implements SecPropagation {

	@Override
	public void propagate(CirStatement statement, CirNode location, SecStateError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) location;
		if(error instanceof SecExpressionError) {
			CirExpression operand = ((SecExpressionError) error).
					get_orig_expression().get_expression().get_cir_source();
			if(expression.get_operand(0) == operand) {
				if(error instanceof SecSetExpressionError) {
					this.lprocess_set_expression(statement, expression, (SecSetExpressionError) error, propagations);
				}
				else if(error instanceof SecAddExpressionError) {
					this.lprocess_add_expression(statement, expression, (SecAddExpressionError) error, propagations);
				}
				else if(error instanceof SecInsExpressionError) {
					this.lprocess_ins_expression(statement, expression, (SecInsExpressionError) error, propagations);
				}
				else if(error instanceof SecUnyExpressionError) {
					this.lprocess_uny_expression(statement, expression, (SecUnyExpressionError) error, propagations);
				}
				else {
					throw new IllegalArgumentException(error.generate_code());
				}
			}
			else if(expression.get_operand(1) == operand) {
				if(error instanceof SecSetExpressionError) {
					this.rprocess_set_expression(statement, expression, (SecSetExpressionError) error, propagations);
				}
				else if(error instanceof SecAddExpressionError) {
					this.rprocess_add_expression(statement, expression, (SecAddExpressionError) error, propagations);
				}
				else if(error instanceof SecInsExpressionError) {
					this.rprocess_ins_expression(statement, expression, (SecInsExpressionError) error, propagations);
				}
				else if(error instanceof SecUnyExpressionError) {
					this.rprocess_uny_expression(statement, expression, (SecUnyExpressionError) error, propagations);
				}
				else {
					throw new IllegalArgumentException(error.generate_code());
				}
			}
			else {
				throw new IllegalArgumentException(expression.generate_code(true));
			}
 		}
	}
	
	
	protected abstract void lprocess_set_expression(CirStatement statement,
			CirComputeExpression expression, SecSetExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
	protected abstract void rprocess_set_expression(CirStatement statement,
			CirComputeExpression expression, SecSetExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
	protected abstract void lprocess_add_expression(CirStatement statement,
			CirComputeExpression expression, SecAddExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
	protected abstract void rprocess_add_expression(CirStatement statement,
			CirComputeExpression expression, SecAddExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
	protected abstract void lprocess_ins_expression(CirStatement statement,
			CirComputeExpression expression, SecInsExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
	protected abstract void rprocess_ins_expression(CirStatement statement,
			CirComputeExpression expression, SecInsExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
	protected abstract void lprocess_uny_expression(CirStatement statement,
			CirComputeExpression expression, SecUnyExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
	protected abstract void rprocess_uny_expression(CirStatement statement,
			CirComputeExpression expression, SecUnyExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
}
