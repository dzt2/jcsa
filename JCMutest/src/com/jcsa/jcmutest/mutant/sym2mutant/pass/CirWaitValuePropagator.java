package com.jcsa.jcmutest.mutant.sym2mutant.pass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirWaitValuePropagator implements CirErrorPropagator {

	@Override
	public void propagate(SymStateError error, CirNode source_location,
			CirNode target_location, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* 1. declarations */
		CirWaitExpression target = (CirWaitExpression) target_location;
		CirExpression source = (CirExpression) source_location;
		SymConstraint constraint; SymStateError state_error;
		SymbolExpression muta_operand; SymbolExpression muta_value;

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
			CirExecution wait_execution = source_location.get_tree().
					get_localizer().get_execution(wait_statement);
			CirExecution call_execution = wait_execution.
					get_graph().get_execution(wait_execution.get_id() - 1);
			CirCallStatement call_statement =
						(CirCallStatement) call_execution.get_statement();
			CirArgumentList alist = call_statement.get_arguments();

			/* 4. collect the arguments list */
			List<Object> arguments = new ArrayList<>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			muta_value = SymbolFactory.call_expression(muta_operand, arguments);

			/* 5. construct the error propagation pair */
			constraint = SymInstances.
					expr_constraint(wait_statement, Boolean.TRUE, true);
			state_error = SymInstances.expr_error(target, muta_value);
			propagations.put(state_error, constraint);
		}
	}

}
