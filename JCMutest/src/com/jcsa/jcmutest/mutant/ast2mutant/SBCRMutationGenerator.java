package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class SBCRMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception { }

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(location instanceof AstBreakStatement
			|| location instanceof AstContinueStatement) {
			AstNode statement = location;
			while(statement != null) {
				if(statement instanceof AstWhileStatement
					|| statement instanceof AstDoWhileStatement
					|| statement instanceof AstForStatement) {
					return true;
				}
				else {
					statement = statement.get_parent();
				}
			}
			return false;
		}
		else {
			return false;
		}
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstStatement statement = (AstStatement) location;
		if(statement instanceof AstBreakStatement) {
			mutations.add(AstMutations.break_to_continue((AstBreakStatement) statement));
		}
		else {
			mutations.add(AstMutations.continue_to_break((AstContinueStatement) statement));
		}
	}

}
