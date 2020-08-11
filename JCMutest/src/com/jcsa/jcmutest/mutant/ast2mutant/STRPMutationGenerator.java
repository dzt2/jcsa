package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class STRPMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function) throws Exception {}

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(location instanceof AstStatement) {
			return !(location.get_parent() instanceof AstFunctionDefinition);
		}
		else {
			return false;
		}
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstStatement statement = (AstStatement) location;
		mutations.add(AstMutations.trap_on_statement(statement));
	}

}
