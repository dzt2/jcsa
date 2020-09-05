package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UNOIInfectParser extends SedInfectParser {

	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().beg_statement(mutation.get_location());
	}

	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, 
			AstMutation mutation, SedInfection infection) throws Exception {
		CirExpression expression = 
				cir_tree.get_localizer().get_cir_value(mutation.get_location());
		SedDescription constraint, init_error; SedExpression condition;
		if(expression != null) {
			switch(mutation.get_operator()) {
			case insert_arith_neg:
			{
				condition = SedFactory.not_equals(expression, Integer.valueOf(0));
				constraint = SedFactory.condition_constraint(statement, condition, true);
				init_error = SedFactory.nev_expression(statement, expression, COperator.negative);
				break;
			}
			case insert_bitws_rsv:
			{
				constraint = SedFactory.condition_constraint(statement, 
						(SedExpression) SedFactory.fetch(Boolean.TRUE), true);
				init_error = SedFactory.nev_expression(statement, expression, COperator.bit_not);
				break;
			}
			case insert_logic_not:
			{
				constraint = SedFactory.condition_constraint(statement, 
						(SedExpression) SedFactory.fetch(Boolean.TRUE), true);
				init_error = SedFactory.nev_expression(statement, expression, COperator.logic_not);
				break;
			}
			case insert_abs_value:
			{
				condition = SedFactory.greater_tn(expression, Integer.valueOf(0));
				constraint = SedFactory.condition_constraint(statement, condition, true);
				init_error = SedFactory.nev_expression(statement, expression, COperator.negative);
				break;
			}
			case insert_nabs_value:
			{
				condition = SedFactory.smaller_tn(expression, Integer.valueOf(0));
				constraint = SedFactory.condition_constraint(statement, condition, true);
				init_error = SedFactory.nev_expression(statement, expression, COperator.negative);
				break;
			}
			default: throw new IllegalArgumentException("Invalid: " + mutation.toString());
			}
			infection.add_infection_pair(constraint, init_error);
		}
	}

}
