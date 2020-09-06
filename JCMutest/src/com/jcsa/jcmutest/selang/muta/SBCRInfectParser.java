package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SBCRInfectParser extends SedInfectParser {
	
	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().beg_statement(mutation.get_location());
	}
	
	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		boolean beg_or_end;
		switch(mutation.get_operator()) {
		case break_to_continue:	beg_or_end = true; 	break;
		case continue_to_break: beg_or_end = false; break;
		default: throw new IllegalArgumentException("Invalid: " + mutation);
		}
		AstNode location = mutation.get_location();
		
		while(location != null) {
			if(location instanceof AstDoWhileStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstForStatement) {
				CirStatement next_statement;
				if(beg_or_end) {
					next_statement = (CirStatement) cir_tree.get_localizer().
							get_cir_nodes(location, CirIfStatement.class).get(0);
				}
				else {
					next_statement = (CirStatement) cir_tree.get_localizer().
							get_cir_nodes(location, CirIfEndStatement.class).get(0);
				}
				
				SedDescription constraint, init_error;
				constraint = SedFactory.
						condition_constraint(statement, Boolean.TRUE, true);
				init_error = SedFactory.mut_statement(statement, next_statement);
				infection.add_infection_pair(constraint, init_error);
			}
			else {
				location = location.get_parent();
			}
		}
	}
	
}
