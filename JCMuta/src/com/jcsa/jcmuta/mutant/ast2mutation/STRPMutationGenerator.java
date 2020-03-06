package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * trap_on_statement(statement) where statement can be any statement such that:
 * (1) expression_statement is inserted with trap() within the expression it contains;
 * (2) labeled statement is inserted with trap() after the labeled location is defined;
 * (3) the others are translated as trap(); statement; such that trapping occurs before
 * 	   the statement is executed.
 * 
 * @author yukimula
 *
 */
public class STRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstStatement) {
			AstStatement statement = (AstStatement) location;
			locations.add(statement);
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstStatement statement = (AstStatement) location;
		mutations.add(AstMutation.STRP(statement));
	}

}
