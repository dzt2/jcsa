package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

/**
 * 
 * @author yukimula
 *
 */
public class BTRPMutationParser extends CirMutationParser {

	@Override
	public void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception {
		AstExpression ast_location = (AstExpression) source.get_location();
		CirExpression location = this.get_use_point(cir_tree, ast_location);
		switch(source.get_operator()) {
		case trap_on_true:	targets.add(CirMutations.BTRP(location, true)); break;
		case trap_on_false:	targets.add(CirMutations.BTRP(location, false));break;
		default: throw new IllegalArgumentException("Invalid source: null");
		}
	}
	
}
