package com.jcsa.jcmutest.mutant.cir2mutant.gate;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * *(set_expr) 	--> set_refer
 * *(set_refer)	--> set_refer
 * @author yukimula
 *
 */
public class CirDereferencePropagator implements CirErrorPropagator {
	
	@Override
	public void propagate(CirMutations cir_mutations, CirStateError error, CirNode source_location,
			CirNode target_location, Map<CirStateError, CirConstraint> propagations) throws Exception {
		CirDeferExpression target = (CirDeferExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		CirConstraint constraint; CirStateError state_error; 
		SymExpression muta_operand; SymExpression muta_value;
		
		if(source == target.get_address()) {
			/* 1. obtain the mutated operand */
			if(error instanceof CirExpressionError) {
				muta_operand = ((CirExpressionError) error).get_mutation_value();
			}
			else if(error instanceof CirReferenceError) {
				muta_operand = ((CirReferenceError) error).get_mutation_value();
			}
			else {
				muta_operand = null;
			}
			
			/* 2. true --> set_refer(*muta_operand) */
			if(muta_operand != null) {
				constraint = cir_mutations.expression_constraint(
						target.statement_of(), Boolean.TRUE, true);
				muta_value = SymFactory.
						dereference(target.get_data_type(), muta_operand);
				state_error = cir_mutations.refer_error(target, muta_value);
				propagations.put(state_error, constraint);
			}
		}
	}
	
}
