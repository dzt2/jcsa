package com.jcsa.jcmutest.selang.muta;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class TTRPInfectParser extends SedInfectParser {
	
	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return (CirStatement) cir_tree.get_cir_nodes(mutation.
				get_location(), CirIfStatement.class).get(0);
	}
	
	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		CirIfStatement if_statement = (CirIfStatement) statement;
		int times = ((Integer) mutation.get_parameter()).intValue();
		
		SedExpression condition = (SedExpression) SedFactory.parse(if_statement.get_condition());
		SedDescription lconst = SedFactory.condition_constraint(if_statement, condition, true);
		SedDescription rconst = SedFactory.execution_constraint(if_statement, times + 1);
		List<SedDescription> constraints = new ArrayList<SedDescription>();
		constraints.add(lconst); constraints.add(rconst);
		SedDescription constraint = SedFactory.conjunct(if_statement, constraints);
		
		infection.add_infection_pair(constraint, SedFactory.trp_statement(if_statement));
	}
	
}
