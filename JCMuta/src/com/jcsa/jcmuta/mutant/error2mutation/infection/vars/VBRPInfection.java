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
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * 
 * @author yukimula
 *
 */
public class VBRPInfection extends StateInfection {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = this.get_result_of(cir_tree, this.get_location(mutation));
		
		SymExpression constraint; StateError error;
		switch(mutation.get_mutation_operator()) {
		case set_true:
		{
			constraint = StateEvaluation.new_condition(expression, false);
			error = graph.get_error_set().set_bool(expression, true);
		}
		break;
		case set_false:
		{
			constraint = StateEvaluation.new_condition(expression, true);
			error = graph.get_error_set().set_bool(expression, false);
		}
		break;
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_mutation_operator());
		}
		
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(error, constraints);
	}
	
}
