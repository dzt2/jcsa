package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SGLRSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location().get_parent();
		AstNode parameter = ((AstNode) mutation.get_parameter()).get_parent();
		CirStatement source = this.find_beg_stmt(tree, location);
		CirStatement target = this.find_end_statement(tree, parameter);
		SadAssertion state_error = SadFactory.set_statement(source, target);
		this.connect(reach_node, state_error);
	}

}
