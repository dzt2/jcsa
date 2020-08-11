package com.jcsa.jcmutest.mutant.ast2mutant.generate.stmt;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class SBCRMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		if(location instanceof AstBreakStatement
			|| location instanceof AstContinueStatement) {
			AstNode node = location.get_parent();
			while(node != null) {
				if(node instanceof AstForStatement
					|| node instanceof AstWhileStatement
					|| node instanceof AstDoWhileStatement) {
					return true;
				}
				else {
					node = node.get_parent();
				}
			}
			return false;
		}
		else {
			return false;
		}
	}

	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		if(location instanceof AstBreakStatement) {
			mutations.add(AstMutations.break_to_continue((AstBreakStatement) location));
		}
		else {
			mutations.add(AstMutations.continue_to_break((AstContinueStatement) location));
		}
	}

}
