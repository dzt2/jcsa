package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UIOIInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_beg_statement(this.location);
	}

	@Override
	protected void generate_infections() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
