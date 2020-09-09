package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcmutest.mutant.sec2mutant.apis.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SBCRInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_location() throws Exception {
		return (CirStatement) this.cir_tree.get_cir_nodes(
				mutation.get_location(), CirGotoStatement.class).get(0);
	}

	@Override
	protected void generate_infections() throws Exception {
		/* find the loop-statement and determine next-target */
		AstNode location = this.mutation.get_location();
		CirStatement target_statement = null;
		while(location != null) {
			if(location instanceof AstDoWhileStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstForStatement) {
				if(mutation.get_operator() == MutaOperator.break_to_continue) {
					target_statement = (CirStatement) this.cir_tree.
							get_cir_nodes(location, CirIfStatement.class).get(0);
					break;
				}
				else if(mutation.get_operator() == MutaOperator.continue_to_break) {
					target_statement = (CirStatement) this.cir_tree.
							get_cir_nodes(location, CirIfEndStatement.class).get(0);
					break;
				}
				else {
					throw new IllegalArgumentException(this.mutation.toString());
				}
			}
			else {
				location = location.get_parent();
			}
		}
		if(target_statement == null) {
			throw new IllegalArgumentException("Not in loop-structure");
		}
		CirExecution target = this.cir_tree.get_localizer().get_execution(target_statement);
		
		/* true ==> set_stmt(source, target) */
		CirExecution source = this.cir_tree.get_localizer().get_execution(statement);
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		SecDescription init_error = 
					SecFactory.set_statement(source.get_statement(), target.get_statement());
		this.add_infection(constraint, init_error);
	}

}
