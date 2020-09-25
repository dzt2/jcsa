package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecBitwsRshLPropagator extends SecExpressionPropagator {

	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = this.get_roperand();
		SymExpression loperand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
		SecStateError target_error = this.set_expression(muta_expression);
		SecConstraint constraint = this.condition_constraint(
				SymFactory.smaller_tn(roperand, Integer.valueOf(64)));
		this.append_propagation_pair(constraint, target_error);
	}

	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = this.get_roperand();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression loperand, muta_expression; SecStateError target_error;
		
		switch(operator) {
		case arith_add:
		{
			loperand = SymFactory.arith_add(type, ori_operand, add_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_sub:
		{
			loperand = SymFactory.arith_sub(type, ori_operand, add_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mul:
		{
			loperand = SymFactory.arith_mul(type, ori_operand, add_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_div:
		{
			loperand = SymFactory.arith_div(type, ori_operand, add_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mod:
		{
			loperand = SymFactory.arith_mod(type, ori_operand, add_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_and:
		{
			loperand = SymFactory.bitws_and(type, ori_operand, add_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_or:
		{
			loperand = SymFactory.bitws_ior(type, ori_operand, add_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_xor:
		{
			loperand = SymFactory.bitws_xor(type, ori_operand, add_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			loperand = SymFactory.bitws_lsh(type, ori_operand, add_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			target_error = this.add_expression(COperator.righ_shift, add_operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		SecConstraint constraint = this.condition_constraint(
				SymFactory.smaller_tn(roperand, Integer.valueOf(64)));
		this.append_propagation_pair(constraint, target_error);
	}

	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = this.get_roperand();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression loperand, muta_expression; SecStateError target_error;
		
		switch(operator) {
		case arith_sub:
		{
			loperand = SymFactory.arith_sub(type, add_operand, ori_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_div:
		{
			loperand = SymFactory.arith_div(type, add_operand, ori_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mod:
		{
			loperand = SymFactory.arith_mod(type, add_operand, ori_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			loperand = SymFactory.bitws_lsh(type, add_operand, ori_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			loperand = SymFactory.bitws_rsh(type, add_operand, ori_operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		SecConstraint constraint = this.condition_constraint(
				SymFactory.smaller_tn(roperand, Integer.valueOf(64)));
		this.append_propagation_pair(constraint, target_error);
	}

	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = this.get_roperand();
		SymExpression operand = error.get_orig_expression().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression loperand, muta_expression; SecStateError target_error;
		
		switch(operator) {
		case negative:
		{
			loperand = SymFactory.arith_neg(type, operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_not:
		{
			loperand = SymFactory.bitws_rsv(type, operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case logic_not:
		{
			loperand = SymFactory.logic_not(operand);
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		SecConstraint constraint = this.condition_constraint(
				SymFactory.smaller_tn(roperand, Integer.valueOf(64)));
		this.append_propagation_pair(constraint, target_error);
	}

}
