package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class TTRPMutationGenerator extends MutationGenerator {
	
	private final int[] loop_times = new int[] {
		2, 3, 4, 5, 6, 7, 8, 10,
		20, 30, 40, 50, 60, 80, 100
	};

	@Override
	protected void initialize(AstFunctionDefinition function) throws Exception { }

	@Override
	protected boolean available(AstNode location) throws Exception {
		return location instanceof AstForStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement;
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstStatement loop_statement = (AstStatement) location;
		for(int loop_time : this.loop_times) {
			mutations.add(AstMutations.trap_for_time(loop_statement, loop_time));
		}
	}

}
