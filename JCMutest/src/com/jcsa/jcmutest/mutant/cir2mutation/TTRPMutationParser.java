package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;

public class TTRPMutationParser extends CirMutationParser {

	@Override
	public void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception {
		AstNode ast_location = source.get_location();
		int loop_time = ((Integer) source.get_parameter()).intValue();
		CirIfStatement if_statement = (CirIfStatement) this.
				get_cir_node(cir_tree, ast_location, CirIfStatement.class, 0);
		if(ast_location instanceof AstForStatement
			|| ast_location instanceof AstWhileStatement)
			targets.add(CirMutations.TTRP(if_statement, loop_time + 1));
		else
			targets.add(CirMutations.TTRP(if_statement, loop_time));
	}

}
