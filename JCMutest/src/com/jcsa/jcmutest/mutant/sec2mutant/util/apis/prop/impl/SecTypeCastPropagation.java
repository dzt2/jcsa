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

public class SecTypeCastPropagation extends SecUnaryPropagation {
	
	@Override
	protected void process_set_expression(CirStatement statement, CirExpression expression, SecSetExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType cast_type = expression.get_data_type();
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.type_cast(cast_type, muta_operand);
		SecConstraint constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		SecStateError target_error = SecFactory.set_expression(statement, expression, muta_expression);
		propagations.add(new SecInfectPair(constraint, target_error));
	}
	
	@Override
	protected void process_add_expression(CirStatement statement, CirExpression expression, SecAddExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType cast_type = expression.get_data_type();
		COperator operator = error.get_operator().get_operator();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		CType orig_type = error.get_orig_expression().get_type().get_ctype();
		
		SymExpression muta_expression;
		switch(operator) {
		case arith_add:	muta_expression = SymFactory.arith_add(orig_type, loperand, roperand); break;
		case arith_sub:	muta_expression = SymFactory.arith_sub(orig_type, loperand, roperand); break;
		case arith_mul:	muta_expression = SymFactory.arith_mul(orig_type, loperand, roperand); break;
		case arith_div:	muta_expression = SymFactory.arith_div(orig_type, loperand, roperand); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(orig_type, loperand, roperand); break;
		case bit_and:	muta_expression = SymFactory.bitws_and(orig_type, loperand, roperand); break;
		case bit_or:	muta_expression = SymFactory.bitws_ior(orig_type, loperand, roperand); break;
		case bit_xor:	muta_expression = SymFactory.bitws_xor(orig_type, loperand, roperand); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(orig_type, loperand, roperand); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(orig_type, loperand, roperand); break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.type_cast(cast_type, muta_expression);
		
		propagations.add(new SecInfectPair(
				SecFactory.condition_constraint(statement, Boolean.TRUE, true),
				SecFactory.set_expression(statement, expression, muta_expression)));
	}
	
	@Override
	protected void process_ins_expression(CirStatement statement, CirExpression expression, SecInsExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType cast_type = expression.get_data_type();
		COperator operator = error.get_operator().get_operator();
		SymExpression roperand = error.get_orig_expression().get_expression();
		SymExpression loperand = error.get_operand().get_expression();
		CType orig_type = error.get_orig_expression().get_type().get_ctype();
		
		SymExpression muta_expression;
		switch(operator) {
		case arith_sub:	muta_expression = SymFactory.arith_sub(orig_type, loperand, roperand); break;
		case arith_div:	muta_expression = SymFactory.arith_div(orig_type, loperand, roperand); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(orig_type, loperand, roperand); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(orig_type, loperand, roperand); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(orig_type, loperand, roperand); break;
		default: 		throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.type_cast(cast_type, muta_expression);
		
		propagations.add(new SecInfectPair(
				SecFactory.condition_constraint(statement, Boolean.TRUE, true),
				SecFactory.set_expression(statement, expression, muta_expression)));
	}
	
	@Override
	protected void process_uny_expression(CirStatement statement, CirExpression expression, SecUnyExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType cast_type = expression.get_data_type();
		COperator operator = error.get_operator().get_operator();
		SymExpression operand = error.get_orig_expression().get_expression();
		CType orig_type = error.get_orig_expression().get_type().get_ctype();
		
		SymExpression muta_expression;
		switch(operator) {
		case negative:	muta_expression = SymFactory.arith_neg(orig_type, operand); break;
		case bit_not:	muta_expression = SymFactory.bitws_rsv(orig_type, operand); break;
		case logic_not:	muta_expression = SymFactory.logic_not(operand); 			break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.type_cast(cast_type, muta_expression);
		
		propagations.add(new SecInfectPair(
				SecFactory.condition_constraint(statement, Boolean.TRUE, true),
				SecFactory.set_expression(statement, expression, muta_expression)));
	}
	
}
