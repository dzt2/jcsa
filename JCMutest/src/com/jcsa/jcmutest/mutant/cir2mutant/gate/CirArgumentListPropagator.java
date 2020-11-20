package com.jcsa.jcmutest.mutant.cir2mutant.gate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateValueError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class CirArgumentListPropagator implements CirErrorPropagator {

	@Override
	public void propagate(CirMutations cir_mutations, SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* 0. extract the mutation argument */
		SymExpression mutation_argument;
		if(error instanceof SymExpressionError) {
			mutation_argument = ((SymExpressionError) error).get_mutation_value();
		}
		else if(error instanceof SymStateValueError) {
			mutation_argument = ((SymStateValueError) error).get_mutation_value();
		}
		else if(error instanceof SymReferenceError) {
			mutation_argument = ((SymReferenceError) error).get_mutation_value();
		}
		else {
			return;
		}
		
		/* 1. declarations */
		CirArgumentList alist = (CirArgumentList) target_location;
		CirExpression source = (CirExpression) source_location;
		CirCallStatement call_statement = (CirCallStatement) alist.get_parent();
		CirExecution call_execution = cir_mutations.
				get_cir_tree().get_localizer().get_execution(call_statement);
		CirExecution wait_execution = call_execution.
						get_graph().get_execution(call_execution.get_id() + 1);
		CirAssignStatement statement = (CirAssignStatement) wait_execution.get_statement();
		CirExpression target = statement.get_rvalue(); SymExpression muta_value;
		
		/* 2. rebuild the new arguments list */
		List<Object> arguments = new ArrayList<Object>();
		for(int k = 0; k < alist.number_of_arguments(); k++) {
			if(alist.get_argument(k) == source) {
				arguments.add(mutation_argument);
			}
			else {
				arguments.add(alist.get_argument(k));
			}
		}
		muta_value = SymFactory.call_expression(target.
				get_data_type(), call_statement.get_function(), arguments);
		
		/* 3. construct constraint-error pair */
		SymStateError state_error = cir_mutations.expr_error(target, muta_value);
		SymConstraint constraint = cir_mutations.expression_constraint(statement, Boolean.TRUE, true);
		propagations.put(state_error, constraint);
	}

}
