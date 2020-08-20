package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class VTRPMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirExpression expression = this.get_use_point(tree, source.get_location());
		switch(source.get_operator()) {
		case trap_on_pos:
			targets.add(CirMutations.trap_on_great(expression, 0L)); break;
		case trap_on_neg:
			targets.add(CirMutations.trap_on_small(expression, 0L)); break;
		case trap_on_zro:
			targets.add(CirMutations.trap_on_equal(expression, 0L)); break;
		default: throw new IllegalArgumentException("Unsupport: " + source.toString());
		}
	}

}
