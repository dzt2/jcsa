package com.jcsa.jcmuta.mutant.error2mutation.infection.oaln;

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
 * loperand == 0 and roperand != 0	--> dif_numb(1)
 * loperand != 0 and roperand == 0	--> dif_numb(1)
 * loperand != 0 and roperand != 0	--> set_true
 * @author yukimula
 *
 */
public class MULLORInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.logic_or(loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints;
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		CirStatement statement = expression.statement_of();
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() > 1) {
					output.put(graph.get_error_set().set_bool(expression, true), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) lconstant).longValue() < 0) {
					output.put(graph.get_error_set().set_bool(expression, true), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) lconstant).longValue() == 0) {
					return true;	/** equivalent mutant detected **/
				}
				else {
					constraints = StateEvaluation.get_disjunctions();
					this.add_constraint(constraints, statement, StateEvaluation.greater_tn(roperand, 1L));
					this.add_constraint(constraints, statement, StateEvaluation.smaller_tn(roperand, 0L));
					output.put(graph.get_error_set().set_bool(expression, true), constraints);
				}
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() > 1) {
					output.put(graph.get_error_set().set_bool(expression, true), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) rconstant).longValue() < 0) {
					output.put(graph.get_error_set().set_bool(expression, true), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) rconstant).longValue() == 0) {
					return true;	/** equivalent mutant detected **/
				}
				else {
					constraints = StateEvaluation.get_disjunctions();
					this.add_constraint(constraints, statement, StateEvaluation.greater_tn(loperand, 1L));
					this.add_constraint(constraints, statement, StateEvaluation.smaller_tn(loperand, 0L));
					output.put(graph.get_error_set().set_bool(expression, true), constraints);
				}
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints = StateEvaluation.get_disjunctions();
		CirStatement statement = expression.statement_of();
		
		this.add_constraint(constraints, statement, StateEvaluation.greater_tn(loperand, 1L));
		this.add_constraint(constraints, statement, StateEvaluation.smaller_tn(loperand, 0L));
		this.add_constraint(constraints, statement, StateEvaluation.greater_tn(roperand, 1L));
		this.add_constraint(constraints, statement, StateEvaluation.smaller_tn(roperand, 0L));
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		return true;
	}

}
