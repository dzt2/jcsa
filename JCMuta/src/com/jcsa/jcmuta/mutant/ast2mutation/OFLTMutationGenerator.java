package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OFLTMutationGenerator extends AstMutationGenerator {

	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstRelationExpression) {
			COperator operator = ((AstRelationExpression) location).get_operator().get_operator();
			if(operator == COperator.equal_with || operator == COperator.not_equals)
				locations.add(location);
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		mutations.add(AstMutation.OFLT((AstRelationExpression) location));
	}

}
