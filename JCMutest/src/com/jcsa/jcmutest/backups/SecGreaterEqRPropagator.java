package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecGreaterEqRPropagator extends SecExpressionPropagator {

	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		SymExpression loperand = this.get_loperand();
		SymExpression roperand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.smaller_eq(roperand, loperand);
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception {
		CType type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression loperand = this.get_loperand();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression roperand, muta_expression; SecStateError target_error;
		
		switch(operator) {
		case arith_add:
		{
			roperand = SymFactory.arith_add(type, ori_operand, add_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_sub:
		{
			roperand = SymFactory.arith_sub(type, ori_operand, add_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mul:
		{
			roperand = SymFactory.arith_mul(type, ori_operand, add_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_div:
		{
			roperand = SymFactory.arith_div(type, ori_operand, add_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mod:
		{
			roperand = SymFactory.arith_mod(type, ori_operand, add_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_and:
		{
			roperand = SymFactory.bitws_and(type, ori_operand, add_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_or:
		{
			roperand = SymFactory.bitws_ior(type, ori_operand, add_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_xor:
		{
			roperand = SymFactory.bitws_xor(type, ori_operand, add_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			roperand = SymFactory.bitws_lsh(type, ori_operand, add_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			roperand = SymFactory.bitws_rsh(type, ori_operand, add_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}

	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		CType type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression loperand = this.get_loperand();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression roperand, muta_expression; SecStateError target_error;
		
		switch(operator) {
		case arith_sub:
		{
			roperand = SymFactory.arith_sub(type, add_operand, ori_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_div:
		{
			roperand = SymFactory.arith_div(type, add_operand, ori_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mod:
		{
			roperand = SymFactory.arith_mod(type, add_operand, ori_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			roperand = SymFactory.bitws_lsh(type, add_operand, ori_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			roperand = SymFactory.bitws_rsh(type, add_operand, ori_operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}

	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception {
		CType type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression loperand = this.get_loperand();
		SymExpression operand = error.get_orig_expression().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression roperand, muta_expression; SecStateError target_error;
		
		switch(operator) {
		case negative:
		{
			roperand = SymFactory.arith_neg(type, operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_not:
		{
			roperand = SymFactory.bitws_rsv(type, operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case logic_not:
		{
			roperand = SymFactory.logic_not(operand);
			muta_expression = SymFactory.smaller_eq(roperand, loperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}

}
