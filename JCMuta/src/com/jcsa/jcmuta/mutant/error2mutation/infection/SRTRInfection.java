package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;

public class SRTRInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression source = this.get_result_of(cir_tree, this.get_location(mutation));
		CirExpression target = this.get_result_of(
						cir_tree, this.get_location((AstNode) mutation.get_parameter()));
		
		SymExpression loperand = SymFactory.parse(source);
		SymExpression roperand = SymFactory.parse(target);
		SymExpression constraint = SymFactory.new_binary_expression(
				CBasicTypeImpl.bool_type, COperator.not_equals, loperand, roperand);
		constraint = this.derive_sym_constraint(constraint);
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(source.statement_of(), constraint);
		StateError error = graph.get_error_set().mut_expr(source);
		
		output.put(error, constraints);
	}

}
