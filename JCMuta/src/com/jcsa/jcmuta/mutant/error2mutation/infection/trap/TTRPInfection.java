package com.jcsa.jcmuta.mutant.error2mutation.infection.trap;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class TTRPInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return (CirStatement) cir_tree.get_cir_nodes(this.get_location(mutation), CirIfStatement.class);
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirIfStatement statement = (CirIfStatement) cir_tree.
				get_cir_nodes(this.get_location(mutation), CirIfStatement.class);
		long loop_times = ((Integer) mutation.get_parameter()).longValue();
		
		SymExpression constraint = StateEvaluation.new_condition(statement.get_condition(), true);
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		StateError error = graph.get_error_set().execute_for(statement, loop_times);
		output.put(error, constraints);
	}

}
