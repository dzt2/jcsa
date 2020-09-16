package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.impl;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;


public class SecArithNegPropagator extends SecExpressionPropagator {

	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.arith_neg(type, muta_operand);
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		
		SecStateError target_error; SymExpression muta_operand, muta_expression;
		switch(operator) {
		case arith_add:
		{
			target_error = this.add_expression(COperator.arith_sub, roperand);
			break;
		}
		case arith_sub:
		{
			target_error = this.add_expression(COperator.arith_add, roperand);
			break;
		}
		case arith_mul:
		{
			target_error = this.add_expression(COperator.arith_mul, roperand);
			break;
		}
		case arith_div:
		{
			target_error = this.add_expression(COperator.arith_div, roperand);
			break;
		}
		case arith_mod:
		{
			muta_operand = SymFactory.arith_mod(type, loperand, roperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			target_error = this.set_expression(muta_expression); break;
		}
		case bit_and:
		{
			muta_operand = SymFactory.bitws_and(type, loperand, roperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			target_error = this.set_expression(muta_expression); break;
		}
		case bit_or:
		{
			muta_operand = SymFactory.bitws_ior(type, loperand, roperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			target_error = this.set_expression(muta_expression); break;
		}
		case bit_xor:
		{
			muta_operand = SymFactory.bitws_xor(type, loperand, roperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			target_error = this.set_expression(muta_expression); break;
		}
		case left_shift:
		{
			muta_operand = SymFactory.bitws_lsh(type, loperand, roperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			target_error = this.set_expression(muta_expression); break;
		}
		case righ_shift:
		{
			muta_operand = SymFactory.bitws_rsh(type, loperand, roperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			target_error = this.set_expression(muta_expression); break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		
		SecStateError target_error; SymExpression muta_operand, muta_expression;
		switch(operator) {
		case arith_sub:
		{
			muta_operand = SymFactory.arith_sub(type, roperand, loperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			break;
		}
		case arith_div:
		{
			muta_operand = SymFactory.arith_div(type, roperand, loperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			break;
		}
		case arith_mod:
		{
			muta_operand = SymFactory.arith_mod(type, roperand, loperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			break;
		}
		case left_shift:
		{
			muta_operand = SymFactory.bitws_lsh(type, roperand, loperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			break;
		}
		case righ_shift:
		{
			muta_operand = SymFactory.bitws_rsh(type, roperand, loperand);
			muta_expression = SymFactory.arith_neg(type, muta_operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception {
		/* declarations */
		CType type = this.target_expression().get_data_type();
		SymExpression operand = error.get_orig_expression().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; SymExpression muta_expression;
		
		switch(operator) {
		case negative:
		{
			target_error = this.uny_expression(COperator.negative);
			break;
		}
		case bit_not:
		{
			muta_expression = SymFactory.arith_add(type, operand, Integer.valueOf(1));
			target_error = this.set_expression(muta_expression);
			break;
		}
		case logic_not:
		{
			muta_expression = SymFactory.logic_not(operand);
			muta_expression = SymFactory.arith_neg(type, muta_expression);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}

}
