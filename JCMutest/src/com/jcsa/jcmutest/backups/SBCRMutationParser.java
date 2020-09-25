package com.jcsa.jcmutest.backups;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SBCRMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		/* find the goto-statement for break | continue statement */
		AstNode location = source.get_location();
		CirStatement src_stmt = (CirStatement) this.get_cir_nodes(
					tree, location, CirGotoStatement.class).get(0);
		
		/* find the loop-statement where the mutation is seeded */
		AstNode loop_statement = null;
		while(location != null) {
			if(location instanceof AstForStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement) {
				loop_statement = location;
				break;
			}
			else {
				location = location.get_parent();
			}
		}
		if(loop_statement == null)
			throw new IllegalArgumentException("Not in loop-statement");
		
		/* determine the target statement to which the source goes to */
		CirStatement trg_statement;
		switch(source.get_operator()) {
		case break_to_continue:
			trg_statement = (CirStatement) this.get_cir_nodes(
					tree, loop_statement, CirIfStatement.class).get(0);
			break;
		case continue_to_break:
			trg_statement = (CirStatement) this.get_cir_nodes(
					tree, loop_statement, CirIfEndStatement.class).get(0);
			break;
		default: throw new IllegalArgumentException("Unsupport: " + source);
		}
		
		/* append the mutation to the targets */
		targets.add(CirMutations.set_goto_stmt(src_stmt, trg_statement));
	}

}
