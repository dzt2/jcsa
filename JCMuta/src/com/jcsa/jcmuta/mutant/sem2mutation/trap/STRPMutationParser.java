package com.jcsa.jcmuta.mutant.sem2mutation.trap;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class STRPMutationParser extends SemanticMutationParser {
	
	private AstStatement get_location(AstMutation ast_mutation) throws Exception {
		AstNode location = ast_mutation.get_location();
		while(location != null) {
			if(location instanceof AstStatement)
				return (AstStatement) location;
			else location = location.get_parent();
		}
		throw new IllegalArgumentException("Unable to locate");
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		return this.get_prev_statement(this.get_location(ast_mutation));
	}

	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		this.infect(sem_mutation.get_assertions().trapping());
	}

}
