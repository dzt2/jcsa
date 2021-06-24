package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
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
			AstMutation mutation, Map<SymCondition, SymCondition> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		SymCondition constraint; SymCondition state_error; SymbolExpression condition, muta_value;
		CirExecution execution = CirMutations.execution_of(statement);
		
		switch(mutation.get_operator()) {
		case delete_arith_neg:
		{
			condition = SymbolFactory.not_equals(expression, Integer.valueOf(0));
			constraint = CirMutations.eva_expr(execution, condition);
			muta_value = SymbolFactory.arith_neg(expression);
			state_error = CirMutations.mut_expr(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case delete_bitws_rsv:
		{
			constraint = CirMutations.cov_stmt(execution, 1);
			muta_value = SymbolFactory.bitws_rsv(expression);
			state_error = CirMutations.mut_expr(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		case delete_logic_not:
		{
			constraint = CirMutations.cov_stmt(execution, 1);
			muta_value = SymbolFactory.logic_not(expression);
			state_error = CirMutations.mut_expr(expression, muta_value);
			infections.put(state_error, constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation);
		}
	}
	
}
