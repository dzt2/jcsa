package com.jcsa.jcmutest.mutant.cir2mutant.pgate;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class CirArithMulPropagator implements CirErrorPropagator {

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
			muta_value = SymFactory.arith_add(target.get_data_type(), 
					muta_operand, target.get_operand(1));
			constraint = cir_mutations.expression_constraint(target.statement_of(), 
					SymFactory.not_equals(target.get_operand(1), Integer.valueOf(0)), 
					true);
		}
		else if(source == target.get_operand(1)) {
			muta_value = SymFactory.arith_add(target.get_data_type(), 
					target.get_operand(0), muta_operand);
			constraint = cir_mutations.expression_constraint(target.statement_of(), 
					SymFactory.not_equals(target.get_operand(0), Integer.valueOf(0)), 
					true);
		}
		else {
			throw new IllegalArgumentException(target.generate_code(true));
		}
		
		state_error = cir_mutations.expr_error(target, muta_value);
		propagations.put(state_error, constraint);
	}

}
