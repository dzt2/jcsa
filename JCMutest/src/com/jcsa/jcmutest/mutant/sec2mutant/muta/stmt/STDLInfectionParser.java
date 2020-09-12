package com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt;

import java.util.Set;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class STDLInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_beg_statement(mutation.get_location());
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		Set<CirStatement> statements = this.get_statements_in(mutation.get_location());
		SecDescription constraint = this.get_constraint(Boolean.TRUE, true), init_error;
		init_error = this.del_statements(statements);
		if(init_error == null) {
			return false;
		}
		else {
			return this.add_infection(constraint, init_error);
		}
	}

}
