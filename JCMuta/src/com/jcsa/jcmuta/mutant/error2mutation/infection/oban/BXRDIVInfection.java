package com.jcsa.jcmuta.mutant.error2mutation.infection.oban;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * loperand == 0		--> set_numb(0)
 * roperand == 0		--> failure()
 * roperand == -1		--> dif_numb(1)
 * loperand == roperand	--> set_numb(1) + dif_numb(1)
 * otherwise 			--> chg_numb(x)
 * @author yukimula
 *
 */
public class BXRDIVInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.binary_expression(expression.
				get_data_type(), COperator.arith_div, loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(!((Boolean) lconstant).booleanValue()) {
					output.put(graph.get_error_set().set_numb(expression, 0L), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0L) {
					output.put(graph.get_error_set().set_numb(expression, 0L), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(!((Boolean) rconstant).booleanValue()) {
					output.put(graph.get_error_set().failure(), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 0L) {
					output.put(graph.get_error_set().failure(), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) rconstant).longValue() == -1L) {
					output.put(graph.get_error_set().dif_addr(expression, 1L), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint, condition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		constraint = StateEvaluation.equal_with(loperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		
		constraint = StateEvaluation.equal_with(roperand, 0);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().failure(), constraints);
		
		constraint = StateEvaluation.equal_with(loperand, -1L);
		condition = StateEvaluation.equal_with(loperand, roperand);
		constraints = StateEvaluation.get_disjunctions();
		this.add_constraint(constraints, statement, constraint);
		this.add_constraint(constraints, statement, condition);
		output.put(graph.get_error_set().dif_numb(expression, 1), constraints);
		
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, condition);
		output.put(graph.get_error_set().set_numb(expression, 1), constraints);
		
		output.put(graph.get_error_set().chg_numb(expression), 
				StateEvaluation.get_conjunctions());
		
		return true;
	}

}
