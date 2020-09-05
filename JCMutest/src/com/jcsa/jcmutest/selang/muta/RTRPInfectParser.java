package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class RTRPInfectParser extends SedInfectParser {

	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().beg_statement(mutation.get_location());
	}

	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		CirExpression source_expression = cir_tree.
				get_localizer().get_cir_value(mutation.get_location());
		CirExpression target_expression = cir_tree.get_localizer().
				get_cir_value((AstNode) mutation.get_parameter());
		
		SedDescription constraint, init_error;
		constraint = SedFactory.condition_constraint(statement, SedFactory.
				not_equals(source_expression, target_expression), true);
		init_error = SedFactory.mut_expression(statement, source_expression, 
				(SedExpression) SedFactory.parse(target_expression));
		infection.add_infection_pair(constraint, init_error);
	}

}
