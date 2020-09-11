package com.jcsa.jcmutest.mutant.sec2mutant.muta.oprt;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.proc.SetOperatorProcesses;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class OLXNInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) mutation.get_location();
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		CirExpression loperand = this.get_cir_expression(location.get_loperand());
		CirExpression roperand = this.get_cir_expression(location.get_roperand());
		return SetOperatorProcesses.generate_infections(
					mutation, statement, expression, loperand, roperand, infection);
	}

}
