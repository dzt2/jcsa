package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class CTRPInfectParser extends SedInfectParser {

	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		AstNode location = (AstNode) mutation.get_parameter();
		while(location != null) {
			if(location instanceof AstCaseStatement) {
				return (CirCaseStatement) cir_tree.get_localizer().
						get_cir_nodes(location, CirCaseStatement.class).get(0);
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not the case-statement");
	}

	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		CirCaseStatement case_statement = (CirCaseStatement) statement;
		SedExpression condition = (SedExpression) 
						SedFactory.parse(case_statement.get_condition());
		SedDescription constraint, init_error;
		
		constraint = SedFactory.condition_constraint(statement, condition, true);
		init_error = SedFactory.trp_statement(case_statement);
		infection.add_infection_pair(constraint, init_error);
	}

}
