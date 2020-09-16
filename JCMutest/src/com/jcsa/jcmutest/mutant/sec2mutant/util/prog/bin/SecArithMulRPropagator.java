package com.jcsa.jcmutest.mutant.sec2mutant.util.prog.bin;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.util.prog.SecExpressionPropagator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecArithMulRPropagator extends SecExpressionPropagator {

	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression loperand = this.get_loperand();
		SymExpression roperand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.arith_mul(type, loperand, roperand);
		SecConstraint constraint = this.condition_constraint(
				SymFactory.not_equals(loperand, Integer.valueOf(0)));
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(constraint, target_error);
	}

	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression loperand = this.get_loperand();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression muta_expression, roperand; SecStateError target_error;
		
		switch(operator) {
		case arith_add:
		{
			muta_expression = SymFactory.arith_mul(type, loperand, add_operand);
			target_error = this.add_expression(COperator.arith_add, muta_expression);
			break;
		}
		case arith_sub:
		{
			muta_expression = SymFactory.arith_mul(type, loperand, add_operand);
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
			roperand = SymFactory.arith_mod(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_and:
		{
			roperand = SymFactory.bitws_and(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_or:
		{
			roperand = SymFactory.bitws_ior(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_xor:
		{
			roperand = SymFactory.bitws_xor(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			roperand = SymFactory.bitws_lsh(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			roperand = SymFactory.bitws_rsh(type, ori_operand, add_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		SecConstraint constraint = this.condition_constraint(
				SymFactory.not_equals(loperand, Integer.valueOf(0)));
		this.append_propagation_pair(constraint, target_error);
	}

	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression loperand = this.get_loperand();
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression muta_expression, roperand; SecStateError target_error;
		
		switch(operator) {
		case arith_sub:
		{
			roperand = SymFactory.arith_sub(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_div:
		{
			roperand = SymFactory.arith_div(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mod:
		{
			roperand = SymFactory.arith_mod(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			roperand = SymFactory.bitws_lsh(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			roperand = SymFactory.bitws_rsh(type, add_operand, ori_operand);
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		SecConstraint constraint = this.condition_constraint(
				SymFactory.not_equals(loperand, Integer.valueOf(0)));
		this.append_propagation_pair(constraint, target_error);
	}

	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		COperator operator = error.get_operator().get_operator();
		SymExpression operand = error.get_orig_expression().get_expression();
		SymExpression loperand = this.get_loperand();
		SymExpression muta_expression, roperand; SecStateError target_error;
		
		switch(operator) {
		case negative:
		{
			target_error = this.uny_expression(COperator.negative);
			break;
		}
		case bit_not:
		{
			roperand = SymFactory.bitws_rsv(type, operand);
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
		
		SecConstraint constraint = this.condition_constraint(
				SymFactory.not_equals(loperand, Integer.valueOf(0)));
		this.append_propagation_pair(constraint, target_error);
	}

}
