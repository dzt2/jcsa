package com.jcsa.jcmutest.mutant.sec2mutant.muta.refs;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class VCRPInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		SecConstraint constraint; SecDescription init_error;
		CirExpression orig_expression = this.get_cir_expression(mutation.get_location());
		SymExpression muta_expression = SymFactory.parse(mutation.get_parameter());
		
		constraint = this.get_constraint(this.sym_condition(COperator.
				not_equals, orig_expression, muta_expression), true);
		init_error = this.set_expression(orig_expression, muta_expression);
		this.add_infection(constraint, init_error); return true;
	}

}
