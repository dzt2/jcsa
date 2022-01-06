package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class SWDRMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception { }

	@Override
	protected boolean available(AstNode location) throws Exception {
		return location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement;
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		if(location instanceof AstWhileStatement) {
			mutations.add(AstMutations.while_to_do_while((AstWhileStatement) location));
		}
		else {
			mutations.add(AstMutations.do_while_to_while((AstDoWhileStatement) location));
		}
	}

}
