package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class UNODCirMutationParser extends CirMutationParser {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}
	
	@Override
	protected void generate_infections(CirMutations mutations, CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirStateError, CirConstraint> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		CirConstraint constraint; CirStateError state_error; SymExpression condition, muta_value;
		
		switch(mutation.get_operator()) {
		case delete_arith_neg:
		{
			condition = SymFactory.not_equals(expression, Integer.valueOf(0));
			constraint = mutations.expression_constraint(statement, condition, true);
			muta_value = SymFactory.arith_neg(expression.get_data_type(), expression);
			state_error = mutations.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case delete_bitws_rsv:
		{
			constraint = mutations.expression_constraint(statement, Boolean.TRUE, true);
			muta_value = SymFactory.bitws_rsv(expression.get_data_type(), expression);
			state_error = mutations.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case delete_logic_not:
		{
			constraint = mutations.expression_constraint(statement, Boolean.TRUE, true);
			muta_value = SymFactory.logic_not(expression);
			state_error = mutations.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation);
		}
	}
	
}
