package com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxo;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParsers;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class ORXNCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymCondition, SymCondition> infections) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) 
				this.get_cir_expression(cir_tree, mutation.get_location());
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirOperatorParsers.generate_infections(mutation, statement, expression, loperand, roperand, infections);
	}
	
}
