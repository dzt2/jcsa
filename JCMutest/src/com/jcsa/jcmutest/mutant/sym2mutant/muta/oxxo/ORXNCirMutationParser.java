package com.jcsa.jcmutest.mutant.sym2mutant.muta.oxxo;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.oprt.CirSetOperatorParsers;
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
	protected void generate_infections(CirMutations mutations, CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) 
				this.get_cir_expression(cir_tree, mutation.get_location());
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirSetOperatorParsers.generate_infections(mutation, 
				statement, expression, loperand, roperand, mutations, infections);
	}

}
