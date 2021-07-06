package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymConditions;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VINCCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			Map<SymCondition, SymCondition> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		CirExecution execution = SymConditions.execution_of(statement);
		SymCondition constraint = SymConditions.cov_stmt(execution, 1);
		
		SymbolExpression muta_value; 
		switch(mutation.get_operator()) {
		case inc_constant:
		{
			muta_value = SymbolFactory.arith_add(expression.get_data_type(), expression, mutation.get_parameter());
			break;
		}
		case mul_constant:
		{
			muta_value = SymbolFactory.arith_mul(expression.get_data_type(), expression, mutation.get_parameter());
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		SymCondition init_error = SymConditions.mut_expr(expression, muta_value);
		
		infections.put(init_error, constraint);
	}

}
