package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UIOIInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_end_statement(this.location);
	}

	@Override
	protected void generate_infections() throws Exception {
		CirExpression expression = this.get_cir_value(location);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		
		switch(mutation.get_operator()) {
		case insert_prev_inc:
			if(expression.statement_of() != null)
				init_errors.add(SecFactory.add_expression(statement, expression, COperator.arith_add, Integer.valueOf(1)));
		case insert_post_inc:
			init_errors.add(SecFactory.uny_expression(statement, expression, COperator.increment)); break;
		case insert_prev_dec:
			init_errors.add(SecFactory.add_expression(statement, expression, COperator.arith_add, Integer.valueOf(-1)));
		case insert_post_dec:
			init_errors.add(SecFactory.uny_expression(statement, expression, COperator.decrement)); break;
		default: throw new IllegalArgumentException(this.mutation.toString());
		}
		
		if(init_errors.size() == 1) {
			this.add_infection(SecFactory.assert_constraint(statement, Boolean.TRUE, true), init_errors.get(0));
		}
		else {
			this.add_infection(SecFactory.assert_constraint(statement, Boolean.TRUE, true), SecFactory.conjunct(statement, init_errors));
		}
	}

}
