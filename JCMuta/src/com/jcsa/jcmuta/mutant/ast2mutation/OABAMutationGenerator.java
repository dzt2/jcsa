package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OABAMutationGenerator extends AstMutationGenerator {
	
	private static final List<COperator> replaces = new LinkedList<COperator>();
	static {
		replaces.add(COperator.bit_and_assign);
		replaces.add(COperator.bit_or_assign);
		replaces.add(COperator.bit_xor_assign);
		replaces.add(COperator.left_shift_assign);
		replaces.add(COperator.righ_shift_assign);
	}
	
	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstArithAssignExpression)
			locations.add(location);
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstArithAssignExpression expression = (AstArithAssignExpression) location;
		for(COperator replace : replaces) {
			if(expression.get_operator().get_operator() != replace) {
				mutations.add(AstMutation.OABA(expression, replace));
			}
		}
	}

}
