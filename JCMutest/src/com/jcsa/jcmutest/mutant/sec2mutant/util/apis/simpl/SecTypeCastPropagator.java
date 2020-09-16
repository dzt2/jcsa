package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.simpl;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.*;

public class SecTypeCastPropagator extends SecComputationPropagator {

	@Override
	protected void set_expression_error(SecSetExpressionError error) throws Exception {
		CType cast_type = this.target_expression().get_data_type();
		SymExpression muta_expression = SymFactory.
				type_cast(cast_type, error.get_muta_expression());
		this.add_propagation_pair(this.condition_constraint(), 
				this.set_expression(muta_expression));
	}

	@Override
	protected void add_expression_error(SecAddExpressionError error) throws Exception {
		CType cast_type = this.target_expression().get_data_type();
		CType orig_type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; SymExpression muta_expression;
		switch(operator) {
		case arith_add:
		{
			muta_expression = SymFactory.arith_add(orig_type, loperand, roperand);
			break;
		}
		case arith_sub:
		{
			muta_expression = SymFactory.arith_sub(orig_type, loperand, roperand);
			break;
		}
		case arith_mul:
		{
			muta_expression = SymFactory.arith_mul(orig_type, loperand, roperand);
			break;
		}
		case arith_div:
		{
			muta_expression = SymFactory.arith_div(orig_type, loperand, roperand);
			break;
		}
		case arith_mod:
		{
			muta_expression = SymFactory.arith_mod(orig_type, loperand, roperand);
			break;
		}
		case bit_and:
		{
			muta_expression = SymFactory.bitws_and(orig_type, loperand, roperand);
			break;
		}
		case bit_or:
		{
			muta_expression = SymFactory.bitws_ior(orig_type, loperand, roperand);
			break;
		}
		case bit_xor:
		{
			muta_expression = SymFactory.bitws_xor(orig_type, loperand, roperand);
			break;
		}
		case left_shift:
		{
			muta_expression = SymFactory.bitws_lsh(orig_type, loperand, roperand);
			break;
		}
		case righ_shift:
		{
			muta_expression = SymFactory.bitws_rsh(orig_type, loperand, roperand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.type_cast(cast_type, muta_expression);
		target_error = this.set_expression(muta_expression);
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void ins_expression_error(SecInsExpressionError error) throws Exception {
		CType cast_type = this.target_expression().get_data_type();
		CType orig_type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression roperand = error.get_orig_expression().get_expression();
		SymExpression loperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; SymExpression muta_expression;
		switch(operator) {
		case arith_sub:
		{
			muta_expression = SymFactory.arith_sub(orig_type, loperand, roperand);
			break;
		}
		case arith_div:
		{
			muta_expression = SymFactory.arith_div(orig_type, loperand, roperand);
			break;
		}
		case arith_mod:
		{
			muta_expression = SymFactory.arith_mod(orig_type, loperand, roperand);
			break;
		}
		case left_shift:
		{
			muta_expression = SymFactory.bitws_lsh(orig_type, loperand, roperand);
			break;
		}
		case righ_shift:
		{
			muta_expression = SymFactory.bitws_rsh(orig_type, loperand, roperand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		muta_expression = SymFactory.type_cast(cast_type, muta_expression);
		target_error = this.set_expression(muta_expression);
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void uny_expression_error(SecUnyExpressionError error) throws Exception {
		CType cast_type = this.target_expression().get_data_type();
		cast_type = CTypeAnalyzer.get_value_type(cast_type);
		COperator operator = error.get_operator().get_operator();
		SymExpression operand = error.get_orig_expression().get_expression();
		SecStateError target_error;
		if(CTypeAnalyzer.is_boolean(cast_type)) {
			switch(operator) {
			case logic_not:
				target_error = this.uny_expression(COperator.logic_not);
				break;
			case bit_not:
				target_error = this.set_expression(Boolean.TRUE); break;
			default: 
				target_error = null;
				break;
			}
		}
		else {
			SymExpression muta_expression;
			switch(operator) {
			case negative:
				muta_expression = SymFactory.arith_neg(operand.get_data_type(), operand);
				break;
			case bit_not:
				muta_expression = SymFactory.bitws_rsv(operand.get_data_type(), operand);
				break;
			case logic_not:
				muta_expression = SymFactory.logic_not(operand);
				break;
			default: throw new IllegalArgumentException(operator.toString());
			}
			muta_expression = SymFactory.type_cast(cast_type, muta_expression);
			target_error = this.set_expression(muta_expression);
		}
		if(target_error != null)
			this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected boolean test_target_location(CirNode location) throws Exception {
		return location instanceof CirCastExpression;
	}

}
