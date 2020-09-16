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
		SymExpression orig_operand = error.get_orig_expression().get_expression();
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		CType type = this.target_expression().get_data_type();
		
		SecStateError target_error; SymExpression muta_expression;
		if(orig_operand.get_cir_source() == loperand) {
			muta_expression = SymFactory.arith_add(type, muta_operand, roperand);
		}
		else if(orig_operand.get_cir_source() == roperand) {
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void ins_expression_error(SecInsExpressionError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void uny_expression_error(SecUnyExpressionError error) throws Exception {
		// TODO Auto-generated method stub
		
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
