package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;

public class SGLRMutationExtension extends MutationExtension {

	@Override
	protected AstMutation cover(AstMutation source) throws Exception {
		AstGotoStatement statement = 
				(AstGotoStatement) source.get_location().get_parent();
		return this.coverage_mutation(statement);
	}

	@Override
	protected AstMutation weak(AstMutation source) throws Exception {
		return this.cover(source);
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		AstGotoStatement source_statement = 
				(AstGotoStatement) source.get_location().get_parent();
		AstLabeledStatement target_statement = (AstLabeledStatement) 
						((AstNode) source.get_location()).get_parent();
		return AstMutations.set_goto_label(source_statement, target_statement);
	}

}
