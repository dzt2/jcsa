package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UNODMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirExpression expression = this.get_use_point(tree, source.get_location());
		switch(source.get_operator()) {
		case delete_arith_neg: targets.add(CirMutations.ins_operator(expression, COperator.negative)); break;
		case delete_bitws_rsv: targets.add(CirMutations.ins_operator(expression, COperator.bit_not)); break;
		case delete_logic_not: targets.add(CirMutations.ins_operator(expression, COperator.logic_not)); break;
		default: throw new IllegalArgumentException("Unsupport source: " + source);
		}
	}

}
