package com.jcsa.jcmutest.mutant.sec2mutant.muta.refs;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class VCRPInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_cir_value(location).statement_of();
	}

	@Override
	protected void generate_infections() throws Exception {
		CirExpression orig_expression = this.get_cir_value(this.location);
		SymExpression muta_expression = SymFactory.parse(mutation.get_parameter());
		
		SecConstraint constraint = SecFactory.assert_constraint(this.statement, 
				SymFactory.not_equals(orig_expression, muta_expression), true);
		SecDescription init_error = SecFactory.
				set_expression(this.statement, orig_expression, muta_expression);
		this.add_infection(constraint, init_error);
	}

}
