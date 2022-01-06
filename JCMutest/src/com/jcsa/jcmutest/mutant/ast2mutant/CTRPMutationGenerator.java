package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class CTRPMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {}

	@Override
	protected boolean available(AstNode location) throws Exception {
		return location instanceof AstSwitchStatement;
	}

	/**
	 * @param location
	 * @return the case statements defined in the switch-statement body
	 * @throws Exception
	 */
	private Iterable<AstCaseStatement> get_cases(AstSwitchStatement location) throws Exception {
		Queue<AstNode> queue = new LinkedList<>();
		List<AstCaseStatement> cases = new ArrayList<>();
		queue.add(location.get_body());
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			if(node instanceof AstCaseStatement) {
				cases.add((AstCaseStatement) node);
			}
			else if(node instanceof AstSwitchStatement) {
				continue;
			}
			else {
				for(int k = 0; k < node.number_of_children(); k++) {
					queue.add(node.get_child(k));
				}
			}
		}
		return cases;
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstSwitchStatement switch_statement = (AstSwitchStatement) location;
		Iterable<AstCaseStatement> case_statements = this.get_cases(switch_statement);
		for(AstCaseStatement case_statement : case_statements) {
			mutations.add(AstMutations.trap_on_case(switch_statement, case_statement));
		}
	}

}
