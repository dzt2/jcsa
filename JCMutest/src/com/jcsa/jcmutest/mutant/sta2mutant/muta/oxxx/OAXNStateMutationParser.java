package com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateOperatorParsers;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class OAXNStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		CirComputeExpression expression = (CirComputeExpression)
				this.get_cir_expression(mutation.get_location());
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		StateOperatorParsers.parse(mutation, this.get_r_execution(), 
				expression, loperand, roperand, this.infection_map);
	}

}
