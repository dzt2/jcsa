package com.jcsa.jcmutest.mutant.cir2mutant;

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
		/* 1. get the goto-statement as the source */
		AstNode location = source.get_location();
		CirStatement src = (CirStatement) this.get_cir_nodes(
				tree, location, CirGotoStatement.class).get(0);
		
		/* 2. find the loop-structure in which mutation is seeded */
		AstNode loop_statement = null;
		while(location != null) {
			if(location instanceof AstDoWhileStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstForStatement) {
				loop_statement = location; 
				break;
			}
			else {
				location = location.get_parent();
			}
		}
		if(loop_statement == null) {
			throw new IllegalArgumentException("Not in loop");
		}
		
		/* 3. determine the next statement being reached */
		CirStatement trg;
		switch (source.get_operator()) {
		case break_to_continue:
		{
			trg = (CirStatement) this.get_cir_nodes(tree, 
					loop_statement, CirIfStatement.class).get(0);
			break;
		}
		case continue_to_break:
		{
			trg = (CirStatement) this.get_cir_nodes(tree, 
					loop_statement, CirIfEndStatement.class).get(0);
			break;
		}
		default: throw new IllegalArgumentException("Invalid: " + source);
		}
		
		/* 4. generate the cir-mutation as set_goto_label */
		targets.add(CirMutations.SGLR(src, trg));
	}

}
