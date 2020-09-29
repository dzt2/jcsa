package com.jcsa.jcmutest.mutant.cir2mutant.struct.propagate;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.struct.CirErrorPropagator;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * set_expr(body) 	--> set_expr(body.field)
 * set_refer(body)	--> set_refer(body.field)
 * @author yukimula
 *
 */
public class CirFieldOfPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, CirStateError error, CirNode source_location,
			CirNode target_location, Map<CirStateError, CirConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirFieldExpression target = (CirFieldExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		CirConstraint constraint; CirStateError state_error; 
		SymExpression muta_operand; SymExpression muta_value;
		
		/* 2. check the operand */
		if(source == target.get_body()) {
			/* 3. construct the target error */
			if(error instanceof CirExpressionError) {
				muta_operand = ((CirExpressionError) error).get_mutation_value();
				muta_value = SymFactory.field_expression(target.get_data_type(), 
						muta_operand, target.get_field().get_name());
				state_error = cir_mutations.expr_error(target, muta_value);
			}
			else if(error instanceof CirReferenceError) {
				muta_operand = ((CirReferenceError) error).get_mutation_value();
				muta_value = SymFactory.field_expression(target.get_data_type(), 
						muta_operand, target.get_field().get_name());
				state_error = cir_mutations.refer_error(target, muta_value);
			}
			else {
				state_error = null;
			}
			
			/* 4. generate constraint-error pair */
			if(state_error != null) {
				constraint = cir_mutations.expression_constraint(
						target.statement_of(), Boolean.TRUE, true);
				propagations.put(state_error, constraint);
			}
		}
	}

}
