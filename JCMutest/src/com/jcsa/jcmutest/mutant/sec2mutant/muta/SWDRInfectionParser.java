package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sec2mutant.apis.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SWDRInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_location() throws Exception {
		return this.cir_tree.get_localizer().
				beg_statement(this.mutation.get_location());
	}
	
	private void get_statements(AstNode location, Set<CirStatement> statements) throws Exception {
		AstCirPair range = this.cir_tree.get_localizer().get_cir_range(location);
		if(range != null && range.executional()) {
			statements.add(range.get_beg_statement());
			statements.add(range.get_end_statement());
		}
		for(int k = 0; k < location.number_of_children(); k++) {
			this.get_statements(location.get_child(k), statements);
		}
	}
	
	private void while_to_do_while() throws Exception {
		AstWhileStatement location = (AstWhileStatement) mutation.get_location();
		CirIfStatement if_statement = (CirIfStatement) this.cir_tree.
				get_cir_nodes(mutation.get_location(), CirIfStatement.class).get(0);
		Set<CirStatement> statements = new HashSet<CirStatement>();
		this.get_statements(location, statements);
		
		SymExpression condition = SymFactory.parse(if_statement.get_condition());
		SecConstraint constraint = SecFactory.assert_constraint(statement, condition, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				init_errors.add(SecFactory.add_statement(statement));
			}
		}
		SecDescription init_error = SecFactory.conjunct(statement, init_errors);
		
		this.add_infection(constraint, init_error);
	}
	
	private void do_while_to_while() throws Exception {
		AstDoWhileStatement location = (AstDoWhileStatement) mutation.get_location();
		CirIfStatement if_statement = (CirIfStatement) this.cir_tree.
				get_cir_nodes(mutation.get_location(), CirIfStatement.class).get(0);
		Set<CirStatement> statements = new HashSet<CirStatement>();
		this.get_statements(location, statements);
		
		SymExpression condition = SymFactory.parse(if_statement.get_condition());
		SecConstraint constraint = SecFactory.assert_constraint(statement, condition, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				init_errors.add(SecFactory.del_statement(statement));
			}
		}
		SecDescription init_error = SecFactory.conjunct(statement, init_errors);
		
		this.add_infection(constraint, init_error);
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
