package com.jcsa.jcmutest.mutant.ast2mutant.generate.stmt;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class SWDRMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement;
	}

	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		if(location instanceof AstWhileStatement) {
			mutations.add(AstMutations.while_to_do_while((AstWhileStatement) location));
		}
		else {
			mutations.add(AstMutations.do_while_to_while((AstDoWhileStatement) location));
		}
	}

}
