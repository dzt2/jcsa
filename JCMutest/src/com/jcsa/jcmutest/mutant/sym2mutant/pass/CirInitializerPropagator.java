package com.jcsa.jcmutest.mutant.sym2mutant.pass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceUtils;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirInitializerPropagator implements CirErrorPropagator {

	@Override
	public void propagate(SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirInitializerBody target = (CirInitializerBody) target_location;
		CirExpression source = (CirExpression) source_location;
		SymConstraint constraint; SymStateError state_error; 
		SymbolExpression muta_operand; SymbolExpression muta_value;
		
		/* 2. obtain the muta_operand */
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
			muta_value = SymbolFactory.initializer_list(elements);
			
			/* 4. construct the constraint-error pair */
			constraint = SymInstanceUtils.expr_constraint(
					target.statement_of(), Boolean.TRUE, true);
			state_error = SymInstanceUtils.expr_error(target, muta_value);
			propagations.put(state_error, constraint);
		}
	}

}
