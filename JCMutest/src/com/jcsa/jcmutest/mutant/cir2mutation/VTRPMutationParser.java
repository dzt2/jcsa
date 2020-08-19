package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class VTRPMutationParser extends CirMutationParser {
	
	@Override
	public void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception {
		CirExpression expression = this.get_use_point(cir_tree, (AstExpression) source.get_location());
		switch(source.get_operator()) {
		case trap_on_pos:
		{
			targets.add(CirMutations.VTRP(expression, Boolean.TRUE)); break;
		}
		case trap_on_neg:
		{
			targets.add(CirMutations.VTRP(expression, Boolean.FALSE)); break;
		}
		case trap_on_zro:
		{
			targets.add(CirMutations.VTRP(expression, null)); break;
		}
		case trap_on_dif:
		{
			targets.add(CirMutations.trap_on_diff(expression, source.get_parameter()));
			break;
		}
		default: throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	
}
