package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class TTRPMutationGenerator extends AstMutationGenerator {
	
	private final int[] loop_times = {
			2, 3, 4, 5, 6, 7, 8,
			10, 12, 14, 16, 18, 20,
			30, 40, 60, 80, 100, 120
	};

	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		return location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement
				|| location instanceof AstForStatement;
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstStatement loop_statement = (AstStatement) location;
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(int loop_time : loop_times) {
			mutations.add(AstMutations.trap_for_time(loop_statement, loop_time));
		}
		return mutations;
	}

}
