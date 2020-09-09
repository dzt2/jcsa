package com.jcsa.jcmutest.mutant.sec2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class BTRPInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_end_statement(this.location);
	}

	@Override
	protected void generate_infections() throws Exception {
		CirExpression expression = this.get_cir_value(location);
		boolean value;
		switch(this.mutation.get_operator()) {
		case trap_on_true:	value = true; 	break;
		case trap_on_false:	value = false;	break;
		default: throw new IllegalArgumentException(this.mutation.toString());
		}
		
		this.add_infection(
				SecFactory.assert_constraint(this.statement, expression, value), 
				SecFactory.trap_statement(this.statement));
	}

}
