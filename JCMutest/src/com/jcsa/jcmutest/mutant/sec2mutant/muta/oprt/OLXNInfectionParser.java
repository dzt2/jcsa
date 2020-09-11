package com.jcsa.jcmutest.mutant.sec2mutant.muta.oprt;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.proc.SetOperatorProcesses;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class OLXNInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		
		CirAssignStatement save1 = (CirAssignStatement) this.get_cir_node(
				mutation.get_location(), CirSaveAssignStatement.class, 0);
		CirAssignStatement save2 = (CirAssignStatement) this.get_cir_node(
				mutation.get_location(), CirSaveAssignStatement.class, 1);
		
		CirExpression loperand = save1.get_rvalue(), roperand = save2.get_rvalue();
		return SetOperatorProcesses.generate_infections(
					mutation, statement, expression, loperand, roperand, infection);
	}

}
