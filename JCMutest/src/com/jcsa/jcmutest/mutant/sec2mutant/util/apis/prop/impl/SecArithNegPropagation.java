package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.prop.impl;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectPair;
import com.jcsa.jcmutest.mutant.sec2mutant.util.apis.prop.SecUnaryPropagation;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecArithNegPropagation extends SecUnaryPropagation {
	
	@Override
	protected void process_set_expression(CirStatement statement, CirExpression expression, SecSetExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.arith_neg(expression.get_data_type(), muta_operand);
		SecStateError target_error = SecFactory.set_expression(statement, expression, muta_expression);
		SecConstraint constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		propagations.add(new SecInfectPair(constraint, target_error));
	}
	
	@Override
	protected void process_add_expression(CirStatement statement, CirExpression expression, SecAddExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		SecConstraint constraint; SecStateError target_error;
		COperator operator = error.get_operator().get_operator();
		switch(operator) {
		case arith_add:
		{
			SymExpression operand = error.get_operand().get_expression();
			operand = SymFactory.arith_neg(expression.get_data_type(), operand);
			constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
			target_error = SecFactory.add_expression(statement, expression, COperator.arith_add, operand);
			break;
		}
		case arith_sub:
		{
			SymExpression operand = error.get_operand().get_expression();
			constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
			target_error = SecFactory.add_expression(statement, expression, COperator.arith_add, operand);
			break;
		}
		case arith_mul:
		case arith_div:
		{
			SymExpression operand = error.get_operand().get_expression();
			constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
			target_error = SecFactory.add_expression(statement, expression, operator, operand);
			break;
		}
		case arith_mod:
		{
			SymExpression loperand = error.get_orig_expression().get_expression();
			SymExpression roperand = error.get_operand().get_expression();
			SymExpression muta_expression = SymFactory.arith_mod(expression.get_data_type(), loperand, roperand);
			muta_expression = SymFactory.arith_neg(expression.get_data_type(), muta_expression);
			target_error = SecFactory.set_expression(statement, expression, muta_expression);
			this.process_set_expression(statement, expression, (SecSetExpressionError) target_error, propagations);
			return;
		}
		case bit_and:
		{
			SymExpression loperand = error.get_orig_expression().get_expression();
			SymExpression roperand = error.get_operand().get_expression();
			SymExpression muta_expression = SymFactory.bitws_and(expression.get_data_type(), loperand, roperand);
			muta_expression = SymFactory.arith_neg(expression.get_data_type(), muta_expression);
			target_error = SecFactory.set_expression(statement, expression, muta_expression);
			this.process_set_expression(statement, expression, (SecSetExpressionError) target_error, propagations);
			return;
		}
		case bit_or:
		{
			SymExpression loperand = error.get_orig_expression().get_expression();
			SymExpression roperand = error.get_operand().get_expression();
			SymExpression muta_expression = SymFactory.bitws_ior(expression.get_data_type(), loperand, roperand);
			muta_expression = SymFactory.arith_neg(expression.get_data_type(), muta_expression);
			target_error = SecFactory.set_expression(statement, expression, muta_expression);
			this.process_set_expression(statement, expression, (SecSetExpressionError) target_error, propagations);
			return;
		}
		case bit_xor:
		{
			SymExpression loperand = error.get_orig_expression().get_expression();
			SymExpression roperand = error.get_operand().get_expression();
			SymExpression muta_expression = SymFactory.bitws_xor(expression.get_data_type(), loperand, roperand);
			muta_expression = SymFactory.arith_neg(expression.get_data_type(), muta_expression);
			target_error = SecFactory.set_expression(statement, expression, muta_expression);
			this.process_set_expression(statement, expression, (SecSetExpressionError) target_error, propagations);
			return;
		}
		case left_shift:
		{
			SymExpression loperand = error.get_orig_expression().get_expression();
			SymExpression roperand = error.get_operand().get_expression();
			SymExpression muta_expression = SymFactory.bitws_lsh(expression.get_data_type(), loperand, roperand);
			muta_expression = SymFactory.arith_neg(expression.get_data_type(), muta_expression);
			target_error = SecFactory.set_expression(statement, expression, muta_expression);
			this.process_set_expression(statement, expression, (SecSetExpressionError) target_error, propagations);
			return;
		}
		case righ_shift:
		{
			SymExpression loperand = error.get_orig_expression().get_expression();
			SymExpression roperand = error.get_operand().get_expression();
			SymExpression muta_expression = SymFactory.bitws_rsh(expression.get_data_type(), loperand, roperand);
			muta_expression = SymFactory.arith_neg(expression.get_data_type(), muta_expression);
			target_error = SecFactory.set_expression(statement, expression, muta_expression);
			this.process_set_expression(statement, expression, (SecSetExpressionError) target_error, propagations);
			return;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		propagations.add(new SecInfectPair(constraint, target_error));
	}
	
	@Override
	protected void process_ins_expression(CirStatement statement, CirExpression expression, SecInsExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		SecStateError target_error;
		COperator operator = error.get_operator().get_operator();
		SymExpression loperand = error.get_operand().get_expression();
		SymExpression roperand = error.get_orig_expression().get_expression();
		
		SymExpression muta_expression; CType type = expression.get_data_type();
		switch(operator) {
		case arith_sub:
		{
			muta_expression = SymFactory.arith_sub(type, loperand, roperand); 
			break;
		}
		case arith_div:
		{
			muta_expression = SymFactory.arith_div(type, loperand, roperand); 
			break;
		}
		case arith_mod:
		{
			muta_expression = SymFactory.arith_mod(type, loperand, roperand); 
			break;
		}
		case left_shift:
		{
			muta_expression = SymFactory.bitws_lsh(type, loperand, roperand); 
			break;
		}
		case righ_shift:
		{
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand); 
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.arith_neg(type, muta_expression);
		
		target_error = SecFactory.set_expression(statement, expression, muta_expression);
		this.process_set_expression(statement, expression, (SecSetExpressionError) target_error, propagations);
	}
	
	@Override
	protected void process_uny_expression(CirStatement statement, CirExpression expression, SecUnyExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		COperator operator = error.get_operator().get_operator();
		SecConstraint constraint; SecStateError target_error;
		SymExpression orig_expression, muta_expression;
		CType type = expression.get_data_type();
		
		switch(operator) {
		case negative:
		{
			constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
			target_error = SecFactory.uny_expression(statement, expression, COperator.negative);
			propagations.add(new SecInfectPair(constraint, target_error)); break;
		}
		case bit_not:
		{
			constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
			orig_expression = error.get_orig_expression().get_expression();
			muta_expression = SymFactory.arith_add(type, orig_expression, Integer.valueOf(1));
			target_error = SecFactory.set_expression(statement, expression, muta_expression);
			this.process_set_expression(statement, expression, (SecSetExpressionError) target_error, propagations);
			break;
		}
		case logic_not:
		{
			constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
			orig_expression = error.get_orig_expression().get_expression();
			muta_expression = SymFactory.logic_not(orig_expression);
			muta_expression = SymFactory.arith_neg(type, muta_expression);
			target_error = SecFactory.set_expression(statement, expression, muta_expression);
			this.process_set_expression(statement, expression, (SecSetExpressionError) target_error, propagations);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
	}
	
}
