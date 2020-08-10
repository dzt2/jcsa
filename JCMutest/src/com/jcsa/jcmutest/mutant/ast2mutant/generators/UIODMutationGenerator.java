package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;

public class UIODMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		return location instanceof AstIncreUnaryExpression
				|| location instanceof AstIncrePostfixExpression;
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		mutations.add(AstMutations.UIOD((AstExpression) location));
		return mutations;
	}

}
