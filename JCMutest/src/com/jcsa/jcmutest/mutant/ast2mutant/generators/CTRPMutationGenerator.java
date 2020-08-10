package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;

public class CTRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		return location instanceof AstSwitchStatement;
	}
	
	/**
	 * @param location
	 * @return the case statement within the switch body
	 * @throws Exception
	 */
	private Iterable<AstCaseStatement> cases_in(AstSwitchStatement location) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(location.get_body());
		List<AstCaseStatement> case_statements = new ArrayList<AstCaseStatement>();
		
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			if(node instanceof AstCaseStatement) {
				case_statements.add((AstCaseStatement) node);
			}
			else if(!(node instanceof AstSwitchStatement)) {
				for(int k = 0; k < node.number_of_children(); k++) {
					queue.add(node.get_child(k));
				}
			}
		}
		
		return case_statements;
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstSwitchStatement statement = (AstSwitchStatement) location;
		Iterable<AstCaseStatement> case_statements = this.cases_in(statement);
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(AstCaseStatement case_statement : case_statements) {
			mutations.add(AstMutations.trap_on_case(statement, case_statement));
		}
		return mutations;
	}

}
