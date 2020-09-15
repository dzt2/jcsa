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
		CType type = expression.get_data_type();
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.arith_neg(type, muta_operand);
		propagations.add(new SecInfectPair(
				SecFactory.condition_constraint(statement, Boolean.TRUE, true),
				SecFactory.set_expression(statement, expression, muta_expression)));
	}
	
	@Override
	protected void process_add_expression(CirStatement statement, CirExpression expression, SecAddExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType type = expression.get_data_type(); SymExpression muta_operand;
		COperator operator = error.get_operator().get_operator();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		SecConstraint constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		SecStateError target_error;
		switch(operator) {
		case arith_add:
		{
			add_operand = SymFactory.arith_neg(type, add_operand);
			target_error = SecFactory.add_expression(statement, expression, COperator.arith_add, add_operand);
			break;
		}
		case arith_sub:
		{
			target_error = SecFactory.add_expression(statement, expression, COperator.arith_add, add_operand);
			break;
		}
		case arith_mul:
		case arith_div:
		{
			target_error = SecFactory.add_expression(statement, expression, operator, add_operand);
			break;
		}
		case arith_mod:
		{
			muta_operand = SymFactory.arith_mod(type, ori_operand, add_operand);
			muta_operand = SymFactory.arith_neg(type, muta_operand);
			target_error = SecFactory.set_expression(statement, expression, muta_operand);
			break;
		}
		case bit_and:
		{
			muta_operand = SymFactory.bitws_and(type, ori_operand, add_operand);
			muta_operand = SymFactory.arith_neg(type, muta_operand);
			target_error = SecFactory.set_expression(statement, expression, muta_operand);
			break;
		}
		case bit_or:
		{
			muta_operand = SymFactory.bitws_ior(type, ori_operand, add_operand);
			muta_operand = SymFactory.arith_neg(type, muta_operand);
			target_error = SecFactory.set_expression(statement, expression, muta_operand);
			break;
		}
		case bit_xor:
		{
			muta_operand = SymFactory.bitws_xor(type, ori_operand, add_operand);
			muta_operand = SymFactory.arith_neg(type, muta_operand);
			target_error = SecFactory.set_expression(statement, expression, muta_operand);
			break;
		}
		case left_shift:
		{
			muta_operand = SymFactory.bitws_lsh(type, ori_operand, add_operand);
			muta_operand = SymFactory.arith_neg(type, muta_operand);
			target_error = SecFactory.set_expression(statement, expression, muta_operand);
			break;
		}
		case righ_shift:
		{
			muta_operand = SymFactory.bitws_rsh(type, ori_operand, add_operand);
			muta_operand = SymFactory.arith_neg(type, muta_operand);
			target_error = SecFactory.set_expression(statement, expression, muta_operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		propagations.add(new SecInfectPair(constraint, target_error));
	}
	
	@Override
	protected void process_ins_expression(CirStatement statement, CirExpression expression, SecInsExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType type = expression.get_data_type(); SymExpression muta_operand;
		COperator operator = error.get_operator().get_operator();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression ins_operand = error.get_operand().get_expression();
		SecConstraint constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		switch(operator) {
		case arith_sub:
		{
			muta_operand = SymFactory.arith_sub(type, ins_operand, ori_operand);
			break;
		}
		case arith_div:
		{
			muta_operand = SymFactory.arith_div(type, ins_operand, ori_operand);
			break;
		}
		case arith_mod:
		{
			muta_operand = SymFactory.arith_mod(type, ins_operand, ori_operand);
			break;
		}
		case left_shift:
		{
			muta_operand = SymFactory.bitws_lsh(type, ins_operand, ori_operand);
			break;
		}
		case righ_shift:
		{
			muta_operand = SymFactory.bitws_rsh(type, ins_operand, ori_operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		SecStateError target_error = SecFactory.set_expression(statement, expression, muta_operand);
		
		propagations.add(new SecInfectPair(constraint, target_error));
	}
	
	@Override
	protected void process_uny_expression(CirStatement statement, CirExpression expression, SecUnyExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType type = expression.get_data_type(); SymExpression muta_operand;
		COperator operator = error.get_operator().get_operator();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SecConstraint constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		SecStateError target_error;
		switch(operator) {
		case negative:
		{
			target_error = SecFactory.uny_expression(statement, expression, COperator.negative);
			break;
		}
		case bit_not:
		{
			muta_operand = SymFactory.arith_add(type, ori_operand, Integer.valueOf(1));
			target_error = SecFactory.set_expression(statement, expression, muta_operand);
			break;
		}
		case logic_not:
		{
			muta_operand = SymFactory.logic_not(ori_operand);
			muta_operand = SymFactory.arith_neg(type, muta_operand);
			target_error = SecFactory.set_expression(statement, expression, muta_operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		propagations.add(new SecInfectPair(constraint, target_error));
	}
	
}
