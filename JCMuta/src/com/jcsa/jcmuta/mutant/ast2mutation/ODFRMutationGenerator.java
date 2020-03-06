package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class ODFRMutationGenerator extends AstMutationGenerator {

	private Queue<AstStatement> queue = new LinkedList<AstStatement>();
	private List<AstCaseStatement> case_statements = new ArrayList<AstCaseStatement>();

	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstSwitchStatement) {
			AstSwitchStatement switch_statement = (AstSwitchStatement) location;
			queue.clear(); this.case_statements.clear();
			queue.add(switch_statement.get_body()); boolean has_default = false;
			
			/* collect all the case statements belonging to the switch statement */
			while(!queue.isEmpty()) {
				AstStatement stmt = queue.poll();
				
				if(stmt instanceof AstCompoundStatement) {
					AstStatementList list = ((AstCompoundStatement) stmt).get_statement_list();
					for(int k = 0; k < list.number_of_statements(); k++) {
						queue.add(list.get_statement(k));
					}
				}
				else if(stmt instanceof AstIfStatement) {
					queue.add(((AstIfStatement) stmt).get_true_branch());
					if(((AstIfStatement) stmt).has_else()) {
						queue.add(((AstIfStatement) stmt).get_false_branch());
					}
				}
				else if(stmt instanceof AstWhileStatement) {
					queue.add(((AstWhileStatement) stmt).get_body());
				}
				else if(stmt instanceof AstDoWhileStatement) {
					queue.add(((AstDoWhileStatement) stmt).get_body());
				}
				else if(stmt instanceof AstForStatement) {
					queue.add(((AstForStatement) stmt).get_body());
				}
				else if(stmt instanceof AstCaseStatement) {
					case_statements.add((AstCaseStatement) stmt);
				}
				else if(stmt instanceof AstDefaultStatement) {
					has_default = true; 
				}
			}
			
			if(!has_default) locations.add(location);
			else this.case_statements.clear();
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		for(AstCaseStatement case_statement : this.case_statements) {
			mutations.add(AstMutation.ODFR(case_statement));
		}
	}

}
