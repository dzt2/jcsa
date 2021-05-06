package com.jcsa.jcmutest.mutant.sym2mutant.pass;

import java.util.Map;

import com.jcsa.jcmutest.mutant.sym2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * *(set_expr) 	--> set_refer
 * *(set_refer)	--> set_refer
 * @author yukimula
 *
 */
public class CirDereferencePropagator implements CirErrorPropagator {
	
	@Override
	public void propagate(CirMutations cir_mutations, SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		CirDeferExpression target = (CirDeferExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymConstraint constraint; SymStateError state_error; 
		SymbolExpression muta_operand; SymbolExpression muta_value;
		
		if(source == target.get_address()) {
			/* 1. obtain the mutated operand */
			if(error instanceof SymExpressionError) {
				muta_operand = ((SymExpressionError) error).get_mutation_value();
			}
			else if(error instanceof SymReferenceError) {
				muta_operand = ((SymReferenceError) error).get_mutation_value();
			}
			else {
				muta_operand = null;
			}
			
			/* 2. true --> set_refer(*muta_operand) */
			if(muta_operand != null) {
				constraint = cir_mutations.expression_constraint(
						target.statement_of(), Boolean.TRUE, true);
				muta_value = SymbolFactory.dereference(muta_operand);
				state_error = cir_mutations.refer_error(target, muta_value);
				propagations.put(state_error, constraint);
			}
		}
	}
	
}
