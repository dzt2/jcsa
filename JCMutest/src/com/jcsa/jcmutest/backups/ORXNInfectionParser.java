package com.jcsa.jcmutest.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class ORXNInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) 
				this.get_cir_expression(mutation.get_location());
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		return SetOperatorProcesses.generate_infections(mutation, 
				statement, expression, loperand, roperand, infection);
	}

}
