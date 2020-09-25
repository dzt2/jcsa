package com.jcsa.jcmutest.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SGLRInfectionParser extends SecInfectionParser {
	
	private CirStatement get_source(AstMutation mutation) throws Exception {
		AstGotoStatement location = 
				(AstGotoStatement) mutation.get_location().get_parent();
		return (CirStatement) this.get_cir_node(location, CirGotoStatement.class);
	}
	
	private CirStatement get_target(AstMutation mutation) throws Exception {
		AstLabeledStatement location = (AstLabeledStatement) 
				((AstNode) mutation.get_parameter()).get_parent();
		return (CirStatement) this.get_cir_node(location, CirLabelStatement.class);
	}
	
	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_source(mutation);
	}
	
	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirStatement source = this.get_source(mutation);
		CirStatement target = this.get_target(mutation);
		this.add_infection(
				this.get_constraint(Boolean.TRUE, true), 
				this.set_statement(source, target));
		return true;
	}

}
