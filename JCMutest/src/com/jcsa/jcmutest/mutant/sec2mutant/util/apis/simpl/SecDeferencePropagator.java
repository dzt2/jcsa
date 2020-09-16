package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.simpl;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecDeferencePropagator extends SecComputationPropagator {

	@Override
	protected void set_expression_error(SecSetExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.dereference(type, muta_operand);
		SecStateError target_error = this.set_expression(muta_expression);
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void add_expression_error(SecAddExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		
		SymExpression muta_expression; SecStateError target_error;
		switch(operator) {
		case arith_add:
		{
			muta_expression = SymFactory.arith_add(
							loperand.get_data_type(), loperand, roperand);
			muta_expression = SymFactory.dereference(type, muta_expression);
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_sub:
		{
			muta_expression = SymFactory.arith_sub(
						loperand.get_data_type(), loperand, roperand);
			muta_expression = SymFactory.dereference(type, muta_expression);
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		{
			this.report_unsupported_operations();
			target_error = null; break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void ins_expression_error(SecInsExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		
		SymExpression muta_expression; SecStateError target_error;
		switch(operator) {
		case arith_sub:
		{
			muta_expression = SymFactory.arith_sub(
						loperand.get_data_type(), roperand, loperand);
			muta_expression = SymFactory.dereference(type, muta_expression);
			target_error = this.set_expression(muta_expression); break;
		}
		case arith_div:
		case arith_mod:
		case left_shift:
		case righ_shift:
		{
			this.report_unsupported_operations();
			target_error = null; break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.add_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void uny_expression_error(SecUnyExpressionError error) throws Exception {
		this.report_unsupported_operations();
	}

	@Override
	protected boolean test_target_location(CirNode location) throws Exception {
		return location instanceof CirDeferExpression;
	}

}
