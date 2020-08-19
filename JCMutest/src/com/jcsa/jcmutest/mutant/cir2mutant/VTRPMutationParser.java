package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class VTRPMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirExpression expression = this.get_use_point(tree, source.get_location());
		switch(source.get_operator()) {
		case trap_on_pos:
			targets.add(CirMutations.VTRP(expression, COperator.positive)); break;
		case trap_on_neg:
			targets.add(CirMutations.VTRP(expression, COperator.negative)); break;
		case trap_on_zro:
			targets.add(CirMutations.VTRP(expression, COperator.assign)); 	break;
		default: throw new IllegalArgumentException("Unsupport source: " + source);
		}
	}

}
