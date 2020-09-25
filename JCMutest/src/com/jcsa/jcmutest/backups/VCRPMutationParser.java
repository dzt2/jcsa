package com.jcsa.jcmutest.backups;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class VCRPMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirExpression expression = this.get_use_point(tree, source.get_location());
		switch(source.get_operator()) {
		case set_integer:	
			targets.add(CirMutations.set_expression(expression, 
					((Long) source.get_parameter()).longValue()));
			break;
		case set_double:
			targets.add(CirMutations.set_expression(expression, 
					((Double) source.get_parameter()).doubleValue()));
			break;
		default: throw new IllegalArgumentException("Unsupport source: " + source);
		}
	}

}
