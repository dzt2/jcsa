package com.jcsa.jcmutest.mutant.cir2mutant.gate;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class CirLogicAndPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, CirStateError error, CirNode source_location,
			CirNode target_location, Map<CirStateError, CirConstraint> propagations) throws Exception {
		CirComputeExpression target = (CirComputeExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymExpression muta_operand; SymExpression muta_value;
		CirConstraint constraint; CirStateError state_error; 
		
		if(error instanceof CirExpressionError) {
			muta_operand = ((CirExpressionError) error).get_mutation_value();
		}
		else if(error instanceof CirReferenceError) {
			muta_operand = ((CirReferenceError) error).get_mutation_value();
		}
		else {
			return;
		}
		
		if(source == target.get_operand(0)) {
			constraint = cir_mutations.expression_constraint(
					target.statement_of(), 
					target.get_operand(1), true);
		}
		else if(source == target.get_operand(1)) {
			constraint = cir_mutations.expression_constraint(
					target.statement_of(), 
					target.get_operand(0), true);
		}
		else {
			throw new IllegalArgumentException(target.generate_code(true));
		}
		muta_value = muta_operand;
		state_error = cir_mutations.expr_error(target, muta_value);
		
		propagations.put(state_error, constraint);
	}

}
