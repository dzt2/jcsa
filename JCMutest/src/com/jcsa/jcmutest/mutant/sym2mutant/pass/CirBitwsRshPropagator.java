package com.jcsa.jcmutest.mutant.sym2mutant.pass;

import java.util.Map;

import com.jcsa.jcmutest.mutant.sym2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;


public class CirBitwsRshPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		CirComputeExpression target = (CirComputeExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymbolExpression muta_operand; SymbolExpression muta_value;
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
			constraint = cir_mutations.expression_constraint(
					target.statement_of(), Boolean.TRUE, true);
			muta_value = SymbolFactory.bitws_rsh(target.get_data_type(), 
					muta_operand, target.get_operand(1));
		}
		else if(source == target.get_operand(1)) {
			constraint = cir_mutations.expression_constraint(
					target.statement_of(), SymbolFactory.not_equals(target.
							get_operand(0), Integer.valueOf(0)), true);
			muta_value = SymbolFactory.bitws_rsh(target.get_data_type(), 
					target.get_operand(0), muta_operand);
		}
		else {
			throw new IllegalArgumentException(target.generate_code(true));
		}
		
		state_error = cir_mutations.expr_error(target, muta_value);
		propagations.put(state_error, constraint);
	}

}
