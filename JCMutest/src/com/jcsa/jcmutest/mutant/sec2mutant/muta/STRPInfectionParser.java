package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import com.jcsa.jcmutest.mutant.sec2mutant.apis.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class STRPInfectionParser extends SecInfectionParser {
	
	@Override
	protected CirStatement get_location() throws Exception {
		return this.cir_tree.get_localizer().
				beg_statement(this.mutation.get_location());
	}
	
	@Override
	protected void generate_infections() throws Exception {
		SecConstraint constraint = SecFactory.
				assert_constraint(statement, Boolean.TRUE, true);
		SecDescription init_error = SecFactory.trap_statement(statement);
		this.add_infection(constraint, init_error);
	}
	
}
