package com.jcsa.jcmutest.mutant.cir2mutant.muta.refs;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymConditions;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;


public class VBRPCirMutationParser extends CirMutationParser {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}
	
	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			Map<SymCondition, SymCondition> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		SymCondition constraint; CirExecution execution = SymConditions.execution_of(statement);
		
		switch(mutation.get_operator()) {
		case set_true: 	
		{
			constraint = SymConditions.eva_expr(execution, expression, false);
			infections.put(SymConditions.mut_expr(expression, Boolean.TRUE), constraint);
			break;
		}
		case set_false:	
		{
			constraint = SymConditions.eva_expr(execution, expression, true);
			infections.put(SymConditions.mut_expr(expression, Boolean.FALSE), constraint);
			break;
		}
		default: 
		{
			throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		}
	}
	
}
