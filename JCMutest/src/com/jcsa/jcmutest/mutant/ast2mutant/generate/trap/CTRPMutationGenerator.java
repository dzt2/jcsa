package com.jcsa.jcmutest.mutant.ast2mutant.generate.trap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;

public class CTRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return location instanceof AstSwitchStatement;
	}
	
	/**
	 * @param switch_statement
	 * @return the case statements in the switch body
	 * @throws Exception
	 */
	private Iterable<AstCaseStatement> cases_in(AstSwitchStatement switch_statement) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		List<AstCaseStatement> cases = new ArrayList<AstCaseStatement>();
		queue.add(switch_statement.get_body());
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			if(node instanceof AstCaseStatement) {
				cases.add((AstCaseStatement) node);
			}
			else if(!(node instanceof AstSwitchStatement)) {
				for(int k = 0; k < node.number_of_children(); k++) {
					queue.add(node.get_child(k));
				}
			}
		}
		return cases;
	}

	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstSwitchStatement switch_statement = (AstSwitchStatement) location;
		Iterable<AstCaseStatement> case_statements = this.cases_in(switch_statement);
		for(AstCaseStatement case_statement : case_statements) {
			mutations.add(AstMutations.trap_on_case(switch_statement, case_statement));
		}
	}

}
