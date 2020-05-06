package com.jcsa.jcmuta.mutant.error2mutation.infection.stmt;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class SBCRInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	/**
	 * get the condition in the looping statement
	 * @param cir_tree
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_loop_condition(CirTree cir_tree, AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		while(location != null) {
			CirIfStatement if_statement;
			if(location instanceof AstWhileStatement) {
				if_statement = (CirIfStatement) cir_tree.get_cir_nodes(location, CirIfStatement.class);
				return if_statement.get_condition();
			}
			else if(location instanceof AstDoWhileStatement) {
				if_statement = (CirIfStatement) cir_tree.get_cir_nodes(location, CirIfStatement.class);
				return if_statement.get_condition();
			}
			else if(location instanceof AstForStatement) {
				if_statement = (CirIfStatement) cir_tree.get_cir_nodes(location, CirIfStatement.class);
				return if_statement.get_condition();
			}
			else {
				location = location.get_parent();
			}
		}
		return null;	/* unable to find */
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression condition = this.get_loop_condition(cir_tree, mutation);
		
		if(condition == null) {
			StateConstraints constraints = new StateConstraints(true);
			output.put(graph.get_error_set().syntax_error(), constraints);
		}
		else {
			SymExpression constraint; StateError error;
			switch(mutation.get_mutation_operator()) {
			case break_to_continue:
			{
				constraint = StateEvaluation.new_condition(condition, true);
				error = graph.get_error_set().set_bool(condition, true);
			}
			break;
			case continue_to_break:
			{
				constraint = StateEvaluation.new_condition(condition, true);
				error = graph.get_error_set().set_bool(condition, false);
			}
			break;
			default: throw new IllegalArgumentException("Invalid: " + mutation.get_mutation_operator());
			}
			
			StateConstraints constraints = StateEvaluation.get_conjunctions();
			this.add_constraint(constraints, condition.statement_of(), constraint);
			output.put(error, constraints);
		}
	}

}
