package com.jcsa.jcmutest.mutant.cir2mutant.pgate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class CirInitializerPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, CirStateError error, CirNode source_location,
			CirNode target_location, Map<CirStateError, CirConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirInitializerBody target = (CirInitializerBody) target_location;
		CirExpression source = (CirExpression) source_location;
		CirConstraint constraint; CirStateError state_error; 
		SymExpression muta_operand; SymExpression muta_value;
		
		/* 2. obtain the muta_operand */
		if(error instanceof CirExpressionError) {
			muta_operand = ((CirExpressionError) error).get_mutation_value();
		}
		else if(error instanceof CirReferenceError) {
			muta_operand = ((CirReferenceError) error).get_mutation_value();
		}
		else {
			muta_operand = null;
		}
		
		if(muta_operand != null) {
			/* 3. construct the muta_value */
			List<Object> elements = new ArrayList<Object>();
			for(int k = 0; k < target.number_of_elements(); k++) {
				if(target.get_element(k) == source) {
					elements.add(muta_operand);
				}
				else {
					elements.add(target.get_element(k));
				}
			}
			muta_value = SymFactory.initializer_list(
					target.get_data_type(), elements);
			
			/* 4. construct the constraint-error pair */
			constraint = cir_mutations.expression_constraint(
					target.statement_of(), Boolean.TRUE, true);
			state_error = cir_mutations.expr_error(target, muta_value);
			propagations.put(state_error, constraint);
		}
	}

}
