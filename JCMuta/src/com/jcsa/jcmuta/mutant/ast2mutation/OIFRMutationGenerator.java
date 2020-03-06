package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class OIFRMutationGenerator extends AstMutationGenerator{

	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstIfStatement) {
			AstIfStatement statement = (AstIfStatement) location;
			if(statement.has_else()) {
				AstStatement false_branch = statement.get_false_branch();
				if(false_branch instanceof AstIfStatement) {
					locations.add(statement);
				}
			}
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstIfStatement statement = (AstIfStatement) location;
		mutations.add(AstMutation.OIFR(statement));
	}
	
}
