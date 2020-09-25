package com.jcsa.jcmutest.backups;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UIOIMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirExpression expression = this.get_use_point(tree, source.get_location());
		switch(source.get_operator()) {
		case insert_prev_inc:	targets.add(CirMutations.ins_operator(expression, COperator.increment)); break;
		case insert_prev_dec:	targets.add(CirMutations.ins_operator(expression, COperator.decrement)); break;
		case insert_post_inc:	targets.add(CirMutations.ins_operator(expression, COperator.arith_add)); break;
		case insert_post_dec:	targets.add(CirMutations.ins_operator(expression, COperator.arith_sub)); break;
		default: throw new IllegalArgumentException("Unsupport source: " + source);
		}
	}

}
