package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;

public class VINCInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = this.get_result_of(cir_tree, this.get_location(mutation));
		StateConstraints constraints = new StateConstraints(true);
		
		if(mutation.get_mutation_operator() == MutaOperator.inc_value) {
			long parameter = ((Long) mutation.get_parameter()).longValue();
			if(parameter != 0) {
				output.put(graph.get_error_set().dif_addr(expression, parameter), constraints);
			}
		}
		else if(mutation.get_mutation_operator() == MutaOperator.mul_value) {
			double parameter = ((Double) mutation.get_parameter()).doubleValue();
			SymExpression condition = SymFactory.parse(expression);
			condition = SymFactory.new_binary_expression(
					CBasicTypeImpl.bool_type, COperator.not_equals, 
					condition, SymFactory.new_constant(0L));
			condition = this.derive_sym_constraint(condition);
			constraints.add_constraint(expression.statement_of(), condition);
			
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
