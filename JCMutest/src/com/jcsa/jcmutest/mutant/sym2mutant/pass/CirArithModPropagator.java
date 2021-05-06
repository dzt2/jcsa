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

public class CirArithModPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		CirComputeExpression target = (CirComputeExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymbolExpression muta_operand; SymbolExpression muta_value;
		SymConstraint constraint;  SymStateError state_error;
		if(error instanceof SymExpressionError) 
			muta_operand = ((SymExpressionError) error).get_mutation_value();
		else if(error instanceof SymReferenceError) 
			muta_operand = ((SymReferenceError) error).get_mutation_value();
		else return;
		
		/* muta_operand / y */
		if(source == target.get_operand(0)) {
			muta_value = SymbolFactory.arith_mod(target.get_data_type(), 
					muta_operand, target.get_operand(1));
			constraint = cir_mutations.expression_constraint(
					target.statement_of(), Boolean.TRUE, true);
			state_error = cir_mutations.expr_error(target, muta_value);
			propagations.put(state_error, constraint);
		}
		/* x / muta_operand */
		else if(source == target.get_operand(1)) {
			constraint = cir_mutations.expression_constraint(target.statement_of(), 
					SymbolFactory.equal_with(muta_operand, Integer.valueOf(0)), true);
			state_error = cir_mutations.trap_error(target.statement_of());
			propagations.put(state_error, constraint);
			
			constraint = cir_mutations.expression_constraint(target.statement_of(), 
					SymbolFactory.not_equals(muta_operand, Integer.valueOf(0)), true);
			muta_value = SymbolFactory.arith_mod(
					target.get_data_type(), target.get_operand(0), muta_operand);
			state_error = cir_mutations.expr_error(target, muta_value);
			propagations.put(state_error, constraint);
		}
		else {
			throw new IllegalArgumentException(target.generate_code(true));
		}
	}

}
