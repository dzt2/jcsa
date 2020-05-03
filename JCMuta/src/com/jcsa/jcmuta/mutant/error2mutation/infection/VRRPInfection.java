package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;

public class VRRPInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = this.get_result_of(
				cir_tree, this.get_location(mutation));
		CName parameter = (CName) mutation.get_parameter();
		
		SymExpression loperand = SymFactory.parse(expression);
		SymExpression roperand = SymFactory.new_address(parameter.get_name(), 
				tfactory.get_pointer_type(expression.get_data_type()));
		roperand = SymFactory.new_unary_expression(expression.
				get_data_type(), COperator.dereference, roperand);
		SymExpression constraint = SymFactory.new_binary_expression(
				CBasicTypeImpl.bool_type, COperator.not_equals, loperand, roperand);
		
		StateConstraints constraints = new StateConstraints(true);
		constraint = this.derive_sym_constraint(constraint);
		constraints.add_constraint(expression.statement_of(), constraint);
		StateError error = graph.get_error_set().chg_numb(expression);
		
		output.put(error, constraints);
	}

}
