package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class VINCInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_cir_value(this.location).statement_of();
	}

	@Override
	protected void generate_infections() throws Exception {
		CirExpression orig_expression = this.get_cir_value(this.location);
		CirStatement statement = orig_expression.statement_of();
		SecConstraint constraint = 
				SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		Object parameter = this.mutation.get_parameter();
		
		switch(this.mutation.get_operator()) {
		case inc_constant:
		{
			this.add_infection(constraint, SecFactory.add_expression(statement, 
				orig_expression, COperator.arith_add, SymFactory.parse(parameter)));
			break;
		}
		case mul_constant:
		{
			this.add_infection(constraint, SecFactory.add_expression(statement, 
				orig_expression, COperator.arith_mul, SymFactory.parse(parameter)));
			break;
		}
		default: throw new IllegalArgumentException(this.mutation.toString());
		}
		
	}

}
