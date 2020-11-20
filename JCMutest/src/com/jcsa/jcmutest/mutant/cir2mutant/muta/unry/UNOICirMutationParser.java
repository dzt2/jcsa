package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class UNOICirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(CirMutations mutations, CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		SymConstraint constraint; SymStateError state_error; SymExpression condition, muta_value;
		
		switch(mutation.get_operator()) {
		case insert_arith_neg:
		{
			condition = SymFactory.not_equals(expression, Integer.valueOf(0));
			constraint = mutations.expression_constraint(statement, condition, true);
			muta_value = SymFactory.arith_neg(expression.get_data_type(), expression);
			state_error = mutations.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case insert_bitws_rsv:
		{
			constraint = mutations.expression_constraint(statement, Boolean.TRUE, true);
			muta_value = SymFactory.bitws_rsv(expression.get_data_type(), expression);
			state_error = mutations.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case insert_logic_not:
		{
			constraint = mutations.expression_constraint(statement, Boolean.TRUE, true);
			muta_value = SymFactory.logic_not(expression);
			state_error = mutations.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case insert_abs_value:
		{
			condition = SymFactory.smaller_tn(expression, Integer.valueOf(0));
			constraint = mutations.expression_constraint(statement, condition, true);
			muta_value = SymFactory.arith_neg(expression.get_data_type(), expression);
			state_error = mutations.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case insert_nabs_value:
		{
			condition = SymFactory.greater_tn(expression, Integer.valueOf(0));
			constraint = mutations.expression_constraint(statement, condition, true);
			muta_value = SymFactory.arith_neg(expression.get_data_type(), expression);
			state_error = mutations.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}

}
