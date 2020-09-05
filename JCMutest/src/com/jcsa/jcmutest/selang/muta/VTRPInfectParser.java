package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class VTRPInfectParser extends SedInfectParser {

	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().end_statement(mutation.get_location());
	}

	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		CirExpression expression = cir_tree.get_localizer().get_cir_value(mutation.get_location());
		if(expression != null) {
			SedExpression condition, parameter; SedDescription constraint;
			switch(mutation.get_operator()) {
			case trap_on_pos:
			{
				condition = SedFactory.greater_tn(expression, Integer.valueOf(0));
				break;
			}
			case trap_on_neg:
			{
				condition = SedFactory.smaller_tn(expression, Integer.valueOf(0));
				break;
			}
			case trap_on_zro:
			{
				condition = SedFactory.equal_with(expression, Integer.valueOf(0));
				break;
			}
			case trap_on_dif:
			{
				if(mutation.get_parameter() instanceof String) {
					parameter = SedFactory.id_expression(
							expression.get_data_type(), mutation.get_parameter().toString());
				}
				else {
					parameter = (SedExpression) SedFactory.fetch(mutation.get_parameter());
				}
				condition = (SedExpression) SedFactory.parse(expression);
				condition = SedFactory.not_equals(condition, parameter);
				break;
			}
			default: throw new IllegalArgumentException("Invalid: " + mutation);
			}
			constraint = SedFactory.condition_constraint(statement, condition, true);
			infection.add_infection_pair(constraint, SedFactory.trp_statement(statement));
		}
	}

}
