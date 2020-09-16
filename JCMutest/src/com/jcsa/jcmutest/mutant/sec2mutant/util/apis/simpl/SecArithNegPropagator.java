package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.simpl;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirArithExpression;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecArithNegPropagator extends SecComputationPropagator {

	@Override
	protected void set_expression_error(SecSetExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.arith_neg(type, muta_operand);
		SecStateError target_error = this.set_expression(muta_expression);
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void add_expression_error(SecAddExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression(), operand;
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error;
		switch(operator) {
		case arith_add:
		{
			operand = SymFactory.arith_neg(type, roperand);
			target_error = this.add_expression(COperator.arith_add, operand);
			break;
		}
		case arith_sub:
		{
			operand = roperand;
			target_error = this.add_expression(COperator.arith_add, operand);
			break;
		}
		case arith_mul:
		{
			operand = roperand;
			target_error = this.add_expression(COperator.arith_mul, operand);
			break;
		}
		case arith_div:
		{
			operand = roperand;
			target_error = this.add_expression(COperator.arith_div, operand);
			break;
		}
		case arith_mod:
		{
			operand = SymFactory.arith_mod(loperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		case bit_and:
		{
			operand = SymFactory.bitws_and(loperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		case bit_or:
		{
			operand = SymFactory.bitws_ior(loperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		case bit_xor:
		{
			operand = SymFactory.bitws_xor(loperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		case left_shift:
		{
			operand = SymFactory.bitws_lsh(loperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		case righ_shift:
		{
			operand = SymFactory.bitws_lsh(loperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void ins_expression_error(SecInsExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression roperand = error.get_orig_expression().get_expression();
		SymExpression loperand = error.get_operand().get_expression(), operand;
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error;
		switch(operator) {
		case arith_sub:	
		{
			operand = SymFactory.arith_add(roperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		case arith_div:	
		{
			operand = SymFactory.arith_div(roperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		case arith_mod:	
		{
			operand = SymFactory.arith_mod(roperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		case left_shift:
		{
			operand = SymFactory.bitws_lsh(roperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		case righ_shift:
		{
			operand = SymFactory.bitws_rsh(roperand.get_data_type(), loperand, roperand);
			target_error = this.set_expression(SymFactory.arith_neg(type, operand));
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void uny_expression_error(SecUnyExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression operand = error.get_orig_expression().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error;
		switch(operator) {
		case negative:
		{
			target_error = this.uny_expression(COperator.negative);
			break;
		}
		case bit_not:
		{
			operand = SymFactory.arith_add(type, operand, Integer.valueOf(1));
			target_error = this.set_expression(operand); 
			break;
		}
		case logic_not:
		{
			operand = SymFactory.logic_not(operand);
			operand = SymFactory.arith_neg(type, operand);
			target_error = this.set_expression(operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}
	
	@Override
	protected boolean test_target_location(CirNode location) throws Exception {
		if(location instanceof CirArithExpression) {
			return ((CirArithExpression) location).get_operator() == COperator.negative;
		}
		else {
			return false;
		}
	}

}
