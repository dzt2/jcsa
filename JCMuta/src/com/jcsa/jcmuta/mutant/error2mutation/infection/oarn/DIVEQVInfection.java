package com.jcsa.jcmuta.mutant.error2mutation.infection.oarn;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * loperand == 0 							--> equivalence
 * loperand == roperand 					--> equivalence
 * loperand != 0 and loperand != roperand 	--> set_false
 * @author yukimula
 *
 */
public class DIVEQVInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.equal_with(loperand, roperand); 
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		SymExpression constraint; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		if(!(lconstant instanceof SymExpression)) {
			if(!StateEvaluation.is_zero_number(lconstant)) {
				constraint = StateEvaluation.not_equals(loperand, roperand);
				constraints = StateEvaluation.get_conjunctions();
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, false), constraints);
			}
			return true;
		}
		
		if(!(rconstant instanceof SymExpression)) {
			constraints = StateEvaluation.get_conjunctions();
			this.add_constraint(constraints, statement, StateEvaluation.not_equals(loperand, 0L));
			this.add_constraint(constraints, statement, StateEvaluation.not_equals(loperand, roperand));
			output.put(graph.get_error_set().set_bool(expression, false), constraints);
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, StateEvaluation.not_equals(loperand, 0L));
		this.add_constraint(constraints, statement, StateEvaluation.not_equals(loperand, roperand));
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
		return true;
	}

}
