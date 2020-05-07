package com.jcsa.jcmuta.mutant.error2mutation.infection.incr;

import java.util.Map;

import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class VINCInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = this.get_result_of(cir_tree, this.get_location(mutation));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(mutation.get_mutation_operator() == MutaOperator.inc_value) {
			long parameter = ((Integer) mutation.get_parameter()).longValue();
			if(parameter != 0) {
				output.put(graph.get_error_set().dif_addr(expression, parameter), constraints);
			}
		}
		else if(mutation.get_mutation_operator() == MutaOperator.mul_value) {
			double parameter = ((Double) mutation.get_parameter()).doubleValue();
			SymExpression constraint = StateEvaluation.not_equals(expression, 0L);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			
			if(parameter == 0) {
				output.put(graph.get_error_set().set_numb(expression, 0.0), constraints);
			}
			else if(parameter == -1) {
				output.put(graph.get_error_set().neg_numb(expression), constraints);
			}
			else if(parameter > 1) {
				output.put(graph.get_error_set().inc_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().dec_numb(expression), constraints);
			}
		}
		else {
			throw new IllegalArgumentException(mutation.get_mutation_operator().toString());
		}
	}

}
