package com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt;

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
	
	@Override
	protected CirStatement get_statement() throws Exception {
		return (CirStatement) this.get_cir_nodes(
					this.location, CirGotoStatement.class).get(0);
	}
	
	@Override
	protected void generate_infections() throws Exception {
		CirStatement source = this.statement;
		AstNode location = this.location; 
		CirStatement target = null;
		while(location != null) {
			if(location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement
				|| location instanceof AstForStatement) {
				if(mutation.get_operator() == MutaOperator.break_to_continue) {
					target = (CirStatement) this.get_cir_nodes(
							location, CirIfStatement.class).get(0);
				}
				else if(mutation.get_operator() == MutaOperator.continue_to_break) {
					target = (CirStatement) this.get_cir_nodes(
							location, CirIfEndStatement.class).get(0);
				}
				else {
					throw new IllegalArgumentException(this.mutation.toString());
				}
				break;
			}
			else {
				location = location.get_parent();
			}
		}
		
		this.add_infection(
				SecFactory.assert_constraint(source, Boolean.TRUE, true), 
				SecFactory.set_statement(source, target));
	}
	
}
