package com.jcsa.jcmutest.mutant.ast2mutant.generate.trap;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class TTRPMutationGenerator extends AstMutationGenerator {
	
	private final int[] loop_times = new int[] {
		2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16,
		20, 30, 40, 50, 60, 70, 80, 90, 100
	};

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return location instanceof AstForStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement;
	}

	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstStatement statement = (AstStatement) location;
		for(int loop_time : loop_times) {
			mutations.add(AstMutations.trap_for_time(statement, loop_time));
		}
	}

}
