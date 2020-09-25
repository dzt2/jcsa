package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecArithMulLPropagator extends SecExpressionPropagator {

	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = this.get_roperand();
		SymExpression loperand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.arith_mul(type, loperand, roperand);
		SecConstraint constraint = this.condition_constraint(
				SymFactory.not_equals(roperand, Integer.valueOf(0)));
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(constraint, target_error);
	}

	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = this.get_roperand();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression muta_expression, loperand; SecStateError target_error;
		SecConstraint constraint = this.condition_constraint(
				SymFactory.not_equals(roperand, Integer.valueOf(0)));
		
		switch(operator) {
		case arith_add:
		{
			muta_expression = SymFactory.arith_mul(type, add_operand, roperand);
			target_error = this.add_expression(COperator.arith_add, muta_expression);
			break;
		}
		case arith_sub:
		{
			muta_expression = SymFactory.arith_mul(type, add_operand, roperand);
			target_error = this.add_expression(COperator.arith_sub, muta_expression);
			break;
		}
		case arith_mul:
		{
			target_error = this.add_expression(COperator.arith_mul, add_operand);
			break;
		}
		case arith_div:
		{
			target_error = this.add_expression(COperator.arith_div, add_operand);
			break;
		}
		case arith_mod:
		{
			loperand = SymFactory.arith_mod(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_and:
		{
			loperand = SymFactory.bitws_and(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_or:
		{
			loperand = SymFactory.bitws_ior(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_xor:
		{
			loperand = SymFactory.bitws_xor(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			loperand = SymFactory.bitws_lsh(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			loperand = SymFactory.bitws_rsh(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.append_propagation_pair(constraint, target_error);
	}

	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = this.get_roperand();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression muta_expression, loperand; SecStateError target_error;
		SecConstraint constraint = this.condition_constraint(
				SymFactory.not_equals(roperand, Integer.valueOf(0)));
		
		switch(operator) {
		case arith_sub:
		{
			loperand = SymFactory.arith_sub(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_div:
		{
			loperand = SymFactory.arith_div(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mod:
		{
			loperand = SymFactory.arith_mod(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			loperand = SymFactory.bitws_lsh(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			loperand = SymFactory.bitws_rsh(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.append_propagation_pair(constraint, target_error);
	}

	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		COperator operator = error.get_operator().get_operator();
		SymExpression operand = error.get_orig_expression().get_expression();
		SymExpression roperand = this.get_roperand();
		SymExpression muta_expression, loperand; SecStateError target_error;
		SecConstraint constraint = this.condition_constraint(
				SymFactory.not_equals(roperand, Integer.valueOf(0)));
		
		switch(operator) {
		case negative:
		{
			target_error = this.uny_expression(COperator.negative);
			break;
		}
		case bit_not:
		{
			loperand = SymFactory.bitws_rsv(type, operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case logic_not:
		{
			target_error = this.uny_expression(COperator.logic_not);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.append_propagation_pair(constraint, target_error);
	}

}
