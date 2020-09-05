package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class BTRPInfectParser extends SedInfectParser {

	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().end_statement(mutation.get_location());
	}

	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		AstExpression ast_expression = CTypeAnalyzer.
				get_expression_of((AstExpression) mutation.get_location());
		CirExpression cir_expression = 
					cir_tree.get_localizer().get_cir_value(ast_expression);
		if(cir_expression != null) {
			SedExpression condition; SedDescription constraint, init_error;
			condition = (SedExpression) SedFactory.parse(cir_expression);
			
			boolean assert_value;
			switch(mutation.get_operator()) {
			case trap_on_true:	assert_value = true;	break;
			case trap_on_false:	assert_value = false;	break;
			default: throw new IllegalArgumentException("Invalid: " + mutation);
			}
			
			constraint = SedFactory.
					condition_constraint(statement, condition, assert_value);
			init_error = SedFactory.trp_statement(statement);
			infection.add_infection_pair(constraint, init_error);
		}
	}
	
}
