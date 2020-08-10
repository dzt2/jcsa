package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class STDLMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		if(location instanceof AstGotoStatement
			|| location instanceof AstBreakStatement
			|| location instanceof AstContinueStatement
			|| location instanceof AstForStatement
			|| location instanceof AstIfStatement
			|| location instanceof AstSwitchStatement
			|| location instanceof AstWhileStatement
			|| location instanceof AstDoWhileStatement) {
			return true;
		}
		else if(location instanceof AstExpressionStatement) {
			return ((AstExpressionStatement) location).has_expression();
		}
		else if(location instanceof AstCompoundStatement) {
			return ((AstCompoundStatement) location).has_statement_list();
		}
		else {
			return false;
		}
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstStatement statement = (AstStatement) location;
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		mutations.add(AstMutations.delete_statement(statement));
		return mutations;
	}

}
