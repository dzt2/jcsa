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
 * roperand == -1		--> dif_numb(-1)
 * loperand == roperand	--> set_numb(0) + dif_numb(-1)
 * --> chg_numb(x)
 * 
 * @author yukimula
 *
 */
public class DIVBXRInfection extends OPRTInfection {

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
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == -1L) {
					output.put(graph.get_error_set().dif_numb(expression, -1L), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		/** roperand == -1 --> dif_numb(-1) **/
		constraint = StateEvaluation.equal_with(roperand, -1L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().dif_numb(expression, -1L), constraints);
		
		/** loperand == roperand	--> set_numb(0) + dif_numb(-1) **/
		constraint = StateEvaluation.equal_with(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		output.put(graph.get_error_set().dif_numb(expression, -1L), constraints);
		
		/** --> chg_numb(x) **/
		output.put(graph.get_error_set().chg_numb(expression), StateEvaluation.get_conjunctions());
		return true;
	}

}
