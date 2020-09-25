package com.jcsa.jcmutest.backups;

import java.util.Set;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class STDLStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_beg_statement(AstMutation mutation) throws Exception {
		return this.get_beg_statement(mutation.get_location());
	}

	@Override
	protected CirStatement find_end_statement(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		Set<CirStatement> statements = this.get_statements_in(mutation.get_location());
		int counter = 0; 
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		for(CirStatement element : statements) {
			if(!(element instanceof CirTagStatement)) {
				this.add_infection(constraint, this.del_statement(element));
				counter++;
			}
		}
		return counter > 0;
	}

}
