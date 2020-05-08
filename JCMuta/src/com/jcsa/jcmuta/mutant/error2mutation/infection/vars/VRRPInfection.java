package com.jcsa.jcmuta.mutant.error2mutation.infection.vars;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;

public class VRRPInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = this.get_result_of(
				cir_tree, this.get_location(mutation));
		CName parameter = (CName) mutation.get_parameter();
		SymExpression source = SymFactory.parse(expression);
		SymExpression target = StateEvaluation.new_variable(
				expression.get_data_type(), parameter.get_name());
		SymExpression constraint = StateEvaluation.not_equals(source, target);
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().mut_refer(expression), constraints);
	}

}
