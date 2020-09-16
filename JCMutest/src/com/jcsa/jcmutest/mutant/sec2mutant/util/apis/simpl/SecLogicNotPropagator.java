package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.simpl;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirLogicExpression;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecLogicNotPropagator extends SecComputationPropagator {

	@Override
	protected void set_expression_error(SecSetExpressionError error) throws Exception {
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SecFactory.sym_condition(muta_operand, false);
		SecStateError target_error = this.set_expression(muta_expression);
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void add_expression_error(SecAddExpressionError error) throws Exception {
		CType type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; SymExpression muta_expression;
		switch(operator) {
		case arith_add:
		{
			muta_expression = SymFactory.arith_add(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_sub:
		{
			muta_expression = SymFactory.arith_sub(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_mul:
		{
			muta_expression = SymFactory.arith_mul(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_div:
		{
			muta_expression = SymFactory.arith_div(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_mod:
		{
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case bit_and:
		{
			muta_expression = SymFactory.bitws_and(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case bit_or:
		{
			muta_expression = SymFactory.bitws_ior(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case bit_xor:
		{
			muta_expression = SymFactory.bitws_xor(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case left_shift:
		{
			muta_expression = SymFactory.bitws_lsh(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case righ_shift:
		{
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void ins_expression_error(SecInsExpressionError error) throws Exception {
		CType type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression roperand = error.get_orig_expression().get_expression();
		SymExpression loperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; SymExpression muta_expression;
		switch(operator) {
		case arith_sub:
		{
			muta_expression = SymFactory.arith_sub(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_div:
		{
			muta_expression = SymFactory.arith_div(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_mod:
		{
			muta_expression = SymFactory.arith_mod(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case left_shift:
		{
			muta_expression = SymFactory.bitws_lsh(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		case righ_shift:
		{
			muta_expression = SymFactory.bitws_rsh(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_expression, false);
			target_error = this.set_expression(muta_expression); break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void uny_expression_error(SecUnyExpressionError error) throws Exception {
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error;
		switch(operator) {
		case negative:
		{
			target_error = null; break;
		}
		case bit_not:
		{
			target_error = this.set_expression(Boolean.TRUE); break;
		}
		case logic_not:
		{
			target_error = this.uny_expression(COperator.logic_not);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		if(target_error != null) {
			this.add_propagation_pair(this.condition_constraint(), target_error);
		}
	}

	@Override
	protected boolean test_target_location(CirNode location) throws Exception {
		if(location instanceof CirLogicExpression) {
			return ((CirLogicExpression) location).get_operator() == COperator.logic_not;
		}
		else {
			return false;
		}
	}

}
