package com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
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
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		for(CirStatement loc_statement : statements) {
			if(!(loc_statement instanceof CirTagStatement)) {
				init_errors.add(SecFactory.del_statement(loc_statement));
			}
		}
		
		if(init_errors.isEmpty()) {
			return false;
		}
		else {
			SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
			if(init_errors.size() == 1) {
				this.add_infection(constraint, init_errors.get(0));
			}
			else {
				this.add_infection(constraint, this.conjunct(init_errors));
			}
			return true;
		}
	}

}
