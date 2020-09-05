package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class VCRPInfectParser extends SedInfectParser {
	
	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().beg_statement(mutation.get_location());
	}
	
	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, 
			AstMutation mutation, SedInfection infection) throws Exception {
		CirExpression expression = 
				cir_tree.get_localizer().get_cir_value(mutation.get_location());
		SedExpression parameter = 
					(SedExpression) SedFactory.fetch(mutation.get_parameter());
		
		if(expression != null) {
			SedDescription constraint, init_error;
			constraint = SedFactory.condition_constraint(statement, 
							SedFactory.not_equals(expression, parameter), true);
			init_error = SedFactory.mut_expression(statement, expression, parameter);
			infection.add_infection_pair(constraint, init_error);
		}
	}
	
}
