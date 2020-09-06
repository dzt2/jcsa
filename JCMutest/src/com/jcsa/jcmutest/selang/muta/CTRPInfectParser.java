package com.jcsa.jcmutest.selang.muta;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class CTRPInfectParser extends SedInfectParser {

	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		while(location != null) {
			if(location instanceof AstSwitchStatement) {
				return (CirStatement) cir_tree.get_localizer().get_cir_nodes(
							location, CirSaveAssignStatement.class).get(0);
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not a switch statement");
	}

	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		AstNode parameter = (AstNode) mutation.get_parameter();
		while(parameter != null) {
			if(parameter instanceof AstCaseStatement) {
				CirCaseStatement case_statement = (CirCaseStatement) cir_tree.
						get_localizer().get_cir_nodes(parameter, CirCaseStatement.class).get(0);
				List<SedDescription> constraints = new ArrayList<SedDescription>();
				SedDescription constraint, init_error;
				
				constraints.add(SedFactory.condition_constraint(
								statement, case_statement.get_condition(), true));
				constraints.add(SedFactory.execution_constraint(case_statement, 1));
				constraint = SedFactory.conjunct(statement, constraints);
				
				init_error = SedFactory.trp_statement(case_statement);
				infection.add_infection_pair(constraint, init_error);
			}
			else {
				parameter = parameter.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in case study");
	}

}
