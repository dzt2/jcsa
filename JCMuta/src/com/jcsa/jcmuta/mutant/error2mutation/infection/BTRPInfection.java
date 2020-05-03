package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
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
	
	/**
	 * p 
	 * p != 0
	 * p != null
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private StateConstraints trap_on_true_constraints(CirExpression expression) throws Exception {
		SymExpression constraint = this.get_sym_condition(expression, true);
		constraint = this.derive_sym_constraint(constraint);
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(expression.statement_of(), constraint);
		
		return constraints;
	}
	
	/**
	 * !p
	 * p == null
	 * p == 0
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private StateConstraints trap_on_false_constraints(CirExpression expression) throws Exception {
		SymExpression constraint = this.get_sym_condition(expression, false);
		constraint = this.derive_sym_constraint(constraint);
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(expression.statement_of(), constraint);
		
		return constraints;
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		AstExpression ast_location = (AstExpression) this.get_location(mutation);
		CirExpression expression = this.get_result_of(cir_tree, ast_location);
		
		StateConstraints constraints;
		switch(mutation.get_mutation_operator()) {
		case trap_on_true:	constraints = this.trap_on_true_constraints(expression);	break;
		case trap_on_false:	constraints = this.trap_on_false_constraints(expression);	break;
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_mutation_operator());
		}
		StateError error = graph.get_error_set().failure();
		
		output.put(error, constraints);
	}
	
}
