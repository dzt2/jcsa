package com.jcsa.jcmuta.mutant.error2mutation.infection.olln;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class LORLANInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.binary_expression(CBasicTypeImpl.
				bool_type, COperator.logic_and, loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		CirStatement statement = expression.statement_of(); SymExpression constraint; 
		
		if(!(lconstant instanceof SymExpression)) {
			boolean lvalue = StateEvaluation.get_condition_value(lconstant);
			if(lvalue) {
				constraint = StateEvaluation.new_condition(roperand, false);
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, false), constraints);
			}
			else {
				constraint = StateEvaluation.new_condition(roperand, true);
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, false), constraints);
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			boolean rvalue = StateEvaluation.get_condition_value(rconstant);
			if(rvalue) {
				constraint = StateEvaluation.new_condition(loperand, false);
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, false), constraints);
			}
			else {
				constraint = StateEvaluation.new_condition(loperand, true);
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, false), constraints);
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression condition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		condition = StateEvaluation.not_equals(
				StateEvaluation.new_condition(loperand, true), 
				StateEvaluation.new_condition(roperand, true));
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, condition);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
		
		return true;
	}

}
