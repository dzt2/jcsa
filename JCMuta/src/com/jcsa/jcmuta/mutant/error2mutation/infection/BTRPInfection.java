package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class BTRPInfection extends StateInfection {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		CirExpression result = this.get_result_of(cir_tree, this.get_location(mutation));
		if(result != null) {
			return result.statement_of();
		}
		else {
			return null;
		}
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		AstExpression ast_location = (AstExpression) this.get_location(mutation);
		CirExpression expression = this.get_result_of(cir_tree, ast_location);
		
		SymExpression constraint;
		switch(mutation.get_mutation_operator()) {
		case trap_on_true:	constraint = StateEvaluation.new_condition(expression, true);	break;
		case trap_on_false:	constraint = StateEvaluation.new_condition(expression, false);	break;
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_mutation_operator());
		}
		
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		StateError error = graph.get_error_set().failure();
		
		output.put(error, constraints);
	}
	
}
