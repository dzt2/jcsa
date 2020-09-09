package com.jcsa.jcmutest.mutant.sec2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class STRPInfectionParser extends SecInfectionParser {
	
	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_beg_statement(this.location);
	}
	
	@Override
	protected void generate_infections() throws Exception {
		this.add_infection(
				SecFactory.assert_constraint(statement, Boolean.TRUE, true), 
				SecFactory.trap_statement(this.statement));
	}
	
}
