package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class SGLRMutationGenerator extends AstMutationGenerator {
	
	/**
	 * @param function
	 * @return the set of labeled statements in the function
	 * @throws Exception
	 */
	private Iterable<AstLabeledStatement> labels_in(AstFunctionDefinition function) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(function.get_body());
		
		List<AstLabeledStatement> labeled_statements = 
								new ArrayList<AstLabeledStatement>();
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			if(node instanceof AstLabeledStatement) {
				labeled_statements.add((AstLabeledStatement) node);
			}
			else {
				for(int k = 0; k < node.number_of_children(); k++) {
					queue.add(node.get_child(k));
				}
			}
		}
		return labeled_statements;
	}

	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		return location instanceof AstGotoStatement;
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstGotoStatement goto_statement = (AstGotoStatement) location;
		Iterable<AstLabeledStatement> labeled_statements = this.
				labels_in(location.get_tree().function_of(location));
		String key = goto_statement.get_label().get_name();
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(AstLabeledStatement labeled_statement : labeled_statements) {
			if(!labeled_statement.get_label().get_name().equals(key)) {
				mutations.add(AstMutations.set_goto_label(goto_statement, labeled_statement));
			}
		}
		return mutations;
	}

}
