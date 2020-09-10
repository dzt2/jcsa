package com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
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
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		switch(mutation.get_operator()) {
		case while_to_do_while:
		{
			for(CirStatement body_statement : statements) {
				if(!(body_statement instanceof CirTagStatement)) {
					init_errors.add(SecFactory.add_statement(body_statement));
				}
			}
			break;
		}
		case do_while_to_while:
		{
			for(CirStatement body_statement : statements) {
				if(!(body_statement instanceof CirTagStatement)) {
					init_errors.add(SecFactory.del_statement(body_statement));
				}
			}
			break;
		}
		default: throw new IllegalArgumentException(mutation.toString());
		}
		
		/* add the infection-pair */
		if(init_errors.isEmpty()) {
			return false;
		}
		else if(init_errors.size() == 1) {
			this.add_infection(constraint, init_errors.get(0));
		}
		else {
			this.add_infection(constraint, this.conjunct(init_errors));
		}
		return true;
	}

}
