package com.jcsa.jcmuta.mutant.error2mutation.infection.oabn;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * too few bit-1	--> equivalent
 * too many bits	--> set_numb by mask
 * 
 * loperand & roperand != 0			--> chg_numb
 * loperand == -1 or roperand == -1	--> set_numb
 * @author yukimula
 *
 */
public class ADDBORInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(type) || CTypeAnalyzer.is_integer(type)) {
			return StateEvaluation.binary_expression(expression.
					get_data_type(), COperator.arith_mod, loperand, roperand);
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
				return true;	/** equivalent because not masked **/
			}
			else if(lconstant instanceof Long) {
				long value = ((Long) lconstant).longValue();
				if(this.number_of_bit1(value) <= StateInfection.min_bitwise) {
					return true;	/** equivalent because not masked **/
				}
				else if(this.number_of_bit1(value) >= StateInfection.max_bitwise) {
					output.put(graph.get_error_set().set_numb(expression, value), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				return true;	/** equivalent because not masked **/
			}
			else if(rconstant instanceof Long) {
				long value = ((Long) rconstant).longValue();
				if(this.number_of_bit1(value) <= StateInfection.min_bitwise) {
					return true;	/** equivalent because not masked **/
				}
				else if(this.number_of_bit1(value) >= StateInfection.max_bitwise) {
					output.put(graph.get_error_set().set_numb(expression, value), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		return false;	/** unable to decide the mutation partially **/
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition, rcondition; StateConstraints constraints;
		
		/* loperand == -1 or roperand == -1 --> set_numb(-1) */
		lcondition = StateEvaluation.equal_with(loperand, -1L);
		rcondition = StateEvaluation.equal_with(roperand, -1L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), lcondition);
		this.add_constraint(constraints, expression.statement_of(), rcondition);
		output.put(graph.get_error_set().set_numb(expression, -1L), constraints);
		
		/* loperand & roperand != 0 */
		lcondition = StateEvaluation.binary_expression(
				expression.get_data_type(), COperator.bit_and, loperand, roperand);
		rcondition = StateEvaluation.not_equals(lcondition, StateEvaluation.new_constant(0L));
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), rcondition);
		output.put(graph.get_error_set().dec_numb(expression), constraints);
		
		return true;
	}

}
