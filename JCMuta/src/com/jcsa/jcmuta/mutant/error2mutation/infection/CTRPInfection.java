package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;

/**
 * trap_on_case(expression, val)
 * @author yukimula
 *
 */
public class CTRPInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		AstExpression condition = (AstExpression) this.get_location(mutation);
		AstExpression case_value = (AstExpression) 
					this.get_location((AstMutation) mutation.get_parameter());
		
		CirStatement statement = this.get_beg_statement(cir_tree, condition);
		CirExpression loperand = this.get_result_of(cir_tree, condition);
		CirExpression roperand = this.get_result_of(cir_tree, case_value);
		
		SymExpression lop = SymFactory.parse(loperand);
		SymExpression rop = SymFactory.parse(roperand);
		SymExpression constraint = SymFactory.new_binary_expression(
				CBasicTypeImpl.bool_type, COperator.equal_with, lop, rop);
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(statement, constraint);
		StateError error = graph.get_error_set().failure();
		
		output.put(error, constraints);
	}

}
