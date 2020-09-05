package com.jcsa.jcmutest.selang.muta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class STDLInfectParser extends SedInfectParser {
	
	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().beg_statement(mutation.get_location());
	}
	
	private void collect_statements(CirTree cir_tree, 
			AstNode location, Set<CirStatement> statements) throws Exception {
		AstCirPair range = cir_tree.get_cir_range(location);
		if(range.executional()) {
			statements.add(range.get_beg_statement());
			statements.add(range.get_end_statement());
		}
		
		for(int k = 0; k < location.number_of_children(); k++) {
			this.collect_statements(cir_tree, location.get_child(k), statements);
		}
	}
	
	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		Set<CirStatement> statements = new HashSet<CirStatement>();
		this.collect_statements(cir_tree, mutation.get_location(), statements);
		
		List<SedDescription> init_errors = new ArrayList<SedDescription>();
		for(CirStatement cir_statement : statements) {
			if(!(cir_statement instanceof CirTagStatement)) {
				init_errors.add(SedFactory.del_statement(cir_statement));
			}
		}
		
		SedDescription constraint = SedFactory.condition_constraint(statement, 
						(SedExpression) SedFactory.fetch(Boolean.TRUE), true);
		SedDescription init_error = SedFactory.conjunct(statement, init_errors);
		infection.add_infection_pair(constraint, init_error);
	}
	
}
