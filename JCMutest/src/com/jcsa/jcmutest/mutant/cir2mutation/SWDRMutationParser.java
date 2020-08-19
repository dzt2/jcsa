package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SWDRMutationParser extends CirMutationParser {

	@Override
	public void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception {
		AstStatement loop_statement = (AstStatement) source.get_location();
		CirStatement condition_stmt = (CirStatement) this.get_cir_node(
						cir_tree, loop_statement, CirIfStatement.class, 0);
		targets.add(CirMutations.SWDR((CirIfStatement) condition_stmt));
	}

}
