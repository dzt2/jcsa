package com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SGLRInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_beg_statement(location.get_parent());
	}

	@Override
	protected void generate_infections() throws Exception {
		AstNode source_location = this.location.get_parent();
		AstNode target_location = (AstNode) this.mutation.get_parameter();
		
		CirStatement source = (CirStatement) this.get_cir_nodes(
				source_location, CirGotoStatement.class).get(0);
		CirStatement target = (CirStatement) this.get_cir_nodes(
				target_location, CirLabelStatement.class).get(0);
		
		this.add_infection(
				SecFactory.assert_constraint(statement, Boolean.TRUE, true), 
				SecFactory.set_statement(source, target));
	}

}
