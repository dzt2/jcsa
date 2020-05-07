package com.jcsa.jcmuta.mutant.error2mutation.infection.incr;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;

public class UNOIInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = this.get_result_of(cir_tree, this.get_location(mutation));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		switch(mutation.get_mutation_operator()) {
		case insert_arith_neg:
		{
			SymExpression loperand = SymFactory.parse(expression);
			SymExpression roperand = SymFactory.new_constant(0L);
			SymExpression condition = SymFactory.new_binary_expression(
					CBasicTypeImpl.bool_type, COperator.not_equals, loperand, roperand);
			constraints.add_constraint(expression.statement_of(), condition);
			
			output.put(graph.get_error_set().neg_numb(expression), constraints);
		}
		break;
		case insert_bitws_rsv:
		{
			output.put(graph.get_error_set().rsv_numb(expression), constraints);
		}
		break;
		case insert_logic_not:
		{
			output.put(graph.get_error_set().chg_bool(expression), constraints);
		}
		break;
		case insert_abs:
		{
			this.add_constraint(constraints, expression.statement_of(), 
					StateEvaluation.smaller_tn(expression, 0L));
			output.put(graph.get_error_set().neg_numb(expression), constraints);
		}
		break;
		case insert_neg_abs:
		{
			this.add_constraint(constraints, expression.statement_of(), 
					StateEvaluation.greater_tn(expression, 0L));
			output.put(graph.get_error_set().neg_numb(expression), constraints);
		}
		break;
		default: throw new IllegalArgumentException(mutation.get_mutation_operator().toString());
		}
		
	}

}
