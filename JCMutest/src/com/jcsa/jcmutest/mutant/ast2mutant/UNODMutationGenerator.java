package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class UNODMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {}

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(location instanceof AstUnaryExpression) {
			switch(((AstUnaryExpression) location).get_operator().get_operator()) {
			case negative:
			case bit_not:
			case logic_not:	return true;
			default: 		return false;
			}
		}
		else {
			return false;
		}
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		mutations.add(AstMutations.UNOD((AstUnaryExpression) location));
	}

}
