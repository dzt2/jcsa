package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import com.jcsa.jcmutest.mutant.sec2mutant.apis.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class BTRPInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_location() throws Exception {
		return this.cir_tree.get_localizer().
				end_statement(mutation.get_location());
	}

	@Override
	protected void generate_infections() throws Exception {
		SymExpression expression = SymFactory.
				parse(this.mutation.get_location());
		boolean value;
		switch(this.mutation.get_operator()) {
		case trap_on_true:	value = true;	break;
		case trap_on_false:	value = false;	break;
		default: throw new IllegalArgumentException(this.mutation.toString());
		}
		
		SecConstraint constraint = 
				SecFactory.assert_constraint(this.statement, expression, value);
		SecDescription state_error = SecFactory.trap_statement(this.statement);
		this.add_infection(constraint, state_error);
	}

}
