package com.jcsa.jcmutest.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class ETRPStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_beg_statement(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected CirStatement find_end_statement(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		this.add_infection(
				this.get_constraint(Boolean.TRUE, true), 
				this.trap_statement(statement));
		return true;
	}

}
