package com.jcsa.jcmuta.mutant.error2mutation.infection.oabn;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * loperand == 0	--> neg_numb(expr)
 * roperand == 0	--> equivalent
 * loperand == -1	--> equivalent
 * roperand == -1	--> rsv_numb(expr)
 * loperand & roperand != roperand	--> chg_numb(expr)
 * @author yukimula
 *
 */
public class SUBBXRInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(type) || CTypeAnalyzer.is_integer(type)) {
			return StateEvaluation.binary_expression(expression.
					get_data_type(), COperator.bit_xor, loperand, roperand);
		}
		else { 	return null; 	/* invalid type returns null */ }
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(!((Boolean) lconstant).booleanValue()) {
					output.put(graph.get_error_set().neg_numb(expression), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0) {
					output.put(graph.get_error_set().neg_numb(expression), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) lconstant).longValue() == -1) {
					return true;	/** equivalent mutant detected **/
				}
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(!((Boolean) rconstant).booleanValue()) {
					return true;	/** equivalent mutant detected **/
				}
			}
			else if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 0) {
					return true;	/** equivalent mutant detected **/
				}
				else if(((Long) rconstant).longValue() == -1) {
					output.put(graph.get_error_set().rsv_numb(expression), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition, rcondition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		/** loperand == 0	--> neg_numb(expr) **/
		lcondition = StateEvaluation.equal_with(loperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		output.put(graph.get_error_set().neg_numb(expression), constraints);
		
		/** roperand == -1	--> rsv_numb(expr) **/
		rcondition = StateEvaluation.equal_with(roperand, -1L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().rsv_numb(expression), constraints);
		
		/** loperand & roperand != roperand	--> chg_numb(expr) **/
		lcondition = StateEvaluation.binary_expression(expression.get_data_type(), 
				COperator.bit_and, loperand, roperand);
		rcondition = StateEvaluation.not_equals(lcondition, StateEvaluation.get_symbol(roperand));
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		return true;
	}

}
