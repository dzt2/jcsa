package com.jcsa.jcmutest.mutant.sym2mutant.pass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateValueError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirArgumentListPropagator implements CirErrorPropagator {

	@Override
	public void propagate(SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* 0. extract the mutation argument */
		SymbolExpression mutation_argument;
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
		CirExecution call_execution = source_location.get_tree().get_localizer().get_execution(call_statement);
		CirExecution wait_execution = call_execution.
						get_graph().get_execution(call_execution.get_id() + 1);
		CirAssignStatement statement = (CirAssignStatement) wait_execution.get_statement();
		CirExpression target = statement.get_rvalue(); SymbolExpression muta_value;
		
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
		muta_value = SymbolFactory.call_expression(call_statement.get_function(), arguments);
		
		/* 3. construct constraint-error pair */
		SymStateError state_error = SymInstances.expr_error(target, muta_value);
		SymConstraint constraint = SymInstances.expr_constraint(statement, Boolean.TRUE, true);
		propagations.put(state_error, constraint);
	}

}
