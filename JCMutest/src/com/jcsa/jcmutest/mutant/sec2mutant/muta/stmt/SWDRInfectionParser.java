package com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt;

import java.util.Set;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

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
		SecDescription init_error;
		Set<CirStatement> statements = this.get_statements_in_body(mutation);
		switch(mutation.get_operator()) {
		case while_to_do_while:
		{
			init_error = this.add_statements(statements); break;
		}
		case do_while_to_while:
		{
			init_error = this.del_statements(statements); break;
		}
		default: throw new IllegalArgumentException(mutation.toString());
		}
		
		/* add the infection-pair */
		if(init_error == null) {
			return false;
		}
		else {
			return this.add_infection(constraint, init_error);
		}
	}

}
