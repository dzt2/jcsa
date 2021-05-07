package com.jcsa.jcmutest.mutant.sym2mutant.pass;

import java.util.Map;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceUtils;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * set_expr(body) 	--> set_expr(body.field)
 * set_refer(body)	--> set_refer(body.field)
 * @author yukimula
 *
 */
public class CirFieldOfPropagator implements CirErrorPropagator {

	@Override
	public void propagate(SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirFieldExpression target = (CirFieldExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymConstraint constraint; SymStateError state_error; 
		SymbolExpression muta_operand; SymbolExpression muta_value;
		
		/* 2. check the operand */
		if(source == target.get_body()) {
			/* 3. construct the target error */
			if(error instanceof SymExpressionError) {
				muta_operand = ((SymExpressionError) error).get_mutation_value();
				muta_value = SymbolFactory.field_expression(muta_operand, target.get_field().get_name());
				state_error = SymInstanceUtils.expr_error(target, muta_value);
			}
			else if(error instanceof SymReferenceError) {
				muta_operand = ((SymReferenceError) error).get_mutation_value();
				muta_value = SymbolFactory.field_expression(muta_operand, target.get_field().get_name());
				state_error = SymInstanceUtils.refr_error(target, muta_value);
			}
			else {
				state_error = null;
			}
			
			/* 4. generate constraint-error pair */
			if(state_error != null) {
				constraint = SymInstanceUtils.expr_constraint(
						target.statement_of(), Boolean.TRUE, true);
				propagations.put(state_error, constraint);
			}
		}
	}

}
