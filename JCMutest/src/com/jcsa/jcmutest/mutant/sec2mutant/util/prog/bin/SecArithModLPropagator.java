package com.jcsa.jcmutest.mutant.sec2mutant.util.prog.bin;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.util.prog.SecExpressionPropagator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecArithModLPropagator extends SecExpressionPropagator {

	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = this.get_roperand();
		SymExpression loperand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.arith_mod(type, loperand, roperand);
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.condition_constraint(SymFactory.
				not_equals(roperand, Integer.valueOf(1))), target_error);
	}

	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = this.get_roperand();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; SymExpression loperand, muta_expression;
		
		switch(operator) {
		case arith_add:
		{
			muta_expression = SymFactory.arith_mod(type, add_operand, roperand);
			target_error = this.add_expression(COperator.arith_add, add_operand);
			break;
		}
		case arith_sub:
		{
			add_operand = SymFactory.arith_neg(type, add_operand);
			muta_expression = SymFactory.arith_mod(type, add_operand, roperand);
			target_error = this.add_expression(COperator.arith_add, add_operand);
			break;
		}
		case arith_mul:
		{
			loperand = SymFactory.arith_mul(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_div:
		{
			loperand = SymFactory.arith_div(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mod:
		{
			loperand = SymFactory.arith_mod(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_and:
		{
			loperand = SymFactory.bitws_and(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_or:
		{
			loperand = SymFactory.bitws_ior(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_xor:
		{
			loperand = SymFactory.bitws_xor(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			loperand = SymFactory.bitws_lsh(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			loperand = SymFactory.bitws_rsh(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.append_propagation_pair(this.condition_constraint(SymFactory.
				not_equals(roperand, Integer.valueOf(1))), target_error);
	}

	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = this.get_roperand();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; SymExpression loperand, muta_expression;
		
		switch(operator) {
		case arith_sub:
		{
			loperand = SymFactory.arith_sub(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_div:
		{
			loperand = SymFactory.arith_div(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mod:
		{
			loperand = SymFactory.arith_mod(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			loperand = SymFactory.bitws_lsh(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			loperand = SymFactory.bitws_rsh(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.append_propagation_pair(this.condition_constraint(SymFactory.
				not_equals(roperand, Integer.valueOf(1))), target_error);
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
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_not:
		{
			loperand = SymFactory.bitws_rsv(type, operand);
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
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
		
		this.append_propagation_pair(this.condition_constraint(SymFactory.
				not_equals(roperand, Integer.valueOf(1))), target_error);
	}

}
