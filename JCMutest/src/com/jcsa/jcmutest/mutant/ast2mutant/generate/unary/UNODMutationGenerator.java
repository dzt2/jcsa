package com.jcsa.jcmutest.mutant.ast2mutant.generate.unary;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;

public class UNODMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		if(location instanceof AstUnaryExpression) {
			switch(((AstUnaryExpression) location).get_operator().get_operator()) {
			case negative:
			case bit_not:
			case logic_not:
					return true;
			default: return false;
			}
		}
		else {
			return false;
		}
	}

	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstUnaryExpression expression = (AstUnaryExpression) location;
		mutations.add(AstMutations.UNOD(expression));
	}

}
