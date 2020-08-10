package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;

public class SBCRMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		return location instanceof AstBreakStatement
				|| location instanceof AstContinueStatement;
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		if(location instanceof AstBreakStatement) {
			mutations.add(AstMutations.break_to_continue((AstBreakStatement) location));
		}
		else {
			mutations.add(AstMutations.continue_to_break((AstContinueStatement) location));
		}
		return mutations;
	}

}
