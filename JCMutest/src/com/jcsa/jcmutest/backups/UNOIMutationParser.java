package com.jcsa.jcmutest.backups;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UNOIMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirExpression expression = this.get_use_point(tree, source.get_location());
		switch(source.get_operator()) {
		case insert_arith_neg:	
			targets.add(CirMutations.ins_operator(expression, COperator.negative));
			break;
		case insert_bitws_rsv:
			targets.add(CirMutations.ins_operator(expression, COperator.bit_not));
			break;
		case insert_logic_not:
			targets.add(CirMutations.ins_operator(expression, COperator.logic_not));
			break;
		case insert_abs_value:
			targets.add(CirMutations.ins_operator(expression, COperator.positive));
			break;
		case insert_nabs_value:
			targets.add(CirMutations.ins_operator(expression, COperator.assign));
			break;
		default: throw new IllegalArgumentException("Invalid source: " + source);
		}
	}

}
