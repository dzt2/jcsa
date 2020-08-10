package com.jcsa.jcmutest.mutant.ast2mutant.generate.stmt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class SGLRMutationGenerator extends AstMutationGenerator {
	
	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return location instanceof AstGotoStatement;
	}
	
	private Iterable<AstLabeledStatement> labels_in(AstFunctionDefinition function) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(function.get_body());
		List<AstLabeledStatement> labels = new ArrayList<AstLabeledStatement>();
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			if(node instanceof AstLabeledStatement) {
				labels.add((AstLabeledStatement) node);
			}
			else {
				for(int k = 0; k < node.number_of_children(); k++) {
					queue.add(node.get_child(k));
				}
			}
		}
		return labels;
	}
	
	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstGotoStatement goto_statement = (AstGotoStatement) location;
		Iterable<AstLabeledStatement> labeled_statements = this.
				labels_in(location.get_tree().function_of(location));
		for(AstLabeledStatement labeled_statement : labeled_statements) {
			if(goto_statement.get_label().get_name().equals(
					labeled_statement.get_label().get_name())) {
				mutations.add(AstMutations.set_goto_label(goto_statement, labeled_statement));
			}
		}
	}
	
}
