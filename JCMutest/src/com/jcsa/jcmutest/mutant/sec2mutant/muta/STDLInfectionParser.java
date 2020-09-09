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
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class STDLInfectionParser extends SecInfectionParser {

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
	
	@Override
	protected void generate_infections() throws Exception {
		Set<CirStatement> statements = new HashSet<CirStatement>();
		this.get_statements(mutation.get_location(), statements);
		
		SecConstraint constraint = 
				SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				init_errors.add(SecFactory.del_statement(statement));
			}
		}
		
		if(!init_errors.isEmpty()) {
			if(init_errors.size() == 1) {
				this.add_infection(constraint, init_errors.get(0));
			}
			else {
				SecDescription init_error = 
						SecFactory.conjunct(statement, init_errors);
				this.add_infection(constraint, init_error);
			}
		}
	}

}
