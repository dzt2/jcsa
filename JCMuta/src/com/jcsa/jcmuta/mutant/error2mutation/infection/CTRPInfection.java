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

/**
 * trap_on_case(expression, val)
 * @author yukimula
 *
 */
public class CTRPInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		AstExpression condition = (AstExpression) this.get_location(mutation);
		AstExpression case_value = (AstExpression) 
					this.get_location((AstMutation) mutation.get_parameter());
		
		CirStatement statement = this.get_beg_statement(cir_tree, condition);
		CirExpression loperand = this.get_result_of(cir_tree, condition);
		CirExpression roperand = this.get_result_of(cir_tree, case_value);
		
		SymExpression constraint = StateEvaluation.equal_with(loperand, roperand);
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		StateError error = graph.get_error_set().failure();
		
		output.put(error, constraints);
	}

}
