package com.jcsa.jcmutest.backups;

import java.util.Set;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class SWDRInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return (CirStatement) this.get_cir_node(mutation.get_location(), CirIfStatement.class);
	}
	
	private Set<CirStatement> get_statements_in_body(AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		if(location instanceof AstWhileStatement) {
			return this.get_statements_in(((AstWhileStatement) location).get_body());
		}
		else if(location instanceof AstDoWhileStatement) {
			return this.get_statements_in(((AstDoWhileStatement) location).get_body());
		}
		else {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
	}
	
	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		/* constraint: condition = false */
		CirIfStatement if_statement = 
				(CirIfStatement) get_cir_node(mutation.get_location(), CirIfStatement.class);
		SecConstraint constraint = this.get_constraint(if_statement.get_condition(), false);
		
		/* initial errors by operator */
		Set<CirStatement> statements = this.get_statements_in_body(mutation);
		int counter = 0;
		for(CirStatement element : statements) {
			if(!(element instanceof CirTagStatement)) {
				if(mutation.get_operator() == MutaOperator.while_to_do_while) {
					this.add_infection(constraint, this.add_statement(element));
				}
				else if(mutation.get_operator() == MutaOperator.do_while_to_while) {
					this.add_infection(constraint, this.del_statement(element));
				}
				else {
					throw new IllegalArgumentException(mutation.toString());
				}
				counter++;
			}
		}
		return counter > 0;
	}

}
