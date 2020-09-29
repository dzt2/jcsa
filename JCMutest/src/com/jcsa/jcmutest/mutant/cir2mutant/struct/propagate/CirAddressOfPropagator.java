package com.jcsa.jcmutest.mutant.cir2mutant.struct.propagate;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.struct.CirErrorPropagator;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * set_refer --> set_expr(&muta_value)
 * @author yukimula
 *
 */
public class CirAddressOfPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, CirStateError error, CirNode source_location,
			CirNode target_location, Map<CirStateError, CirConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirAddressExpression target = (CirAddressExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymExpression muta_operand; SymExpression muta_value;
		CirConstraint constraint; CirStateError state_error;
		
		/* 2. perform error propagation */
		if(source == target.get_operand()) {
			/* 3. determine the mutation value of operand */
			if(error instanceof CirReferenceError) {
				muta_operand = ((CirReferenceError) error).get_mutation_value();
			}
			else {
				muta_operand = null;
			}
			
			/* 4. constraint the constraint-error propagation */
			if(muta_operand != null) {
				muta_value = SymFactory.address_of(target.get_data_type(), muta_operand);
				constraint = cir_mutations.
						expression_constraint(target.statement_of(), Boolean.TRUE, true);
				state_error = cir_mutations.expr_error(target, muta_value);
				propagations.put(state_error, constraint);
			}
		}
	}

}
