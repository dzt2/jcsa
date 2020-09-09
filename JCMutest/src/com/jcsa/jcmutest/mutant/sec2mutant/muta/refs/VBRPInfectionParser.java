package com.jcsa.jcmutest.mutant.sec2mutant.muta.refs;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class VBRPInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_cir_value(location).statement_of();
	}

	@Override
	protected void generate_infections() throws Exception {
		CirExpression expression = this.get_cir_value(this.location);
		boolean parameter;
		switch(this.mutation.get_operator()) {
		case set_true:	parameter = false;	break;
		case set_false:	parameter = true;	break;
		default: throw new IllegalArgumentException(this.mutation.toString());
		}
		
		SecConstraint constraint = SecFactory.
				assert_constraint(this.statement, expression, !parameter);
		SecDescription init_error = SecFactory.set_expression(
				this.statement, expression, Boolean.valueOf(parameter));
		this.add_infection(constraint, init_error);
	}

}
