package com.jcsa.jcmutest.mutant.sec2mutant.muta.oprt;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.proc.SetOperatorProcesses;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class OEXAInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return (CirStatement) this.get_cir_node(mutation.get_location(), CirBinAssignStatement.class);
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirAssignStatement assign_stmt = (CirAssignStatement) this.
				get_cir_node(mutation.get_location(), CirBinAssignStatement.class);
		CirExpression expression = assign_stmt.get_rvalue();
		CirExpression loperand = assign_stmt.get_lvalue();
		CirExpression roperand = assign_stmt.get_rvalue();
		return SetOperatorProcesses.generate_infections(mutation, 
				statement, expression, loperand, roperand, infection);
	}

}
