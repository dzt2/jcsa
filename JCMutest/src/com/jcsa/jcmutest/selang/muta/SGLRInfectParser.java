package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SGLRInfectParser extends SedInfectParser {
	
	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().beg_statement(
				mutation.get_location().get_parent());
	}
	
	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		AstGotoStatement source = (AstGotoStatement) mutation.get_location().get_parent();
		AstLabeledStatement target = (AstLabeledStatement) ((AstNode) mutation.get_parameter()).get_parent();
		
		CirStatement source_statement = cir_tree.get_localizer().beg_statement(source);
		CirStatement target_statement = cir_tree.get_localizer().beg_statement(target);
		
		SedDescription constraint, init_error;
		constraint = SedFactory.condition_constraint(
				statement, (SedExpression) SedFactory.fetch(Boolean.TRUE), true);
		init_error = SedFactory.mut_statement(source_statement, target_statement);
		infection.add_infection_pair(constraint, init_error);
	}
	
}