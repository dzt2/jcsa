package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;

public class UNOIMutationGenerator extends AstMutationGenerator {
	
	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
