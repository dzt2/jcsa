package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class SGLRMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {
		this.labels.clear();
		for(AstNode location : locations) {
			if(location instanceof AstLabeledStatement) {
				this.labels.add((AstLabeledStatement) location);
			}
		}
	}
	
	private List<AstLabeledStatement> labels = new ArrayList<AstLabeledStatement>();
	
	@Override
	protected boolean available(AstNode location) throws Exception {
		return location instanceof AstGotoStatement;
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstGotoStatement goto_statement = (AstGotoStatement) location;
		for(AstLabeledStatement labeled_statement : this.labels) {
			mutations.add(AstMutations.
					set_goto_label(goto_statement, labeled_statement));
		}
	}

}
