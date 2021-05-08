package com.jcsa.jcmutest.mutant.sym2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UNODCirMutationParser extends CirMutationParser {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}
	
	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		SymConstraint constraint; SymStateError state_error; SymbolExpression condition, muta_value;
		
		switch(mutation.get_operator()) {
		case delete_arith_neg:
		{
			condition = SymbolFactory.not_equals(expression, Integer.valueOf(0));
			constraint = SymInstances.expr_constraint(statement, condition, true);
			muta_value = SymbolFactory.arith_neg(expression);
			state_error = SymInstances.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case delete_bitws_rsv:
		{
			constraint = SymInstances.expr_constraint(statement, Boolean.TRUE, true);
			muta_value = SymbolFactory.bitws_rsv(expression);
			state_error = SymInstances.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case delete_logic_not:
		{
			constraint = SymInstances.expr_constraint(statement, Boolean.TRUE, true);
			muta_value = SymbolFactory.logic_not(expression);
			state_error = SymInstances.expr_error(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation);
		}
	}
	
}
