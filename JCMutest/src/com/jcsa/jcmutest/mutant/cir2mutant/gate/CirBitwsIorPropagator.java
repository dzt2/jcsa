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

public class CirBitwsIorPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		CirComputeExpression target = (CirComputeExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymExpression muta_operand; SymExpression muta_value;
		SymConstraint constraint; SymStateError state_error; 
		
		if(error instanceof SymExpressionError) {
			muta_operand = ((SymExpressionError) error).get_mutation_value();
		}
		else if(error instanceof SymReferenceError) {
			muta_operand = ((SymReferenceError) error).get_mutation_value();
		}
		else {
			return;
		}
		
		if(source == target.get_operand(0)) {
			muta_value = SymFactory.bitws_ior(target.get_data_type(), 
					muta_operand, target.get_operand(1));
			constraint = cir_mutations.expression_constraint(target.statement_of(), 
					SymFactory.not_equals(target.get_operand(1), Integer.valueOf(~0)), 
					true);
		}
		else if(source == target.get_operand(1)) {
			muta_value = SymFactory.bitws_ior(target.get_data_type(), 
					target.get_operand(0), muta_operand);
			constraint = cir_mutations.expression_constraint(target.statement_of(), 
					SymFactory.not_equals(target.get_operand(0), Integer.valueOf(~0)), 
					true);
		}
		else {
			throw new IllegalArgumentException(target.generate_code(true));
		}
		
		state_error = cir_mutations.expr_error(target, muta_value);
		propagations.put(state_error, constraint);
	}

}
