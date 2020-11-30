package com.jcsa.jcmutest.mutant.cir2mutant.gate;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class CirBitwsRsvPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirComputeExpression target = (CirComputeExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymConstraint constraint; SymStateError state_error; 
		SymExpression muta_operand; SymExpression muta_value;
		
		if(source == target.get_operand(0)) {
			/* 2. obtain the muta-operand */
			if(error instanceof SymExpressionError) {
				muta_operand = ((SymExpressionError) error).get_mutation_value();
			}
			else if(error instanceof SymReferenceError) {
				muta_operand = ((SymReferenceError) error).get_mutation_value();
			}
			else {
				muta_operand = null;
			}
			
			/* 3. construct the muta_value */
			if(muta_operand != null) {
				muta_value = SymFactory.
						bitws_rsv(target.get_data_type(), muta_operand);
				constraint = cir_mutations.expression_constraint(
						target.statement_of(), Boolean.TRUE, true);
				state_error = cir_mutations.expr_error(target, muta_value);
				propagations.put(state_error, constraint);
			}
		}
	}

}