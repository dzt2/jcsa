package com.jcsa.jcmuta.mutant.error2mutation.infection.oaan;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * (+, *):
 * partial:
 * 	loperand = 0 --> set_numb(0)
 *  roperand = 0 --> set_numb(0)
 *  loperand = 1 --> dif_numb(-1)
 *  roperand = 1 --> dif_numb(-1)
 * @author yukimula
 *
 */
public class ADDMULInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		if(CTypeAnalyzer.is_boolean(type) || CTypeAnalyzer.is_number(type)) {
			return StateEvaluation.binary_expression(expression.
					get_data_type(), COperator.arith_mul, loperand, roperand);
		}
		else { 	return null; 	/* invalid type returns null */ }
	}
	
	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		/* lconstant == 0 || lconstant == 1 */
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(((Boolean) lconstant).booleanValue()) {
					output.put(graph.get_error_set().dif_numb(expression, -1L), StateEvaluation.get_conjunctions());
				}
				else {
					output.put(graph.get_error_set().set_numb(expression, 0L), StateEvaluation.get_conjunctions());
				}
				return true;
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0) {
					output.put(graph.get_error_set().set_numb(expression, 0L), StateEvaluation.get_conjunctions());
					return true;
				}
				else if(((Long) lconstant).longValue() == 1) {
					output.put(graph.get_error_set().dif_numb(expression, -1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
			else if(lconstant instanceof Double) {
				if(((Double) lconstant).doubleValue() == 0) {
					output.put(graph.get_error_set().set_numb(expression, 0L), StateEvaluation.get_conjunctions());
					return true;
				}
				else if(((Double) lconstant).doubleValue() == 1) {
					output.put(graph.get_error_set().dif_numb(expression, -1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
		}
		
		/* rconstant == 0 || rconstant == 1 */
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(((Boolean) rconstant).booleanValue()) {
					output.put(graph.get_error_set().dif_numb(expression, -1L), StateEvaluation.get_conjunctions());
				}
				else {
					output.put(graph.get_error_set().set_numb(expression, 0L), StateEvaluation.get_conjunctions());
				}
				return true;
			}
			else if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 0) {
					output.put(graph.get_error_set().set_numb(expression, 0L), StateEvaluation.get_conjunctions());
					return true;
				}
				else if(((Long) rconstant).longValue() == 1) {
					output.put(graph.get_error_set().dif_numb(expression, -1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
			else if(rconstant instanceof Double) {
				if(((Double) rconstant).doubleValue() == 0) {
					output.put(graph.get_error_set().set_numb(expression, 0L), StateEvaluation.get_conjunctions());
					return true;
				}
				else if(((Double) rconstant).doubleValue() == 1) {
					output.put(graph.get_error_set().dif_numb(expression, -1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
		}
		
		return false;	/** undecidable partially **/
	}
	
	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lconstraint, rconstraint; StateConstraints constraints;
		
		lconstraint = StateEvaluation.equal_with(loperand, 0L);
		rconstraint = StateEvaluation.equal_with(roperand, 0L);
		constraints = StateEvaluation.get_disjunctions();
		this.add_constraint(constraints, expression.statement_of(), lconstraint);
		this.add_constraint(constraints, expression.statement_of(), rconstraint);
		output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		
		lconstraint = StateEvaluation.not_equals(loperand, 0L);
		rconstraint = StateEvaluation.not_equals(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), lconstraint);
		this.add_constraint(constraints, expression.statement_of(), rconstraint);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		return true;
	}
	
}