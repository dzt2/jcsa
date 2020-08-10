package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class RTRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		if(location instanceof AstReturnStatement) {
			return ((AstReturnStatement) location).has_expression();
		}
		else {
			return false;
		}
	}
	
	private Iterable<AstReturnStatement> returns_of(AstFunctionDefinition function) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(function.get_body());
		List<AstReturnStatement> statements = new ArrayList<AstReturnStatement>();
		
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			if(node instanceof AstReturnStatement) {
				if(((AstReturnStatement) node).has_expression()) {
					statements.add((AstReturnStatement) node);
				}
			}
			else {
				for(int k = 0; k < node.number_of_children(); k++) {
					queue.add(node.get_child(k));
				}
			}
		}
		
		return statements;
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstReturnStatement source = (AstReturnStatement) location;
		Iterable<AstReturnStatement> targets = this.
				returns_of(location.get_tree().function_of(location));
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(AstReturnStatement target : targets) {
			if(!source.generate_code().equals(target.generate_code())) {
				mutations.add(AstMutations.RTRP(source, target));
			}
		}
		return mutations;
	}

}
