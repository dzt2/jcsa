package com.jcsa.jcmutest.mutant.cir2mutant.struct.propagate;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.struct.CirErrorPropagator;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class CirArithNegPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, CirStateError error, CirNode source_location,
			CirNode target_location, Map<CirStateError, CirConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirComputeExpression target = (CirComputeExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		CirConstraint constraint; CirStateError state_error; 
		SymExpression muta_operand; SymExpression muta_value;
		
		if(source == target.get_operand(0)) {
			/* 2. obtain the muta-operand */
			if(error instanceof CirExpressionError) {
				muta_operand = ((CirExpressionError) error).get_mutation_value();
			}
			else if(error instanceof CirReferenceError) {
				muta_operand = ((CirReferenceError) error).get_mutation_value();
			}
			else {
				muta_operand = null;
			}
			
			/* 3. construct the muta_value */
			if(muta_operand != null) {
				muta_value = SymFactory.
						arith_neg(target.get_data_type(), muta_operand);
				constraint = cir_mutations.expression_constraint(
						target.statement_of(), Boolean.TRUE, true);
				state_error = cir_mutations.expr_error(target, muta_value);
				propagations.put(state_error, constraint);
			}
		}
	}

}
