package com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class STDLInfectionParser extends SecInfectionParser {
	
	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_beg_statement(location);
	}
	
	@Override
	protected void generate_infections() throws Exception {
		Set<CirStatement> statements = this.get_statements_in(location);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				init_errors.add(SecFactory.del_statement(statement));
			}
		}
		
		if(statements.isEmpty()) {
			if(statements.size() == 1) {
				this.add_infection(
						SecFactory.assert_constraint(statement, Boolean.TRUE, true), 
						init_errors.get(0));
			}
			else {
				this.add_infection(
						SecFactory.assert_constraint(statement, Boolean.TRUE, true), 
						SecFactory.conjunct(statement, init_errors));
			}
		}
	}
	
}
