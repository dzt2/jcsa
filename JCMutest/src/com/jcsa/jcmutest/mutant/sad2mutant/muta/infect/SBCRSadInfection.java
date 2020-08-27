package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SBCRSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		/* find the loop-statement where mutation is seeded */
		AstNode loop_statement = mutation.get_location();
		while(loop_statement != null) {
			if(loop_statement instanceof AstDoWhileStatement
				|| loop_statement instanceof AstWhileStatement
				|| loop_statement instanceof AstForStatement) {
				break;
			}
			else {
				loop_statement = loop_statement.get_parent();
			}
		}
		if(loop_statement == null)
			throw new RuntimeException("Not in a loop-structure");
		
		/* determine the source and target statement */
		CirStatement source = this.find_beg_stmt(tree, mutation.get_location());
		CirStatement target;
		switch(mutation.get_operator()) {
		case break_to_continue:
		{
			target = (CirStatement) this.get_cir_nodes(tree, 
					loop_statement, CirIfStatement.class).get(0);
			break;
		}
		case continue_to_break:
		{
			target = (CirStatement) this.get_cir_nodes(tree, 
					loop_statement, CirIfEndStatement.class).get(0);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator");
		}
		
		SadAssertion state_error = SadFactory.set_statement(source, target);
		this.connect(reach_node, state_error);
	}

}
