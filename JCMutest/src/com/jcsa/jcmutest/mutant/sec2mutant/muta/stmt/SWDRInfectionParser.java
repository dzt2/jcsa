package com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SWDRInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_beg_statement(this.location);
	}
	
	private void while_to_do_while() throws Exception {
		CirIfStatement if_statement = (CirIfStatement) this.
				get_cir_nodes(location, CirIfStatement.class).get(0);
		SymExpression condition = 
					SymFactory.parse(if_statement.get_condition());
		
		Set<CirStatement> statements = this.get_statements_in(
					((AstWhileStatement) location).get_body());
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				init_errors.add(SecFactory.add_statement(statement));
			}
		}
		
		if(!init_errors.isEmpty()) {
			SecConstraint constraint = SecFactory.
					assert_constraint(if_statement, condition, false);
			if(init_errors.size() == 1) {
				this.add_infection(constraint, init_errors.get(0));
			}
			else {
				this.add_infection(constraint, SecFactory.
						conjunct(if_statement, init_errors));
			}
		}
	}
	
	private void do_while_to_while() throws Exception {
		CirIfStatement if_statement = (CirIfStatement) this.
				get_cir_nodes(location, CirIfStatement.class).get(0);
		SymExpression condition = 
					SymFactory.parse(if_statement.get_condition());
		
		Set<CirStatement> statements = this.get_statements_in(
					((AstDoWhileStatement) location).get_body());
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				init_errors.add(SecFactory.del_statement(statement));
			}
		}
		
		if(!init_errors.isEmpty()) {
			SecConstraint constraint = SecFactory.
					assert_constraint(if_statement, condition, false);
			if(init_errors.size() == 1) {
				this.add_infection(constraint, init_errors.get(0));
			}
			else {
				this.add_infection(constraint, SecFactory.
						conjunct(if_statement, init_errors));
			}
		}
	}
	
	@Override
	protected void generate_infections() throws Exception {
		switch(this.mutation.get_operator()) {
		case while_to_do_while:	this.while_to_do_while(); break;
		case do_while_to_while:	this.do_while_to_while(); break;
		default: throw new IllegalArgumentException(this.mutation.toString());
		}
	}

}
