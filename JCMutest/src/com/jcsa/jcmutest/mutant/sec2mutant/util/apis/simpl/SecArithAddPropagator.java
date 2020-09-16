package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.simpl;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirArithExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecArithAddPropagator extends SecComputationPropagator {

	@Override
	protected void set_expression_error(SecSetExpressionError error) throws Exception {
		/* declarations */
		CirComputeExpression expression = this.target_binary_expression();
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		CType type = this.target_expression().get_data_type();
		
		SecStateError target_error; SymExpression muta_expression;
		if(this.is_left_operand()) {
			muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
		}
		else if(this.is_right_operand()) {
			muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
		}
		else {
			throw new IllegalArgumentException(error.generate_code());
		}
		target_error = this.set_expression(muta_expression);
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void add_expression_error(SecAddExpressionError error) throws Exception {
		/* declarations */
		CirComputeExpression expression = this.target_binary_expression();
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		SymExpression orig_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		CType type = this.target_expression().get_data_type();
		
		SecStateError target_error; SymExpression muta_operand, muta_expression;
		switch(operator) {
		case arith_add:	
		{
			target_error = this.add_expression(COperator.arith_add, add_operand);
			break;
		}
		case arith_sub:	
		{
			target_error = this.add_expression(COperator.arith_sub, add_operand);
			break;
		}
		case arith_mul:
		{
			muta_operand = SymFactory.arith_mul(type, orig_operand, add_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_div:	
		{
			muta_operand = SymFactory.arith_div(type, orig_operand, add_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_mod:	
		{
			muta_operand = SymFactory.arith_mod(type, orig_operand, add_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case bit_and:	
		{
			muta_operand = SymFactory.bitws_and(type, orig_operand, add_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case bit_or:	
		{
			muta_operand = SymFactory.bitws_ior(type, orig_operand, add_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case bit_xor:	
		{
			muta_operand = SymFactory.bitws_xor(type, orig_operand, add_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case left_shift:
		{
			muta_operand = SymFactory.bitws_lsh(type, orig_operand, add_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case righ_shift:
		{
			muta_operand = SymFactory.bitws_rsh(type, orig_operand, add_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void ins_expression_error(SecInsExpressionError error) throws Exception {
		/* declarations */
		CirComputeExpression expression = this.target_binary_expression();
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		SymExpression orig_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		CType type = this.target_expression().get_data_type();
		
		SecStateError target_error; SymExpression muta_operand, muta_expression;
		switch(operator) {
		case arith_sub:	
		{
			muta_operand = SymFactory.arith_sub(type, add_operand, orig_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_div:	
		{
			muta_operand = SymFactory.arith_div(type, add_operand, orig_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_mod:	
		{
			muta_operand = SymFactory.arith_mod(type, add_operand, orig_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case left_shift:
		{
			muta_operand = SymFactory.bitws_lsh(type, add_operand, orig_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		case righ_shift:
		{
			muta_operand = SymFactory.bitws_rsh(type, add_operand, orig_operand);
			if(loperand == orig_operand.get_cir_source()) {
				muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
			}
			else {
				muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
			}
			target_error = this.set_expression(muta_expression); break;
		}
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void uny_expression_error(SecUnyExpressionError error) throws Exception {
		CirComputeExpression expression = this.target_binary_expression();
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CType type = this.target_expression().get_data_type();
		
		SymExpression operand = error.get_orig_expression().get_expression();
		COperator operator = error.get_operator().get_operator();
		SymExpression muta_operand, muta_expression; SecStateError target_error;
		
		switch(operator) {
		case negative:
		{
			muta_operand = SymFactory.arith_neg(operand.get_data_type(), operand);
			break;
		}
		case bit_not:
		{
			muta_operand = SymFactory.bitws_rsv(operand.get_data_type(), operand);
			break;
		}
		case logic_not:
		{
			muta_operand = SymFactory.logic_not(operand);
			break;
		}
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		
		if(loperand == operand.get_cir_source()) {
			muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
		}
		else if(roperand == operand.get_cir_source()) {
			muta_expression = SymFactory.arith_add(type, loperand, muta_operand);
		}
		else {
			this.report_unsupported_operations();	return;
		}
		target_error = this.set_expression(muta_expression);
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected boolean test_target_location(CirNode location) throws Exception {
		if(location instanceof CirArithExpression) {
			return ((CirArithExpression) location).get_operator() == COperator.arith_add;
		}
		else {
			return false;
		}
	}

}
