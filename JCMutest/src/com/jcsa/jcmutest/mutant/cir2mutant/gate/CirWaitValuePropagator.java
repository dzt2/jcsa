package com.jcsa.jcmutest.mutant.cir2mutant.gate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class CirWaitValuePropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirWaitExpression target = (CirWaitExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymConstraint constraint; SymStateError state_error; 
		SymExpression muta_operand; SymExpression muta_value;
		
		if(source == target.get_function()) {
			/* 2. determine the muta_operand */
			if(error instanceof SymExpressionError) {
				muta_operand = ((SymExpressionError) error).get_mutation_value();
			}
			else if(error instanceof SymReferenceError) {
				muta_operand = ((SymReferenceError) error).get_mutation_value();
			}
			else {
				muta_operand = null;
			}
			
			/* 3. find the call-statement */
			CirStatement wait_statement = target.statement_of();
			CirExecution wait_execution = cir_mutations.get_cir_tree().
					get_localizer().get_execution(wait_statement);
			CirExecution call_execution = wait_execution.
					get_graph().get_execution(wait_execution.get_id() - 1);
			CirCallStatement call_statement = 
						(CirCallStatement) call_execution.get_statement();
			CirArgumentList alist = call_statement.get_arguments();
			
			/* 4. collect the arguments list */
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			muta_value = SymFactory.call_expression(
					target.get_data_type(), muta_operand, arguments);
			
			/* 5. construct the error propagation pair */
			constraint = cir_mutations.
					expression_constraint(wait_statement, Boolean.TRUE, true);
			state_error = cir_mutations.expr_error(target, muta_value);
			propagations.put(state_error, constraint);
		}
	}

}
