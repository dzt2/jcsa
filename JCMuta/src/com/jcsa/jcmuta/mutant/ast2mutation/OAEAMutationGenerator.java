package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;

public class OAEAMutationGenerator extends AstMutationGenerator {
	
	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstArithAssignExpression)
			locations.add(location);
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstArithAssignExpression expression = (AstArithAssignExpression) location;
		mutations.add(AstMutation.OAEA(expression));
	}

}
