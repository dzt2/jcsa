package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.trap;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class ETRPSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		CirStatement statement = this.find_beg_stmt(tree, mutation.get_location());
		this.connect(reach_node, SadFactory.trap_statement(statement));
	}

}
