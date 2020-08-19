package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
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
	public void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception {
		/* 1. get the goto-statement where the loop occurs */
		AstNode ast_location = source.get_location();
		CirStatement source_stmt = (CirStatement) get_cir_node(
				cir_tree, ast_location, CirGotoStatement.class, 0);
		
		/* 2. find the loop-statement in AST location block */
		AstNode parent = ast_location.get_parent(), loop_statement = null;
		while(parent != null) {
			if(parent instanceof AstForStatement
				|| parent instanceof AstWhileStatement
				|| parent instanceof AstDoWhileStatement) {
				loop_statement = parent; break;
			}
			else {
				parent = parent.get_parent();
			}
		}
		if(loop_statement == null)
			throw new IllegalArgumentException("Not in loop-structure");
		
		/* 3. determine the next statement where the transition occurs */
		CirStatement target_stmt;
		if(ast_location instanceof AstBreakStatement) {
			target_stmt = (CirStatement) this.get_cir_node(
					cir_tree, loop_statement, CirIfStatement.class, 0);
		}
		else {
			target_stmt = (CirStatement) this.get_cir_node(cir_tree, 
					loop_statement, CirIfEndStatement.class, 0);
		}
		
		/* 4. set_goto_label(source_stmt, target_stmt) */
		targets.add(CirMutations.SGLR(source_stmt, target_stmt));
	}

}
