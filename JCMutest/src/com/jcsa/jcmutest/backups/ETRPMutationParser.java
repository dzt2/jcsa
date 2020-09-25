package com.jcsa.jcmutest.backups;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class ETRPMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirStatement statement = beg_statement(tree, source.get_location());
		targets.add(CirMutations.trap_on_stmt(statement, 1));
	}

}
