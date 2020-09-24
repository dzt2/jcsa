package com.jcsa.jcmutest.mutant.sec2mutant.mut.oprt;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.StateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.proc.StateOperatorProcesses;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class OLXNStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_beg_statement(AstMutation mutation) throws Exception {
		return this.find_end_statement(mutation);
	}

	@Override
	protected CirStatement find_end_statement(AstMutation mutation) throws Exception {
		CirStatement statement = this.
				get_cir_expression(mutation.get_location()).statement_of();
		if(statement == null) 
			statement = this.get_end_statement(mutation.get_location());
		return statement;
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		
		CirAssignStatement save1 = (CirAssignStatement) this.get_cir_node(
				mutation.get_location(), CirSaveAssignStatement.class, 0);
		CirAssignStatement save2 = (CirAssignStatement) this.get_cir_node(
				mutation.get_location(), CirSaveAssignStatement.class, 1);
		
		CirExpression loperand = save1.get_rvalue(), roperand = save2.get_rvalue();
		return StateOperatorProcesses.generate_infections(
					mutation, statement, expression, loperand, roperand, this.state_mutation);
	}

}
