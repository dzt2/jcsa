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

public class SecLogicNotPropagation extends SecUnaryPropagation {
	
	@Override
	protected void process_set_expression(CirStatement statement, CirExpression expression, SecSetExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.logic_not(muta_operand);
		SecConstraint constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		SecStateError target_error = SecFactory.set_expression(statement, expression, muta_expression);
		propagations.add(new SecInfectPair(constraint, target_error));
	}
	
	@Override
	protected void process_add_expression(CirStatement statement, CirExpression expression, SecAddExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType type = expression.get_data_type(); SymExpression muta_expression;
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		
		switch(operator) {
		case arith_add:	muta_expression = SymFactory.arith_add(type, loperand, roperand); break;
		case arith_sub:	muta_expression = SymFactory.arith_sub(type, loperand, roperand); break;
		case arith_mul:	muta_expression = SymFactory.arith_mul(type, loperand, roperand); break;
		case arith_div:	muta_expression = SymFactory.arith_div(type, loperand, roperand); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(type, loperand, roperand); break;
		case bit_and:	muta_expression = SymFactory.bitws_and(type, loperand, roperand); break;
		case bit_or:	muta_expression = SymFactory.bitws_ior(type, loperand, roperand); break;
		case bit_xor:	muta_expression = SymFactory.bitws_xor(type, loperand, roperand); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(type, loperand, roperand); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(type, loperand, roperand); break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.logic_not(muta_expression);
		
		propagations.add(new SecInfectPair(
				SecFactory.condition_constraint(statement, Boolean.TRUE, true),
				SecFactory.set_expression(statement, expression, muta_expression)));
	}
	
	@Override
	protected void process_ins_expression(CirStatement statement, CirExpression expression, SecInsExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType type = expression.get_data_type(); SymExpression muta_expression;
		SymExpression roperand = error.get_orig_expression().get_expression();
		SymExpression loperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		
		switch(operator) {
		case arith_sub:	muta_expression = SymFactory.arith_sub(type, loperand, roperand); break;
		case arith_div:	muta_expression = SymFactory.arith_div(type, loperand, roperand); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(type, loperand, roperand); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(type, loperand, roperand); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(type, loperand, roperand); break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.logic_not(muta_expression);
		
		propagations.add(new SecInfectPair(
				SecFactory.condition_constraint(statement, Boolean.TRUE, true),
				SecFactory.set_expression(statement, expression, muta_expression)));
	}
	
	@Override
	protected void process_uny_expression(CirStatement statement, CirExpression expression, SecUnyExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType type = expression.get_data_type(); SymExpression muta_expression;
		SymExpression operand = error.get_orig_expression().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecConstraint constraint = 
				SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		SecStateError target_error;
		switch(operator) {
		case negative:	
		{
			return;		/* not possible for propagation */
		}
		case bit_not:	
		{
			muta_expression = SymFactory.bitws_rsv(type, operand);
			muta_expression = SymFactory.logic_not(muta_expression);
			target_error = SecFactory.set_expression(statement, expression, muta_expression);
			break;
		}
		case logic_not:	
		{
			target_error = SecFactory.uny_expression(statement, expression, COperator.logic_not);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		propagations.add(new SecInfectPair(constraint, target_error));
	}
	
}
