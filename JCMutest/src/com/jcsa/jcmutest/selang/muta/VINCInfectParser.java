package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class VINCInfectParser extends SedInfectParser {

	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().beg_statement(mutation.get_location());
	}

	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		CirExpression expression = cir_tree.get_localizer().get_cir_value(mutation.get_location());
		SedDescription init_error, constraint = SedFactory.
				condition_constraint(statement, (SedExpression) SedFactory.fetch(Boolean.TRUE), true);
		if(expression != null) {
			switch(mutation.get_operator()) {
			case inc_constant:
			{
				init_error = SedFactory.app_expression(statement, expression, COperator.
						arith_add, (SedExpression) SedFactory.fetch(mutation.get_parameter()));
				break;
			}
			case mul_constant:
			{
				init_error = SedFactory.app_expression(statement, expression, COperator.
						arith_mul, (SedExpression) SedFactory.fetch(mutation.get_parameter())); 
				break;
			}
			default: throw new IllegalArgumentException("Invalid mutation: " + mutation.toString());
			}
			infection.add_infection_pair(constraint, init_error);
		}
	}

}
