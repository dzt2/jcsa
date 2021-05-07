package com.jcsa.jcmutest.mutant.sym2mutant.pass;

import java.util.Map;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceUtils;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirTypeCastPropagator implements CirErrorPropagator {
	
	@Override
	public void propagate(SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirCastExpression target = (CirCastExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymConstraint constraint; SymStateError state_error; 
		SymbolExpression muta_operand; SymbolExpression muta_value;
		
		if(source == target.get_operand()) {
			/* 2. determine the muta_operand */
			if(error instanceof SymExpressionError) {
				muta_operand = ((SymExpressionError) error).get_mutation_value();
			}
			else if(error instanceof SymReferenceError) {
				muta_operand = ((SymReferenceError) error).get_mutation_value();
			}
			else {
				muta_operand = null;
			}
			
			/* 3. construct the constraint-error pair */
			if(muta_operand != null) {
				constraint = SymInstanceUtils.expr_constraint(
						target.statement_of(), Boolean.TRUE, true);
				muta_value = SymbolFactory.
						cast_expression(target.get_data_type(), muta_operand);
				state_error = SymInstanceUtils.expr_error(target, muta_value);
				propagations.put(state_error, constraint);
			}
		}
	}
	
}
