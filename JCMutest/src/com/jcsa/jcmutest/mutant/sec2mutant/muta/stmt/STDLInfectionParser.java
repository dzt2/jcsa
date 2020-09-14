package com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt;

import java.util.Set;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class STDLInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_beg_statement(mutation.get_location());
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
