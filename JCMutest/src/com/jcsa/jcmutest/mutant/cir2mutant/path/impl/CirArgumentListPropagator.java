package com.jcsa.jcmutest.mutant.cir2mutant.path.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.path.CirErrorPropagator;
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
	public void propagate(CirMutations cir_mutations, CirStateError error, CirNode source_location,
			CirNode target_location, Map<CirStateError, CirConstraint> propagations) throws Exception {
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
				arguments.add(source);
			}
			else {
				arguments.add(alist.get_argument(k));
			}
		}
		muta_value = SymFactory.call_expression(target.
				get_data_type(), call_statement.get_function(), arguments);
		
		/* 3. construct constraint-error pair */
		CirStateError state_error = cir_mutations.expr_error(target, muta_value);
		CirConstraint constraint = cir_mutations.expression_constraint(statement, Boolean.TRUE, true);
		propagations.put(state_error, constraint);
	}

}
