package com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SBCRInfectionParser extends SecInfectionParser {
	
	private CirStatement get_source(AstMutation mutation) throws Exception {
		return (CirStatement) this.
				get_cir_node(mutation.get_location(), CirGotoStatement.class);
	}
	
	private CirStatement get_target(AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		while(location != null) {
			if(location instanceof AstForStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement) {
				if(mutation.get_operator() == MutaOperator.break_to_continue) {
					return (CirStatement) this.get_cir_node(location, CirIfStatement.class);
				}
				else if(mutation.get_operator() == MutaOperator.continue_to_break) {
					return (CirStatement) this.get_cir_node(location, CirIfEndStatement.class);
				}
				else {
					throw new IllegalArgumentException(mutation.toString());
				}
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in loop structure");
	}
	
	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_source(mutation);
	}
	
	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirStatement source = this.get_source(mutation);
		CirStatement target = this.get_target(mutation);
		this.add_infection(
				this.get_constraint(Boolean.TRUE, true), 
				SecFactory.set_statement(source, target));
		return true;
	}

}
