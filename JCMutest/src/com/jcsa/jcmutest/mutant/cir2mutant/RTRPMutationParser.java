package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class RTRPMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirExpression src = this.get_use_point(tree, source.get_location());
		CirExpression trg = this.get_use_point(tree, (AstNode) source.get_parameter());
		if(src != trg) targets.add(CirMutations.set_expression(src, trg));
	}

}
