package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.impl;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;

public class SecLogicNotPropagator extends SecExpressionPropagator {
	
	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SecFactory.sym_condition(muta_operand, false);
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
		
		
		
	}
	
	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
