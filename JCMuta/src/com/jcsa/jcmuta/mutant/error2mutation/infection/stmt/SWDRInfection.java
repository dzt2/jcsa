package com.jcsa.jcmuta.mutant.error2mutation.infection.stmt;

import java.util.Map;
import java.util.Set;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class SWDRInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return (CirStatement) cir_tree.get_cir_nodes(this.get_location(mutation), CirIfStatement.class).get(0);
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirIfStatement if_statement = (CirIfStatement) this.get_location(cir_tree, mutation);
		
		SymExpression constraint = StateEvaluation.
				new_condition(if_statement.get_condition(), false);
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, if_statement, constraint);
		
		switch(mutation.get_mutation_operator()) {
		case while_to_do:
		{
			output.put(graph.get_error_set().set_bool(if_statement.get_condition(), true), constraints);
		}
		break;
		case do_to_while:
		{
			Set<CirStatement> statements_in_loop =
					this.collect_statements_in(cir_tree, this.get_location(mutation));
			statements_in_loop.remove(if_statement);
			for(CirStatement statement : statements_in_loop) {
				if(!(statement instanceof CirTagStatement)) {
					output.put(graph.get_error_set().not_execute(statement), constraints);
				}
			}
		}
		break;
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_mutation_operator());
		}
		
	}

}
