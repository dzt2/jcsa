package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecDereferencePropagator extends SecExpressionPropagator {

	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.dereference(type, muta_operand);
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception {
		CType type = this.target_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; SymExpression muta_expression;
		
		switch(operator) {
		case arith_add:
		{
			muta_expression = SymFactory.arith_add(loperand.get_data_type(), loperand, roperand);
			muta_expression = SymFactory.dereference(type, muta_expression);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_sub:
		{
			muta_expression = SymFactory.arith_sub(loperand.get_data_type(), loperand, roperand);
			muta_expression = SymFactory.dereference(type, muta_expression);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: 
		{
			this.report_unsupported_operation();
			target_error = null;
			break;
		}
		}
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}

	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		this.report_unsupported_operation();
	}

	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception {
		this.report_unsupported_operation();
	}

}
