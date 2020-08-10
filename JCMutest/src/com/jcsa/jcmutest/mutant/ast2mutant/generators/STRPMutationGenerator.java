package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class STRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		if(location instanceof AstStatement) {
			if(location instanceof AstGotoStatement
				|| location instanceof AstBreakStatement
				|| location instanceof AstContinueStatement
				|| location instanceof AstLabeledStatement
				|| location instanceof AstCaseStatement
				|| location instanceof AstDefaultStatement
				|| location instanceof AstReturnStatement
				|| location instanceof AstIfStatement
				|| location instanceof AstSwitchStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement
				|| location instanceof AstForStatement
				|| location instanceof AstCompoundStatement) {
				return true;
			}
			else if(location instanceof AstExpressionStatement
					|| location instanceof AstDeclarationStatement) {
				AstNode parent = location.get_parent();
				if(parent instanceof AstForStatement) {
					return ((AstForStatement) parent).get_body() == location;
				}
				else {
					return true;
				}
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstStatement statement = (AstStatement) location;
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		mutations.add(AstMutations.trap_on_statement(statement));
		return mutations;
	}

}
