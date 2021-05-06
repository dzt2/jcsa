package com.jcsa.jcmutest.mutant.sym2mutant.pass;

import java.util.Map;

import com.jcsa.jcmutest.mutant.sym2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * set_refer --> set_expr(&muta_value)
 * @author yukimula
 *
 */
public class CirAddressOfPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirAddressExpression target = (CirAddressExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymbolExpression muta_operand; SymbolExpression muta_value;
		SymConstraint constraint; SymStateError state_error;
		
		/* 2. perform error propagation */
		if(source == target.get_operand()) {
			/* 3. determine the mutation value of operand */
			if(error instanceof SymReferenceError) {
				muta_operand = ((SymReferenceError) error).get_mutation_value();
			}
			else {
				muta_operand = null;
			}
			
			/* 4. constraint the constraint-error propagation */
			if(muta_operand != null) {
				muta_value = SymbolFactory.address_of(muta_operand);
				constraint = cir_mutations.
						expression_constraint(target.statement_of(), Boolean.TRUE, true);
				state_error = cir_mutations.expr_error(target, muta_value);
				propagations.put(state_error, constraint);
			}
		}
	}

}
