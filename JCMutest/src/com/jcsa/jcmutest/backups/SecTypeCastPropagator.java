package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecTypeCastPropagator extends SecExpressionPropagator {
	
	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		CType cast_type = this.target_expression().get_data_type();
		SymExpression operand = error.get_orig_expression().get_expression();
		SymExpression muta_expression = SymFactory.type_cast(cast_type, operand);
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}
	
	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception {
		CType cast_type = this.target_expression().get_data_type();
		CType type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression muta_operand, muta_expression; SecStateError target_error;
		switch(operator) {
		case arith_add:
		{
			muta_operand = SymFactory.arith_add(type, loperand, roperand);
			break;
		}
		case arith_sub:
		{
			muta_operand = SymFactory.arith_sub(type, loperand, roperand);
			break;
		}
		case arith_mul:
		{
			muta_operand = SymFactory.arith_mul(type, loperand, roperand);
			break;
		}
		case arith_div:
		{
			muta_operand = SymFactory.arith_div(type, loperand, roperand);
			break;
		}
		case arith_mod:
		{
			muta_operand = SymFactory.arith_mod(type, loperand, roperand);
			break;
		}
		case bit_and:
		{
			muta_operand = SymFactory.bitws_and(type, loperand, roperand);
			break;
		}
		case bit_or:
		{
			muta_operand = SymFactory.bitws_ior(type, loperand, roperand);
			break;
		}
		case bit_xor:
		{
			muta_operand = SymFactory.bitws_xor(type, loperand, roperand);
			break;
		}
		case left_shift:
		{
			muta_operand = SymFactory.bitws_lsh(type, loperand, roperand);
			break;
		}
		case righ_shift:
		{
			muta_operand = SymFactory.bitws_rsh(type, loperand, roperand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.type_cast(cast_type, muta_operand);
		target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}
	
	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		CType cast_type = this.target_expression().get_data_type();
		CType type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression roperand = error.get_orig_expression().get_expression();
		SymExpression loperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression muta_operand, muta_expression; SecStateError target_error;
		switch(operator) {
		case arith_sub:
		{
			muta_operand = SymFactory.arith_sub(type, loperand, roperand);
			break;
		}
		case arith_div:
		{
			muta_operand = SymFactory.arith_div(type, loperand, roperand);
			break;
		}
		case arith_mod:
		{
			muta_operand = SymFactory.arith_mod(type, loperand, roperand);
			break;
		}
		case left_shift:
		{
			muta_operand = SymFactory.bitws_lsh(type, loperand, roperand);
			break;
		}
		case righ_shift:
		{
			muta_operand = SymFactory.bitws_rsh(type, loperand, roperand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.type_cast(cast_type, muta_operand);
		target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}
	
	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception {
		CType cast_type = this.target_expression().get_data_type();
		CType type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression operand = error.get_orig_expression().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression muta_operand, muta_expression; SecStateError target_error;
		switch(operator) {
		case negative:
		{
			muta_operand = SymFactory.arith_neg(type, operand);
			break;
		}
		case bit_not:
		{
			muta_operand = SymFactory.bitws_rsv(type, operand);
			break;
		}
		case logic_not:
		{
			muta_operand = SymFactory.logic_not(operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.type_cast(cast_type, muta_operand);
		target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}
	
}
