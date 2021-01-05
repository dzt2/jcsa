package com.jcsa.jcmutest.mutant.cir2mutant.gate;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirAssignPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirAssignStatement target = (CirAssignStatement) target_location;
		CirExpression source = (CirExpression) source_location;
		SymConstraint constraint; SymStateError state_error;
		SymbolExpression muta_operand; SymbolExpression muta_value;
		
		if(source == target.get_rvalue()) {
			CirExpression reference = target.get_lvalue();
			if(error instanceof SymExpressionError) {
				muta_operand = ((SymExpressionError) error).get_mutation_value();
			}
			else if(error instanceof SymReferenceError) {
				muta_operand = ((SymReferenceError) error).get_mutation_value();
			}
			else {
				muta_operand = null;
			}
			
			if(muta_operand != null) {
				muta_value = SymbolFactory.cast_expression(reference.get_data_type(), muta_operand);
				constraint = cir_mutations.expression_constraint(target, Boolean.TRUE, true);
				state_error = cir_mutations.state_error((CirReferExpression) reference, muta_value);
				propagations.put(state_error, constraint);
			}
		}
	}

}
